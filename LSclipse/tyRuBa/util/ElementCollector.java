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

import java.util.Set;

public abstract class ElementCollector
{
  LinkedList elementStore = new LinkedList();
  
  protected void addElement(Object e)
  {
    this.elementStore.addElement(e);
  }
  
  public ElementCollector(ElementSource s)
  {
    setSource(s);
  }
  
  public ElementCollector() {}
  
  public RemovableElementSource elements()
  {
    return new ElementCollectorSource(this);
  }
  
  private boolean hurting = false;
  protected ElementSource source = ElementSource.theEmpty;
  
  /* Error */
  protected final int kick()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 32	tyRuBa/util/ElementCollector:hurting	Z
    //   4: ifne +31 -> 35
    //   7: aload_0
    //   8: iconst_1
    //   9: putfield 32	tyRuBa/util/ElementCollector:hurting	Z
    //   12: aload_0
    //   13: invokevirtual 54	tyRuBa/util/ElementCollector:newElementFromSource	()I
    //   16: istore_1
    //   17: goto +11 -> 28
    //   20: astore_2
    //   21: aload_0
    //   22: iconst_0
    //   23: putfield 32	tyRuBa/util/ElementCollector:hurting	Z
    //   26: aload_2
    //   27: athrow
    //   28: aload_0
    //   29: iconst_0
    //   30: putfield 32	tyRuBa/util/ElementCollector:hurting	Z
    //   33: iload_1
    //   34: ireturn
    //   35: iconst_0
    //   36: ireturn
    // Line number table:
    //   Java source line #42	-> byte code offset #0
    //   Java source line #45	-> byte code offset #7
    //   Java source line #46	-> byte code offset #12
    //   Java source line #47	-> byte code offset #20
    //   Java source line #50	-> byte code offset #21
    //   Java source line #51	-> byte code offset #26
    //   Java source line #50	-> byte code offset #28
    //   Java source line #52	-> byte code offset #33
    //   Java source line #55	-> byte code offset #35
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	ElementCollector
    //   16	2	1	foundElement	int
    //   28	6	1	foundElement	int
    //   20	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	20	finally
  }
  
  protected abstract int newElementFromSource();
  
  public void setSource(ElementSource source)
  {
    this.source = source;
  }
  
  public void print(PrintingState p)
  {
    p.print(toString());
    if (!p.visited.contains(this))
    {
      p.visited.add(this);
      p.print("(");
      p.indent();
      p.newline();
      p.print("collected= ");
      p.indent();
      this.elementStore.elements().print(p);
      p.outdent();
      p.newline();
      p.print("source= ");
      p.indent();
      if (this.source == null) {
        p.print("null");
      } else {
        this.source.print(p);
      }
      p.outdent();
      p.outdent();
      p.print(")");
    }
  }
}
