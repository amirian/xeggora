/**
 * Implementation of single mixed 2nd order aggregation method 
 * 
 * @author Mohammad Mahdi Amirian
 *
 */

package com.mlnengine.xeggora.app.solver.aggregate;

import java.util.ArrayList;
import java.util.HashMap;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;

public class AggregationManagerDoubleSimpleImpl implements AggregationManager {
	private int positionOfSingleLiteral1 = 0;
	private int positionOfSingleLiteral2 = 0;
	private int quadPositionOfLiterals = 0;
	
	// for aggregation of soft formulas if there is no double variable assigned
	private HashMap<String, DoubleAggregatedConstraint> mixedAggregatedSoftFormulas = new HashMap<String,DoubleAggregatedConstraint>();

	public AggregationManagerDoubleSimpleImpl(int quadPos1, int quadPos2, int quadPositionOfLiterals){
		this.setQuadPositionOfLiterals(quadPos1, quadPos2, quadPositionOfLiterals);
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#getAggregatedSoftFormulas()
	 */
	@Override
	public void addConstraintsToILP(ILPConnector con) throws ILPException{
		for(DoubleAggregatedConstraint mixedConstr : mixedAggregatedSoftFormulas.values()){
			mixedConstr.addConstraintAndDeleteOldOne(con);
		}
	}

	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#resetAggregatedSoftFormulas()
	 */
	@Override
	public void resetAggregatedSoftFormulas() {
		this.mixedAggregatedSoftFormulas = new HashMap<String, DoubleAggregatedConstraint>();
	}
		
	/* (non-Javadoc)
	 * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#addAggregatedSoftConstraint(double, java.util.ArrayList, boolean)
	 */
	@Override
	public void addClauseForAggregation(Clause clause, FormulaHard formula) {
		// take the first one as single var.
		StringBuilder id = new StringBuilder();
		// everything but the first one!
		ArrayList<Literal> restriction = clause.getRestriction();

		if(restriction.size()>0){
			int pos1 = Math.min(this.positionOfSingleLiteral1, restriction.size()-1);
			int pos2 = Math.min(this.positionOfSingleLiteral2, restriction.size()-1);
			Literal singleAxiom1 = restriction.get(pos1);
			Literal singleAxiom2 = restriction.get(pos2);

			ArrayList<Literal> aggregatedAxioms = new ArrayList<Literal>();
			for(int i = 0 ; i<restriction.size(); i++){
				if(i!=pos1 && i!=pos2){
					Literal a = restriction.get(i);
					id.append(a.toString());
					aggregatedAxioms.add(a);
				}
			}

			if (Parameters.DEBUG_OUTPUT) 
				System.out.println("\n#" + quadPositionOfLiterals + ": Adding "+clause+" for Aggregation in\nformula: "+formula+"\nat Position: [" + pos1 + ", " + pos2 + "]" + "\nand Single Axioms: {"+singleAxiom1 + ", "+singleAxiom2+"}\nfrom: "+restriction.size()+" axioms\nAggregated axioms are: "+id);

			DoubleAggregatedConstraint mixedConstr = mixedAggregatedSoftFormulas.get(id.toString());
						
			if(mixedConstr == null){
				mixedConstr = new DoubleAggregatedConstraint(id.toString(), aggregatedAxioms, formula.isConjunction(), !(formula instanceof FormulaSoft), clause.getWeight());
				this.mixedAggregatedSoftFormulas.put(id.toString(),mixedConstr);
			}
			mixedConstr.addMixedSingleVar(new DoubleLiteral(singleAxiom1, singleAxiom2));
		}
	}



	@Override
	public int getNumberOfAggregatedClauses() {
		return this.mixedAggregatedSoftFormulas.size();
	}

	@Override
	public void calculateAggregation() {
		// here we have to do nothing, since aggregation already have been calculated.		
	}

	public int getPositionOfSingleLiteral1() {
		return positionOfSingleLiteral1;
	}

	public int getPositionOfSingleLiteral2() {
		return positionOfSingleLiteral2;
	}

	public void setQuadPositionOfLiterals(int quadPos1, int quadPos2, int quadPositionOfLiterals) {
		this.quadPositionOfLiterals = quadPositionOfLiterals;
		this.positionOfSingleLiteral1 = quadPos1;
		this.positionOfSingleLiteral2 = quadPos2;
	}

	@Override
	public int aggregationOrder() {
		return positionOfSingleLiteral1 == positionOfSingleLiteral2 ? 1 : 2;//TODO is this correct?!
	}

	@Override
	public int getNumberOfCountingConstraintsAggregatingMoreThanOneClause() {
		int moreThanOne=0;
		for(DoubleAggregatedConstraint c : mixedAggregatedSoftFormulas.values()){
			if(c.aggregatedMoreThanOneClause()){
				moreThanOne = moreThanOne+1;
			}
		}
		return moreThanOne;
	}

	@Override
	public int getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral() {
		int number=0;
		for(DoubleAggregatedConstraint c : mixedAggregatedSoftFormulas.values()){
			number = number+c.numberOfClausesWithMoreThanOneLiteral();
		}
		return number;
	}

	@Override
	public int getNumberOfCountingConstraintsWithMoreThanOneLiteral() {
		int count=0;
		for(DoubleAggregatedConstraint c : mixedAggregatedSoftFormulas.values()){
			if(c.hasMoreThanOneLiteral()){
				count = count+1;
			}
		}
		return count;
	}

	@Override
	public int getNumberOfCountingConstraintsWithOneLiteral() {
		int count=0;
		for(DoubleAggregatedConstraint c : mixedAggregatedSoftFormulas.values()){
			if(!c.hasMoreThanOneLiteral()){
				count = count+1;
			}
		}
		return count;
	}

	@Override
	public int getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral() {
		int number=0;
		for(DoubleAggregatedConstraint c : mixedAggregatedSoftFormulas.values()){
			number = number+c.numberOfClausesWithOneLiteral();
		}
		return number;
	}
}
