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

public class CompiledTest extends SemiDetCompiled {

	private Compiled tested;

	public CompiledTest(Compiled tested) {
		super(tested.getMode().first());
		this.tested = tested;
	}

	public Frame runSemiDet(Object input, RBContext context) {
		if (tested.runNonDet(input, context).hasMoreElements())
			return (Frame)input;
		else 
			return null;
	}
	
	public Compiled negate() {
		return new CompiledNot(tested);
	}

	public String toString() {
		return "TEST(" + tested + ")";
	}

}
