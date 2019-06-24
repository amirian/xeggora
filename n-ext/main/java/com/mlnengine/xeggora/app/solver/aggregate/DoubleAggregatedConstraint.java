/**
 * Implementation of Mixed Aggregated Constraint with new Mixed Single Variable 
 * 
 * @author Mohammad Mahdi Amirian
 *
 */

package com.mlnengine.xeggora.app.solver.aggregate;

import java.util.ArrayList;

import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;

public class DoubleAggregatedConstraint {
	private String id;
	private ArrayList<Literal> aggregatedVars = new ArrayList<Literal>();
	private ArrayList<DoubleLiteral> mixedSingleVars = new ArrayList<DoubleLiteral>();
	
	private boolean isHard;
	private double weight;
	private boolean conjunction;
	//private ArrayList<GRBConstr> oldConstraints = new ArrayList<GRBConstr>();
	
	//private boolean deleteConstr =true;

	
	public DoubleAggregatedConstraint(String id, ArrayList<Literal> aggregatedVars, boolean conjunction, boolean isHard, double weight){
		this.id=id;
		//this.zVariable=zVariable;
		this.aggregatedVars=aggregatedVars;
		this.isHard=isHard;
		this.weight=weight;
		this.conjunction = conjunction;
		//this.oldConstraints=new ArrayList<GRBConstr>();
		//this.deleteConstr=true;
	}
	

	public void addMixedSingleVar(DoubleLiteral mixedSingleVar){
		this.mixedSingleVars.add(mixedSingleVar);
		//this.deleteConstr=true;
	}


	public double getWeight(){
		return this.weight;
	}
	

	

	public void addConstraintAndDeleteOldOne(ILPConnector con) throws ILPException{
		con.addMixedAggregatedConstraint(isHard, weight, aggregatedVars, mixedSingleVars, conjunction);
	}


	public boolean aggregatedMoreThanOneClause(){
		if(mixedSingleVars.size()>1){
			return true;
		}
		return false;
	}
	
	public boolean hasMoreThanOneLiteral(){
		if(aggregatedVars.size()>0){
			return true;
		}
		return false;
	}
	
	public int numberOfClausesWithMoreThanOneLiteral(){
		if(hasMoreThanOneLiteral()){
			return mixedSingleVars.size();
		}
		return 0;
	}
	public int numberOfClausesWithOneLiteral(){
		if(hasMoreThanOneLiteral()){
			return 0;
		}
		return mixedSingleVars.size();
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregatedConstraint#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregatedConstraint#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleAggregatedConstraint other = (DoubleAggregatedConstraint) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
