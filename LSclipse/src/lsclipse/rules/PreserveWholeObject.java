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
/* PreserveWholeObject.java
 * 
 * This class is used to check adherence to the preserve
 * whole object logical refactoring rule.
 * 
 * author:   Kyle Prete
 * created:  8/5/2010
 */
package lsclipse.rules;

import lsclipse.RefactoringQuery;
import tyRuBa.tdbc.ResultSet;
import tyRuBa.tdbc.TyrubaException;

public class PreserveWholeObject implements Rule {
	private static final String OLD_PARAM_NAME = "?oldParamName";
	private static final String OBJ_PARAM_NAME = "?objParamName";
	private static final String M_FULL_NAME = "?mFullName";
	private static final String OBJT_FULL_NAME = "?objtFullName";
	private static final String OBJM_FULL_NAME = "?objmFullName";
	private static final String CLIENTM_FULL_NAME = "?clientmFullName";
	private String name_;

	public PreserveWholeObject() {
		name_ = "preserve_whole_object";
	}

	@Override
	public String getName() {
		return name_;
	}

	@Override
	public String getRefactoringString() {
		return getName() + "(" + M_FULL_NAME + "," + "?tParamShortName" + ")";
	}

	@Override
	public RefactoringQuery getRefactoringQuery() {
		return new RefactoringQuery(getName(), getQueryString());
	}

	private String getQueryString() {
		return "deleted_calls(" + CLIENTM_FULL_NAME + "," + OBJM_FULL_NAME
				+ ")," + "after_method(" + OBJM_FULL_NAME + ",?,"
				+ OBJT_FULL_NAME + ")," + "before_calls(" + CLIENTM_FULL_NAME
				+ "," + M_FULL_NAME + ")," + "after_calls(" + CLIENTM_FULL_NAME
				+ "," + M_FULL_NAME + ")," + "added_calls(" + M_FULL_NAME + ","
				+ OBJM_FULL_NAME + ")," + "added_parameter(" + M_FULL_NAME
				+ ",?," + OBJ_PARAM_NAME + ")," + "deleted_parameter("
				+ M_FULL_NAME + ",?," + OLD_PARAM_NAME + ")";
	}

	@Override
	public String checkAdherence(ResultSet rs) throws TyrubaException {
		String objParamName = rs.getString(OBJ_PARAM_NAME);
		String oldParamName = rs.getString(OLD_PARAM_NAME);
		String objType = rs.getString(OBJT_FULL_NAME);
		
		String objParamShortType = objParamName.substring(0, objParamName.indexOf(':'));
		String oldParamShortType = oldParamName.substring(0, oldParamName.indexOf(':'));
		
		// Make sure old parameter was not of obj type and that new one is.
		if (objType.contains(oldParamShortType) || !objType.contains(objParamShortType))
			return null;
		
		return getName() + "(\"" + rs.getString(M_FULL_NAME) + "\",\"" + objParamShortType + "\")";
	}

}
