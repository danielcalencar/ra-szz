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

public class HandleTable
  implements Serializable
{
  private static final int DEFAULTSIZE = 32;
  private static final double GROWTHFACTOR = 1.33D;
  private int freeHead;
  private int[] free;
  private int[] update;
  private Object[] references;
  
  public HandleTable()
  {
    this.freeHead = 1;
    
    this.free = new int[32];
    this.update = new int[32];
    this.references = new Object[32];
    for (int i = 1; i < 32; i++) {
      this.free[i] = (i + 1);
    }
  }
  
  public long add(Object reference)
  {
    if (this.freeHead != this.references.length)
    {
      long indirection = (this.update[this.freeHead] << 32) + this.freeHead;
      
      this.references[this.freeHead] = reference;
      
      this.freeHead = this.free[this.freeHead];
      
      return indirection;
    }
    int[] tmpFree = new int[(int)(this.free.length * 1.33D)];
    int[] tmpUpdate = new int[(int)(this.update.length * 1.33D)];
    Object[] tmpReferences = new Object[(int)(this.references.length * 1.33D)];
    
    System.arraycopy(this.free, 0, tmpFree, 0, this.free.length);
    System.arraycopy(this.update, 0, tmpUpdate, 0, this.update.length);
    System.arraycopy(this.references, 0, tmpReferences, 0, this.references.length);
    for (int i = this.free.length; i < tmpFree.length; i++) {
      tmpFree[i] = (i + 1);
    }
    this.free = tmpFree;
    this.update = tmpUpdate;
    this.references = tmpReferences;
    
    return add(reference);
  }
  
  public Object get(long handle)
  {
    int index = (int)(handle & 0xFFFFFFFFFFFFFFFF);
    if (this.update[index] == handle >> 32) {
      return this.references[index];
    }
    return null;
  }
  
  public void remove(long handle)
  {
    int index = (int)(handle & 0xFFFFFFFFFFFFFFFF);
    if (this.update[index] == handle >> 32)
    {
      this.free[index] = this.freeHead;
      this.update[index] += 1;
      this.references[index] = null;
      this.freeHead = index;
    }
  }
}
