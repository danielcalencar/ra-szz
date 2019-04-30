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

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorPair
  implements Iterator
{
  Iterator[] iterators;
  int which;
  
  public IteratorPair(Iterator carIt, Iterator cdrIt)
  {
    this.iterators = new Iterator[] { carIt, cdrIt };
    
    this.which = 0;
  }
  
  public boolean hasNext()
  {
    if (this.which < 2)
    {
      if ((this.iterators[this.which] != null) && (this.iterators[this.which].hasNext())) {
        return true;
      }
      this.which += 1;
      return hasNext();
    }
    return false;
  }
  
  public Object next()
  {
    if (hasNext()) {
      return this.iterators[this.which].next();
    }
    throw new NoSuchElementException();
  }
  
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
