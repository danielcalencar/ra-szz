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
package tyRuBa.util;

class EmptySource extends ElementSource {
	
	public static EmptySource the = new EmptySource();

	public int status() {
		return NO_MORE_ELEMENTS;
	}
	public Object nextElement() {
		throw new Error("TheEmpty ElementSource has no elements");
	}
	/** More efficient append to forget about useless empty sources */
	public ElementSource append(ElementSource other) {
		return other;
	}
	/** More efficient map for empty sources */
	public ElementSource map(Action what) {
		return theEmpty;
	}
	public void print(PrintingState p) {
		p.print("{*empty*}");
	}
	public boolean isEmpty() {
		return true;
	}
	
	public ElementSource first() {
		return this;
	}

}