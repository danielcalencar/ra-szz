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

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class LookaheadIterator
  implements Iterator
{
  private Iterator _itr = null;
  private ItrValue _next = new ItrValue();
  private int _last = -1;
  private int _index = -1;
  
  public Iterator getIterator()
  {
    initialize();
    return this._itr;
  }
  
  public boolean hasNext()
  {
    initialize();
    return this._next.valid;
  }
  
  public Object next()
  {
    initialize();
    if (!this._next.valid) {
      throw new NoSuchElementException();
    }
    Object next = this._next.value;
    setNext();
    return next;
  }
  
  public void remove()
  {
    initialize();
    
    Iterator itr = newIterator();
    for (int i = 0; i <= this._last; i++) {
      itr.next();
    }
    itr.remove();
    
    this._index = (this._last - 1);
    this._itr = itr;
    setNext();
    
    this._last = -1;
  }
  
  protected abstract Iterator newIterator();
  
  protected abstract void processValue(ItrValue paramItrValue);
  
  private void initialize()
  {
    if (this._itr == null)
    {
      this._itr = newIterator();
      setNext();
    }
  }
  
  private void setNext()
  {
    this._next.value = null;
    this._next.valid = false;
    
    int index = this._index;
    while (this._itr.hasNext())
    {
      this._next.value = this._itr.next();
      this._next.valid = true;
      index++;
      
      processValue(this._next);
      if (this._next.valid) {
        break;
      }
    }
    this._last = this._index;
    this._index = index;
  }
  
  public static class ItrValue
  {
    public Object value = null;
    public boolean valid = false;
  }
}
