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
import java.util.HashMap;
import java.util.Iterator;
import tyRuBa.modes.CompositeType;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.Factory;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeMapping;
import tyRuBa.modes.TypeModeError;
import tyRuBa.modes.UserDefinedTypeConstructor;

public class TypeInfoBase
  implements PredInfoProvider
{
  MetaBase metaBase;
  HashMap predicateMap = new HashMap();
  HashMap typeConstructorMap = new HashMap();
  HashMap functorMap = new HashMap();
  HashMap toTyRuBaMappingMap = new HashMap();
  
  public TypeInfoBase(String identifier)
  {
    addTypeConst(TypeConstructor.theAny);
    this.metaBase = null;
  }
  
  public void enableMetaData(QueryEngine qe)
  {
    this.metaBase = new MetaBase(qe);
    for (Iterator iter = this.typeConstructorMap.values().iterator(); iter.hasNext();) {
      this.metaBase.assertTypeConstructor((TypeConstructor)iter.next());
    }
  }
  
  public void insert(PredInfo pInfo)
    throws TypeModeError
  {
    PredInfo result = (PredInfo)this.predicateMap.get(pInfo.getPredId());
    if (result != null) {
      throw new TypeModeError("Duplicate mode/type entries for predicate " + 
        pInfo.getPredId());
    }
    this.predicateMap.put(pInfo.getPredId(), pInfo);
  }
  
  public PredInfo getPredInfo(PredicateIdentifier predId)
    throws TypeModeError
  {
    PredInfo result = maybeGetPredInfo(predId);
    if (result == null) {
      throw new TypeModeError("Unknown predicate " + predId);
    }
    return result;
  }
  
  public PredInfo maybeGetPredInfo(PredicateIdentifier predId)
  {
    return (PredInfo)this.predicateMap.get(predId);
  }
  
  public void addTypeConst(TypeConstructor t)
  {
    this.typeConstructorMap.put(t.getName() + "/" + t.getTypeArity(), t);
    if (this.metaBase != null) {
      this.metaBase.assertTypeConstructor(t);
    }
  }
  
  public void addFunctorConst(Type repAs, CompositeType type)
  {
    TypeConstructor tc = type.getTypeConstructor();
    FunctorIdentifier functorId = tc.getFunctorId();
    
    ConstructorType constrType = ConstructorType.makeUserDefined(functorId, repAs, type);
    this.functorMap.put(functorId, constrType);
    tc.setConstructorType(constrType);
  }
  
  public void addTypeMapping(FunctorIdentifier id, TypeMapping mapping)
    throws TypeModeError
  {
    TypeConstructor tc = findTypeConst(id.getName(), id.getArity());
    tc.getConstructorType();
    if ((tc instanceof UserDefinedTypeConstructor)) {
      ((UserDefinedTypeConstructor)tc).setMapping(mapping);
    } else {
      throw new Error("The tyRuBa type " + id + " is not a mappable type. Only Userdefined types can be mapped.");
    }
    if (tc.hasRepresentation())
    {
      ConstructorType ct = tc.getConstructorType();
      mapping.setFunctor(ct);
    }
    this.toTyRuBaMappingMap.put(mapping.getMappedClass(), mapping);
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer(
      "/******** predicate info ********/\n");
    Iterator itr = this.predicateMap.values().iterator();
    while (itr.hasNext())
    {
      PredInfo element = (PredInfo)itr.next();
      result.append(element.toString());
    }
    result.append("/******** user defined types ****/\n");
    itr = this.typeConstructorMap.values().iterator();
    while (itr.hasNext()) {
      result.append(itr.next() + "\n");
    }
    result.append("/********************************/\n");
    return result.toString();
  }
  
  public TypeConstructor findType(String typeName)
  {
    if ((typeName.equals("String")) || 
      (typeName.equals("Integer")) || 
      (typeName.equals("Number")) || 
      (typeName.equals("Float"))) {
      typeName = "java.lang." + typeName;
    }
    if (typeName.equals("RegExp")) {
      typeName = "org.apache.regexp.RE";
    }
    TypeConstructor result = (TypeConstructor)this.typeConstructorMap.get(typeName + "/0");
    if ((result == null) && 
      (typeName.indexOf('.') >= 0)) {
      try
      {
        Class cl = Class.forName(typeName);
        result = Factory.makeTypeConstructor(cl);
        addTypeConst(result);
      }
      catch (ClassNotFoundException localClassNotFoundException) {}
    }
    return result;
  }
  
  public TypeConstructor findTypeConst(String typeName, int arity)
  {
    TypeConstructor result = (TypeConstructor)this.typeConstructorMap.get(typeName + "/" + arity);
    if (result == null)
    {
      result = Factory.makeTypeConstructor(typeName, arity);
      addTypeConst(result);
    }
    return result;
  }
  
  public ConstructorType findConstructorType(FunctorIdentifier id)
  {
    return (ConstructorType)this.functorMap.get(id);
  }
  
  public TypeMapping findTypeMapping(Class cls)
  {
    return (TypeMapping)this.toTyRuBaMappingMap.get(cls);
  }
  
  public void clear()
  {
    this.predicateMap = new HashMap();
    this.typeConstructorMap = new HashMap();
  }
}
