package com.googlecode.rockit.conn.ilp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPVariable.ILPVarCategory;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;
import com.mlnengine.xeggora.app.solver.aggregate.DoubleLiteral;

import gurobi.GRBException;

public abstract class ILPConnector {

	
	// helper for adding multible constraints
	int rhs = 0;
	
	int zVarIndex =0;

	int numberOfMixedVariables = 0;
	
	int numberOfBinaryVariables = 0;
	// Aggregation Manager
	//TODO for tests two activated
	//AggregationManager aggregationManager = null;
	
	public ILPConnector() throws ILPException 
	{
	  this.initialize();
	}	
	
	/**
	 * Adds binary restriction in which every variable has to be true.
	 * 
	 * It builds restrictions like
	 * 
	 * (1-x1) + (1-x2) + x3 + x4 + x5 <= 1
	 * 
	 * Thus, x1 and x2 must be negative in the ilp while
	 * x3,x4 and x5 must be positive (+0) in the ilp.
	 * 
	 * If we have just one axiom in the list, the evidence is directly encoded by restricting the variable to 
	 * - lb = 0 and ub = 0 (negated) or
	 * - lb = 1 and ub = 1 (positive).
	 * 
	 * @param axioms Array of components (including name and positive and negated information)
	 * @throws ParseException When adding a restriction, there has to be as many variable names as entries in the hasToBePositive Array.
	 * @throws SolveException 
	 */
	public void addHardConstraint(ArrayList<Literal> axioms) throws SolveException{
		this.rhs = 1;
		this.addConstraint(this.getLHSExpression(axioms),ILPOperator.GEQ,this.rhs);
	}
	
	/**
	 * Generates the left hand side expression.
	 * 
	 * For e.g.
	 * a v ! b v c
	 * it creates
	 * {+a, -b, +c}.
	 * 
	 * Furthermore, it changes rhs as followed:
	 * - if sumPositive then it adds 1 for every positive axiom (in our example: rhs = rhs + 2)
	 * - if !sumPositive then it substracts 1 for every negated axiom (in our example: rhs = rhs - 1)
	 * 
	 * @param variableNames
	 * @param mustBePositive
	 * @param expr
	 * @return
	 * @throws SolveException
	 */
	private ArrayList<ILPVariable> getLHSExpression(ArrayList<Literal> axioms) throws SolveException{
		  ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
		  for(Literal axiom : axioms){
			  if(axiom.isPositive()){
				  // less equal value does not change
				  expr.add(new ILPVariable(axiom.getName(), +1, ILPVarCategory.NORMAL)); 
			  } else {
				  this.rhs = rhs - 1;
				  expr.add(new ILPVariable(axiom.getName(), -1, ILPVarCategory.NORMAL)); 
			  }
		  }
		  // assign the private variable greaterEqualValue the correct weight		  
		  return expr;
	}
	
	/**
	 * Adds binary restrictions for soft constraints.
	 * 
	 * @param weight value of the target function weight of the soft formular
	 * @param variableNames names of the variables
	 * @param mustBePositive Array with values if the respective variable (same position in array) has to be positive or negative
	 * @param conjunction if true, the whole formula is added as conjunction, if false it is added as disjunction.
	 * @param dublicateDetection this string detects if a similar formula has already been added. If no, the formula is added. If yes, the weight of the formula is increased (but no new z variable is generated).
	 * @throws ParseException When adding a restriction, there has to be as many variable names as entries in the hasToBePositive Array.
	 * @throws SolveException 
	 */
	public void addSingleSoftConstraint(double weight, ArrayList<Literal> restriction, boolean conjunction) throws SolveException{
		String z = this.getNextZ(weight);
		// disjunction: of course if it is no conjunction AND if the variable length is less than 1.
		if(!conjunction || (restriction!=null && restriction.size()<=1)){
			
			if(weight>0 ){				
				addSoftConstraintDisjunctionPositive(weight, restriction, z);
				if(Parameters.USE_SAMPLING){
					this.addSoftConstraintDisjunctionNegative(weight, restriction,z);
				}
			}else{
				addSoftConstraintDisjunctionNegative(weight, restriction,z);
				if(Parameters.USE_SAMPLING){
					this.addSoftConstraintDisjunctionPositive(weight, restriction,z);
				}
			}
		}else{
			// conjunction
			if(weight>0 ){
				addSoftConstraintConjunctionPositive(weight, restriction, z);
			}else{
				addSingleSoftConstraintConjunctionNegative(weight, restriction, z);
			}
		}  
	}

	
	/**
	 * Adds soft constraints for Disjunction (or) and a negative weight. Internally, the function also assigns 
	 * the value for the private greaterEqualValue variable.
	 * 
	 * Further, it creates the necessary unique "zzz" variables with the help of the zVarIndex.
	 * 
	 * Assume for example the following formula:
	 * a(B,C) or b(A,B) or ! c(A,C)    - 0.5
	 * 
	 * Then, the target function is extended with:
	 * - 0.5 * z_i
	 * 
	 * Further, the following restriction is added:
	 * + a(B,C) + b(A,B) + (1-c(A,C)) <= 3*z_i
	 * 
	 * In general, for every positive literal C+ and for every negative literal C- the following restriction is added.
	 * 
	 *     (sum_C+ y_i) + (sum_C- (1-y_i)) <= (sum_C 1) z
	 * <-> (sum_C+ y_i) - (sum_C- y_i) - (sum_C 1) z <= - (sum_C- 1)
	 * 
	 *  
	 * @param weight
	 * @param variableNames
	 * @param mustBePositive
	 * @return
	 * @throws SolveException
	 */
	private void addSoftConstraintDisjunctionNegative(double weight, ArrayList<Literal> restriction, String z) throws SolveException{
		// accesses the private helper variable greaterEqualValue.
		this.rhs = 0;
		ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
		int sumC = restriction.size();
		lhs.add(new ILPVariable(z, (- sumC), ILPVarCategory.Z));
		this.addConstraint(lhs, ILPOperator.LEQ, this.rhs);

	}

	
	
