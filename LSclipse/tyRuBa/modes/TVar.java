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
package tyRuBa.modes;

import java.util.Map;

public class TVar
  extends Type
{
  private Type content;
  private static int ctr = 1;
  private int id = ++ctr;
  private String name;
  
  public TVar(String name)
  {
    this.name = name;
    this.content = null;
  }
  
  public Type getContents()
  {
    TVar me = derefTVar();
    if (me.content == null) {
      return null;
    }
    return this.content;
  }
  
  public String toString()
  {
    TVar me = derefTVar();
    if (me.isFree()) {
      return "?" + me.getName() + "_" + me.id;
    }
    return me.getContents().toString();
  }
  
  public String getName()
  {
    TVar me = derefTVar();
    return me.name;
  }
  
  private void setContents(Type other)
  {
    this.content = other;
  }
  
  public void checkEqualTypes(Type other, boolean grow)
    throws TypeModeError
  {
    TVar me = derefTVar();
    if (me.equals(other)) {
      return;
    }
    if ((other instanceof TVar))
    {
      TVar otherVar = ((TVar)other).derefTVar();
      if (me.isFree())
      {
        if (!otherVar.isFreeFor(this)) {
          throw new TypeModeError("Recursion in inferred type " + this + " & " + 
            otherVar);
        }
        me.setContents(otherVar);
      }
      else if (otherVar.isFree())
      {
        if (!me.isFreeFor(otherVar)) {
          throw new TypeModeError("Recursion in inferred type " + this + " & " + 
            otherVar);
        }
        otherVar.setContents(me);
      }
      else
      {
        me.content.checkEqualTypes(otherVar.content);
        me.setContents(otherVar);
      }
    }
    else if (me.isFree())
    {
      if (!other.isFreeFor(this)) {
        throw new TypeModeError("Recursion in inferred type " + this + " & " + 
          other);
      }
      me.setContents(other);
    }
    else
    {
      me.content.checkEqualTypes(other);
    }
  }
  
  public boolean isSubTypeOf(Type declared, Map renamings)
  {
    TVar me = derefTVar();
    if (!me.isFree()) {
      return me.getContents().isSubTypeOf(declared, renamings);
    }
    if (!(declared instanceof TVar)) {
      return false;
    }
    TVar vdeclared = ((TVar)declared).derefTVar();
    if (!vdeclared.isFree()) {
      return false;
    }
    TVar renamed = (TVar)renamings.get(me);
    if (renamed == null)
    {
      renamings.put(me, vdeclared);
      return true;
    }
    return vdeclared.equals(renamed);
  }
  
  private TVar derefTVar()
  {
    if ((this.content != null) && ((this.content instanceof TVar))) {
      return ((TVar)this.content).derefTVar();
    }
    return this;
  }
  
  public boolean isFree()
  {
    return getContents() == null;
  }
  
  public boolean isFreeFor(TVar var)
  {
    TVar me = derefTVar();
    if (!me.isFree()) {
      return me.content.isFreeFor(var);
    }
    return var != me;
  }
  
  public Type clone(Map varRenamings)
  {
    TVar me = derefTVar();
    TVar clone = (TVar)varRenamings.get(me);
    if (clone != null) {
      return clone;
    }
    clone = new TVar(me.getName());
    clone.setContents(me.content == null ? 
      null : me.content.clone(varRenamings));
    varRenamings.put(me, clone);
    return clone;
  }
  
  public Type union(Type other)
    throws TypeModeError
  {
    TVar me = derefTVar();
    if (!me.isFree()) {
      return me.getContents().union(other);
    }
    if (me.equals(other)) {
      return me;
    }
    check(other.isFreeFor(me), me, other);
    me.setContents(other);
    return me.content;
  }
  
  public boolean equals(Object other)
  {
    if (!(other instanceof TVar)) {
      return false;
    }
    return derefTVar() == ((TVar)other).derefTVar();
  }
  
  public int hashCode()
  {
    TVar aliasOfMe = derefTVar();
    if (aliasOfMe == this) {
      return super.hashCode();
    }
    return aliasOfMe.hashCode();
  }
  
  public Type intersect(Type other)
    throws TypeModeError
  {
    TVar me = derefTVar();
    if (me.equals(other)) {
      return me;
    }
    if (!me.isFree())
    {
      me.setContents(me.content.intersect(other));
      return me.content.intersect(other);
    }
    check(other.isFreeFor(me), this, other);
    me.setContents(other);
    return other;
  }
  
  public Type copyStrictPart()
  {
    if (isFree()) {
      return Factory.makeTVar(getName());
    }
    return getContents().copyStrictPart();
  }
  
  public boolean hasOverlapWith(Type other)
  {
    TVar me = derefTVar();
    if (!me.isFree()) {
      return me.content.hasOverlapWith(other);
    }
    return true;
  }
  
  public Type getParamType(String currName, Type repAs)
  {
    if ((repAs instanceof TVar))
    {
      if (currName.equals(((TVar)repAs).getName())) {
        return this;
      }
      return null;
    }
    if (!isFree()) {
      return getContents().getParamType(currName, repAs);
    }
    return null;
  }
  
  public Class javaEquivalent()
    throws TypeModeError
  {
    Type contents = getContents();
    if (contents != null) {
      return contents.javaEquivalent();
    }
    throw new TypeModeError("This type variable is empty, and therefore has no java equivalent");
  }
}
