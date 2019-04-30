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

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

public abstract class Type
  implements Cloneable, Serializable
{
  public static final Type integer = Factory.makeStrictAtomicType(Factory.makeTypeConstructor(Integer.class));
  public static final Type string = Factory.makeStrictAtomicType(Factory.makeTypeConstructor(String.class));
  public static final Type number = Factory.makeStrictAtomicType(Factory.makeTypeConstructor(Number.class));
  public static final Type object = Factory.makeStrictAtomicType(Factory.makeTypeConstructor(Object.class));
  
  public void checkEqualTypes(Type other)
    throws TypeModeError
  {
    checkEqualTypes(other, true);
  }
  
  public abstract void checkEqualTypes(Type paramType, boolean paramBoolean)
    throws TypeModeError;
  
  public abstract boolean isFreeFor(TVar paramTVar);
  
  public abstract Type clone(Map paramMap);
  
  public abstract Type union(Type paramType)
    throws TypeModeError;
  
  public abstract Type intersect(Type paramType)
    throws TypeModeError;
  
  public static void check(boolean b, Type t1, Type t2)
    throws TypeModeError
  {
    if (!b) {
      throw new TypeModeError("Incompatible types: " + t1 + ", " + t2);
    }
  }
  
  public abstract boolean isSubTypeOf(Type paramType, Map paramMap);
  
  public abstract Type copyStrictPart();
  
  public abstract boolean hasOverlapWith(Type paramType);
  
  public boolean hasOverlapWith(Vector types, boolean hasOverlap)
  {
    int size = types.size();
    boolean equalTypes = false;
    int counter = 0;
    while ((!equalTypes) && (counter < size))
    {
      Type currType = (Type)types.elementAt(counter);
      if (currType.equals(this))
      {
        hasOverlap = hasOverlap;
        equalTypes = true;
      }
      else if ((hasOverlap) && 
        (!currType.hasOverlapWith(this)))
      {
        hasOverlap = false;
      }
      counter++;
    }
    if (!equalTypes) {
      types.add(this);
    }
    return hasOverlap;
  }
  
  public abstract Type getParamType(String paramString, Type paramType);
  
  public Class javaEquivalent()
    throws TypeModeError
  {
    throw new TypeModeError("This type " + this + " has no defined mapping to a Java equivalent");
  }
  
  public boolean isJavaType()
  {
    return false;
  }
}
