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

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class IdentityMap
  extends HashMap
{
  public IdentityMap() {}
  
  public IdentityMap(int initialCapacity)
  {
    super(initialCapacity);
  }
  
  public IdentityMap(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }
  
  public IdentityMap(Map map)
  {
    putAll(map);
  }
  
  public Object clone()
  {
    return new IdentityMap(this);
  }
  
  public boolean containsKey(Object key)
  {
    return super.containsKey(createKey(key));
  }
  
  public Set entrySet()
  {
    return new EntrySet(super.entrySet());
  }
  
  public Object get(Object key)
  {
    return super.get(createKey(key));
  }
  
  public Set keySet()
  {
    return new KeySet(super.keySet());
  }
  
  public Object put(Object key, Object value)
  {
    return super.put(createKey(key), value);
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
    return super.remove(createKey(key));
  }
  
  private static Object createKey(Object key)
  {
    if (key == null) {
      return key;
    }
    return new IdentityKey(key);
  }
  
  private static class IdentityKey
  {
    private Object _key = null;
    
    public IdentityKey(Object key)
    {
      this._key = key;
    }
    
    public Object getKey()
    {
      return this._key;
    }
    
    public int hashCode()
    {
      return System.identityHashCode(this._key);
    }
    
    public boolean equals(Object other)
    {
      if (this == other) {
        return true;
      }
      if (!(other instanceof IdentityKey)) {
        return false;
      }
      return ((IdentityKey)other).getKey() == this._key;
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
      IdentityMap.IdentityKey key = (IdentityMap.IdentityKey)this._entry.getKey();
      if (key == null) {
        return null;
      }
      return key.getKey();
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
    private Set _entrySet = null;
    
    public EntrySet(Set entrySet)
    {
      this._entrySet = entrySet;
    }
    
    public int size()
    {
      return this._entrySet.size();
    }
    
    public boolean add(Object o)
    {
      Map.Entry entry = (Map.Entry)o;
      return this._entrySet.add(new IdentityMap.MapEntry(entry));
    }
    
    public Iterator iterator()
    {
      new Iterator()
      {
        Iterator _itr = IdentityMap.EntrySet.this._entrySet.iterator();
        
        public boolean hasNext()
        {
          return this._itr.hasNext();
        }
        
        public Object next()
        {
          return new IdentityMap.MapEntry((Map.Entry)this._itr.next());
        }
        
        public void remove()
        {
          this._itr.remove();
        }
      };
    }
  }
  
  private class KeySet
    extends AbstractSet
  {
    private Set _keySet = null;
    
    public KeySet(Set keySet)
    {
      this._keySet = keySet;
    }
    
    public int size()
    {
      return this._keySet.size();
    }
    
    public boolean remove(Object rem)
    {
      for (Iterator itr = this._keySet.iterator(); itr.hasNext();) {
        if (((IdentityMap.IdentityKey)itr.next()).getKey() == rem)
        {
          itr.remove();
          return true;
        }
      }
      return false;
    }
    
    public Iterator iterator()
    {
      new Iterator()
      {
        private Iterator _itr = IdentityMap.KeySet.this._keySet.iterator();
        
        public boolean hasNext()
        {
          return this._itr.hasNext();
        }
        
        public Object next()
        {
          IdentityMap.IdentityKey key = (IdentityMap.IdentityKey)this._itr.next();
          if (key == null) {
            return null;
          }
          return key.getKey();
        }
        
        public void remove()
        {
          this._itr.remove();
        }
      };
    }
  }
}
