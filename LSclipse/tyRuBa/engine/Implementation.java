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
package tyRuBa.engine;

import java.util.ArrayList;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.compilation.SemiDetCompiled;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.Multiplicity;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.TypeModeError;
import tyRuBa.util.ElementSource;

public abstract class Implementation
  extends RBComponent
{
  private PredicateMode mode;
  private ArrayList solutions = null;
  private ArrayList arguments;
  private ArrayList results;
  private RBTuple argsAndResults;
  
  public abstract void doit(RBTerm[] paramArrayOfRBTerm);
  
  public Implementation(String paramModesString, String modeString)
  {
    this.mode = Factory.makePredicateMode(paramModesString, modeString);
    BindingList bindings = this.mode.getParamModes();
    ArrayList argsAndRes = new ArrayList();
    this.arguments = new ArrayList();
    this.results = new ArrayList();
    for (int i = 0; i < bindings.size(); i++)
    {
      RBTerm curr = FrontEnd.makeUniqueVar("arg" + i);
      argsAndRes.add(curr);
      if (bindings.get(i).isBound()) {
        this.arguments.add(curr);
      } else {
        this.results.add(curr);
      }
    }
    this.argsAndResults = RBTuple.make(argsAndRes);
  }
  
  public RBTerm getArgAt(int i)
  {
    return (RBTerm)this.arguments.get(i);
  }
  
  public int getNumArgs()
  {
    return this.arguments.size();
  }
  
  public RBTerm getResultAt(int i)
  {
    return (RBTerm)this.results.get(i);
  }
  
  public PredicateMode getPredicateMode()
  {
    return this.mode;
  }
  
  public Mode getMode()
  {
    return getPredicateMode().getMode();
  }
  
  public BindingList getBindingList()
  {
    return getPredicateMode().getParamModes();
  }
  
  public PredicateIdentifier getPredId()
  {
    throw new Error("This should not happen");
  }
  
  public RBTuple getArgs()
  {
    return this.argsAndResults;
  }
  
  public void addSolution()
  {
    this.solutions.add(new RBTerm[0]);
  }
  
  public void addSolution(Object o)
  {
    this.solutions.add(new RBTerm[] { RBCompoundTerm.makeJava(o) });
  }
  
  public void addSolution(Object o1, Object o2)
  {
    this.solutions.add(new RBTerm[] { RBCompoundTerm.makeJava(o1), RBCompoundTerm.makeJava(o2) });
  }
  
  public void addTermSolution(RBTerm t)
  {
    this.solutions.add(new RBTerm[] { t });
  }
  
  public void addTermSolution(RBTerm t1, RBTerm t2)
  {
    this.solutions.add(new RBTerm[] { t1, t2 });
  }
  
  public TupleType typecheck(PredInfoProvider predinfo)
    throws TypeModeError
  {
    throw new Error("This should not happen");
  }
  
  public RBComponent convertToMode(PredicateMode mode, ModeCheckContext context)
    throws TypeModeError
  {
    if (mode.equals(getPredicateMode())) {
      return this;
    }
    throw new Error("This should not happen");
  }
  
  public ArrayList eval(RBContext rb, Frame f, Frame callFrame)
  {
    this.solutions = new ArrayList();
    RBTerm[] args = new RBTerm[getNumArgs()];
    for (int i = 0; i < getNumArgs(); i++) {
      args[i] = getArgAt(i).substitute(f);
    }
    doit(args);
    ArrayList results = new ArrayList();
    for (int i = 0; i < this.solutions.size(); i++)
    {
      Frame result = (Frame)f.clone();
      RBTerm[] sols = (RBTerm[])this.solutions.get(i);
      for (int j = 0; j < sols.length; j++)
      {
        result = getResultAt(j).substitute(result).unify(sols[j], result);
        if (result == null) {
          j = sols.length;
        }
      }
      if (result != null) {
        results.add(callFrame.callResult(result));
      }
    }
    return results;
  }
  
  public String toString()
  {
    return "Implementation in mode: " + this.mode;
  }
  
  public Compiled compile(CompilationContext c)
  {
    if (getMode().hi.compareTo(Multiplicity.one) <= 0) {
      new SemiDetCompiled()
      {
        public Frame runSemiDet(Object input, RBContext context)
        {
          Frame callFrame = new Frame();
          RBTuple goal = 
            (RBTuple)((RBTuple)input).instantiate(callFrame);
          Frame fc = goal.unify(Implementation.this.getArgs(), new Frame());
          ArrayList results = Implementation.this.eval(context, fc, callFrame);
          if (results.size() == 0) {
            return null;
          }
          return (Frame)results.get(0);
        }
      };
    }
    new Compiled(getMode())
    {
      public ElementSource runNonDet(Object input, RBContext context)
      {
        Frame callFrame = new Frame();
        RBTuple goal = 
          (RBTuple)((RBTuple)input).instantiate(callFrame);
        Frame fc = goal.unify(Implementation.this.getArgs(), new Frame());
        ArrayList results = Implementation.this.eval(context, fc, callFrame);
        if (results == null) {
          return ElementSource.theEmpty;
        }
        return ElementSource.with(results);
      }
    };
  }
}
