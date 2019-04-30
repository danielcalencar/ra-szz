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
package tyRuBa.modes;

public abstract class Multiplicity
  implements Comparable
{
  public static Multiplicity fromInt(int i)
  {
    if (i < 0) {
      throw new Error("Only works for positive integers");
    }
    if (i == 0) {
      return zero;
    }
    if (i == 1) {
      return one;
    }
    return many;
  }
  
  public static final Multiplicity zero = new Multiplicity(0)
  {
    public String toString()
    {
      return "Mult(0)";
    }
    
    public Multiplicity multiply(Multiplicity other)
    {
      return this;
    }
    
    public Multiplicity add(Multiplicity other)
    {
      return other;
    }
  };
  public static final Multiplicity one = new Multiplicity(1)
  {
    public String toString()
    {
      return "Mult(1)";
    }
    
    public Multiplicity multiply(Multiplicity other)
    {
      return other;
    }
    
    public Multiplicity add(Multiplicity other)
    {
      if (other.equals(zero)) {
        return this;
      }
      if (other.equals(infinite)) {
        return other;
      }
      return many;
    }
  };
  public static final Multiplicity many = new Multiplicity(2)
  {
    public String toString()
    {
      return "Mult(>1)";
    }
    
    public Multiplicity multiply(Multiplicity other)
    {
      if (other.equals(zero)) {
        return other;
      }
      if (other.equals(infinite)) {
        return other;
      }
      return this;
    }
    
    public Multiplicity add(Multiplicity other)
    {
      if (other.equals(infinite)) {
        return other;
      }
      return this;
    }
  };
  public static final Multiplicity infinite = new Multiplicity(999)
  {
    public String toString()
    {
      return "Mult(INFINITE)";
    }
    
    public Multiplicity multiply(Multiplicity other)
    {
      if (other.equals(zero)) {
        return other;
      }
      return this;
    }
    
    public Multiplicity add(Multiplicity other)
    {
      return this;
    }
  };
  int compareInt;
  
  protected Multiplicity(int compareInt)
  {
    this.compareInt = compareInt;
  }
  
  public abstract Multiplicity multiply(Multiplicity paramMultiplicity);
  
  public abstract Multiplicity add(Multiplicity paramMultiplicity);
  
  public int compareTo(Object o)
  {
    if (this.compareInt < ((Multiplicity)o).compareInt) {
      return -1;
    }
    if (this.compareInt > ((Multiplicity)o).compareInt) {
      return 1;
    }
    return 0;
  }
  
  public boolean equals(Object arg0)
  {
    return compareTo(arg0) == 0;
  }
  
  public int hashCode()
  {
    return (this.compareInt + 13) * 113;
  }
  
  public Multiplicity min(Multiplicity other)
  {
    if (compareTo(other) == 1) {
      return other;
    }
    return this;
  }
  
  public Multiplicity max(Multiplicity other)
  {
    if (compareTo(other) == -1) {
      return other;
    }
    return this;
  }
}