	/**
	 * Adds soft constraints for Disjunction (or) and a positive weight. Internally, the function also assigns 
	 * the value for the private greaterEqualValue variable.
	 * 
	 * Further, it creates the necessary unique "zzz" variables with the help of the zVarIndex.
	 *  
	 * Thereby, a new variable "zzz" is added to the target function multiplied with the weight:
	 * 
	 * max ... + weight * z + ...
	 * 
	 * Further, for every positive literal C+ and for every negative literal C- the following restriction is added.
	 * 
	 *     (sum_C+ y_i) + (sum_C- (1-y_i)) >= z
	 * <-> (sum_C+ y_i) - (sum_C- y_i) - z >= - (sum_C- 1)
	 * 
	 * This restriction ensures that z is true if one positive literal is true or one negative literal is false.
	 *  
	 * @param weight
	 * @param variableNames
	 * @param mustBePositive
	 * @return
	 * @throws SolveException
	 */
	private void addSoftConstraintDisjunctionPositive(double weight, ArrayList<Literal> restriction,String z) throws SolveException{
		this.rhs = 0;
		ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
		lhs.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		this.addConstraint(lhs, ILPOperator.GEQ, this.rhs);  
	}

	/**
	 * Adds soft constraints for Conjunction (and) and a positive weight. Internally, the function also assigns 
	 * the value for the private greaterEqualValue variable.
	 * 
	 * Further, it creates the necessary unique "z" variables with the help of the zVarIndex.
	 *  
	 * Thereby, a new variable "z" is added to the target function multiplied with the weight:
	 * 
	 * max ... + weight * z + ...
	 * 
	 * Further, for every positive literal C+ and for every negative literal C- the following restriction is added.
	 * 
	 * (sum_C+ y_i) - (sum_C- (1-y_i)) >= ((sum_C+ 1) + (sum_C- 1)) * z <=>
	 * (sum_C+ y_i) - (sum_C- y_i) - ((sum_C+ 1) + (sum_C- 1)) * z >= - (sum_C- 1)
	 * 
	 * This restriction ensures that z is true if one positive literal is true or one negative literal is false.
	 *  
	 * @param weight
	 * @param variableNames
	 * @param mustBePositive
	 * @return
	 * @throws SolveException
	 */
	private void addSoftConstraintConjunctionPositive(double weight, ArrayList<Literal> restriction, String z) throws SolveException{
		
		// accesses the private helper variable greaterEqualValue.
		this.rhs = 0;
		ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
		lhs.add(new ILPVariable(z, ((-1)*restriction.size()), ILPVarCategory.Z));
		this.addConstraint(lhs, ILPOperator.GEQ, this.rhs);  
		
	}
	
	/**
	 * Adds soft constraints for Conjunction (and) and a negative weight. Internally, the function also assigns 
	 * the value for the private greaterEqualValue variable.
	 * 
	 * Further, it creates the necessary unique "zzz" variables with the help of the zVarIndex.
	 *  
	 * Thereby, a new variable "zzz" is added to the target function multiplied with the weight:
	 * 
	 * max ... + weight * z_i + ...
	 * 
	 * Further, for every positive literal C+ and for every negative literal C- the following restriction is added.
	 * 
	 *     (sum_C+ y_i) - (sum_C- y_i) <= z + (sum_C+ 1) + (sum_C- 1) - 1
	 * <=> (sum_C+ y_i) - (sum_C- y_i) - z <= (sum_C+ 1) - 1
	 * 
	 * This restriction ensures that z is true if one positive literal is true or one negative literal is false.
	 *  
	 * Example: a n b n \neg c     - 0.5
	 * leads to: a + b + (1-c) <= z + 2
	 *  
	 * @param weight
	 * @param variableNames
	 * @param mustBePositive
	 * @return
	 * @throws SolveException
	 */
	private void addSingleSoftConstraintConjunctionNegative(double weight, ArrayList<Literal> restriction, String z) throws SolveException{		
		this.rhs = 0;
		ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
		lhs.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		// (sum_C+ 1) + (sum_C- 1) - (sum_C- 1) - 1 = (sum_C+ 1) - 1
		this.rhs = restriction.size() - this.rhs - 1;
		
		this.addConstraint(lhs, ILPOperator.LEQ, rhs);
	}
	
