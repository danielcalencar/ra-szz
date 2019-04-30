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
package tyRuBa.modes;

import java.util.ArrayList;
import tyRuBa.engine.ModedRuleBaseIndex;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBPredicateExpression;
import tyRuBa.engine.RuleBase;

public class Factory
{
  public static Bound makeBound()
  {
    return Bound.the;
  }
  
  public static Free makeFree()
  {
    return Free.the;
  }
  
  public static BindingMode makePartiallyBound()
  {
    return PatiallyBound.the;
  }
  
  public static Type makeAtomicType(TypeConstructor typeConstructor)
  {
    return typeConstructor.apply(makeTupleType(), false);
  }
  
  public static Type makeStrictAtomicType(TypeConstructor typeConstructor)
  {
    return typeConstructor.applyStrict(makeTupleType(), false);
  }
  
  public static Type makeSubAtomicType(TypeConstructor typeConstructor)
  {
    return typeConstructor.apply(makeTupleType(), true);
  }
  
  public static TVar makeTVar(String name)
  {
    return new TVar(name);
  }
  
  public static PredicateMode makePredicateMode(BindingList paramModes, Mode mode)
  {
    return makePredicateMode(paramModes, mode, true);
  }
  
  public static PredicateMode makePredicateMode(BindingList paramModes, Mode mode, boolean toBeChecked)
  {
    if (paramModes.isAllBound()) {
      return new PredicateMode(paramModes, Mode.makeSemidet(), false);
    }
    return new PredicateMode(paramModes, mode, toBeChecked);
  }
  
  public static PredicateMode makeAllBoundMode(int size)
  {
    BindingList bounds = new BindingList();
    for (int i = 0; i < size; i++) {
      bounds.add(makeBound());
    }
    return new PredicateMode(bounds, Mode.makeSemidet(), false);
  }
  
  public static PredicateMode makePredicateMode(String paramModesString, String modeString)
  {
    BindingList paramModes = makeBindingList();
    for (int i = 0; i < paramModesString.length(); i++)
    {
      char currChar = paramModesString.charAt(i);
      if ((currChar == 'b') || (currChar == 'B')) {
        paramModes.add(makeBound());
      } else if ((currChar == 'f') || (currChar == 'F')) {
        paramModes.add(makeFree());
      } else {
        throw new Error("unknown binding mode " + currChar);
      }
    }
    Mode mode = Mode.convertFromString(modeString);
    
    return makePredicateMode(paramModes, mode);
  }
  
  public static TupleType makeTupleType()
  {
    return new TupleType();
  }
  
  public static TupleType makeTupleType(Type t)
  {
    return new TupleType(new Type[] { t });
  }
  
  public static TupleType makeTupleType(Type t1, Type t2)
  {
    return new TupleType(new Type[] { t1, t2 });
  }
  
  public static TupleType makeTupleType(Type t1, Type t2, Type t3)
  {
    return new TupleType(new Type[] { t1, t2, t3 });
  }
  
  public static TupleType makeTupleType(Type t1, Type t2, Type t3, Type t4)
  {
    return new TupleType(new Type[] { t1, t2, t3, t4 });
  }
  
  public static BindingList makeBindingList()
  {
    return new BindingList();
  }
  
  public static BindingList makeBindingList(BindingMode bm)
  {
    return new BindingList(bm);
  }
  
  public static BindingList makeBindingList(int repeat, BindingMode bm)
  {
    BindingList result = makeBindingList();
    for (int i = 0; i < repeat; i++) {
      result.add(bm);
    }
    return result;
  }
  
  public static PredInfo makePredInfo(QueryEngine engine, String predName, TupleType tList)
  {
    return new PredInfo(engine, predName, tList);
  }
  
  public static PredInfo makePredInfo(QueryEngine engine, String predName, TupleType tList, ArrayList predModes, boolean isPersistent)
  {
    if (predModes.isEmpty()) {
      predModes.add(makeAllBoundMode(tList.size()));
    }
    return new PredInfo(engine, predName, tList, predModes, isPersistent);
  }
  
  public static PredInfo makePredInfo(QueryEngine engine, String predName, TupleType tList, ArrayList predModes)
  {
    return makePredInfo(engine, predName, tList, predModes, false);
  }
  
  public static Type makeListType(Type et)
  {
    return new ListType(et);
  }
  
  public static ListType makeEmptyListType()
  {
    return new ListType();
  }
  
  public static ModeCheckContext makeModeCheckContext(ModedRuleBaseIndex rulebases)
  {
    return new ModeCheckContext(new BindingEnv(), rulebases);
  }
  
  public static TypeEnv makeTypeEnv()
  {
    return new TypeEnv();
  }
  
  public static RBExpression makeModedExpression(RBExpression exp, Mode mode, ModeCheckContext context)
  {
    return exp.makeModed(mode, context);
  }
  
  public static RBExpression makeModedExpression(RBPredicateExpression exp, Mode resultMode, ModeCheckContext resultContext, RuleBase bestRuleBase)
  {
    return exp.makeModed(resultMode, resultContext, bestRuleBase);
  }
  
  public static TypeConstructor makeTypeConstructor(Class javaclass)
  {
    return new JavaTypeConstructor(javaclass);
  }
  
  public static TypeConstructor makeTypeConstructor(String name, int arity)
  {
    return new UserDefinedTypeConstructor(name, arity);
  }
}
