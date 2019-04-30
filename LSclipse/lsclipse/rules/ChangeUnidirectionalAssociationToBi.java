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

public class ChangeUnidirectionalAssociationToBi
  implements Rule
{
  private static final String T2P_FULL_NAME = "?t2pFullName";
  private static final String TP_FULL_NAME = "?tpFullName";
  private static final String T2_FULL_NAME = "?t2FullName";
  private static final String T_FULL_NAME = "?tFullName";
  private String name_;
  
  public ChangeUnidirectionalAssociationToBi()
  {
    this.name_ = "change_uni_to_bi";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return getName() + "(" + "?tFullName" + "," + "?t2FullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    RefactoringQuery changeunitobi = new RefactoringQuery(getName(), 
      getQueryString());
    return changeunitobi;
  }
  
  private String getQueryString()
  {
    return "before_field(?fFullName, ?, ?t2FullName),after_field(?fFullName, ?, ?t2FullName),before_fieldoftype(?fFullName, ?tpFullName),after_fieldoftype(?fFullName, ?tpFullName),added_field(?f2FullName, ?, ?tFullName),added_fieldoftype(?f2FullName, ?t2pFullName),NOT(equals(?tFullName, ?t2FullName))";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String tFullName = rs.getString("?tFullName");
    String t2FullName = rs.getString("?t2FullName");
    String tpFullName = rs.getString("?tpFullName");
    String t2pFullName = rs.getString("?t2pFullName");
    if (((tFullName.equals(tpFullName)) && (t2pFullName.contains(t2FullName))) || (
      (t2FullName.equals(t2pFullName)) && 
      (tpFullName.contains(tFullName))))
    {
      String result = getName() + "(\"" + tFullName + "\",\"" + 
        t2FullName + "\")";
      return result;
    }
    return null;
  }
}
