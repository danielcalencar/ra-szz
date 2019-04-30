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
package lsclipse.rules;

import lsclipse.RefactoringQuery;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ReplaceArrayWithObject
  implements Rule
{
  private static final String NEWT_FULL_NAME = "?newtFullName";
  private static final String F_FULL_NAME = "?fFullName";
  private static final String OLDT_FULL_NAME = "?oldtFullName";
  private String name_;
  
  public ReplaceArrayWithObject()
  {
    this.name_ = "replace_array_with_object";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return getName() + "(" + "?fFullName" + "," + "?newtFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    RefactoringQuery repl = new RefactoringQuery(getName(), 
      getQueryString());
    return repl;
  }
  
  private String getQueryString()
  {
    return "deleted_fieldoftype(?fFullName, ?oldtFullName),added_fieldoftype(?fFullName, ?newtFullName),added_type(?newtFullName, ?, ?)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String oldType = rs.getString("?oldtFullName");
    if (oldType.endsWith("[]"))
    {
      String writeTo = getName() + "(\"" + rs.getString("?fFullName") + 
        "\",\"" + rs.getString("?newtFullName") + "\")";
      
      return writeTo;
    }
    return null;
  }
}
