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
package tyRuBa.modes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;

public class GenericConstructorType
  extends ConstructorType
  implements Serializable
{
  FunctorIdentifier identifier;
  Type args;
  CompositeType result;
  
  public GenericConstructorType(FunctorIdentifier identifier, Type args, CompositeType result)
  {
    this.identifier = identifier;
    this.args = args;
    this.result = result;
  }
  
  public FunctorIdentifier getFunctorId()
  {
    return this.identifier;
  }
  
  public TypeConstructor getTypeConst()
  {
    return this.result.getTypeConstructor();
  }
  
  public int getArity()
  {
    if ((this.args instanceof TupleType)) {
      return ((TupleType)this.args).size();
    }
    return 1;
  }
  
  public RBTerm apply(RBTerm tuple)
  {
    return RBCompoundTerm.make(this, tuple);
  }
  
  public RBTerm apply(ArrayList terms)
  {
    return apply(RBTuple.make(terms));
  }
  
  public Type apply(Type argType)
    throws TypeModeError
  {
    Map renamings = new HashMap();
    Type iargs = this.args.clone(renamings);
    CompositeType iresult = (CompositeType)this.result.clone(renamings);
    
    argType.checkEqualTypes(iargs);
    return iresult.getTypeConstructor().apply(iresult.getArgs(), true);
  }
  
  public boolean equals(Object other)
  {
    if (other.getClass() != getClass()) {
      return false;
    }
    GenericConstructorType ctOther = (GenericConstructorType)other;
    return (this.args.equals(ctOther.args)) && (this.identifier.equals(ctOther.identifier)) && (this.result.equals(ctOther.result));
  }
  
  public int hashCode()
  {
    return this.args.hashCode() + this.identifier.hashCode() * 13 + this.result.hashCode() * 31;
  }
}
