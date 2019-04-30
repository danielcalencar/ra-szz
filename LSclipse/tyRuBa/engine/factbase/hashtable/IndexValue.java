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
package tyRuBa.engine.factbase.hashtable;

import java.io.Serializable;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.Validator;
import tyRuBa.engine.factbase.ValidatorManager;

public class IndexValue
  implements Serializable
{
  private long validatorHandle;
  private RBTuple parts;
  
  public static IndexValue make(Validator v, RBTuple parts)
  {
    if (v == null) {
      return new IndexValue(0L, parts);
    }
    return new IndexValue(v.handle(), parts);
  }
  
  public static IndexValue make(long validatorHandle, RBTuple parts)
  {
    return new IndexValue(validatorHandle, parts);
  }
  
  private IndexValue(long validatorHandle, RBTuple parts)
  {
    this.validatorHandle = validatorHandle;
    this.parts = parts;
  }
  
  public long getValidatorHandle()
  {
    return this.validatorHandle;
  }
  
  public RBTuple getParts()
  {
    return this.parts;
  }
  
  public boolean isValid(ValidatorManager vm)
  {
    if (this.validatorHandle == 0L) {
      return true;
    }
    Validator v = vm.get(this.validatorHandle);
    if ((v == null) || (!v.isValid())) {
      return false;
    }
    return true;
  }
  
  public IndexValue prepend(RBTuple tuple)
  {
    return new IndexValue(this.validatorHandle, tuple.append(this.parts));
  }
  
  public String toString()
  {
    return this.parts.toString();
  }
}
