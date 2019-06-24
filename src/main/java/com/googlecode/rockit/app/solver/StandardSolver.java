package com.googlecode.rockit.app.solver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.grounder.StandardGrounder;
import com.googlecode.rockit.app.learner.FormulaForLearning;
import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.app.solver.thread.CardinalityFormulaRestrictionBuilder;
import com.googlecode.rockit.app.solver.thread.FormulaRestrictionBuilder;
import com.googlecode.rockit.app.solver.thread.RestrictionBuilder;
import com.googlecode.rockit.conn.ilp.GurobiConnector;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.conn.ilp.ILPVariable.ILPVarCategory;
import com.googlecode.rockit.conn.sql.MySQLConnector;
import com.googlecode.rockit.conn.sql.SQLQueryGenerator;
import com.googlecode.rockit.exception.DatabaseException;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.formulas.FormulaAbstract;
import com.googlecode.rockit.javaAPI.formulas.FormulaCardinality;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaObjective;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;
import com.mlnengine.xeggora.app.solver.aggregate.AggregationManagerDoubleSimpleImpl;

/**
 * Solves the MAP Query. 
 *
 * After initializing the sql and ilp connectors, we create all necessary SQL statements with the method 'determineFormulasToCheck'.
 * 
 * Then, the method solve() starts the process. 
 * After calling StandardGrounder.ground(), it basically calls runCuttingPlaneInference, which may also only perform one CPI iteration (adding all constraints).
 * 
 * 
 * @author Jan
 *
 */
public class StandardSolver extends AbstractSolver
{
	public static long ilpTotalTime = 0;
    // Model
    private Model                     model;

    // connectors
    private MySQLConnector            sql;
    private ILPConnector           ilpCon;

    // ready baked sql statements for checks
    private HashSet<FormulaHard>      formulasDuringCPI;
    private HashSet<FormulaHard>      formulasBeforeCPI;
    private HashSet<FormulaHard>      formulasEvidence;
    // private HashSet<FormulaCardinality> cardinalitiesToCheck;

    // count restrictions for log
    private HashMap<Literal, Literal> evidenceAxioms = new HashMap<Literal, Literal>();

    private HerbrandUniverse          u              = HerbrandUniverse.getInstance();

    private ArrayList<String>         results        = new ArrayList<String>();
    
    public StandardSolver() throws ParseException, SolveException
    {
        // PREPARATION:
        // initialize SQLConnector without deleting the database
        this.sql = new MySQLConnector();
        sql.deleteAll();
        // Initializing the ILPConnector
        initILPConnector();

    }


    public StandardSolver(Model model, MySQLConnector sql) throws ParseException, SolveException
    {
        this.sql = sql;
        this.determineFormulasToCheck(model);
        initILPConnector();
    }


    public StandardSolver(Model model) throws ParseException, SolveException
    {
        this();
        this.determineFormulasToCheck(model);
    }


