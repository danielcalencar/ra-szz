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
import java.util.Iterator;
import tyRuBa.engine.factbase.FactBase;
import tyRuBa.engine.factbase.FactLibraryManager;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.TypeModeError;

public class ModedRuleBaseCollection
{
  private QueryEngine engine;
  ArrayList modedRBs = new ArrayList();
  FactBase facts = null;
  private PredicateIdentifier predId;
  private FactLibraryManager factLibraryManager;
  ArrayList unconvertedRules = new ArrayList();
  
  private class InsertionInfo
  {
    RBComponent rule;
    ModedRuleBaseIndex conversionContext;
    TupleType resultTypes;
    
    InsertionInfo(RBComponent r, ModedRuleBaseIndex c, TupleType t)
    {
      this.rule = r;
      this.conversionContext = c;
      this.resultTypes = t;
    }
    
    public String toString()
    {
      return this.rule.toString();
    }
    
    public boolean isValid()
    {
      return this.rule.isValid();
    }
  }
  
  public ModedRuleBaseCollection(QueryEngine qe, PredInfo p, String identifier)
  {
    this.engine = qe;
    this.facts = p.getFactBase();
    this.predId = p.getPredId();
    this.factLibraryManager = this.engine.frontend().getFactLibraryManager();
    for (int i = 0; i < p.getNumPredicateMode(); i++)
    {
      PredicateMode pm = p.getPredicateModeAt(i);
      this.modedRBs.add(makeEmptyModedRuleBase(pm, this.facts));
    }
  }
  
  private RuleBase newModedRuleBase(PredicateMode pm, FactBase facts)
  {
    ModedRuleBase result = makeEmptyModedRuleBase(pm, facts);
    this.modedRBs.add(result);
    for (Iterator iter = this.unconvertedRules.iterator(); iter.hasNext();)
    {
      InsertionInfo insertion = (InsertionInfo)iter.next();
      if (!insertion.isValid()) {
        iter.remove();
      } else {
        try
        {
          result.insert(
            insertion.rule, insertion.conversionContext, insertion.resultTypes);
        }
        catch (TypeModeError e)
        {
          e.printStackTrace();
          throw new Error("Cannot happen because all the rules have already been inserted before");
        }
      }
    }
    return result;
  }
  
  private ModedRuleBase makeEmptyModedRuleBase(PredicateMode pm, FactBase facts)
  {
    ModedRuleBase result = new ModedRuleBase(this.engine, pm, facts, this.factLibraryManager, this.predId);
    return result;
  }
  
  public int HashCode()
  {
    return this.modedRBs.hashCode() * 17 + 4986;
  }
  
  public void insertInEach(RBComponent r, ModedRuleBaseIndex rulebases, TupleType resultTypes)
    throws TypeModeError
  {
    if (r.isGroundFact())
    {
      this.facts.insert(r);
    }
    else if (!this.facts.isPersistent())
    {
      this.unconvertedRules.add(new InsertionInfo(r, rulebases, resultTypes));
      int size = this.modedRBs.size();
      for (int i = 0; i < size; i++) {
        ((RuleBase)this.modedRBs.get(i)).insert(r, rulebases, resultTypes);
      }
    }
    else
    {
      throw new Error("Rules cannot be added to persistent factbases");
    }
  }
  
  public RuleBase getBest(BindingList bindings)
  {
    RuleBase result = null;
    for (int i = 0; i < this.modedRBs.size(); i++)
    {
      RuleBase currRulebase = (RuleBase)this.modedRBs.get(i);
      BindingList currBindings = currRulebase.getParamModes();
      if (currBindings.equals(bindings)) {
        return currRulebase;
      }
      if (bindings.satisfyBinding(currBindings)) {
        if (result == null) {
          result = currRulebase;
        } else if (currRulebase.isBetterThan(result)) {
          result = currRulebase;
        }
      }
    }
    if ((result == null) || (result.getParamModes().equals(bindings))) {
      return result;
    }
    if (bindings.hasFree())
    {
      result = newModedRuleBase(
        Factory.makePredicateMode(bindings, result.getMode().moreBound(), false), 
        this.facts);
      return result;
    }
    result = newModedRuleBase(
      Factory.makePredicateMode(bindings, Mode.makeSemidet(), false), 
      this.facts);
    return result;
  }
  
  public void dumpFacts(PrintStream out)
  {
    out.print(this.facts);
  }
  
  public void backup()
  {
    this.facts.backup();
  }
}
