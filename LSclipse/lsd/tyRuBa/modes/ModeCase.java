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
 * Created on Jun 25, 2003
 */
package tyRuBa.modes;

import java.util.Collection;

import tyRuBa.engine.RBExpression;

public class ModeCase {
	private Collection boundVars;
	private RBExpression exp;
		
	public ModeCase(Collection boundVars, RBExpression exp) {
		this.boundVars = boundVars;
		this.exp = exp;
	}
		
	public RBExpression getExp() {
		return exp;
	}
		
	public Collection getBoundVars() {
		return boundVars;
	}
	
	public String toString() {
		String varString = getBoundVars().toString();
		return "BOUND " + varString.substring(1, varString.length() - 1)
			+ " : " + getExp();
	}
}
	
