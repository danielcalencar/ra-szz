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

public class SeparateQueryFromModifier
  implements Rule
{
  private static final String F2_FULL_NAME = "?f2FullName";
  private static final String F1_FULL_NAME = "?f1FullName";
  private static final String T_FULL_NAME = "?tFullName";
  private static final String M2_SHORT_NAME = "?m2ShortName";
  private static final String M1_SHORT_NAME = "?m1ShortName";
  private static final String OLDM_SHORT_NAME = "?oldmShortName";
  private static final String M2_FULL_NAME = "?m2FullName";
  private static final String M1_FULL_NAME = "?m1FullName";
  private static final String OLDM_FULL_NAME = "?oldmFullName";
  private String name_;
  
  public SeparateQueryFromModifier()
  {
    this.name_ = "separate_query_from_modifier";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return 
      getName() + "(" + "?oldmFullName" + "," + "?m1FullName" + "," + "?m2FullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    RefactoringQuery sep_qm = new RefactoringQuery(getName(), 
      getQueryString());
    return sep_qm;
  }
  
  private String getQueryString()
  {
    return "before_field(?f1FullName,?,?tFullName),after_field(?f1FullName,?,?tFullName),before_field(?f2FullName,?,?tFullName),after_field(?f2FullName,?,?tFullName),deleted_method(?oldmFullName,?oldmShortName,?tFullName),deleted_accesses(?f1FullName,?oldmFullName),deleted_accesses(?f2FullName,?oldmFullName),added_method(?m1FullName,?m1ShortName,?tFullName),added_accesses(?f1FullName,?m1FullName),added_method(?m2FullName,?m2ShortName,?tFullName),added_accesses(?f2FullName,?m2FullName),NOT(equals(?m1FullName,?m2FullName))";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String oldmShortName = rs.getString("?oldmShortName");
    String m1ShortName = rs.getString("?m1ShortName");
    String m2ShortName = rs.getString("?m2ShortName");
    if ((!CodeCompare.compare(oldmShortName, m1ShortName)) || 
      (!CodeCompare.compare(oldmShortName, m2ShortName))) {
      return null;
    }
    if (m1ShortName.compareTo(m2ShortName) < 0) {
      return 
      
        getName() + "(\"" + rs.getString("?oldmFullName") + "\",\"" + rs.getString("?m1FullName") + "\",\"" + rs.getString("?m2FullName") + "\")";
    }
    return 
    
      getName() + "(\"" + rs.getString("?oldmFullName") + "\",\"" + rs.getString("?m2FullName") + "\",\"" + rs.getString("?m1FullName") + "\")";
  }
}