    public void determineFormulasToCheck(Model model) throws ParseException, SolveException
    {
        this.model = model;

        // get all the SQL Statements from the hard formulas
        // which need to be checked.
        HashSet<FormulaHard> formulasToCheck = SQLQueryGenerator.getSQLQueriesForHardFormulas(model);

        // add all the SQL Statements from the soft formulas
        // which need to be checked.
        formulasToCheck.addAll(SQLQueryGenerator.getSQLQueriesForSoftFormulas(model));

        // get all the SQL Statements from the cardinality formulas
        // which need to be checked.
        formulasToCheck.addAll(SQLQueryGenerator.getSQLQueriesForCardinalityFormula(model));

        // initialize RestrictionBuilder
        for(FormulaHard formula : formulasToCheck) {
            if(formula instanceof FormulaCardinality) {
                formula.setRestrictionBuilder(new CardinalityFormulaRestrictionBuilder((FormulaCardinality) formula));
            } else {
                formula.setRestrictionBuilder(new FormulaRestrictionBuilder(formula));
            }
        }

        // get all the SQL Statements for evidence formulas (hard ones with only
        // one predicate)
        // which need to be checked.
        // put evidence axioms in ilp. Formulas with just 1 predicate will use
        // no joins with results (like without CPI).
        // they do not have to be called afterwards (deleted from
        // hardformulastocheck).
        this.formulasEvidence = new HashSet<FormulaHard>();
        this.formulasDuringCPI = new HashSet<FormulaHard>();
        this.formulasBeforeCPI = new HashSet<FormulaHard>();
        for(FormulaHard formula : formulasToCheck) {
            // if weight = 0.0 we do not have to check formula
            boolean weightZero = false;
            if(formula instanceof FormulaSoft) {
                if(((FormulaSoft) formula).getDoubleVariable() == null && ((FormulaSoft) formula).getWeight() == 0) {
                    weightZero = true;
                }
//Me BUG
                if(((FormulaSoft) formula).getDoubleVariable() != null && ((FormulaSoft) formula).getWeight() == null) 
                    weightZero = true;
            }

            if(!weightZero) {
                if(formula.getRestrictions().size() == 1) {
                    // soft formulas with only one constraint are not hard! but
                    // they can be materialized once (without CPI).
                    if(formula instanceof FormulaCardinality) {
                        // normal assignment
                        if(formula.isCuttingPlaneInferenceUsed()) {
                            this.generateTablesPerFormulaForDublicateDetection(formula);
                            this.formulasDuringCPI.add(formula);
                            this.formulasBeforeCPI.add(formula);
                        } else {
                            this.formulasBeforeCPI.add(formula);
                        }
                    } else if(formula instanceof FormulaSoft) {
                        // add everything if formula has positive weight
                        if(((FormulaSoft) formula).getWeight() > 0) {
                            formula.useCuttingPlaneInference(false);
                            this.formulasBeforeCPI.add(formula);
                        } else {
                            // normal assignment
                            if(formula.isCuttingPlaneInferenceUsed()) {
                                this.generateTablesPerFormulaForDublicateDetection(formula);
                                this.formulasDuringCPI.add(formula);
                                this.formulasBeforeCPI.add(formula);
                            } else {
                                this.formulasBeforeCPI.add(formula);
                            }
                        }

                        // hard formulas with only one predicate always cause
                        // evidence.
                    } else {
                        formula.useCuttingPlaneInference(false);
                        this.formulasEvidence.add(formula);
                    }
                } else {
                    // normal assignment
                    if(formula.isCuttingPlaneInferenceUsed()) {
                        this.generateTablesPerFormulaForDublicateDetection(formula);
                        this.formulasDuringCPI.add(formula);
                        this.formulasBeforeCPI.add(formula);
                    } else {
                        this.formulasBeforeCPI.add(formula);
                    }
                }
            }
        }

    }


    public ILPConnector getILPConnector()
    {
        return ilpCon;
    }


    public MySQLConnector getMySQLConnector()
    {
        return sql;
    }


    /**
     * @throws ReadOrWriteToFileException
     * 
     *             This method solves the markov logic network using Integer
     *             Linear Programming.
     * 
     *             As input it needs a grounding of every restriction and
     *             objective formula like it is done in the standard evaluator.
     * 
     *             Its output is a list of variables which are true in the
     *             solution.
     * 
     * @param model
     * @return A list of variables which are true in the current setting.
     * @throws ParseException
     * @throws
     */
    public ArrayList<String> solve() throws SolveException, ParseException
    {
        StandardGrounder grounder = new StandardGrounder(model, this.sql);
        grounder.ground();
//Me{
		System.out.println("Aggregation order " + Parameters.AGGREGATION_ORDER);
//}Me
        System.out.print("===== Start Standard Solver =====");
        System.out.println(new Date());

        ArrayList<String> results = new ArrayList<String>();

        // start of cutting plane inferences
        results = runCuttingPlaneInference();

        return results;
    }


    public void close() throws ILPException, DatabaseException
    {
        ilpCon.close();
        sql.dropDatabase();
        sql.close();
    }


    public void closeILPConnector() throws ILPException
    {
        ilpCon.close();
    }


    public void initILPConnector() throws ILPException
    {
    	ilpCon = new GurobiConnector();
    }


    /**
     * Prepares the ILP for the MCSAT sampler. It adds everything to the ILP.
     * 
     * @throws SolveException
     * @throws ParseException
     * 
     */
    public void prepareForMCSATSampler() throws SolveException, ParseException
    {
        StandardGrounder grounder = new StandardGrounder(model, this.sql);
        grounder.ground();

        // put Objective Variables into ILP
        this.putObjectiveVariableIntoILP(model);

        // Evidence formulas only need to be added once.
        this.computeGroundedClauses(formulasEvidence, evidenceAxioms, null, sql);
        this.addConstraintsFromSQLResults(true/*false*/, formulasEvidence, evidenceAxioms, null, sql, ilpCon);
        if(Parameters.DEBUG_OUTPUT == true)
            System.out.println("#evidence axioms: " + evidenceAxioms.size());

        // WITHOUT CPI (is almost empty if CPI is enabled)
        // add Hard and Soft Formula Restrictions derived from the result tables

        // start CPI with empty constraints and with

        this.putResultsIntoTables(new ArrayList<String>(), model, sql);
        this.computeGroundedClauses(formulasEvidence, null, evidenceAxioms, sql);
        addConstraintsFromSQLResults(true, formulasBeforeCPI, null, evidenceAxioms, sql, ilpCon);

        // addConstraintsFromSQLResults(formulasDuringCPI, null, evidenceAxioms,
        // sql, gurobi);
    }


