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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import tyRuBa.engine.visitor.CollectVarsVisitor;
import tyRuBa.engine.visitor.InstantiateVisitor;
import tyRuBa.engine.visitor.SubstantiateVisitor;
import tyRuBa.engine.visitor.SubstituteVisitor;
import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.util.TwoLevelKey;

public abstract class RBTerm
  implements Cloneable, Serializable, TwoLevelKey
{
  public Object up()
  {
    return new UppedTerm(this);
  }
  
  public abstract Frame unify(RBTerm paramRBTerm, Frame paramFrame);
  
  abstract boolean freefor(RBVariable paramRBVariable);
  
  abstract boolean isGround();
  
  public abstract BindingMode getBindingMode(ModeCheckContext paramModeCheckContext);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  protected abstract boolean sameForm(RBTerm paramRBTerm, Frame paramFrame1, Frame paramFrame2);
  
  public final boolean sameForm(RBTerm other)
  {
    return sameForm(other, new Frame(), new Frame());
  }
  
  public abstract int formHashCode();
  
  public String quotedToString()
  {
    return toString();
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new Error("This should not happen");
    }
  }
  
  public int intValue()
  {
    throw new Error("This is not an integer");
  }
  
  public final Collection getVariables()
  {
    CollectVarsVisitor visitor = new CollectVarsVisitor();
    accept(visitor);
    Collection vars = visitor.getVars();
    vars.remove(RBIgnoredVariable.the);
    return vars;
  }
  
  protected abstract Type getType(TypeEnv paramTypeEnv)
    throws TypeModeError;
  
  public String functorTypeConstructor()
    throws TypeModeError
  {
    throw new TypeModeError(toString() + " cannot be used as a functor");
  }
  
  public final RBVariable[] varMap()
  {
    ArrayList varlist = new ArrayList();
    CollectVarsVisitor visitor = new CollectVarsVisitor(varlist);
    accept(visitor);
    return (RBVariable[])varlist.toArray(new RBVariable[varlist.size()]);
  }
  
  public abstract void makeAllBound(ModeCheckContext paramModeCheckContext);
  
  public RBTerm addTypeCast(TypeConstructor typeCast)
    throws TypeModeError
  {
    ConstructorType constructorType = typeCast.getConstructorType();
    if (constructorType == null) {
      throw new TypeModeError("Illegal cast: " + typeCast + " is not a concrete type");
    }
    return constructorType.apply(this);
  }
  
  public abstract Object accept(TermVisitor paramTermVisitor);
  
  public RBTerm substitute(Frame frame)
  {
    SubstituteVisitor visitor = new SubstituteVisitor(frame);
    return (RBTerm)accept(visitor);
  }
  
  public RBTerm instantiate(Frame frame)
  {
    InstantiateVisitor visitor = new InstantiateVisitor(frame);
    return (RBTerm)accept(visitor);
  }
  
  public RBTerm substantiate(Frame subst, Frame inst)
  {
    SubstantiateVisitor visitor = new SubstantiateVisitor(subst, inst);
    return (RBTerm)accept(visitor);
  }
  
  public abstract String toString();
  
  public boolean isOfType(TypeConstructor t)
  {
    return false;
  }
}
