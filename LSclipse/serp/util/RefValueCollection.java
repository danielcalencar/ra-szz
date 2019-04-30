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
package serp.util;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

abstract class RefValueCollection
  implements RefCollection
{
  private Collection _coll = null;
  private ReferenceQueue _queue = new ReferenceQueue();
  private boolean _identity = false;
  
  public RefValueCollection()
  {
    this(new LinkedList());
  }
  
  public RefValueCollection(Collection coll)
  {
    if (((coll instanceof MapSet)) && (((MapSet)coll).isIdentity()))
    {
      this._identity = true;
      this._coll = new HashSet();
    }
    else
    {
      this._coll = coll;
      this._coll.clear();
    }
  }
  
  public boolean makeHard(Object obj)
  {
    removeExpired();
    if ((this._coll instanceof List))
    {
      for (ListIterator li = ((List)this._coll).listIterator(); li.hasNext();)
      {
        Object value = li.next();
        if (equal(obj, value))
        {
          li.set(obj);
          return true;
        }
      }
    }
    else if (remove(obj))
    {
      this._coll.add(obj);
      return true;
    }
    return false;
  }
  
  public boolean makeReference(Object obj)
  {
    removeExpired();
    if (obj == null) {
      return false;
    }
    if ((this._coll instanceof List)) {
      for (ListIterator li = ((List)this._coll).listIterator(); li.hasNext();)
      {
        Object value = li.next();
        if (equal(obj, value))
        {
          li.set(createRefValue(obj, this._queue, this._identity));
          return true;
        }
      }
    } else {
      for (Iterator itr = this._coll.iterator(); itr.hasNext();)
      {
        Object value = itr.next();
        if (equal(obj, value))
        {
          itr.remove();
          add(obj);
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean equal(Object obj, Object value)
  {
    if ((value instanceof RefValue)) {
      value = ((RefValue)value).getValue();
    }
    return ((this._identity) || (obj == null)) && ((obj == value) || ((!this._identity) && (obj != null) && (obj.equals(value))));
  }
  
  public boolean add(Object obj)
  {
    removeExpired();
    return addFilter(obj);
  }
  
  public boolean addAll(Collection objs)
  {
    removeExpired();
    
    boolean added = false;
    for (Iterator itr = objs.iterator(); itr.hasNext();) {
      added = (added) || (addFilter(itr.next()));
    }
    return added;
  }
  
  private boolean addFilter(Object obj)
  {
    if (obj == null) {
      return this._coll.add(null);
    }
    return this._coll.add(createRefValue(obj, this._queue, this._identity));
  }
  
  public void clear()
  {
    this._coll.clear();
  }
  
  public boolean contains(Object obj)
  {
    if (obj == null) {
      return this._coll.contains(null);
    }
    return this._coll.contains(createRefValue(obj, null, this._identity));
  }
  
  public boolean containsAll(Collection objs)
  {
    boolean contains = true;
    for (Iterator itr = objs.iterator(); (contains) && (itr.hasNext());) {
      contains = contains(itr.next());
    }
    return contains;
  }
  
  public boolean equals(Object other)
  {
    return this._coll.equals(other);
  }
  
  public boolean isEmpty()
  {
    return this._coll.isEmpty();
  }
  
  public boolean remove(Object obj)
  {
    removeExpired();
    return removeFilter(obj);
  }
  
  public boolean removeAll(Collection objs)
  {
    removeExpired();
    
    boolean removed = false;
    for (Iterator itr = objs.iterator(); itr.hasNext();) {
      removed = (removed) || (removeFilter(itr.next()));
    }
    return removed;
  }
  
  public boolean retainAll(Collection objs)
  {
    removeExpired();
    
    boolean removed = false;
    for (Iterator itr = iterator(); itr.hasNext();) {
      if (!objs.contains(itr.next()))
      {
        itr.remove();
        removed = true;
      }
    }
    return removed;
  }
  
  private boolean removeFilter(Object obj)
  {
    if (obj == null) {
      return this._coll.remove(null);
    }
    return this._coll.remove(createRefValue(obj, null, this._identity));
  }
  
  public int size()
  {
    return this._coll.size();
  }
  
  public Object[] toArray()
  {
    ArrayList list = new ArrayList(size());
    for (Iterator itr = iterator(); itr.hasNext();) {
      list.add(itr.next());
    }
    return list.toArray();
  }
  
  public Object[] toArray(Object[] a)
  {
    ArrayList list = new ArrayList(size());
    for (Iterator itr = iterator(); itr.hasNext();) {
      list.add(itr.next());
    }
    return list.toArray(a);
  }
  
  public Iterator iterator()
  {
    return new ValuesIterator(null);
  }
  
  protected abstract RefValue createRefValue(Object paramObject, ReferenceQueue paramReferenceQueue, boolean paramBoolean);
  
  private void removeExpired()
  {
    Object value;
    while ((value = this._queue.poll()) != null)
    {
      Object value;
      try
      {
        this._queue.remove(1L);
      }
      catch (InterruptedException localInterruptedException) {}
      this._coll.remove(value);
    }
  }
  
  static abstract interface RefValue
  {
    public abstract Object getValue();
  }
  
  private class ValuesIterator
    extends LookaheadIterator
  {
    private ValuesIterator() {}
    
    protected Iterator newIterator()
    {
      return RefValueCollection.this._coll.iterator();
    }
    
    protected void processValue(LookaheadIterator.ItrValue value)
    {
      if ((value.value instanceof RefValueCollection.RefValue))
      {
        RefValueCollection.RefValue ref = (RefValueCollection.RefValue)value.value;
        if (ref.getValue() == null) {
          value.valid = false;
        } else {
          value.value = ref.getValue();
        }
      }
    }
  }
}
