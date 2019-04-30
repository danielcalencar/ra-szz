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

public class First
  extends ElementSource
{
  private ElementSource source;
  
  public First(ElementSource from)
  {
    this.source = from;
  }
  
  public void print(PrintingState p)
  {
    p.print("First(");
    this.source.print(p);
    p.outdent();
    p.print(")");
  }
  
  public int status()
  {
    if (this.source == null) {
      return -1;
    }
    int stat = this.source.status();
    if (this.source.status() == -1) {
      this.source = null;
    }
    return stat;
  }
  
  public Object nextElement()
  {
    ElementSource it = this.source;
    this.source = null;
    return it.nextElement();
  }
  
  public ElementSource first()
  {
    return this;
  }
}
