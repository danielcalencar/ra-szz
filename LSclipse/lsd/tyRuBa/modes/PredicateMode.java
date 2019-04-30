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
/*****************************************************************\
 * File:        PredicateMode.java
 * Author:      TyRuBa
 * Meta author: Kris De Volder <kdvolder@cs.ubc.ca>
\*****************************************************************/
package tyRuBa.modes;

/**
 * A PredicateMode consists of a BindingList, a Mode and a flag toBeCheck.
 * If toBeCheck is set, then each each RBComponent with this declared mode
 * will be checked before inserting into any rulebase. 
 */

public class PredicateMode {

	private BindingList paramModes;
	private Mode mode;
	private boolean toBeCheck;

	/** Constructor */
	public PredicateMode(BindingList paramModes, Mode mode,	boolean toBeCheck) {
		this.paramModes = paramModes;
		this.mode = mode;
		this.toBeCheck = toBeCheck;
	}
	
	public int hashCode() {
		return paramModes.hashCode()
			+ 13 * (mode.hashCode() + 13 * (this.getClass().hashCode()));
	}

	public boolean equals(Object other) {
		if (other instanceof PredicateMode) {
			PredicateMode cother = (PredicateMode) other;
			return (paramModes.equals(cother.paramModes))
				&& (mode.equals(cother.mode));
		} else {
			return false;
		}
	}

	public String toString() {
		return paramModes + " IS " + mode;
	}

//	public void setParamModes(BindingList newValue) {
//		paramModes = newValue;
//	}

	public BindingList getParamModes() {
		return paramModes;
	}

	public Mode getMode() {
		return (Mode) mode.clone();
	}
	
	public boolean toBeCheck() {
		return toBeCheck;
	}
}
