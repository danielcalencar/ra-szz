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
package tyRuBa.engine;

/** This is wrapper for RBTerms that get passed to Java functions. 
    Is used for RBTerm objects that don't really know how to map
    themselves onto a Java equivalent.  At least this preserves
    the x.up().down() = x  
*/

public class UppedTerm {
	RBTerm term;

	public UppedTerm(RBTerm t) {
		term = t;
	}

	public RBTerm down() {
		return term;
	}

	public String toString() {
		return term.toString();
	}
}
