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

import junit.framework.TestCase;
import tyRuBa.modes.Multiplicity;

public class MultiplicityTest
  extends TestCase
{
  Multiplicity zero = Multiplicity.zero;
  Multiplicity one = Multiplicity.one;
  Multiplicity many = Multiplicity.many;
  Multiplicity infinite = Multiplicity.infinite;
  
  public MultiplicityTest(String arg0)
  {
    super(arg0);
  }
  
  public void testObjects()
  {
    assertFalse(this.zero.equals(this.one));
    assertFalse(this.one.equals(this.zero));
    
    assertFalse(this.zero.equals(this.many));
    assertFalse(this.many.equals(this.zero));
    
    assertFalse(this.one.equals(this.many));
    assertFalse(this.many.equals(this.one));
    
    assertTrue(this.zero.equals(this.zero));
    assertTrue(this.one.equals(this.one));
    assertTrue(this.many.equals(this.many));
    assertTrue(this.infinite.equals(this.infinite));
  }
  
  public void testMultiply()
  {
    for (int i = 0; i < 5; i++)
    {
      Multiplicity im = Multiplicity.fromInt(i);
      for (int j = 0; j < 5; j++)
      {
        Multiplicity jm = Multiplicity.fromInt(j);
        Multiplicity result = im.multiply(jm);
        assertEquals(result, Multiplicity.fromInt(i * j));
      }
    }
  }
  
  public void testInfMultiply()
  {
    assertEquals(this.zero, this.zero.multiply(this.infinite));
    assertEquals(this.zero, this.infinite.multiply(this.zero));
    for (int i = 1; i < 5; i++)
    {
      Multiplicity im = Multiplicity.fromInt(i);
      assertEquals(this.infinite, im.multiply(this.infinite));
      assertEquals(this.infinite, this.infinite.multiply(im));
    }
    assertEquals(this.infinite, this.infinite.multiply(this.infinite));
  }
  
  public void testAdd()
  {
    for (int i = 0; i < 5; i++)
    {
      Multiplicity im = Multiplicity.fromInt(i);
      for (int j = 0; j < 5; j++)
      {
        Multiplicity jm = Multiplicity.fromInt(j);
        Multiplicity result = im.add(jm);
        assertEquals(result, Multiplicity.fromInt(i + j));
      }
    }
  }
  
  public void testInfAdd()
  {
    for (int i = 0; i < 5; i++)
    {
      Multiplicity im = Multiplicity.fromInt(i);
      assertEquals(this.infinite, im.add(this.infinite));
      assertEquals(this.infinite, this.infinite.add(im));
    }
    assertEquals(this.infinite, this.infinite.add(this.infinite));
  }
  
  public void testCompare()
  {
    for (int i = 0; i <= 2; i++)
    {
      Multiplicity im = Multiplicity.fromInt(i);
      assertEquals(im.compareTo(this.infinite), -1);
      assertEquals(this.infinite.compareTo(im), 1);
      for (int j = 0; j <= 2; j++)
      {
        Multiplicity jm = Multiplicity.fromInt(j);
        if (i == j) {
          assertEquals(im.compareTo(jm), 0);
        }
        if (i < j) {
          assertEquals(im.compareTo(jm), -1);
        }
        if (i > j) {
          assertEquals(im.compareTo(jm), 1);
        }
      }
    }
    assertEquals(this.infinite.compareTo(this.infinite), 0);
  }
}
