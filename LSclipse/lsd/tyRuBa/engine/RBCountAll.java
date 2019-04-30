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
import tyRuBa.engine.compilation.CompiledCount;
import tyRuBa.engine.visitor.ExpressionVisitor;
import tyRuBa.modes.ErrorMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBCountAll extends RBExpression {

	private RBExpression query;
	private RBTerm extract;
	private RBTerm result;

	public RBCountAll(RBExpression q, RBTerm e, RBTerm r) {
		query = q;
		extract = e;
		result = r;
	}

	public RBExpression getQuery() {
		return query;
	}

	public RBTerm getExtract() {
		return extract;
	}
	
	public RBTerm getResult() {
		return result; 
	}

	public String toString() {
		return "COUNTALL(" + getQuery() + "," + getExtract() + "," + getResult() + ")";
	}

	public Compiled compile(CompilationContext c) {
		return new CompiledCount(getQuery().compile(c),getExtract(),getResult());
	}

	public TypeEnv typecheck(PredInfoProvider predinfo, TypeEnv startEnv) throws TypeModeError {
		try {
			TypeEnv afterQueryEnv = getQuery().typecheck(predinfo, startEnv);
			TypeEnv resultTypeEnv = Factory.makeTypeEnv();
			Type resultType = getResult().getType(resultTypeEnv);
			resultType.checkEqualTypes(Factory.makeAtomicType(
					Factory.makeTypeConstructor(java.lang.Integer.class)));
//			resultType.checkEqualTypes(getResult().getType(afterQueryEnv));
			return afterQueryEnv.intersect(resultTypeEnv);
		} catch (TypeModeError e) {
			throw new TypeModeError(e, this);
		}
	}

	public RBExpression convertToMode(ModeCheckContext context, boolean rearrange) throws TypeModeError {
		Collection freevars = query.getFreeVariables(context);
		Collection extractedVars = getExtract().getVariables();
		freevars.removeAll(extractedVars);
		
		if (!freevars.isEmpty()) {
			return Factory.makeModedExpression(
				this,
				new ErrorMode("Variables improperly left unbound in COUNT: " 
					+ freevars),
				context);
		} else {
			RBExpression convQuery = query.convertToMode(context, rearrange);
			Mode convertedMode = convQuery.getMode();
			if (convertedMode instanceof ErrorMode) { 
				return Factory.makeModedExpression(this, convQuery.getMode(), 
					convQuery.getNewContext());
			} else {
				ModeCheckContext newContext = (ModeCheckContext)context.clone();
				result.makeAllBound(newContext);
				return Factory.makeModedExpression(
					new RBCountAll(convQuery, getExtract(), getResult()),
					convertedMode.findAll(), newContext);
			}
		}
	}
	
	public RBExpression convertToNormalForm(boolean negate) {
		RBExpression result = 
			new RBCountAll(getQuery().convertToNormalForm(false), 
					getExtract(), getResult());
		if (negate) {
			return new RBNotFilter(result);
		} else {
			return result;
		}
	}

	public Object accept(ExpressionVisitor v) {
		return v.visit(this);
	}

}
