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
 * Created on Oct 4, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tyRuBa.tests;

import java.util.Collection;

import tyRuBa.engine.RBExpression;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.util.ElementSource;

/**
 * @author kdvolder
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GeneralTest extends TyrubaTest {

	/**
	 * Constructor for GeneralTest.
	 * @param arg0
	 */
	public GeneralTest(String arg0) {
		super(arg0);
	}

	public void setUp() throws Exception {
		TyrubaTest.initfile = true;
		super.setUp();
	}

	public void testAskForMoreAgain() throws ParseException, TypeModeError {
		ElementSource result = frontend.frameQuery("string_append(abc,def,?x)");
		int ctr = 0;
		while (result.hasMoreElements()) {
			ctr++;
			result.nextElement();
		}
		assertEquals(1,ctr);
		assertFalse(result.hasMoreElements());
	}		

	public void testPersistentRBQuotedInFact() throws ParseException, TypeModeError {
		frontend.parse("test :: String PERSISTENT MODES (F) IS NONDET END");
		frontend.parse("test({Hola Pola!}).");
		test_must_succeed("test({Hola Pola!})");
		test_must_succeed("test(\"Hola Pola!\")");
	}
	
	public void testGetVars() throws ParseException, TypeModeError {
		RBExpression exp = frontend.makeExpression("string_append(?x,?a,abc);string_append(?x,?b,def).");
		Collection vars = exp.getVariables();
		assertEquals(vars.size(),1);
		assertEquals(vars.toString(),"[?x]");
	}
}
