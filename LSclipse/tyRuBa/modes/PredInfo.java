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

import java.util.ArrayList;
import java.util.HashMap;
import tyRuBa.engine.PredicateIdentifier;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.factbase.FactBase;
import tyRuBa.engine.factbase.SimpleArrayListFactBase;
import tyRuBa.engine.factbase.hashtable.HashTableFactBase;

public class PredInfo
{
  private QueryEngine engine;
  private PredicateIdentifier predId;
  private TupleType tList;
  private ArrayList predModes;
  private FactBase factbase;
  private boolean isPersistent;
  
  public PredInfo(QueryEngine qe, String predName, TupleType tList, ArrayList predModes, boolean isPersistent)
  {
    this.engine = qe;
    this.predId = new PredicateIdentifier(predName, tList.size());
    this.tList = tList;
    this.predModes = predModes;
    this.isPersistent = isPersistent;
  }
  
  public PredInfo(QueryEngine qe, String predName, TupleType tList, ArrayList predModes)
  {
    this(qe, predName, tList, predModes, false);
  }
  
  public PredInfo(QueryEngine qe, String predName, TupleType tList)
  {
    this(qe, predName, tList, new ArrayList());
  }
  
  public void addPredicateMode(PredicateMode pm)
  {
    this.predModes.add(pm);
  }
  
  public PredicateIdentifier getPredId()
  {
    return this.predId;
  }
  
  public TupleType getTypeList()
  {
    return (TupleType)this.tList.clone(new HashMap());
  }
  
  public int getNumPredicateMode()
  {
    return this.predModes.size();
  }
  
  public PredicateMode getPredicateModeAt(int pos)
  {
    return (PredicateMode)this.predModes.get(pos);
  }
  
  public int hashCode()
  {
    return this.predId.hashCode();
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer(
      this.predId.toString() + this.tList + "\nMODES\n");
    
    int size = this.predModes.size();
    for (int i = 0; i < size; i++) {
      result.append(this.predModes.get(i) + "\n");
    }
    return result.toString();
  }
  
  public FactBase getFactBase()
  {
    if ((this.factbase == null) && ((this.predId.getArity() == 0) || (this.engine == null))) {
      this.factbase = new SimpleArrayListFactBase(this);
    } else if (this.factbase == null) {
      if (this.isPersistent) {
        this.factbase = new HashTableFactBase(this);
      } else {
        this.factbase = new SimpleArrayListFactBase(this);
      }
    }
    return this.factbase;
  }
  
  public QueryEngine getQueryEngine()
  {
    return this.engine;
  }
  
  public boolean isPersistent()
  {
    return this.isPersistent;
  }
  
  public int getArity()
  {
    return getPredId().getArity();
  }
}
