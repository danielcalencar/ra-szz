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
package tyRuBa.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class OneMap
  implements Map, Serializable
{
  private Object key = null;
  private Object value = null;
  
  public void clear()
  {
    this.key = null;
    this.value = null;
  }
  
  public int size()
  {
    return (this.key == null) && (this.value == null) ? 0 : 1;
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public boolean containsKey(Object key)
  {
    return ((key == null) && (this.key == null)) || ((key != null) && (key.equals(this.key)));
  }
  
  public boolean containsValue(Object value)
  {
    return ((value == null) && (this.value == null)) || ((value != null) && (value.equals(this.value)));
  }
  
  public Object get(Object key)
  {
    return ((key == null) && (this.key == null)) || (
      (key != null) && (key.equals(this.key))) ? this.value : null;
  }
  
  public Object put(Object key, Object value)
  {
    Object previous = get(key);
    
    this.key = key;
    this.value = value;
    
    return previous;
  }
  
  public Object remove(Object key)
  {
    Object previous = get(key);
    if (containsKey(key))
    {
      this.key = null;
      this.value = null;
    }
    return previous;
  }
  
  public void putAll(Map t)
  {
    Iterator m = t.entrySet().iterator();
    if (m.hasNext())
    {
      Map.Entry e = (Map.Entry)m.next();
      put(e.getKey(), e.getValue());
    }
  }
  
  public Set keySet()
  {
    return new KeySet(null);
  }
  
  private class Entry
    implements Map.Entry
  {
    private Object key;
    private Object value;
    
    public Entry(Object key, Object value)
    {
      this.key = key;
      this.value = value;
    }
    
    public Object getKey()
    {
      return this.key;
    }
    
    public Object getValue()
    {
      return this.value;
    }
    
    public Object setValue(Object value)
    {
      Object previous = this.value;
      this.value = value;
      OneMap.this.value = value;
      
      return previous;
    }
  }
  
  private class KeySet
    implements Set
  {
    private KeySet() {}
    
    public boolean add(Object object)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection c)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object object)
    {
      return OneMap.this.remove(object) != null;
    }
    
    public void removeAll()
    {
      clear();
    }
    
    public boolean retainAll(Collection collection)
    {
      Iterator iterator = collection.iterator();
      while (iterator.hasNext()) {
        if (OneMap.this.containsKey(iterator.next())) {
          return false;
        }
      }
      clear();
      return true;
    }
    
    public boolean removeAll(Collection collection)
    {
      Iterator iterator = collection.iterator();
      while (iterator.hasNext()) {
        if (remove(iterator.next())) {
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return OneMap.this.size();
    }
    
    public boolean isEmpty()
    {
      return OneMap.this.isEmpty();
    }
    
    public void clear()
    {
      OneMap.this.clear();
    }
    
    public Iterator iterator()
    {
      return new SetIterator();
    }
    
    private class SetIterator
      implements Iterator
    {
      private boolean exhausted = OneMap.KeySet.this.isEmpty();
      private boolean cleared = this.exhausted;
      
      public SetIterator() {}
      
      public boolean hasNext()
      {
        return !this.exhausted;
      }
      
      public Object next()
      {
        if (hasNext())
        {
          this.exhausted = true;
          return OneMap.this.key;
        }
        throw new NoSuchElementException();
      }
      
      public void remove()
      {
        if ((this.exhausted) && (!this.cleared))
        {
          this.cleared = true;
          OneMap.this.clear();
        }
        else
        {
          throw new IllegalStateException();
        }
      }
    }
    
    public boolean contains(Object object)
    {
      if (OneMap.this.key == object) {
        return true;
      }
      return false;
    }
    
    public boolean containsAll(Collection collection)
    {
      return (collection.size() == 0) || ((collection.size() == 1) && (OneMap.this.size() == 1) && (contains(collection.iterator().next())));
    }
    
    public Object[] toArray()
    {
      if (OneMap.this.size() == 0) {
        return new Object[0];
      }
      return new Object[] { OneMap.this.key };
    }
    
    public Object[] toArray(Object[] a)
    {
      if (OneMap.this.size() == 0)
      {
        if (a.length > 0) {
          a[0] = null;
        }
        return a;
      }
      if (a.length >= 1)
      {
        a[0] = OneMap.this.key;
        if (a.length > 1) {
          a[1] = null;
        }
        return a;
      }
      Object[] r = (Object[])Array.newInstance(a.getClass(), 1);
      r[0] = OneMap.this.key;
      return r;
    }
  }
  
  public Set entrySet()
  {
    return new EntrySet();
  }
  
  public class EntrySet
    implements Set
  {
    public EntrySet() {}
    
    public boolean add(Object object)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection c)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object object)
    {
      if (contains(object))
      {
        clear();
        return true;
      }
      return false;
    }
    
    public void removeAll()
    {
      clear();
    }
    
    public boolean retainAll(Collection collection)
    {
      Iterator iterator = collection.iterator();
      while (iterator.hasNext()) {
        if (contains(iterator.next())) {
          return false;
        }
      }
      clear();
      return true;
    }
    
    public boolean removeAll(Collection collection)
    {
      Iterator iterator = collection.iterator();
      while (iterator.hasNext()) {
        if (remove(iterator.next())) {
          return true;
        }
      }
      return false;
    }
    
    public int size()
    {
      return OneMap.this.size();
    }
    
    public boolean isEmpty()
    {
      return OneMap.this.isEmpty();
    }
    
    public void clear()
    {
      OneMap.this.clear();
    }
    
    public Iterator iterator()
    {
      return new SetIterator();
    }
    
    private class SetIterator
      implements Iterator
    {
      private boolean exhausted = OneMap.EntrySet.this.isEmpty();
      private boolean cleared = this.exhausted;
      
      public SetIterator() {}
      
      public boolean hasNext()
      {
        return !this.exhausted;
      }
      
      public Object next()
      {
        if (hasNext())
        {
          this.exhausted = true;
          return new OneMap.Entry(OneMap.this, OneMap.this.key, OneMap.this.value);
        }
        throw new NoSuchElementException();
      }
      
      public void remove()
      {
        if ((this.exhausted) && (!this.cleared))
        {
          this.cleared = true;
          OneMap.this.clear();
        }
        else
        {
          throw new IllegalStateException();
        }
      }
    }
    
    public boolean contains(Object object)
    {
      if ((object instanceof OneMap.Entry))
      {
        OneMap.Entry entry = (OneMap.Entry)object;
        if ((OneMap.this.key == entry.getKey()) && (OneMap.this.value == entry.getValue())) {
          return true;
        }
      }
      return false;
    }
    
    public boolean containsAll(Collection collection)
    {
      return (collection.size() == 0) || ((collection.size() == 1) && (OneMap.this.size() == 1) && (contains(collection.iterator().next())));
    }
    
    public Object[] toArray()
    {
      if (isEmpty()) {
        return new Object[0];
      }
      return new Object[] { new OneMap.Entry(OneMap.this, OneMap.this.key, OneMap.this.value) };
    }
    
    public Object[] toArray(Object[] a)
    {
      if (OneMap.this.size() == 0)
      {
        if (a.length > 0) {
          a[0] = null;
        }
        return a;
      }
      if (a.length >= 1)
      {
        a[0] = new OneMap.Entry(OneMap.this, OneMap.this.key, OneMap.this.value);
        if (a.length > 1) {
          a[1] = null;
        }
        return a;
      }
      Object[] r = (Object[])Array.newInstance(a.getClass(), 1);
      r[0] = new OneMap.Entry(OneMap.this, OneMap.this.key, OneMap.this.value);
      return r;
    }
  }
  
  public Collection values()
  {
    return new Values(null);
  }
  
  private class Values
    implements Collection
  {
    private Values() {}
    
    public void clear()
    {
      OneMap.this.clear();
    }
    
    public boolean add(Object object)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection collection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(Object object)
    {
      if (contains(object))
      {
        clear();
        return true;
      }
      return false;
    }
    
    public boolean containsAll(Collection collection)
    {
      return (collection.size() == 0) || ((collection.size() == 1) && (OneMap.this.size() == 1) && (contains(collection.iterator().next())));
    }
    
    public boolean contains(Object object)
    {
      return OneMap.this.containsValue(object);
    }
    
    public boolean isEmpty()
    {
      return OneMap.this.isEmpty();
    }
    
    public int size()
    {
      return OneMap.this.size();
    }
    
    public boolean retainAll(Collection collection)
    {
      Iterator iterator = collection.iterator();
      while (iterator.hasNext()) {
        if (contains(iterator.next())) {
          return false;
        }
      }
      clear();
      return true;
    }
    
    public boolean removeAll(Collection collection)
    {
      Iterator iterator = collection.iterator();
      while (iterator.hasNext()) {
        if (remove(iterator.next())) {
          return true;
        }
      }
      return false;
    }
    
    public Iterator iterator()
    {
      return new ValueIterator();
    }
    
    private class ValueIterator
      implements Iterator
    {
      private boolean exhausted = OneMap.Values.this.isEmpty();
      private boolean cleared = this.exhausted;
      
      public ValueIterator() {}
      
      public boolean hasNext()
      {
        return !this.exhausted;
      }
      
      public Object next()
      {
        if (hasNext())
        {
          this.exhausted = true;
          return OneMap.this.value;
        }
        throw new NoSuchElementException();
      }
      
      public void remove()
      {
        if ((this.exhausted) && (!this.cleared))
        {
          this.cleared = true;
          OneMap.this.clear();
        }
        else
        {
          throw new IllegalStateException();
        }
      }
    }
    
    public Object[] toArray()
    {
      if (isEmpty()) {
        return new Object[0];
      }
      return new Object[] { OneMap.this.value };
    }
    
    public Object[] toArray(Object[] a)
    {
      if (OneMap.this.size() == 0)
      {
        if (a.length > 0) {
          a[0] = null;
        }
        return a;
      }
      if (a.length >= 1)
      {
        a[0] = OneMap.this.value;
        if (a.length > 1) {
          a[1] = null;
        }
        return a;
      }
      Object[] r = (Object[])Array.newInstance(a.getClass(), 1);
      r[0] = OneMap.this.value;
      return r;
    }
  }
}
