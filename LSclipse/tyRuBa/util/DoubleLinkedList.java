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

import java.util.Iterator;

public class DoubleLinkedList
{
  public static class Entry
  {
    private Entry prev;
    private Entry next;
    
    public Entry()
    {
      this.prev = null;
      this.next = null;
    }
  }
  
  private Entry head = null;
  private Entry tail = null;
  private int size = 0;
  
  public boolean isEmpty()
  {
    return this.size == 0;
  }
  
  public int size()
  {
    return this.size;
  }
  
  public Entry head()
  {
    return this.head;
  }
  
  public Entry tail()
  {
    return this.tail;
  }
  
  public void clear()
  {
    this.head = null;
    this.tail = null;
    this.size = 0;
  }
  
  public void enqueue(Entry entry)
  {
    if (this.head != null)
    {
      entry.prev = null;
      entry.next = this.head;
      
      this.head.prev = entry;
      this.head = entry;
    }
    else
    {
      entry.prev = null;
      entry.next = null;
      
      this.head = entry;
      this.tail = this.head;
    }
    this.size += 1;
  }
  
  public void addLast(Entry entry)
  {
    if (this.tail != null)
    {
      entry.prev = this.tail;
      entry.next = null;
      
      this.tail.next = entry;
      this.tail = entry;
    }
    else
    {
      entry.prev = null;
      entry.next = null;
      
      this.tail = entry;
      this.head = this.tail;
    }
    this.size += 1;
  }
  
  public void addAll(DoubleLinkedList list)
  {
    if (list.head != null) {
      if (this.head != null)
      {
        this.head.prev = list.tail;
        list.tail.next = this.head;
        this.head = list.head;
        
        this.size += list.size;
      }
      else
      {
        this.head = list.head;
        this.tail = list.tail;
        
        this.size = list.size;
      }
    }
  }
  
  public void addAfter(DoubleLinkedList list, Entry entry)
  {
    if (list.head != null)
    {
      if (entry != this.tail)
      {
        list.tail.next = entry.next;
        entry.next.prev = list.tail;
        entry.next = list.head;
        list.head.prev = entry;
      }
      else
      {
        this.tail = list.tail;
        entry.next = list.head;
        list.head.prev = entry;
      }
      this.size += list.size;
    }
  }
  
  public void addAfter(Entry after, Entry entry)
  {
    if (entry != this.tail)
    {
      after.next = entry.next;
      entry.next.prev = after;
      entry.next = after;
      after.prev = entry;
    }
    else
    {
      after.next = null;
      this.tail = after;
      entry.next = after;
      after.prev = entry;
    }
    this.size += 1;
  }
  
  public void addBefore(DoubleLinkedList list, Entry entry)
  {
    if (list.head != null)
    {
      if (entry != this.head)
      {
        list.head.prev = entry.prev;
        entry.prev.next = list.head;
        entry.prev = list.tail;
        list.tail.next = entry;
      }
      else
      {
        this.head = list.head;
        entry.prev = list.tail;
        list.tail.next = entry;
      }
      this.size += list.size;
    }
  }
  
  public void addBefore(Entry before, Entry entry)
  {
    if (entry != this.head)
    {
      before.prev = entry.prev;
      entry.prev.next = before;
      entry.prev = before;
      before.next = entry;
    }
    else
    {
      before.prev = null;
      this.head = before;
      entry.prev = before;
      before.next = entry;
    }
    this.size += 1;
  }
  
  public void remove(Entry entry)
  {
    if (entry != this.head)
    {
      if (entry != this.tail)
      {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
      }
      else
      {
        entry.prev.next = null;
        this.tail = entry.prev;
      }
    }
    else if (entry != this.tail)
    {
      entry.next.prev = null;
      this.head = entry.next;
    }
    else
    {
      this.head = null;
      this.tail = null;
    }
    entry.prev = null;
    entry.next = null;
    
    this.size -= 1;
  }
  
  public Entry dequeue()
  {
    Entry result = this.tail;
    remove(result);
    return result;
  }
  
  public Entry peek()
  {
    return this.tail;
  }
  
  public String toString()
  {
    String result = "DoubleLL( ";
    Entry current = this.head;
    while (current != null)
    {
      result = result + current + " ";
      current = current.next;
    }
    return result + ")";
  }
  
  public Iterator iterator()
  {
    new Iterator()
    {
      private DoubleLinkedList.Entry current = DoubleLinkedList.this.head;
      
      public boolean hasNext()
      {
        return this.current != null;
      }
      
      public Object next()
      {
        DoubleLinkedList.Entry result = this.current;
        this.current = DoubleLinkedList.Entry.access$2(this.current);
        return result;
      }
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}