    /**
     * Prepares the ILP for the MCSAT sampler. Returns every clause.
     * 
     * @throws SolveException
     * @throws ParseException
     * 
     */
    public ArrayList<Clause> getAllClauses() throws SolveException, ParseException
    {
        // Get ground clauses for objective formulas.
        ArrayList<Clause> clauses = this.getObjectiveVariableClauses(model);

        // Get ground clauses for evidence formulas.
        for(FormulaHard f : formulasEvidence) {
            RestrictionBuilder b = f.getRestrictionBuilder();
            clauses.addAll(b.getClauses());
            if(f instanceof FormulaCardinality && Parameters.USE_SYMMETRIES_IN_MARGINAL_INFERENCE) { throw new ParseException(
                    "In marginal inference we can not use cardinality constraints when symmetry detection is activated. Set use_symmetries_in_marginal_inference to false in configruation file or remove every existential / cardinality constraint."); }
        }

        // get clauses for all other formulas (formulasAfterCPI is empty)
        for(FormulaHard f : formulasBeforeCPI) {
            RestrictionBuilder b = f.getRestrictionBuilder();
            clauses.addAll(b.getClauses());
            if(f instanceof FormulaCardinality && Parameters.USE_SYMMETRIES_IN_MARGINAL_INFERENCE) { throw new ParseException(
                    "In marginal inference we can not use cardinality constraints when symmetry detection is activated. Set use_symmetries_in_marginal_inference to false in configruation file or remove every existential / cardinality constraint."); }
        }
        return clauses;
    }


    /**
     * Belongs to solve procedure. Excluded because this is needed also by other
     * solving techniques like the Exact k boundend map inference method.
     * 
     * Initially, it puts the evidence and the ground formulas with only one literal and weigt > 0 into the ILP.
     * 
     * In each iteration, it solves the ILP, puts the result into the table, computes grounded clauses, and adds (violated) constraints to the ILP.
     * 
     * @return
     * @throws SolveException
     * @throws ParseException
     * @throws ReadOrWriteToFileException
     */
    public ArrayList<String> runCuttingPlaneInference() throws SolveException, ParseException
    {
        

        // put Objective Variables into ILP
        this.putObjectiveVariableIntoILP(model);

        // Evidence formulas only need to be added once.
        this.computeGroundedClauses(formulasEvidence, evidenceAxioms, null, sql);
        this.addConstraintsFromSQLResults(true/*false*/, formulasEvidence, evidenceAxioms, null, sql, ilpCon);
        if(Parameters.DEBUG_OUTPUT == true)
            System.out.println("#evidence axioms: " + evidenceAxioms.size());

        // WITHOUT CPI (is almost empty if CPI is enabled)
        // add Hard and Soft Formula Restrictions derived from the result tables

        // start CPI with empty constraints and with

        this.putResultsIntoTables(results, model, sql);
        this.computeGroundedClauses(formulasBeforeCPI, null, evidenceAxioms, sql);
        this.addConstraintsFromSQLResults(true, formulasBeforeCPI, null, evidenceAxioms, sql, ilpCon);
        
        
        // addConstraintsFromSQLResults(formulasDuringCPI, null, evidenceAxioms,
        // sql, gurobi);

        // WITH CPI
        int loopCount = 0;
        boolean hasChanged = false;
        do {
            System.out.print("---- Start  Loop " + loopCount + " ----");
            System.out.println(new Date());
            // solve of the ILP
            long ilptime = System.currentTimeMillis();
            results = ilpCon.solve();
            ilptime = System.currentTimeMillis() - ilptime;
            ilpTotalTime += ilptime;
            System.out.println("ILP took " + ilptime + " Milliseconds.");

            // creates tables and puts the results in them.
            this.putResultsIntoTables(results, model, sql);
            // add Formula Restrictions (all kinds, hard, soft, and cardinality)
            // derived from the result tables
            this.computeGroundedClauses(formulasDuringCPI, null, evidenceAxioms, sql);
            hasChanged = this.addConstraintsFromSQLResults(true, formulasDuringCPI, null, evidenceAxioms, sql, ilpCon);

            // write in log
            loopCount++;
            // Stop when nothing has changed OR
            // (when constraint sampling is enabled and loopCount >=
            // numberOfRounds
            // constraintSampling = !Parameters.CONSTRAINT_SAMPLING_ACTIVATE ||
            // (loopCount <= Parameters.CONSTRAINT_SAMPLING_ROUNDS);
        } while(hasChanged);
        // close logfile
        this.closeLogFile();

        System.out.println("Total ILP Time: " + ilpTotalTime);
        return results;
    }


