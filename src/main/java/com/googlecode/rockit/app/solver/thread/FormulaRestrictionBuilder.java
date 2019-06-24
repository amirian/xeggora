package com.googlecode.rockit.app.solver.thread;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.aggregate.simple.AggregationManagerOptimalColumnImpl;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;

import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.conn.sql.MySQLConnector;

import com.googlecode.rockit.exception.DatabaseException;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.SolveException;

import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableString;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;
import com.mlnengine.xeggora.app.solver.aggregate.AggregationManagerDoubleOptimalImpl;
import com.mlnengine.xeggora.app.solver.aggregate.FullAggregationManager;
import com.mlnengine.xeggora.app.solver.aggregate.PowerSet;

public class FormulaRestrictionBuilder extends RestrictionBuilder {

	final private static boolean aggregateFullAfterEvidence = true;//TODO move this to parameters 
	final private static boolean discardBarrenAggregation = true;//TODO move this to parameters 
	private FormulaHard formula = null;
	private HashMap<Literal,Literal> literals = new HashMap<Literal,Literal>();

	private ArrayList<Clause> clauses = new ArrayList<Clause>();
	
	private MySQLConnector sql =null;
	
	private boolean trackLiterals = false;
	
	private boolean hasExtraClauses;
	private int restrictionCounter;
	private int restrictionCounterWithoutEvidence;
	private HashMap<Literal,Literal> evidenceAxioms = null;
	
	private AggregationManager aggregationManager = null;
	private FullAggregationManager fullAggregationManager = null;
	
	private boolean foundOneRestriction = false;
	public FormulaRestrictionBuilder(FormulaHard formula) {
		  this.formula=formula;
		  this.reset();
		  literals = new HashMap<Literal,Literal>();
		  this.foundOneRestriction =false;
	}
	
	public void reset(){
		hasExtraClauses = false;
		//Me		  if(formula instanceof FormulaSoft)
		  if(Parameters.USE_CUTTING_PLANE_AGGREGATION){
		//Me{				  
		  if (Parameters.AGGREGATION_ORDER == 1)
			  this.aggregationManager=new AggregationManagerOptimalColumnImpl(formula.getRestrictions().size());
		  else if (Parameters.AGGREGATION_ORDER == 2)
			  this.aggregationManager=new AggregationManagerDoubleOptimalImpl(formula.getRestrictions().size(), Parameters.AGGREGATION_ORDER);
		  else
			  this.fullAggregationManager=null;//Me TODO new FullAggregationManager();
		//}Me			  
		  }
	
	}
	
	public ArrayList<Clause> getClauses(){
		return this.clauses;
	}
	  
	  /* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#getFormula()
	 */
	@Override
	public FormulaHard getFormula(){
		  return formula;
	  }
	  
	public void run(){
		this.generateRestrictions();
	}	
	
