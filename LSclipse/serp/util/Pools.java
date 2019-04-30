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

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class Pools
{
  public static Pool synchronizedPool(Pool pool)
  {
    if (pool == null) {
      throw new NullPointerException();
    }
    return new SynchronizedPool(pool);
  }
  
  private static class SynchronizedPool
    implements Pool, Serializable
  {
    private Pool _pool = null;
    
    public SynchronizedPool(Pool pool)
    {
      this._pool = pool;
    }
    
    public synchronized int getMaxPool()
    {
      return this._pool.getMaxPool();
    }
    
    public synchronized void setMaxPool(int max)
    {
      this._pool.setMaxPool(max);
    }
    
    public synchronized int getMinPool()
    {
      return this._pool.getMinPool();
    }
    
    public synchronized void setMinPool(int min)
    {
      this._pool.setMinPool(min);
    }
    
    public synchronized int getWait()
    {
      return this._pool.getWait();
    }
    
    public synchronized void setWait(int millis)
    {
      this._pool.setWait(millis);
    }
    
    public synchronized int getAutoReturn()
    {
      return this._pool.getAutoReturn();
    }
    
    public synchronized void setAutoReturn(int millis)
    {
      this._pool.setAutoReturn(millis);
    }
    
    public Iterator iterator()
    {
      return this._pool.iterator();
    }
    
    public synchronized int size()
    {
      return this._pool.size();
    }
    
    public synchronized boolean isEmpty()
    {
      return this._pool.isEmpty();
    }
    
    public synchronized boolean contains(Object obj)
    {
      return this._pool.contains(obj);
    }
    
    public synchronized boolean containsAll(Collection c)
    {
      return this._pool.containsAll(c);
    }
    
    public synchronized Object[] toArray()
    {
      return this._pool.toArray();
    }
    
    public synchronized Object[] toArray(Object[] fill)
    {
      return this._pool.toArray(fill);
    }
    
    public synchronized boolean add(Object obj)
    {
      return this._pool.add(obj);
    }
    
    public synchronized boolean addAll(Collection c)
    {
      return this._pool.addAll(c);
    }
    
    public synchronized boolean remove(Object obj)
    {
      return this._pool.remove(obj);
    }
    
    public synchronized boolean removeAll(Collection c)
    {
      return this._pool.removeAll(c);
    }
    
    public synchronized boolean retainAll(Collection c)
    {
      return this._pool.retainAll(c);
    }
    
    public synchronized void clear()
    {
      this._pool.clear();
    }
    
    public synchronized boolean equals(Object obj)
    {
      return this._pool.equals(obj);
    }
    
    public synchronized int hashCode()
    {
      return this._pool.hashCode();
    }
    
    public synchronized Object get()
    {
      return this._pool.get();
    }
    
    public synchronized Object get(Object match)
    {
      return this._pool.get(match);
    }
    
    public synchronized Object get(Object match, Comparator comp)
    {
      return this._pool.get(match, comp);
    }
    
    public synchronized Set takenSet()
    {
      return this._pool.takenSet();
    }
  }
}
