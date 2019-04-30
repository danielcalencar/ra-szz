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
import tyRuBa.engine.SimpleRuleBaseBucket;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class RuleBaseBucketTest
  extends TyrubaTest
{
  SimpleRuleBaseBucket bucket;
  SimpleRuleBaseBucket otherBucket;
  
  public void setUp()
    throws Exception
  {
    tyRuBa.engine.RuleBase.silent = true;
    super.setUp();
    this.bucket = new SimpleRuleBaseBucket(this.frontend);
    this.otherBucket = new SimpleRuleBaseBucket(this.frontend);
  }
  
  public void testOutdateSemiDetPersistentFact()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: String, String \nPERSISTENT MODES (B,F) IS SEMIDET END \n");
    
    this.bucket.addStuff("foo(bucket,buck).");
    this.bucket.addStuff("foo(target,targ).");
    test_resultcount("foo(bucket,?x)", 1);
    test_resultcount("foo(bucket,buck)", 1);
    
    this.bucket.setOutdated();
    
    test_resultcount("foo(bucket,?x)", 1);
    test_resultcount("foo(bucket,buck)", 1);
  }
  
  public void testOutdateSemiDetPersistentFact2()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: String, String \nPERSISTENT MODES (B,F) IS SEMIDET END \n");
    
    this.bucket.addStuff("foo(bucket,buck).");
    this.bucket.addStuff("foo(target,targ).");
    test_resultcount("foo(bucket,?x)", 1);
    test_resultcount("foo(bucket,buck)", 1);
    
    this.bucket.clearStuff();
    this.bucket.addStuff("foo(bucket,buck2).");
    this.bucket.addStuff("foo(target,targ2).");
    test_resultcount("foo(bucket,?x)", 1);
    test_resultcount("foo(bucket,buck2)", 1);
  }
  
  public void testOutdateSemiDetPersistentFact3()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: String, String \nPERSISTENT MODES (B,F) IS SEMIDET END \n");
    
    this.bucket.addStuff("foo(a#bucket,buck).");
    this.bucket.addStuff("foo(a#target,targ).");
    test_resultcount("foo(a#bucket,?x)", 1);
    test_resultcount("foo(a#bucket,buck)", 1);
    
    this.bucket.clearStuff();
    this.bucket.addStuff("foo(b#bucket,buck).");
    this.bucket.addStuff("foo(b#target,targ).");
    
    test_resultcount("foo(b#bucket,?x)", 1);
    test_resultcount("foo(b#bucket,buck)", 1);
    
    this.bucket.clearStuff();
    this.bucket.addStuff("foo(a#bucket,buck1).");
    this.bucket.addStuff("foo(a#target,targ1).");
    
    test_resultcount("foo(a#bucket,?x)", 1);
    test_resultcount("foo(a#bucket,buck1)", 1);
  }
  
  public void testGlobalFact()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: String \nMODES (F) IS NONDET END \n");
    
    this.bucket.addStuff("foo(bucket).");
    this.frontend.parse("foo(frontend).");
    
    test_must_succeed("foo(bucket)");
    test_must_succeed("foo(frontend)");
    test_must_succeed("foo(bucket)", this.bucket);
    test_must_succeed("foo(frontend)", this.bucket);
    test_must_fail("foo(bad)");
    
    this.bucket.clearStuff();
    
    test_must_fail("foo(bucket)");
    test_must_fail("foo(bucket)", this.bucket);
    test_must_succeed("foo(frontend)");
    test_must_succeed("foo(frontend)", this.bucket);
    test_must_fail("foo(c)");
  }
  
  public void testAllCleared()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: String \nMODES (F) IS NONDET END \n");
    
    this.bucket.addStuff("foo(bucket).");
    this.bucket.addStuff("foo(bucket2).");
    this.bucket.addStuff("foo(bucket3).");
    
    test_must_succeed("foo(bucket).");
    
    this.bucket.clearStuff();
    
    test_must_fail("foo(?x).");
  }
  
  public void testLocalFact()
    throws ParseException, TypeModeError
  {
    this.bucket.addStuff("bar :: String");
    this.bucket.addStuff("bar(bucket).");
    test_must_succeed("bar(bucket)", this.bucket);
    try
    {
      test_must_fail("bar(bucket)");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to the frontend.");
    }
    catch (TypeModeError localTypeModeError1) {}
    try
    {
      this.frontend.parse("bar(frontend).");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to the frontend.");
    }
    catch (TypeModeError localTypeModeError2) {}
    this.bucket.clearStuff();
    try
    {
      test_must_fail("bar(bucket).", this.bucket);
      fail("This should have thrown a TypeModeError because bucket has been cleared and the predicate bar is no longer declared in bucket.");
    }
    catch (TypeModeError localTypeModeError3) {}
  }
  
  public void testDuplicate()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foobar :: String");
    try
    {
      this.bucket.parse("foobar :: String");
      fail("This should have thrown a TypeModeError since foobar is declared in the frontend, bucket cannot declare it again.");
    }
    catch (TypeModeError localTypeModeError) {}
  }
  
  public void testGlobalRule()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: Object");
    this.bucket.addStuff("foo(?x) :- equals(?x, bucket).");
    this.frontend.parse("foo(?x) :- equals(?x, 1).");
    
    test_must_succeed("foo(bucket)");
    test_must_succeed("foo(1)");
    test_must_succeed("foo(bucket)", this.bucket);
    test_must_succeed("foo(1)", this.bucket);
    test_must_fail("foo(bad)");
    
    this.bucket.clearStuff();
    
    test_must_fail("foo(bucket)");
    test_must_fail("foo(bucket)", this.bucket);
    test_must_succeed("foo(1)");
    test_must_succeed("foo(1)", this.bucket);
    test_must_fail("foo(c)");
  }
  
  public void testLocalRule()
    throws ParseException, TypeModeError
  {
    this.bucket.addStuff("bar :: String");
    this.bucket.addStuff("bar(?x) :- equals(?x, bucket).");
    test_must_succeed("bar(bucket)", this.bucket);
    try
    {
      test_must_fail("bar(bucket)");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to frontend.");
    }
    catch (TypeModeError localTypeModeError1) {}
    try
    {
      this.frontend.parse("bar(?x) :- equals(?x, frontend).");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to frontend.");
    }
    catch (TypeModeError localTypeModeError2) {}
    this.bucket.clearStuff();
    try
    {
      test_must_fail("bar(bucket).", this.bucket);
      fail("This should have thrown a TypeModeError because bucket has been cleared and the predicate bar is no longer declared in bucket.");
    }
    catch (TypeModeError e)
    {
      System.err.println(e.getMessage());
    }
  }
  
  public void testGlobalFactWithMultipleBuckets()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: String");
    this.frontend.parse("foo(frontend).");
    this.otherBucket.addStuff("foo(otherBucket).");
    this.bucket.addStuff("foo(bucket).");
    
    test_must_succeed("foo(frontend)");
    test_must_succeed("foo(frontend)", this.otherBucket);
    test_must_succeed("foo(frontend)", this.bucket);
    
    test_must_succeed("foo(otherBucket)");
    test_must_succeed("foo(otherBucket)", this.otherBucket);
    test_must_succeed("foo(otherBucket)", this.bucket);
    
    test_must_succeed("foo(bucket)");
    test_must_succeed("foo(bucket)", this.otherBucket);
    test_must_succeed("foo(bucket)", this.bucket);
    
    this.otherBucket.clearStuff();
    
    test_must_succeed("foo(frontend)");
    test_must_succeed("foo(frontend)", this.otherBucket);
    test_must_succeed("foo(frontend)", this.bucket);
    
    test_must_fail("foo(otherBucket)");
    test_must_fail("foo(otherBucket)", this.otherBucket);
    test_must_fail("foo(otherBucket)", this.bucket);
    
    test_must_succeed("foo(bucket)");
    test_must_succeed("foo(bucket)", this.otherBucket);
    test_must_succeed("foo(bucket)", this.bucket);
  }
  
  public void testLocalFactWithMultipleBuckets()
    throws ParseException, TypeModeError
  {
    this.bucket.addStuff("bar :: String");
    this.bucket.addStuff("bar(bucket).");
    test_must_succeed("bar(bucket)", this.bucket);
    try
    {
      test_must_fail("bar(bucket)");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to frontend.");
    }
    catch (TypeModeError localTypeModeError1) {}
    try
    {
      test_must_fail("bar(bucket)", this.otherBucket);
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to otherBucket.");
    }
    catch (TypeModeError localTypeModeError2) {}
    this.otherBucket.addStuff("bar :: String");
    this.otherBucket.addStuff("bar(otherBucket).");
    test_must_succeed("bar(otherBucket)", this.otherBucket);
    test_must_fail("bar(otherBucket)", this.bucket);
    try
    {
      test_must_fail("bar(otherBucket)");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to frontend.");
    }
    catch (TypeModeError localTypeModeError3) {}
    test_must_fail("bar(bucket)", this.otherBucket);
  }
  
  public void testGlobalRuleWithMultipleBuckets()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("foo :: Object");
    this.frontend.parse("foo(?x) :- equals(?x,1).");
    this.otherBucket.addStuff("foo(?x) :- equals(?x,10.1).");
    this.bucket.addStuff("foo(?x) :- equals(?x,bucket).");
    
    test_must_succeed("foo(1)");
    test_must_succeed("foo(1)", this.otherBucket);
    test_must_succeed("foo(1)", this.bucket);
    
    test_must_succeed("foo(10.1)");
    test_must_succeed("foo(10.1)", this.otherBucket);
    test_must_succeed("foo(10.1)", this.bucket);
    
    test_must_succeed("foo(bucket)");
    test_must_succeed("foo(bucket)", this.otherBucket);
    test_must_succeed("foo(bucket)", this.bucket);
    
    this.otherBucket.clearStuff();
    
    test_must_succeed("foo(1)");
    test_must_succeed("foo(1)", this.otherBucket);
    test_must_succeed("foo(1)", this.bucket);
    
    test_must_fail("foo(10.1)");
    test_must_fail("foo(10.1)", this.otherBucket);
    test_must_fail("foo(10.1)", this.bucket);
    
    test_must_succeed("foo(bucket)");
    test_must_succeed("foo(bucket)", this.otherBucket);
    test_must_succeed("foo(bucket)", this.bucket);
  }
  
  public void testLocalRuleWithMultipleBuckets()
    throws ParseException, TypeModeError
  {
    this.bucket.addStuff("bar :: String");
    this.bucket.addStuff("bar(?x) :- equals(?x,bucket).");
    test_must_succeed("bar(bucket)", this.bucket);
    try
    {
      test_must_fail("bar(bucket)");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to frontend.");
    }
    catch (TypeModeError localTypeModeError1) {}
    try
    {
      test_must_fail("bar(bucket)", this.otherBucket);
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to otherBucket.");
    }
    catch (TypeModeError localTypeModeError2) {}
    this.otherBucket.addStuff("bar :: String");
    this.otherBucket.addStuff("bar(?x) :- equals(?x,otherBucket).");
    test_must_succeed("bar(otherBucket)", this.otherBucket);
    test_must_fail("bar(otherBucket)", this.bucket);
    try
    {
      test_must_fail("bar(otherBucket)");
      fail("This should have thrown a TypeModeError because the predicate bar is unknown to frontend.");
    }
    catch (TypeModeError localTypeModeError3) {}
    test_must_fail("bar(bucket)", this.otherBucket);
  }
  
  public void testRuleBaseCollectionBug()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("bar :: String,String MODES (F,F) IS NONDET END");
    
    this.bucket.addStuff("bar(?x,?z) :- bar(?x,?y), bar(?y,?z).");
    this.frontend.updateBuckets();
    this.bucket.setOutdated();
    this.bucket.addStuff("bar(?x,?z) :- bar(?x,?y), bar(?y,?z), bar(?x,?z).");
    this.frontend.updateBuckets();
    this.bucket.setOutdated();
    this.frontend.updateBuckets();
  }
  
  public void testRuleBaseDestroy()
    throws ParseException, TypeModeError
  {
    this.frontend.parse("factFrom :: String MODES (F) IS NONDET END");
    
    this.bucket.addStuff("factFrom(bucket).");
    this.otherBucket.addStuff("factFrom(otherBucket).");
    test_resultcount("factFrom(?buck)", 2);
    this.otherBucket.destroy();
    test_resultcount("factFrom(?buck)", 1);
    this.bucket.destroy();
    test_resultcount("factFrom(?buck)", 0);
  }
  
  public RuleBaseBucketTest(String arg0)
  {
    super(arg0);
  }
}
