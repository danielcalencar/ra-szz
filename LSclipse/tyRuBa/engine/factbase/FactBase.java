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
package tyRuBa.engine.factbase;

import tyRuBa.engine.RBComponent;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.modes.PredicateMode;

public abstract class FactBase
{
  public abstract boolean isPersistent();
  
  public abstract void backup();
  
  public abstract void insert(RBComponent paramRBComponent);
  
  public abstract boolean isEmpty();
  
  public final Compiled compile(PredicateMode mode, CompilationContext context)
  {
    if (isEmpty()) {
      return Compiled.fail;
    }
    return basicCompile(mode, context);
  }
  
  public abstract Compiled basicCompile(PredicateMode paramPredicateMode, CompilationContext paramCompilationContext);
}
