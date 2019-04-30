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

public class ExistQuantifierAndNotFilterTest
  extends TyrubaTest
{
  public ExistQuantifierAndNotFilterTest(String arg0)
  {
    super(arg0);
  }
  
  public void setUp()
    throws Exception
  {
    TyrubaTest.initfile = false;
    super.setUp();
  }
  
  public void testSimpleNot()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("planet :: String\nMODES (F) IS MULTI END\n");
    
    this.frontend.parse("orbits :: String, String\nMODES\n(B,F) IS SEMIDET\n(F,F)  IS MULTI\n(F,B) IS NONDET\nEND\n");
    
    this.frontend.parse("no_moon :: String\nMODES (F) IS NONDET END\n");
    
    this.frontend.parse("no_moon(?x) :- NOT( EXISTS ?m : orbits(?m,?x) ), planet(?x).");
  }
  
  public void testBadNot()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("planet :: String\nMODES (F) IS MULTI END\n");
    
    this.frontend.parse("orbits :: String, String\nMODES\n(B,F) IS SEMIDET\n(F,F)  IS MULTI\n(F,B) IS NONDET\nEND\n");
    
    this.frontend.parse("no_moon :: String\nMODES (F) IS NONDET END\n");
    try
    {
      this.frontend.parse("no_moon(?x) :- NOT(orbits(?m,?x)), planet(?x).");
      fail("This should have thrown a TypeModeError because ?m is not bound before entering NOT");
    }
    catch (TypeModeError localTypeModeError) {}
  }
  
  public void testBadExist()
    throws Exception
  {
    TyrubaTest.initfile = true;
    super.setUp();
    
    this.frontend.parse("foo :: ?t\nMODES (F) IS NONDET END");
    try
    {
      this.frontend.parse("foo(?x) :- EXISTS ?x : member(?x,[bar]).");
      fail("This should have thrown a TypeModeError because ?x does not become bound after EXISTS");
    }
    catch (TypeModeError e)
    {
      System.err.println(e.getMessage());
    }
    this.frontend.parse("foo1 :: String\nMODES (F) IS NONDET END");
    
    this.frontend.parse("foo1(?x) :- member(?x,[bar,foo]), (EXISTS ?x : equals(?x,bar)).");
    
    test_must_succeed("foo1(bar)");
    test_must_succeed("foo1(foo)");
  }
}
