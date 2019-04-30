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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class AbstractPool
  implements Pool
{
  private static Comparator COMP_TRUE = new Comparator()
  {
    public int compare(Object o1, Object o2)
    {
      return 0;
    }
  };
  private static Comparator COMP_EQUAL = new Comparator()
  {
    public int compare(Object o1, Object o2)
    {
      if ((o1 == o2) || ((o1 != null) && (o2 != null) && (o1.equals(o2)))) {
        return 0;
      }
      return System.identityHashCode(o1) < System.identityHashCode(o2) ? 
        -1 : 1;
    }
  };
  private int _min = 0;
  private int _max = 0;
  private int _wait = 0;
  private int _autoReturn = 0;
  
  public AbstractPool() {}
  
  public AbstractPool(int min, int max, int wait, int autoReturn)
  {
    setMinPool(min);
    setMaxPool(max);
    setWait(wait);
    setAutoReturn(autoReturn);
  }
  
  public AbstractPool(Collection c)
  {
    addAll(c);
  }
  
  public int getMaxPool()
  {
    return this._max;
  }
  
  public void setMaxPool(int max)
  {
    if ((max < 0) || (max < this._min)) {
      throw new IllegalArgumentException(String.valueOf(max));
    }
    this._max = max;
    if (this._max > 0)
    {
      int trim = size() + takenMap().size() - this._max;
      if (trim > 0)
      {
        Iterator itr = freeSet().iterator();
        for (int i = 0; (i < trim) && (itr.hasNext()); i++)
        {
          itr.next();
          itr.remove();
        }
      }
    }
  }
  
  public int getMinPool()
  {
    return this._min;
  }
  
  public void setMinPool(int min)
  {
    if ((min < 0) || ((this._max > 0) && (min > this._max))) {
      throw new IllegalArgumentException(String.valueOf(min));
    }
    this._min = min;
  }
  
  public int getWait()
  {
    return this._wait;
  }
  
  public void setWait(int millis)
  {
    if (millis < 0) {
      throw new IllegalArgumentException(String.valueOf(millis));
    }
    this._wait = millis;
  }
  
  public int getAutoReturn()
  {
    return this._autoReturn;
  }
  
  public void setAutoReturn(int millis)
  {
    if (millis < 0) {
      throw new IllegalArgumentException(String.valueOf(millis));
    }
    this._autoReturn = millis;
  }
  
  public Iterator iterator()
  {
    new Iterator()
    {
      private Iterator _itr = AbstractPool.this.freeSet().iterator();
      
      public boolean hasNext()
      {
        return this._itr.hasNext();
      }
      
      public Object next()
      {
        return this._itr.next();
      }
      
      public void remove()
      {
        if (AbstractPool.this.size() + AbstractPool.this.takenMap().size() <= AbstractPool.this._min) {
          throw new IllegalStateException();
        }
        this._itr.remove();
        synchronized (AbstractPool.this)
        {
          AbstractPool.this.notifyAll();
        }
      }
    };
  }
  
  public int size()
  {
    return freeSet().size();
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public boolean contains(Object obj)
  {
    return freeSet().contains(obj);
  }
  
  public boolean containsAll(Collection c)
  {
    return freeSet().containsAll(c);
  }
  
  public Object[] toArray()
  {
    return freeSet().toArray();
  }
  
  public Object[] toArray(Object[] fill)
  {
    return freeSet().toArray(fill);
  }
  
  public boolean add(Object obj)
  {
    if (obj == null) {
      return false;
    }
    Map taken = takenMap();
    boolean removed = takenMap().remove(obj) != null;
    boolean added = ((this._max == 0) || (size() + taken.size() < this._max)) && 
      (freeSet().add(obj));
    if ((removed) || (added)) {
      synchronized (this)
      {
        notifyAll();
      }
    }
    return added;
  }
  
  public boolean addAll(Collection c)
  {
    boolean ret = false;
    for (Iterator itr = c.iterator(); itr.hasNext();) {
      ret = (add(itr.next())) || (ret);
    }
    return ret;
  }
  
  public boolean remove(Object obj)
  {
    if (size() + takenMap().size() <= this._min) {
      return false;
    }
    if (freeSet().remove(obj))
    {
      synchronized (this)
      {
        notifyAll();
      }
      return true;
    }
    return false;
  }
  
  public boolean removeAll(Collection c)
  {
    boolean ret = false;
    for (Iterator itr = c.iterator(); itr.hasNext();) {
      ret = (remove(itr.next())) || (ret);
    }
    return ret;
  }
  
  public boolean retainAll(Collection c)
  {
    Collection remove = new LinkedList();
    for (Iterator itr = freeSet().iterator(); itr.hasNext();)
    {
      Object next = itr.next();
      if (!c.contains(next)) {
        remove.add(next);
      }
    }
    return removeAll(remove);
  }
  
  public void clear()
  {
    freeSet().clear();
    takenMap().clear();
    synchronized (this)
    {
      notifyAll();
    }
  }
  
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Pool)) {
      return false;
    }
    Pool p = (Pool)obj;
    return (p.size() == size()) && (p.containsAll(this));
  }
  
  public int hashCode()
  {
    int sum = 0;
    for (Iterator itr = freeSet().iterator(); itr.hasNext();)
    {
      Object next = itr.next();
      sum += (next == null ? 0 : next.hashCode());
    }
    return sum;
  }
  
  public Object get()
  {
    return get(null, COMP_TRUE);
  }
  
  public Object get(Object match)
  {
    return get(match, COMP_EQUAL);
  }
  
  public Object get(Object match, Comparator comp)
  {
    if ((comp == null) && (match == null)) {
      comp = COMP_TRUE;
    } else if (comp == null) {
      comp = COMP_EQUAL;
    }
    Object obj = find(match, comp);
    if (obj != null) {
      return obj;
    }
    long now = System.currentTimeMillis();
    long end = now + this._wait;
    while (now < end)
    {
      synchronized (this)
      {
        try
        {
          wait(end - now);
        }
        catch (InterruptedException localInterruptedException) {}
      }
      obj = find(match, comp);
      if (obj != null) {
        return obj;
      }
      now = System.currentTimeMillis();
    }
    throw new NoSuchElementException();
  }
  
  public Set takenSet()
  {
    return Collections.unmodifiableSet(takenMap().keySet());
  }
  
  protected Object find(Object match, Comparator comp)
  {
    clean();
    if (size() == 0) {
      return null;
    }
    Map taken = takenMap();
    Object next = null;
    for (Iterator itr = freeSet().iterator(); itr.hasNext();)
    {
      next = itr.next();
      if (comp.compare(match, next) == 0)
      {
        itr.remove();
        taken.put(next, new Long(System.currentTimeMillis()));
        return next;
      }
    }
    return null;
  }
  
  protected void clean()
  {
    if (this._autoReturn > 0)
    {
      Collection back = null;
      long now = System.currentTimeMillis();
      for (Iterator itr = takenMap().entrySet().iterator(); 
            itr.hasNext();)
      {
        Map.Entry entry = (Map.Entry)itr.next();
        if (entry.getKey() == null)
        {
          itr.remove();
        }
        else if (((Long)entry.getValue()).longValue() + this._autoReturn < now)
        {
          if (back == null) {
            back = new LinkedList();
          }
          back.add(entry.getKey());
        }
      }
      if (back != null) {
        addAll(back);
      }
    }
  }
  
  protected abstract Set freeSet();
  
  protected abstract Map takenMap();
}
