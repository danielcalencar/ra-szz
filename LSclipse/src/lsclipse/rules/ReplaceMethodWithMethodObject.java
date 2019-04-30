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
/* ReplaceMethodWithMethodObject.java
 * 
 * This class is used to test a tyRuBa result set for more liberal adherence 
 * to the "replace method with method object" logical refactoring rule.
 * 
 * author:   Kyle Prete
 * created:  8/20/2010
 */

package lsclipse.rules;

import lsclipse.RefactoringQuery;
import lsclipse.utils.CodeCompare;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ReplaceMethodWithMethodObject implements Rule {
	private static final String CALLINGT_FULL_NAME = "?callingtFullName";
	private static final String T_FULL_NAME = "?tFullName";
	public static final String NEWM_BODY = "?newmBody";
	public static final String M_BODY = "?mBody";
	public static final String M_FULL_NAME = "?mFullName";
	public static final String NEWM_FULL_NAME = "?newmFullName";

	private String name_;

	public ReplaceMethodWithMethodObject() {
		super();
		name_ = "replace_method_with_method_object";
	}

	@Override
	public String getRefactoringString() {
		return getName() + "(" + M_FULL_NAME + "," + T_FULL_NAME + ")";
	}

	@Override
	public RefactoringQuery getRefactoringQuery() {
		return new RefactoringQuery(getName(), getQueryString());
	}

	private static String getQueryString() {
		// Class named after old function?
		return "added_type(" + T_FULL_NAME + ", " + "?tShortName"
				+ ", ?), added_method(" + NEWM_FULL_NAME + ", ?, "
				+ T_FULL_NAME + ")," + "added_calls(" + M_FULL_NAME + ", "
				+ NEWM_FULL_NAME + ")," + "after_method(" + M_FULL_NAME
				+ ", ?, " + CALLINGT_FULL_NAME + "), added_methodbody("
				+ NEWM_FULL_NAME + "," + NEWM_BODY + "), deleted_methodbody("
				+ M_FULL_NAME + "," + M_BODY + ")";
		// Do not need to check before_method, as deleted_methodbody
		// would not be spawned.

	}

	@Override
	public String checkAdherence(ResultSet rs) throws TyrubaException {
		String newmBody_str = rs.getString(NEWM_BODY);
		String mBody_str = rs.getString(M_BODY);

		if (newmBody_str.length() > 1
				&& CodeCompare.compare(newmBody_str, mBody_str)) {

			String writeTo = getName() + "(\"" + rs.getString(M_FULL_NAME)
					+ "\",\"" + rs.getString(T_FULL_NAME) + "\")";

			return writeTo;
		}
		return null;

	}

	@Override
	public String getName() {
		return name_;
	}
}
