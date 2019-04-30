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
package tyRuBa.engine.compilation;

import tyRuBa.engine.Frame;
import tyRuBa.engine.RBContext;

public class CompiledConjunction_SemiDet_SemiDet extends SemiDetCompiled {

	private final SemiDetCompiled left;
	private final SemiDetCompiled right;

	public CompiledConjunction_SemiDet_SemiDet(SemiDetCompiled left, SemiDetCompiled right) {
		super(left.getMode().multiply(right.getMode()));
		this.left = left;
		this.right = right;
	}

	public Frame runSemiDet(Object input, RBContext context) {
		Frame leftResult = left.runSemiDet(input, context);
		if (leftResult == null)
			return null;
		else
			return right.runSemiDet(leftResult, context);
	}

	public String toString() {
		return "(" + right + " ==> " + left + ")";
	}

}
