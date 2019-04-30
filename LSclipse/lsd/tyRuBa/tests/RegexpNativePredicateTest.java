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

public class RegexpNativePredicateTest extends TyrubaTest {

	public RegexpNativePredicateTest(String arg0) {
		super(arg0);
	}
	
	public void setUp() throws Exception {
		TyrubaTest.initfile = true;
		super.setUp();
	}

	public void testRegexp() throws ParseException, TypeModeError {
		test_must_succeed("RegExp(/c*/)");
		test_must_fail("list_ref(0,[1,/c*/],?x), RegExp(?x)");
		test_must_fail("list_ref(0,[abc,/c*/],?x), RegExp(?x)");
	}

	public void testReMatch() throws ParseException, TypeModeError {
		test_must_succeed("re_match(/c*/,ccc)");
		test_must_fail("re_match(/a/,ccc)");
		test_must_succeed("re_match(/b+/,abc)");
	}
	
	public void testBadUseOfRegexp() throws ParseException, TypeModeError {
		frontend.parse(
				"TYPE Element AS String " +
				"name :: Element, String " +
				"MODES (B,F) IS SEMIDET (F,F) IS NONDET (F,B) IS NONDET END");
		test_must_fail("name(foo::Element,/bar/)"); 
		 //TODO: in a better implementation this should really be a type error!
	}
}
