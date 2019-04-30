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

import java.io.PrintStream;
import java.util.Collection;
import junit.framework.Assert;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.visitor.CollectFreeVarsVisitor;
import tyRuBa.engine.visitor.CollectVarsVisitor;
import tyRuBa.engine.visitor.ExpressionVisitor;
import tyRuBa.engine.visitor.SubstituteVisitor;
import tyRuBa.modes.ErrorMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.tdbc.PreparedQuery;

public abstract class RBExpression
  implements Cloneable
{
  private Mode mode = null;
  private ModeCheckContext newContext = null;
  
  public abstract Compiled compile(CompilationContext paramCompilationContext);
  
  PreparedQuery prepareForRunning(QueryEngine engine)
    throws TypeModeError
  {
    RBExpression converted = convertToNormalForm();
    TypeEnv resultEnv = converted.typecheck(engine.rulebase(), Factory.makeTypeEnv());
    RBExpression result = 
      converted.convertToMode(Factory.makeModeCheckContext(engine.rulebase()));
    if ((result.getMode() instanceof ErrorMode)) {
      throw new TypeModeError(
        this + " cannot be converted to any declared mode\n" + 
        "   " + result.getMode());
    }
    if (!RuleBase.silent)
    {
      System.err.println("inferred types: " + resultEnv);
      System.err.println("converted to Mode: " + result);
    }
    return new PreparedQuery(engine, result, resultEnv);
  }
  
  public final Collection getVariables()
  {
    CollectVarsVisitor visitor = new CollectVarsVisitor();
    accept(visitor);
    Collection vars = visitor.getVars();
    vars.remove(RBIgnoredVariable.the);
    return vars;
  }
  
  public final Collection getFreeVariables(ModeCheckContext context)
  {
    CollectFreeVarsVisitor visitor = new CollectFreeVarsVisitor(context);
    accept(visitor);
    return visitor.getVars();
  }
  
  public abstract TypeEnv typecheck(PredInfoProvider paramPredInfoProvider, TypeEnv paramTypeEnv)
    throws TypeModeError;
  
  public final RBExpression convertToMode(ModeCheckContext context)
    throws TypeModeError
  {
    return convertToMode(context, true);
  }
  
  public abstract RBExpression convertToMode(ModeCheckContext paramModeCheckContext, boolean paramBoolean)
    throws TypeModeError;
  
  public RBExpression convertToNormalForm()
  {
    return convertToNormalForm(false);
  }
  
  public RBExpression convertToNormalForm(boolean negate)
  {
    RBExpression result;
    RBExpression result;
    if (negate) {
      result = new RBNotFilter(this);
    } else {
      result = this;
    }
    return result;
  }
  
  public RBExpression crossMultiply(RBExpression other)
  {
    if ((other instanceof RBCompoundExpression)) {
      return other.crossMultiply(this);
    }
    return FrontEnd.makeAnd(this, other);
  }
  
  public abstract Object accept(ExpressionVisitor paramExpressionVisitor);
  
  public RBExpression substitute(Frame frame)
  {
    SubstituteVisitor visitor = new SubstituteVisitor(frame);
    return (RBExpression)accept(visitor);
  }
  
  public RBExpression addExistsQuantifier(RBVariable[] newVars, boolean negate)
  {
    RBExistsQuantifier exists = new RBExistsQuantifier(newVars, this);
    if (negate) {
      return new RBNotFilter(exists);
    }
    return exists;
  }
  
  public RBExpression makeModed(Mode mode, ModeCheckContext context)
  {
    try
    {
      RBExpression clone = (RBExpression)clone();
      clone.setMode(mode, context);
      return clone;
    }
    catch (CloneNotSupportedException e)
    {
      e.printStackTrace();
      throw new Error("Should not happen");
    }
  }
  
  private void setMode(Mode mode, ModeCheckContext context)
  {
    this.mode = mode;
    this.newContext = context;
  }
  
  public boolean isBetterThan(RBExpression other)
  {
    return getMode().isBetterThan(other.getMode());
  }
  
  protected Mode getMode()
  {
    return this.mode;
  }
  
  public ModeCheckContext getNewContext()
  {
    Assert.assertNotNull(this.newContext);
    return this.newContext;
  }
}
