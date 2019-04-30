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

public class RemoveControlFlag
  implements Rule
{
  private static final String CONDITION = "?condition";
  private static final String EXPRESSION = "?expression";
  private static final String IDENTIFIER = "?identifier";
  private static final String M_FULL_NAME = "?mFullName";
  private String name_;
  
  public RemoveControlFlag()
  {
    this.name_ = "remove_control_flag";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return getName() + "(" + "?identifier" + "," + "?mFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "deleted_localvar(?mFullName,\"boolean\",?identifier,?expression),deleted_conditional(?condition,?,?,?mFullName),NOT(added_conditional(?condition,?,?,?mFullName))";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String condition = rs.getString("?condition");
    String identifier = rs.getString("?identifier");
    if (!condition.contains(identifier)) {
      return null;
    }
    return 
      getName() + "(\"" + identifier + "\",\"" + rs.getString("?mFullName") + "\")";
  }
}
