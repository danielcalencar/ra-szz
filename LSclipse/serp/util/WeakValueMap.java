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

public class WeakValueMap
  extends RefValueMap
{
  public WeakValueMap() {}
  
  public WeakValueMap(Map map)
  {
    super(map);
  }
  
  protected RefValueMap.RefMapValue createRefMapValue(Object key, Object value, ReferenceQueue queue)
  {
    return new WeakMapValue(key, value, queue);
  }
  
  private static final class WeakMapValue
    extends WeakReference
    implements RefValueMap.RefMapValue
  {
    private Object _key = null;
    private boolean _valid = true;
    
    public WeakMapValue(Object key, Object value, ReferenceQueue queue)
    {
      super(queue);
      this._key = key;
    }
    
    public Object getKey()
    {
      return this._key;
    }
    
    public Object getValue()
    {
      return get();
    }
    
    public boolean isValid()
    {
      return this._valid;
    }
    
    public void invalidate()
    {
      this._valid = false;
    }
    
    public boolean equals(Object other)
    {
      if (this == other) {
        return true;
      }
      if (other == null) {
        return false;
      }
      if (!(other instanceof WeakMapValue)) {
        return (get() != null) && (get().equals(other));
      }
      return (get() != null) && (get().equals(
        ((WeakMapValue)other).get()));
    }
  }
}
