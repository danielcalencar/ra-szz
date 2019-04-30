/* 
*    Ref-Finder
*    Copyright (C) <2015>  <PLSE_UCLA>
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package tyRuBa.engine;

/** An "avoid recursion" context guards against infinite recursion. 

    When recursion gets "to deep" an error is thrown.
    Some debug information (the rule stack) is output to System.err.
*/
public class RBAvoidRecursion extends RBContext {
	/** The "parrent" context */
	protected RBContext guarded;
	/** The current rule (DEBUG info) */
	protected RBRule rule;

	private int depth;

	private static int maxDepth = 0;

	public static int depthLimit = 250;

	public RBAvoidRecursion(RBContext aContext, RBRule r) {
		rule = r;
		guarded = aContext;
		depth = aContext.depth() + 1;
		if (depth > maxDepth) {
			//System.err.println("DEPTH : "+depth);
			maxDepth = depth;
			if (depth == depthLimit) {
				System.err.print(this);
				throw new Error("To deep recursion in rule application");
			}
		}
	}

	int depth() {
		return depth;
	}

	public String toString() {
		StringBuffer result = new StringBuffer(rule + "\n");
		if (guarded instanceof RBAvoidRecursion)
			result.append(guarded.toString());
		else
			result.append("--------------------");
		return result.toString();
	}

}
