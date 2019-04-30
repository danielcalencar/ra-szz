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

/*
 * Created on Apr 17, 2003
 */

import junit.framework.TestCase;
import junit.framework.TestSuite;
import tyRuBa.modes.*;

/**
 * @author kdvolder
 */
public class ModeTest extends TestCase {

	/**
	 * Constructor for ModeTest.
	 * @param arg0
	 */
	public ModeTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testMultiply() {
		for (int ilo = 0; ilo <= 1; ilo++) {
			Multiplicity ilom = Multiplicity.fromInt(ilo);
			for (int ihi = ilo; ihi <= 2; ihi++) {
				Multiplicity ihim = Multiplicity.fromInt(ihi);

				Mode imode = new Mode(ilom, ihim);

				for (int jlo = 0; jlo <= 1; jlo++) {
					Multiplicity jlom = Multiplicity.fromInt(jlo);
					for (int jhi = jlo; jhi <= 2; jhi++) {
						Multiplicity jhim = Multiplicity.fromInt(jhi);
						Mode jmode = new Mode(jlom, jhim);
						Mode result = imode.multiply(jmode);
						assertEquals(
							"" + imode + " * " + jmode,
							result,
							new Mode(
								Multiplicity.fromInt(ilo * jlo),
								Multiplicity.fromInt(ihi * jhi)));
						assertTrue(
							"Ordercheck for "
								+ imode
								+ " * "
								+ jmode
								+ " = "
								+ result,
							result.lo.compareTo(result.hi) <= 0);
					}
				}
			}
		}
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(MultiplicityTest.class);
		suite.addTestSuite(ModeTest.class);
		return suite;
	}

}
