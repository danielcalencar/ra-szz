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
package tyRuBa.util;

import java.io.Serializable;

public class SimpleTwoLevelKey
  implements TwoLevelKey, Serializable
{
  String first;
  Object second;
  
  public SimpleTwoLevelKey(String first, Object second)
  {
    this.first = first;
    if ((this.first instanceof String)) {
      this.first = this.first.intern();
    }
    this.second = second;
    if ((this.second instanceof String)) {
      this.second = ((String)this.second).intern();
    }
  }
  
  public String getFirst()
  {
    return this.first;
  }
  
  public Object getSecond()
  {
    return this.second;
  }
}
