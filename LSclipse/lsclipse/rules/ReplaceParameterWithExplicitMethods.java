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

public class ReplaceParameterWithExplicitMethods
  implements Rule
{
  private static final String M2_FULL_NAME = "?m2FullName";
  private static final String T_FULL_NAME = "?tFullName";
  private static final String M1_FULL_NAME = "?m1FullName";
  private static final String OLDPARAMS = "?oldparams";
  private static final String PARAMS2 = "?params2";
  private static final String PARAMS1 = "?params1";
  private static final String OLDM_SHORT_NAME = "?oldmShortName";
  private static final String M2_SHORT_NAME = "?m2ShortName";
  private static final String M1_SHORT_NAME = "?m1ShortName";
  private static final String OLDM_FULL_NAME = "?oldmFullName";
  private String name_;
  
  public ReplaceParameterWithExplicitMethods()
  {
    this.name_ = "replace_param_explicit_methods";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return getName() + "(" + "?oldmFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "added_method(?m1FullName,?m1ShortName,?tFullName),after_parameter(?m1FullName,?params1,?),added_method(?m2FullName,?m2ShortName,?tFullName),after_parameter(?m2FullName,?params2,?),NOT(equals(?m1ShortName,?m2ShortName)),deleted_method(?oldmFullName,?oldmShortName,?tFullName),before_parameter(?oldmFullName,?oldparams,?)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String m1ShortName = rs.getString("?m1ShortName");
    String m2ShortName = rs.getString("?m2ShortName");
    String oldmShortName = rs.getString("?oldmShortName");
    if ((!CodeCompare.compare(m1ShortName, m2ShortName)) || 
      (!CodeCompare.compare(m1ShortName, oldmShortName)) || 
      (!CodeCompare.compare(m2ShortName, oldmShortName))) {
      return null;
    }
    String[] params1 = rs.getString("?params1").split(", ");
    String[] params2 = rs.getString("?params2").split(", ");
    String[] oldParams = rs.getString("?oldparams").split(", ");
    
    int oldLen = numParams(oldParams);
    int len1 = numParams(params1);
    int len2 = numParams(params2);
    if ((len1 != len2) || (len1 >= oldLen)) {
      return null;
    }
    return getName() + "(\"" + rs.getString("?oldmFullName") + "\")";
  }
  
  private int numParams(String[] params)
  {
    if (params.length == 0) {
      return 0;
    }
    if (params[0] == "") {
      return 0;
    }
    return params.length;
  }
}
