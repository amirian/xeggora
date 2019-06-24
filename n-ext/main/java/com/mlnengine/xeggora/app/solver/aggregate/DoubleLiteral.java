/**
 * A pair of Literals, used in Mixed Single Variables 
 * 
 * @author Mohammad Mahdi Amirian
 *
 */

package com.mlnengine.xeggora.app.solver.aggregate;

import com.googlecode.rockit.app.solver.pojo.Literal;

public class DoubleLiteral {
	private Literal a;
	private Literal b;
	private String name;
	public DoubleLiteral(Literal a, Literal b) {
		setLiteral(a, b);
	}
	
/* doesn't work!
  	private boolean upperboundSet = false;
	private boolean lowerboundSet = false;
  	public boolean hadSetBound(boolean isUpperbound) {
 		boolean t;
		if (isUpperbound) {
			t = upperboundSet;
			upperboundSet = true;
		} else {
			t = lowerboundSet;
			lowerboundSet = true;
		}
		return t;
	}
*/
	
	public void setLiteral(Literal a, Literal b) {
		this.a = a;
		this.b = b;
		if (a == null || b == null)
			System.out.println("MIX LITERAL ADD ERROR");
//Me TODO Throw Exception
		if (a == null)
			if (b == null)
				name = "EMPTY";
			else
				name = b.getName();
		else
			if (b == null)
				name = a.getName();
			else {
				StringBuilder builder = new StringBuilder();
				if (!a.isPositive())
					builder.append("~");
				builder.append(a.getName());
				builder.append("_v_");
				if (!b.isPositive())
					builder.append("~");
				builder.append(b.getName());
				name = builder.toString();
			}
	}

	public Literal getA() {
		return a;
	}
	
	public Literal getB() {
		return b;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}


}
