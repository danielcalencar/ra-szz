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
import tyRuBa.engine.RBAvoidRecursion;
import tyRuBa.engine.RBContext;
import tyRuBa.engine.RBRule;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;

public class SemiDetCompiledRule
  extends SemiDetCompiled
{
  RBTuple args;
  SemiDetCompiled compiledCond;
  RBRule rule;
  
  public SemiDetCompiledRule(RBRule rule, RBTuple args, SemiDetCompiled compiledCond)
  {
    super(rule.getMode());
    this.args = args;
    this.compiledCond = compiledCond;
    this.rule = rule;
  }
  
  public Frame runSemiDet(Object input, RBContext context)
  {
    RBTerm goaL = (RBTerm)input;
    
    Frame callFrame = new Frame();
    
    RBTuple goal = (RBTuple)goaL.instantiate(callFrame);
    
    Frame fc = goal.unify(this.args, new Frame());
    if (fc == null) {
      return null;
    }
    RBRule r = this.rule.substitute(fc);
    context = new RBAvoidRecursion(context, r);
    Frame result = this.compiledCond.runSemiDet(fc, context);
    if (result == null) {
      return null;
    }
    return callFrame.callResult(result);
  }
  
  public String toString()
  {
    return "RULE(" + this.args + " :- " + this.compiledCond + ")";
  }
}
