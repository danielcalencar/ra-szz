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

import tyRuBa.engine.Validator;

public abstract interface ValidatorManager
{
  public abstract void add(Validator paramValidator, String paramString);
  
  public abstract void update(long paramLong, Boolean paramBoolean1, Boolean paramBoolean2);
  
  public abstract void remove(long paramLong);
  
  public abstract void remove(String paramString);
  
  public abstract Validator get(long paramLong);
  
  public abstract Validator get(String paramString);
  
  public abstract String getIdentifier(long paramLong);
  
  public abstract void printOutValidators();
  
  public abstract void backup();
  
  public abstract long getLastInvalidatedTime();
}
