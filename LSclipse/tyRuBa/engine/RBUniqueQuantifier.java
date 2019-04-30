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
import java.util.HashSet;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.compilation.CompiledUnique;
import tyRuBa.engine.visitor.ExpressionVisitor;
import tyRuBa.modes.ErrorMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBUniqueQuantifier
  extends RBExpression
{
  private RBExpression exp;
  private RBVariable[] vars;
  
  public RBUniqueQuantifier(Collection variables, RBExpression exp)
  {
    this.exp = exp;
    this.vars = ((RBVariable[])variables.toArray(new RBVariable[variables.size()]));
  }
  
  public RBUniqueQuantifier(RBVariable[] vars, RBExpression exp)
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
  
  public RBVariable[] getQuantifiedVars()
  {
    return this.vars;
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer("(UNIQUE ");
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
    Collection boundedVars = this.exp.getVariables();
    Collection vars = new HashSet();
    for (int i = 0; i < getNumVars(); i++)
    {
      RBVariable currVar = getVarAt(i);
      if (!boundedVars.contains(currVar)) {
        return Factory.makeModedExpression(
          this, 
          new ErrorMode("UNIQUE variable " + currVar + 
          " must become bound in " + this.exp), 
          context);
      }
      vars.add(currVar);
    }
    Collection freeVars = getFreeVariables(resultContext);
    if (freeVars.isEmpty())
    {
      RBExpression converted = this.exp.convertToMode(context, rearrange);
      return Factory.makeModedExpression(
        new RBUniqueQuantifier(vars, converted), 
        converted.getMode().unique(), converted.getNewContext());
    }
    return Factory.makeModedExpression(
      this, 
      new ErrorMode("Variables improperly left unbound in UNIQUE: " + 
      freeVars), 
      resultContext);
  }
  
  public RBExpression convertToNormalForm(boolean negate)
  {
    RBExpression result = new RBUniqueQuantifier(
      this.vars, this.exp.convertToNormalForm(false));
    if (negate) {
      return new RBNotFilter(result);
    }
    return result;
  }
  
  public Object accept(ExpressionVisitor v)
  {
    return v.visit(this);
  }
  
  public Compiled compile(CompilationContext c)
  {
    return new CompiledUnique(this.vars, this.exp.compile(c));
  }
}
