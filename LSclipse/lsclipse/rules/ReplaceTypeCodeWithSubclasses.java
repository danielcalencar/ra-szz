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

public class ReplaceTypeCodeWithSubclasses
  implements Rule
{
  private String name_;
  
  public ReplaceTypeCodeWithSubclasses()
  {
    this.name_ = "replace_type_code_with_subclasses";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String fShortName1 = rs.getString("?fShortName1");
    String fShortName2 = rs.getString("?fShortName2");
    String tCodeShortName1 = rs.getString("?tCodeShortName1");
    String tCodeShortName2 = rs.getString("?tCodeShortName2");
    
    fShortName1 = fShortName1.toLowerCase();
    fShortName2 = fShortName2.toLowerCase();
    tCodeShortName1 = tCodeShortName1.toLowerCase();
    tCodeShortName2 = tCodeShortName2.toLowerCase();
    if (((CodeCompare.compare(fShortName1, tCodeShortName1)) && 
      (CodeCompare.compare(fShortName2, tCodeShortName2))) || (
      (CodeCompare.compare(fShortName1, tCodeShortName2)) && 
      (CodeCompare.compare(fShortName2, tCodeShortName1))))
    {
      String writeTo = getName() + "(\"" + rs.getString("?tFullName") + 
        "\")";
      return writeTo;
    }
    return null;
  }
  
  private String getQueryString()
  {
    return "before_field(?fFullName1, ?fShortName1, ?tFullName), before_field(?fFullName2, ?fShortName2, ?tFullName), NOT(equals(?fFullName1, ?fFullName2)), before_fieldmodifier(?fFullName1, \"static\"), before_fieldmodifier(?fFullName2, \"static\"), before_fieldmodifier(?fFullName1, \"final\"), before_fieldmodifier(?fFullName2, \"final\"), deleted_field(?tCodefFullName, ?, ?tFullName), added_type(?tCodeFullName1, ?tCodeShortName1, ?), added_type(?tCodeFullName2, ?tCodeShortName2, ?), NOT(equals(?tCodeFullName1, ?tCodeFullName2)),added_subtype(?tFullName, ?tCodeFullName1), added_subtype(?tFullName, ?tCodeFullName2)";
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
  
  public String getRefactoringString()
  {
    return getName() + "(?tFullName)";
  }
}
