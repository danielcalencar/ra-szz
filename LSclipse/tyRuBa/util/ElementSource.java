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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ElementSource
{
  public static final int ELEMENT_READY = 1;
  public static final int NO_ELEMENTS_READY = 0;
  public static final int NO_MORE_ELEMENTS = -1;
  
  public boolean isEmpty()
  {
    return false;
  }
  
  public abstract void print(PrintingState paramPrintingState);
  
  public abstract int status();
  
  public abstract Object nextElement();
  
  public boolean hasMoreElements()
  {
    return status() == 1;
  }
  
  public static ElementSource singleton(Object e)
  {
    new ElementSource()
    {
      public int status()
      {
        if (ElementSource.this == null) {
          return -1;
        }
        return 1;
      }
      
      public Object nextElement()
      {
        Object el = ElementSource.this;
        this.myElement = null;
        return el;
      }
      
      public void print(PrintingState p)
      {
        p.print("{");
        if (ElementSource.this == null) {
          p.print("null");
        } else {
          p.print(ElementSource.this.toString());
        }
        p.print("}");
      }
      
      public boolean isEmpty()
      {
        return ElementSource.this == null;
      }
      
      public ElementSource first()
      {
        return this;
      }
    };
  }
  
  public static final ElementSource theEmpty = EmptySource.the;
  
  public ElementSource append(ElementSource other)
  {
    if (other.isEmpty()) {
      return this;
    }
    return new AppendSource(this, other);
  }
  
  public ElementSource map(Action what)
  {
    return new MapElementSource(this, what);
  }
  
  public static ElementSource with(Object[] els)
  {
    new ElementSource()
    {
      int pos = 0;
      
      public int status()
      {
        return this.pos < ElementSource.this.length ? 1 : -1;
      }
      
      public Object nextElement()
      {
        return ElementSource.this[(this.pos++)];
      }
      
      public void print(PrintingState p)
      {
        p.print("{");
        for (int i = 0; i < ElementSource.this.length; i++)
        {
          if (i > 0) {
            p.print(",");
          }
          p.print(ElementSource.this[i].toString());
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
    };
  }
  
  public static ElementSource with(ArrayList els)
  {
    if (els.isEmpty()) {
      return theEmpty;
    }
    return new ArrayListSource(els);
  }
  
  public static ElementSource with(Iterator it)
  {
    new ElementSource()
    {
      public int status()
      {
        return ElementSource.this.hasNext() ? 1 : -1;
      }
      
      public Object nextElement()
      {
        return ElementSource.this.next();
      }
      
      public void print(PrintingState p)
      {
        p.print("{");
        p.print("NOT CURRENTLY SUPPORTED");
        p.print("}");
      }
    };
  }
  
  public void forceAll()
  {
    while (hasMoreElements()) {
      nextElement();
    }
  }
  
  public String toString()
  {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    print(new PrintingState(new PrintStream(result)));
    return result.toString();
  }
  
  public ElementSource first()
  {
    return new First(this);
  }
  
  public ElementSource immediateFirst()
  {
    int stat = status();
    if (stat == 1) {
      return singleton(nextElement());
    }
    if (stat == -1) {
      return theEmpty;
    }
    return first();
  }
  
  public ElementSource flatten()
  {
    return new FlattenElementSource(this);
  }
  
  public SynchronizedElementSource synchronizeOn(SynchResource resource)
  {
    return new SynchronizedElementSource(resource, this);
  }
  
  public int countElements()
  {
    int result = 0;
    while (hasMoreElements())
    {
      nextElement();
      result++;
    }
    return result;
  }
  
  public void release() {}
  
  public Object firstElementOrNull()
  {
    if (hasMoreElements())
    {
      Object result = nextElement();
      release();
      return result;
    }
    return null;
  }
}
