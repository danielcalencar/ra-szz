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

public class ReplaceTypeCodeWithState
  implements Rule
{
  private String name_;
  
  public ReplaceTypeCodeWithState()
  {
    this.name_ = "replace_type_code_with_state";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String fShortName1 = rs.getString("?fShortName1");
    String fShortName2 = rs.getString("?fShortName2");
    String tCodeShortName1 = rs.getString("?tCodeShortName1");
    String tCodeShortName2 = rs.getString("?tCodeShortName2");
    String tCodeShortName = rs.getString("?tCodeShortName");
    String tShortName = rs.getString("?tShortName");
    
    fShortName1 = fShortName1.toLowerCase();
    fShortName2 = fShortName2.toLowerCase();
    tCodeShortName1 = tCodeShortName1.toLowerCase();
    tCodeShortName2 = tCodeShortName2.toLowerCase();
    tCodeShortName = tCodeShortName.toLowerCase();
    tShortName = tShortName.toLowerCase();
    if (((CodeCompare.compare(fShortName1, tCodeShortName1)) && 
      (CodeCompare.compare(fShortName2, tCodeShortName2))) || (
      (CodeCompare.compare(fShortName1, tCodeShortName2)) && 
      (CodeCompare.compare(fShortName2, tCodeShortName1)) && 
      (CodeCompare.compare(tCodeShortName, tShortName))))
    {
      String writeTo = getName() + "(\"" + rs.getString("?tFullName") + 
        "\",\"" + rs.getString("?tCodeFullName") + "\")";
      return writeTo;
    }
    return null;
  }
  
  private String getQueryString()
  {
    return "deleted_field(?old_fFullName1, ?fShortName1, ?tFullName), deleted_field(?old_fFullName2, ?fShortName2, ?tFullName), NOT(equals(?fShortName1, ?fShortName2)), before_fieldmodifier(?old_fFullName1, \"static\"), before_fieldmodifier(?old_fFullName2, \"static\"), before_fieldmodifier(?old_fFullName1, \"final\"), before_fieldmodifier(?old_fFullName2, \"final\"), modified_type(?tFullName, ?tShortName, ?), added_field(?new_fFullName1, ?fShortName1, ?tCodeFullName), added_field(?new_fFullName2, ?fShortName2, ?tCodeFullName), after_fieldmodifier(?new_fFullName1, \"static\"), after_fieldmodifier(?new_fFullName2, \"static\"), after_fieldmodifier(?new_fFullName1, \"final\"), after_fieldmodifier(?new_fFullName2, \"final\"), deleted_fieldoftype(?tCodefieldFullName, ?), added_fieldoftype(?tCodefieldFullName, ?tCodeFullName), added_type(?tCodeFullName, ?tCodeShortName, ?), added_type(?tCodeFullName1, ?tCodeShortName1, ?), added_type(?tCodeFullName2, ?tCodeShortName2, ?), added_subtype(?tCodeFullName, ?tCodeFullName1), added_subtype(?tCodeFullName, ?tCodeFullName2),NOT(equals(?tCodeShortName1, ?tCodeShortName2))";
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
    return getName() + "(?tFullName, ?tCodeFullName)";
  }
}
