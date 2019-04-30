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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.compilation.CompiledFact;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TVar;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBFact
  extends RBComponent
  implements Cloneable
{
  private RBTuple args;
  private PredicateIdentifier pred;
  
  public RBFact(RBPredicateExpression e)
  {
    this.pred = e.getPredId();
    this.args = e.getArgs();
  }
  
  public PredicateIdentifier getPredId()
  {
    return this.pred;
  }
  
  public RBTuple getArgs()
  {
    return this.args;
  }
  
  public Object clone()
  {
    try
    {
      RBFact cl = (RBFact)super.clone();
      cl.args = ((RBTuple)this.args.clone());
      return cl;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new Error("This shouldn't happen!");
    }
  }
  
  public String toString()
  {
    return getPredName() + this.args + ".";
  }
  
  public boolean isGround()
  {
    return this.args.isGround();
  }
  
  public boolean isGroundFact()
  {
    return isGround();
  }
  
  public TupleType typecheck(PredInfoProvider predinfo)
    throws TypeModeError
  {
    try
    {
      TypeEnv startEnv = new TypeEnv();
      PredicateIdentifier pred = getPredId();
      PredInfo pInfo = predinfo.getPredInfo(pred);
      TupleType predTypes = pInfo.getTypeList();
      
      RBTuple args = getArgs();
      int numArgs = args.getNumSubterms();
      TupleType startArgTypes = Factory.makeTupleType();
      for (int i = 0; i < numArgs; i++)
      {
        Type currStrictPart = predTypes.get(i).copyStrictPart();
        Type argType = args.getSubterm(i).getType(startEnv);
        if (!(currStrictPart instanceof TVar)) {
          argType = argType.intersect(currStrictPart);
        }
        startArgTypes.add(argType);
      }
      Map varRenamings = new HashMap();
      if (!startArgTypes.isSubTypeOf(predTypes, varRenamings)) {
        throw new TypeModeError("Inferred types " + 
          startArgTypes + " incompatible with declared types " + predTypes);
      }
      return startArgTypes;
    }
    catch (TypeModeError e)
    {
      throw new TypeModeError(e, this);
    }
  }
  
  public RBComponent convertToMode(PredicateMode mode, ModeCheckContext context)
    throws TypeModeError
  {
    BindingList paramModes = mode.getParamModes();
    
    RBTuple args = getArgs();
    for (int i = 0; i < args.getNumSubterms(); i++) {
      if (paramModes.get(i).isBound()) {
        args.getSubterm(i).makeAllBound(context);
      }
    }
    Collection vars = args.getVariables();
    context.removeAllBound(vars);
    if (!vars.isEmpty()) {
      throw new TypeModeError("Variables: " + vars + 
        " do not become bound in " + this);
    }
    return this;
  }
  
  public Mode getMode()
  {
    return Mode.makeSemidet();
  }
  
  public Compiled compile(CompilationContext c)
  {
    return new CompiledFact(this.args);
  }
}
