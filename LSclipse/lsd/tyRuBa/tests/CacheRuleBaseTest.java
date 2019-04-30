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

import tyRuBa.engine.RuleBase;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class CacheRuleBaseTest extends TyrubaTest {
	
	public void setUp() throws Exception {
		RuleBase.useCache = true;
		TyrubaTest.initfile = false;
		super.setUp();
	}

	public CacheRuleBaseTest(String arg0) {
		super(arg0);
	}
	
	public void test() throws ParseException, TypeModeError {
		frontend.parse("foo, bar, goo :: String\n" +			"MODES (F) IS NONDET END");
			
		test_must_fail("foo(?x)");
		frontend.parse("foo(?x) :- bar(?x).");
		test_must_fail("foo(?x)");
		frontend.parse("bar(bar).");
		test_must_succeed("foo(bar)");
		
		frontend.parse("goo(goo).");
		frontend.parse("bar(?x) :- goo(?x).");
		test_must_succeed("foo(goo)");
		test_resultcount("foo(?x)", 2);
	}
	
	public void testMinnieBug() throws ParseException, TypeModeError {
		frontend.parse("married :: String, String\n" +			"MODES (F,F) IS NONDET END\n");
		
		frontend.parse("married(Minnie,Mickey).");
		frontend.parse("married(?x,?y) :- married(?y,?x).");

		test_resultcount("married(?a,?b)", 2);
		test_must_succeed("married(Minnie,Mickey)");
		test_must_equal("married(Minnie,?x)", "?x", "Mickey");
	}
	
}
