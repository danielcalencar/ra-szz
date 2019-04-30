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

public class PreserveWholeObject
  implements Rule
{
  private static final String OLD_PARAM_NAME = "?oldParamName";
  private static final String OBJ_PARAM_NAME = "?objParamName";
  private static final String M_FULL_NAME = "?mFullName";
  private static final String OBJT_FULL_NAME = "?objtFullName";
  private static final String OBJM_FULL_NAME = "?objmFullName";
  private static final String CLIENTM_FULL_NAME = "?clientmFullName";
  private String name_;
  
  public PreserveWholeObject()
  {
    this.name_ = "preserve_whole_object";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return getName() + "(" + "?mFullName" + "," + "?tParamShortName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "deleted_calls(?clientmFullName,?objmFullName),after_method(?objmFullName,?,?objtFullName),before_calls(?clientmFullName,?mFullName),after_calls(?clientmFullName,?mFullName),added_calls(?mFullName,?objmFullName),added_parameter(?mFullName,?,?objParamName),deleted_parameter(?mFullName,?,?oldParamName)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String objParamName = rs.getString("?objParamName");
    String oldParamName = rs.getString("?oldParamName");
    String objType = rs.getString("?objtFullName");
    
    String objParamShortType = objParamName.substring(0, objParamName.indexOf(':'));
    String oldParamShortType = oldParamName.substring(0, oldParamName.indexOf(':'));
    if ((objType.contains(oldParamShortType)) || (!objType.contains(objParamShortType))) {
      return null;
    }
    return getName() + "(\"" + rs.getString("?mFullName") + "\",\"" + objParamShortType + "\")";
  }
}
