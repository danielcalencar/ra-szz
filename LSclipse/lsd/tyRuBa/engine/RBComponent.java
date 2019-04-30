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

import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.TypeModeError;

/** 
 * Anything which can be inserted into a rulebase must be a
  RB component. Basically this is anything which knows how to unify itself
  with RBTerms.

  An RBComponent is something which is interpretable as a set of propositions
  in some or other way.
  */

public abstract class RBComponent {
	
	/** This method may return null for components (like rules)
	 * that have not yet been mode converted.
	 */
	public abstract Mode getMode();
	
	/** Functional (side effect free) append of two RBComponents 
	public RBComponent append(RBComponent other,RuleBase parent) {
		RBComponentVector result = new RBComponentVector(parent);
		result.insert(this);
		result.insert(other);
		return result.simplify();
	}
	*/

	/** make the union of two components (concatenate them) */
//	public RBComponent union(RBComponent other) {
//		return RBUnion.make(this,other);
//	}

//	/** Is this a null component ? */
//	public boolean isNull() {
//		return false;
//	}

	public abstract TupleType typecheck(PredInfoProvider predinfos) throws TypeModeError;
	
	public abstract RBComponent convertToMode(PredicateMode mode,
	ModeCheckContext context) throws TypeModeError;

	public abstract PredicateIdentifier getPredId();

	public String getPredName() {
		return getPredId().getName();
	}

	public abstract RBTuple getArgs();

	public RBComponent convertToNormalForm() {
		return this;
	}

	public boolean isGroundFact() {
		return false; // must be overriden by RBFact!
	}

	public boolean isValid() {
		// All components are always valid... except for ValidatorComponents.
		return true;
	}
	
	public abstract Compiled compile(CompilationContext c);

    public Validator getValidator() {
        return null;
    }
	
}
