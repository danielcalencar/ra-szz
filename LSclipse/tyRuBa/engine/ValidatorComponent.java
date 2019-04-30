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

public class ValidatorComponent
  extends RBComponent
{
  private Validator validator;
  private RBComponent comp;
  
  public ValidatorComponent(RBComponent c, Validator validator)
  {
    this.validator = validator;
    this.comp = c;
  }
  
  public boolean isValid()
  {
    return (this.validator != null) && (this.validator.isValid());
  }
  
  void checkValid()
  {
    if (!isValid()) {
      throw new Error("Internal Error: Using an invalidated component: " + 
        this);
    }
  }
  
  public Validator getValidator()
  {
    return this.validator;
  }
  
  public RBTuple getArgs()
  {
    checkValid();
    return this.comp.getArgs();
  }
  
  public PredicateIdentifier getPredId()
  {
    checkValid();
    return this.comp.getPredId();
  }
  
  public TupleType typecheck(PredInfoProvider predinfos)
    throws TypeModeError
  {
    checkValid();
    return this.comp.typecheck(predinfos);
  }
  
  public RBComponent convertToNormalForm()
  {
    checkValid();
    return new ValidatorComponent(this.comp.convertToNormalForm(), this.validator);
  }
  
  public boolean isGroundFact()
  {
    checkValid();
    return this.comp.isGroundFact();
  }
  
  public String toString()
  {
    if (isValid()) {
      return this.comp.toString();
    }
    return "ValidatorComponent(INVALIDATED," + this.comp + ")";
  }
  
  public RBComponent convertToMode(PredicateMode mode, ModeCheckContext context)
    throws TypeModeError
  {
    checkValid();
    RBComponent converted = this.comp.convertToMode(mode, context);
    return new ValidatorComponent(converted, this.validator);
  }
  
  public Mode getMode()
  {
    checkValid();
    return this.comp.getMode();
  }
  
  public Compiled compile(CompilationContext c)
  {
    checkValid();
    return this.comp.compile(c);
  }
}
