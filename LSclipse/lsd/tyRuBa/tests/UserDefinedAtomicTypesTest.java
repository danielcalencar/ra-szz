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
 * Created on Mar 2, 2005
 */
package tyRuBa.tests;

import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

/**
 * @author kdvolder
 */
public class UserDefinedAtomicTypesTest extends TyrubaTest {

	public UserDefinedAtomicTypesTest(String arg0) {
		super(arg0);
	}
	
	/**
	 * This test is known to fail at this time, it is testing a feature we wish to support in
	 * the future.
	 */
	public void testUnification() throws ParseException, TypeModeError {
		frontend.parse("TYPE Foo AS String");
		frontend.parse(
				"foo2string :: Foo, String \n" +
				"MODES (B,F) IS DET (F,B) IS DET END");
		
		frontend.parse("foo2string(?s::Foo,?s).");

		test_must_succeed("foo2string(abc::Foo,abc)");
		test_must_fail("foo2string(abc::Foo,ab)");
		test_must_equal("foo2string(abc::Foo,?x)", "?x", "abc");
		test_must_equal("foo2string(?x::Foo,abc)", "?x", "abc");
		test_must_equal("foo2string(?x,abc)", "?x", "abc::Foo");
	}
	
}
