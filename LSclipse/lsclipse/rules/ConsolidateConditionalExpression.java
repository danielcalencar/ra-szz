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

public class ConsolidateConditionalExpression
  implements Rule
{
  private String name_;
  
  public ConsolidateConditionalExpression()
  {
    this.name_ = "consolidate_cond_expression";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String old_cond1 = rs.getString("?old_cond1");
    String old_cond2 = rs.getString("?old_cond2");
    String new_cond = rs.getString("?new_cond");
    String extMthdBody = "";
    boolean foundExtract = true;
    try
    {
      extMthdBody = rs.getString("?extMthdBody");
    }
    catch (NullPointerException localNullPointerException)
    {
      foundExtract = false;
    }
    if (foundExtract)
    {
      if ((!extMthdBody.contains(old_cond1)) || 
        (!extMthdBody.contains(old_cond2))) {
        return null;
      }
    }
    else if ((!new_cond.contains(old_cond1)) || 
      (!new_cond.contains(old_cond2))) {
      return null;
    }
    String writeTo = getName() + "(\"" + rs.getString("?mFullName") + 
      "\")";
    
    return writeTo;
  }
  
  private String getQueryString()
  {
    return "(deleted_conditional(?old_cond1, ?ifPart, ?elsePart, ?mFullName), deleted_conditional(?old_cond2, ?ifPart, ?elsePart, ?mFullName), added_conditional(?new_cond, ?ifPart, ?elsePart, ?mFullName), extract_method(?mFullName, ?, ?extMthdBody, ?)); (deleted_conditional(?old_cond1, ?ifPart, ?elsePart, ?mFullName), deleted_conditional(?old_cond2, ?ifPart, ?elsePart, ?mFullName), added_conditional(?new_cond, ?ifPart, ?elsePart, ?mFullName))";
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
