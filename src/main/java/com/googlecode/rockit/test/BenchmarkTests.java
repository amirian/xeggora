package com.googlecode.rockit.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.StandardSolver;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.file.MyFileWriter;

public class BenchmarkTests {
	
	
	public static void main(String[] args) throws ReadOrWriteToFileException, ParseException, IOException, RecognitionException, SolveException, SQLException {
		/*
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		// Has to be disabled for runtime comparisons!!!
		Parameters.DEBUG_OUTPUT=false;
    	int cores = Runtime.getRuntime().availableProcessors();
    	//Parameters.THREAD_NUMBER=cores+cores;
		/* pr:0.0000001 		
		 * lp:0.01
		 * rc:0.000001
		 * ie:0.000001
		 * er:0.000001
		 * ELOG max:0.001
		 * conference:-1
		 */
		String folder =
				//"numeric/temporal/";
				//"alignment/";
				//"main/artificial/";
				//"CASAS/";
				//"TexasHoldemMLN/3/";
				//"confidence/1stOrder/";
				//"MLOCSR/";
				"pr/modified/complete/";
				//"ELOGatIJCAJ2011/ekaw_mechanical_turk_";
				//"main/backup/";
				//"testOrder2/";
				//"CODIatOAEI2011/anatomy/mouse.owl-human.owl_";
				//"main/codi/";
				//"others/uw-cse/";
				//"CODIatOAEI2011/conference";
				//"CODIatOAEI2011/conferenceAll/conference_joined_";
				//"ontologymatching/census/";

		Parameters.THREAD_NUMBER=Parameters.DEBUG_OUTPUT ? 1:cores;
		int numiter = 1;
		Parameters.AGGREGATION_ORDER=-1;
		Parameters.USE_CUTTING_PLANE_INFERENCE=true;
		Parameters.USE_CUTTING_PLANE_AGGREGATION=Parameters.AGGREGATION_ORDER != 0;

		//  	System.out.println("Aggregation Order: " + Parameters.AGGREGATION_ORDER);
		System.out.println("Parameters.THREAD_NUMBER " + Parameters.THREAD_NUMBER);
    	
		
		ArrayList<String> results = new ArrayList<String>();
		MyFileWriter writer = new MyFileWriter("TUFFYatVLDB2011Results.txt");
		String s=null;
	
		String modelFile = "data/"+folder+"prog.mln";
		String evidenceFile = "data/"+folder+"evidence.db";
//		String queryFile = "data/"+folder+"query.db";
		
//		String modelFile = "data/RCA/default.mln";
//		String evidenceFile = "data/RCA/output_1000_1_1.db";

//		String modelFile = "data/all/main2/ag.mln";
//		String evidenceFile = "data/all/main2/evidence.db";

/*		String[] evidenceFiles = {"test-adult-income-12att-50.db",
        "test-adult-income-12att-60.db",
        "test-adult-income-12att-70.db",
        "test-adult-income-12att-80.db",
        "test-adult-income-12att-90.db",
        "test-adult-income--50.db",
        "test-adult-raw-50.db",
        "test-adult-raw-60.db",
        "test-adult-raw-70.db",
        "test-adult-raw-80.db",
        "test-adult-raw-90.db",
        "test-income-12att-raw-50.db",
        "test-income-12att-raw-60.db",
        "test-income-12att-raw-70.db",
        "test-income-12att-raw-80.db",
        "test-income-12att-raw-90.db"};
*/
		long totalruntime = 0;
		long totalILPTime = 0;
		long sumruntime = 0;
		try{ 
			totalruntime = System.currentTimeMillis();
			for (int i = 0; i < numiter; i++) {
				System.out.println("\n*** ITERATION " + (i + 1) + " ***");
				StandardSolver.ilpTotalTime = 0;
				s=StandardSolverTest.test(modelFile, evidenceFile, 0.000001, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
				totalILPTime += StandardSolver.ilpTotalTime;
				results.add(s);writer.writeln(s);writer.flush();
				long runtime = StandardSolverTest.runtime;
				results.add("duration:"+runtime);
				sumruntime += runtime;
				System.out.println("Iteration finished. Run Time: " + runtime + ", ILP Time: " + StandardSolver.ilpTotalTime);
			}
			totalruntime = System.currentTimeMillis() - totalruntime;
		} catch (Exception e) {
			s = modelFile+";"+evidenceFile+";Exception"+e.getMessage();// TODO Auto-generated catch block
			results.add(s);writer.writeln(s);writer.flush();
	}
/*	
		
		// conference
		// CONFERENCE		
		String[] onts = new String[]{
"cmt", "conference", "confOf", "edas", "ekaw", "iasted", "sigkdd"
//						"cmt", "confOf", "iasted"
						};
		for(int i = 0; i<onts.length; i++){
					
					for(int j = i+1; j<onts.length;j++){
					//String modelFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_prog.mln";
					//String evidenceFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_evidence.db";
					modelFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_prog.mln";
					evidenceFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_evidence.db";
					
//					queryFile = "data/CODIatOAEI2011/query.db";
				
					try {
						r = System.currentTimeMillis();
						s = StandardSolverTest.test(modelFile, evidenceFile, -1, true, true);
						r = System.currentTimeMillis() - r;
						results.add(s);writer.writeln(s);writer.flush();
						} catch (Exception e) {
							s = modelFile+";"+evidenceFile+";Exception;"+e.getMessage();// TODO Auto-generated catch block
							results.add(s);writer.writeln(s);writer.flush();
						}catch (Error e){
							s = modelFile+";"+evidenceFile+";Error;"+e.getMessage();// TODO Auto-generated catch block
							results.add(s);writer.writeln(s);writer.flush();
						}
					}
					runtime = StandardSolverTest.runtime + runtime;
					System.out.println("Total Runtime of" + modelFile + ": " + r);
				}
*/
		
		
		System.out.println("#iterations: " + numiter);
		System.out.println("Average Runtime: " + sumruntime / numiter);
        System.out.println("Average ILP Time: " + totalILPTime / numiter);

		writer.closeFile();
		// print results
		System.out.println("==========================");
		System.out.println();
		for(String res : results){
			System.out.println(res);
		}
	}
}
