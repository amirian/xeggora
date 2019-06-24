package com.mlnengine.xeggora.app.solver.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;

public class FullAggregationManager implements AggregationManager{
	// for aggregation of soft formulas if there is no double variable assigned
	private HashMap<String, FullAggregatedConstraint> clusteredAggregatedSoftFormulas = new HashMap<String,FullAggregatedConstraint>();
	private Set<PredicateExpression> aggregatedExpressions;
	private Set<PredicateExpression> aggregatingExpressions;
	private boolean isConjunction;
	private boolean isHard;
	
	public FullAggregationManager(Set<PredicateExpression> aggregatedExpressions, Set<PredicateExpression> aggregatingExpressions,
			boolean isConjunction, boolean isHard){
		this.isConjunction = isConjunction;
		this.isHard = isHard;
		this.aggregatedExpressions = aggregatedExpressions;
		this.aggregatingExpressions = aggregatingExpressions; 
	}
	
	public Set<PredicateExpression> getAggregatedExpressions() {
		return aggregatedExpressions;
	}
	
	public Set<PredicateExpression> getAggregatingExpressions() {
		return aggregatingExpressions;
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#getAggregatedSoftFormulas()
	 */
	@Override
	public void addConstraintsToILP(ILPConnector con) throws ILPException{
//		if (aggregatingExpressions.size()==0)
//			isHard = isHard;
		//TODO
		for(FullAggregatedConstraint clusteredConstr : clusteredAggregatedSoftFormulas.values()){
			clusteredConstr.addConstraintAndDeleteOldOne(con);
		}
	}
//Me temp
	@Override
	public String toString() {
		return "AggregatedExpressions: "+ aggregatedExpressions.toString() + 
				"\nAggregatingExpressions: "+ aggregatingExpressions.toString();// aggregatedVars.size()+"\nAggregatiedVar: "+aggregatedVars+"\nList: "+aggregatingList);
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#resetAggregatedSoftFormulas()
	 */
	@Override
	public void resetAggregatedSoftFormulas() {
		this.clusteredAggregatedSoftFormulas = new HashMap<String, FullAggregatedConstraint>();
	}
		
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#addAggregatedSoftConstraint(double, java.util.ArrayList, boolean)
	 */
	
	public void addForAggregation(ArrayList<Literal> aggregatedAxiomsForClause, ArrayList<Literal> aggregatingAxiomsForClause, double weight) {
		StringBuilder id = new StringBuilder();
		for(Literal l:aggregatedAxiomsForClause)
			id.append(l.toString());
		FullAggregatedConstraint fullConstr = clusteredAggregatedSoftFormulas.get(id.toString());
		
		if(fullConstr == null){
			fullConstr = new FullAggregatedConstraint(id.toString(), aggregatedAxiomsForClause, isConjunction, isHard, weight);
			this.clusteredAggregatedSoftFormulas.put(id.toString(),fullConstr);
		}
		fullConstr.addAggregatingVars(aggregatingAxiomsForClause);
		
	}
	
	@Override
	public void addClauseForAggregation(Clause clause, FormulaHard formula) {
//Nothing! aggregation is performed in addForAggregation instead
	}



	@Override
	public int getNumberOfAggregatedClauses() {
		return this.clusteredAggregatedSoftFormulas.size();
	}

	@Override
	public void calculateAggregation() {
		// here we have to do nothing, since aggregation already have been calculated.		
	}

	@Override
	public int getNumberOfCountingConstraintsAggregatingMoreThanOneClause() {
		int moreThanOne=0;
		for(FullAggregatedConstraint c : clusteredAggregatedSoftFormulas.values()){
			if(c.aggregatedMoreThanOneClause())
				moreThanOne = moreThanOne+1;
		}
		return moreThanOne;
	}

	@Override
	public int getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral() {
		int number=0;
		for(FullAggregatedConstraint c : clusteredAggregatedSoftFormulas.values())
			number = number+c.numberOfClausesWithMoreThanOneLiteral();
		
		return number;
	}

	@Override
	public int getNumberOfCountingConstraintsWithMoreThanOneLiteral() {
		int count=0;
		for(FullAggregatedConstraint c : clusteredAggregatedSoftFormulas.values()){
			if(c.hasMoreThanOneLiteral())
				count = count+1;
		}
		return count;
	}

	@Override
	public int getNumberOfCountingConstraintsWithOneLiteral() {
		int count=0;
		for(FullAggregatedConstraint c : clusteredAggregatedSoftFormulas.values()){
			if(!c.hasMoreThanOneLiteral())
				count = count+1;
		}
		return count;
	}

	@Override
	public int getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral() {
		int number=0;
		for(FullAggregatedConstraint c : clusteredAggregatedSoftFormulas.values())
			number = number+c.numberOfClausesWithOneLiteral();
		return number;
	}

	@Override
	public int aggregationOrder() {
		return aggregatingExpressions.size();
	}
}
