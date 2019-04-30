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
/* ReplaceArrayWithObject.java
 * 
 * This class is used to test a tyRuBa result set for adherence 
 * to the "replace array with object" logical refactoring rule.
 * 
 * author:   Kyle Prete and Napol Rachatasumrit
 * created:  8/3/2010
 */
package lsclipse.rules;

import lsclipse.RefactoringQuery;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ReplaceArrayWithObject implements Rule {
	private static final String NEWT_FULL_NAME = "?newtFullName";
	private static final String F_FULL_NAME = "?fFullName";
	private static final String OLDT_FULL_NAME = "?oldtFullName";
	private String name_;

	public ReplaceArrayWithObject() {
		name_ = "replace_array_with_object";
	}

	@Override
	public String getName() {
		return name_;
	}

	@Override
	public String getRefactoringString() {
		return getName() + "(" + F_FULL_NAME + "," + NEWT_FULL_NAME + ")";
	}

	@Override
	public RefactoringQuery getRefactoringQuery() {
		RefactoringQuery repl = new RefactoringQuery(getName(),
				getQueryString());
		return repl;
	}

	private String getQueryString() {
		return "deleted_fieldoftype(" + F_FULL_NAME + ", " + OLDT_FULL_NAME
				+ ")," + "added_fieldoftype(" + F_FULL_NAME + ", "
				+ NEWT_FULL_NAME + ")," + "added_type(" + NEWT_FULL_NAME
				+ ", ?, ?)";
	}

	@Override
	public String checkAdherence(ResultSet rs) throws TyrubaException {
		String oldType = rs.getString(OLDT_FULL_NAME);
		if (oldType.endsWith("[]")) {
			String writeTo = getName() + "(\"" + rs.getString(F_FULL_NAME)
					+ "\",\"" + rs.getString(NEWT_FULL_NAME) + "\")";

			return writeTo;
		}
		return null;
	}

}
