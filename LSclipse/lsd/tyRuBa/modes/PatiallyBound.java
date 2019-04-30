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
package tyRuBa.modes;

public class PatiallyBound extends BindingMode {
	
	static public PatiallyBound the = new PatiallyBound();

	private PatiallyBound() {}

	public int hashCode() {
		return this.getClass().hashCode();
	}

	public boolean equals(Object other) {
		return other instanceof PatiallyBound;
	}

	public String toString() {
		return "BF";
	}

	/** check that this binding satisfied the binding mode */
	public boolean satisfyBinding(BindingMode mode) {
		return mode instanceof Free;
	}

	public boolean isBound() {
		return false;
	}
	public boolean isFree() {
		return false;
	}
}
