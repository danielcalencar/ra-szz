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

import java.util.Collection;
import java.util.Iterator;
import tyRuBa.engine.ModedRuleBaseIndex;
import tyRuBa.engine.PredicateIdentifier;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RBVariable;
import tyRuBa.engine.RuleBase;

public class ModeCheckContext
  implements Cloneable
{
  private BindingEnv bindings;
  private ModedRuleBaseIndex rulebases;
  
  public ModeCheckContext(BindingEnv initialBindings, ModedRuleBaseIndex rulebases)
  {
    this.bindings = initialBindings;
    this.rulebases = rulebases;
  }
  
  public RuleBase getBestRuleBase(PredicateIdentifier predId, RBTuple args, BindingList bindings)
  {
    for (int i = 0; i < args.getNumSubterms(); i++) {
      bindings.add(args.getSubterm(i).getBindingMode(this));
    }
    RuleBase result = this.rulebases.getBest(predId, bindings);
    return result;
  }
  
  public ModedRuleBaseIndex getModedRuleBaseIndex()
  {
    return this.rulebases;
  }
  
  public BindingEnv getBindingEnv()
  {
    return this.bindings;
  }
  
  public boolean isBound(RBVariable var)
  {
    return getBindingEnv().isBound(var);
  }
  
  public void removeAllBound(Collection vars)
  {
    Iterator itr = vars.iterator();
    while (itr.hasNext())
    {
      RBVariable curr = (RBVariable)itr.next();
      if (isBound(curr)) {
        itr.remove();
      }
    }
  }
  
  public void makeBound(RBVariable variable)
  {
    this.bindings.putBindingMode(variable, Factory.makeBound());
  }
  
  public void bindVars(Collection vars)
  {
    Iterator itr = vars.iterator();
    while (itr.hasNext()) {
      makeBound((RBVariable)itr.next());
    }
  }
  
  public Object clone()
  {
    ModeCheckContext cl = new ModeCheckContext(this.bindings, 
      getModedRuleBaseIndex());
    cl.bindings = ((BindingEnv)getBindingEnv().clone());
    return cl;
  }
  
  public String toString()
  {
    return 
      "---------ModeCheckContext---------\nBindings: " + this.bindings;
  }
  
  public ModeCheckContext intersection(ModeCheckContext other)
  {
    return new ModeCheckContext(getBindingEnv().intersection(
      other.getBindingEnv()), getModedRuleBaseIndex());
  }
  
  public boolean checkIfAllBound(Collection boundVars)
  {
    for (Iterator iter = boundVars.iterator(); iter.hasNext();)
    {
      RBVariable element = (RBVariable)iter.next();
      if (!isBound(element)) {
        return false;
      }
    }
    return true;
  }
}
