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

public class SynchronizedElementSource
  extends ElementSource
{
  private SynchResource resource;
  private ElementSource src;
  
  public SynchronizedElementSource(SynchResource resource, ElementSource src)
  {
    synchronized (resource)
    {
      resource.getSynchPolicy().newSource();
      this.resource = resource;
      this.src = src;
    }
  }
  
  public void print(PrintingState p)
  {
    p.print("Synchronized(");
    this.src.print(p);
    p.print(")");
  }
  
  public int status()
  {
    if (this.resource == null) {
      return -1;
    }
    synchronized (this.resource)
    {
      int result = this.src.status();
      if (result == -1) {
        release();
      }
      return result;
    }
  }
  
  public void release()
  {
    if (this.resource != null)
    {
      if (this.src != null) {
        this.src.release();
      }
      this.src = null;
      this.resource.getSynchPolicy().sourceDone();
      this.resource = null;
    }
  }
  
  /* Error */
  public Object nextElement()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 26	tyRuBa/util/SynchronizedElementSource:resource	LtyRuBa/util/SynchResource;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 28	tyRuBa/util/SynchronizedElementSource:src	LtyRuBa/util/ElementSource;
    //   11: invokevirtual 67	tyRuBa/util/ElementSource:nextElement	()Ljava/lang/Object;
    //   14: aload_1
    //   15: monitorexit
    //   16: areturn
    //   17: aload_1
    //   18: monitorexit
    //   19: athrow
    // Line number table:
    //   Java source line #68	-> byte code offset #0
    //   Java source line #69	-> byte code offset #7
    //   Java source line #68	-> byte code offset #17
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	20	0	this	SynchronizedElementSource
    //   5	13	1	Ljava/lang/Object;	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	17	finally
    //   17	19	17	finally
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.resource != null) {
        if (Aurelizer.debug_sounds != null) {
          Aurelizer.debug_sounds.enter("ok");
        }
      }
      release();
      super.finalize();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      if (Aurelizer.debug_sounds != null) {
        Aurelizer.debug_sounds.enter("error");
      }
    }
  }
}
