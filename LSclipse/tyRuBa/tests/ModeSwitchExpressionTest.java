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

public class ModeSwitchExpressionTest
  extends TyrubaTest
{
  public ModeSwitchExpressionTest(String arg0)
  {
    super(arg0);
  }
  
  public void setUp()
    throws Exception
  {
    super.setUp();
  }
  
  public void testWithoutDefault()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: =Integer, =Integer\nMODES\n(F,B) IS DET\n(B,F) IS DET\n END");
    
    this.frontend.parse("foo(?x,?y) :- BOUND ?x : sum(?x,1,?y)| BOUND ?y: sum(?y,1,?x).");
    
    test_must_equal("foo(1,?y)", "?y", "2");
    test_must_equal("foo(?x,1)", "?x", "2");
  }
  
  public void testWithDefault()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: =Integer, =Integer\nMODES\n(F,B) IS DET\n(B,F) IS DET\nEND");
    
    this.frontend.parse("foo(?x,?y) :- BOUND ?x : sum(?x,1,?y)| DEFAULT: sum(?y,1,?x).");
    
    test_must_equal("foo(1,?y)", "?y", "2");
    test_must_equal("foo(?x,1)", "?x", "2");
  }
}
