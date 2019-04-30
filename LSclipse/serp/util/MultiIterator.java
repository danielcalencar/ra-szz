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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class MultiIterator
  implements Iterator
{
  private static final int PAST_END = -1;
  private Iterator _itr = Collections.EMPTY_LIST.iterator();
  private Iterator _last = null;
  private int _index = 0;
  
  public boolean hasNext()
  {
    setIterator();
    return this._itr.hasNext();
  }
  
  public Object next()
  {
    setIterator();
    return this._itr.next();
  }
  
  public void remove()
  {
    setIterator();
    this._last.remove();
  }
  
  protected abstract Iterator newIterator(int paramInt);
  
  private void setIterator()
  {
    if (this._index == -1) {
      return;
    }
    this._last = this._itr;
    
    Iterator newItr = this._itr;
    while ((newItr != null) && (!newItr.hasNext())) {
      newItr = newIterator(this._index++);
    }
    if ((newItr != null) && (this._itr != newItr)) {
      this._itr = newItr;
    } else if (newItr == null) {
      this._index = -1;
    }
  }
}
