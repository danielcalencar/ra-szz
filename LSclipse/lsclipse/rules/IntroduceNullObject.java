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

public class IntroduceNullObject
  implements Rule
{
  private static final String NULLT_FULL_NAME = "?nulltFullName";
  private static final String SERVERT_FULL_NAME = "?servertFullName";
  private static final String SERVERM_FULL_NAME = "?servermFullName";
  private static final String M_FULL_NAME = "?mFullName";
  private static final String NULL_COND = "?nullCond";
  private String name_;
  
  public IntroduceNullObject()
  {
    this.name_ = "introduce_null_object";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return 
      getName() + "(" + "?nulltFullName" + "," + "?servertFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "deleted_conditional(?nullCond, ?, ?, ?mFullName),NOT(added_conditional(?nullCond, ?, ?, ?mFullName)),before_calls(?mFullName, ?servermFullName),after_calls(?mFullName, ?servermFullName),after_method(?servermFullName, ?, ?servertFullName),added_type(?nulltFullName, ?, ?),added_subtype(?servertFullName, ?nulltFullName)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    if (!rs.getString("?nullCond").contains("==null")) {
      return null;
    }
    return 
      getName() + "(\"" + rs.getString("?nulltFullName") + "\",\"" + rs.getString("?servertFullName") + "\")";
  }
}
