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
import lsclipse.utils.CodeCompare;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ReplaceMethodWithMethodObject
  implements Rule
{
  private static final String CALLINGT_FULL_NAME = "?callingtFullName";
  private static final String T_FULL_NAME = "?tFullName";
  public static final String NEWM_BODY = "?newmBody";
  public static final String M_BODY = "?mBody";
  public static final String M_FULL_NAME = "?mFullName";
  public static final String NEWM_FULL_NAME = "?newmFullName";
  private String name_;
  
  public ReplaceMethodWithMethodObject()
  {
    this.name_ = "replace_method_with_method_object";
  }
  
  public String getRefactoringString()
  {
    return getName() + "(" + "?mFullName" + "," + "?tFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private static String getQueryString()
  {
    return "added_type(?tFullName, ?tShortName, ?), added_method(?newmFullName, ?, ?tFullName),added_calls(?mFullName, ?newmFullName),after_method(?mFullName, ?, ?callingtFullName), added_methodbody(?newmFullName,?newmBody), deleted_methodbody(?mFullName,?mBody)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String newmBody_str = rs.getString("?newmBody");
    String mBody_str = rs.getString("?mBody");
    if ((newmBody_str.length() > 1) && 
      (CodeCompare.compare(newmBody_str, mBody_str)))
    {
      String writeTo = getName() + "(\"" + rs.getString("?mFullName") + 
        "\",\"" + rs.getString("?tFullName") + "\")";
      
      return writeTo;
    }
    return null;
  }
  
  public String getName()
  {
    return this.name_;
  }
}
