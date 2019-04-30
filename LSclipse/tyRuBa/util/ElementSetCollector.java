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

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class ElementSetCollector
  extends ElementCollector
{
  private Set seen = new HashSet();
  
  protected void addElement(Object e)
  {
    super.addElement(e);
    this.seen.add(e);
  }
  
  public ElementSetCollector(ElementSource s)
  {
    super(s);
  }
  
  public ElementSetCollector() {}
  
  private boolean isPresent(Object el)
  {
    return this.seen.contains(el);
  }
  
  protected int newElementFromSource()
  {
    int status;
    Object element;
    do
    {
      if (this.source == null) {
        return -1;
      }
      status = this.source.status();
      if (status != 1) {
        break;
      }
      element = this.source.nextElement();
    } while (isPresent(element));
    addElement(element);
    return 1;
    if (status == -1)
    {
      this.source = null;
      this.seen = null;
    }
    return status;
  }
  
  public static void main(String[] args)
  {
    ElementSetCollector testSet = new ElementSetCollector();
    testSet.setSource(
      testSet.elements().map(new Action()
      {
        public Object compute(Object a)
        {
          int i = ((Integer)a).intValue();
          i = (i + 1) % 10;
          return new Integer(i);
        }
      }).append(ElementSource.singleton(new Integer(1))));
    
    RemovableElementSource testSetEls = new ElementSetCollector(testSet.elements()).elements();
    while (testSetEls.status() == 1)
    {
      System.out.println(testSetEls.peekNextElement());
      testSetEls.removeNextElement();
    }
  }
}
