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
package tyRuBa.engine.factbase.hashtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import tyRuBa.engine.FrontEnd;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.factbase.NamePersistenceManager;
import tyRuBa.engine.factbase.ValidatorManager;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Mode;
import tyRuBa.modes.PredicateMode;
import tyRuBa.util.Action;
import tyRuBa.util.ElementSource;
import tyRuBa.util.pager.Location;
import tyRuBa.util.pager.Pager;
import tyRuBa.util.pager.Pager.Resource;
import tyRuBa.util.pager.Pager.ResourceId;
import tyRuBa.util.pager.Pager.Task;

public final class Index
{
  private final int[] freePlaces;
  private final int[] boundPlaces;
  private String predicateName;
  private boolean checkDet;
  private QueryEngine engine;
  private ValidatorManager validatorManager;
  private NamePersistenceManager nameManager;
  private Location storageLocation;
  
  static class HashMapResource
    extends HashMap
    implements Pager.Resource
  {
    private long myLastCleanTime = System.currentTimeMillis();
    
    public boolean isClean(ValidatorManager vm)
    {
      long lastDirty = vm.getLastInvalidatedTime();
      return this.myLastCleanTime > lastDirty;
    }
    
    public void clean(ValidatorManager vm)
    {
      this.myLastCleanTime = System.currentTimeMillis();
      for (Iterator iter = entrySet().iterator(); iter.hasNext();)
      {
        Map.Entry entry = (Map.Entry)iter.next();
        Object whatIsThere = entry.getValue();
        if ((whatIsThere instanceof ArrayList))
        {
          ArrayList lstWhatIsThere = (ArrayList)whatIsThere;
          for (Iterator iterator = lstWhatIsThere.iterator(); iterator.hasNext();)
          {
            IndexValue element = (IndexValue)iterator.next();
            if (!element.isValid(vm)) {
              iterator.remove();
            }
          }
          int size = lstWhatIsThere.size();
          if (size == 0) {
            iter.remove();
          } else if (size == 1) {
            entry.setValue(lstWhatIsThere.get(0));
          }
        }
        else
        {
          IndexValue idxWhatIsThere = (IndexValue)whatIsThere;
          if (!idxWhatIsThere.isValid(vm)) {
            iter.remove();
          }
        }
      }
    }
  }
  
  public String toString()
  {
    String result = "Index(" + this.predicateName + " ";
    int arity = this.boundPlaces.length + this.freePlaces.length;
    char[] boundMap = new char[arity];
    for (int i = 0; i < boundMap.length; i++) {
      boundMap[i] = 'F';
    }
    for (int i = 0; i < this.boundPlaces.length; i++) {
      boundMap[this.boundPlaces[i]] = 'B';
    }
    return result + new String(boundMap) + ")";
  }
  
  Index(PredicateMode mode, Location storageLocation, QueryEngine engine, String predicateName)
  {
    this.validatorManager = engine.getFrontEndValidatorManager();
    this.engine = engine;
    this.storageLocation = storageLocation;
    this.predicateName = predicateName;
    this.nameManager = engine.getFrontendNamePersistenceManager();
    this.checkDet = ((mode.getMode().isDet()) || (mode.getMode().isSemiDet()));
    BindingList bl = mode.getParamModes();
    this.boundPlaces = new int[bl.getNumBound()];
    int boundPos = 0;
    this.freePlaces = new int[bl.getNumFree()];
    int freePos = 0;
    for (int i = 0; i < bl.size(); i++) {
      if (bl.get(i).isBound()) {
        this.boundPlaces[(boundPos++)] = i;
      } else {
        this.freePlaces[(freePos++)] = i;
      }
    }
  }
  
  Index(PredicateMode mode, Location storageLocation, QueryEngine engine, String predicateName, NamePersistenceManager nameManager, ValidatorManager validatorManager)
  {
    this(mode, storageLocation, engine, predicateName);
    this.nameManager = nameManager;
    this.validatorManager = validatorManager;
  }
  
  private Pager getPager()
  {
    return this.engine.getFrontEndPager();
  }
  
  private RBTuple extract(int[] toExtract, RBTuple from)
  {
    RBTerm[] extracted = new RBTerm[toExtract.length];
    for (int i = 0; i < extracted.length; i++) {
      extracted[i] = from.getSubterm(toExtract[i]);
    }
    return FrontEnd.makeTuple(extracted);
  }
  
  public RBTuple extractBound(RBTuple goal)
  {
    return extract(this.boundPlaces, goal);
  }
  
  public RBTuple extractFree(RBTuple goal)
  {
    return extract(this.freePlaces, goal);
  }
  
