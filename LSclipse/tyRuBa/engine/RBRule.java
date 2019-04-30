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

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.compilation.CompiledRule;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.ErrorMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.Multiplicity;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TVar;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBRule
  extends RBComponent
  implements Cloneable
{
  private PredicateIdentifier pred;
  private RBTuple args;
  private RBExpression cond;
  Mode mode = null;
  
  public Mode getMode()
  {
    return this.mode;
  }
  
  public RBRule(RBPredicateExpression pred, RBExpression exp)
  {
    this.pred = pred.getPredId();
    this.args = pred.getArgs();
    this.cond = exp;
  }
  
  RBRule(PredicateIdentifier pred, RBTuple args, RBExpression cond)
  {
    this.pred = pred;
    this.args = args;
    this.cond = cond;
  }
  
  RBRule(PredicateIdentifier pred, RBTuple args, RBExpression cond, Mode resultMode)
  {
    this(pred, args, cond);
  }
  
  public PredicateIdentifier getPredId()
  {
    return this.pred;
  }
  
  public RBTuple getArgs()
  {
    return this.args;
  }
  
  public final RBExpression getCondition()
  {
    return this.cond;
  }
  
  public RBComponent addCondition(RBExpression e)
  {
    return new RBRule(this.pred, this.args, FrontEnd.makeAnd(e, this.cond));
  }
  
  public Object clone()
  {
    try
    {
      return (RBRule)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new Error("This shouldn't happen!");
    }
  }
  
  public RBRule substitute(Frame frame)
  {
    RBRule r = (RBRule)clone();
    r.args = ((RBTuple)this.args.substitute(frame));
    r.cond = this.cond.substitute(frame);
    return r;
  }
  
  public String conclusionToString()
  {
    return getPredName() + getArgs();
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer(conclusionToString());
    if (this.cond != null) {
      result.append(" :- " + this.cond);
    }
    result.append(".");
    return result.toString();
  }
  
  public TupleType typecheck(PredInfoProvider predinfo)
    throws TypeModeError
  {
    try
    {
      TypeEnv startEnv = new TypeEnv();
      PredicateIdentifier pred = getPredId();
      PredInfo pInfo = predinfo.getPredInfo(pred);
      if (pInfo == null) {
        throw new TypeModeError("Unknown predicate " + pred);
      }
      TupleType predTypes = pInfo.getTypeList();
      RBTuple args = getArgs();
      int numArgs = args.getNumSubterms();
      TupleType startArgTypes = Factory.makeTupleType();
      for (int i = 0; i < numArgs; i++)
      {
        Type currStrictPart = predTypes.get(i).copyStrictPart();
        Type argType = args.getSubterm(i).getType(startEnv);
        if (!(currStrictPart instanceof TVar)) {
          argType.checkEqualTypes(currStrictPart, false);
        }
        startArgTypes.add(argType);
      }
      TypeEnv inferredTypeEnv = getCondition().typecheck(predinfo, startEnv);
      
      TupleType argTypes = Factory.makeTupleType();
      Map varRenamings = new HashMap();
      for (int i = 0; i < numArgs; i++) {
        argTypes.add(args.getSubterm(i).getType(inferredTypeEnv));
      }
      if (!argTypes.isSubTypeOf(predTypes, varRenamings)) {
        throw new TypeModeError("Inferred types " + 
          argTypes + " incompatible with declared types " + predTypes);
      }
      return argTypes;
    }
    catch (TypeModeError e)
    {
      throw new TypeModeError(e, this);
    }
  }
  
  public RBComponent convertToNormalForm()
  {
    return new RBRule(this.pred, this.args, this.cond.convertToNormalForm());
  }
  
  public RBComponent convertToMode(PredicateMode predMode, ModeCheckContext context)
    throws TypeModeError
  {
    BindingList paramModes = predMode.getParamModes();
    boolean toBeCheck = predMode.toBeCheck();
    int numArgs = this.args.getNumSubterms();
    for (int i = 0; i < numArgs; i++)
    {
      RBTerm currArg = this.args.getSubterm(i);
      if (paramModes.get(i).isBound()) {
        currArg.makeAllBound(context);
      }
    }
    RBExpression converted = this.cond.convertToMode(context);
    if ((converted.getMode() instanceof ErrorMode)) {
      throw new TypeModeError("cannot convert " + conclusionToString() + 
        ":-" + converted + 
        " to any legal mode\n" + 
        "    " + converted.getMode());
    }
    Mode resultMode = converted.getMode();
    Collection vars = this.args.getVariables();
    context.removeAllBound(vars);
    if (vars.isEmpty())
    {
      resultMode = resultMode.first();
    }
    else if (toBeCheck)
    {
      if (!resultMode.compatibleWith(predMode.getMode())) {
        throw new TypeModeError("cannot convert " + conclusionToString() + 
          ":-" + converted + 
          " to the declared mode " + predMode.getMode() + ".\n" + 
          "inferred mode was " + converted.getMode());
      }
      vars = this.args.getVariables();
      converted.getNewContext().removeAllBound(vars);
      if (!vars.isEmpty()) {
        throw new TypeModeError("Variables " + vars + 
          " do not become bound in " + this);
      }
    }
    else
    {
      resultMode = resultMode.restrictedBy(predMode.getMode());
    }
    RBRule convertedRule = new RBRule(this.pred, this.args, converted, resultMode);
    if (!RuleBase.silent) {
      System.err.println(predMode + " ==> " + convertedRule);
    }
    return convertedRule;
  }
  
  public Compiled compile(CompilationContext c)
  {
    Compiled compiledCond = this.cond.compile(c);
    if (compiledCond.getMode().hi.compareTo(this.mode.hi) > 0) {
      compiledCond = compiledCond.first();
    }
    return CompiledRule.make(this, this.args, compiledCond);
  }
}
