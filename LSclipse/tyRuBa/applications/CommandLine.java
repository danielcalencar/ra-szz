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
package tyRuBa.applications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import tyRuBa.engine.FrontEnd;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.tests.PerformanceTest;

public class CommandLine
{
  FrontEnd frontend = null;
  boolean loadInitFile = true;
  File dbDir = null;
  int cachesize = 5000;
  private boolean backgroundPageCleaning = false;
  
  void ensureFrontEnd()
    throws IOException, ParseException, TypeModeError
  {
    if (this.frontend == null) {
      if (this.dbDir == null) {
        this.frontend = new FrontEnd(this.loadInitFile, new File("./fdb/"), true, null, true, this.backgroundPageCleaning);
      } else {
        this.frontend = new FrontEnd(this.loadInitFile, this.dbDir, true, null, false, this.backgroundPageCleaning);
      }
    }
    this.frontend.setCacheSize(this.cachesize);
  }
  
  public static void main(String[] args)
  {
    new CommandLine().realmain(args);
  }
  
  public void realmain(String[] args)
  {
    if (args.length == 0)
    {
      System.err.println("ERROR: no commandline arguments where given");
      return;
    }
    try
    {
      int start = 0;
      for (int i = start; i < args.length; i++) {
        if (args[i].charAt(0) == '-')
        {
          if (args[i].equals("-noinit"))
          {
            System.err.println("Option -noinit seen...");
            if (this.frontend != null) {
              throw new Error("The -noinit option must occur before any file names");
            }
            this.loadInitFile = false;
          }
          else if (args[i].equals("-bgpager"))
          {
            if (this.frontend != null) {
              throw new Error("The -bgpager option must occur before any file names");
            }
            this.backgroundPageCleaning = true;
          }
          else if (args[i].equals("-cachesize"))
          {
            this.cachesize = Integer.parseInt(args[(++i)]);
            if (this.frontend != null) {
              this.frontend.setCacheSize(this.cachesize);
            }
          }
          else if (args[i].equals("-dbdir"))
          {
            if (this.frontend != null) {
              throw new Error("The -dbdir option must occur before any file names");
            }
            if (this.dbDir != null) {
              throw new Error("The -dbdir option can only be set once");
            }
            this.dbDir = new File(args[(++i)]);
          }
          else if (args[i].equals("-o"))
          {
            ensureFrontEnd();
            this.frontend.redirectOutput(
              new PrintStream(new FileOutputStream(args[(++i)])));
          }
          else if (args[i].equals("-i"))
          {
            System.err.println("Option -i seen...");
            ensureFrontEnd();
            boolean keepGoing = false;
            do
            {
              try
              {
                System.err.println(
                  "\n--- Interactive mode... type queries!");
                System.err.println("end with CTRL-D");
                this.frontend.load(System.in);
                keepGoing = false;
              }
              catch (ParseException e)
              {
                keepGoing = true;
                System.err.println(
                  "TyRuBaParser:" + e.getMessage());
              }
              catch (TypeModeError e)
              {
                keepGoing = true;
                System.err.println(
                  "Type or Mode Error: " + e.getMessage());
              }
            } while (
            
              keepGoing);
          }
          else if (args[i].equals("-silent"))
          {
            tyRuBa.engine.RuleBase.silent = true;
          }
          else if (args[i].equals("-nocache"))
          {
            if (this.frontend != null) {
              throw new Error("The -nocache option must occur before any file names");
            }
            tyRuBa.engine.RuleBase.useCache = false;
          }
          else if (args[i].equals("-classpath"))
          {
            ensureFrontEnd();
            this.frontend.parse("classpath(\"" + args[(++i)] + "\").");
          }
          else if (args[i].equals("-parse"))
          {
            String command = args[(++i)];
            while (!args[i].endsWith(".")) {
              command = command + " " + args[(++i)];
            }
            System.err.println("-parse " + command);
            this.frontend.parse(command);
          }
          else if (args[i].equals("-benchmark"))
          {
            ensureFrontEnd();
            String queryfile = args[(++i)];
            PerformanceTest test = PerformanceTest.make(this.frontend, queryfile);
            this.frontend.output().println("----- results for tests in " + queryfile + " -------");
            this.frontend.output().println(test);
          }
          else if (args[i].equals("-metadata"))
          {
            ensureFrontEnd();
            this.frontend.enableMetaData();
          }
          else
          {
            System.err.println(
              "*** Error: unkown commandline option: " + args[i]);
            System.exit(-1);
          }
        }
        else
        {
          ensureFrontEnd();
          System.err.println("Loading file: " + args[i]);
          this.frontend.load(args[i]);
        }
      }
    }
    catch (FileNotFoundException e)
    {
      System.err.println("TyRuBaParser:" + e.getMessage());
      System.exit(-1);
    }
    catch (IOException e)
    {
      System.err.println("TyRuBaParser:" + e.getMessage());
      System.exit(-2);
    }
    catch (ParseException e)
    {
      System.err.println(e.getMessage());
      System.exit(-3);
    }
    catch (TypeModeError e)
    {
      e.printStackTrace();
      System.exit(-4);
    }
    this.frontend.shutdown();
    System.exit(0);
  }
}