  public void addFact(IndexValue fact)
  {
    RBTuple parts = fact.getParts();
    RBTuple whole_key = extractBound(parts);
    RBTuple free = extractFree(parts);
    if (whole_key == RBTuple.theEmpty) {
      whole_key = free;
    }
    final Object key = whole_key.getSecond();
    final String topLevelKey = whole_key.getFirst();
    final IndexValue value = IndexValue.make(fact.getValidatorHandle(), free);
    
    getPager().asynchDoTask(getResourceFromKey(whole_key), new Pager.Task(true)
    {
      public Object doIt(Pager.Resource map_rsrc)
      {
        Index.HashMapResource map = (Index.HashMapResource)map_rsrc;
        if ((map != null) && (!map.isClean(Index.this.validatorManager))) {
          map.clean(Index.this.validatorManager);
        }
        if (map == null) {
          map = new Index.HashMapResource();
        }
        Object whatIsThere = map.get(key);
        if (whatIsThere == null)
        {
          map.put(key, value);
        }
        else if ((whatIsThere instanceof ArrayList))
        {
          ArrayList lstWhatIsThere = (ArrayList)whatIsThere;
          if (Index.this.checkDet) {
            for (Iterator iter = lstWhatIsThere.iterator(); iter.hasNext();)
            {
              IndexValue element = (IndexValue)iter.next();
              if (!element.getParts().equals(value.getParts())) {
                throw new Error(
                  "OOPS!! More than one fact has been inserted into a Det/SemiDet predicate (" + Index.this.predicateName + ") present = " + 
                  element.getParts() + " ||| new = " + value.getParts() + key);
              }
            }
          }
          lstWhatIsThere.add(value);
        }
        else
        {
          IndexValue idxWhatIsThere = (IndexValue)whatIsThere;
          if ((Index.this.checkDet) && 
            (!idxWhatIsThere.getParts().equals(value.getParts()))) {
            throw new Error(
              "OOPS!! More than one fact has been inserted into a Det/SemiDet predicate (" + Index.this.predicateName + ") present = " + 
              idxWhatIsThere.getParts() + " ||| new = " + value.getParts() + key);
          }
          ArrayList lstWhatIsThere = new ArrayList(2);
          lstWhatIsThere.add(whatIsThere);
          lstWhatIsThere.add(value);
          map.put(key, lstWhatIsThere);
        }
        changedResource(map);
        return null;
      }
    });
    getPager().asynchDoTask(this.storageLocation.getResourceID("keys.data"), new Pager.Task(true)
    {
      public Object doIt(Pager.Resource rsrc)
      {
        Index.HashSetResource toplevelKeys = (Index.HashSetResource)rsrc;
        if (toplevelKeys == null) {
          toplevelKeys = new Index.HashSetResource();
        }
        if (toplevelKeys.add(topLevelKey)) {
          changedResource(toplevelKeys);
        }
        return null;
      }
    });
  }
  
  private Pager.ResourceId getResourceFromKey(RBTuple whole_key)
  {
    return this.storageLocation.getResourceID(this.nameManager.getPersistentName(whole_key.getFirst()));
  }
  
  public ElementSource getMatchElementSource(RBTuple inputPars)
  {
    if (inputPars == RBTuple.theEmpty) {
      return convertIndexValuesToRBTuples(values());
    }
    final Object key = inputPars.getSecond();
    
    (ElementSource)getPager().synchDoTask(getResourceFromKey(inputPars), new Pager.Task(false)
    {
      public Object doIt(Pager.Resource rsrc)
      {
        Index.HashMapResource map_resource = (Index.HashMapResource)rsrc;
        if (map_resource == null) {
          return ElementSource.theEmpty;
        }
        return Index.this.convertIndexValuesToRBTuples(Index.this.removeInvalids(map_resource.get(key)));
      }
    });
  }
  
  public RBTuple getMatchSingle(RBTuple inputPars)
  {
    return (RBTuple)getMatchElementSource(inputPars).firstElementOrNull();
  }
  
  public ElementSource values()
  {
    
    
      getTopLevelKeys().map(new Action()
      {
        public Object compute(Object arg)
        {
          String topkey = (String)arg;
          return Index.this.getTopKeyValues(topkey);
        }
      }).flatten();
  }
  
  private ElementSource getTopLevelKeys()
  {
    HashSetResource topLevelKeys = (HashSetResource)getPager().synchDoTask(
      this.storageLocation.getResourceID("keys.data"), new Pager.Task(false)
      {
        public Object doIt(Pager.Resource rsrc)
        {
          Index.HashSetResource toplevelKeys = (Index.HashSetResource)rsrc;
          return toplevelKeys;
        }
      });
    return ElementSource.with(topLevelKeys.iterator());
  }
  
  private ElementSource getTopKeyValues(String topkey)
  {
    ElementSource valid_values = (ElementSource)getPager().synchDoTask(
      this.storageLocation.getResourceID(this.nameManager.getPersistentName(topkey)), new Pager.Task(false)
      {
        public Object doIt(Pager.Resource rsrc)
        {
          Index.HashMapResource map = (Index.HashMapResource)rsrc;
          ElementSource.with(map.values().iterator()).map(new Action()
          {
            public Object compute(Object arg)
            {
              return Index.this.removeInvalids(arg);
            }
          })
          
            .flatten();
        }
      });
    return valid_values;
  }
  
  public ElementSource convertIndexValuesToRBTuples(ElementSource source)
  {
    source.map(new Action()
    {
      public Object compute(Object arg)
      {
        return ((IndexValue)arg).getParts();
      }
    });
  }
  
  public ElementSource removeInvalids(Object values)
  {
    if (values == null) {
      return ElementSource.theEmpty;
    }
    if ((values instanceof ArrayList)) {
      ElementSource.with((ArrayList)values).map(new Action()
      {
        public Object compute(Object arg)
        {
          if (((IndexValue)arg).isValid(Index.this.validatorManager)) {
            return arg;
          }
          return null;
        }
      });
    }
    if (((IndexValue)values).isValid(this.validatorManager)) {
      return ElementSource.singleton(values);
    }
    return ElementSource.theEmpty;
  }
  
  public void backup() {}
  
  static class HashSetResource
    extends HashSet
    implements Pager.Resource
  {}
}
