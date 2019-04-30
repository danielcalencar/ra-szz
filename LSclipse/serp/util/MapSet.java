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
import java.util.Set;

public class MapSet
  extends AbstractSet
{
  Map _map = null;
  
  public MapSet()
  {
    this(new HashMap());
  }
  
  public MapSet(Map map)
  {
    this._map = map;
    this._map.clear();
  }
  
  public int size()
  {
    return this._map.size();
  }
  
  public boolean add(Object obj)
  {
    if (this._map.containsKey(obj)) {
      return false;
    }
    this._map.put(obj, null);
    return true;
  }
  
  public boolean remove(Object obj)
  {
    boolean contained = this._map.containsKey(obj);
    this._map.remove(obj);
    return contained;
  }
  
  public boolean contains(Object obj)
  {
    return this._map.containsKey(obj);
  }
  
  public Iterator iterator()
  {
    return this._map.keySet().iterator();
  }
  
  boolean isIdentity()
  {
    return this._map instanceof IdentityMap;
  }
}
