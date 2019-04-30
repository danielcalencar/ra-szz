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

public class LinkedList
{
  private class Bucket
  {
    Object el;
    
    Bucket(Object e, Bucket r)
    {
      this.el = e;this.next = r;
    }
    
    Bucket(Object e)
    {
      this.el = e;
    }
    
    Bucket next = null;
  }
  
  private Bucket head = new Bucket("dummy");
  private Bucket tail = this.head;
  
  public void addElement(Object e)
  {
    this.tail.next = new Bucket(e);
    this.tail = this.tail.next;
  }
  
  public boolean isEmpty()
  {
    return this.head.next == null;
  }
  
  public RemovableElementSource elements()
  {
    new RemovableElementSource()
    {
      private LinkedList.Bucket pos = LinkedList.this.head;
      
      public int status()
      {
        if (this.pos.next != null) {
          return 1;
        }
        return 0;
      }
      
      public Object peekNextElement()
      {
        return this.pos.next.el;
      }
      
      public void removeNextElement()
      {
        if ((this.pos.next = this.pos.next.next) == null) {
          LinkedList.this.tail = this.pos;
        }
      }
      
      public Object nextElement()
      {
        this.pos = this.pos.next;
        return this.pos.el;
      }
      
      public void print(PrintingState p)
      {
        p.print("Linked[");
        for (LinkedList.Bucket current = this.pos.next; current != null; current = current.next)
        {
          p.printObj(current.el);
          if (current.next != null) {
            p.print(",");
          }
        }
        p.print("]");
      }
    };
  }
}
