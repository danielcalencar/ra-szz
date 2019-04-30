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
package changetypes;

public interface IAtomicChange {

	public enum ChangeTypes {
		//LSD facts
		ADD_PACKAGE,
		ADD_TYPE,
		ADD_METHOD,
		ADD_FIELD,
		ADD_RETURN,
		ADD_FIELDOFTYPE,
		ADD_ACCESSES,
		ADD_CALLS,
		ADD_SUBTYPE,
		ADD_INHERITEDFIELD,
		ADD_INHERITEDMETHOD,
		ADD_PARAMETER, // Niki's edit
		
		DEL_PACKAGE,
		DEL_TYPE,
		DEL_METHOD,
		DEL_FIELD,
		DEL_RETURN,
		DEL_FIELDOFTYPE,
		DEL_ACCESSES,
		DEL_CALLS,
		DEL_SUBTYPE,
		DEL_INHERITEDFIELD,
		DEL_INHERITEDMETHOD,
		//Non-LSD facts
		CHANGE_METHODBODY,		//(methodFullName, methodBody)
		CHANGE_METHODSIGNATURE,	//(methodFullName, methodSignature) Method args is encoded as "type1:arg1,type2:arg2,...->return"
		ADD_CONDITIONAL, // Niki's edit
		DEL_CONDITIONAL, // Niki's edit
		DEL_PARAMETER // Niki's edit
	}

}
