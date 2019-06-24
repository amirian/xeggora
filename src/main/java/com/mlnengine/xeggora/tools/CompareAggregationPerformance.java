package com.mlnengine.xeggora.tools;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.googlecode.rockit.app.Parameters;

import gurobi.*;

public class CompareAggregationPerformance {
	static String folder = "TestAggregationPerformance/";
	static int datasize=100000;
	static int percent=10;
	static int numiter=5;
	int k=6;
	int len=7;
	int numberOfData = datasize * k * percent / 200;
	int cNumberOfData = (len - k) * percent / 200;
	int[][] xData;
	int[][] yData;
	int[][] cData;
	public CompareAggregationPerformance(int datasize, int k, int len, int percent) {
		this.datasize = datasize;//100000
		this.k = k;//6
		this.len = len;//7
		this.percent = percent;//10
		numberOfData = datasize * k * percent / 200;
		cNumberOfData = (len - k) * percent / 200;
		xData = new int[numiter][2 * numberOfData];
		yData = new int[numiter][2 * numberOfData];
		cData = new int[numiter][2 * cNumberOfData];
		for (int iter=0;iter<numiter;iter++) {
			int[] order = new int[datasize * k];
			for (int i = 0; i < order.length; i++)
				order[i] = i;
			for (int i = 0; i < 2 * numberOfData; i++) {
				int index = (int) (Math.random() * (datasize * k - i)) + i;
				int a = order[index];
				order[index] = order[i];
				yData[iter][i] = a % datasize;//(int) (Math.random() * datasize);
				xData[iter][i] = a / datasize;//(int) (Math.random() * k); 
				//if (yData[i]>=datasize)
					//System.err.println("**********************ERROR (y): " + yData[i] + " >= " + datasize);
				//if (xData[i]>=k)
					//System.err.println("**********************ERROR (x): " + xData[i] + " >= " + k);
			}
			for (int i = 0; i < 2 * cNumberOfData; i++)
				cData[iter][i] = (int) (Math.random() * (len - k));
		}
/*		int[][] m = new int[k][datasize];
		for (int i=0;i<xData.length;i++)
			m[xData[i]][yData[i]]++;
		for (int i=0;i<k;i++) {
			System.out.println();
			for (int j=0;j<datasize;j++)
				System.out.print(m[i][j]);
		}
		System.out.println("\n======================================================================================");
*/		
	}
	public long timeToMakeModel(int iter, boolean addRandomData, boolean positive, boolean perfect, String name) throws GRBException {
		GRBEnv    env   = new GRBEnv(folder + "lp.log");
		GRBModel  model = new GRBModel(env);
//		model.getEnv().set(GRB.IntParam.Symmetry, 0);model.update();//Disable symmetry detection
		
		GRBVar z = model.addVar(0, datasize, 0.0, GRB.CONTINUOUS/*INTEGER*/, "z");
		GRBVar[][] v=new GRBVar[datasize][k];
		GRBVar[] c=new GRBVar[len-k];
		GRBVar[] s=new GRBVar[datasize];
		String sn = positive?"L":"R";
		for (int j = 0; j < len - k; j++)
			c[j] = model.addVar(0, 1/*GRB.INFINITY*/, 0.0, GRB.CONTINUOUS/*BINARY*/, "c"+Integer.toString(j));
		for (int i = 0; i < datasize; i++) {
			if (k > 1 || !perfect)
				s[i] = model.addVar(0, 1/*GRB.INFINITY*/, 0.0, GRB.CONTINUOUS/*BINARY*/, sn+Integer.toString(i));
			for (int j = 0; j < k; j++)
				v[i][j] = model.addVar(0, 1/*GRB.INFINITY*/, 0.0, GRB.CONTINUOUS/*BINARY*/, "v"+Integer.toString(i)+"_"+Integer.toString(j));
		}

		GRBLinExpr obj = new GRBLinExpr();
		obj.addTerm(1, z);
		model.setObjective(obj, positive ? GRB.MAXIMIZE : GRB.MINIMIZE);

		GRBLinExpr expr;

		expr = new GRBLinExpr();
		for (int i = 0; i < datasize; i++)
			if (k > 1 || !perfect)
				expr.addTerm(1, s[i]);
			else if (k > 0 || !perfect)
				expr.addTerm(1, v[i][0]);
		if (positive) {
			if (perfect)
				for (int j = 0; j < len - k; j++) 
					expr.addTerm(datasize, c[j]);
			expr.addTerm(-1, z);
			model.addConstr(expr, GRB.GREATER_EQUAL, 0, "main");
		} else { 
			expr.addTerm(-1, z);
			model.addConstr(expr, GRB.LESS_EQUAL, 0, "main");
			if (perfect)
				for (int j = 0; j < len - k; j++) {
					GRBLinExpr expr2 = new GRBLinExpr();
					expr2.addTerm(datasize, c[j]);
					expr2.addTerm(-1, z);
					model.addConstr(expr2, GRB.LESS_EQUAL, 0, "C" + Integer.toString(j));
				}
		}

		for (int i = 0; i < datasize; i++) {
			expr = new GRBLinExpr();
			if (k > 1 || !perfect)
				expr.addTerm(positive ? -1 : perfect ? -k : -len, s[i]);
			else if (k > 0 || !perfect)
				expr.addTerm(positive ? -1 : perfect ? -k : -len, v[i][0]);
			for (int j = 0; j < k; j++)
				expr.addTerm(1, v[i][j]);
			if (!perfect) 
				for (int j = 0; j < len - k; j++) 
					expr.addTerm(1, c[j]);
			if (k > 1 || !perfect)
				model.addConstr(expr, positive ? GRB.GREATER_EQUAL : GRB.LESS_EQUAL, 0, "S" + Integer.toString(i));
		}
		if (addRandomData) {
			for (int i = 0; i < numberOfData; i++) {
				expr = new GRBLinExpr();
				expr.addTerm(1, v[yData[iter][i]][xData[iter][i]]); 
				model.addConstr(expr, GRB.GREATER_EQUAL, 1, "D" + Integer.toString(i));
			}
			for (int i = numberOfData; i < 2 * numberOfData; i++) {
				expr = new GRBLinExpr();
				expr.addTerm(1, v[yData[iter][i]][xData[iter][i]]); 
				model.addConstr(expr, GRB.LESS_EQUAL, 0, "D" + Integer.toString(i));
			}
			for (int i = 0; i < cNumberOfData; i++) {
				expr = new GRBLinExpr();
				expr.addTerm(1, c[cData[iter][i]]); 
				model.addConstr(expr, GRB.GREATER_EQUAL, 1, "D" + Integer.toString(i));
			}
			for (int i = cNumberOfData; i < 2 * cNumberOfData; i++) {
				expr = new GRBLinExpr();
				expr.addTerm(1, c[cData[iter][i]]); 
				model.addConstr(expr, GRB.LESS_EQUAL, 0, "D" + Integer.toString(i));
			}
			addRandomData = true;
		}
		
		model.update();
//		model.write(folder + name + "_" + datasize + "_" + len + "_" + k + "_" + (addRandomData ? percent:0) + ".lp");
		System.out.println("\n***************** " + name + "_" + datasize + "_" + len + "_" + k + "_" + (addRandomData ? percent:0) + " *****************\n");

		long start = System.currentTimeMillis();
		model.optimize();
		model.dispose();
		env.dispose();
		return System.currentTimeMillis() - start;
	}
	
