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
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class RefValueMap
  implements RefMap
{
  private Map _map = null;
  private ReferenceQueue _queue = new ReferenceQueue();
  
  public RefValueMap()
  {
    this(new HashMap());
  }
  
  public RefValueMap(Map map)
  {
    this._map = map;
    this._map.clear();
  }
  
  public boolean makeHard(Object key)
  {
    removeExpired();
    if (!containsKey(key)) {
      return false;
    }
    Object value = this._map.get(key);
    if ((value instanceof RefMapValue))
    {
      RefMapValue ref = (RefMapValue)value;
      value = ref.getValue();
      if (value == null) {
        return false;
      }
      ref.invalidate();
      this._map.put(key, value);
    }
    return true;
  }
  
  public boolean makeReference(Object key)
  {
    removeExpired();
    
    Object value = this._map.get(key);
    if (value == null) {
      return false;
    }
    if (!(value instanceof RefMapValue)) {
      put(key, value);
    }
    return true;
  }
  
  public void clear()
  {
    Collection values = this._map.values();
    for (Iterator itr = values.iterator(); itr.hasNext();)
    {
      Object value = itr.next();
      if ((value instanceof RefMapValue)) {
        ((RefMapValue)value).invalidate();
      }
      itr.remove();
    }
  }
  
  public boolean containsKey(Object key)
  {
    return this._map.containsKey(key);
  }
  
  public boolean containsValue(Object value)
  {
    return values().contains(value);
  }
  
  public Set entrySet()
  {
    return new EntrySet(null);
  }
  
  public boolean equals(Object other)
  {
    return this._map.equals(other);
  }
  
  public Object get(Object key)
  {
    Object value = this._map.get(key);
    if (!(value instanceof RefMapValue)) {
      return value;
    }
    return ((RefMapValue)value).getValue();
  }
  
  public boolean isEmpty()
  {
    return this._map.isEmpty();
  }
  
  public Set keySet()
  {
    return new KeySet(null);
  }
  
  public Object put(Object key, Object value)
  {
    removeExpired();
    
    Object replaced = putFilter(key, value);
    if (!(replaced instanceof RefMapValue)) {
      return replaced;
    }
    return ((RefMapValue)replaced).getValue();
  }
  
  public void putAll(Map map)
  {
    removeExpired();
    for (Iterator itr = map.entrySet().iterator(); itr.hasNext();)
    {
      Map.Entry entry = (Map.Entry)itr.next();
      putFilter(entry.getKey(), entry.getValue());
    }
  }
  
  private Object putFilter(Object key, Object value)
  {
    Object replaced;
    Object replaced;
    if (value == null) {
      replaced = this._map.put(key, null);
    } else {
      replaced = this._map.put(key, createRefMapValue(key, value, this._queue));
    }
    if ((replaced instanceof RefMapValue)) {
      ((RefMapValue)replaced).invalidate();
    }
    return replaced;
  }
  
  public Object remove(Object key)
  {
    removeExpired();
    
    Object value = this._map.remove(key);
    if (!(value instanceof RefMapValue)) {
      return value;
    }
    RefMapValue ref = (RefMapValue)value;
    ref.invalidate();
    return ref.getValue();
  }
  
  public int size()
  {
    return this._map.size();
  }
  
  public Collection values()
  {
    return new ValueCollection(null);
  }
  
  public String toString()
  {
    return this._map.toString();
  }
  
  protected abstract RefMapValue createRefMapValue(Object paramObject1, Object paramObject2, ReferenceQueue paramReferenceQueue);
  
  private void removeExpired()
  {
    RefMapValue ref;
    while ((ref = (RefMapValue)this._queue.poll()) != null)
    {
      RefMapValue ref;
      try
      {
        this._queue.remove(1L);
      }
      catch (InterruptedException localInterruptedException) {}
      if (ref.isValid()) {
        this._map.remove(ref.getKey());
      }
    }
  }
  
  private class MapEntry
    implements Map.Entry
  {
    Map.Entry _entry = null;
    
    public MapEntry(Map.Entry entry)
    {
      this._entry = entry;
    }
    
    public Object getKey()
    {
      return this._entry.getKey();
    }
    
    public Object getValue()
    {
      Object value = this._entry.getValue();
      if (!(value instanceof RefValueMap.RefMapValue)) {
        return value;
      }
      return ((RefValueMap.RefMapValue)value).getValue();
    }
    
    public Object setValue(Object value)
    {
      Object ret = this._entry.getValue();
      if (value == null) {
        this._entry.setValue(null);
      } else {
        this._entry.setValue(RefValueMap.this.createRefMapValue(this._entry.getKey(), 
          value, RefValueMap.this._queue));
      }
      if (!(ret instanceof RefValueMap.RefMapValue)) {
        return ret;
      }
      RefValueMap.RefMapValue ref = (RefValueMap.RefMapValue)ret;
      ref.invalidate();
      return ref.getValue();
    }
    
    public boolean equals(Object other)
    {
      if (other == this) {
        return true;
      }
      if (!(other instanceof Map.Entry)) {
        return false;
      }
      Object key = getKey();
      Object key2 = ((Map.Entry)other).getKey();
      if (((key == null) && (key2 != null)) || (
        (key != null) && (!key.equals(key2)))) {
        return false;
      }
      Object val = getValue();
      Object val2 = ((Map.Entry)other).getValue();
      
      return ((val == null) && (val2 == null)) || ((val != null) && (val2.equals(val2)));
    }
  }
  
  private class EntrySet
    extends AbstractSet
  {
    private EntrySet() {}
    
    public int size()
    {
      return RefValueMap.this.size();
    }
    
    public boolean add(Object o)
    {
      Map.Entry entry = (Map.Entry)o;
      RefValueMap.this.put(entry.getKey(), entry.getValue());
      return true;
    }
    
    public Iterator iterator()
    {
      return new EntryIterator(null);
    }
    
    private class EntryIterator
      extends LookaheadIterator
    {
      private EntryIterator() {}
      
      protected Iterator newIterator()
      {
        return RefValueMap.this._map.entrySet().iterator();
      }
      
      protected void processValue(LookaheadIterator.ItrValue value)
      {
        Map.Entry entry = (Map.Entry)value.value;
        if ((entry.getValue() instanceof RefValueMap.RefMapValue))
        {
          RefValueMap.RefMapValue ref = (RefValueMap.RefMapValue)entry.getValue();
          if (ref.getValue() == null) {
            value.valid = false;
          }
        }
        value.value = new RefValueMap.MapEntry(RefValueMap.this, entry);
      }
    }
  }
  
  private class ValueCollection
    extends AbstractCollection
  {
    private ValueCollection() {}
    
    public int size()
    {
      return RefValueMap.this.size();
    }
    
    public Iterator iterator()
    {
      return new ValueIterator(null);
    }
    
    private class ValueIterator
      extends LookaheadIterator
    {
      private ValueIterator() {}
      
      protected Iterator newIterator()
      {
        return RefValueMap.this._map.values().iterator();
      }
      
      protected void processValue(LookaheadIterator.ItrValue value)
      {
        if ((value.value instanceof RefValueMap.RefMapValue))
        {
          RefValueMap.RefMapValue ref = (RefValueMap.RefMapValue)value.value;
          if (ref.getValue() == null) {
            value.valid = false;
          } else {
            value.value = ref.getValue();
          }
        }
      }
    }
  }
  
  private class KeySet
    extends AbstractSet
  {
    private KeySet() {}
    
    public int size()
    {
      return RefValueMap.this.size();
    }
    
    public Iterator iterator()
    {
      return new KeyIterator(null);
    }
    
    private class KeyIterator
      extends LookaheadIterator
    {
      private KeyIterator() {}
      
      protected Iterator newIterator()
      {
        return RefValueMap.this._map.entrySet().iterator();
      }
      
      protected void processValue(LookaheadIterator.ItrValue value)
      {
        Map.Entry entry = (Map.Entry)value.value;
        if ((entry.getValue() instanceof RefValueMap.RefMapValue))
        {
          RefValueMap.RefMapValue ref = (RefValueMap.RefMapValue)entry.getValue();
          if (ref.getValue() == null) {
            value.valid = false;
          }
        }
        value.value = entry.getKey();
      }
    }
  }
  
  static abstract interface RefMapValue
  {
    public abstract Object getKey();
    
    public abstract Object getValue();
    
    public abstract boolean isValid();
    
    public abstract void invalidate();
  }
}
