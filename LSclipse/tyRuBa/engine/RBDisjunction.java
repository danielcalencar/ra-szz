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

import java.util.Vector;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.visitor.ExpressionVisitor;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBDisjunction
  extends RBCompoundExpression
{
  public RBDisjunction() {}
  
  public RBDisjunction(Vector exps)
  {
    super(exps);
  }
  
  public RBDisjunction(Object[] exps)
  {
    super(exps);
  }
  
  public RBDisjunction(RBExpression e1, RBExpression e2)
  {
    super(e1, e2);
  }
  
  public Compiled compile(CompilationContext c)
  {
    Compiled result = Compiled.fail;
    for (int i = 0; i < getNumSubexps(); i++) {
      result = result.disjoin(getSubexp(i).compile(c));
    }
    return result;
  }
  
  protected String separator()
  {
    return ";";
  }
  
  public RBExpression crossMultiplyDisjunction(RBDisjunction other)
  {
    int numExps = getNumSubexps();
    int otherNumExps = other.getNumSubexps();
    RBDisjunction result = new RBDisjunction();
    for (int pos = 0; pos < numExps; pos++) {
      for (int otherPos = 0; otherPos < otherNumExps; otherPos++) {
        result.addSubexp(
          FrontEnd.makeAnd(getSubexp(pos), other.getSubexp(otherPos)));
      }
    }
    return result;
  }
  
  public RBExpression crossMultiply(RBExpression other)
  {
    if ((other instanceof RBDisjunction)) {
      return crossMultiplyDisjunction((RBDisjunction)other);
    }
    int numExps = getNumSubexps();
    RBDisjunction result = new RBDisjunction();
    for (int pos = 0; pos < numExps; pos++) {
      result.addSubexp(FrontEnd.makeAnd(getSubexp(pos), other));
    }
    return result;
  }
  
  public TypeEnv typecheck(PredInfoProvider predinfo, TypeEnv startEnv)
    throws TypeModeError
  {
    TypeEnv resultEnv = null;
    try
    {
      for (int i = 0; i < getNumSubexps(); i++)
      {
        TypeEnv currEnv = getSubexp(i).typecheck(predinfo, startEnv);
        if (resultEnv == null) {
          resultEnv = currEnv;
        } else {
          resultEnv = resultEnv.union(currEnv);
        }
      }
    }
    catch (TypeModeError e)
    {
      throw new TypeModeError(e, this);
    }
    return resultEnv;
  }
  
  public RBExpression convertToMode(ModeCheckContext context, boolean rearrange)
    throws TypeModeError
  {
    RBDisjunction result = new RBDisjunction();
    Mode resultMode = Mode.makeFail();
    ModeCheckContext resultContext = null;
    for (int i = 0; i < getNumSubexps(); i++)
    {
      RBExpression converted = 
        getSubexp(i).convertToMode(context, rearrange);
      if (resultContext == null) {
        resultContext = converted.getNewContext();
      } else {
        resultContext = 
          resultContext.intersection(converted.getNewContext());
      }
      result.addSubexp(converted);
      resultMode = resultMode.add(converted.getMode());
    }
    return Factory.makeModedExpression(result, resultMode, resultContext);
  }
  
  public RBExpression convertToNormalForm(boolean negate)
  {
    if (negate)
    {
      RBExpression result = null;
      for (int i = 0; i < getNumSubexps(); i++)
      {
        RBExpression converted = 
          getSubexp(i).convertToNormalForm(true);
        if (result == null) {
          result = converted;
        } else {
          result = result.crossMultiply(converted);
        }
      }
      return result;
    }
    RBDisjunction result = new RBDisjunction();
    for (int i = 0; i < getNumSubexps(); i++) {
      result.addSubexp(getSubexp(i).convertToNormalForm(false));
    }
    return result;
  }
  
  public Object accept(ExpressionVisitor v)
  {
    return v.visit(this);
  }
  
  public RBExpression addExistsQuantifier(RBVariable[] newVars, boolean negate)
  {
    RBCompoundExpression result;
    RBCompoundExpression result;
    if (negate) {
      result = new RBConjunction();
    } else {
      result = new RBDisjunction();
    }
    for (int i = 0; i < getNumSubexps(); i++) {
      result.addSubexp(getSubexp(i).addExistsQuantifier(newVars, negate));
    }
    return result;
  }
}
