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

import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.MetaBase;

public abstract class TypeConstructor
{
  public static TypeConstructor theAny = Factory.makeTypeConstructor(Object.class);
  
  public Type apply(TupleType args, boolean growable)
  {
    if (growable) {
      return new GrowableType(new CompositeType(this, false, args));
    }
    return new CompositeType(this, false, args);
  }
  
  public Type applyStrict(TupleType args, boolean growable)
  {
    if (growable) {
      return new GrowableType(new CompositeType(this, true, args));
    }
    return new CompositeType(this, true, args);
  }
  
  public boolean isSuperTypeOf(TypeConstructor other)
  {
    if (equals(other)) {
      return true;
    }
    TypeConstructor superTypeConst = other.getSuperTypeConstructor();
    
    return (superTypeConst != null) && (isSuperTypeOf(superTypeConst));
  }
  
  public abstract TypeConstructor getSuperTypeConstructor();
  
  public TypeConstructor getSuperestTypeConstructor()
  {
    TypeConstructor superConst = getSuperTypeConstructor();
    if (superConst == null) {
      return this;
    }
    return superConst.getSuperestTypeConstructor();
  }
  
  public abstract String getName();
  
  public abstract int getTypeArity();
  
  public int getTermArity()
  {
    try
    {
      if (!hasRepresentation()) {
        throw new TypeModeError("The type constructor " + this + "is abstract and cannot be used as a term constructor");
      }
      Type representedBy = getRepresentation();
      if ((representedBy instanceof TupleType)) {
        return ((TupleType)representedBy).size();
      }
      if ((representedBy instanceof ListType)) {
        return 1;
      }
      if ((representedBy instanceof CompositeType)) {
        return 1;
      }
      throw new Error("This should not happen");
    }
    catch (TypeModeError localTypeModeError)
    {
      throw new Error("This should not happen, unless the type system is broken");
    }
  }
  
  public abstract String getParameterName(int paramInt);
  
  public TypeConstructor lowerBound(TypeConstructor otherTypeConst)
  {
    if (equals(otherTypeConst)) {
      return this;
    }
    if (isSuperTypeOf(otherTypeConst)) {
      return this;
    }
    if (otherTypeConst.isSuperTypeOf(this)) {
      return otherTypeConst;
    }
    return getSuperTypeConstructor().lowerBound(otherTypeConst);
  }
  
  public Type getRepresentation()
  {
    throw new Error("This is not a user defined type: " + this);
  }
  
  public boolean hasRepresentation()
  {
    return false;
  }
  
  public abstract ConstructorType getConstructorType();
  
  public FunctorIdentifier getFunctorId()
  {
    return new FunctorIdentifier(getName(), getTermArity());
  }
  
  public abstract boolean isInitialized();
  
  public void setParameter(TupleType args)
  {
    throw new Error("This is not a user defined type: " + this);
  }
  
  public void setConstructorType(ConstructorType constrType)
  {
    throw new Error("This is not a user defined type: " + this);
  }
  
  public void addSubTypeConst(TypeConstructor typeConstructor)
    throws TypeModeError
  {
    throw new TypeModeError("This is not a user defined type: " + this);
  }
  
  public void addSuperTypeConst(TypeConstructor superConst)
    throws TypeModeError
  {
    throw new TypeModeError("This is not a user defined type: " + this);
  }
  
  public void setRepresentationType(Type repBy)
  {
    throw new Error("This is not a user defined type: " + this);
  }
  
  public boolean isJavaTypeConstructor()
  {
    return false;
  }
  
  public void setMetaBase(MetaBase base) {}
  
  public abstract Class javaEquivalent();
}