	public void addAggregatedConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars, boolean isConjunction) throws ILPException{
		String z = isHard? null:this.getNextZ(weight, singleVars.size());
		if(isConjunction)
			throw new ILPException("Can not aggregate conjunction formulas");
		// only do something if at least one single variable is present
		//0 <= z <= k
		if(singleVars.size()>0){
			if(isHard || weight>0){//Me TODO think of negative hard formula
				this.addAggregatedPositiveDisjunctionConstraint(isHard, weight, aggregatedVars, singleVars, z);
			}else{
				this.addAggregatedNegativeDisjunctionConstraint(isHard, weight, aggregatedVars, singleVars, z);
			}
		}
	}
	
	/**
	 * w negative and disjunction:
	 * 
	 * sum k * aggregatedVars_positive - sum k * aggregatedVars_negative - z <= - (Sum_{negative aggregatedVars} 1) * k
	 * 
	 * Me IMPORTANT: Jan's method IS OBVIOUSLY WRONG, Should be separate for each aggregated var, OR z var shouldn't be limited to n
	 * 
     * Me Should be for each aggregatedVars: k * aggregatedVar_positive - z <= 0 & -k * aggregatedVar_negative - z <= - k  		
	 * 
	 * (-1)_{if single var negative} sum singleVars - z <= - (number of negative single vars)
	 * optional: 0 <= z <= k
	 * 
	 * @param model
	 * @return
	 * @throws ILPException 
	 */
	private void addAggregatedNegativeDisjunctionConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars, String z) throws ILPException {
		int k = singleVars.size();
/*Me IMPORTANT: Jan's method IS OBVIOUSLY WRONG, Should be separate for each aggregated var, OR z var shouldn't be limited to n
		//sum k * aggregatedVars_positive - sum k * aggregatedVars_negative - z <= (Sum_{negative aggregatedVars} 1) * k

 		if(aggregatedVars.size()>0){
 
			ArrayList<ILPVariable> expr1 = new ArrayList<ILPVariable>();
			
			for(Literal axiom : aggregatedVars){
				if(axiom.isPositive()){
					//sum k * aggregatedVars_positive
					expr1.add(new ILPVariable(axiom.getName(), k, false));
				}else{
					//- sum k * aggregatedVars_negative
					expr1.add(new ILPVariable(axiom.getName(), - k, false));
				}
			}
			// - z
			expr1.add(new ILPVariable(z, -1, true));
			//for(int i =0; i<k; i++){
			//	expr1.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
			//}
			
			//<=
			ILPOperator lessEqual = ILPOperator.LEQ;
			

			//- (Sum_{negative aggregatedVars} 1) * k
			double rhs = 0;
			for(Literal negAggVar : aggregatedVars){
				if(!negAggVar.isPositive()){
					rhs=rhs-k;
				}
			}
			this.addConstraint(expr1, lessEqual, rhs);
		}
*/
//Me{ Revised and Resolved: Should be for each aggregatedVars: k * aggregatedVar_positive - z <= 0 & -k * aggregatedVar_negative - z <= - k  		
		
		for(Literal axiom : aggregatedVars){
			ArrayList<ILPVariable> expr1 = new ArrayList<ILPVariable>();
			if(axiom.isPositive())
				//k * aggregatedVar_positive
				expr1.add(new ILPVariable(axiom.getName(), k, ILPVarCategory.NORMAL));
			else
				//- k * aggregatedVars_negative
				expr1.add(new ILPVariable(axiom.getName(), - k, ILPVarCategory.NORMAL));
	 		if (isHard)
	 			//Never reach this place! TODO Throw Exception or think of negative hard formula
	 			System.out.println("ERROR! no Hard Formula with negative weight is permitted");
	 		else
	 			// - z
	 			expr1.add(new ILPVariable(z, -1, ILPVarCategory.Z));
			double rhs = 0;
			if(!axiom.isPositive())
				rhs=rhs-k;
			ILPOperator lessEqual = ILPOperator.LEQ;
			this.addConstraint(expr1, lessEqual, rhs);
		}
		//for(int i =0; i<k; i++){
		//	expr1.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
		//}
//}Me 			
		// (-1)_{if single var negative} sum singleVars - z <= (0 if singleVars positive, k if singleVars negative)
		ArrayList<ILPVariable> expr2 = new ArrayList<ILPVariable>();

		//(-1)_{if single var negative} sum singleVars
		for(Literal singleVar : singleVars){
			double one_or_minus_one = -1;
			if (singleVar.isPositive()) one_or_minus_one = +1; 
			expr2.add(new ILPVariable(singleVar.getName(), one_or_minus_one, ILPVarCategory.NORMAL));
		}
 		if (isHard)
 			//Never reach this place! TODO Throw Exception or think of negative hard formula
 			System.out.println("ERROR! no Hard Formula with negative weight is permitted");
 		else
 			// - z
 			expr2.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		/*for(int i =0; i<k; i++){
			expr2.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
		}*/
		
		//<=
		ILPOperator lessEqual = ILPOperator.LEQ;
				
		// - (number of negative singleVars)
		double rhs = 0;
		for(Literal singleVar : singleVars){
			if(!singleVar.isPositive()){
				rhs = rhs-1;
			}
		}
		this.addConstraint(expr2, lessEqual, rhs);
	}
	
	
	/**
	 * w positive and disjunction:
	 * sum k * aggregatedVars_positive - sum k * aggregatedVars_negative + (-1)_{if single var negative} sum singleVars - z >= - (Sum_{negative aggVariables} 1) * k - (sum_{negative singleVars} 1)
	 * 0 <= z <= k
	 * 
	 * @param model
	 * @return
	 * @throws GRBException
	 */
	private void addAggregatedPositiveDisjunctionConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars, String z) throws ILPException{
		int k = singleVars.size();
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
		
		for(Literal aggAxiom : aggregatedVars){
			if(aggAxiom.isPositive()){
				//sum k * aggregatedVars_positive
				expr.add(new ILPVariable(aggAxiom.getName(),k, ILPVarCategory.NORMAL));
			}else{
				//- sum k * aggregatedVars_negative
				expr.add(new ILPVariable(aggAxiom.getName(),- k, ILPVarCategory.NORMAL));
			}
		}

		//(-1)_{if single var negative} sum singleVars
		
		for(Literal singleVar : singleVars){
			double one_or_minus_one = -1;
			if(singleVar.isPositive()) one_or_minus_one = 1;
			expr.add(new ILPVariable(singleVar.getName(),one_or_minus_one, ILPVarCategory.NORMAL));
		}
		//>=
		ILPOperator greaterEqual = ILPOperator.GEQ;
				
		// - (Sum_{negative aggVariables} 1) * k - (sum_{negative singleVars} 1)
		double rhs = 0;
		for(Literal aggAxiom : aggregatedVars){
			if(!aggAxiom.isPositive()){
				rhs=rhs-k;
			}
		}
		for(Literal singAxiom: singleVars){
			if(!singAxiom.isPositive()){
				rhs = rhs-1;
			}
		}
		
 		if (isHard)
 			rhs = rhs + k;
 		else
 			// - z
 			expr.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		/*for(int i =0; i<k; i++){
			expr.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
		}*/
		
		this.addConstraint(expr, greaterEqual, rhs);
		
	}
	
