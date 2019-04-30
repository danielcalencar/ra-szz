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

import java.util.Vector;

public class CompositeElementSource
  extends ElementSource
{
  Vector children = new Vector();
  
  public void add(ElementSource child)
  {
    this.children.addElement(child);
  }
  
  public ElementSource get(int i)
  {
    return (ElementSource)this.children.elementAt(i);
  }
  
  public int numberOfChildren()
  {
    return this.children.size();
  }
  
  public int i = -1;
  
  public int status()
  {
    for (this.i = 0; this.i < numberOfChildren();)
    {
      int stat = get(this.i).status();
      if (stat == 1) {
        return stat;
      }
      if (stat == -1) {
        this.children.removeElementAt(this.i);
      } else {
        this.i += 1;
      }
    }
    if (numberOfChildren() == 0) {
      return -1;
    }
    return 0;
  }
  
  public Object nextElement()
  {
    int stat;
    int stat;
    if ((this.i < 0) || (this.i >= numberOfChildren())) {
      stat = status();
    } else {
      stat = 1;
    }
    if (stat == 1) {
      return get(this.i).nextElement();
    }
    throw new Error("No nextElement found in CompositeElementSource");
  }
  
  public void print(PrintingState p)
  {
    p.print("Composite(");
    p.indent();p.newline();
    for (int i = 0; i < numberOfChildren(); i++) {
      get(i).print(p);
    }
    p.outdent();
    p.print(")");
  }
  
  public ElementSource simplify()
  {
    if (this.children.size() == 0) {
      return ElementSource.theEmpty;
    }
    if (this.children.size() == 1) {
      return get(0);
    }
    return this;
  }
}
