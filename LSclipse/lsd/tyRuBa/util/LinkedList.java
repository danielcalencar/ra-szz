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

public class LinkedList {

  private class Bucket {
    Object el;
    Bucket next = null;
    
    Bucket(Object e) { el=e; }
    Bucket(Object e,Bucket r) {el=e; next=r;}
  }

  /** The start of the stored elements list */
  private Bucket head = new Bucket("dummy"); //dummy bucket makes code to add elements easier
  /** The end of the stored elements list */
  private Bucket tail = head;
  
  /** Add an element to the end */
  public void addElement(Object e) {
    tail.next = new Bucket(e);
    tail=tail.next;
  }

  /** Empty? */
  public boolean isEmpty() {
    return head.next==null;
  }

  /** Create an ElementSource which produces the elements in this LinkedList
    one by one */
  public RemovableElementSource elements() {
    return new RemovableElementSource() {
      private Bucket pos = head;

      public int status() {
	if (pos.next!=null)
	  return ELEMENT_READY;
	else
	  return NO_ELEMENTS_READY;
      }
      
      public Object peekNextElement() {
	return pos.next.el;
      }

      public void removeNextElement() {
	if ((pos.next=pos.next.next)==null)
	  tail=pos;
      }

      public Object nextElement() {
	pos = pos.next;
	return pos.el;
      }
      
      public void print(PrintingState p) {
      	p.print("Linked[");
      	for (Bucket current = pos.next;current!=null;current = current.next) {
      		p.printObj(current.el);
      		if (current.next!=null)
      			p.print(",");
      	}
      	p.print("]");
      }
    };
  }

}