	public static void main(String[] args) {
		//name + " took: " + tm + " milliseconds.\n";
		int maxlen = 12;
		long [][] PN = new long [maxlen][maxlen];
		long [][] PP = new long [maxlen][maxlen];
		long [][] NN = new long [maxlen][maxlen];
		long [][] NP = new long [maxlen][maxlen];
		long [][] PNE = new long [maxlen][maxlen];
		long [][] PPE = new long [maxlen][maxlen];
		long [][] NNE = new long [maxlen][maxlen];
		long [][] NPE = new long [maxlen][maxlen];

		StringBuilder result = new StringBuilder();
		StringBuilder resultPN = new StringBuilder();
		StringBuilder resultPP = new StringBuilder();
		StringBuilder resultNN = new StringBuilder();
		StringBuilder resultNP = new StringBuilder();
		StringBuilder resultPNE = new StringBuilder();
		StringBuilder resultPPE = new StringBuilder();
		StringBuilder resultNNE = new StringBuilder();
		StringBuilder resultNPE = new StringBuilder();
		try {
			for (int len = maxlen - 1; len > 0; len--) {
				for (int k = 0; k < len; k++) {
					CompareAggregationPerformance c = new CompareAggregationPerformance(datasize, k, len, percent);
					result.append(
							"Data Size: " + datasize + ", k: " + k + ", len: " + len +
							"\n---------------------------------------");
					long sum;

// Without Evidence Test:	    	
					result.append("\n Without Evidence:");
					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						 sum += c.timeToMakeModel(iter, false,  true, false, "PositiveNaive  ");
					PN[len][k] = sum / numiter;
					result.append("\nPositiveNaive  " + " took: " + PN[len][k] + " milliseconds.\n");
					resultPN.append(PN[len][k] + "\t");
					
					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						sum += c.timeToMakeModel(iter, false,  true,  true, "PositivePerfect");
					PP[len][k] = sum / numiter;
					result.append("\nPositivePerfect" + " took: " + PP[len][k] + " milliseconds.\n");
					resultPP.append(PP[len][k] + "\t");
					
					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						sum += c.timeToMakeModel(iter, false, false, false, "NegativeNaive  ");
					NN[len][k] = sum / numiter;
					result.append("\nNegativeNaive  " + " took: " + NN[len][k] + " milliseconds.\n");
					resultNN.append(NN[len][k] + "\t");
					
					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						sum += c.timeToMakeModel(iter, false, false,  true, "NegativePerfect");
					NP[len][k] = sum / numiter;
					result.append("\nNegativePerfect" + " took: " + NP[len][k] + " milliseconds.\n");
					resultNP.append(NP[len][k] + "\t");

// With percent% Evidence Test:	    	
					result.append(
							"\n With " + percent + "% Random Evidence:");

					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						sum += c.timeToMakeModel(iter, true,  true, false, "PositiveNaive  ");
					PNE[len][k] = sum / numiter;
					result.append("\nEvidential PositiveNaive  " + " took: " + PNE[len][k] + " milliseconds.\n");
					resultPNE.append(PNE[len][k] + "\t");
					
					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						sum += c.timeToMakeModel(iter, true,  true,  true, "PositivePerfect");
					PPE[len][k] = sum / numiter;
					result.append("\nEvidential PositivePerfect" + " took: " + PPE[len][k] + " milliseconds.\n");
					resultPPE.append(PPE[len][k] + "\t");
					
					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						sum += c.timeToMakeModel(iter, true, false, false, "NegativeNaive  ");
					NNE[len][k] = sum / numiter;
					result.append("\nEvidential NegativeNaive  " + " took: " + NNE[len][k] + " milliseconds.\n");
					resultNNE.append(NNE[len][k] + "\t");
					
					sum=0;
					for (int iter=0;iter<numiter;iter++ )
						sum += c.timeToMakeModel(iter, true, false,  true, "NegativePerfect");
					NPE[len][k] = sum / numiter;
					result.append("\nEvidential NegativePerfect" + " took: " + NPE[len][k] + " milliseconds.\n");
					resultNPE.append(NPE[len][k] + "\t");
					
					
					result.append("\n---------------------------------------\n");
				}
				resultPN.append("\n");
				resultPP.append("\n");
				resultNN.append("\n");
				resultNP.append("\n");
				resultPNE.append("\n");
				resultPPE.append("\n");
				resultNNE.append("\n");
				resultNPE.append("\n");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}
    	PrintWriter writer;
		try {
			writer = new PrintWriter(folder + "result"+percent+".txt", "UTF-8");
    		writer.print(result);
	    	writer.close();    	

//Without Evidence Test Output:
	    	writer = new PrintWriter(folder + "resultPN.txt", "UTF-8");
    		writer.print(resultPN);
	    	writer.close();    	

	    	writer = new PrintWriter(folder + "resultPP.txt", "UTF-8");
    		writer.print(resultPP);
	    	writer.close();    	

	    	writer = new PrintWriter(folder + "resultNN.txt", "UTF-8");
    		writer.print(resultNN);
	    	writer.close();    	

	    	writer = new PrintWriter(folder + "resultNP.txt", "UTF-8");
    		writer.print(resultNP);
	    	writer.close();    	

// With percent% Evidence Test Output:	    	
	    	writer = new PrintWriter(folder + "resultPNE"+percent+".txt", "UTF-8");
    		writer.print(resultPNE);
	    	writer.close();    	

	    	writer = new PrintWriter(folder + "resultPPE"+percent+".txt", "UTF-8");
    		writer.print(resultPPE);
	    	writer.close();    	

	    	writer = new PrintWriter(folder + "resultNNE"+percent+".txt", "UTF-8");
    		writer.print(resultNNE);
	    	writer.close();    	

	    	writer = new PrintWriter(folder + "resultNPE"+percent+".txt", "UTF-8");
    		writer.print(resultNPE);
	    	writer.close();    	
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MLNTools.tone(1000,500);  
	}
}

