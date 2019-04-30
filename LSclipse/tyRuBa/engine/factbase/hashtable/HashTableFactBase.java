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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import junit.framework.Assert;
import tyRuBa.engine.Frame;
import tyRuBa.engine.PredicateIdentifier;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBComponent;
import tyRuBa.engine.RBContext;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.Validator;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.compilation.SemiDetCompiled;
import tyRuBa.engine.factbase.FactBase;
import tyRuBa.engine.factbase.NamePersistenceManager;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.Multiplicity;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.PredicateMode;
import tyRuBa.util.Action;
import tyRuBa.util.ElementSource;
import tyRuBa.util.pager.FileLocation;

public class HashTableFactBase
  extends FactBase
{
  private Map indexes;
  private int arity;
  private String name;
  private Index allFreeIndex;
  private QueryEngine engine;
  private boolean isEmpty = true;
  private String storageLocation;
  
  public HashTableFactBase(PredInfo info)
  {
    this.arity = info.getArity();
    this.name = info.getPredId().getName();
    this.engine = info.getQueryEngine();
    this.storageLocation = (this.engine.getStoragePath() + "/" + this.engine.getFrontendNamePersistenceManager().getPersistentName(this.name) + "/" + this.arity + "/");
    initIndexes(info);
  }
  
  private void initIndexes(PredInfo info)
  {
    this.indexes = new HashMap();
    
    BindingList allFree = Factory.makeBindingList(this.arity, Factory.makeFree());
    PredicateMode freeMode = new PredicateMode(allFree, new Mode(Multiplicity.zero, Multiplicity.many), false);
    this.allFreeIndex = new Index(freeMode, new FileLocation(this.storageLocation + "/" + 
      freeMode.getParamModes().getBFString() + "/"), this.engine, this.name + "/" + this.arity);
    this.indexes.put(freeMode.getParamModes(), this.allFreeIndex);
    
    BindingList allBound = Factory.makeBindingList(this.arity, Factory.makeBound());
    PredicateMode boundMode = new PredicateMode(allBound, new Mode(Multiplicity.zero, Multiplicity.one), false);
    this.indexes.put(boundMode.getParamModes(), this.allFreeIndex);
    for (int i = 0; i < info.getNumPredicateMode(); i++)
    {
      PredicateMode pm = info.getPredicateModeAt(i);
      BindingList paramModes = pm.getParamModes();
      if (new File(this.storageLocation + "/" + paramModes.getBFString()).exists()) {
        this.isEmpty = false;
      }
      if ((paramModes.getNumFree() != this.arity) && (paramModes.getNumBound() != this.arity)) {
        this.indexes.put(pm.getParamModes(), new Index(pm, new FileLocation(this.storageLocation + "/" + 
          pm.getParamModes().getBFString() + "/"), this.engine, this.name + "/" + this.arity));
      }
    }
    int numIndexes = (int)Math.pow(2.0D, this.arity);
    for (int i = 0; i < numIndexes; i++)
    {
      BindingList blist = Factory.makeBindingList();
      int checkNum = 1;
      for (int j = 0; j < this.arity; j++)
      {
        if ((i & checkNum) == 0) {
          blist.add(Factory.makeBound());
        } else {
          blist.add(Factory.makeFree());
        }
        checkNum *= 2;
      }
      if ((blist.getNumBound() != 0) && (blist.getNumFree() != 0) && (!this.indexes.containsKey(blist)) && 
        (new File(this.storageLocation + "/" + blist.getBFString()).exists()))
      {
        this.isEmpty = false;
        PredicateMode mode = new PredicateMode(blist, new Mode(Multiplicity.zero, Multiplicity.many), false);
        Index idx = new Index(mode, new FileLocation(this.storageLocation + "/" + 
          mode.getParamModes().getBFString() + "/"), this.engine, this.name + "/" + this.arity);
        this.indexes.put(mode.getParamModes(), idx);
      }
    }
  }
  
  private Index getIndex(PredicateMode mode)
  {
    Index index = (Index)this.indexes.get(mode.getParamModes());
    if (index == null)
    {
      index = makeIndex(mode);
      this.indexes.put(mode.getParamModes(), index);
    }
    return index;
  }
  
  private Index makeIndex(PredicateMode mode)
  {
    Index index = new Index(mode, new FileLocation(this.storageLocation + "/" + mode.getParamModes().getBFString() + "/"), 
      this.engine, this.name + "/" + this.arity);
    for (ElementSource iter = this.allFreeIndex.values(); iter.hasMoreElements();)
    {
      IndexValue fact = (IndexValue)iter.nextElement();
      index.addFact(fact);
    }
    return index;
  }
  
  public boolean isEmpty()
  {
    return this.isEmpty;
  }
  
  public boolean isPersistent()
  {
    return true;
  }
  
  public void insert(RBComponent f)
  {
    Assert.assertTrue("Only ground facts should be insterted in to FactBases", f.isGroundFact());
    this.isEmpty = false;
    
    Validator v = f.getValidator();
    for (Iterator iter = this.indexes.entrySet().iterator(); iter.hasNext();)
    {
      Map.Entry entry = (Map.Entry)iter.next();
      BindingList key = (BindingList)entry.getKey();
      if (key.getNumFree() != 0)
      {
        Index index = (Index)entry.getValue();
        index.addFact(IndexValue.make(v, f.getArgs()));
      }
    }
  }
  
  public Compiled basicCompile(PredicateMode mode, CompilationContext context)
  {
    final Index index = getIndex(mode);
    if (mode.getMode().hi.compareTo(Multiplicity.one) <= 0)
    {
      if (mode.getParamModes().getNumFree() != 0) {
        new SemiDetCompiled(mode.getMode())
        {
          public Frame runSemiDet(Object input, RBContext context)
          {
            RBTuple goal = (RBTuple)input;
            
            RBTuple inputPars = index.extractBound(goal);
            RBTuple outputPars = index.extractFree(goal);
            RBTuple retrieved = index.getMatchSingle(inputPars);
            if (retrieved == null) {
              return null;
            }
            return retrieved.unify(outputPars, new Frame());
          }
        };
      }
      new SemiDetCompiled(mode.getMode())
      {
        public Frame runSemiDet(Object input, RBContext context)
        {
          RBTuple goal = (RBTuple)input;
          RBTuple retrieved = index.getMatchSingle(goal);
          if (retrieved == null) {
            return null;
          }
          return new Frame();
        }
      };
    }
    if (mode.getParamModes().getNumFree() != 0) {
      new Compiled(mode.getMode())
      {
        public ElementSource runNonDet(Object input, RBContext context)
        {
          RBTuple goal = (RBTuple)input;
          
          RBTuple inputPars = index.extractBound(goal);
          final RBTuple outputPars = index.extractFree(goal);
          ElementSource matches = index.getMatchElementSource(inputPars);
          matches.map(new Action()
          {
            public Object compute(Object arg)
            {
              RBTuple retrieved = (RBTuple)arg;
              return retrieved.unify(outputPars, new Frame());
            }
          });
        }
      };
    }
    throw new Error("This case should not happen");
  }
  
  public void backup()
  {
    for (Iterator iter = this.indexes.entrySet().iterator(); iter.hasNext();)
    {
      Map.Entry entry = (Map.Entry)iter.next();
      BindingList key = (BindingList)entry.getKey();
      if (key.getNumFree() != 0)
      {
        Index idx = (Index)entry.getValue();
        idx.backup();
      }
    }
  }
}
