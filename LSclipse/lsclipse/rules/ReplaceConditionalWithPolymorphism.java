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

public class ReplaceConditionalWithPolymorphism
  implements Rule
{
  private static final String SUBM_FULL_NAME = "?newmFullName";
  private static final String SUBT_FULL_NAME = "?subtFullName";
  private static final String T_FULL_NAME = "?tFullName";
  private static final String M_SHORT_NAME = "?mShortName";
  private static final String M_FULL_NAME = "?mFullName";
  private static final String CONDITION = "?condition";
  private static final String NEWM_BODY = "?newmBody";
  private static final String IF_PART = "?ifPart";
  private static final String F_FULL_NAME = "?fFullName";
  private static final String TYPET_FULL_NAME = "?typeTFullName";
  private static final String TYPEM_FULL_NAME = "?typeMFullName";
  private String name_;
  
  public ReplaceConditionalWithPolymorphism()
  {
    this.name_ = "replace_conditional_with_polymorphism";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return null;
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "deleted_conditional(?condition,?ifPart,?,?mFullName),before_method(?mFullName,?mShortName,?tFullName),(after_subtype(?tFullName,?subtFullName);(after_field(?fFullName,?,?tFullName),after_fieldoftype(?fFullName,?typeTFullName),after_subtype(?typeTFullName,?subtFullName),added_method(?typeMFullName,?mShortName,?typeTFullName),added_calls(?mFullName,?typeMFullName))),added_method(?newmFullName,?mShortName,?subtFullName),added_methodbody(?newmFullName,?newmBody)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String cond = rs.getString("?condition").toLowerCase();
    if ((!cond.toLowerCase().contains("type")) && 
      (!cond.contains("instanceof"))) {
      return null;
    }
    String newmBody_str = rs.getString("?newmBody");
    String ifPart_str = rs.getString("?ifPart");
    if ((newmBody_str.length() > 1) && 
      (CodeCompare.compare(newmBody_str, ifPart_str))) {
      return 
        getName() + "(\"" + rs.getString("?mFullName") + "\",\"" + rs.getString("?subtFullName") + "\")";
    }
    return null;
  }
}
