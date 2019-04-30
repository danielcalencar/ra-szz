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
import tyRuBa.modes.CompositeType;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeMapping;
import tyRuBa.modes.TypeModeError;

public class BucketModedRuleBaseIndex
  extends ModedRuleBaseIndex
{
  BasicModedRuleBaseIndex localRuleBase;
  BasicModedRuleBaseIndex globalRuleBase;
  
  public void enableMetaData()
  {
    this.localRuleBase.enableMetaData();
    this.globalRuleBase.enableMetaData();
  }
  
  public BucketModedRuleBaseIndex(QueryEngine qe, String identifier, BasicModedRuleBaseIndex globalRuleBase)
  {
    this.localRuleBase = new BasicModedRuleBaseIndex(qe, identifier);
    this.globalRuleBase = globalRuleBase;
  }
  
  protected ModedRuleBaseCollection getModedRuleBases(PredicateIdentifier predID)
    throws TypeModeError
  {
    ModedRuleBaseCollection result = this.localRuleBase.maybeGetModedRuleBases(predID);
    if (result == null) {
      return this.globalRuleBase.getModedRuleBases(predID);
    }
    return result;
  }
  
  public void insert(PredInfo p)
    throws TypeModeError
  {
    if (this.globalRuleBase.contains(p.getPredId())) {
      throw new TypeModeError("Duplicate mode/type entries for predicate " + 
        p.getPredId());
    }
    this.localRuleBase.insert(p);
  }
  
  public void dumpFacts(PrintStream out)
  {
    out.print("local facts: ");
    this.localRuleBase.dumpFacts(out);
    out.print("global facts: ");
    this.globalRuleBase.dumpFacts(out);
  }
  
  public PredInfo maybeGetPredInfo(PredicateIdentifier predId)
  {
    PredInfo result = this.localRuleBase.maybeGetPredInfo(predId);
    if (result == null) {
      return this.globalRuleBase.maybeGetPredInfo(predId);
    }
    return result;
  }
  
  public void addTypePredicate(TypeConstructor TypeConstructor, ArrayList subtypes)
  {
    this.localRuleBase.addTypePredicate(TypeConstructor, subtypes);
  }
  
  protected void basicAddTypeConst(TypeConstructor t)
  {
    this.localRuleBase.basicAddTypeConst(t);
  }
  
  public void addFunctorConst(Type repAs, CompositeType type)
  {
    this.localRuleBase.addFunctorConst(repAs, type);
  }
  
  public void addTypeMapping(TypeMapping mapping, FunctorIdentifier id)
    throws TypeModeError
  {
    this.localRuleBase.addTypeMapping(mapping, id);
  }
  
  public TypeConstructor findType(String typeName)
  {
    TypeConstructor result = this.localRuleBase.findType(typeName);
    if (result == null) {
      result = this.globalRuleBase.findType(typeName);
    }
    return result;
  }
  
  public TypeConstructor findTypeConst(String typeName, int arity)
  {
    TypeConstructor result = this.localRuleBase.typeInfoBase.findTypeConst(typeName, arity);
    if (result != null) {
      return result;
    }
    return this.globalRuleBase.typeInfoBase.findTypeConst(typeName, arity);
  }
  
  public ConstructorType findConstructorType(FunctorIdentifier id)
  {
    ConstructorType result = this.localRuleBase.typeInfoBase.findConstructorType(id);
    if (result != null) {
      return result;
    }
    return this.globalRuleBase.typeInfoBase.findConstructorType(id);
  }
  
  public TypeMapping findTypeMapping(Class forWhat)
  {
    TypeMapping result = this.localRuleBase.typeInfoBase.findTypeMapping(forWhat);
    if (result != null) {
      return result;
    }
    return this.globalRuleBase.typeInfoBase.findTypeMapping(forWhat);
  }
  
  public void clear()
  {
    this.localRuleBase.clear();
  }
  
  public void backup()
  {
    this.globalRuleBase.backup();
    this.localRuleBase.backup();
  }
}
