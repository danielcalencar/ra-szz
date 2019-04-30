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

public class IntroduceParamObject
  implements Rule
{
  private String name_;
  
  public IntroduceParamObject()
  {
    this.name_ = "introduce_parameter_object";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String new_param = getTypeofFieldfromParam(rs.getString("?new_param"));
    String old_param1 = getTypeofFieldfromParam(rs.getString("?old_param1"));
    String old_param2 = getTypeofFieldfromParam(rs.getString("?old_param2"));
    String fType1 = getTypeofFieldfromFOT(rs.getString("?fType1"));
    String fType2 = getTypeofFieldfromFOT(rs.getString("?fType2"));
    String tShortName = rs.getString("?tShortName");
    if (((old_param1.equals(fType1)) && 
      (old_param2.equals(fType2))) || (
      (old_param1.equals(fType2)) && 
      (old_param2.equals(fType1)) && 
      (new_param.equals(tShortName))))
    {
      String writeTo = getName() + "(\"" + rs.getString("?mFullName") + 
        "\",\"" + rs.getString("?tFullName") + "\")";
      return writeTo;
    }
    return null;
  }
  
  private static String getTypeofFieldfromParam(String arg)
  {
    return arg.substring(0, arg.indexOf(':'));
  }
  
  private static String getTypeofFieldfromFOT(String arg)
  {
    if (arg.indexOf('.') == -1) {
      return arg;
    }
    return arg.substring(arg.indexOf('.') + 1);
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    RefactoringQuery repl = new RefactoringQuery(getName(), 
      getQueryString());
    return repl;
  }
  
  private String getQueryString()
  {
    return "deleted_parameter(?mFullName,?, ?old_param1), deleted_parameter(?mFullName,?, ?old_param2), NOT(equals(?old_param1, ?old_param2)),added_parameter(?mFullName,?, ?new_param), added_type(?tFullName, ?tShortName, ?), added_field(?fFullName1, ?, ?tFullName), added_fieldoftype(?fFullName1, ?fType1), added_field(?fFullName2, ?, ?tFullName), added_fieldoftype(?fFullName2, ?fType2), NOT(equals(?fFullName1, ?fFullName2))";
  }
  
  public String getRefactoringString()
  {
    return getName() + "(?mFullName, ?tFullName)";
  }
}
