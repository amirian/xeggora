package com.mlnengine.xeggora.tools;
/* Copyright 2015, Gurobi Optimization, Inc. */

/* This example formulates and solves the following simple QCP model:

     maximize    x
     subject to  x + y + z = 1
                 x^2 + y^2 <= z^2 (second-order cone)
                 x^2 <= yz        (rotated second-order cone)
*/

import gurobi.*;

public class IQCP {
  public static void main(String[] args) {
    try {
      GRBEnv    env   = new GRBEnv("qcp.log");
      GRBModel  model = new GRBModel(env);

      // Create variables
      int n=10;
      GRBVar z = model.addVar(0,GRB.INFINITY/*n*/,0.0/*2.3*/, GRB.INTEGER, "z");
      GRBVar fac = model.addVar(0, GRB.INFINITY, 0.0, GRB.BINARY, "fac");
      GRBVar[] fbc=new GRBVar[n];
      GRBVar[] cbc=new GRBVar[n];
/*me*/	  GRBVar[] mbc=new GRBVar[n];

      for (int i=0;i<n;i++){
    	  fbc[i] = model.addVar(0, GRB.INFINITY, 0.0, GRB.BINARY, "fb"+Integer.toString(i)+"c");
    	  cbc[i] = model.addVar(0, GRB.INFINITY, 0.0, GRB.BINARY, "cb"+Integer.toString(i)+"c");
/*me*/    	  mbc[i] = model.addVar(0, GRB.INFINITY, 0.0, GRB.BINARY, "mb"+Integer.toString(i)+"c");
      }

      // Integrate new variables

      model.update();

      // Set objective

      GRBLinExpr obj = new GRBLinExpr();
      obj.addTerm(2.3, z);
      //obj.addTerm(n/2, fac);// added for complexity
      model.setObjective(obj, GRB.MAXIMIZE);

      // Add linear constraint

      GRBLinExpr expr = new GRBLinExpr();
      expr.addTerm(1, z); 
      model.addConstr(expr, GRB.LESS_EQUAL, n, "c0");

      // Add second-order cone
      for (int i=0;i<n;i++){
    	  if (i%2==1){
        	  expr = new GRBLinExpr();
    		  expr.addTerm(1, cbc[i]);
    		  model.addConstr(expr, GRB.GREATER_EQUAL, 1, "c_cb"+Integer.toString(i));
    	  }
    	  if ((i/2)%2==1){
        	  expr = new GRBLinExpr();
    		  expr.addTerm(1, fbc[i]);
    		  model.addConstr(expr, GRB.GREATER_EQUAL, 1, "c_fb"+Integer.toString(i));
    	  }
      }

//me      GRBQuadExpr qexpr = new GRBQuadExpr();
      GRBLinExpr qexpr = new GRBLinExpr();
      
      for (int i=0; i<n; i++)
//me   	  qexpr.addTerm(1.0, fbc[i], cbc[i]);
      {
    	  qexpr.addTerm(1.0, mbc[i]);
    	  expr = new GRBLinExpr();
    	  expr.addTerm(-1,mbc[i]);
    	  expr.addTerm(1,fbc[i]);
    	  expr.addTerm(1,cbc[i]);
		  model.addConstr(expr, GRB.LESS_EQUAL, 1, "l_mb"+Integer.toString(i));
    	  expr = new GRBLinExpr();
    	  expr.addTerm(2,mbc[i]);
    	  expr.addTerm(-1,fbc[i]);
    	  expr.addTerm(-1,cbc[i]);
		  model.addConstr(expr, GRB.LESS_EQUAL, 0, "r_mb"+Integer.toString(i));
            
      }
      qexpr.addTerm(1.0, z);
      qexpr.addTerm(n, fac);
      model.addConstr(qexpr, GRB.LESS_EQUAL, 2*n, "qc0");//me addQConstr

		model.update();
		model.write("qcp2.lp");
		model.write("qcp2.mps");

		// Optimize model

      model.optimize();

      System.out.println(z.get(GRB.StringAttr.VarName)
                         + " " +z.get(GRB.DoubleAttr.X));
      System.out.println(fac.get(GRB.StringAttr.VarName)
              + " " +fac.get(GRB.DoubleAttr.X));

      for (int i=0; i<n; i++) {
    	  System.out.println(fbc[i].get(GRB.StringAttr.VarName)
                  + " " +fbc[i].get(GRB.DoubleAttr.X));
          System.out.println(cbc[i].get(GRB.StringAttr.VarName)
                  + " " +cbc[i].get(GRB.DoubleAttr.X));
      }

      System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal) + " " +
                         obj.getValue());
      System.out.println();

      // Dispose of model and environment

      model.dispose();
      env.dispose();

    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " +
          e.getMessage());
    }
  }
}
