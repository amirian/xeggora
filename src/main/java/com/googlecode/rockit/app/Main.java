package com.googlecode.rockit.app;

import static org.kohsuke.args4j.ExampleMode.ALL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.googlecode.rockit.app.learner.VotedPerceptronLearner;
import com.googlecode.rockit.app.sampler.gibbs.GIBBSLiteral;
import com.googlecode.rockit.app.sampler.gibbs.GIBBSSampler;
import com.googlecode.rockit.app.solver.StandardSolver;
import com.googlecode.rockit.app.solver.exact.ExactSolver;
import com.googlecode.rockit.app.solver.exact.GroundingSet;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.file.ModelFileWriter;
import com.googlecode.rockit.file.ResultFileWriter;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.parser.SyntaxReader;


/**
 * Reads in command line arguments and starts rockit.
 * 
 */
public class Main
{

    @Option (name = "-input", usage = "[required] input file, which contains the model.", metaVar = "prog.mln", required = true)
    private String       input       = null;

    @Option (name = "-data", usage = "[required] data file, which contains the groundings of the predicates. When learning is activated you can also choose more than one file. \nSeparate them with ',' (ex. 'f1.db, f2.db')", metaVar = "evidence.db", required = true)
    private String       groundings  = null;

    @Option (name = "-output", usage = "[required] output file", metaVar = "output.db", required = true)
    private String       out         = null;

    @Option (name = "-para", usage = "[optional] parameter file. If no other filename is given, it will use the standard filename 'xeggora.properties'", metaVar = "xeggora.properties", required = false)
    private String       parameter   = null;

    @Option (name = "-gap", usage = "[optional] [0.1,0.0000000001] Sets the gap of the Gurobi solver. This gives \n- exacter (value around 0.000001) but slower or \n- more approximative (value 0.01) but faster solutions.\nIf the gap parameter is not set, the standard Gurobi gap is used.", required = false)
    private double       gap         = -1;

    @Option (name = "-learn", usage = "[optional] Activates learning.", required = false)
    private boolean      learn       = false;

    @Option (name = "-marginal", usage = "[optional] Activates marginal Inference (Gibbs sampling). Use at least 10*number of sampling variables.", required = false)
    private boolean      marginal    = false;

    @Option (name = "-iterations", usage = "[optional] Number of Rounds. For marginal inference the standard value is 100 * (number of variables). For learning the standard value is 10.", required = false)
    private int          iterations  = -1;

    @Option (name = "-exact", usage = "[optional] Activates exact marginal Inference.", required = false)
    private boolean      exact       = false;

    @Option (name = "-isolution", usage = "[optional] data file, which provides an initial solution.", required = false)
    private String       initialSolution = null;
    
    // using 'handler=...' allows you to specify a custom OptionHandler
    // implementation class. This allows you to bind a standard Java type
    // with a non-standard option syntax
    // @Option(name="-custom",handler=BooleanOptionHandler.class,usage="boolean value for checking the custom handler")
    // private boolean data;

    // receives other command line parameters than options
    @Argument
    private List<String> arguments   = new ArrayList<String>();

    public static void generateMLN() throws FileNotFoundException, UnsupportedEncodingException {
/*    	PrintWriter writer = new PrintWriter("data//main//evidence.db", "UTF-8");
    	for (int i=0; i<200000;i++)
    		writer.println("Sad(R"+i+")");
    	writer.close();    	
*/
    	PrintWriter writer = new PrintWriter("data//main//evidence.db", "UTF-8");
    	for (int i=0; i<25000;i++)
    		writer.println("Kind(B,R"+i+",F,I)");
    	for (int i=25000; i<50000;i++)
    		writer.println("Kind(C,R"+i+",F,I)");
    	for (int i=50000; i<60000;i++)
    		writer.println("Kind(C,R"+i+",F,G)");
    	for (int i=60000; i<75000;i++)
    		writer.println("Kind(B,R"+i+",H,G)");
    	for (int i=75000; i<90000;i++)
    		writer.println("Kind(B,R"+i+",H,I)");
    	for (int i=90000; i<100000;i++)
    		writer.println("Kind(C,R"+i+",H,G)");
    	for (int i=0; i<50000;i++)
    		writer.println("Sad(R"+i*2+")");
    	writer.close();    	
//    	System.out.println(System.getenv("LD_LIBRARY_PATH"));
    	
    }

