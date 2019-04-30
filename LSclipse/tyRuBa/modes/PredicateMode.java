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

public class PredicateMode
{
  private BindingList paramModes;
  private Mode mode;
  private boolean toBeCheck;
  
  public PredicateMode(BindingList paramModes, Mode mode, boolean toBeCheck)
  {
    this.paramModes = paramModes;
    this.mode = mode;
    this.toBeCheck = toBeCheck;
  }
  
  public int hashCode()
  {
    return this.paramModes.hashCode() + 
      13 * (this.mode.hashCode() + 13 * getClass().hashCode());
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof PredicateMode))
    {
      PredicateMode cother = (PredicateMode)other;
      
      return (this.paramModes.equals(cother.paramModes)) && (this.mode.equals(cother.mode));
    }
    return false;
  }
  
  public String toString()
  {
    return this.paramModes + " IS " + this.mode;
  }
  
  public BindingList getParamModes()
  {
    return this.paramModes;
  }
  
  public Mode getMode()
  {
    return (Mode)this.mode.clone();
  }
  
  public boolean toBeCheck()
  {
    return this.toBeCheck;
  }
}