    public Collection<Literal> getEvidenceAxioms()
    {
        return this.evidenceAxioms.values();
    }

    private final int maxorder = 20;
    private int numberOfAggregatedSoftFormulas                                                  = 0;
    private int numberOfAggregatedHardFormulas                                                  = 0;
    private int numberOfQuadraticAggregatedClauses                                              = 0;
    private int numberOfQuadraticCountingConstraintsAggregatingMoreThanOneClause                = 0;
    private int numberOfQuadraticCountingConstraintsWithMoreThanOneLiteral                      = 0;
    private int numberOfQuadraticConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral = 0;
    private int numberOfQuadraticCountingConstraintsWithOneLiteral                              = 0;
	private int numberOfQuadraticConstraintsAggregatedByContingConstraintWithOneLiteral         = 0;
    
    private int numberOfAggregatedClauses                                                       = 0;
    private int numberOfCountingConstraintsAggregatingMoreThanOneClause                         = 0;
    private int numberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral          = 0;
    private int numberOfCountingConstraintsWithMoreThanOneLiteral                               = 0;
    private int numberOfCountingConstraintsWithOneLiteral                                       = 0;
    private int numberOfConstraintsAggregatedByContingConstraintWithOneLiteral                  = 0;

    private int[] numberOfFullAggregatedClauses                                              = new int[maxorder];
    private int[] numberOfFullCountingConstraintsAggregatingMoreThanOneClause                = new int[maxorder];
    private int[] numberOfFullCountingConstraintsWithMoreThanOneLiteral                      = new int[maxorder];
    private int[] numberOfFullConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral = new int[maxorder];
    private int[] numberOfFullCountingConstraintsWithOneLiteral                              = new int[maxorder];
	private int[] numberOfFullConstraintsAggregatedByContingConstraintWithOneLiteral         = new int[maxorder];


    /**
     * Cutting plane inference + CPA must be disabled.
     * 
     * @throws SolveException
     * @throws ParseException
     */
    public void performGrounding() throws SolveException, ParseException
    {
        StandardGrounder grounder = new StandardGrounder(model, this.sql);
        grounder.ground();

        this.putResultsIntoTables(new ArrayList<String>(), model, sql);
        this.computeGroundedClauses(formulasEvidence, evidenceAxioms, null, sql);
        this.computeGroundedClauses(formulasDuringCPI, null, evidenceAxioms, sql);
        this.computeGroundedClauses(formulasBeforeCPI, null, evidenceAxioms, sql);
    }


    /**
     * Retrieve the constraints for a given set of soft and hard formulas.
     * 
     * The retrieved constraints are saved in the respective RestrictionBuilder
     * of each formula.
     * 
     * @param formulas
     *            soft and hard formulas
     * @param literals
     *            evidence literals
     * @param evidenceAxioms
     * @param sql
     * @return the same set of formula with updated RestrictionBuilders in each
     *         formulas
     * @throws SolveException
     */
    private void computeGroundedClauses(HashSet<FormulaHard> formulas, HashMap<Literal, Literal> literals, HashMap<Literal, Literal> evidenceAxioms, MySQLConnector sql) throws SolveException
    {
        if(Parameters.DEBUG_OUTPUT)
            System.out.println("get constraints from SQL database " + new Date());

        // Create formula stack for parallelization
        boolean trackLiteral = true;
        if(literals == null) {
            trackLiteral = false;
        }

        // PARALLEL: Get SQL results and parse them.
        ExecutorService service = Executors.newFixedThreadPool(Parameters.THREAD_NUMBER);
        // ArrayList<RestrictionBuilderThread> threads = new
        // ArrayList<RestrictionBuilderThread>();
        // int numberOfThreads = Parameters.THREAD_NUMBER;

        for(FormulaHard f : formulas) {
            RestrictionBuilder restBuilder = f.getRestrictionBuilder();
            restBuilder.setEvidenceAxioms(evidenceAxioms);
            restBuilder.setSql(sql);
            restBuilder.setTrackLiterals(trackLiteral);

            service.execute(restBuilder);
        }
        // wait for all threads and close the service if everything has
        // finished.
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        if(Parameters.DEBUG_OUTPUT) {
            System.out.print("All threads closed: ");
            System.out.println(service.isShutdown());
        }
        // add evidences
        for(FormulaHard formula : formulas) {
            RestrictionBuilder resBuilder = formula.getRestrictionBuilder();
            if(trackLiteral) {
                literals.putAll(resBuilder.getLiterals());
            }
        }

    }


