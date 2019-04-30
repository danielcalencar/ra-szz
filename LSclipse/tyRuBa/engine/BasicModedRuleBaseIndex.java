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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import tyRuBa.modes.CompositeType;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Free;
import tyRuBa.modes.Mode;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeMapping;
import tyRuBa.modes.TypeModeError;

public class BasicModedRuleBaseIndex
  extends ModedRuleBaseIndex
{
  QueryEngine engine;
  String identifier;
  TypeInfoBase typeInfoBase;
  HashMap index = new HashMap();
  
  public void enableMetaData()
  {
    this.typeInfoBase.enableMetaData(this.engine);
  }
  
  public BasicModedRuleBaseIndex(QueryEngine qe, String identifier)
  {
    this.engine = qe;
    this.identifier = identifier;
    this.typeInfoBase = new TypeInfoBase(identifier);
  }
  
  protected ModedRuleBaseCollection getModedRuleBases(PredicateIdentifier predID)
    throws TypeModeError
  {
    ModedRuleBaseCollection result = maybeGetModedRuleBases(predID);
    if (result == null) {
      throw new TypeModeError("Unknown predicate " + predID);
    }
    return result;
  }
  
  public ModedRuleBaseCollection maybeGetModedRuleBases(PredicateIdentifier predID)
  {
    PredInfo pInfo = this.typeInfoBase.maybeGetPredInfo(predID);
    if (pInfo == null) {
      return null;
    }
    int numPredicateMode = pInfo.getNumPredicateMode();
    if (numPredicateMode == 0) {
      throw new Error("there are no mode declarations for " + predID);
    }
    return (ModedRuleBaseCollection)this.index.get(predID);
  }
  
  public void dumpFacts(PrintStream out)
  {
    for (Iterator iter = this.index.values().iterator(); iter.hasNext();)
    {
      ModedRuleBaseCollection element = (ModedRuleBaseCollection)iter.next();
      element.dumpFacts(out);
    }
  }
  
  public void insert(PredInfo p)
    throws TypeModeError
  {
    this.typeInfoBase.insert(p);
    ModedRuleBaseCollection rulebases = new ModedRuleBaseCollection(this.engine, p, this.identifier);
    this.index.put(p.getPredId(), rulebases);
  }
  
  public void addTypePredicate(TypeConstructor TypeConstructor, ArrayList subtypes)
  {
    RBRule typeRule = null;
    PredicateIdentifier typePred = new PredicateIdentifier(TypeConstructor.getName(), 1);
    RBTuple args = new RBTuple(RBVariable.make("?arg"));
    RBDisjunction cond = new RBDisjunction();
    PredInfo typePredInfo = Factory.makePredInfo(this.engine, TypeConstructor.getName(), 
      Factory.makeTupleType(Factory.makeAtomicType(TypeConstructor)));
    typePredInfo.addPredicateMode(Factory.makeAllBoundMode(1));
    PredicateMode freeMode = Factory.makePredicateMode(
      Factory.makeBindingList(Free.the), Mode.makeNondet());
    
    boolean hasFreeMode = true;
    for (int i = 0; i < subtypes.size(); i++)
    {
      String currTypeConstructorName = ((TypeConstructor)subtypes.get(i)).getName();
      try
      {
        PredInfo currSubTypePredInfo = 
          getPredInfo(new PredicateIdentifier(currTypeConstructorName, 1));
        hasFreeMode = (hasFreeMode) && (currSubTypePredInfo.getNumPredicateMode() > 1);
      }
      catch (TypeModeError localTypeModeError1)
      {
        throw new Error("This should not happen");
      }
      RBExpression currExp = new RBPredicateExpression(
        new PredicateIdentifier(currTypeConstructorName, 1), 
        new RBTuple(RBVariable.make("?arg")));
      cond.addSubexp(currExp);
    }
    if (hasFreeMode) {
      typePredInfo.addPredicateMode(freeMode);
    }
    if (subtypes.size() == 1) {
      typeRule = new RBRule(typePred, args, cond.getSubexp(0));
    } else if (subtypes.size() > 1) {
      typeRule = new RBRule(typePred, args, cond);
    }
    try
    {
      insert(typePredInfo);
      if (typeRule != null) {
        insert(typeRule);
      }
    }
    catch (TypeModeError e)
    {
      throw new Error("This should not happen", e);
    }
  }
  
  protected void basicAddTypeConst(TypeConstructor t)
  {
    this.typeInfoBase.addTypeConst(t);
  }
  
  public void addFunctorConst(Type repAs, CompositeType type)
  {
    this.typeInfoBase.addFunctorConst(repAs, type);
  }
  
  public void addTypeMapping(TypeMapping mapping, FunctorIdentifier id)
    throws TypeModeError
  {
    this.typeInfoBase.addTypeMapping(id, mapping);
  }
  
  public TypeMapping findTypeMapping(Class forWhat)
  {
    return this.typeInfoBase.findTypeMapping(forWhat);
  }
  
  public PredInfo maybeGetPredInfo(PredicateIdentifier predId)
  {
    return this.typeInfoBase.maybeGetPredInfo(predId);
  }
  
  public boolean contains(PredicateIdentifier p)
  {
    PredInfo result = this.typeInfoBase.maybeGetPredInfo(p);
    return result != null;
  }
  
  public TypeConstructor findType(String typeName)
  {
    return this.typeInfoBase.findType(typeName);
  }
  
  public TypeConstructor findTypeConst(String typeName, int arity)
  {
    return this.typeInfoBase.findTypeConst(typeName, arity);
  }
  
  public ConstructorType findConstructorType(FunctorIdentifier id)
  {
    return this.typeInfoBase.findConstructorType(id);
  }
  
  public void clear()
  {
    this.typeInfoBase.clear();
    this.index = new HashMap();
  }
  
  public void backup()
  {
    for (Iterator iter = this.index.values().iterator(); iter.hasNext();)
    {
      ModedRuleBaseCollection coll = (ModedRuleBaseCollection)iter.next();
      coll.backup();
    }
  }
  
  public String toString()
  {
    return getClass().getName() + "(" + this.identifier + ")";
  }
}
