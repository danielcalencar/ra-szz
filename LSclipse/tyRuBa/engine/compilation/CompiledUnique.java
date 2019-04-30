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
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBVariable;
import tyRuBa.util.ElementSource;

public class CompiledUnique
  extends SemiDetCompiled
{
  RBVariable[] vars;
  Compiled exp;
  
  public CompiledUnique(RBVariable[] vars, Compiled exp)
  {
    this.vars = vars;
    this.exp = exp;
  }
  
  public Frame runSemiDet(Object input, RBContext context)
  {
    Frame f = (Frame)input;
    Frame newf = (Frame)f.clone();
    RBTerm[] vals = new RBTerm[this.vars.length];
    newf.removeVars(this.vars);
    for (int i = 0; i < this.vars.length; i++) {
      vals[i] = this.vars[i].substitute(f);
    }
    ElementSource result = this.exp.runNonDet(newf, context);
    if (!result.hasMoreElements()) {
      return null;
    }
    int i;
    for (; result.hasMoreElements(); i < vals.length)
    {
      Frame currentFrame = (Frame)result.nextElement();
      i = 0; continue;
      newf = vals[i].unify(this.vars[i].substitute(currentFrame), newf);
      if (newf == null) {
        return null;
      }
      i++;
    }
    return newf;
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer("UNIQUE(");
    for (int i = 0; i < this.vars.length; i++)
    {
      if (i > 0) {
        result.append(",");
      }
      result.append(this.vars[i]);
    }
    result.append(": " + this.exp + ")");
    return result.toString();
  }
}
