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

public class DecomposeConditional
  implements Rule
{
  private ExtractMethod conditionExtractMethod = null;
  private ExtractMethod ifBlockExtractMethod = null;
  private ExtractMethod elseBlockExtractMethod = null;
  private String name_;
  public static final String ELSE_BLOCK_FULL_NAME = "?m3FullName";
  public static final String IF_BLOCK_FULL_NAME = "?m2FullName";
  public static final String CONDITION_M_FULL_NAME = "?m1FullName";
  public static final String ELSE_BLOCK_2 = "?elseBlockB";
  public static final String IF_BLOCK_2 = "?ifBlockB";
  public static final String CONDITION_BLOCK_2 = "?conditionB";
  public static final String M_FULL_NAME = "?mFullName";
  public static final String ELSE_BLOCK = "?elseBlock";
  public static final String IF_BLOCK = "?ifBlock";
  public static final String CONDITION_BLOCK = "?condition";
  
  public DecomposeConditional()
  {
    this.name_ = "decompose_conditional";
    
    this.conditionExtractMethod = new ExtractMethod("?mFullName", 
      "?m1FullName", "?conditionB", "?t1FullName");
    this.ifBlockExtractMethod = new ExtractMethod("?mFullName", 
      "?m2FullName", "?ifBlockB", "?t2FullName");
    this.elseBlockExtractMethod = new ExtractMethod("?mFullName", 
      "?m3FullName", "?elseBlockB", "?t3FullName");
  }
  
  private String getQueryString()
  {
    return 
    
      "deleted_conditional(?condition, ?ifBlock, ?elseBlock, ?mFullName), " + this.conditionExtractMethod.getRefactoringString() + ", " + this.ifBlockExtractMethod.getRefactoringString() + ", " + this.elseBlockExtractMethod.getRefactoringString();
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    RefactoringQuery decompose_conditional = new RefactoringQuery(
      getName(), getQueryString());
    return decompose_conditional;
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String m1FullName = rs.getString("?m1FullName");
    String m2FullName = rs.getString("?m2FullName");
    String m3FullName = rs.getString("?m3FullName");
    
    String ifBlock = rs.getString("?ifBlock");
    String ifBlockB = rs.getString("?ifBlockB");
    String elseBlock = rs.getString("?elseBlock");
    String elseBlockB = rs.getString("?elseBlockB");
    String condition = rs.getString("?condition");
    String conditionB = rs.getString("?conditionB");
    if ((!m1FullName.equals(m2FullName)) && (!m1FullName.equals(m3FullName)) && 
      (!m2FullName.equals(m3FullName)) && 
      (CodeCompare.compare(ifBlock, ifBlockB)) && 
      (CodeCompare.compare(elseBlock, elseBlockB)) && 
      (CodeCompare.compare(condition, conditionB))) {
      return 
      
        getName() + "(\"" + condition + "\",\"" + ifBlock + "\",\"" + elseBlock + "\",\"" + rs.getString("?mFullName") + "\")";
    }
    return null;
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return 
      getName() + "(" + "?condition" + "," + "?ifBlock" + "," + "?elseBlock" + "," + "?mFullName" + ")";
  }
}