//Me{
	public void addFullAggregatedConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars,
			ArrayList<ArrayList<Literal>> aggregatingList, boolean isConjunction) throws ILPException{
		String z = isHard? null:this.getNextZ(weight, aggregatingList.size());
		if (Parameters.DEBUG_OUTPUT)
			System.out.println("Adding to ILP: "+aggregatedVars+"\nAggregating:\n"+aggregatingList);
		if(isConjunction)
			throw new ILPException("Can not aggregate conjunction formulas");
		// only do something if at least one single variable is present
		//0 <= z <= k
//Me TODO what is this??
		if(aggregatingList.size()>0){
			if(isHard || weight>0){//Me TODO think of negative hard formula
				this.addFullAggregatedPositiveDisjunctionConstraint(isHard, weight, aggregatedVars, aggregatingList, z);
			}else{
				this.addFullAggregatedNegativeDisjunctionConstraint(isHard, weight, aggregatedVars, aggregatingList, z);
			}
		}
	}
	
	/**
	 * Multiple w negative and disjunction:
	 * 
	 * for each aggregatedVar: k * aggregatedVar_positive [1 - aggregatedVar_negative] <= z
	 * 
     * i aggregatingVars: p, q,...: multiple = p_v_q_v_...: optional(left hand): multiple <= p + q + ... <= i * multiple  		
	 * 
	 * sum of multipleAuxAggregatingVars <= z, multipleAuxAggregatingVar is always positive
	 * 
	 * optional: z <= k
	 * 
	 * @param model
	 * @return
	 * @throws ILPException 
	 */
	private void addFullAggregatedNegativeDisjunctionConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<ArrayList<Literal>> aggregatingList, String z) throws ILPException {
		int k = aggregatingList.size();
		// for each aggregatedVar: k * aggregatedVar_positive [1 - aggregatedVar_negative] <= z
		for(Literal aggAxiom : aggregatedVars){
			ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
			double rhs = 0d;
			if(aggAxiom.isPositive())
				// k * aggregatedVar_positive
				expr.add(new ILPVariable(aggAxiom.getName(),k, ILPVarCategory.NORMAL));
			else {
				// k - k * aggregatedVar_negative
				expr.add(new ILPVariable(aggAxiom.getName(),- k, ILPVarCategory.NORMAL));
				rhs = rhs - k;
			}
	 		if (isHard)
	 			//Never reach this place! TODO Throw Exception or think of negative hard formula
	 			throw new ILPException("ERROR! no Hard Formula with negative weight is permitted");
	 		expr.add(new ILPVariable(z, -1, ILPVarCategory.Z));
			this.addConstraint(expr, ILPOperator.LEQ, rhs);
		}

		rhs = 0;
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
 		for(ArrayList<Literal> aggregatingVar : aggregatingList)
 			if (aggregatingVar.size() > 1) {//except for first-order aggregation
	 			String name = addMultipleAuxiliaryIfNew(aggregatingVar);
	 			// sum of multipleAuxAggregatingVars <= z, multipleAuxAggregatingVar is always positive
				expr.add(new ILPVariable(name, 1.0, ILPVarCategory.MIXED));
			    // i aggregatingVars: p, q,...: multiple = p_v_q_v_...: optional(left hand): multiple <= p + q + ... <= i * multiple  		
	//				addBoundMultipleConstraint(aggregatingVar, true);
				addBoundMultipleConstraint(name, aggregatingVar, false);
			} else if (aggregatingVar.size() == 1) {//first-order aggregation
				expr.add(new ILPVariable(aggregatingVar.get(0).getName(), aggregatingVar.get(0).isPositive() ? 1d : -1d, ILPVarCategory.NORMAL));
				if(!aggregatingVar.get(0).isPositive())
					rhs = rhs-1;
			}
 		if (isHard)
 			//Never reach this place! TODO Throw Exception or think of negative hard formula
 			throw new ILPException("ERROR! no Hard Formula with negative weight is permitted");
		expr.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		this.addConstraint(expr, ILPOperator.LEQ, rhs);
		
	}
	
	/**
	 * Multiple w positive and disjunction:
	 * 
     * i aggregatingVars: p, q,...: multiple = p_v_q_v_...: multiple <= p + q +... <= i * multiple :optional (right hand)  		
	 * 
	 * sum k * aggregatedVars_positive [1 - aggregatedVar_negative] + (-1)_{if multipleAggregatingVar negative} + sum of multipleAggregatingVars >= z 
	 * 
	 * 0 <= z <= k
	 * 
	 * @param model
	 * @return
	 * @throws GRBException
	 */
	private void addFullAggregatedPositiveDisjunctionConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<ArrayList<Literal>> aggregatingList, String z) throws ILPException{
		int k = aggregatingList.size();
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
		
		double rhs = 0;

		for(Literal aggAxiom : aggregatedVars){
			if(aggAxiom.isPositive()){
				//sum k * aggregatedVars_positive
				expr.add(new ILPVariable(aggAxiom.getName(),k, ILPVarCategory.NORMAL));
			}else{
				//sum k - k * aggregatedVars_negative
				expr.add(new ILPVariable(aggAxiom.getName(),- k, ILPVarCategory.NORMAL));
				rhs = rhs - k;
			}
		}

 		for(ArrayList<Literal> aggregatingVar : aggregatingList)
 			if (aggregatingVar.size() > 1) {//except for first-order aggregation
	 			String name = addMultipleAuxiliaryIfNew(aggregatingVar);
	 			// multipleAggregatingVar is always positive
				expr.add(new ILPVariable(name, 1d, ILPVarCategory.MIXED));
			    // i aggregatingVars: p, q,...: multiple = p_v_q_v_...: multiple <= p + q +... <= i * multiple :optional (right hand)  		
				addBoundMultipleConstraint(name, aggregatingVar, true);
	//				addBoundMultipleConstraint(name, aggregatingVar, false);
			} else if (aggregatingVar.size() == 1) {//first-order aggregation
				expr.add(new ILPVariable(aggregatingVar.get(0).getName(), aggregatingVar.get(0).isPositive() ? 1d : -1d, ILPVarCategory.NORMAL));
				if(!aggregatingVar.get(0).isPositive())
					rhs = rhs-1;
			}
 		if (isHard)//replace z variable with k
 			rhs = rhs + k;
 		else
 			expr.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		this.addConstraint(expr, ILPOperator.GEQ, rhs);
		
	}
	
	private void addBoundMultipleConstraint(String multipleVarName, ArrayList<Literal> aggregatingVar, boolean isUpperbound) throws ILPException{
//It doesn't work! deleted.			if (mixedSingleVar.hadSetBound(isUpperbound)) return;
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
		double rhs = 0;
		for (Literal a:aggregatingVar)
			if(a.isPositive())
				expr.add(new ILPVariable(a.getName(), 1.0, ILPVarCategory.NORMAL));
			else {
				expr.add(new ILPVariable(a.getName(), -1.0, ILPVarCategory.NORMAL));
				rhs = rhs -1;
			}

		if (isUpperbound) {
			expr.add(new ILPVariable(multipleVarName, -1.0, ILPVarCategory.MIXED));
			this.addConstraint(expr, ILPOperator.GEQ, rhs);
		} else {
			expr.add(new ILPVariable(multipleVarName, -aggregatingVar.size(), ILPVarCategory.MIXED));
			this.addConstraint(expr, ILPOperator.LEQ, rhs);
		}
	}

	private String addMultipleAuxiliaryIfNew(ArrayList<Literal> aggregatingVar) throws ILPException {
		StringBuilder name = new StringBuilder();
		for (Literal a:aggregatingVar) {
			if (!a.isPositive())
				name.append("~");
			name.append(a.getName());
			name.append("_v_");
		}
		name.setLength(name.length() - 3);
		try {
			this.addVariable(name.toString(), 0d, 0d, 1d, false, ILPVarCategory.MIXED);
			return name.toString();
		} catch (ILPException e) {
			 e.printStackTrace();
			 throw new ILPException("Failed to set auxiliary variable :" + name + " " + e.getMessage());
		}
	}
		

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void addMixedAggregatedConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<DoubleLiteral> mixedSingleVars, boolean isConjunction) throws ILPException{
		String z = isHard? null:this.getNextZ(weight, mixedSingleVars.size());
		if(isConjunction)
			throw new ILPException("Can not aggregate conjunction formulas");
	
		// only do something if at least one single variable is present
		//0 <= z <= k
		if(mixedSingleVars.size()>0){
			if(isHard || weight>0){//Me TODO think of negative hard formula
				this.addMixedAggregatedPositiveDisjunctionConstraint(isHard, weight, aggregatedVars, mixedSingleVars, z);
			}else{
				this.addMixedAggregatedNegativeDisjunctionConstraint(isHard, weight, aggregatedVars, mixedSingleVars, z);
			}
		}
	}

	/**
	 * Mixed w negative and disjunction:
	 * 
	 * for each aggregatedVar: k * aggregatedVar_positive [1 - aggregatedVar_negative] <= z
	 * 
     * singleVars: p, q: mixed = p_v_q: optional(left hand): mixed <= p + q <= 2 * mixed  		
	 * 
	 * sum of mixedSingleVars <= z, MixedVar is always positive
	 * 
	 * optional: z <= k
	 * 
	 * @param model
	 * @return
	 * @throws ILPException 
	 */
	private void addMixedAggregatedNegativeDisjunctionConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<DoubleLiteral> mixedSingleVars, String z) throws ILPException {
		int k = mixedSingleVars.size();
		// for each aggregatedVar: k * aggregatedVar_positive [1 - aggregatedVar_negative] <= z
		for(Literal aggAxiom : aggregatedVars){
			ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
			double rhs = 0d;
			if(aggAxiom.isPositive())
				// k * aggregatedVar_positive
				expr.add(new ILPVariable(aggAxiom.getName(),k, ILPVarCategory.NORMAL));
			else {
				// k - k * aggregatedVar_negative
				expr.add(new ILPVariable(aggAxiom.getName(),- k, ILPVarCategory.NORMAL));
				rhs = rhs - k;
			}
	 		if (isHard)
	 			//Never reach this place! TODO Throw Exception or think of negative hard formula
	 			throw new ILPException("ERROR! no Hard Formula with negative weight is permitted");
 			expr.add(new ILPVariable(z, -1, ILPVarCategory.Z));
			this.addConstraint(expr, ILPOperator.LEQ, rhs);
		}

		
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
 		for(DoubleLiteral mixedSingleVar : mixedSingleVars){
 			addAuxiliaryIfNew(mixedSingleVar);
 			// sum of mixedSingleVars <= z, MixedVar is always positive
			expr.add(new ILPVariable(mixedSingleVar.getName(), 1.0, ILPVarCategory.MIXED));
			// singleVars: p, q: mixed = p_v_q: optional(left hand): mixed <= p + q <= 2 * mixed
//				addBoundMixedConstraint(mixedSingleVar, true);
			addBoundMixedConstraint(mixedSingleVar, false);
		}
 		if (isHard)
 			//Never reach this place! TODO Throw Exception or think of negative hard formula
 			throw new ILPException("ERROR! no Hard Formula with negative weight is permitted");
		expr.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		this.addConstraint(expr, ILPOperator.LEQ, 0d);
		
	}
	
	/**
	 * Mixed w positive and disjunction:
	 * 
     * singleVars: p, q: mixed = p_v_q: mixed <= p + q <= 2 * mixed :optional (right hand)  		
	 * 
	 * sum k * aggregatedVars_positive [1 - aggregatedVar_negative] + (-1)_{if single var negative} + sum of mixedSingleVars >= z 
	 * 
	 * 0 <= z <= k
	 * 
	 * @param model
	 * @return
	 * @throws GRBException
	 */
	private void addMixedAggregatedPositiveDisjunctionConstraint(boolean isHard, double weight, ArrayList<Literal> aggregatedVars, ArrayList<DoubleLiteral> mixedSingleVars, String z) throws ILPException{
		int k = mixedSingleVars.size();
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
		
		double rhs = 0;

		for(Literal aggAxiom : aggregatedVars){
			if(aggAxiom.isPositive()){
				//sum k * aggregatedVars_positive
				expr.add(new ILPVariable(aggAxiom.getName(),k, ILPVarCategory.NORMAL));
			}else{
				//sum k - k * aggregatedVars_negative
				expr.add(new ILPVariable(aggAxiom.getName(),- k, ILPVarCategory.NORMAL));
				rhs = rhs - k;
			}
		}

 		for(DoubleLiteral mixedSingleVar : mixedSingleVars){
 			addAuxiliaryIfNew(mixedSingleVar);
 			// MixedVar is always positive
			expr.add(new ILPVariable(mixedSingleVar.getName(), 1d, ILPVarCategory.MIXED));
			//singleVars: p, q: mixed = p_v_q: mixed <= p + q <= 2 * mixed :optional (right hand)
			addBoundMixedConstraint(mixedSingleVar, true);
//				addBoundMixedConstraint(mixedSingleVar, false);
		}
 		if (isHard) //replace z variable with k
 			rhs = rhs + k;
 		else
 			expr.add(new ILPVariable(z, -1, ILPVarCategory.Z));
		this.addConstraint(expr, ILPOperator.GEQ, rhs);
		
	}
	
	private void addBoundMixedConstraint(DoubleLiteral mixedSingleVar, boolean isUpperbound) throws ILPException{
//It doesn't work! deleted.			if (mixedSingleVar.hadSetBound(isUpperbound)) return;
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
		double rhs = 0;
		if(mixedSingleVar.getA().isPositive())
			expr.add(new ILPVariable(mixedSingleVar.getA().getName(), 1.0, ILPVarCategory.NORMAL));
		else {
			expr.add(new ILPVariable(mixedSingleVar.getA().getName(), -1.0, ILPVarCategory.NORMAL));
			rhs = rhs -1;
		}
		if(mixedSingleVar.getB().isPositive())
			expr.add(new ILPVariable(mixedSingleVar.getB().getName(), 1.0, ILPVarCategory.NORMAL));
		else {
			expr.add(new ILPVariable(mixedSingleVar.getB().getName(), -1.0, ILPVarCategory.NORMAL));
			rhs = rhs -1;
		}

		if (isUpperbound) {
			expr.add(new ILPVariable(mixedSingleVar.getName(), -1.0, ILPVarCategory.MIXED));
			this.addConstraint(expr, ILPOperator.GEQ, rhs);
		} else {
			expr.add(new ILPVariable(mixedSingleVar.getName(), -2.0, ILPVarCategory.MIXED));
			this.addConstraint(expr, ILPOperator.LEQ, rhs);
		}
	}

	private void addAuxiliaryIfNew(DoubleLiteral mixedSingleVar) throws ILPException {
		try {
			this.addVariable(mixedSingleVar.getName(), 0d, 0d, 1d, false, ILPVarCategory.MIXED);
		} catch (ILPException e) {
			 e.printStackTrace();
			 throw new ILPException("Failed to set auxiliary variable :" + mixedSingleVar.getName() + " " + e.getMessage());
		}
	}
		