    /**
     * 
     * Retrieve the constraints for a given set of soft and hard formulas and
     * add them to the ILP.
     * 
     * @param clauses
     *            the list with restrictions
     * @param sql
     *            the sql reference
     * @param gurobi
     *            the gurobi reference
     * @return false if nothing has changed, true else.
     * @throws SolveException
     */
    private boolean addConstraintsFromSQLResults(boolean aggregateHardFormulas, HashSet<FormulaHard> formulas, HashMap<Literal, Literal> literals, HashMap<Literal, Literal> evidenceAxioms, MySQLConnector sql, ILPConnector gurobi) throws SolveException
    {

        if(Parameters.DEBUG_OUTPUT)
            System.out.println("add retrieved constraints to ILP " + new Date());
        boolean foundOneRestriction = false;

        // SINGLE: Add constraints to gurobi
        for(FormulaHard formula : formulas) {

            RestrictionBuilder resBuilder = formula.getRestrictionBuilder();
            // if(Parameters.DEBUG_OUTPUT)
            if(Parameters.USE_CUTTING_PLANE_AGGREGATION) {// && Parameters.DEBUG_OUTPUT) {
                AggregationManager m = resBuilder.getAggregationManager();
                if(m != null) {
                	if (Parameters.DEBUG_OUTPUT)
                		if (m.aggregationOrder() > 2) //TODO Remove this 
                			System.out.println("**** ORDER: "+m.aggregationOrder() + " ****\n" + m.toString());//
                		
                	if (formula instanceof FormulaSoft)
                		numberOfAggregatedSoftFormulas ++;
                	else
                		numberOfAggregatedHardFormulas ++;
	            	if (Parameters.AGGREGATION_ORDER < 0) {
	            		int o = m.aggregationOrder();//will never be zero here //TODO report
	            		numberOfFullAggregatedClauses[o] += m.getNumberOfAggregatedClauses();
	                    numberOfFullCountingConstraintsAggregatingMoreThanOneClause[o] += m.getNumberOfCountingConstraintsAggregatingMoreThanOneClause();
	                    numberOfFullCountingConstraintsWithMoreThanOneLiteral[o] += m.getNumberOfCountingConstraintsWithMoreThanOneLiteral();
	                    numberOfFullConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral[o] += m.getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral();
	                    numberOfFullCountingConstraintsWithOneLiteral[o] += m.getNumberOfCountingConstraintsWithOneLiteral();
	            		numberOfFullConstraintsAggregatedByContingConstraintWithOneLiteral[o] += m.getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral();
            		} else if (m.aggregationOrder() > 1) {
                		numberOfQuadraticAggregatedClauses += m.getNumberOfAggregatedClauses();
                        numberOfQuadraticCountingConstraintsAggregatingMoreThanOneClause += m.getNumberOfCountingConstraintsAggregatingMoreThanOneClause();
                        numberOfQuadraticCountingConstraintsWithMoreThanOneLiteral += m.getNumberOfCountingConstraintsWithMoreThanOneLiteral();
                        numberOfQuadraticConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral += m.getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral();
	                    numberOfQuadraticCountingConstraintsWithOneLiteral += m.getNumberOfCountingConstraintsWithOneLiteral();
	            		numberOfQuadraticConstraintsAggregatedByContingConstraintWithOneLiteral += m.getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral();
            		} else if (m.aggregationOrder() == 1) {
                		numberOfAggregatedClauses += m.getNumberOfAggregatedClauses();
                		numberOfCountingConstraintsAggregatingMoreThanOneClause += m.getNumberOfCountingConstraintsAggregatingMoreThanOneClause();
                		numberOfCountingConstraintsWithMoreThanOneLiteral += m.getNumberOfCountingConstraintsWithMoreThanOneLiteral();
                		numberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral += m.getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral();
                        numberOfCountingConstraintsWithOneLiteral += m.getNumberOfCountingConstraintsWithOneLiteral();
                        numberOfConstraintsAggregatedByContingConstraintWithOneLiteral += m.getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral();
                	}

                }
            }
           	resBuilder.addConstraints(aggregateHardFormulas, gurobi);

            if(resBuilder.isFoundOneRestriction())
                foundOneRestriction = true;

        }
        if(Parameters.USE_CUTTING_PLANE_AGGREGATION) {// && Parameters.DEBUG_OUTPUT) {
        	if (Parameters.AGGREGATION_ORDER > 1)
	            System.out.println(
	                 "#AggregatedHardFormulas: " + numberOfAggregatedHardFormulas
	             + "\n#AggregatedSoftFormulas: " + numberOfAggregatedSoftFormulas
	             + "\n#AggregatedClauses: " + numberOfAggregatedClauses
	             + "\n#QuadraticAggregatedClauses: " + numberOfQuadraticAggregatedClauses
	             + "\n#CountingConstraintsAggregatingMoreThanOneClause: " + numberOfCountingConstraintsAggregatingMoreThanOneClause
	             + "\n#QuadraticCountingConstraintsAggregatingMoreThanOneClause: " + numberOfQuadraticCountingConstraintsAggregatingMoreThanOneClause
	             + "\n#CountingConstraintsWithMoreThanOneLiteral: " + numberOfCountingConstraintsWithMoreThanOneLiteral
	             + "\n#QuadraticCountingConstraintsWithMoreThanOneLiteral: " + numberOfQuadraticCountingConstraintsWithMoreThanOneLiteral
	             + "\n#ConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral: " + numberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral
	             + "\n#QuadraticConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral: " + numberOfQuadraticConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral
	             + "\n#CountingConstraintsWithOneLiteral: " + numberOfCountingConstraintsWithOneLiteral
	             + "\n#QuadraticCountingConstraintsWithOneLiteral: " + numberOfQuadraticCountingConstraintsWithOneLiteral 
	             + "\n#ConstraintsAggregatedByContingConstraintWithOneLiteral: " + numberOfConstraintsAggregatedByContingConstraintWithOneLiteral
	             + "\n#QuadraticConstraintsAggregatedByContingConstraintWithOneLiteral: " + numberOfQuadraticConstraintsAggregatedByContingConstraintWithOneLiteral
	             + "\n#ZVariables: " + getILPConnector().getZVarIndex()
	             + "\n#BinaryVariables: " + getILPConnector().getNumberOfBinaryVariables()
	             + "\n#MixedVariables: " + getILPConnector().getNumberOfMixedVariables());
        	else if (Parameters.AGGREGATION_ORDER >= 0)
	            System.out.println(
	                 "#AggregatedHardFormulas: " + numberOfAggregatedHardFormulas
	             + "\n#AggregatedSoftFormulas: " + numberOfAggregatedSoftFormulas
	             + "\n#AggregatedClauses: " + numberOfAggregatedClauses
	             + "\n#CountingConstraintsAggregatingMoreThanOneClause: " + numberOfCountingConstraintsAggregatingMoreThanOneClause
	             + "\n#CountingConstraintsWithMoreThanOneLiteral: " + numberOfCountingConstraintsWithMoreThanOneLiteral
	             + "\n#ConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral: " + numberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral
	             + "\n#CountingConstraintsWithOneLiteral: " + numberOfCountingConstraintsWithOneLiteral
	             + "\n#ConstraintsAggregatedByContingConstraintWithOneLiteral: " + numberOfConstraintsAggregatedByContingConstraintWithOneLiteral
	             + "\n#ZVariables: " + getILPConnector().getZVarIndex()
	             + "\n#BinaryVariables: " + getILPConnector().getNumberOfBinaryVariables());
        	else {
	            System.out.print(
		                   "#AggregatedHardFormulas: " + numberOfAggregatedHardFormulas
		               + "\n#AggregatedSoftFormulas: " + numberOfAggregatedSoftFormulas);
	            for (int o = 0; o < maxorder; o++)
	            	if (numberOfFullAggregatedClauses[o] +
	            		numberOfFullCountingConstraintsAggregatingMoreThanOneClause[o] +
	            		numberOfFullCountingConstraintsWithMoreThanOneLiteral[o] +
	            		numberOfFullConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral[o] +
	            		numberOfFullCountingConstraintsWithOneLiteral[o] +
	            		numberOfFullConstraintsAggregatedByContingConstraintWithOneLiteral[o] > 0)
	            		System.out.print(
	           	               "\n#AggregatedClauses[" + o + "]: " + numberOfFullAggregatedClauses[o]
	        	             + "\n#CountingConstraintsAggregatingMoreThanOneClause[" + o + "]: " + numberOfFullCountingConstraintsAggregatingMoreThanOneClause[o]
	        	             + "\n#CountingConstraintsWithMoreThanOneLiteral[" + o + "]: " + numberOfFullCountingConstraintsWithMoreThanOneLiteral[o]
	        	             + "\n#ConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral[" + o + "]: " + numberOfFullConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral[o]
	        	  	             + "\n#CountingConstraintsWithOneLiteral[" + o + "]: " + numberOfFullCountingConstraintsWithOneLiteral[o]
	        	  	             + "\n#ConstraintsAggregatedByContingConstraintWithOneLiteral[" + o + "]: " + numberOfFullConstraintsAggregatedByContingConstraintWithOneLiteral[o]);

	            System.out.println(
	               "\n#ZVariables: " + getILPConnector().getZVarIndex()
	             + "\n#BinaryVariables: " + getILPConnector().getNumberOfBinaryVariables()
	             + "\n#MixedVariables: " + getILPConnector().getNumberOfMixedVariables());
	            				
        	}
        	
        } else
        	System.out.println("\n#BinaryVariables: " + getILPConnector().getNumberOfBinaryVariables());

        System.out.println();
        return foundOneRestriction;
    }



