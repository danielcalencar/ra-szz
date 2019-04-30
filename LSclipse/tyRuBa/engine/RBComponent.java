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

import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.TypeModeError;

public abstract class RBComponent
{
  public abstract Mode getMode();
  
  public abstract TupleType typecheck(PredInfoProvider paramPredInfoProvider)
    throws TypeModeError;
  
  public abstract RBComponent convertToMode(PredicateMode paramPredicateMode, ModeCheckContext paramModeCheckContext)
    throws TypeModeError;
  
  public abstract PredicateIdentifier getPredId();
  
  public String getPredName()
  {
    return getPredId().getName();
  }
  
  public abstract RBTuple getArgs();
  
  public RBComponent convertToNormalForm()
  {
    return this;
  }
  
  public boolean isGroundFact()
  {
    return false;
  }
  
  public boolean isValid()
  {
    return true;
  }
  
  public abstract Compiled compile(CompilationContext paramCompilationContext);
  
  public Validator getValidator()
  {
    return null;
  }
}