//}Me
		
	/**
	 * Builds cardinality restrictions of the form:
	 * 
	 * x1 + x2 + ... + xN <= cardinality
	 * 
	 * and adds it to the ILP.
	 * 
	 * @param variableNames
	 * @param cardinality
	 * @throws ILPException 
	 */
	public boolean addCardinalityConstraint(Set<String> variableNames, boolean lessEqual, int cardinality) throws ILPException{
		
		// build constraint
		ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
		
		ILPOperator operator = ILPOperator.LEQ;
		if(!lessEqual){
			operator = ILPOperator.GEQ;
		}
		for(String varName: variableNames){
		  expr.add(new ILPVariable(varName, 1, ILPVarCategory.NORMAL));
		}
		// add constraint
		this.addConstraint(expr, operator, (double)cardinality);
		
		return true;
	}
	
	/**
	 * If the soft formula has not already been added, it creates a new z variable with the given weight.
	 * If the soft formula already exists, the weight is just updated.
	 * 
	 * @param weight
	 * @param dublicateDetection
	 * @return
	 * @throws ILPException
	 */
	
	private String getNextZ(double weight) throws ILPException{
		return this.getNextZ(weight, 1.0);
	}
	
	
	/**
	 * Creates a new integer z variable. Used for the aggregated soft constraints.
	 *
	private GRBVar getNextIntegerZ(double weight) throws ILPException{
		return this.getNextIntegerZ(weight, GRB.INFINITY);
	}/
	
	/**
	 * Creates a new integer z variable. Used for the aggregated soft constraints.
	 */
	private String getNextZ(double weight, double upperbound) throws ILPException{
		StringBuilder varName = new StringBuilder();
		varName.append("z").append(this.zVarIndex);
		this.zVarIndex++;
		
		try {
			this.addVariable(varName.toString(), weight, 0.0d, upperbound, false, ILPVarCategory.Z);
		} catch (ILPException e) {
			 e.printStackTrace();
			 throw new ILPException("Failed to set the next z variable variable. " + e.getMessage());
		}
		return varName.toString();
	}
	
	/**
	 * Transforms a given list of integer values so that it only returns
	 * (1) those where the integer solution is 1 and 
	 * (2) those which are no added 'z' variables.
	 * 
	 * @param input
	 * @return
	 */
	public ArrayList<String> returnTrueGroundings(HashMap<String,Integer> input){
		ArrayList<String> solutionVars = new ArrayList<String>();
		for(Entry<String, Integer> set : input.entrySet()){
			int x = set.getValue();
			if(x==1){
				String varName = set.getKey();
				try{
					Integer.parseInt(varName.substring(1));
				}catch(NumberFormatException e){
//Me
					if (varName.indexOf("_v_") < 0)
						solutionVars.add(varName);
				}
//Me				System.out.println("Sol: "+varName + "->" +x);
			}
//Me			else{
//Me				System.out.println("WOWOWOWOW");
//Me			}
			//System.out.println(variables[i].get(StringAttr.VarName) + " " +x);
		}
		return solutionVars;
	}
	
	public int getNumberOfMixedVariables() {
		return numberOfMixedVariables;
	}
	
	public int getNumberOfBinaryVariables() {
		return numberOfBinaryVariables;
	}

	public int getZVarIndex() {
		return zVarIndex;
	}
	
	/**
	 * Solves the ILP, and 
	 * transforms the results so that it returns only true groundings.
	 * @return
	 * @throws SolveException
	 */
	public ArrayList<String> solve() throws SolveException{
		return this.returnTrueGroundings(this.optimizeILP());
	}
	

	/**
	 * Sets the start value of the given variables in varNames to 1.
	 * 
	 * @param varNames
	 * @throws ILPException
	 */
	public abstract void addStartValues(List<String> varNames) throws ILPException; 
	
	
	/**
	 * Writes model to file. File has to end with .ilp or lp
	 * @param filename
	 * @throws ILPException
	 */
	public abstract void writeModelToFile(String filename) throws ILPException;
	
	/**
	 * Returns all the variables available in the current ILP including the "zzz" variables.
	 * 
	 * The double value contains the actual weight of the variable in the target function.
	 * 
	 * @return
	 * @throws ILPException 
	 */
