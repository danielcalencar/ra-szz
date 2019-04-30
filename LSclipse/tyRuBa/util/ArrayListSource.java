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

import java.util.ArrayList;

public class ArrayListSource
  extends ElementSource
{
  int pos = 0;
  int sz;
  ArrayList els;
  
  public ArrayListSource(ArrayList els)
  {
    this.els = els;
    this.sz = els.size();
  }
  
  public int status()
  {
    return this.pos < this.sz ? 1 : -1;
  }
  
  public Object nextElement()
  {
    return this.els.get(this.pos++);
  }
  
  public void print(PrintingState p)
  {
    p.print("{");
    for (int i = this.pos; i < this.els.size(); i++)
    {
      if (i > 0) {
        p.print(",");
      }
      p.print(this.els.get(i).toString());
    }
    p.print("}");
  }
  
  public ElementSource first()
  {
    if (hasMoreElements()) {
      return ElementSource.singleton(nextElement());
    }
    return ElementSource.theEmpty;
  }
}
