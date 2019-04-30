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

class ElementCollectorSource
  extends RemovableElementSource
{
  private ElementCollector myCollector;
  private RemovableElementSource pos;
  
  ElementCollectorSource(ElementCollector aCollector)
  {
    this.myCollector = aCollector;
    this.pos = this.myCollector.elementStore.elements();
  }
  
  public int status()
  {
    if (this.pos.hasMoreElements()) {
      return 1;
    }
    if (this.myCollector != null)
    {
      int st = this.myCollector.kick();
      if (st == -1) {
        this.myCollector = null;
      }
      return st;
    }
    return -1;
  }
  
  public void removeNextElement()
  {
    status();
    this.pos.removeNextElement();
  }
  
  public Object peekNextElement()
  {
    return this.pos.peekNextElement();
  }
  
  public Object nextElement()
  {
    status();
    return this.pos.nextElement();
  }
  
  public void print(PrintingState p)
  {
    p.print("CollectorSource(");
    p.indent();p.newline();
    p.print("pos= ");
    p.indent();
    this.pos.print(p);
    p.outdent();p.newline();
    p.print("on = ");
    p.indent();
    if (this.myCollector == null) {
      p.print("null");
    } else {
      this.myCollector.print(p);
    }
    p.outdent();
    p.outdent();
    p.print(")");
  }
}
