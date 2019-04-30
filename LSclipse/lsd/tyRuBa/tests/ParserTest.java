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

public class ParserTest extends TyrubaTest {

	public ParserTest(String arg0) {
		super(arg0);
	}

	public void testParseExpression() throws ParseException, TypeModeError {
		try {
			test_must_fail("string_append(?x,?y,abc) string_append(?y,?z,abc)");
			fail("Should throw a parse exception");
		}
		catch (ParseException e) {
		}
	}

}
