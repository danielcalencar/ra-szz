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

import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Bound;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBTemplateVar
  extends RBSubstitutable
{
  public RBTemplateVar(String name)
  {
    super(name.intern());
  }
  
  public Frame unify(RBTerm other, Frame f)
  {
    throw new Error("Unsupported operation");
  }
  
  boolean freefor(RBVariable v)
  {
    throw new Error("Unsupported operation");
  }
  
  boolean isGround()
  {
    return true;
  }
  
  public BindingMode getBindingMode(ModeCheckContext context)
  {
    return Bound.the;
  }
  
  protected boolean sameForm(RBTerm other, Frame leftToRight, Frame rightToLeft)
  {
    throw new Error("Unsupported operation");
  }
  
  public int formHashCode()
  {
    throw new Error("Unsupported operation");
  }
  
  protected Type getType(TypeEnv env)
    throws TypeModeError
  {
    return env.get(this);
  }
  
  public void makeAllBound(ModeCheckContext context) {}
  
  public Object accept(TermVisitor v)
  {
    return v.visit(this);
  }
  
  public String getFirst()
  {
    throw new Error("Variables cannot be two level keys");
  }
  
  public Object getSecond()
  {
    throw new Error("Variables cannot be two level keys");
  }
}
