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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author cburns
 *
 * Special Iterator class that iterates over a pair of iterators.
 * I'm sure you can see the possibilities this opens, beyond just adding
 * two Iterators together.
 */
public class IteratorPair implements Iterator {
	Iterator iterators[];
	int which;
	
	public IteratorPair(Iterator carIt, Iterator cdrIt) {
		iterators = new Iterator[] { carIt, cdrIt };
		
		which = 0;
	}

	public boolean hasNext() {
		if(which < 2) {
			if(iterators[which] != null && iterators[which].hasNext()) {
				return true;
			} else {
				which++;
				return hasNext();
			}
		} else {
			return false;
		}
	}

	public Object next() {
		if(hasNext()) {
			return iterators[which].next();
		} else {
			throw new NoSuchElementException();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException(); //maybe implement this later
	}
}
