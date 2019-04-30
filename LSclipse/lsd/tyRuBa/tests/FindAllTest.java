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
package tyRuBa.tests;

import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class FindAllTest extends TyrubaTest {

	public FindAllTest(String arg0) {
		super(arg0);
	}

	public void setUp() throws Exception {
		TyrubaTest.initfile = true;
		super.setUp();
	}

	public void testFindAll() throws ParseException, TypeModeError {
		frontend.parse("testje :: [[Integer]]\n" +
			"MODES (F) IS DET END");
		frontend.parse("testje(?re) :- FINDALL(append(?x,?,[1,2,3]),?x,?re).");
		
		test_must_succeed("testje([[],[1],[1,2],[1,2,3]])");
		test_must_equal("FINDALL((EXISTS ?y : append(?x,?y,[1,2,3])),?x,?re)",
			"?re", "[[],[1],[1,2],[1,2,3]]");
		test_must_succeed("member([1],?re), FINDALL(append(?x,?,[1,2,3]),?x,?re)");
		test_must_succeed("FINDALL(append(?x,?,?lst),?x,?re), append([1],[2,3],?lst)");
		
		try {
			test_must_fail("FINDALL(append(?x,?y,[1,2,3]),?x,?re)");
			fail("Should have thrown a TypeModeError because ?y is not bound.");
		}
		catch (TypeModeError e) {
		}
	}

	public void testCountAll() throws ParseException, TypeModeError {
		test_must_succeed("COUNTALL((EXISTS ?y:append(?x,?y,[1,2,3])),?x,4).");
		test_must_fail("COUNTALL((EXISTS ?y:append(?x,?y,[1,2,3])),?x,2).");
		test_must_equal("COUNTALL((EXISTS ?y: append(?x,?y,[1,2,3])),?x,?n)",
			"?n", "4");
		
		try {
			test_must_fail("COUNTALL(append(?x,?y,[1,2,3]),?x,?re)");
			fail("Should have thrown a TypeModeError because ?y is not bound.");
		}
		catch (TypeModeError e) {
		}
	}

}
