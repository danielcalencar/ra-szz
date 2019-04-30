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

/**
 * @author kdvolder
 */
public abstract class RBSubstitutable extends RBTerm {
	
	protected String name;
	
	RBSubstitutable(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object obj) {
		return (obj instanceof RBSubstitutable)
			&& ((RBSubstitutable) obj).name == this.name;
	}	

}
