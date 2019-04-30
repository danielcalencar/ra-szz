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

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public abstract interface Pool
  extends Set
{
  public abstract int getMaxPool();
  
  public abstract void setMaxPool(int paramInt);
  
  public abstract int getMinPool();
  
  public abstract void setMinPool(int paramInt);
  
  public abstract int getWait();
  
  public abstract void setWait(int paramInt);
  
  public abstract int getAutoReturn();
  
  public abstract void setAutoReturn(int paramInt);
  
  public abstract Iterator iterator();
  
  public abstract Object get();
  
  public abstract Object get(Object paramObject);
  
  public abstract Object get(Object paramObject, Comparator paramComparator);
  
  public abstract Set takenSet();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
}
