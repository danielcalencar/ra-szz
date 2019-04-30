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
package tyRuBa.engine.compilation;

import tyRuBa.engine.Frame;
import tyRuBa.engine.RBContext;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;
import tyRuBa.modes.Mode;

public class CompiledFact extends SemiDetCompiled {
	
	RBTuple args;

	public CompiledFact(RBTuple args) {
		super(Mode.makeSemidet());
		this.args = args;
	}

	public Frame runSemiDet(Object input, RBContext context) {
		RBTerm goal = (RBTerm) input;
		// System.err.println("         Goal: " + goal);
		// System.err.println("Checking Fact: " + this);
		final Frame callFrame = new Frame();
		// Rename all the variables in the goal to avoid name conflicts.
		goal = (RBTuple) goal.instantiate(callFrame);
		//System.err.println("     Unifying : " + goal);
		//System.err.println("         with : " + args);
		Frame fc = goal.unify(args, new Frame());
		if (fc == null) {
			return null;
		} else {
			//System.err.println("         resu : " + fc);
			Frame result = callFrame.callResult(fc);
			//System.err.println("    callFrame : " + callFrame);
			//System.err.println("       result : " + result);
			return result;
		}
	}
	
	public String toString() {
		return "FACT(" + args + ")";
	}

}
