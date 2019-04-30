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

public class ReplaceExceptionWithTest
  implements Rule
{
  private static final String M_BODY = "?mBody";
  private static final String CONDITION = "?condition";
  private static final String CATCH_BLOCKS = "?catchBlocks";
  private static final String ELSE_BLOCK = "?elseBlock";
  private static final String M_FULL_NAME = "?mFullName";
  private static final String IF_BLOCK = "?ifBlock";
  private static final String TRY_BLOCK = "?tryBlock";
  private String name_;
  
  public ReplaceExceptionWithTest()
  {
    this.name_ = "replace_exception_with_test";
  }
  
  public String getName()
  {
    return this.name_;
  }
  
  public String getRefactoringString()
  {
    return 
      getName() + "(?catchStatement," + "?condition" + "," + "?mFullName" + ")";
  }
  
  public RefactoringQuery getRefactoringQuery()
  {
    return new RefactoringQuery(getName(), getQueryString());
  }
  
  private String getQueryString()
  {
    return "deleted_trycatch(?tryBlock,?catchBlocks,?,?mFullName),added_conditional(?condition,?ifBlock,?elseBlock,?mFullName),NOT(before_conditional(?condition, ?, ?, ?mFullName)), added_methodbody(?mFullName,?mBody)";
  }
  
  public String checkAdherence(ResultSet rs)
    throws TyrubaException
  {
    String tryBlock = rs.getString("?tryBlock");
    String condition = rs.getString("?condition");
    String ifBlock = rs.getString("?ifBlock");
    String elseBlock = rs.getString("?elseBlock");
    if (elseBlock.equals(""))
    {
      String mBody = rs.getString("?mBody");
      String firstPart = condition + ")" + ifBlock;
      
      elseBlock = mBody.substring(mBody.indexOf(firstPart) + 
        firstPart.length());
    }
    String compareToCatch = null;
    if (CodeCompare.compare(tryBlock, ifBlock)) {
      compareToCatch = elseBlock;
    } else if (CodeCompare.compare(tryBlock, elseBlock)) {
      compareToCatch = ifBlock;
    } else {
      return null;
    }
    assert (compareToCatch != null);
    
    String catchString = rs.getString("?catchBlocks");
    String[] catchBlocks = catchString.split(",");
    String[] arrayOfString1;
    int j = (arrayOfString1 = catchBlocks).length;
    for (int i = 0; i < j; i++)
    {
      String catchBlock = arrayOfString1[i];
      if (catchBlock.length() != 0)
      {
        String exception = catchBlock.substring(0, catchBlock.indexOf(':'));
        String catchBody = catchBlock
          .substring(catchBlock.indexOf(':') + 1);
        if ((catchBody.length() > 0) && 
          (CodeCompare.compare(compareToCatch, catchBody))) {
          return 
            getName() + "(\"" + exception + "\",\"" + condition + "\",\"" + rs.getString("?mFullName") + "\")";
        }
      }
    }
    return null;
  }
}
