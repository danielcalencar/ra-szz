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
import java.util.Collection;

public class WeakCollection
  extends RefValueCollection
{
  public WeakCollection() {}
  
  public WeakCollection(Collection coll)
  {
    super(coll);
  }
  
  protected RefValueCollection.RefValue createRefValue(Object value, ReferenceQueue queue, boolean identity)
  {
    if (queue == null) {
      return new WeakValue(value, identity);
    }
    return new WeakValue(value, queue, identity);
  }
  
  private static final class WeakValue
    extends WeakReference
    implements RefValueCollection.RefValue
  {
    private boolean _identity = false;
    
    public WeakValue(Object value, boolean identity)
    {
      super();
      this._identity = identity;
    }
    
    public WeakValue(Object value, ReferenceQueue queue, boolean identity)
    {
      super(queue);
      this._identity = identity;
    }
    
    public Object getValue()
    {
      return get();
    }
    
    public int hashCode()
    {
      Object obj = get();
      if (obj == null) {
        return 0;
      }
      if (this._identity) {
        return System.identityHashCode(obj);
      }
      return obj.hashCode();
    }
    
    public boolean equals(Object other)
    {
      if (this == other) {
        return true;
      }
      if ((other instanceof RefValueCollection.RefValue)) {
        other = ((RefValueCollection.RefValue)other).getValue();
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
      Object value = getValue();
      Object otherValue;
      Object otherValue;
      if ((other instanceof RefValueCollection.RefValue)) {
        otherValue = ((RefValueCollection.RefValue)other).getValue();
      } else {
        otherValue = other;
      }
      if ((value == null) && (otherValue == null)) {
        return 0;
      }
      if ((value == null) && (otherValue != null)) {
        return -1;
      }
      if (otherValue == null) {
        return 1;
      }
      if (!(value instanceof Comparable)) {
        return System.identityHashCode(otherValue) - 
          System.identityHashCode(value);
      }
      return ((Comparable)value).compareTo(otherValue);
    }
  }
}
