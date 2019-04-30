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

public class ModeCheckTest
  extends TyrubaTest
{
  public ModeCheckTest(String arg0)
  {
    super(arg0);
  }
  
  protected void setUp()
    throws Exception
  {
    TyrubaTest.initfile = true;
    super.setUp();
  }
  
  public void testBadRule()
    throws ParseException, TypeModeError
  {
    try
    {
      this.frontend.parse("foo :: ?x, [?x]\nMODES (F,B) IS NONDET END");
      
      this.frontend.parse("foo(?x,?lst) :- member(?xx,?lst).");
      fail("This should have thrown a TypeModeError because ?xx never becomes bound");
    }
    catch (TypeModeError localTypeModeError) {}
  }
  
  public void testBadFact()
    throws ParseException, TypeModeError
  {
    try
    {
      this.frontend.parse("append(?x,[],?y).");
      fail("This should have thrown a TypeModeError because in BBF, ?y never becomes bound");
    }
    catch (TypeModeError localTypeModeError) {}
  }
  
  public void testDisjunction()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("studiesIn, worksIn :: String, String, String\nMODES\n(F,B,B) IS NONDET\n(B,F,F) IS SEMIDET\nEND\n");
    
    this.frontend.parse("staffOrStudent :: String, String, String\nMODES\n(F,B,B) IS NONDET\n(B,F,F) IS NONDET\nEND\n");
    
    this.frontend.parse("studiesIn(Terry,UBC,CPSC).");
    this.frontend.parse("worksIn(Kris,UBC,CPSC).");
    this.frontend.parse("staffOrStudent(?name,?sch,?dept) :- studiesIn(?name,?sch,?dept); worksIn(?name,?sch,?dept).");
  }
  
  public void testBadDisjunction()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("studiesIn, worksIn :: String, String, String\nMODES\n(F,B,B) IS NONDET\n(B,F,F) IS NONDET\nEND\n");
    
    this.frontend.parse("staffOrStudent :: String, String, String\nMODES\n(F,B,B) IS NONDET\n(B,F,F) IS NONDET\nEND\n");
    
    this.frontend.parse("studiesIn(Terry,UBC,CPSC).");
    this.frontend.parse("worksIn(Kris,UBC,CPSC).");
    try
    {
      this.frontend.parse("staffOrStudent(?name,?sch,?dept) :- studiesIn(?name,UBC,CPSC); worksIn(?name,?sch,?dept).");
      
      fail("This should have thrown a TypeModeError: only ?name becomes bound, but ?sch and ?dept remain unbound");
    }
    catch (TypeModeError localTypeModeError) {}
  }
  
  public void testConjunction()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("friends, friendOfFriend :: String, String\nMODES\n(F,B) IS NONDET\n(B,F) IS NONDET\nEND\n");
    
    this.frontend.parse("friends(Terry,Edith).");
    this.frontend.parse("friends(Edith,Rick).");
    
    this.frontend.parse("friendOfFriend(?x,?z) :- friends(?x,?y), friends(?y,?z).");
  }
  
  public void testBadConjunction()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("friends, friendOfFriend :: String, String\nMODES\n(F,B) IS NONDET\n(B,F) IS NONDET\nEND\n");
    try
    {
      this.frontend.parse("friendOfFriend(?x,?z) :- friends(?xx,?y), friends(?y,?z).");
      fail("This should have thrown a TypeModeError becuase ?x never becomes bound in FB");
    }
    catch (TypeModeError localTypeModeError) {}
  }
  
  public void testWhyWeNeedCovertToNormalForm()
    throws ParseException, TypeModeError
  {
    test_must_succeed("(sum(1,2,?x); sum(1,2,?y)), sum(?x,?y,5)");
  }
  
  public void testRuleWithNoArgument()
    throws ParseException, TypeModeError
  {
    try
    {
      this.frontend.parse("foo :: ()MODES () IS NONDET END");
      
      fail("This should have thrown a TypeModeError since only FAILURE or SUCCESS can be returned");
    }
    catch (TypeModeError localTypeModeError) {}
    this.frontend.parse("foo :: ()");
    this.frontend.parse("foo() :- append(?x,?y,[1,2,3]).");
  }
  
  public void testInsertion()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: Object \n MODES (F) IS SEMIDET END");
    
    this.frontend.parse("foo(?x) :- equals(?x,1).");
    try
    {
      this.frontend.parse("foo(?x) :- equals(?x,1.1).");
      fail("This should have thrown a TypeModeError since foo(?x) would returnmultiple answers (i.e. ?x=1, ?x=1.1).");
    }
    catch (TypeModeError localTypeModeError1) {}
    this.frontend.parse("foo :: Object, Object \n MODES (F,B) IS SEMIDET END");
    
    this.frontend.parse("foo(?x,?y) :- equals(?x,?y), equals(?y,foo).");
    this.frontend.parse("foo(?x,?y) :- equals(?x,?y), equals(?y,1).");
    this.frontend.parse("foo(?x,?y) :- equals(?x,?y), equals(?y,1.1).");
    
    this.frontend.parse("foo1 :: Object, Object \n MODES (F,B) IS SEMIDET END");
    
    this.frontend.parse("foo1(?x,?y) :- equals(?x,?y), equals(?y,1.1).");
    this.frontend.parse("foo1(?x,?y) :- equals(?x,?y), equals(?y,foo).");
    this.frontend.parse("foo1(?x,?y) :- equals(?x,?y), equals(?y,1).");
    
    this.frontend.parse("foo2 :: Object, Object \n MODES (F,B) IS SEMIDET END");
    
    this.frontend.parse("foo2(?x,?y) :- equals(?x,?y), equals(?y,foo).");
    try
    {
      this.frontend.parse("foo2(?x,?y) :- equals(?x,?y), equals(?y,bar).");
      fail("This should have thrown a TypeModeError since there is already a rule with inferred type String that returns mode SEMIDET");
    }
    catch (TypeModeError localTypeModeError2) {}
  }
}
