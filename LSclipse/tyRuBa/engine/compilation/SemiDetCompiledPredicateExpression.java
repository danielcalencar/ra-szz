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

import tyRuBa.engine.Frame;
import tyRuBa.engine.RBContext;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RuleBase;
import tyRuBa.modes.Mode;

public class SemiDetCompiledPredicateExpression
  extends SemiDetCompiled
{
  private final RuleBase rules;
  private final RBTuple args;
  
  public SemiDetCompiledPredicateExpression(Mode mode, RuleBase rules, RBTuple args)
  {
    super(mode);
    this.rules = rules;
    this.args = args;
  }
  
  public final Frame runSemiDet(Object input, RBContext context)
  {
    RBTuple goal = (RBTuple)this.args.substitute((Frame)input);
    Frame result = compiledRules().runSemiDet(goal, context);
    if (((Frame)input).isEmpty()) {
      return result;
    }
    if (result == null) {
      return null;
    }
    return ((Frame)input).append(result);
  }
  
  private SemiDetCompiled compiledRules()
  {
    return this.rules.getSemiDetCompiledRules();
  }
  
  public String toString()
  {
    return "SEMIDET PRED(" + this.args + ")";
  }
}
