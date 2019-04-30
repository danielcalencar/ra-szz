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
package tyRuBa.engine.compilation;

import tyRuBa.engine.CachedRuleBase;
import tyRuBa.engine.Frame;
import tyRuBa.engine.RBContext;
import tyRuBa.engine.SemiDetCachedRuleBase;
import tyRuBa.modes.Mode;
import tyRuBa.modes.Multiplicity;
import tyRuBa.util.Action;
import tyRuBa.util.ElementSource;

public abstract class Compiled
{
  private final Mode mode;
  
  public Compiled(Mode mode)
  {
    this.mode = mode;
  }
  
  public Mode getMode()
  {
    return this.mode;
  }
  
  public static Compiled succeed = new SemiDetCompiled(Mode.makeDet())
  {
    public ElementSource run(ElementSource in)
    {
      return in;
    }
    
    public Frame runSemiDet(Object input, RBContext context)
    {
      return new Frame();
    }
    
    public Compiled conjoin(Compiled other)
    {
      return other;
    }
    
    public Compiled disjoin(Compiled other)
    {
      return this;
    }
    
    public Compiled negate()
    {
      return Compiled.fail;
    }
    
    public String toString()
    {
      return "SUCCEED";
    }
  };
  public static Compiled fail = new SemiDetCompiled(Mode.makeFail())
  {
    public ElementSource run(ElementSource in)
    {
      return ElementSource.theEmpty;
    }
    
    public Frame runSemiDet(Object input, RBContext context)
    {
      return null;
    }
    
    public Compiled conjoin(Compiled other)
    {
      return this;
    }
    
    public Compiled disjoin(Compiled other)
    {
      return other;
    }
    
    public Compiled negate()
    {
      return Compiled.succeed;
    }
    
    public String toString()
    {
      return "FAIL";
    }
  };
  
  public ElementSource run(ElementSource inputs, final RBContext context)
  {
    
    
      inputs.map(new Action()
      {
        public Object compute(Object arg)
        {
          return Compiled.this.runNonDet(arg, context);
        }
      }).flatten();
  }
  
  public abstract ElementSource runNonDet(Object paramObject, RBContext paramRBContext);
  
  public ElementSource start(Frame putMap)
  {
    return runNonDet(putMap, new RBContext());
  }
  
  public Compiled conjoin(Compiled other)
  {
    if ((other instanceof SemiDetCompiled)) {
      return new CompiledConjunction_Nondet_Semidet(
        this, (SemiDetCompiled)other);
    }
    return new CompiledConjunction(this, other);
  }
  
  public Compiled disjoin(Compiled other)
  {
    if (other.equals(fail)) {
      return this;
    }
    if ((other instanceof SemiDetCompiled)) {
      return other.disjoin(this);
    }
    return new CompiledDisjunction(this, other);
  }
  
  public SemiDetCompiled first()
  {
    return new CompiledFirst(this);
  }
  
  public Compiled negate()
  {
    return new CompiledNot(this);
  }
  
  public Compiled test()
  {
    return new CompiledTest(this);
  }
  
  public static Compiled makeCachedRuleBase(Compiled compiledRules)
  {
    if (compiledRules.getMode().hi.compareTo(Multiplicity.one) <= 0) {
      return new SemiDetCachedRuleBase((SemiDetCompiled)compiledRules);
    }
    return new CachedRuleBase(compiledRules);
  }
}
