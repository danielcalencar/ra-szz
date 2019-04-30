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

abstract class RefKeyMap
  implements RefMap
{
  private Map _map = null;
  private ReferenceQueue _queue = new ReferenceQueue();
  private boolean _identity = false;
  
  public RefKeyMap()
  {
    this(new HashMap());
  }
  
  public RefKeyMap(Map map)
  {
    if ((map instanceof IdentityMap))
    {
      this._identity = true;
      this._map = new HashMap();
    }
    else
    {
      this._map = map;
      this._map.clear();
    }
  }
  
  public boolean makeHard(Object key)
  {
    removeExpired();
    if (!containsKey(key)) {
      return false;
    }
    Object value = remove(key);
    this._map.put(key, value);
    return true;
  }
  
  public boolean makeReference(Object key)
  {
    removeExpired();
    if (key == null) {
      return false;
    }
    if (!containsKey(key)) {
      return false;
    }
    Object value = remove(key);
    put(key, value);
    return true;
  }
  
  public void clear()
  {
    this._map.clear();
  }
  
  public boolean containsKey(Object key)
  {
    if (key == null) {
      return this._map.containsKey(null);
    }
    return this._map.containsKey(createRefMapKey(key, null, this._identity));
  }
  
  public boolean containsValue(Object value)
  {
    return this._map.containsValue(value);
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
    if (key == null) {
      return this._map.get(null);
    }
    return this._map.get(createRefMapKey(key, null, this._identity));
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
    return putFilter(key, value);
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
    if (key == null) {
      return this._map.put(null, value);
    }
    key = createRefMapKey(key, this._queue, this._identity);
    Object ret = this._map.remove(key);
    this._map.put(key, value);
    return ret;
  }
  
  public Object remove(Object key)
  {
    removeExpired();
    if (key == null) {
      return this._map.remove(null);
    }
    return this._map.remove(createRefMapKey(key, null, this._identity));
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
  
  protected abstract RefMapKey createRefMapKey(Object paramObject, ReferenceQueue paramReferenceQueue, boolean paramBoolean);
  
  private void removeExpired()
  {
    Object key;
    while ((key = this._queue.poll()) != null)
    {
      Object key;
      try
      {
        this._queue.remove(1L);
      }
      catch (InterruptedException localInterruptedException) {}
      this._map.remove(key);
    }
  }
  
  private static final class MapEntry
    implements Map.Entry
  {
    Map.Entry _entry = null;
    
    public MapEntry(Map.Entry entry)
    {
      this._entry = entry;
    }
    
    public Object getKey()
    {
      Object key = this._entry.getKey();
      if (!(key instanceof RefKeyMap.RefMapKey)) {
        return key;
      }
      return ((RefKeyMap.RefMapKey)key).getKey();
    }
    
    public Object getValue()
    {
      return this._entry.getValue();
    }
    
    public Object setValue(Object value)
    {
      return this._entry.setValue(value);
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
      return RefKeyMap.this.size();
    }
    
    public boolean add(Object o)
    {
      Map.Entry entry = (Map.Entry)o;
      RefKeyMap.this.put(entry.getKey(), entry.getValue());
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
        return RefKeyMap.this._map.entrySet().iterator();
      }
      
      protected void processValue(LookaheadIterator.ItrValue value)
      {
        Map.Entry entry = (Map.Entry)value.value;
        if ((entry.getKey() instanceof RefKeyMap.RefMapKey))
        {
          RefKeyMap.RefMapKey ref = (RefKeyMap.RefMapKey)entry.getKey();
          if (ref.getKey() == null) {
            value.valid = false;
          }
        }
        value.value = new RefKeyMap.MapEntry(entry);
      }
    }
  }
  
  private class KeySet
    extends AbstractSet
  {
    private KeySet() {}
    
    public int size()
    {
      return RefKeyMap.this.size();
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
        return RefKeyMap.this._map.keySet().iterator();
      }
      
      protected void processValue(LookaheadIterator.ItrValue value)
      {
        if ((value.value instanceof RefKeyMap.RefMapKey))
        {
          RefKeyMap.RefMapKey ref = (RefKeyMap.RefMapKey)value.value;
          if (ref.getKey() == null) {
            value.valid = false;
          } else {
            value.value = ref.getKey();
          }
        }
      }
    }
  }
  
  private class ValueCollection
    extends AbstractCollection
  {
    private ValueCollection() {}
    
    public int size()
    {
      return RefKeyMap.this.size();
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
        return RefKeyMap.this._map.entrySet().iterator();
      }
      
      protected void processValue(LookaheadIterator.ItrValue value)
      {
        Map.Entry entry = (Map.Entry)value.value;
        if ((entry.getKey() instanceof RefKeyMap.RefMapKey))
        {
          RefKeyMap.RefMapKey ref = (RefKeyMap.RefMapKey)entry.getKey();
          if (ref.getKey() == null) {
            value.valid = false;
          }
        }
        value.value = entry.getValue();
      }
    }
  }
  
  static abstract interface RefMapKey
    extends Comparable
  {
    public abstract Object getKey();
  }
}
