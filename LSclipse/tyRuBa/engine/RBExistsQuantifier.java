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

public class RBExistsQuantifier
  extends RBExpression
{
  private RBExpression exp;
  private RBVariable[] vars;
  
  public RBExistsQuantifier(Collection variables, RBExpression exp)
  {
    this.exp = exp;
    this.vars = ((RBVariable[])variables.toArray(new RBVariable[variables.size()]));
  }
  
  RBExistsQuantifier(RBVariable[] vars, RBExpression exp)
  {
    this.exp = exp;
    this.vars = vars;
  }
  
  public RBExpression getExp()
  {
    return this.exp;
  }
  
  public int getNumVars()
  {
    return this.vars.length;
  }
  
  public RBVariable getVarAt(int pos)
  {
    return this.vars[pos];
  }
  
  public TypeEnv typecheck(PredInfoProvider predinfo, TypeEnv startEnv)
    throws TypeModeError
  {
    try
    {
      return getExp().typecheck(predinfo, startEnv);
    }
    catch (TypeModeError e)
    {
      throw new TypeModeError(e, this);
    }
  }
  
  public RBExpression convertToMode(ModeCheckContext context, boolean rearrange)
    throws TypeModeError
  {
    ModeCheckContext resultContext = (ModeCheckContext)context.clone();
    Collection boundedVars = getVariables();
    for (int i = 0; i < getNumVars(); i++)
    {
      RBVariable currVar = getVarAt(i);
      if (!boundedVars.contains(currVar)) {
        return Factory.makeModedExpression(
          this, 
          new ErrorMode("Existentially quantified variable " + currVar + 
          " must become bound in " + getExp()), 
          context);
      }
    }
    RBExpression converted = getExp().convertToMode(context, rearrange);
    return Factory.makeModedExpression(
      new RBExistsQuantifier(this.vars, converted), 
      converted.getMode(), resultContext);
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer("(EXISTS ");
    for (int i = 0; i < this.vars.length; i++)
    {
      if (i > 0) {
        result.append(",");
      }
      result.append(this.vars[i].toString());
    }
    result.append(" : " + getExp() + ")");
    return result.toString();
  }
  
  public Compiled compile(CompilationContext c)
  {
    return getExp().compile(c);
  }
  
  public RBExpression convertToNormalForm(boolean negate)
  {
    Frame varRenaming = new Frame();
    RBVariable[] newVars = new RBVariable[this.vars.length];
    for (int i = 0; i < this.vars.length; i++) {
      newVars[i] = ((RBVariable)this.vars[i].instantiate(varRenaming));
    }
    RBExpression convertedExp = this.exp.substitute(varRenaming).convertToNormalForm(false);
    return convertedExp.addExistsQuantifier(newVars, negate);
  }
  
  public Object accept(ExpressionVisitor v)
  {
    return v.visit(this);
  }
}