    public static void modifyMLN() throws FileNotFoundException, UnsupportedEncodingException {

    	BufferedReader br = new BufferedReader(new FileReader("data//pr//modified//complete//prog.mln"));
    	PrintWriter writer = new PrintWriter("data//pr//modified//complete//evidence.db", "UTF-8");
    	try {
    	    String line = br.readLine();
    	    while (line != null) {
    	    	if (!(line.length()==0||line.startsWith("//"))) {
	        	    StringBuilder sb = new StringBuilder();
	        	    sb.append("FunctionProteinConf(");
	        	    sb.append(line.substring(line.indexOf(",")+1));
	        	    sb.append(",");
	        	    sb.append(line.substring(0,line.indexOf(",")));
	        	    sb.append(")");
	        		writer.println(sb.toString());
    	    	}
    	        line = br.readLine();
    	    }
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
    	    try {
				br.close();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    public static void main(String[] args) throws IOException, ParseException, RecognitionException, SolveException, SQLException, ReadOrWriteToFileException
    {
        new Main().doMain(args);
//        generateMLN();
//        modifyMLN();
    }


    /**
     * 
     * @param args
     * @throws IOException
     * @throws ParseException
     * @throws RecognitionException
     * @throws SolveException
     * @throws SQLException
     * @throws ReadOrWriteToFileException
     */
    public void doMain(String[] args) throws IOException, ParseException, RecognitionException, SolveException, SQLException, ReadOrWriteToFileException
    {
        CmdLineParser parser = new CmdLineParser(this);

        // if you have a wider console, you could increase the value;
        // here 80 is also the default
        parser.setUsageWidth(80);

        try {
            // parse the arguments.
            parser.parseArgument(args);

            long start = System.currentTimeMillis();
            System.out.println("==== Start Xeggora =====" + new Date());
            System.out.println("-input " + input);
            System.out.println("-data " + groundings);
            
            if (initialSolution != null){
                System.out.println("-isolution " + initialSolution);
            }
            
            System.out.println("-output " + out);

            // after parsing arguments, you should check
            // if enough arguments are given.
            if(!arguments.isEmpty()) {
                System.err.println("The following non-valid inputs will be ignored:");
                for(String a : arguments) {
                    System.err.println(a);
                }
            }

            // load parameter file
            if(parameter != null) {
                Parameters.PROPERTYFILE = parameter;
                System.out.println("-para: use xeggora.properties file.");
            }
            Parameters.readPropertyFile();

            // set optional arguments
            // -gap
            if(gap <= 0.1 && gap >= 0.0000000001) {
                Parameters.GAP = gap;
                System.out.println("-gap " + gap);
            } else if(gap != -1) {
                System.err.println("Gap parameter must be between 0.01 and 0.0000000001. It is set to " + gap);
            } else {
                System.out.println("-gap: use gurobi standard gap.");
            }
            if(Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION) {
                System.out.println("-simplify Negative Weight and Conjunction");
            }

            System.out.println("-debug output " + Parameters.DEBUG_OUTPUT);

            Parameters.USE_SAMPLING = marginal;

            Parameters.USE_LEARNING = learn;
            
            // Start rockit
            SyntaxReader reader = new SyntaxReader();

			if (!exact && !learn && !marginal) {
				doMapState(reader);
            } else if(!exact && !learn && marginal) {
                doGibbsSampling(reader);
			} else if (exact && !learn && !marginal) {
				doExactInference(reader);
            } else if(!exact && learn && !marginal) {
                doLearning(reader);
            } else {
                throw new ParseException("The parameters exact, marginal, sample and learn can not be enabled at the same time.");
            }

			long duration = System.currentTimeMillis() - start;
			System.out.println("Xeggora runtime was " + duration + " milliseconds.");

        } catch(CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java -jar xeggora.jar options...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.println(" Example: java -jar xeggora.jar " + parser.printExample(ALL));

            return;
        }

        // this will redirect the output to the specified output
        // System.out.println(out);
    }

	private void doMapState(SyntaxReader reader)
			throws ParseException, IOException, SolveException, SQLException,
			ILPException, ReadOrWriteToFileException {
        // start MAP query (Compute the most probable world)
        System.out.println("-use MAP inference");
//Me{       
        if (Parameters.AGGREGATION_ORDER > 2 || Parameters.AGGREGATION_ORDER < -1) {
    		Parameters.AGGREGATION_ORDER = -1;
    		System.out.println("Aggregation order must be in {-1, 2}, setting it to default: -1");
        }
        Parameters.USE_CUTTING_PLANE_AGGREGATION = Parameters.AGGREGATION_ORDER != 0;
//}Me
        Model model = reader.getModel(this.input.toString(), this.groundings.toString());
//Me{       
        if (Parameters.DEBUG_OUTPUT) System.out.println(model);
//}Me
        StandardSolver solver = new StandardSolver(model);
        if(initialSolution != null) {            
            reader.setInitialSolution(model, initialSolution);            
            solver.setInitialSolution();
        }
        ArrayList<String> results = solver.solve();
        solver.close();
        ResultFileWriter writer = new ResultFileWriter(out.toString());
        writer.printResultFile(results);
	}

	private void doGibbsSampling(SyntaxReader reader)
			throws ParseException, IOException, SolveException, SQLException,
			ReadOrWriteToFileException {
		// start marginal query (GIBBS Sampling)
		System.out.println("-use GIBBS Sampling");
		if (Parameters.USE_CUTTING_PLANE_INFERENCE || Parameters.USE_CUTTING_PLANE_AGGREGATION) {
			Parameters.USE_CUTTING_PLANE_AGGREGATION = false;
			Parameters.USE_CUTTING_PLANE_INFERENCE = false;
			System.out.println("Set both CPI and CPA to false, since they both can not be used for sampling.");
		}
		Model model = reader.getModel(this.input.toString(), this.groundings.toString());
		StandardSolver solver = new StandardSolver(model);
		GIBBSSampler sampler = new GIBBSSampler();
		// ground MLN and retrieve Clauses
		System.out.println("------ find coherent world (MAP state) as starting point -------");
		ArrayList<String> consistentStartingPoints = solver.solve();
		ArrayList<Clause> clauses = solver.getAllClauses();
		Collection<Literal> evidence = solver.getEvidenceAxioms();
		solver = null; // free memory
		ArrayList<GIBBSLiteral> output = sampler.sample(iterations, clauses, evidence, consistentStartingPoints);
		ResultFileWriter writer = new ResultFileWriter(out.toString());
		if (iterations < 0) {
			iterations = Math.min(output.size() * 1000, 100000000);
		}
		writer.printResultFile(output, iterations);
	}

	private void doExactInference(SyntaxReader reader)
			throws ParseException, IOException, SolveException, SQLException,
			ReadOrWriteToFileException {
		// Parameters.DEBUG_OUTPUT = true;
		boolean useCpa = Parameters.USE_CUTTING_PLANE_AGGREGATION;
		//TODO add support for full aggregation
		boolean useCpi = Parameters.USE_CUTTING_PLANE_INFERENCE;
		if (Parameters.USE_CUTTING_PLANE_AGGREGATION || Parameters.USE_CUTTING_PLANE_INFERENCE) {
			System.err.println("Cutting plane aggregation and cutting plane inference must be disabled for exact inference.\n Disabling now, will reset to current values after exact inference is done.");
			Parameters.USE_CUTTING_PLANE_AGGREGATION = false;
			Parameters.USE_CUTTING_PLANE_INFERENCE = false;
		}
		System.out.println("-use exact inference");
		System.out.println("Cutting plane inference and cutting plane aggregation have been set to FALSE.");
		Model model = reader.getModel(this.input.toString(), this.groundings.toString());

		ExactSolver solver = new ExactSolver(model);
		Map<GroundingSet, Double> result = solver.solve();
		System.out.println("Normalization constant z = " + solver.getNormalizationConstant());
		// System.out.println("Internal result: " + result);

		if (Parameters.DEBUG_OUTPUT) {
			String exactDistribution = solver.getDistribution(result, true);
			String debugFile = (out.lastIndexOf('.') > out.lastIndexOf('/')) && (out.lastIndexOf('.') > out.lastIndexOf('\\')) ? out.substring(0, out.lastIndexOf('.')) + "_distribution.log" : out + "_distribution.log";
			FileWriter fw = new FileWriter(debugFile);
			fw.write(exactDistribution);
			fw.close();
		}

		System.out.println("Aggregating...");
		HashMap<String, Double> aggregatedResult = solver.aggregate(result);
		System.out.println("... done.");

		ResultFileWriter writer = new ResultFileWriter(out.toString());
		writer.printResultFile(aggregatedResult);

		Parameters.USE_CUTTING_PLANE_AGGREGATION = useCpa;
		Parameters.USE_CUTTING_PLANE_INFERENCE = useCpi;
	}

	private void doLearning(SyntaxReader reader) throws ParseException,
			IOException, RecognitionException, SQLException, SolveException,
			ReadOrWriteToFileException {
		// start learner (has to be tested!)
		System.out.println("-learn");

		Model model = reader.getModelForLearning(this.input.toString());
		// parse multiple groundings
		ArrayList<String> groundingList = new ArrayList<String>();
		for (String g : groundings.split(",")) {
			groundingList.add(g.trim());
		}
		if (iterations < 0) {
			System.out.println("-set iterations to 10");
			iterations = 10;
		}else{
			System.out.println("- iterations " + iterations);
		}
		
		VotedPerceptronLearner learn = new VotedPerceptronLearner(model);
		Model resultModel = learn.learn(groundingList, iterations);
		ModelFileWriter writer = new ModelFileWriter();
		writer.writeModel(resultModel, out.toString());
		if (Parameters.DEBUG_OUTPUT)
			System.out.println(resultModel);
	}

	
}