	private ArrayList<PredicateExpression> generateAndRunCountingQuery(
			String tableSQL, 
			ArrayList<PredicateExpression> restrictions,
			Set<VariableType> forVariables) throws DatabaseException{
			
//Me{ generate All possible 2-clusters for restrictions
		
//List of all possible clustering candidate sets of predicates with reverse order (Bigger restrictions set is better)
		TreeMap<ArrayList<PredicateExpression>,Set<VariableType>> candidates = 
				new TreeMap<ArrayList<PredicateExpression>,Set<VariableType>> 
//This comparator together with setting Set<PredicateExpression> as key, prevents the Map to get large.
//TODO doesn't seem to have bugs,but much care is needed to avoid overwriting important candidate clusters		
				(new Comparator<ArrayList<PredicateExpression>>() {
					@Override 
					public int compare(ArrayList<PredicateExpression> o1, ArrayList<PredicateExpression> o2) {
						if (o1.size()!=o2.size())
							return o2.size() - o1.size();
						return o2.toString().compareTo(o1.toString());
					} 
				});	
//Me Get all subsets of variables				
		
		PowerSet<VariableType> pset = new PowerSet<VariableType>(forVariables);
		for(Set<VariableType> subsetVar: pset) {
			ArrayList<PredicateExpression> identicalLiterals = new ArrayList<PredicateExpression>();
		   	for (PredicateExpression literal: restrictions) 
		   		if (subsetVar.containsAll(literal.getAllVariables())) 
		   			//if (restrictions.size() - identicalLiterals.size() > 1)//first method
		   			identicalLiterals.add(literal);
		   	if (!identicalLiterals.isEmpty()
		   		&& identicalLiterals.size() < restrictions.size())//second method:
		   		candidates.put(identicalLiterals,subsetVar);
		}
		boolean DBDEBUG = false;
		if (Parameters.DEBUG_OUTPUT)
			System.out.println("ALL: " + restrictions.size());

//Generate SQL for counting all possible candidates from table 
		StringBuilder countingSQL = new StringBuilder("SELECT COUNT(*) as 'all'");
		if (candidates.size() == 0) {
			return new ArrayList<PredicateExpression>();
//TODO IMPORTANT: why is it null?			
		}
		String alias = tableSQL.substring(tableSQL.length() - 2) + '.';//either 'xx' or 'yy'(for CPI)
		for(Entry<ArrayList<PredicateExpression>,Set<VariableType>> candidate: candidates.entrySet()) {
				StringBuilder vars = new StringBuilder(",COUNT(DISTINCT ");
				StringBuilder varName = new StringBuilder("as `n_");
				for(VariableType var : candidate.getValue()) {
				vars.append(alias);
				vars.append(var.getName());
				vars.append(", ");
				varName.append(var.getName());
				varName.append("|");
			}
					
			vars.setCharAt(vars.length() - 2, ')');
			varName.setCharAt(varName.length() - 1, '`');
			countingSQL.append(vars);
			countingSQL.append(varName);
			if (Parameters.DEBUG_OUTPUT) {
				if (candidate.getKey().size() + 1 == restrictions.size())
					System.out.print("FIRST ORDER  ");
				System.out.println("SIZE: " + candidate.getKey().size() + " pr: " + candidate.getKey() + ", Vars: " + varName);
		}
		}
		if (Parameters.DEBUG_OUTPUT)
			System.out.println();
//new	countingSQL.setCharAt(6, ' ');
		
		countingSQL.append(tableSQL.substring(tableSQL.indexOf("FROM")));
		if (Parameters.DEBUG_OUTPUT)
			System.out.println("Counting SQL: " + countingSQL);
		try {
		    ResultSet countingRes = sql.executeSelectQuery(countingSQL.toString());
		/*replaced with the following loop, must have a one to one mapping
		    while (countingRes.next()) {
				ResultSetMetaData md = countingRes.getMetaData();
				for (int i = 1; i <= md.getColumnCount(); i++)
					if (Parameters.DEBUG_OUTPUT)
						System.out.println(md.getColumnName(i) + ": " + countingRes.getString(i));
			}
		*/
			if (!countingRes.next())
				throw new DatabaseException("Counting features from database don't map with its generator set!!!");
		    int index = 0;
		    int order = 0;
		    int minorder = 0;
		    ArrayList<PredicateExpression> bestCandidate = null;
		    int min = Integer.MAX_VALUE;
		    //DEBUG 
		    int min2 = min;
	    	int numOfRecords = countingRes.getInt(++index);//new
		    
		    for(Entry<ArrayList<PredicateExpression>,Set<VariableType>> candidate: candidates.entrySet()) {
		    	int num = countingRes.getInt(++index);
		    	order = restrictions.size() - candidate.getKey().size();
    			if (DBDEBUG) 
		    		if (num == 0)
		    			num = num;
		    		else
		    			if (num == 1)
		    	//TODO What's happening?
    						System.out.println("SINGLE, ORDER: " + order + 
    								" Clusters: " + min + ": " + candidate.getKey() + 
    								"\nALL RES: " + restrictions + 
    								"\nOther CAN: "+ bestCandidate);

		    	if (Parameters.DEBUG_OUTPUT) 
		    		System.out.print("\n" + candidate + ": " + num + " Clusters. ORDER: " + order);
		    	if ((!discardBarrenAggregation || num < numOfRecords /*new*/) && min > num) // Be careful: min>=num is WRONG!
		    		if (minorder != 1 || order == 1)//benefit of 1st order
		    		{// || (num <<< min) BUT 1st order is always better
		    			min = num;
		    			minorder = order;
		    			bestCandidate = candidate.getKey();
		    		}
		    	//DEBUG
		    	else	min2 = num;
		    }
		    if (Parameters.DEBUG_OUTPUT) 
		    	System.out.println("\nSo, best clustering Aggregation is by: " + bestCandidate + " with: " + min + " CLUSTERS. ORDER: " + minorder + "\n");
//DEBUG
		    if (DBDEBUG && min2 < min) System.out.println("SACRIFICED: " + min2 + " with: " + min + " Clusters.");
			if (countingRes.next())//Must have only one row
				throw new DatabaseException("Counting features from database don't map with its generator set!!!");
		//}Me		        
		    countingRes.getStatement().close();
			countingRes.close();
			return min == 0 ? null : bestCandidate;
		}catch (SQLException e){
			e.printStackTrace();
			throw new DatabaseException("FormulaRestrictionBuilder: Problems in COUNTING SQL.");
		}
	}

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#generateRestrictions()
	 */
	@Override
	public void generateRestrictions(){
		try {
			clauses = new ArrayList<Clause>();
			literals = new HashMap<Literal,Literal>();
			foundOneRestriction=false;
			hasExtraClauses = false;
			int additionalRestrictionCounter = 0;
			    
			ResultSet res;
			boolean isHard = true;
			boolean fair = false;
			boolean doubleVariableExists=false;
			FormulaSoft formulaSoft = null;
//Me
			if (Parameters.DEBUG_OUTPUT) 
				System.out.println("Starting process of formula: "+formula);
			
			if(formula instanceof FormulaSoft){
				isHard=false;
				formulaSoft=(FormulaSoft) formula;
				if(((FormulaSoft) formula).getDoubleVariable()!=null)
					doubleVariableExists=true;
				if (formulaSoft.getWeight() == null)
					return;//TODO fair = true; CONFIDENCE
				if (formulaSoft.getWeight() == 0)
					return;//TODO fair = true;
			}
			
			String tableSQL=formula.getSqlQuery();
			ArrayList<PredicateExpression> restrictions = formula.getRestrictions();
			Set<VariableType> forVariables = formula.getForVariables();
			
			ArrayList<PredicateExpression> aggregatedPredicates = null;
			ArrayList<PredicateExpression> aggregatingPredicates = null;
			if (Parameters.AGGREGATION_ORDER < 0 && !doubleVariableExists && !fair) {
				aggregatedPredicates = generateAndRunCountingQuery(tableSQL, restrictions, forVariables);
				if (aggregatedPredicates == null || aggregatedPredicates.size() == 0 && restrictions.size() > 1) //first order is an exception: can aggregate on empty candidate
					fair = true;
				else {
					aggregatingPredicates = new ArrayList<PredicateExpression>(restrictions);
					aggregatingPredicates.removeAll(aggregatedPredicates);
//Me Temp
//					if (aggregatingPredicates.size()>2)
//						System.out.println("WOW: " + aggregatedPredicates + " -- " + aggregatingPredicates);
					fullAggregationManager = new FullAggregationManager(aggregatedPredicates, aggregatingPredicates, formula.isConjunction(), isHard);
				}
			}

			if (Parameters.DEBUG_OUTPUT) System.out.println("Table SQL: " + tableSQL);
			HashMap<String,Integer> varIndex = new HashMap<String, Integer>();
			int i=0;
			for(VariableType var : forVariables){
				varIndex.put(var.getName(), i);
				i++;
			}
			int numberOfVars = varIndex.size();
			ArrayList<String[]> valuesToInsert = new ArrayList<String[]>();
			ArrayList<Double> doubleToInsert = new ArrayList<Double>();
			try {
				res = sql.executeSelectQuery(tableSQL);
//Me Temp
//				if (Parameters.DEBUG_OUTPUT)
//					if (aggregatedPredicates != null && aggregatingPredicates != null)
//						System.out.println("HEY: " + aggregatedPredicates + " || " + aggregatingPredicates);
				while (res.next()) {
// if this restriction has not been added yet.
					restrictionCounterWithoutEvidence++;
// get information out of axiom
					String[] resTemp = new String[numberOfVars];
					i=1;
					if(formula instanceof FormulaSoft) i=2;
					for(int j=i ; j<(i+numberOfVars); j++) {
						resTemp[j-i] = res.getString(j);
						//if(formula instanceof FormulaSoft && ((FormulaSoft) formula).getWeight()<0)System.out.println("ohhh mmmmyyy goood " + res.getString(j));
					}
					if(formula.isCuttingPlaneInferenceUsed()) valuesToInsert.add(resTemp);
		
					double weight =0;
					if(formula instanceof FormulaSoft){
						weight = res.getDouble(1);
						if(formula.isCuttingPlaneInferenceUsed()) doubleToInsert.add(weight);
					}
					
					ArrayList<Literal> axiomsForClause = null;
					boolean shouldAddWithFullAggregation = Parameters.USE_CUTTING_PLANE_AGGREGATION && Parameters.AGGREGATION_ORDER < 0 && !doubleVariableExists && !fair;// && !isHard
					if (shouldAddWithFullAggregation) {
						ArrayList<Literal> aggregatedAxiomsForClause = removeEvidenceLiterals(varIndex, resTemp, fullAggregationManager.getAggregatedExpressions());
						if (aggregatedAxiomsForClause == null)
							continue;
						ArrayList<Literal> aggregatingAxiomsForClause = removeEvidenceLiterals(varIndex, resTemp, fullAggregationManager.getAggregatingExpressions());
						if (aggregatingAxiomsForClause == null)
							continue;
						if (aggregatedAxiomsForClause.size() == 0) {
							if (aggregatingAxiomsForClause.size() == 0)
								continue;
							if (aggregatingAxiomsForClause.size() != 1 && !aggregateFullAfterEvidence) {//first order is an exception
								shouldAddWithFullAggregation = false;
								hasExtraClauses = true;
								axiomsForClause = aggregatingAxiomsForClause;
							}
						}
						if (shouldAddWithFullAggregation) {
							fullAggregationManager.addForAggregation(aggregatedAxiomsForClause, aggregatingAxiomsForClause, weight);
							foundOneRestriction = true;
							additionalRestrictionCounter++;
							//	if(this.restrictionCounter%10000==0) System.out.print(".");
						}
					}
					if (!shouldAddWithFullAggregation) {
						if (axiomsForClause == null)
							axiomsForClause = removeEvidenceLiterals(varIndex, resTemp, formula.getRestrictions());
						Clause clause;
						if (axiomsForClause == null || axiomsForClause.size() == 0)
							//Me TODO IMPORTANT, support null clauses in aggregation method
							clause = null;
						else
							clause = new Clause(weight, axiomsForClause, isHard);
						
	//Me
						if (Parameters.DEBUG_OUTPUT) System.out.println("\nNew " + clause);
						
						if(clause!=null && clause.getRestriction().size()>0){
							foundOneRestriction = true;
							additionalRestrictionCounter++;
	//Me TODO IMPORTANT, support hard formulas in aggregation method
							if(Parameters.USE_CUTTING_PLANE_AGGREGATION && !doubleVariableExists && !fair) {// && !isHard){
								if (Parameters.AGGREGATION_ORDER >= 0)
									this.aggregationManager.addClauseForAggregation(clause, formula);
								else //Should not add for full aggregation
									this.clauses.add(clause);
							} else {
								this.clauses.add(clause);
							}
						}
						/*
						if(this.restrictionCounter%10000==0){
						   	System.out.print(".");
						}*/
					}
			    }
						
				if(!doubleVariableExists && !fair) {//Me && !isHard){
					if (Parameters.AGGREGATION_ORDER > 0)
						this.aggregationManager.calculateAggregation();
					else if (Parameters.AGGREGATION_ORDER < 0)
						this.fullAggregationManager.calculateAggregation();
				}
				
				res.getStatement().close();
				res.close();
			}catch(SQLException e){
				e.printStackTrace();
				throw new DatabaseException("FormulaRestrictionBuilder: Problems in reading the SQL result.");
			}
			this.restrictionCounter=this.restrictionCounter+additionalRestrictionCounter;
			if(Parameters.DEBUG_OUTPUT){
				System.out.println("Processing formula: "+formula);
				//System.out.println(clauses);
				System.out.println("restrictions found: (new) " + additionalRestrictionCounter + " (overall without evidence) " + restrictionCounter + " (overall) "+ restrictionCounterWithoutEvidence);
				if(Parameters.AGGREGATION_ORDER > 0){
					if(this.aggregationManager!=null) //Me This report title is CHERT
						System.out.println("Aggregated " + this.aggregationManager.getNumberOfAggregatedClauses()+" clause[s].");
				} else if (Parameters.AGGREGATION_ORDER < 0) {
					if(this.fullAggregationManager!=null) 
						System.out.println("Fully Aggregated ");//TODO + this.fullAggregationManager.getNumberOfAggregatedClauses()+" clause[s].");
				} else {
					System.out.println();
				}
			}
			
			if(formula.isCuttingPlaneInferenceUsed()){
				StringBuilder filename = new StringBuilder();
				filename.append(Parameters.TEMP_PATH).append(formula.getName()).append("_tempfile.db");
				
				if(formula instanceof FormulaSoft)
					sql.addData(formula.getName(), doubleToInsert, valuesToInsert, filename.toString());
				else
					sql.addData(formula.getName(), valuesToInsert, filename.toString());	
			}
		} catch (SolveException e1) {
			e1.printStackTrace();
		}
	}
	
//returns null if no need to be added to ILP due to evidence [] if it may be added joint with other parts
	private ArrayList<Literal> removeEvidenceLiterals(HashMap<String,Integer> varIndex, String[] resTemp, ArrayList<PredicateExpression> expressions) {
		boolean isConjunction = formula.isConjunction();
		ArrayList<Literal> removedEvidence = new ArrayList<Literal>();
        for(PredicateExpression expr : expressions)
        	if (!addedAxioms(expr, varIndex, resTemp, isConjunction, removedEvidence))
        		return null;
		return removedEvidence;
	}
        
