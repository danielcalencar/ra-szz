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
/* ReplaceDataValueWithObject.java
 * 
 * This class is used to test a tyRuBa result set for more lenient adherence 
 * to the "replace data value with object" logical refactoring rule.
 * 
 * author:   Kyle Prete
 * created:  8/3/2010
 */
package lsclipse.rules;

import lsclipse.RefactoringQuery;
import lsclipse.utils.CodeCompare;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ReplaceDataValueWithObject implements Rule {
	private static final String NEWT_FULL_NAME = "?newtFullName";
	private static final String F_FULL_NAME = "?fFullName";
	private static final String NEWT_SHORT_NAME = "?newtShortName";
	private static final String F_SHORT_NAME = "?fShortName";
	private String name_;

	public ReplaceDataValueWithObject() {
		name_ = "replace_data_with_object";
	}

	@Override
	public String getName() {
		return name_;
	}

	private String getQueryString() {
		return "before_field(" + F_FULL_NAME + ", " + F_SHORT_NAME + ", "
				+ "?tFullName" + ")," + "deleted_fieldoftype(" + F_FULL_NAME
				+ ",?)," + "added_type(" + NEWT_FULL_NAME + ", "
				+ NEWT_SHORT_NAME + ", ?)," + "after_field(" + "?newfFullName"
				+ ", ?, " + "?tFullName" + ")," + "added_fieldoftype("
				+ "?newfFullName" + ", " + NEWT_FULL_NAME + ")";
	}

	@Override
	public String getRefactoringString() {
		return getName() + "(" + F_FULL_NAME + ", " + NEWT_FULL_NAME + ")";
	}

	@Override
	public RefactoringQuery getRefactoringQuery() {
		RefactoringQuery repl = new RefactoringQuery(getName(),
				getQueryString());
		return repl;
	}

	@Override
	public String checkAdherence(ResultSet rs) throws TyrubaException {
		String fieldName = rs.getString(F_SHORT_NAME);
		String typeName = rs.getString(NEWT_SHORT_NAME);

		// TODO(kprete): write name comparison?
		if (CodeCompare.compare(fieldName, typeName)) {

			String writeTo = getName() + "(\"" + rs.getString(F_FULL_NAME)
					+ "\",\"" + rs.getString(NEWT_FULL_NAME) + "\")";

			return writeTo;
		}
		return null;
	}

}
