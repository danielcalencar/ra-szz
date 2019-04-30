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

import lsclipse.LCS;
import lsclipse.RefactoringQuery;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ConsolidateDuplicateConditionalFragment
  implements Rule
{
  private String name_;
  
  public ConsolidateDuplicateConditionalFragment()
  {
    this.name_ = "consolidate_duplicate_cond_fragments";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String old_elsePart = rs.getString("?old_elsePart");
    String new_elsePart = rs.getString("?new_elsePart");
    String old_ifPart = rs.getString("?old_ifPart");
    String new_ifPart = rs.getString("?new_ifPart");
    String body = rs.getString("?mbody");
    if (old_elsePart.equals("")) {
      return null;
    }
    if ((similar_fragments(old_elsePart, new_elsePart, body)) && 
      (similar_fragments(old_ifPart, new_ifPart, body)))
    {
      String writeTo = getName() + "(\"" + rs.getString("?mFullName") + 
        "\")";
      return writeTo;
    }
    return null;
  }
  
  private String getQueryString()
  {
    return "deleted_conditional(?cond, ?old_ifPart, ?old_elsePart, ?mFullName), added_conditional(?cond, ?new_ifPart, ?new_elsePart, ?mFullName), after_methodbody(?mFullName, ?mbody)";
  }
  
  public boolean similar_fragments(String old, String news, String body)
  {
    String lcs = LCS.getLCS(old, news);
    
    int index = old.indexOf(lcs);
    if (index == -1) {
      return false;
    }
    String prefix = old.substring(0, index);
    String suffix = old.substring(index + lcs.length());
    if ((body.contains(prefix)) && (body.contains(suffix))) {
      return true;
    }
    return false;
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
