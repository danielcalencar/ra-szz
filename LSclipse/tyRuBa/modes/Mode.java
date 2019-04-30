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

import java.io.PrintStream;

public class Mode
  implements Cloneable
{
  public final Multiplicity lo;
  public final Multiplicity hi;
  private final String printString;
  private int numFree = 0;
  private int numBound = 0;
  
  public Mode(Multiplicity lo, Multiplicity hi)
  {
    this(lo, hi, 0, 0);
  }
  
  public Mode(Multiplicity lo, Multiplicity hi, int numFree, int numBound)
  {
    this.lo = lo;
    this.hi = hi;
    this.numFree = numFree;
    this.numBound = numBound;
    if (lo.equals(Multiplicity.zero))
    {
      if (hi.equals(Multiplicity.zero)) {
        this.printString = "FAIL";
      } else if (hi.equals(Multiplicity.one)) {
        this.printString = "SEMIDET";
      } else if (hi.equals(Multiplicity.many)) {
        this.printString = "NONDET";
      } else if (hi.equals(Multiplicity.infinite)) {
        this.printString = "INFINITE";
      } else {
        throw new Error("this should not happen");
      }
    }
    else if (lo.equals(Multiplicity.one))
    {
      if (hi.equals(Multiplicity.one)) {
        this.printString = "DET";
      } else if (hi.equals(Multiplicity.many)) {
        this.printString = "MULTI";
      } else if (hi.equals(Multiplicity.infinite)) {
        this.printString = "INFINITE";
      } else {
        throw new Error("this should not happen");
      }
    }
    else
    {
      System.err.println("lo = " + lo + "\nhi = " + hi);
      throw new Error("this should not happen");
    }
  }
  
  public static Mode makeFail()
  {
    return new Mode(Multiplicity.zero, Multiplicity.zero, 0, 0);
  }
  
  public static Mode makeSemidet()
  {
    return new Mode(Multiplicity.zero, Multiplicity.one, 0, 0);
  }
  
  public static Mode makeDet()
  {
    return new Mode(Multiplicity.one, Multiplicity.one, 0, 0);
  }
  
  public static Mode makeNondet()
  {
    return new Mode(Multiplicity.zero, Multiplicity.many, 0, 0);
  }
  
  public static Mode makeMulti()
  {
    return new Mode(Multiplicity.one, Multiplicity.many, 0, 0);
  }
  
  public boolean isFail()
  {
    return (this.lo.equals(Multiplicity.zero)) && (this.hi.equals(Multiplicity.zero));
  }
  
  public boolean isSemiDet()
  {
    return (this.lo.equals(Multiplicity.zero)) && (this.hi.equals(Multiplicity.one));
  }
  
  public boolean isDet()
  {
    return (this.lo.equals(Multiplicity.one)) && (this.hi.equals(Multiplicity.one));
  }
  
  public boolean isNondet()
  {
    return (this.lo.equals(Multiplicity.zero)) && (this.hi.equals(Multiplicity.many));
  }
  
  public boolean isMulti()
  {
    return (this.lo.equals(Multiplicity.one)) && (this.hi.equals(Multiplicity.many));
  }
  
  public static Mode makeConvertTo()
  {
    return new Mode(Multiplicity.zero, Multiplicity.one, 1, 1);
  }
  
  public static Mode convertFromString(String modeString)
  {
    if (modeString.equals("DET")) {
      return makeDet();
    }
    if (modeString.equals("SEMIDET")) {
      return makeSemidet();
    }
    if (modeString.equals("NONDET")) {
      return makeNondet();
    }
    if (modeString.equals("MULTI")) {
      return makeMulti();
    }
    if (modeString.equals("FAIL")) {
      return makeFail();
    }
    if (modeString.equals("ERROR")) {
      return new ErrorMode("");
    }
    throw new Error("unknown mode " + modeString);
  }
  
  public Mode add(Mode other)
  {
    if (other == null) {
      return this;
    }
    if ((other instanceof ErrorMode)) {
      return other.add(this);
    }
    return new Mode(this.lo.max(other.lo), this.hi.add(other.hi), 
      this.numFree + other.numFree, this.numBound + other.numBound);
  }
  
  public Mode multiply(Mode other)
  {
    if ((other instanceof ErrorMode)) {
      return other;
    }
    return new Mode(this.lo.multiply(other.lo), this.hi.multiply(other.hi), 
      this.numFree + other.numFree, this.numBound + other.numBound);
  }
  
  public String toString()
  {
    return this.printString;
  }
  
  public double getPercentFree()
  {
    if (this.numFree == 0) {
      return 0.0D;
    }
    return this.numFree / (this.numFree + this.numBound);
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof Mode))
    {
      Mode om = (Mode)other;
      return (this.hi.equals(om.hi)) && (this.lo.equals(om.lo));
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.hi.hashCode() + 13 * this.lo.hashCode();
  }
  
  public int compareTo(Mode other)
  {
    int result = this.hi.compareTo(other.hi);
    if (result == 0)
    {
      result = this.lo.compareTo(other.lo);
      if (result == 0)
      {
        double thisPercentFree = getPercentFree();
        double otherPercentFree = other.getPercentFree();
        if (thisPercentFree < otherPercentFree) {
          return -1;
        }
        if (thisPercentFree > otherPercentFree) {
          return 1;
        }
        return 0;
      }
    }
    return result;
  }
  
  public boolean isBetterThan(Mode other)
  {
    return compareTo(other) < 0;
  }
  
  public boolean compatibleWith(Mode declared)
  {
    return declared.hi.compareTo(this.hi) >= 0;
  }
  
  public Mode first()
  {
    if ((this instanceof ErrorMode)) {
      return this;
    }
    return new Mode(this.lo.min(Multiplicity.one), this.hi.min(Multiplicity.one), 
      this.numFree, this.numBound);
  }
  
  public Mode negate()
  {
    if (isFail()) {
      return new Mode(Multiplicity.one, Multiplicity.one, this.numFree, this.numBound);
    }
    if ((this instanceof ErrorMode)) {
      return this;
    }
    if ((isDet()) || (isMulti())) {
      return new Mode(Multiplicity.zero, Multiplicity.zero, this.numFree, this.numBound);
    }
    return new Mode(Multiplicity.zero, Multiplicity.one, this.numFree, this.numBound);
  }
  
  public Mode unique()
  {
    if ((isDet()) || (isFail())) {
      return new Mode(this.lo, this.hi, this.numFree, this.numBound);
    }
    if ((this instanceof ErrorMode)) {
      return this;
    }
    return new Mode(Multiplicity.zero, Multiplicity.one, this.numFree, this.numBound);
  }
  
  public Mode restrictedBy(Mode upperBound)
  {
    if (this.hi.compareTo(upperBound.hi) > 0) {
      return new Mode(this.lo, upperBound.hi, this.numFree, this.numBound);
    }
    return this;
  }
  
  public Mode findAll()
  {
    return new Mode(Multiplicity.one, Multiplicity.one, this.numFree, this.numBound);
  }
  
  public Mode moreBound()
  {
    return new Mode(Multiplicity.zero, this.hi, this.numFree, this.numBound);
  }
  
  public void setPercentFree(BindingList bindings)
  {
    for (int i = 0; i < bindings.size(); i++) {
      if (bindings.get(i).isBound()) {
        this.numBound += 1;
      } else {
        this.numFree += 1;
      }
    }
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new Error("This should not happen");
    }
  }
  
  public Mode noOverlapWith(Mode other)
  {
    if (other == null) {
      return this;
    }
    return new Mode(this.lo.min(other.lo), this.hi.max(other.hi));
  }
}
