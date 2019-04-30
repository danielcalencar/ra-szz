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
import tyRuBa.modes.Mode;
import tyRuBa.util.ElementSource;

public class CompiledFirst
  extends SemiDetCompiled
{
  Compiled compiled;
  
  public CompiledFirst(Compiled compiled)
  {
    super(compiled.getMode().first());
    this.compiled = compiled;
  }
  
  public Frame runSemiDet(Object input, RBContext context)
  {
    ElementSource result = this.compiled.runNonDet(input, context);
    if (result.hasMoreElements()) {
      return (Frame)result.nextElement();
    }
    return null;
  }
  
  public String toString()
  {
    return "FIRST(" + this.compiled + ")";
  }
}
