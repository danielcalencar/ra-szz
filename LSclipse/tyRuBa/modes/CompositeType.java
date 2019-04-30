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

import java.util.HashMap;
import java.util.Map;

public class CompositeType
  extends BoundaryType
{
  private TypeConstructor typeConst = null;
  boolean strict = false;
  private TupleType args;
  
  public CompositeType(TypeConstructor typeConst, boolean strict, TupleType args)
  {
    this.typeConst = typeConst;
    this.strict = strict;
    this.args = args;
  }
  
  public TypeConstructor getTypeConstructor()
  {
    return this.typeConst;
  }
  
  public int hashCode()
  {
    return getTypeConstructor().hashCode() + 13 * this.args.hashCode();
  }
  
  public void checkEqualTypes(Type other, boolean grow)
    throws TypeModeError
  {
    if (((other instanceof TVar)) || ((other instanceof GrowableType)))
    {
      other.checkEqualTypes(this, grow);
    }
    else
    {
      check(other instanceof CompositeType, this, other);
      CompositeType cother = (CompositeType)other;
      check(getTypeConstructor().equals(cother.getTypeConstructor()), this, other);
      try
      {
        this.args.checkEqualTypes(cother.args, grow);
      }
      catch (TypeModeError e)
      {
        throw new TypeModeError(e, this);
      }
      boolean newStrict = (this.strict) || (cother.strict);
      cother.strict = newStrict;
      this.strict = newStrict;
    }
  }
  
  public boolean isSubTypeOf(Type other, Map renamings)
  {
    if ((other instanceof TVar)) {
      other = ((TVar)other).getContents();
    }
    if (other == null) {
      return false;
    }
    if (!(other instanceof CompositeType)) {
      return false;
    }
    CompositeType declared = (CompositeType)other;
    TypeConstructor declaredTypeConst = declared.getTypeConstructor();
    if (isStrict()) {
      return (this.typeConst.equals(declaredTypeConst)) && (declared.isStrict()) && (this.args.isSubTypeOf(declared.args, renamings));
    }
    if (this.typeConst.equals(declaredTypeConst)) {
      return this.args.isSubTypeOf(declared.args, renamings);
    }
    if (declaredTypeConst.isSuperTypeOf(this.typeConst))
    {
      Map params = new HashMap();
      for (int i = 0; i < this.typeConst.getTypeArity(); i++)
      {
        String currName = this.typeConst.getParameterName(i);
        params.put(currName, this.args.getParamType(i, this.typeConst));
      }
      for (int i = 0; i < declaredTypeConst.getTypeArity(); i++)
      {
        String currName = declaredTypeConst.getParameterName(i);
        Type paramType = (Type)params.get(currName);
        if (paramType != null)
        {
          Type declaredType = declared.args.getParamType(i, declaredTypeConst);
          if (declaredType == null) {
            return false;
          }
          if (!paramType.isSubTypeOf(declaredType, renamings)) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }
  
  public boolean equals(Object other)
  {
    if (!(other instanceof CompositeType)) {
      return false;
    }
    CompositeType cother = (CompositeType)other;
    
    return (getTypeConstructor().equals(cother.getTypeConstructor())) && (isStrict() == cother.isStrict()) && (this.args.equals(cother.args));
  }
  
  public String toString()
  {
    String constName = this.typeConst.getName() + this.args;
    if (isStrict()) {
      return "=" + constName;
    }
    return constName;
  }
  
  public boolean isFreeFor(TVar var)
  {
    return this.args.isFreeFor(var);
  }
  
  public Type clone(Map tfact)
  {
    return new CompositeType(this.typeConst, this.strict, (TupleType)this.args.clone(tfact));
  }
  
  public String getName()
  {
    return getTypeConstructor().getName();
  }
  
  public TupleType getArgs()
  {
    return this.args;
  }
  
  public Type union(Type other)
    throws TypeModeError
  {
    if (((other instanceof TVar)) || ((other instanceof GrowableType))) {
      return other.union(this);
    }
    check(other instanceof CompositeType, this, other);
    CompositeType cother = (CompositeType)other;
    TypeConstructor otherTypeConst = cother.typeConst;
    if (equals(other)) {
      return this;
    }
    if (this.typeConst.equals(otherTypeConst))
    {
      TupleType resultArg = (TupleType)this.args.union(cother.args);
      if ((this.strict) || (cother.strict)) {
        return this.typeConst.applyStrict(resultArg, false);
      }
      return this.typeConst.apply(resultArg, false);
    }
    if (otherTypeConst.isSuperTypeOf(this.typeConst))
    {
      check(!isStrict(), this, other);
      Map params = new HashMap();
      for (int i = 0; i < this.typeConst.getTypeArity(); i++) {
        params.put(this.typeConst.getParameterName(i), this.args.get(i));
      }
      TupleType resultArg = Factory.makeTupleType();
      for (int i = 0; i < cother.typeConst.getTypeArity(); i++)
      {
        String currName = cother.typeConst.getParameterName(i);
        Type paramValue = (Type)params.get(currName);
        if (paramValue != null) {
          resultArg.add(paramValue.union(cother.args.get(i)));
        } else {
          resultArg.add(cother.args.get(i));
        }
      }
      if (cother.strict) {
        return otherTypeConst.applyStrict(resultArg, false);
      }
      return otherTypeConst.apply(resultArg, false);
    }
    if (this.typeConst.isSuperTypeOf(otherTypeConst)) {
      return cother.intersect(this);
    }
    check(!isStrict(), this, other);
    check(!cother.isStrict(), this, other);
    TypeConstructor superTypeConst = this.typeConst.lowerBound(otherTypeConst);
    Map params = new HashMap();
    for (int i = 0; i < this.typeConst.getTypeArity(); i++) {
      params.put(this.typeConst.getParameterName(i), this.args.get(i));
    }
    for (int i = 0; i < otherTypeConst.getTypeArity(); i++)
    {
      String currName = otherTypeConst.getParameterName(i);
      Type paramValue = (Type)params.get(currName);
      if (paramValue == null) {
        params.put(currName, cother.args.get(i));
      } else {
        params.put(currName, paramValue.union(cother.args.get(i)));
      }
    }
    TupleType resultArg = Factory.makeTupleType();
    for (int i = 0; i < superTypeConst.getTypeArity(); i++)
    {
      String currName = superTypeConst.getParameterName(i);
      resultArg.add((Type)params.get(currName));
    }
    return superTypeConst.apply(resultArg, false);
  }
  
  public Type intersect(Type other)
    throws TypeModeError
  {
    if (((other instanceof TVar)) || ((other instanceof GrowableType))) {
      return other.intersect(this);
    }
    check(other instanceof CompositeType, this, other);
    CompositeType cother = (CompositeType)other;
    TypeConstructor otherTypeConst = cother.typeConst;
    if (equals(other)) {
      return this;
    }
    if (this.typeConst.equals(otherTypeConst))
    {
      TupleType resultArg = (TupleType)this.args.intersect(cother.args);
      if ((this.strict) || (cother.strict)) {
        return this.typeConst.applyStrict(resultArg, false);
      }
      return this.typeConst.apply(resultArg, false);
    }
    if (this.typeConst.isSuperTypeOf(otherTypeConst))
    {
      check(!cother.isStrict(), this, other);
      Map params = new HashMap();
      for (int i = 0; i < this.typeConst.getTypeArity(); i++) {
        params.put(this.typeConst.getParameterName(i), this.args.get(i));
      }
      TupleType resultArg = Factory.makeTupleType();
      for (int i = 0; i < cother.typeConst.getTypeArity(); i++)
      {
        String currName = cother.typeConst.getParameterName(i);
        Type paramValue = (Type)params.get(currName);
        check(paramValue != null, this, other);
        resultArg.add(paramValue.intersect(cother.args.get(i)));
      }
      return cother.typeConst.apply(resultArg, false);
    }
    if (otherTypeConst.isSuperTypeOf(this.typeConst)) {
      return cother.intersect(this);
    }
    throw new TypeModeError("Incompatible types: " + this + ", " + other);
  }
  
  public boolean hasOverlapWith(Type other)
  {
    if (((other instanceof TVar)) || ((other instanceof GrowableType))) {
      return other.hasOverlapWith(this);
    }
    if (!(other instanceof CompositeType)) {
      return false;
    }
    CompositeType cother = (CompositeType)other;
    TypeConstructor otherTypeConst = cother.getTypeConstructor();
    if (this.typeConst.equals(otherTypeConst)) {
      return this.args.hasOverlapWith(cother.args);
    }
    if (this.typeConst.isSuperTypeOf(otherTypeConst))
    {
      Map params = new HashMap();
      for (int i = 0; i < this.typeConst.getTypeArity(); i++) {
        params.put(this.typeConst.getParameterName(i), this.args.get(i));
      }
      for (int i = 0; i < otherTypeConst.getTypeArity(); i++)
      {
        Type paramValue = (Type)params.get(otherTypeConst.getParameterName(i));
        if ((paramValue != null) && 
          (cother.args.get(i).hasOverlapWith(paramValue))) {
          return true;
        }
      }
      return false;
    }
    if (otherTypeConst.isSuperTypeOf(this.typeConst)) {
      return other.hasOverlapWith(this);
    }
    return false;
  }
  
  boolean isStrict()
  {
    return this.strict;
  }
  
  public Type copyStrictPart()
  {
    if (isStrict()) {
      return this.typeConst.applyStrict((TupleType)this.args.copyStrictPart(), false);
    }
    TypeConstructor resultTypeConst = 
      this.typeConst.getSuperestTypeConstructor();
    Map params = new HashMap();
    for (int i = 0; i < this.typeConst.getTypeArity(); i++) {
      params.put(this.typeConst.getParameterName(i), this.args.get(i).copyStrictPart());
    }
    TupleType resultArg = Factory.makeTupleType();
    for (int i = 0; i < resultTypeConst.getTypeArity(); i++)
    {
      String currName = resultTypeConst.getParameterName(i);
      Type paramType = (Type)params.get(currName);
      if (paramType == null) {
        resultArg.add(Factory.makeTVar(currName));
      } else {
        resultArg.add(paramType);
      }
    }
    return resultTypeConst.apply(resultArg, false);
  }
  
  public void makeStrict()
  {
    this.strict = true;
  }
  
  public void addSubType(Type subType)
    throws TypeModeError
  {
    if (!(subType instanceof CompositeType)) {
      throw new TypeModeError(subType + " is an illegal subtype for " + this);
    }
    this.typeConst.addSubTypeConst(((CompositeType)subType).getTypeConstructor());
  }
  
  public void setRepresentationType(Type repBy)
  {
    this.typeConst.setRepresentationType(repBy);
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
    if (!(repAs instanceof CompositeType)) {
      return null;
    }
    CompositeType compositeRepAs = (CompositeType)repAs;
    if (compositeRepAs.getTypeConstructor().equals(this.typeConst)) {
      return this.args.getParamType(currName, compositeRepAs.args);
    }
    return null;
  }
  
  public Class javaEquivalent()
    throws TypeModeError
  {
    return this.typeConst.javaEquivalent();
  }
  
  public boolean isJavaType()
  {
    return getTypeConstructor().isJavaTypeConstructor();
  }
}
