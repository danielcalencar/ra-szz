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

import tyRuBa.engine.FrontEnd;
import tyRuBa.engine.ProgressMonitor;
import tyRuBa.engine.SimpleRuleBaseBucket;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class ProgressMonitorTest
  extends TyrubaTest
{
  SimpleRuleBaseBucket bucket;
  SimpleRuleBaseBucket otherBucket;
  
  public void setUp()
    throws Exception
  {
    super.setUp(this.mon);
    this.bucket = new SimpleRuleBaseBucket(this.frontend);
    this.otherBucket = new SimpleRuleBaseBucket(this.frontend);
  }
  
  MyProgressMonitor mon = new MyProgressMonitor();
  
  static class MyProgressMonitor
    implements ProgressMonitor
  {
    private boolean isDone = true;
    int updates = -99;
    int expectedWork;
    
    public void beginTask(String name, int totalWork)
    {
      this.updates = 0;
      this.expectedWork = totalWork;
      if (!this.isDone) {
        ProgressMonitorTest.fail("No multi tasking/progressing!");
      }
      this.isDone = false;
      ProgressMonitorTest.assertTrue(totalWork > 0);
    }
    
    public void worked(int units)
    {
      this.updates += units;
    }
    
    public void done()
    {
      this.isDone = true;
    }
    
    public int workDone()
    {
      if (!this.isDone) {
        ProgressMonitorTest.fail("Hey... the work is not done!");
      }
      return this.updates;
    }
  }
  
  public ProgressMonitorTest(String arg0)
  {
    super(arg0);
  }
  
  public void testProgressMonitor()
    throws ParseException, TypeModeError
  {
    tyRuBa.engine.RuleBase.autoUpdate = true;
    
    this.frontend.parse("foo :: String");
    
    this.frontend.parse("foo(frontend).");
    this.otherBucket.addStuff("foo(otherBucket).");
    this.bucket.addStuff("foo(bucket).");
    
    test_must_succeed("foo(frontend)");
    
    assertEquals(this.mon.expectedWork, this.mon.workDone());
    
    test_must_succeed("foo(frontend)", this.otherBucket);
    
    this.otherBucket.clearStuff();
    
    test_must_succeed("foo(frontend)");
    
    assertEquals(this.mon.expectedWork, this.mon.workDone());
    
    this.mon.updates = 64537;
    
    test_must_succeed("foo(frontend)", this.otherBucket);
    
    assertEquals(64537, this.mon.workDone());
  }
}
