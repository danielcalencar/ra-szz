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

public class MoveMethod
  implements Rule
{
  private String name_;
  private static final String PACKAGE = "?package";
  private static final String T2_FULL_NAME = "?t2FullName";
  private static final String T1_FULL_NAME = "?t1FullName";
  private static final String M_SHORT_NAME = "?mShortName";
  public static final String M2_BODY = "?m2Body";
  public static final String M1_BODY = "?m1Body";
  public static final String M1_FULL_NAME = "?m1FullName";
  public static final String M2_FULL_NAME = "?m2FullName";
  
  public MoveMethod()
  {
    this.name_ = "move_method";
  }
  
  public String getRefactoringString()
  {
    return 
      getName() + "(" + "?mShortName" + "," + "?t1FullName" + "," + "?t2FullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private static String getQueryString()
  {
    return "deleted_method(?m1FullName, ?mShortName, ?t1FullName), added_method(?m2FullName, ?mShortName, ?t2FullName), before_type(?t1FullName, ?, ?package), after_type(?t2FullName, ?, ?package), NOT(equals(?t1FullName, ?t2FullName)), added_methodbody(?m2FullName,?m2Body), deleted_methodbody(?m1FullName,?m1Body),NOT(equals(?m2FullName,?m1FullName))";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String mShortName = rs.getString("?mShortName");
    if (mShortName.equals("<init>()")) {
      return null;
    }
    String newmBody_str = rs.getString("?m2Body");
    String mBody_str = rs.getString("?m1Body");
    if ((newmBody_str.length() > 1) && 
      (CodeCompare.compare(newmBody_str, mBody_str)))
    {
      String writeTo = getName() + "(\"" + rs.getString("?mShortName") + 
        "\",\"" + rs.getString("?t1FullName") + "\",\"" + 
        rs.getString("?t2FullName") + "\")";
      
      return writeTo;
    }
    return null;
  }
  
  public String getName()
  {
    return this.name_;
  }
}