    /**
     * Takes the results of the ILP and puts it into tables.
     * 
     * Naming convention of results:
     * [NameOfHiddenPredicate]|[value1]|...|[valueN]
     * 
     * The resulting tables are named after the hidden predicate
     * (NameOfHiddenPredicate).
     * 
     * 
     * @param results
     *            Result of the ILP
     * @param sql
     * @throws ReadOrWriteToFileException
     */
    private void putResultsIntoTables(ArrayList<String> results, Model model, MySQLConnector sql) throws DatabaseException
    {

        // convert results into Array (Devided by | delimiter)
        // and determine the table names and the length of the tables
        // which need to be created.
        ArrayList<String[]> dataForInsert = new ArrayList<String[]>();
        // stores the table name as key and the number of cols as value.

        for(String result : results) {
            String[] resultArray = result.split("\\|");
            dataForInsert.add(resultArray);
        }
        // stores the table name as key and the number of cols as value.

        for(PredicateAbstract predicate : model.getAllHiddenPredicates()) {
            String tableName = predicate.getName();

            // drop tables (if exist)
            sql.dropTable(tableName);

            // create Tables
            int numberOfCols = predicate.getTypes().size();
            ArrayList<String> cols = new ArrayList<String>();
            for(int i = 0; i < numberOfCols; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append("field").append(i);
                cols.add(sb.toString());
            }

            Integer size = u.getMaximalKeySize();

            sql.createInMemoryTable(tableName, null, false, cols, size);

            // insert values
            // values for insert is just for the specific table...
            ArrayList<String[]> valuesForInsert = new ArrayList<String[]>();
            for(String[] transform : dataForInsert) {
                if(transform[0].equalsIgnoreCase(tableName)) {
                    // bring into correct format for the "addData" call.
                    String[] insertValues = new String[(transform.length) - 1];
                    for(int i = 0; i < insertValues.length; i++) {
                        insertValues[i] = transform[i + 1];
                    }
                    valuesForInsert.add(insertValues);
                }
            }
            StringBuilder filename = new StringBuilder();
            filename.append(tableName).append("_temp.db");
            sql.addData(tableName, valuesForInsert, filename.toString());
        }

    }


