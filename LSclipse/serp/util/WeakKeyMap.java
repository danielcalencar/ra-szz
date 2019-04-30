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
import java.lang.ref.WeakReference;
import java.util.Map;

public class WeakKeyMap
  extends RefKeyMap
{
  public WeakKeyMap() {}
  
  public WeakKeyMap(Map map)
  {
    super(map);
  }
  
  protected RefKeyMap.RefMapKey createRefMapKey(Object key, ReferenceQueue queue, boolean identity)
  {
    if (queue == null) {
      return new WeakMapKey(key, identity);
    }
    return new WeakMapKey(key, queue, identity);
  }
  
  private static final class WeakMapKey
    extends WeakReference
    implements RefKeyMap.RefMapKey
  {
    private boolean _identity = false;
    private int _hash = 0;
    
    public WeakMapKey(Object key, boolean identity)
    {
      super();
      this._identity = identity;
      this._hash = (identity ? System.identityHashCode(key) : key.hashCode());
    }
    
    public WeakMapKey(Object key, ReferenceQueue queue, boolean identity)
    {
      super(queue);
      this._identity = identity;
      this._hash = (identity ? System.identityHashCode(key) : key.hashCode());
    }
    
    public Object getKey()
    {
      return get();
    }
    
    public int hashCode()
    {
      return this._hash;
    }
    
    public boolean equals(Object other)
    {
      if (this == other) {
        return true;
      }
      if ((other instanceof RefKeyMap.RefMapKey)) {
        other = ((RefKeyMap.RefMapKey)other).getKey();
      }
      Object obj = get();
      if (obj == null) {
        return false;
      }
      if (this._identity) {
        return obj == other;
      }
      return obj.equals(other);
    }
    
    public int compareTo(Object other)
    {
      if (this == other) {
        return 0;
      }
      Object key = getKey();
      Object otherKey;
      Object otherKey;
      if ((other instanceof RefKeyMap.RefMapKey)) {
        otherKey = ((RefKeyMap.RefMapKey)other).getKey();
      } else {
        otherKey = other;
      }
      if (key == null) {
        return -1;
      }
      if (otherKey == null) {
        return 1;
      }
      if (!(key instanceof Comparable)) {
        return System.identityHashCode(otherKey) - 
          System.identityHashCode(key);
      }
      return ((Comparable)key).compareTo(otherKey);
    }
  }
}
