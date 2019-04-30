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

import java.util.ArrayList;
import java.util.Map;

public class TupleType
  extends Type
{
  private ArrayList parts;
  
  public TupleType()
  {
    this.parts = new ArrayList();
  }
  
  public TupleType(Type[] types)
  {
    this.parts = new ArrayList(types.length);
    for (int i = 0; i < types.length; i++) {
      this.parts.add(types[i]);
    }
  }
  
  public Type[] getTypes()
  {
    return (Type[])this.parts.toArray(new Type[this.parts.size()]);
  }
  
  public int hashCode()
  {
    int size = size();
    int hash = getClass().hashCode();
    for (int i = 0; i < size; i++) {
      hash = hash * 19 + get(i).hashCode();
    }
    return hash;
  }
  
  public boolean equals(Object other)
  {
    if (other == null) {
      return false;
    }
    if (!(other instanceof TupleType)) {
      return false;
    }
    TupleType cother = (TupleType)other;
    int size = size();
    if (size != cother.size()) {
      return false;
    }
    for (int i = 0; i < size(); i++)
    {
      Type t1 = get(i);
      Type t2 = cother.get(i);
      if ((t1 == null) && (t2 != null)) {
        return false;
      }
      if ((t1 != null) && (t2 == null)) {
        return false;
      }
      if ((t1 != null) && (!get(i).equals(cother.get(i)))) {
        return false;
      }
    }
    return true;
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer("<");
    for (int i = 0; i < size(); i++)
    {
      if (i != 0) {
        result.append(", ");
      }
      result.append(get(i));
    }
    result.append(">");
    return result.toString();
  }
  
  public void add(Type newPart)
  {
    this.parts.add(newPart);
  }
  
  public Type get(int i)
  {
    return (Type)this.parts.get(i);
  }
  
  public int size()
  {
    return this.parts.size();
  }
  
  public void checkEqualTypes(Type tother, boolean grow)
    throws TypeModeError
  {
    if ((tother instanceof TVar))
    {
      tother.checkEqualTypes(this, grow);
    }
    else
    {
      check(tother instanceof TupleType, this, tother);
      TupleType other = (TupleType)tother;
      check(other.size() == size(), this, other);
      for (int i = 0; i < size(); i++)
      {
        Type t1 = get(i);
        Type t2 = other.get(i);
        t1.checkEqualTypes(t2, grow);
      }
    }
  }
  
  public boolean isSubTypeOf(Type tother, Map renamings)
  {
    if (!(tother instanceof TupleType)) {
      return false;
    }
    TupleType other = (TupleType)tother;
    if (size() != other.size()) {
      return false;
    }
    for (int i = 0; i < size(); i++) {
      if (!get(i).isSubTypeOf(other.get(i), renamings)) {
        return false;
      }
    }
    return true;
  }
  
  public Type clone(Map tfact)
  {
    TupleType result = Factory.makeTupleType();
    for (int i = 0; i < size(); i++) {
      if (get(i) == null) {
        result.add(null);
      } else {
        result.add(get(i).clone(tfact));
      }
    }
    return result;
  }
  
  public Type union(Type tother)
    throws TypeModeError
  {
    if ((tother instanceof TVar)) {
      return tother.union(this);
    }
    check(tother instanceof TupleType, this, tother);
    TupleType other = (TupleType)tother;
    check(other.size() == size(), this, other);
    TupleType result = Factory.makeTupleType();
    for (int i = 0; i < size(); i++) {
      if (get(i) == null) {
        result.add(other.get(i));
      } else {
        result.add(get(i).union(other.get(i)));
      }
    }
    return result;
  }
  
  public Type intersect(Type tother)
    throws TypeModeError
  {
    if ((tother instanceof TVar)) {
      return tother.intersect(this);
    }
    check(tother instanceof TupleType, this, tother);
    TupleType other = (TupleType)tother;
    check(other.size() == size(), this, other);
    TupleType result = Factory.makeTupleType();
    for (int i = 0; i < size(); i++) {
      if (get(i) == null) {
        result.add(other.get(i));
      } else {
        result.add(get(i).intersect(other.get(i)));
      }
    }
    return result;
  }
  
  public boolean hasOverlapWith(Type tother)
  {
    if (tother == null) {
      return false;
    }
    if ((tother instanceof TVar)) {
      return tother.hasOverlapWith(tother);
    }
    if (!(tother instanceof TupleType)) {
      return false;
    }
    TupleType other = (TupleType)tother;
    int size = size();
    if ((size == 0) || (size != other.size())) {
      return false;
    }
    for (int i = 0; i < size(); i++)
    {
      if ((get(i) == null) && (other.get(i) != null)) {
        return false;
      }
      if ((get(i) != null) && 
        (!get(i).hasOverlapWith(other.get(i)))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isFreeFor(TVar var)
  {
    for (int i = 0; i < size(); i++) {
      if ((get(i) != null) && (!get(i).isFreeFor(var))) {
        return false;
      }
    }
    return true;
  }
  
  public Type copyStrictPart()
  {
    TupleType result = new TupleType();
    for (int i = 0; i < size(); i++) {
      result.add(get(i).copyStrictPart());
    }
    return result;
  }
  
  public Type getParamType(int pos, TypeConstructor typeConst)
  {
    if (!typeConst.hasRepresentation()) {
      return get(pos);
    }
    return getParamType(typeConst.getParameterName(pos), 
      typeConst.getRepresentation());
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
    if ((repAs instanceof ListType))
    {
      if (size() == 1) {
        return get(0).getParamType(currName, repAs);
      }
      return null;
    }
    if (!(repAs instanceof TupleType)) {
      return null;
    }
    Type result = null;
    TupleType repAsTuple = (TupleType)repAs;
    if (size() != repAsTuple.size()) {
      return null;
    }
    for (int i = 0; i < size(); i++)
    {
      Type currParamType = get(i).getParamType(currName, repAsTuple.get(i));
      if ((result != null) && (currParamType != null)) {
        try
        {
          result.checkEqualTypes(currParamType);
        }
        catch (TypeModeError localTypeModeError)
        {
          return null;
        }
      } else if (result == null) {
        result = currParamType;
      }
    }
    return result;
  }
}