    /**
     * Use simple select from statements to get the grounded objective
     * variables.
     * 
     * Then they are put into the ilp.
     * 
     * Naming conventions: The variables are named like this:
     * [HiddenPredicateName]|[Value1]|[Value2]|...|[ValueN]
     * 
     * @param model
     * @param sql
     * @param ilpCon
     * @return
     * @throws SolveException
     * @throws ILPException
     */
    private ArrayList<Clause> getObjectiveVariableClauses(Model model) throws SolveException, ILPException
    {
        ArrayList<Clause> literals = new ArrayList<Clause>();
        for(FormulaAbstract formulaAbstract : model.getFormulas()) {
            if(formulaAbstract.getClass().equals(FormulaObjective.class)) {
                FormulaObjective formula = (FormulaObjective) formulaAbstract;
                PredicateExpression predicateExpression = formula.getObjectiveExpression();

                // build simple select query
                StringBuilder select = new StringBuilder();
                select.append("Select ");
                ArrayList<VariableAbstract> variables = predicateExpression.getVariables();
                int variablesSize = variables.size();
                for(int i = 0; i < variablesSize; i++) {
                    select.append(variables.get(i).getName());
                    select.append(", ");
                }
                select.append(formula.getDoubleVariable().getName());
                select.append(" FROM ").append(formula.getName());
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println(select.toString());

                // execute the select query
                try {
                    ResultSet results = sql.executeSelectQuery(select.toString());
                    while(results.next()) {
                        // construct variable String
                        StringBuilder variable = new StringBuilder();
                        variable.append(predicateExpression.getPredicate().getName());
                        for(int i = 1; i < (variablesSize + 1); i++) {
                            variable.append("|").append(results.getString(i));
                        }
                        double weight = results.getDouble(variablesSize + 1);
                        Literal l = new Literal(variable.toString(), true);
                        ArrayList<Literal> c = new ArrayList<Literal>();
                        c.add(l);
                        literals.add(new Clause(weight, c, false));

                    }
                    results.getStatement().close();
                    results.close();
                } catch(SQLException e) {
                    throw new SolveException("An SQL error occured at /n Query: " + select.toString() + "/n position: " + e.getMessage());
                }
            }
        }
        return literals;
    }


