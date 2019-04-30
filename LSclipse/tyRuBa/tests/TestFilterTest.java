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

import tyRuBa.engine.FrontEnd;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class TestFilterTest
  extends TyrubaTest
{
  public TestFilterTest(String arg0)
  {
    super(arg0);
  }
  
  public void setUp()
    throws Exception
  {
    TyrubaTest.initfile = true;
    super.setUp();
  }
  
  public void testTestFilter()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("test_append_ffb :: [?t]\nMODES (B) IS DET END\n");
    
    this.frontend.parse("test_append_ffb(?z) :- TEST(EXISTS ?x,?y : append(?x, ?y, ?z)).");
    
    test_must_succeed("test_append_ffb([1,2,3])");
    
    this.frontend.parse("test_append_bbf :: [?t],[?t]\nMODES (B,B) IS DET END\n");
    
    this.frontend.parse("test_append_bbf(?x, ?y) :- TEST(EXISTS ?z : append(?x,?y,?z)).");
    
    test_must_succeed("test_append_bbf([1],[2])");
    
    this.frontend.parse("test_list_ref_bbf :: =Integer, [?t]\n");
    this.frontend.parse("test_list_ref_bbf(?x,?y) :- TEST(EXISTS ?z : list_ref(?x,?y,?z)).");
    
    test_must_succeed("test_list_ref_bbf(0,[1,2])");
    test_must_fail("test_list_ref_bbf(4,[1])");
    
    this.frontend.parse("test_list_ref_fbb :: [?t], ?t\n");
    this.frontend.parse("test_list_ref_fbb(?y,?z) :- TEST(EXISTS ?x : list_ref(?x,?y,?z)).");
    
    test_must_succeed("test_list_ref_fbb([1,2,3],2)");
    test_must_fail("test_list_ref_fbb([1],2)");
    
    test_must_succeed("TEST(append(?,?,[1,2,3]))");
    test_must_fail("TEST(member(?,[]))");
  }
}
