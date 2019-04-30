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

public class FormTemplateMethod
  implements Rule
{
  private String name_;
  
  public FormTemplateMethod()
  {
    this.name_ = "form_template_method";
  }
  
  private String getQueryString()
  {
    return "added_methodbody(?calleeMFullName1, ?new_mbody1), deleted_methodbody(?mFullName1, ?mbody1), NOT(equals(?calleeMFullName1, ?mFullName1)), added_methodbody(?calleeMFullName2, ?new_mbody2), deleted_methodbody(?mFullName2, ?mbody2), NOT(equals(?calleeMFullName2, ?mFullName2)), NOT(equals(?mFullName1, ?mFullName2)),NOT(equals(?sub_tFullName1, ?sub_tFullName2)),added_method(?mFullName, ?mShortName, ?super_tFullName), deleted_method(?mFullName1, ?mShortName, ?sub_tFullName1), deleted_method(?mFullName2, ?mShortName, ?sub_tFullName2), added_calls(?mFullName, ?calleeMFullName), added_inheritedmethod(?calleemShortName, ?super_tFullName, ?sub_tFullName1), added_inheritedmethod(?calleemShortName, ?super_tFullName, ?sub_tFullName2), added_method(?calleeMFullName1, ?calleemShortName, ?sub_tFullName1), added_method(?calleeMFullName2, ?calleemShortName, ?sub_tFullName2), after_method(?calleeMFullName, ?calleemShortName, ?super_tFullName), after_subtype(?super_tFullName, ?sub_tFullName1), after_subtype(?super_tFullName, ?sub_tFullName2) ";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String sub1 = rs.getString("?sub_tFullName1");
    String sub2 = rs.getString("?sub_tFullName2");
    String new_mbody1 = rs.getString("?new_mbody1");
    String new_mbody2 = rs.getString("?new_mbody2");
    String mbody1 = rs.getString("?mbody1");
    String mbody2 = rs.getString("?mbody2");
    if ((CodeCompare.compare(new_mbody1, mbody1)) && 
      (CodeCompare.compare(new_mbody2, mbody2)))
    {
      String second;
      String first;
      String second;
      if (sub1.compareTo(sub2) < 0)
      {
        String first = sub1;
        second = sub2;
      }
      else
      {
        first = sub2;
        second = sub1;
      }
      String writeTo = getName() + "(\"" + 
        rs.getString("?super_tFullName") + "\",\"" + first + 
        "\",\"" + second + "\",\"" + rs.getString("?mFullName") + 
        "\")";
      return writeTo;
    }
    return null;
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
    return 
      getName() + "(?super_tFullName, ?sub_tFullName1, ?sub_tFullName2, ?mFullName)";
  }
}
