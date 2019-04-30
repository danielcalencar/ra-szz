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

public class ExtractMethod
  implements Rule
{
  private String name_;
  private String new_method_body_;
  private String method_body_;
  private String method_full_name_;
  private String new_method_full_name_;
  private String type_full_name_;
  
  public ExtractMethod()
  {
    this.name_ = "extract_method";
    
    this.new_method_body_ = "?newmBody";
    this.method_body_ = "?mBody";
    this.method_full_name_ = "?mFullName";
    this.new_method_full_name_ = "?newmFullName";
    this.type_full_name_ = "?tFullName";
  }
  
  public ExtractMethod(String method_full_name, String new_method_full_name, String new_method_body, String type_full_name)
  {
    this.name_ = "extract_method";
    
    this.new_method_body_ = new_method_body;
    this.method_body_ = "?mBody";
    this.method_full_name_ = method_full_name;
    this.new_method_full_name_ = new_method_full_name;
    this.type_full_name_ = type_full_name;
  }
  
  public String getRefactoringString()
  {
    return 
    
      getName() + "(" + this.method_full_name_ + "," + this.new_method_full_name_ + "," + this.new_method_body_ + "," + this.type_full_name_ + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    RefactoringQuery extract_method = new RefactoringQuery(getName(), 
      getQueryString());
    return extract_method;
  }
  
  private String getQueryString()
  {
    return 
    
      "added_method(" + this.new_method_full_name_ + ",?," + this.type_full_name_ + "), " + "after_method(" + this.method_full_name_ + ",?," + this.type_full_name_ + ")," + "after_calls(" + this.method_full_name_ + ", " + this.new_method_full_name_ + "), added_methodbody(" + this.new_method_full_name_ + "," + this.new_method_body_ + "), deleted_methodbody(" + this.method_full_name_ + "," + this.method_body_ + ")," + "NOT(equals(" + this.new_method_full_name_ + "," + this.method_full_name_ + "))";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String newmBody_str = rs.getString(this.new_method_body_);
    String mBody_str = rs.getString(this.method_body_);
    if ((newmBody_str.length() > 1) && 
      (CodeCompare.compare(newmBody_str, mBody_str)) && 
      (CodeCompare.contrast(newmBody_str, mBody_str)))
    {
      String writeTo = getName() + "(" + "\"" + 
        rs.getString(this.method_full_name_) + "\"" + "," + "\"" + 
        rs.getString(this.new_method_full_name_) + "\"" + "," + "\"" + 
        newmBody_str + "\"" + "," + "\"" + 
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
