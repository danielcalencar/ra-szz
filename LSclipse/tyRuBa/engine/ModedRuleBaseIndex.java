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

import java.io.PrintStream;
import java.util.ArrayList;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.CompositeType;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeMapping;
import tyRuBa.modes.TypeModeError;

public abstract class ModedRuleBaseIndex
  implements PredInfoProvider
{
  public abstract void enableMetaData();
  
  protected abstract ModedRuleBaseCollection getModedRuleBases(PredicateIdentifier paramPredicateIdentifier)
    throws TypeModeError;
  
  public RuleBase getBest(PredicateIdentifier predID, BindingList bindings)
  {
    try
    {
      return getModedRuleBases(predID).getBest(bindings);
    }
    catch (TypeModeError localTypeModeError)
    {
      throw new Error("this should never happen");
    }
  }
  
  public abstract void insert(PredInfo paramPredInfo)
    throws TypeModeError;
  
  public final void insert(RBComponent r)
    throws TypeModeError
  {
    RBComponent converted = r.convertToNormalForm();
    TupleType resultTypes = converted.typecheck(this);
    ModedRuleBaseCollection rulebases = getModedRuleBases(converted.getPredId());
    rulebases.insertInEach(converted, this, resultTypes);
  }
  
  public abstract void dumpFacts(PrintStream paramPrintStream);
  
  public CompositeType addType(CompositeType type)
    throws TypeModeError
  {
    TypeConstructor newTypeConst = type.getTypeConstructor();
    TypeConstructor oldTypeConst = 
      findTypeConst(newTypeConst.getName(), newTypeConst.getTypeArity());
    if (oldTypeConst != null)
    {
      if (oldTypeConst.isInitialized()) {
        throw new TypeModeError("Duplicate declaration for type " + type);
      }
      oldTypeConst.setParameter(type.getArgs());
      return (CompositeType)oldTypeConst.apply(type.getArgs(), false);
    }
    newTypeConst.setParameter(type.getArgs());
    basicAddTypeConst(newTypeConst);
    return type;
  }
  
  public abstract void addFunctorConst(Type paramType, CompositeType paramCompositeType);
  
  public abstract ConstructorType findConstructorType(FunctorIdentifier paramFunctorIdentifier);
  
  public abstract TypeConstructor findType(String paramString);
  
  public abstract TypeConstructor findTypeConst(String paramString, int paramInt);
  
  protected abstract void basicAddTypeConst(TypeConstructor paramTypeConstructor);
  
  public abstract void addTypePredicate(TypeConstructor paramTypeConstructor, ArrayList paramArrayList);
  
  public final PredInfo getPredInfo(PredicateIdentifier predId)
    throws TypeModeError
  {
    PredInfo result = maybeGetPredInfo(predId);
    if (result == null)
    {
      if (predId.getArity() == 1)
      {
        TypeConstructor t = findType(predId.getName());
        if (t != null)
        {
          NativePredicate.defineTypeTest(this, predId, t);
          return maybeGetPredInfo(predId);
        }
      }
      else if (predId.getArity() == 2)
      {
        String name = predId.getName();
        if (name.startsWith("convertTo"))
        {
          TypeConstructor t = findType(name.substring("convertTo".length()));
          if (t != null)
          {
            NativePredicate.defineConvertTo(this, t);
            return maybeGetPredInfo(predId);
          }
        }
      }
      throw new TypeModeError("Unknown predicate " + predId);
    }
    return result;
  }
  
  public abstract void addTypeMapping(TypeMapping paramTypeMapping, FunctorIdentifier paramFunctorIdentifier)
    throws TypeModeError;
  
  public abstract TypeMapping findTypeMapping(Class paramClass);
  
  public abstract void backup();
}
