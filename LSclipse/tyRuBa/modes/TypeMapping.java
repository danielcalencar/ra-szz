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

public abstract class TypeMapping
  implements Serializable
{
  private ConstructorType functor;
  
  public abstract Class getMappedClass();
  
  public abstract Object toTyRuBa(Object paramObject);
  
  public abstract Object toJava(Object paramObject);
  
  public ConstructorType getFunctor()
  {
    return this.functor;
  }
  
  public void setFunctor(ConstructorType functor)
  {
    if (this.functor != null) {
      throw new Error("Double mapping for " + this + ": " + this.functor + " and " + functor);
    }
    this.functor = functor;
  }
}
