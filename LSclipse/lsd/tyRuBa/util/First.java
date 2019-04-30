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
/*
 * Created on Jul 10, 2003
 */
package tyRuBa.util;

/**
 * An ElementSource that keeps only the first element of
 * another Source. Implemented in such a way that it
 * does not destroy the lazy evaluation property of the
 * other source. Foo
 * 
 * This source does not keep a reference to the other source
 * after it has retrieved its first element.
 */
public class First extends ElementSource {
	
	private ElementSource source;
	
	public First(ElementSource from) {
		source = from;
	}

	public void print(PrintingState p) {
		p.print("First(");
		source.print(p);
		p.outdent();
		p.print(")");
	}

	public int status() {
		if (source==null)
			return NO_MORE_ELEMENTS;
		else {
			int stat = source.status();
			if (source.status()==NO_MORE_ELEMENTS) {
				source = null;
			}
			return stat;
		}
	}

	public Object nextElement() {
		ElementSource it = source;
		source = null;
		return it.nextElement();
	}

	public ElementSource first() {
		return this;
	}

}
