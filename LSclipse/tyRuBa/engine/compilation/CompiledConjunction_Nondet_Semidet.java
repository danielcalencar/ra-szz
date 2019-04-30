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

import tyRuBa.engine.RBContext;
import tyRuBa.modes.Mode;
import tyRuBa.util.Action;
import tyRuBa.util.ElementSource;

public class CompiledConjunction_Nondet_Semidet
  extends Compiled
{
  Compiled left;
  SemiDetCompiled right;
  
  public CompiledConjunction_Nondet_Semidet(Compiled left, SemiDetCompiled right)
  {
    super(left.getMode().multiply(right.getMode()));
    this.left = left;
    this.right = right;
  }
  
  public ElementSource runNonDet(Object input, final RBContext context)
  {
    this.left.runNonDet(input, context).map(new Action()
    {
      public Object compute(Object arg)
      {
        return CompiledConjunction_Nondet_Semidet.this.right.runSemiDet(arg, context);
      }
    });
  }
  
  public String toString()
  {
    return "(" + this.right + " ==> " + this.left + ")";
  }
}
