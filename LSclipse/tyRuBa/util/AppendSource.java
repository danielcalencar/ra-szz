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

class AppendSource
  extends ElementSource
{
  private ElementSource s1;
  private ElementSource s2;
  
  AppendSource(ElementSource s1, ElementSource s2)
  {
    this.s1 = s1;
    this.s2 = s2;
  }
  
  public int status()
  {
    int stat = this.s1.status();
    if (stat == 1) {
      return stat;
    }
    if (stat == -1)
    {
      this.s1 = this.s2;
      this.s2 = theEmpty;
      return this.s1.status();
    }
    return this.s2.status();
  }
  
  public Object nextElement()
  {
    if (this.s1.status() == 1) {
      return this.s1.nextElement();
    }
    return this.s2.nextElement();
  }
  
  public void print(PrintingState p)
  {
    p.print("Append(");
    p.indent();p.newline();
    this.s1.print(p);
    p.newline();p.print("++");
    this.s2.print(p);
    p.outdent();
    p.print(")");
  }
}