	private boolean addedAxioms(PredicateExpression expr, HashMap<String,Integer> varIndex, String[] resTemp, boolean isConjunction, ArrayList<Literal> axioms) {
        String predicate = expr.getPredicate().getName();
    	StringBuilder axiomBuilder = new StringBuilder();
    	axiomBuilder.append(predicate);
    	for(VariableAbstract var : expr.getVariables()){
        	if(var instanceof VariableType) {
        		axiomBuilder.append("|");
	        	axiomBuilder.append(resTemp[varIndex.get(var.getName())]);
        	} 
        	if(var instanceof VariableString){
        		axiomBuilder.append("|");
        		axiomBuilder.append(var.getName());
        	}

        }
    	Literal newAxiom = new Literal(axiomBuilder.toString(), expr.isPositive());
    	
    	// check evidence: dublicate detection required.
        if(Parameters.LEVERAGE_EVIDENCE && evidenceAxioms!=null) {
//Me //IMPORTANT TO ME	        	
    		Literal evidenceAxiom = this.evidenceAxioms.get(newAxiom);
        	if(evidenceAxiom!=null){
        		// if we have a disjunction AND evidenceAxiom and new Axiom are both negated or both positive
        		if(!isConjunction && (evidenceAxiom.isPositive()==newAxiom.isPositive())){
        			// then we know that it will always be true --> no need to add the restriction.
        			return false;
        		}else if(!isConjunction && (evidenceAxiom.isPositive()!=newAxiom.isPositive())){ //Me IMPORTANT BUG == replaced with !=
        			// then we know that we can omit the current newAxiom.
        		}else if(isConjunction){
               		// TODO think of conjunctions
        			axioms.add(newAxiom);
        			if(trackLiterals) this.literals.put(newAxiom,newAxiom);
        		}
        	}else{
        		axioms.add(newAxiom);
    			if(trackLiterals) this.literals.put(newAxiom,newAxiom);
        	}
        } else {
        	// if we do not check evidence, we just add the axiom.
        	axioms.add(newAxiom);
			if(trackLiterals) this.literals.put(newAxiom,newAxiom);
        }
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#addConstraints(com.googlecode.rockit.conn.ilpSolver.GurobiConnector)
	 */
	@Override
	public void addConstraints(boolean aggregateHardFormulas, ILPConnector con) throws ILPException, SolveException{
		if (aggregationManager == null && fullAggregationManager == null || hasExtraClauses) {
//			if (hasExtraClauses)
//				hasExtraClauses = true;
			if (formula instanceof FormulaSoft)
				for(Clause c : this.clauses)
					con.addSingleSoftConstraint(c.getWeight(), c.getRestriction(), formula.isConjunction());
			else
				for(Clause c : this.clauses)
					con.addHardConstraint(c.getRestriction());
			if (aggregationManager == null && fullAggregationManager == null && !hasExtraClauses)//Me TODO correct?
				return;
		}
		
		if (aggregateHardFormulas) {
//Me{ Added hard constraints for aggregation 
			if(Parameters.USE_CUTTING_PLANE_AGGREGATION) {
				if(!(formula instanceof FormulaSoft) ||((FormulaSoft) formula).getDoubleVariable()==null)
					if (Parameters.AGGREGATION_ORDER > 0)
						aggregationManager.addConstraintsToILP(con);
					else
						fullAggregationManager.addConstraintsToILP(con);
			}else{
				if (formula instanceof FormulaSoft)
					for(Clause c : this.clauses)
						con.addSingleSoftConstraint(c.getWeight(), c.getRestriction(), formula.isConjunction());
				else
					for(Clause c : this.clauses)
						con.addHardConstraint(c.getRestriction());
			}
//}Me		
		} else {
			if(formula instanceof FormulaSoft){
				if(Parameters.USE_CUTTING_PLANE_AGGREGATION && ((FormulaSoft) formula).getDoubleVariable()==null){
					if (Parameters.AGGREGATION_ORDER > 0)
						aggregationManager.addConstraintsToILP(con);
					else
						fullAggregationManager.addConstraintsToILP(con);
				}else{
					for(Clause c : this.clauses){
						con.addSingleSoftConstraint(c.getWeight(), c.getRestriction(), formula.isConjunction());
					}
				}
			}else{
				for(Clause c : this.clauses){
					con.addHardConstraint(c.getRestriction());
				}
			}
		}
		this.reset();
	}		
	

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#isFoundOneRestriction()
	 */
	@Override
	public boolean isFoundOneRestriction() {
		return foundOneRestriction;
	}
	

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#foundNoRestriction()
	 */
	@Override
	public void foundNoRestriction() {
		foundOneRestriction=false;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#setTrackLiterals(boolean)
	 */
	@Override
	public void setTrackLiterals(boolean trackLiterals) {
		this.trackLiterals = trackLiterals;
		if(!trackLiterals){
			literals = new HashMap<Literal, Literal>();
		}
	}

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#getLiterals()
	 */
	@Override
	public HashMap<Literal,Literal> getLiterals() {
		return literals;
	}

	
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#setEvidenceAxioms(java.util.HashMap)
	 */
	@Override
	public void setEvidenceAxioms(HashMap<Literal, Literal> evidence) {
		this.evidenceAxioms = evidence;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#setSql(com.googlecode.rockit.conn.sql.MySQLConnector)
	 */
	@Override
	public void setSql(MySQLConnector sql) {
		this.sql = sql;
	}

	@Override
	public AggregationManager getAggregationManager() {
		return Parameters.AGGREGATION_ORDER < 0 ? fullAggregationManager : aggregationManager;
	}


}