    /**
     * Use simple select from statements to get the grounded objective
     * variables.
     * 
     * Then they are put into the ilp.
     * 
     * Naming conventions: The variables are named like this:
     * [HiddenPredicateName]|[Value1]|[Value2]|...|[ValueN]
     * 
     * @param model
     * @param sql
     * @param ilpCon
     * @return
     * @throws SolveException
     * @throws SQLException
     */
    private void putObjectiveVariableIntoILP(Model model) throws SolveException
    {
        for(FormulaAbstract formulaAbstract : model.getFormulas()) {
            if(formulaAbstract.getClass().equals(FormulaObjective.class)) {
                FormulaObjective formula = (FormulaObjective) formulaAbstract;
                PredicateExpression predicateExpression = formula.getObjectiveExpression();

                // build simple select query
                StringBuilder select = new StringBuilder();
                select.append("Select ");
                ArrayList<VariableAbstract> variables = predicateExpression.getVariables();
                int variablesSize = variables.size();
                for(int i = 0; i < variablesSize; i++) {
                    select.append(variables.get(i).getName());
                    select.append(", ");
                }
                select.append(formula.getDoubleVariable().getName());
                select.append(" FROM ").append(formula.getName());
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println(select.toString());

                // execute the select query
                try {
                    ResultSet results = sql.executeSelectQuery(select.toString());
                    while(results.next()) {
                        // construct variable String
                        StringBuilder variable = new StringBuilder();
                        variable.append(predicateExpression.getPredicate().getName());
                        for(int i = 1; i < (variablesSize + 1); i++) {
                            variable.append("|").append(results.getString(i));
                        }
                        double weight = results.getDouble(variablesSize + 1);

                        // add variable to Gurobi solver
                        ilpCon.addVariable(variable.toString(), weight, 0.0, 1.0, false, ILPVarCategory.NORMAL);

                    }
                    results.getStatement().close();
                    results.close();
                } catch(SQLException e) {
                    throw new SolveException("An SQL error occured at /n Query: " + select.toString() + "/n position: " + e.getMessage());
                }
            }
        }
    }


    private void generateTablesPerFormulaForDublicateDetection(FormulaAbstract formula) throws DatabaseException
    {
        ArrayList<String> fieldNames = new ArrayList<String>();
        for(VariableType var : formula.getForVariables()) {
            fieldNames.add(var.getName());
        }
        Integer size = u.getMaximalKeySize();
        if(formula instanceof FormulaSoft) {
            // if the weight is negative, do not create temporary tables
            // (because
            // we can not access them more than once within one query)
            FormulaSoft fs = (FormulaSoft) formula;
            if(fs.getRestrictions().size() > 1 && fs.getWeight() < 0) {
                sql.createInMemoryTable(formula.getName(), "value", false, fieldNames, size);
            } else {
                sql.createInMemoryTable(formula.getName(), "value", true, fieldNames, size);
            }
        } else if(formula instanceof FormulaHard) {
            sql.createInMemoryTable(formula.getName(), null, true, fieldNames, size);

        }
    }



    public void setInitialSolution()
    {
        ArrayList<String> varNames = new ArrayList<String>();
        for(PredicateAbstract p : model.getInitialSolution()) {
            for(String[] a : p.getGroundValues()) {
                StringBuilder varName = new StringBuilder(p.getName());
                for(String s : a) {
                    varName.append("|" + s);
                }
                varNames.add(varName.toString());
            }           
        }
        // set results to varNames.
        //results = varNames;
        try {
            ilpCon.addStartValues(varNames);
        } catch(ILPException e) {
            e.printStackTrace();
        }
    }

}
