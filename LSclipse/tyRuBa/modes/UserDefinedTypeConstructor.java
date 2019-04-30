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
import tyRuBa.engine.MetaBase;

public class UserDefinedTypeConstructor
  extends TypeConstructor
  implements Serializable
{
  private String name;
  private TypeConstructor superConst = null;
  private Type representedBy;
  private TVar[] parameters;
  boolean initialized = false;
  private ConstructorType constructorType;
  private TypeMapping mapping;
  private transient MetaBase metaBase = null;
  
  public void setMetaBase(MetaBase metaBase)
  {
    this.metaBase = metaBase;
    if (this.superConst != null) {
      metaBase.assertSubtype(this.superConst, this);
    }
  }
  
  public UserDefinedTypeConstructor(String name, int arity)
  {
    this.name = name;
    this.parameters = new TVar[arity];
  }
  
  public boolean equals(Object other)
  {
    if (!(other instanceof UserDefinedTypeConstructor)) {
      return false;
    }
    UserDefinedTypeConstructor tother = (UserDefinedTypeConstructor)other;
    
    return (this.name.equals(tother.name)) && (this.parameters.length == tother.parameters.length);
  }
  
  public int hashCode()
  {
    return this.name.hashCode() * 13 + this.parameters.length;
  }
  
  public void addSubTypeConst(TypeConstructor subConst)
    throws TypeModeError
  {
    subConst.addSuperTypeConst(this);
  }
  
  public void addSuperTypeConst(TypeConstructor superConst)
    throws TypeModeError
  {
    if (equals(superConst)) {
      throw new TypeModeError(
        "Recursion in type inheritance: " + this + " depends on itself");
    }
    if (this.superConst == null)
    {
      this.superConst = superConst;
      if (this.metaBase != null) {
        this.metaBase.assertSubtype(superConst, this);
      }
    }
    else
    {
      throw new TypeModeError("Multiple inheritance not supported: " + this + 
        " inherits from " + this.superConst + " and " + superConst);
    }
  }
  
  public void setRepresentationType(Type repType)
  {
    this.representedBy = repType;
    if (this.metaBase != null) {
      this.metaBase.assertRepresentation(this, repType);
    }
  }
  
  public TypeConstructor getSuperTypeConstructor()
  {
    if (this.superConst != null) {
      return this.superConst;
    }
    return TypeConstructor.theAny;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public int getTypeArity()
  {
    return this.parameters.length;
  }
  
  public void setParameter(TupleType type)
  {
    if (type.size() != getTypeArity()) {
      throw new Error("This should not happen");
    }
    for (int i = 0; i < this.parameters.length; i++) {
      this.parameters[i] = ((TVar)type.get(i));
    }
    this.initialized = true;
  }
  
  public String getParameterName(int i)
  {
    if (i < getTypeArity()) {
      return this.parameters[i].getName();
    }
    throw new Error("This should not happen");
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer(this.name + "(");
    for (int i = 0; i < getTypeArity(); i++)
    {
      if (i > 0) {
        result.append(",");
      }
      result.append(this.parameters[i]);
    }
    result.append(")");
    if (this.representedBy != null) {
      result.append(" AS " + this.representedBy);
    }
    return result.toString();
  }
  
  public boolean isInitialized()
  {
    return this.initialized;
  }
  
  public Type getRepresentation()
  {
    return this.representedBy;
  }
  
  public boolean hasRepresentation()
  {
    return this.representedBy != null;
  }
  
  public void setConstructorType(ConstructorType constrType)
  {
    if (this.constructorType != null) {
      throw new Error("Should not set twice!");
    }
    if (!hasRepresentation()) {
      throw new Error("Only concrete composite types can have a constructorType");
    }
    this.constructorType = constrType;
  }
  
  public ConstructorType getConstructorType()
  {
    return this.constructorType;
  }
  
  public TypeConstructor getSuperestTypeConstructor()
  {
    TypeConstructor result = super.getSuperestTypeConstructor();
    if (TypeConstructor.theAny.equals(result)) {
      return this;
    }
    return result;
  }
  
  public TypeMapping getMapping()
  {
    return this.mapping;
  }
  
  public void setMapping(TypeMapping mapping)
  {
    if (this.mapping != null) {
      throw new Error("Can only define a single Java type mapping per tyRuBa type");
    }
    this.mapping = mapping;
  }
  
  public Class javaEquivalent()
  {
    if (getMapping() == null) {
      return null;
    }
    return getMapping().getMappedClass();
  }
}
