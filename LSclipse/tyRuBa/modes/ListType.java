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

public class ListType
  extends Type
{
  private Type element;
  
  public ListType()
  {
    this.element = null;
  }
  
  public ListType(Type element)
  {
    this.element = element;
  }
  
  public int hashCode()
  {
    return getElementType() == null ? 23423 : getElementType().hashCode() + 5774;
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof ListType))
    {
      ListType cother = (ListType)other;
      if (getElementType() == null) {
        return cother.getElementType() == null;
      }
      return (cother.getElementType() != null) && (getElementType().equals(cother.getElementType()));
    }
    return false;
  }
  
  public String toString()
  {
    if (getElementType() == null) {
      return "[]";
    }
    return "[" + getElementType() + "]";
  }
  
  public Type getElementType()
  {
    return this.element;
  }
  
  public boolean isFreeFor(TVar var)
  {
    return (getElementType() == null) || (getElementType().isFreeFor(var));
  }
  
  public Type clone(Map tfact)
  {
    return new ListType(getElementType() == null ? null : getElementType().clone(tfact));
  }
  
  public Type intersect(Type other)
    throws TypeModeError
  {
    if ((other instanceof TVar)) {
      return other.intersect(this);
    }
    if (equals(other)) {
      return this;
    }
    check(other instanceof ListType, this, other);
    ListType lother = (ListType)other;
    if (getElementType() == null) {
      return this;
    }
    if (lother.getElementType() == null) {
      return lother;
    }
    return new ListType(
      getElementType().intersect(lother.getElementType()));
  }
  
  public void checkEqualTypes(Type other, boolean grow)
    throws TypeModeError
  {
    if ((other instanceof TVar))
    {
      other.checkEqualTypes(this, grow);
    }
    else
    {
      check(other instanceof ListType, this, other);
      ListType lother = (ListType)other;
      if (getElementType() != null) {
        if (lother.getElementType() == null) {
          lother.element = getElementType();
        } else {
          try
          {
            getElementType().checkEqualTypes(lother.getElementType(), grow);
          }
          catch (TypeModeError e)
          {
            throw new TypeModeError(e, this);
          }
        }
      }
    }
  }
  
  public boolean isSubTypeOf(Type declared, Map renamings)
  {
    if ((declared instanceof TVar)) {
      declared = ((TVar)declared).getContents();
    }
    if (declared == null) {
      return false;
    }
    if ((declared instanceof ListType))
    {
      ListType ldeclared = (ListType)declared;
      if (getElementType() == null) {
        return true;
      }
      if (ldeclared.getElementType() == null) {
        return false;
      }
      return getElementType().isSubTypeOf(ldeclared.getElementType(), renamings);
    }
    return false;
  }
  
  public boolean hasOverlapWith(Type other)
  {
    if ((other instanceof TVar)) {
      return other.hasOverlapWith(this);
    }
    if ((other instanceof ListType))
    {
      Type otherElement = ((ListType)other).element;
      if ((this.element == null) && (otherElement == null)) {
        return true;
      }
      if ((this.element == null) || (otherElement == null)) {
        return false;
      }
      return this.element.hasOverlapWith(otherElement);
    }
    return false;
  }
  
  public Type copyStrictPart()
  {
    if (this.element == null) {
      return new ListType();
    }
    return new ListType(this.element.copyStrictPart());
  }
  
  public Type union(Type other)
    throws TypeModeError
  {
    if ((other instanceof TVar)) {
      return other.union(this);
    }
    check(other instanceof ListType, this, other);
    ListType lother = (ListType)other;
    if (getElementType() == null) {
      return other;
    }
    if (lother.getElementType() == null) {
      return this;
    }
    return new ListType(
      getElementType().union(lother.getElementType()));
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
    if (!(repAs instanceof ListType)) {
      return null;
    }
    if (this.element == null) {
      return null;
    }
    return this.element.getParamType(currName, ((ListType)repAs).element);
  }
}
