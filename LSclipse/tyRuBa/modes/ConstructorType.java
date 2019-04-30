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

import java.util.ArrayList;
import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.RBTerm;

public abstract class ConstructorType
{
  public abstract FunctorIdentifier getFunctorId();
  
  public abstract TypeConstructor getTypeConst();
  
  public abstract int getArity();
  
  public abstract RBTerm apply(RBTerm paramRBTerm);
  
  public abstract RBTerm apply(ArrayList paramArrayList);
  
  public abstract Type apply(Type paramType)
    throws TypeModeError;
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public static ConstructorType makeUserDefined(FunctorIdentifier functorId, Type repAs, CompositeType type)
  {
    if (repAs.isJavaType()) {
      return new RepAsJavaConstructorType(functorId, repAs, type);
    }
    return new GenericConstructorType(functorId, repAs, type);
  }
  
  public static ConstructorType makeJava(Class javaClass)
  {
    return new JavaConstructorType(javaClass);
  }
}
