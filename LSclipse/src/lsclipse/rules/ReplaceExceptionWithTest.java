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
/* ReplaceExceptionWithTest.java
 * 
 * This class is used to check adherence to the replace
 * exception with test logical refactoring rule.
 * 
 * author:   Kyle Prete
 * created:  8/6/2010
 */
package lsclipse.rules;

import lsclipse.RefactoringQuery;
import lsclipse.utils.CodeCompare;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ReplaceExceptionWithTest implements Rule {
	private static final String M_BODY = "?mBody";
	private static final String CONDITION = "?condition";
	private static final String CATCH_BLOCKS = "?catchBlocks";
	private static final String ELSE_BLOCK = "?elseBlock";
	private static final String M_FULL_NAME = "?mFullName";
	private static final String IF_BLOCK = "?ifBlock";
	private static final String TRY_BLOCK = "?tryBlock";
	private String name_;

	public ReplaceExceptionWithTest() {
		name_ = "replace_exception_with_test";
	}

	@Override
	public String getName() {
		return name_;
	}

	@Override
	public String getRefactoringString() {
		return getName() + "(?catchStatement," + CONDITION + "," + M_FULL_NAME
				+ ")";
	}

	@Override
	public RefactoringQuery getRefactoringQuery() {
		return new RefactoringQuery(getName(), getQueryString());
	}

	private String getQueryString() {
		return "deleted_trycatch(" + TRY_BLOCK + "," + CATCH_BLOCKS + ",?,"
				+ M_FULL_NAME + ")," + "added_conditional(" + CONDITION + ","
				+ IF_BLOCK + "," + ELSE_BLOCK + "," + M_FULL_NAME + "),"
				+ "NOT(before_conditional(" + CONDITION + ", ?, ?, " + M_FULL_NAME + ")), "
				+ "added_methodbody(" + M_FULL_NAME + "," + M_BODY + ")";
	}

	// TODO(kprete):Something here throws an indexOutOfBounds when run on replace cond with polymorphism. Hunt it down.
	@Override
	public String checkAdherence(ResultSet rs) throws TyrubaException {
		String tryBlock = rs.getString(TRY_BLOCK);
		String condition = rs.getString(CONDITION);
		String ifBlock = rs.getString(IF_BLOCK);
		String elseBlock = rs.getString(ELSE_BLOCK);
		if (elseBlock.equals("")) {
			String mBody = rs.getString(M_BODY);
			String firstPart = condition + ")" + ifBlock;
			// Get the rest of the method instead.
			elseBlock = mBody.substring(mBody.indexOf(firstPart)
					+ firstPart.length());
		}
		String compareToCatch = null;

		if (CodeCompare.compare(tryBlock, ifBlock))
			compareToCatch = elseBlock;
		else if (CodeCompare.compare(tryBlock, elseBlock))
			compareToCatch = ifBlock;
		else
			return null;

		assert compareToCatch != null;

		String catchString = rs.getString(CATCH_BLOCKS);
		String[] catchBlocks = catchString.split(",");

		for (String catchBlock : catchBlocks) {
			if (catchBlock.length() == 0)
				continue;
			String exception = catchBlock.substring(0, catchBlock.indexOf(':'));
			String catchBody = catchBlock
					.substring(catchBlock.indexOf(':') + 1);
			if (catchBody.length() > 0
					&& CodeCompare.compare(compareToCatch, catchBody)) {
				return getName() + "(\"" + exception + "\",\"" + condition
						+ "\",\"" + rs.getString(M_FULL_NAME) + "\")";
			}
		}

		return null;
	}

}
