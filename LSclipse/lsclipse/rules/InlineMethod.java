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

public class InlineMethod
  implements Rule
{
  private String name_;
  private String old_method_body_;
  private String method_body_;
  private String method_full_name_;
  private String old_method_full_name_;
  private String type_full_name_;
  private String old_method_short_name_;
  
  public InlineMethod()
  {
    this.name_ = "inline_method";
    
    this.old_method_body_ = "?oldmBody";
    this.method_body_ = "?mBody";
    this.method_full_name_ = "?mFullName";
    this.old_method_full_name_ = "?oldmFullName";
    this.type_full_name_ = "?tFullName";
    this.old_method_short_name_ = "?oldmShortName";
  }
  
  public String getRefactoringString()
  {
    return 
    
      getName() + "(" + this.method_full_name_ + "," + this.old_method_full_name_ + "," + this.old_method_body_ + "," + this.type_full_name_ + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    RefactoringQuery inline_method = new RefactoringQuery(getName(), 
      getQueryString());
    return inline_method;
  }
  
  private String getQueryString()
  {
    return 
    
      "deleted_method(" + this.old_method_full_name_ + ", " + this.old_method_short_name_ + ", " + this.type_full_name_ + "), " + "before_method(" + this.method_full_name_ + ", ?, " + this.type_full_name_ + "), " + "before_calls(" + this.method_full_name_ + ", " + this.old_method_full_name_ + "), added_methodbody(" + this.method_full_name_ + "," + this.method_body_ + "), deleted_methodbody(" + this.old_method_full_name_ + "," + this.old_method_body_ + ")," + "NOT(equals(" + this.method_full_name_ + "," + this.old_method_full_name_ + "))";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String oldmBody_str = rs.getString(this.old_method_body_);
    String mBody_str = rs.getString(this.method_body_);
    if ((oldmBody_str.length() > 1) && 
      (CodeCompare.compare(oldmBody_str, mBody_str)) && 
      (CodeCompare.contrast(oldmBody_str, mBody_str)))
    {
      String writeTo = getName() + "(" + "\"" + 
        rs.getString(this.method_full_name_) + "\"" + "," + "\"" + 
        rs.getString(this.old_method_full_name_) + "\"" + "," + "\"" + 
        oldmBody_str + "\"" + "," + "\"" + 
        rs.getString(this.type_full_name_) + "\"" + ")";
      
      return writeTo;
    }
    return null;
  }
  
  public String getName()
  {
    return this.name_;
  }
}
