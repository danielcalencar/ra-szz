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
import java.util.HashMap;
import java.util.Map;
import serp.util.SoftValueMap;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.compilation.SemiDetCompiled;
import tyRuBa.util.Action;
import tyRuBa.util.DelayedElementSource;
import tyRuBa.util.ElementCollector;
import tyRuBa.util.ElementSetCollector;
import tyRuBa.util.ElementSource;
import tyRuBa.util.RemovableElementSource;

public class CachedRuleBase
  extends Compiled
{
  Compiled compiledContents;
  SemiDetCachedRuleBase mySemiDetCompanion = null;
  private Map cache = null;
  
  public CachedRuleBase(Compiled compiledRuleBase)
  {
    super(compiledRuleBase.getMode());
    this.compiledContents = compiledRuleBase;
    initCache();
  }
  
  private void initCache()
  {
    this.cache = (RuleBase.softCache ? new SoftValueMap() : new HashMap());
  }
  
  public ElementSource runNonDet(Object input, final RBContext context)
  {
    final RBTuple other = (RBTuple)input;
    FormKey k = new FormKey(other);
    CacheEntry entry = (CacheEntry)this.cache.get(k);
    ElementCollector cachedResult;
    if ((entry == null) || 
      ((cachedResult = entry.getCachedResult()) == null))
    {
      ElementCollector result = new ElementSetCollector();
      if (!RuleBase.silent) {
        if (entry == null) {
          System.err.print(".");
        } else {
          System.err.print("@");
        }
      }
      entry = new CacheEntry(k, result);
      this.cache.put(k, entry);
      
      result.setSource(new DelayedElementSource()
      {
        public ElementSource produce()
        {
          return CachedRuleBase.this.compiledContents.runNonDet(other, context);
        }
        
        public String produceString()
        {
          return other.toString();
        }
      });
      return result.elements();
    }
    ElementCollector cachedResult;
    if (!RuleBase.silent) {
      System.err.print("H");
    }
    final Frame call = new Frame();
    if (other.sameForm(entry.key.theKey, call, new Frame())) {
      cachedResult.elements().map(new Action()
      {
        public Object compute(Object f)
        {
          Frame callres = call.callResult((Frame)f);
          
          return callres;
        }
      });
    }
    throw new Error("Should never happen");
  }
  
  private class CacheEntry
  {
    FormKey key;
    ElementCollector result;
    
    CacheEntry(FormKey k, ElementCollector r)
    {
      this.result = r;
      this.key = k;
    }
    
    ElementCollector getCachedResult()
    {
      return this.result;
    }
  }
  
  public SemiDetCompiled first()
  {
    if (this.mySemiDetCompanion == null) {
      this.mySemiDetCompanion = new SemiDetCachedRuleBase(this.compiledContents.first());
    }
    return this.mySemiDetCompanion;
  }
  
  public String toString()
  {
    return "CACHED RULEBASE(...)";
  }
}
