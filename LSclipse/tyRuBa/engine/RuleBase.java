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
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.engine.compilation.SemiDetCompiled;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.Mode;
import tyRuBa.modes.Multiplicity;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.TypeModeError;

public abstract class RuleBase
{
  private QueryEngine engine;
  long upToDateWith = -1L;
  
  private boolean uptodateCheck()
  {
    if (this.upToDateWith < this.engine.frontend().updateCounter)
    {
      forceRecompilation();
      return false;
    }
    return true;
  }
  
  private void forceRecompilation()
  {
    this.compiledRules = null;
    this.semidetCompiledRules = null;
  }
  
  private Compiled compiledRules = null;
  private SemiDetCompiled semidetCompiledRules = null;
  public static boolean useCache = true;
  public static boolean softCache = true;
  public static boolean silent = false;
  public static boolean autoUpdate = true;
  public static final boolean debug_tracing = false;
  public static final boolean debug_checking = false;
  private PredicateMode predMode;
  private boolean isPersistent;
  
  protected RuleBase(QueryEngine engine, PredicateMode predMode, boolean isSQLAble)
  {
    this.engine = engine;
    this.predMode = predMode;
    this.isPersistent = isSQLAble;
  }
  
  public PredicateMode getPredMode()
  {
    return this.predMode;
  }
  
  public BindingList getParamModes()
  {
    return getPredMode().getParamModes();
  }
  
  public Mode getMode()
  {
    return getPredMode().getMode();
  }
  
  public boolean isBetterThan(RuleBase other)
  {
    return getMode().isBetterThan(other.getMode());
  }
  
  public static BasicModedRuleBaseIndex make(FrontEnd frontEnd)
  {
    return new BasicModedRuleBaseIndex(frontEnd, null);
  }
  
  public static BasicModedRuleBaseIndex make(FrontEnd frontEnd, String identifier, boolean temporary)
  {
    return new BasicModedRuleBaseIndex(frontEnd, identifier);
  }
  
  public abstract void insert(RBComponent paramRBComponent, ModedRuleBaseIndex paramModedRuleBaseIndex, TupleType paramTupleType)
    throws TypeModeError;
  
  public void retract(RBFact f)
  {
    throw new Error("Unsupported operation RETRACT");
  }
  
  public RuleBase addCondition(RBExpression e)
  {
    throw new Error("Operation not implemented");
  }
  
  public void dumpFacts(PrintStream out) {}
  
  public Compiled getCompiled()
  {
    uptodateCheck();
    if (this.compiledRules == null)
    {
      this.compiledRules = compile(new CompilationContext());
      if (useCache) {
        this.compiledRules = Compiled.makeCachedRuleBase(this.compiledRules);
      }
      this.upToDateWith = this.engine.frontend().updateCounter;
    }
    return this.compiledRules;
  }
  
  public SemiDetCompiled getSemiDetCompiledRules()
  {
    uptodateCheck();
    if (this.semidetCompiledRules == null)
    {
      Compiled compiled = getCompiled();
      if (compiled.getMode().hi.compareTo(Multiplicity.one) > 0) {
        this.semidetCompiledRules = compiled.first();
      } else {
        this.semidetCompiledRules = ((SemiDetCompiled)compiled);
      }
      this.upToDateWith = this.engine.frontend().updateCounter;
    }
    return this.semidetCompiledRules;
  }
  
  protected abstract Compiled compile(CompilationContext paramCompilationContext);
  
  public boolean isPersistent()
  {
    return this.isPersistent;
  }
}
