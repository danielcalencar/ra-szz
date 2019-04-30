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
package tyRuBa.tests;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import junit.framework.Assert;

public class TestLogger
{
  public static final boolean logging = false;
  private int logtime = 0;
  PrintWriter logFile;
  PrintStream console = System.err;
  
  public TestLogger(PrintWriter writer)
  {
    this.logFile = writer;
  }
  
  public class LogEntry
  {
    LogEntry parent;
    String kind;
    ArrayList info = new ArrayList();
    int enter = 0;
    int exit = 0;
    
    LogEntry(LogEntry parent, String kind, int creationTime)
    {
      this.parent = parent;
      this.kind = kind;
      this.enter = creationTime;
    }
    
    public void addInfo(Object infoArg)
    {
      this.info.add(infoArg.toString());
    }
    
    public String toString()
    {
      return 
      
        this.kind + "(" + this.enter + "," + this.exit + "," + parentID() + infoString() + ").";
    }
    
    private int parentID()
    {
      return this.parent == null ? 0 : this.parent.enter;
    }
    
    private String infoString()
    {
      String result = "";
      for (Iterator iter = this.info.iterator(); iter.hasNext();)
      {
        String element = (String)iter.next();
        result = result + "," + element;
      }
      return result;
    }
    
    public void exit()
    {
      this.exit = (++TestLogger.this.logtime);
      String msg = toString();
      println(msg);
      Assert.assertEquals(TestLogger.this.current, this);
      if ((this.parent != null) && (!this.parent.inProgress())) {
        println("*** problem with parent entry " + this.parent);
      }
      TestLogger.this.current = this.parent;
    }
    
    private void println(String msg)
    {
      if (TestLogger.this.console != null) {
        TestLogger.this.console.println(msg);
      }
      if (TestLogger.this.logFile != null) {
        TestLogger.this.logFile.println(msg);
      }
    }
    
    private boolean inProgress()
    {
      return this.exit == 0;
    }
    
    public void exit(String info)
    {
      addInfo(info);
      exit();
    }
  }
  
  LogEntry current = new LogEntry(null, "BIGBANG", 0);
  
  public LogEntry enter(String kind)
  {
    this.current = new LogEntry(this.current, kind, ++this.logtime);
    return this.current;
  }
  
  public LogEntry enter(String kind, String params)
  {
    this.current = new LogEntry(this.current, kind, ++this.logtime);
    this.current.addInfo(params);
    return this.current;
  }
  
  public LogEntry enter(String kind, int info)
  {
    return enter(kind, info);
  }
  
  public void logNow(String kind, String params)
  {
    LogEntry enter = enter(kind, params);
    enter.exit();
  }
  
  void close()
  {
    while (this.current != null) {
      this.current.exit();
    }
    if (this.logFile != null) {
      this.logFile.close();
    }
  }
  
  private static boolean loading = false;
  
  public synchronized LogEntry enterLoad(String path)
  {
    Assert.assertFalse("Reentrant load should not happen", loading);
    Assert.assertFalse("Load inside storeAll", this.current.kind.equals("storeAll"));
    loading = true;
    return enter("load", "\"" + path + "\"");
  }
  
  public synchronized void exitLoad(LogEntry entry)
  {
    Assert.assertTrue("Must enter load before exit load", loading);
    entry.exit();
    loading = false;
  }
  
  public void assertTrue(String msg, boolean b)
  {
    if (!b)
    {
      logNow("assertionFailed", "\"" + msg + "\"");
      Assert.fail(msg);
    }
  }
}
