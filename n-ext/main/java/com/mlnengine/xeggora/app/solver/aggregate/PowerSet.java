/**
* @author Mohammad Mahdi Amirian
*
*/

package com.mlnengine.xeggora.app.solver.aggregate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;

public class PowerSet<E> implements Iterator<Set<E>>,Iterable<Set<E>>{
   private E[] arr = null;
   private BitSet bset = null;

   @SuppressWarnings("unchecked")
   public PowerSet(Set<E> set)
   {
       arr = (E[])set.toArray();
       bset = new BitSet(arr.length + 1);
   }

   @Override
   public boolean hasNext() {
       return !bset.get(arr.length);
   }

   @Override
   public Set<E> next() {
       Set<E> returnSet = new TreeSet<E>();
       for(int i = 0; i < arr.length; i++)
       {
           if(bset.get(i))
               returnSet.add(arr[i]);
       }
       //increment bset
       for(int i = 0; i < bset.size(); i++)
       {
           if(!bset.get(i))
           {
               bset.set(i);
               break;
           }else
               bset.clear(i);
       }

       return returnSet;
   }

   @Override
   public void remove() {
       throw new UnsupportedOperationException("Not Supported!");
   }

   @Override
   public Iterator<Set<E>> iterator() {
       return this;
   }

}
/* Usage:
        Set<Character> set = new TreeSet<Character> ();
        for(int i = 0; i < 5; i++)
            set.add((char) (i + 'A'));

        PowerSet<Character> pset = new PowerSet<Character>(set);
        for(Set<Character> s:pset)
        {
            System.out.println(s);
        }

*/
/*
My Implementation: All Subsets
				List<Set<String>> subSets = new ArrayList<Set<String>>();
				for(VariableType var : formula.getForVariables()) {
				    List<Set<String>> newSets = new ArrayList<Set<String>>();
				    for(Set<String> curSet:subSets) {
				        Set<String> copyPlusNew = new HashSet<String>();
				        copyPlusNew.addAll(curSet);
				        copyPlusNew.add(",xx." + var.getName());
				        newSets.add(copyPlusNew);
				    }
				    Set<String> newValSet = new HashSet<String>();
				    newValSet.add("xx."+var.getName());
				    newSets.add(newValSet);
				    subSets.addAll(newSets);
				}

 				StringBuilder vars = new StringBuilder();
 				 
				for(Set<String> set:subSets) {
			    	vars.append(",COUNT(DISTINCT ");
				    for(String setEntry:set) {
				    	vars.append(setEntry);
				    }
			    	vars.append(") as `n");
				    for(String setEntry:set) {
				    	vars.append(setEntry);
				    }
					vars.append("`");
				}				
				vars. deleteCharAt(0);
				String sq = "SELECT "+vars.toString() + s.substring(s.indexOf("FROM"));
				//////// 
//old method
// 				StringBuilder vars = new StringBuilder();
// 
//				for(VariableType var : formula.getForVariables()){
//					vars.append(",xx.");
//					vars.append(var.getName());
//					vars.append(" ");
//				}
//				vars. deleteCharAt(0);
//				String sq = "SELECT COUNT( * ) FROM (SELECT DISTINCT "
//						+vars.toString() + s.substring(s.indexOf("FROM"))+") as counts";
//old method end


//				String sq = "SELECT COUNT(DISTINCT xx.x) as `nx`,COUNT(DISTINCT xx.y) as `ny`,COUNT(DISTINCT xx.x,xx.y) as `nxy` FROM "
	//					+ "(SELECT x0.field0 as `x`, x0.field1 as `y` FROM `Kind` x0 ) as xx ";
				//(SELECT DISTINCT xx.x ,xx.y FROM  ,  )as xd
				System.out.println(sq);
				ResultSet res2 = sql.executeSelectQuery(sq);
				try {
					while (res2.next()) {
						java.sql.ResultSetMetaData md = res2.getMetaData();
						for (int i = 1; i <= md.getColumnCount(); i++)
						System.out.println(md.getColumnName(i) + ": " + res2.getString(i));
					}
					res2.getStatement().close();
					res2.close();
				}catch (SQLException e){
					System.out.println("ERROR");
				}
//Me SQL				

*/