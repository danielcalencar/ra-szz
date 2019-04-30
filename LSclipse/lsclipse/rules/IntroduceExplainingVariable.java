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

import java.util.regex.Pattern;
import lsclipse.RefactoringQuery;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class IntroduceExplainingVariable
  implements Rule
{
  private static final String NEWM_BODY = "?newmBody";
  private static final String M_BODY = "?mBody";
  private static final String EXPRESSION = "?expression";
  private static final String IDENTIFIER = "?identifier";
  private static final String M_FULL_NAME = "?mFullName";
  private String name_;
  
  public IntroduceExplainingVariable()
  {
    this.name_ = "introduce_explaining_variable";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return 
      getName() + "(" + "?identifier" + "," + "?expression" + "," + "?mFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "added_localvar(?mFullName,?,?identifier,?expression),NOT(deleted_localvar(?mFullName,?,?identifier,?)),NOT(deleted_localvar(?mFullName,?,?,?expression)),deleted_methodbody(?mFullName,?mBody),added_methodbody(?mFullName,?newmBody)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String expression = rs.getString("?expression");
    String mBody = rs.getString("?mBody");
    String newmBody = rs.getString("?newmBody");
    
    int oldCount = Pattern.compile(expression, 16).split(
      mBody, -1).length - 1;
    int newCount = Pattern.compile(expression, 16).split(
      newmBody, -1).length - 1;
    if (newCount > oldCount) {
      return null;
    }
    if (oldCount < 1) {
      return null;
    }
    return 
      getName() + "(\"" + rs.getString("?identifier") + "\",\"" + expression + "\",\"" + rs.getString("?mFullName") + "\")";
  }
}
