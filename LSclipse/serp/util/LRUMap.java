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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class LRUMap
  implements SortedMap
{
  private Map _orders = new HashMap();
  private TreeMap _values = new TreeMap();
  private int _order = Integer.MAX_VALUE;
  
  public Comparator comparator()
  {
    return null;
  }
  
  public Object firstKey()
  {
    return ((OrderKey)this._values.firstKey()).key;
  }
  
  public Object lastKey()
  {
    return ((OrderKey)this._values.lastKey()).key;
  }
  
  public SortedMap headMap(Object toKey)
  {
    throw new UnsupportedOperationException();
  }
  
  public SortedMap subMap(Object fromKey, Object toKey)
  {
    throw new UnsupportedOperationException();
  }
  
  public SortedMap tailMap(Object fromKey)
  {
    throw new UnsupportedOperationException();
  }
  
  public void clear()
  {
    this._orders.clear();
    this._values.clear();
  }
  
  public boolean containsKey(Object key)
  {
    return this._orders.containsKey(key);
  }
  
  public boolean containsValue(Object value)
  {
    return this._values.containsValue(value);
  }
  
  public Set entrySet()
  {
    return new EntrySet(null);
  }
  
  public boolean equals(Object other)
  {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Map)) {
      return false;
    }
    return new HashMap(this).equals(other);
  }
  
  public Object get(Object key)
  {
    Object order = this._orders.remove(key);
    if (order == null) {
      return null;
    }
    Object value = this._values.remove(order);
    order = nextOrderKey(key);
    this._orders.put(key, order);
    this._values.put(order, value);
    
    return value;
  }
  
  public boolean isEmpty()
  {
    return this._orders.isEmpty();
  }
  
  public Set keySet()
  {
    return new KeySet(null);
  }
  
  public Object put(Object key, Object value)
  {
    Object order = nextOrderKey(key);
    Object oldOrder = this._orders.put(key, order);
    
    Object rem = null;
    if (oldOrder != null) {
      rem = this._values.remove(oldOrder);
    }
    this._values.put(order, value);
    return rem;
  }
  
  public void putAll(Map map)
  {
    for (Iterator itr = map.entrySet().iterator(); itr.hasNext();)
    {
      Map.Entry entry = (Map.Entry)itr.next();
      put(entry.getKey(), entry.getValue());
    }
  }
  
  public Object remove(Object key)
  {
    Object order = this._orders.remove(key);
    if (order != null) {
      return this._values.remove(order);
    }
    return null;
  }
  
  public int size()
  {
    return this._orders.size();
  }
  
  public Collection values()
  {
    return new ValueCollection(null);
  }
  
  public String toString()
  {
    return entrySet().toString();
  }
  
  private synchronized OrderKey nextOrderKey(Object key)
  {
    OrderKey ok = new OrderKey(null);
    ok.key = key;
    ok.order = (this._order--);
    return ok;
  }
  
  private static final class OrderKey
    implements Comparable
  {
    public Object key = null;
    public int order = 0;
    
    public int compareTo(Object other)
    {
      return this.order - ((OrderKey)other).order;
    }
  }
  
  private static final class MapEntry
    implements Map.Entry
  {
    private Map.Entry _valuesEntry = null;
    
    public MapEntry(Map.Entry valuesEntry)
    {
      this._valuesEntry = valuesEntry;
    }
    
    public Object getKey()
    {
      LRUMap.OrderKey ok = (LRUMap.OrderKey)this._valuesEntry.getKey();
      return ok.key;
    }
    
    public Object getValue()
    {
      return this._valuesEntry.getValue();
    }
    
    public Object setValue(Object value)
    {
      return this._valuesEntry.setValue(value);
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
      return LRUMap.this.size();
    }
    
    public boolean add(Object o)
    {
      Map.Entry entry = (Map.Entry)o;
      LRUMap.this.put(entry.getKey(), entry.getValue());
      return true;
    }
    
    public Iterator iterator()
    {
      final Iterator valuesItr = LRUMap.this._values.entrySet().iterator();
      
      new Iterator()
      {
        private LRUMap.MapEntry _last = null;
        
        public boolean hasNext()
        {
          return valuesItr.hasNext();
        }
        
        public Object next()
        {
          this._last = new LRUMap.MapEntry((Map.Entry)valuesItr.next());
          return this._last;
        }
        
        public void remove()
        {
          valuesItr.remove();
          LRUMap.this._orders.remove(this._last.getKey());
        }
      };
    }
  }
  
  private class KeySet
    extends AbstractSet
  {
    private KeySet() {}
    
    public int size()
    {
      return LRUMap.this.size();
    }
    
    public Iterator iterator()
    {
      final Iterator keysItr = LRUMap.this._values.keySet().iterator();
      
      new Iterator()
      {
        private Object _last = null;
        
        public boolean hasNext()
        {
          return keysItr.hasNext();
        }
        
        public Object next()
        {
          this._last = ((LRUMap.OrderKey)keysItr.next()).key;
          return this._last;
        }
        
        public void remove()
        {
          keysItr.remove();
          LRUMap.this._orders.remove(this._last);
        }
      };
    }
  }
  
  private class ValueCollection
    extends AbstractCollection
  {
    private ValueCollection() {}
    
    public int size()
    {
      return LRUMap.this.size();
    }
    
    public Iterator iterator()
    {
      final Iterator valuesItr = LRUMap.this._values.entrySet().iterator();
      
      new Iterator()
      {
        private Object _last = null;
        
        public boolean hasNext()
        {
          return valuesItr.hasNext();
        }
        
        public Object next()
        {
          Map.Entry entry = (Map.Entry)valuesItr.next();
          this._last = ((LRUMap.OrderKey)entry.getKey()).key;
          return entry.getValue();
        }
        
        public void remove()
        {
          valuesItr.remove();
          LRUMap.this._orders.remove(this._last);
        }
      };
    }
  }
}