/*	public HashMap<String, Double> getVariables() throws ILPException{
		HashMap<String,Double> result = new HashMap<String,Double>();
		for(String varName : variables.keySet()){
			GRBVar var = variables.get(varName);
			try {
				result.put(varName, var.get(DoubleAttr.Obj));
			} catch (GRBException e) {
				e.printStackTrace();
				throw new ILPException("The objective value could not be read of variable " +varName);
			}
		}
		return result;
	}	*/  

	public abstract void close() throws ILPException;	

	public abstract void initialize() throws ILPException;

	/**
	 * Adds boolean or integer variable. If upperBound is 1 or lower, then the variable is boolean. If upperBound is larger than 1, the variable is integer. 
	 * 
	 * @param varName
	 * @param weight weight in objective
	 * @param upperBound upper bound of the variable (if > 1 then integer variable)
	 * @param override if true, then the set values are overridden in case the variable already exists.
	 * @param category the connectors can handle zVars different from normal vars and mixed vars
	 * @throws ILPException
	 */
	public abstract void addVariable(String varName, double objWeight, double lowerBound, double upperBound,
			boolean override, ILPVarCategory category) throws ILPException;

	/**
	 * Add ILP constraint. All containing variables have been previously added with the 'addVariable()' function.
	 * 
	 * @param lhs 
	 * @param operator
	 * @param rhs
	 * @throws ILPException
	 */
	public abstract void addConstraint(ArrayList<ILPVariable> lhs, ILPOperator operator, double rhs) throws ILPException;
	
	/**
	 * Solves the ILP and returns the integer values for each solution. 
	 * 
	 * @return solution. 
	 * @throws ILPException thrown if infeasible.
	 */
	public abstract HashMap<String, Integer> optimizeILP() throws ILPException;

	/**
	 * Gets the objective of the ILP from the last solve call.
	 * 
	 * @return
	 */
	public abstract double getObjectiveValue();
	
}
