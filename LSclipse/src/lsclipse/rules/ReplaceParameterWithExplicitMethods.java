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
/* ParameterizeMethod.java
 * 
 * This class is used to check adherence to the replace
 * parameter with explicit methods logical refactoring rule.
 * 
 * author:   Kyle Prete
 * created:  8/4/2010
 */
package lsclipse.rules;

import lsclipse.RefactoringQuery;
import lsclipse.utils.CodeCompare;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class ReplaceParameterWithExplicitMethods implements Rule {
	private static final String M2_FULL_NAME = "?m2FullName";
	private static final String T_FULL_NAME = "?tFullName";
	private static final String M1_FULL_NAME = "?m1FullName";
	private static final String OLDPARAMS = "?oldparams";
	private static final String PARAMS2 = "?params2";
	private static final String PARAMS1 = "?params1";
	private static final String OLDM_SHORT_NAME = "?oldmShortName";
	private static final String M2_SHORT_NAME = "?m2ShortName";
	private static final String M1_SHORT_NAME = "?m1ShortName";
	private static final String OLDM_FULL_NAME = "?oldmFullName";
	private String name_;

	public ReplaceParameterWithExplicitMethods() {
		name_ = "replace_param_explicit_methods";
	}

	@Override
	public String getName() {
		return name_;
	}

	@Override
	public String getRefactoringString() {
		return getName() + "(" + OLDM_FULL_NAME + ")";
	}

	@Override
	public RefactoringQuery getRefactoringQuery() {
		return new RefactoringQuery(getName(), getQueryString());
	}

	private String getQueryString() {
		return "added_method(" + M1_FULL_NAME + "," + M1_SHORT_NAME + ","
				+ T_FULL_NAME + ")," + "after_parameter(" + M1_FULL_NAME + ","
				+ PARAMS1 + ",?)," + "added_method(" + M2_FULL_NAME + ","
				+ M2_SHORT_NAME + "," + T_FULL_NAME + "),"
				+ "after_parameter(" + M2_FULL_NAME + "," + PARAMS2 + ",?),"
				+ "NOT(equals(" + M1_SHORT_NAME + "," + M2_SHORT_NAME + ")),"
				+ "deleted_method(" + OLDM_FULL_NAME + "," + OLDM_SHORT_NAME
				+ "," + T_FULL_NAME + ")," + "before_parameter("
				+ OLDM_FULL_NAME + "," + OLDPARAMS + ",?)";
	}

	@Override
	public String checkAdherence(ResultSet rs) throws TyrubaException {
		String m1ShortName = rs.getString(M1_SHORT_NAME);
		String m2ShortName = rs.getString(M2_SHORT_NAME);
		String oldmShortName = rs.getString(OLDM_SHORT_NAME);
		if (!(CodeCompare.compare(m1ShortName, m2ShortName)
				&& CodeCompare.compare(m1ShortName, oldmShortName) && CodeCompare
				.compare(m2ShortName, oldmShortName)))
			return null;

		String[] params1 = rs.getString(PARAMS1).split(", ");
		String[] params2 = rs.getString(PARAMS2).split(", ");
		String[] oldParams = rs.getString(OLDPARAMS).split(", ");

		// Account for empty String => no parameters
		int oldLen = numParams(oldParams);
		int len1 = numParams(params1);
		int len2 = numParams(params2);

		if ((len1 != len2) || (len1 >= oldLen))
			return null;

		return getName() + "(\"" + rs.getString(OLDM_FULL_NAME) + "\")";
	}

	// Account for empty String => no parameters
	private int numParams(String[] params) {
		if (params.length == 0)
			return 0;
		if (params[0] == "")
			return 0;

		return params.length;
	}

}