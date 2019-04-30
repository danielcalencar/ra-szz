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

public class RenameMethod
  implements Rule
{
  private String name_;
  private static final String T_FULL_NAME = "?tFullName";
  private static final String M1_SHORT_NAME = "?m1ShortName";
  private static final String M2_SHORT_NAME = "?m2ShortName";
  public static final String M2_BODY = "?m2Body";
  public static final String M1_BODY = "?m1Body";
  public static final String M1_FULL_NAME = "?m1FullName";
  public static final String M2_FULL_NAME = "?m2FullName";
  
  public RenameMethod()
  {
    this.name_ = "rename_method";
  }
  
  public String getRefactoringString()
  {
    return 
      getName() + "(" + "?m1FullName" + "," + "?m2FullName" + "," + "?tFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private static String getQueryString()
  {
    return "deleted_method(?m1FullName, ?m1ShortName, ?tFullName), added_method(?m2FullName, ?m2ShortName, ?tFullName), NOT(equals(?m1ShortName, ?m2ShortName)), added_methodbody(?m2FullName,?m2Body), deleted_methodbody(?m1FullName,?m1Body)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String m1Body_str = rs.getString("?m1Body");
    String m2Body_str = rs.getString("?m2Body");
    if ((m2Body_str.length() > 1) && 
      (CodeCompare.compare(m1Body_str, m2Body_str)))
    {
      String writeTo = getName() + "(\"" + rs.getString("?m1FullName") + 
        "\",\"" + rs.getString("?m2FullName") + "\",\"" + 
        rs.getString("?tFullName") + "\")";
      
      return writeTo;
    }
    return null;
  }
  
  public String getName()
  {
    return this.name_;
  }
}
