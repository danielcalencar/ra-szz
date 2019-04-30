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

import java.util.Collection;

import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.visitor.ExpressionVisitor;
import tyRuBa.modes.ErrorMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBTestFilter extends RBExpression {

	private RBExpression test_q;

	public RBTestFilter(RBExpression test_query) {
		test_q = test_query;
	}

	public String toString() {
		return "TEST(" + getQuery() + ")";
	}

	public Compiled compile(CompilationContext c) {
		return test_q.compile(c).test();
	}

	public RBExpression getQuery() {
		return test_q;
	}
	
	public TypeEnv typecheck(PredInfoProvider predinfo, TypeEnv startEnv) throws TypeModeError {
		try {
			return getQuery().typecheck(predinfo, startEnv);
		} catch (TypeModeError e) {
			throw new TypeModeError(e, this);
		}
	}

	public RBExpression convertToMode(ModeCheckContext context, boolean rearrange)
	throws TypeModeError {
		Collection vars = test_q.getFreeVariables(context);
		
		if (vars.isEmpty()) {
			RBExpression converted = test_q.convertToMode(context, rearrange); 
			return Factory.makeModedExpression(
				new RBTestFilter(converted), 
				converted.getMode().first(),
				context);
		} else {
			return Factory.makeModedExpression(this,
				new ErrorMode("Variables improperly left unbound in TEST: " + vars),
				context);
		}
	}

	public RBExpression convertToNormalForm(boolean negate) {
		if (negate) {
			return getQuery().convertToNormalForm(true);
		} else {
			return new RBTestFilter(getQuery().convertToNormalForm(false));
		}
	}

	public Object accept(ExpressionVisitor v) {
		return v.visit(this);
	}

}
