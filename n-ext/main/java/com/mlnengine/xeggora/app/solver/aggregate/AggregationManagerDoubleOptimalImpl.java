/**
 * Implementation of new higher order aggregation method 
 * Current method implements only the 2nd order case
 * 
 * @author Mohammad Mahdi Amirian
 *
 */

package com.mlnengine.xeggora.app.solver.aggregate;

import java.util.HashMap;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.aggregate.simple.AggregationManagerSimpleImpl;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;

public class AggregationManagerDoubleOptimalImpl implements AggregationManager {
	private HashMap<Integer, AggregationManager> aggregationManagers= new HashMap<Integer, AggregationManager>(); 
	private int maxSizeOfLiterals = 0;
	private int order = 2;
	AggregationManager optimalAggregation = null;
	
	public AggregationManagerDoubleOptimalImpl(int maxSizeOfLiterals, int order){
		if (order != 2) {
//			must never occur, throw exception
			order =2;
			System.out.println("Maximum permitted aggregation order is 2, Choosing 2 ...");
		}
		this.order = order;
		this.maxSizeOfLiterals=maxSizeOfLiterals;
		for(int i = 0;i<maxSizeOfLiterals;i++){
			for (int j = i;j<maxSizeOfLiterals;j++) {
				//Start on main diameter, then with distance 1,.. e.g.(0,0)(1,1)(2,2)(0,1)(1,2)(2,2)
				int quadPositionOfLiterals = i + ((j - i) * (1 - j + i + 2 * maxSizeOfLiterals)) / 2;
//				System.out.println(i+ "," + j + "="+quadPositionOfLiterals);
				if (quadPositionOfLiterals < maxSizeOfLiterals) // i == j, behave as 1st order
					aggregationManagers.put(quadPositionOfLiterals, new AggregationManagerSimpleImpl(quadPositionOfLiterals));
				else
					aggregationManagers.put(quadPositionOfLiterals, new AggregationManagerDoubleSimpleImpl(i, j, quadPositionOfLiterals));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#getAggregatedSoftFormulas()
	 */
	@Override
	public void addConstraintsToILP(ILPConnector con) throws ILPException{
		optimalAggregation.addConstraintsToILP(con);
	}

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#resetAggregatedSoftFormulas()
	 */
	@Override
	public void resetAggregatedSoftFormulas() {
		aggregationManagers= new HashMap<Integer, AggregationManager>(); 
	}
		
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#addAggregatedSoftConstraint(double, java.util.ArrayList, boolean)
	 */
	@Override
	public void addClauseForAggregation(Clause clause, FormulaHard formula) {
		for(int i =0; i<maxSizeOfLiterals * (maxSizeOfLiterals + 1) / 2; i++){
			aggregationManagers.get(i).addClauseForAggregation(clause, formula);
		}
	}

	@Override
	public int aggregationOrder() {
		return (optimalAggregation != null && optimalAggregation instanceof AggregationManagerDoubleSimpleImpl) ? 2 : 1;//TODO is this correct?
	}

	@Override
	public int getNumberOfAggregatedClauses() {
		if(optimalAggregation!=null){
			return optimalAggregation.getNumberOfAggregatedClauses();
		}
		return 0;
	}

	@Override
	public void calculateAggregation() {
		// we take the aggregation manager with the minimal number of agg clauses.
		int minClauses = Integer.MAX_VALUE;
		for(int i =0; i<maxSizeOfLiterals * (maxSizeOfLiterals + 1) / 2; i++)
			if (i<maxSizeOfLiterals) {//first order aggregation
				AggregationManagerSimpleImpl currentM = (AggregationManagerSimpleImpl)aggregationManagers.get(i);
				if(Parameters.DEBUG_OUTPUT) System.out.print("AggregationManager: " + i +"["+currentM.getPositionOfSingleLiteral()+"] column has "+ currentM.getNumberOfAggregatedClauses() + " aggregated Clauses.");
				if(currentM.getNumberOfAggregatedClauses()<minClauses){
					minClauses=currentM.getNumberOfAggregatedClauses();
					this.optimalAggregation=currentM;
					if(Parameters.DEBUG_OUTPUT) System.out.println(" Chosen.");
				}else
					if(Parameters.DEBUG_OUTPUT) System.out.println();
			} else {//quadratic aggregation
				AggregationManagerDoubleSimpleImpl currentM = (AggregationManagerDoubleSimpleImpl)aggregationManagers.get(i);
				if(Parameters.DEBUG_OUTPUT) System.out.print("AggregationManager: " + i +"["+currentM.getPositionOfSingleLiteral1()+", "+currentM.getPositionOfSingleLiteral2()+"] column has "+ currentM.getNumberOfAggregatedClauses() + " aggregated Clauses.");
				if(currentM.getNumberOfAggregatedClauses()<minClauses){
					minClauses=currentM.getNumberOfAggregatedClauses();
					this.optimalAggregation=currentM;
					if(Parameters.DEBUG_OUTPUT) System.out.println(" Chosen.");
				}else
					if(Parameters.DEBUG_OUTPUT) System.out.println();
			}
		this.resetAggregatedSoftFormulas();
	}

	@Override
	public int getNumberOfCountingConstraintsAggregatingMoreThanOneClause() {

		if(optimalAggregation!=null){
			return optimalAggregation.getNumberOfCountingConstraintsAggregatingMoreThanOneClause();
		}
		return 0;
	}

	@Override
	public int getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral() {
		if(optimalAggregation!=null){
			return optimalAggregation.getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral();
		}
		return 0;
	}

	@Override
	public int getNumberOfCountingConstraintsWithMoreThanOneLiteral() {
		if(optimalAggregation!=null){
			return optimalAggregation.getNumberOfCountingConstraintsWithMoreThanOneLiteral();
		}
		return 0;
	}

	@Override
	public int getNumberOfCountingConstraintsWithOneLiteral() {
		if(optimalAggregation!=null){
			return optimalAggregation.getNumberOfCountingConstraintsWithOneLiteral();
		}
		return 0;
	}

	@Override
	public int getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral() {
		if(optimalAggregation!=null){
			return optimalAggregation.getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral();
		}
		return 0;
	}

}
