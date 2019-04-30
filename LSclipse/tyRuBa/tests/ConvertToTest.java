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

import java.io.PrintStream;
import tyRuBa.engine.FrontEnd;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class ConvertToTest
  extends TyrubaTest
{
  public ConvertToTest(String arg0)
  {
    super(arg0);
  }
  
  public void setUp()
    throws Exception
  {
    TyrubaTest.initfile = true;
    super.setUp();
  }
  
  public void testConvertToType1()
    throws ParseException, TypeModeError
  {
    test_must_succeed("convertToInteger(1,1)");
    test_must_equal("convertToInteger(1,?x)", "?x", "1");
    test_must_equal("convertToInteger(?x,1)", "?x", "1");
    test_must_fail("convertToInteger(1,2)");
    
    test_must_succeed("convertToString(abc,abc)");
    test_must_equal("convertToString(abc,?x)", "?x", "abc");
    test_must_equal("convertToString(?x,abc)", "?x", "abc");
    
    this.frontend.parse("TYPE Sno AS String");
    this.frontend.parse("TYPE Bol AS String");
    this.frontend.parse("TYPE Snobol = Sno | Bol");
    test_must_succeed("convertToSno(sno::Sno,sno::Sno)");
    test_must_succeed("convertToSnobol(?x,sno::Sno)");
    test_must_succeed("convertToSno(?x,?y), member(?x,[sno::Sno,bol::Bol])");
    test_must_equal("convertToSno(?x,?y), member(?x,[sno::Sno,bol::Bol])", "?y", "sno::Sno");
    test_resultcount("convertToSno(?x,?y), member(?x,[sno::Sno,bol::Bol])", 1);
  }
  
  public void testConvertToType2()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: =Integer, =Integer\nMODES (F,F) IS NONDET END\n");
    
    this.frontend.parse("foo(?x,?y) :- member(?x1,?lst), sum(?x,?x,?y), append([1,2],[a,b],?lst),convertToInteger(?x1,?x).");
    
    test_must_succeed("foo(1,2)");
    test_must_succeed("foo(2,4)");
    
    this.frontend.parse("bar :: =Integer\nMODES (F) IS NONDET END");
    try
    {
      this.frontend.parse("bar(?x) :- member(?x1,[1,2,3]), convertToString(?x1,?x).");
      fail("This should have thrown a TypeModeError because a string cannot be converted from an integer");
    }
    catch (TypeModeError e)
    {
      System.err.println(e.getMessage());
    }
  }
}
