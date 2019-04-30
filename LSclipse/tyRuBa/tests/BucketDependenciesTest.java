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
import tyRuBa.engine.RuleBaseBucket;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class BucketDependenciesTest
  extends TyrubaTest
{
  public BucketDependenciesTest(String arg0)
  {
    super(arg0);
  }
  
  public void setUp()
    throws Exception
  {
    tyRuBa.engine.RuleBase.silent = true;
    super.setUp();
    this.frontend.parse(
      "bucket :: Integer MODES (F) IS NONDET END ");
    
    this.frontend.parse(
      "child :: Integer, Integer MODES     (B,F) IS NONDET     (F,B) IS SEMIDET     (F,F) IS NONDET END ");
  }
  
  private class TestBucket
    extends RuleBaseBucket
  {
    static final int num_buckets = 128;
    private int myID;
    
    public TestBucket(FrontEnd frontend, int bucketID)
    {
      super(bucketID);
      this.myID = bucketID;
    }
    
    protected void update()
      throws TypeModeError, ParseException
    {
      parse("bucket(" + this.myID + ").");
      assertChild(2 * this.myID + 1);
      assertChild(2 * this.myID + 2);
    }
    
    private void assertChild(int childID)
      throws ParseException, TypeModeError
    {
      if (childID < 128)
      {
        new TestBucket(BucketDependenciesTest.this, frontend(), childID);
        parse("child(" + this.myID + "," + childID + ").");
      }
    }
  }
  
  public void testBucketUpdateWithDependencies()
    throws ParseException, TypeModeError
  {
    new TestBucket(this.frontend, 0);
    test_resultcount("bucket(?id)", 128);
  }
  
  public void testBucketUpdateWithDependenciesDouble()
    throws ParseException, TypeModeError
  {
    new TestBucket(this.frontend, 1);
    new TestBucket(this.frontend, 2);
    test_resultcount("bucket(?id)", 127);
  }
}
