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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import tyRuBa.engine.FrontEnd;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.util.ElementSource;

public class PerformanceTest
{
  private boolean isScenario;
  private String[] queries;
  FrontEnd frontend;
  Test[] tests;
  
  public PerformanceTest(FrontEnd frontend, String[] queries, boolean isScenario)
    throws ParseException, IOException, TypeModeError
  {
    this.frontend = frontend;
    this.queries = queries;
    this.isScenario = isScenario;
    run();
  }
  
  private void run()
  {
    if (this.isScenario) {
      runScenario();
    } else {
      runOneByOne();
    }
  }
  
  public static PerformanceTest make(FrontEnd frontend, String queryfile)
    throws ParseException, IOException, TypeModeError
  {
    ArrayList queries = new ArrayList();
    BufferedReader qf = new BufferedReader(new FileReader(queryfile));
    
    boolean isScenario = false;
    String query;
    while ((query = qf.readLine()) != null)
    {
      String query;
      if (!query.startsWith("//")) {
        queries.add(query);
      } else if (query.startsWith("//SCENARIO")) {
        isScenario = true;
      }
    }
    return new PerformanceTest(frontend, (String[])queries.toArray(new String[queries.size()]), isScenario);
  }
  
  public class Test
  {
    String query;
    long runtime = 0L;
    long numresults = 0L;
    private Throwable error = null;
    
    public Test(String query)
    {
      this.query = query;
    }
    
    void run()
      throws ParseException, TypeModeError
    {
      ElementSource result = PerformanceTest.this.frontend.frameQuery(this.query);
      while (result.hasMoreElements())
      {
        this.numresults += 1L;
        result.nextElement();
      }
    }
    
    void timedRun()
    {
      timedScenarioStepRun(System.currentTimeMillis());
    }
    
    public long timedScenarioStepRun(long startTime)
    {
      this.numresults = 0L;
      try
      {
        run();
      }
      catch (Throwable e)
      {
        this.error = e;
      }
      long endtime = System.currentTimeMillis();
      this.runtime = (endtime - startTime);
      return endtime;
    }
    
    public String toString()
    {
      if (this.error != null) {
        return this.query + "#CRASHED: " + this.error.getMessage();
      }
      return 
        this.query + "  #results = " + this.numresults + "  seconds = " + this.runtime / 1000.0D;
    }
  }
  
  private void runOneByOne()
  {
    this.tests = new Test[this.queries.length];
    for (int i = 0; i < this.queries.length; i++)
    {
      this.tests[i] = new Test(this.queries[i]);
      System.gc();
      
      this.tests[i].timedRun();
      System.err.println(this.tests[i]);
    }
  }
  
  private void runScenario()
  {
    this.tests = new Test[this.queries.length];
    for (int i = 0; i < this.queries.length; i++) {
      this.tests[i] = new Test(this.queries[i]);
    }
    System.gc();
    
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < this.queries.length; i++) {
      startTime = this.tests[i].timedScenarioStepRun(startTime);
    }
  }
  
  public String toString()
  {
    StringBuffer out = new StringBuffer();
    for (int i = 0; i < this.tests.length; i++) {
      out.append(this.tests[i] + "\n");
    }
    out.append("TOTAL : " + totalTime() + "\n");
    return out.toString();
  }
  
  public double totalTime()
  {
    long total = 0L;
    for (int i = 0; i < this.tests.length; i++) {
      total += this.tests[i].runtime;
    }
    return total / 1000.0D;
  }
}
