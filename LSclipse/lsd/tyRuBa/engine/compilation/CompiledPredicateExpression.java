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
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RuleBase;
import tyRuBa.modes.Mode;
import tyRuBa.util.Action;
import tyRuBa.util.ElementSource;

public class CompiledPredicateExpression extends Compiled {

	final private RuleBase rules;
	final private RBTuple args;

	public CompiledPredicateExpression(Mode mode, RuleBase rules, RBTuple args) {
		super(mode);
		this.rules = rules;
		this.args = args;	
	}

	final public ElementSource runNonDet(final Object input, RBContext context) {
		RBTuple goal = (RBTuple)args.substitute((Frame)input);
		ElementSource result = compiledRules().runNonDet(goal, context);
		if (((Frame)input).isEmpty()) {
//			PoormansProfiler.countEmptyFrameAppend++;
			return result;
		} else {
			return result.map(new Action() {
				public Object compute(Object resultFrame) {
					return ((Frame)input).append((Frame)resultFrame);
				}
				public String toString() {
					return "++" + input;
				}
			});
		}
	}

	private Compiled compiledRules() {
		return rules.getCompiled();
	}

	public String toString() {
		return "PRED(" + args + ")";
	}

}
