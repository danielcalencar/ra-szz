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
package tyRuBa.engine.factbase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.PredicateIdentifier;
import tyRuBa.engine.RBComponent;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.PredicateMode;

public class FileWriterFactBase
  extends FactBase
{
  private String predicateName;
  private FactBase containedFactBase;
  private static PrintWriter pw;
  
  public FileWriterFactBase(PredicateIdentifier pid, FactBase fb, File f)
  {
    this.predicateName = pid.getName();
    this.containedFactBase = fb;
    try
    {
      if (pw == null) {
        pw = new PrintWriter(new FileOutputStream(f));
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public boolean isEmpty()
  {
    return this.containedFactBase.isEmpty();
  }
  
  public boolean isPersistent()
  {
    return this.containedFactBase.isPersistent();
  }
  
  public synchronized void insert(RBComponent f)
  {
    if (f.isGroundFact())
    {
      pw.print(this.predicateName + "(");
      RBTuple args = f.getArgs();
      printTuple(args);
      pw.println(").");
      this.containedFactBase.insert(f);
    }
  }
  
  private void printTuple(RBTuple args)
  {
    for (int i = 0; i < args.getNumSubterms(); i++)
    {
      RBTerm subterm = args.getSubterm(i);
      if (i > 0) {
        pw.print(", ");
      }
      if ((subterm instanceof RBCompoundTerm))
      {
        RBCompoundTerm compterm = (RBCompoundTerm)subterm;
        pw.print(compterm.getConstructorType().getFunctorId().getName() + "<");
        RBTerm[] terms = new RBTerm[compterm.getNumArgs()];
        for (int j = 0; j < compterm.getNumArgs(); j++) {
          terms[j] = compterm.getArg(j);
        }
        printTuple(RBTuple.make(terms));
        pw.print(">");
      }
    }
  }
  
  public Compiled basicCompile(PredicateMode mode, CompilationContext context)
  {
    pw.flush();
    return this.containedFactBase.compile(mode, context);
  }
  
  public void backup()
  {
    this.containedFactBase.backup();
  }
}
