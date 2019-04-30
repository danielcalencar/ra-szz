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

public class ReplaceNestedCondWithGuardClauses
  implements Rule
{
  private String name_;
  
  public ReplaceNestedCondWithGuardClauses()
  {
    this.name_ = "replace_nested_cond_guard_clauses";
  }
  
  private String getQueryString()
  {
    return "deleted_conditional(?old_cond1, ?old_ifPart1, ?old_elsePart1, ?mFullName), added_conditional(?new_cond1, ?new_ifPart1, \"\", ?mFullName), added_conditional(?new_cond2, ?new_ifPart2, \"\", ?mFullName)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String old_cond1 = rs.getString("?old_cond1");
    String new_cond1 = rs.getString("?new_cond1");
    String new_cond2 = rs.getString("?new_cond2");
    String old_ifPart1 = rs.getString("?old_ifPart1");
    String new_ifPart1 = rs.getString("?new_ifPart1");
    String new_ifPart2 = rs.getString("?new_ifPart2");
    String old_elsePart1 = rs.getString("?old_elsePart1");
    if ((CodeCompare.compare(old_cond1, new_cond1)) && 
      (CodeCompare.compare(old_ifPart1, new_ifPart1)) && 
      (CodeCompare.compare(new_cond2, old_elsePart1)) && 
      (CodeCompare.compare(new_ifPart2, old_elsePart1)))
    {
      String writeTo = getName() + "(\"" + rs.getString("?mFullName") + 
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
    return getName() + "(?mFullName)";
  }
}
