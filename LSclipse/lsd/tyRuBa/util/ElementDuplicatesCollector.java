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

/** An ElementCollector which allows duplicate Elements. Is more efficient than an
  ElementSetCollector because it does not keep track of the elments in a Set
  and does not verify incoming elements for duplicates 
  */

public class ElementDuplicatesCollector extends ElementCollector {

  /** An element collector initialized with one source */
  public ElementDuplicatesCollector(ElementSource s) {
    super(s);
  }

  /** An element collector with no sources (add later with setSource) */
  public ElementDuplicatesCollector() {}

  /** Just get the fits element, we don't have to check for duplicates */
  protected int newElementFromSource() {
  	int stat;
    if ((stat=source.status())==ElementSource.ELEMENT_READY) {
      Object element=source.nextElement();
      addElement(element);
    }
    return stat;
  }
            
//  /** Small test Application */
//  public static void main(String args[]) {
//    final ElementDuplicatesCollector testDuplicates = new ElementDuplicatesCollector();
//    testDuplicates.setSource(
//      testDuplicates.elements().map(new Action() {
//	 	public Object compute(Object a) {
//	   		int i = ((Integer)a).intValue();
//	   		i = (i+1)%10; 
//	   		return new Integer(i);
//	 	}
//      })
//     .append(ElementSource.singleton(new Integer(1))));
//    RemovableElementSource testDuplicatesEls = new ElementDuplicatesCollector(testDuplicates.elements()).elements();
//    //RemovableElementSource testDuplicatesEls = testDuplicates.elements();
//    while (testDuplicatesEls.status()==ElementSource.ELEMENT_READY) {
//      System.out.println(testDuplicatesEls.peekNextElement());
//      testDuplicatesEls.removeNextElement();
//    }
//  }
//
}
