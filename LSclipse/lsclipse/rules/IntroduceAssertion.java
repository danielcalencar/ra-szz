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

public class IntroduceAssertion
  implements Rule
{
  private static final String NEWM_BODY = "?newmBody";
  private static final String OLDM_BODY = "?oldmBody";
  private static final String M_FULL_NAME = "?mFullName";
  private String name_;
  
  public IntroduceAssertion()
  {
    this.name_ = "introduce_assertion";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return getName() + "(" + "?mFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "deleted_methodbody(?mFullName,?oldmBody),added_methodbody(?mFullName,?newmBody)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String oldmBody = rs.getString("?oldmBody");
    String newmBody = rs.getString("?newmBody");
    int oldNumAsserts = oldmBody.split("assert").length - 1;
    int newNumAsserts = newmBody.split("assert").length - 1;
    if (newNumAsserts > oldNumAsserts) {
      return getName() + "(\"" + rs.getString("?mFullName") + "\")";
    }
    return null;
  }
}
