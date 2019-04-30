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

import java.util.NoSuchElementException;

final class MapElementSource
  extends ElementSource
{
  private Object next = null;
  private ElementSource remaining;
  private Action action;
  
  public MapElementSource(ElementSource on, Action what)
  {
    this.action = what;
    this.remaining = on;
  }
  
  public int status()
  {
    if (this.next == null) {
      advance();
    }
    if (this.next == null)
    {
      if (this.remaining == null) {
        return -1;
      }
      int result = this.remaining.status();
      return result;
    }
    return 1;
  }
  
  public Object nextElement()
  {
    if (status() == 1)
    {
      Object theNext = this.next;
      this.next = null;
      return theNext;
    }
    throw new NoSuchElementException("MapElementSource");
  }
  
  public void print(PrintingState p)
  {
    p.print("Map(");
    p.indent();
    if (this.next != null) {
      p.print("ready=" + this.next + " ");
    }
    p.print("Action= " + this.action.toString());p.newline();
    p.print("on =");
    p.indent();
    if (this.remaining == null) {
      p.print("null");
    } else {
      this.remaining.print(p);
    }
    p.outdent();
    p.outdent();
    p.print(")");
  }
  
  private int advance()
  {
    int result = 1;
    this.next = null;
    while ((this.next == null) && ((result = this.remaining.status()) == 1)) {
      this.next = this.action.compute(this.remaining.nextElement());
    }
    if (result == -1) {
      this.remaining = ElementSource.theEmpty;
    }
    return result;
  }
  
  public boolean isEmpty()
  {
    return (this.next == null) && (this.remaining.isEmpty());
  }
  
  public ElementSource first()
  {
    if (this.next != null) {
      return ElementSource.singleton(this.next);
    }
    return this.remaining.first().map(this.action);
  }
  
  public void release()
  {
    super.release();
    this.next = null;
    this.action = null;
    if (this.remaining != null)
    {
      this.remaining.release();
      this.remaining = null;
    }
  }
}
