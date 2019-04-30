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

import java.util.Map;

public class GrowableType
  extends Type
{
  private BoundaryType lowerBound;
  private BoundaryType upperBound;
  
  public GrowableType(BoundaryType lowerBound)
  {
    this.lowerBound = lowerBound;
    this.upperBound = lowerBound;
  }
  
  private GrowableType(BoundaryType lowerBound, BoundaryType upperBound)
  {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }
  
  public int hashCode()
  {
    return this.lowerBound.hashCode() + 13 * this.upperBound.hashCode();
  }
  
  public boolean equals(Object other)
  {
    if (!(other instanceof GrowableType)) {
      return false;
    }
    GrowableType sother = (GrowableType)other;
    
    return (this.lowerBound.equals(sother.lowerBound)) && (this.upperBound.equals(sother.upperBound));
  }
  
  public String toString()
  {
    return this.upperBound.toString();
  }
  
  public void checkEqualTypes(Type other, boolean grow)
    throws TypeModeError
  {
    if ((other instanceof TVar))
    {
      other.checkEqualTypes(this, grow);
    }
    else
    {
      if (equals(other)) {
        return;
      }
      if ((other instanceof GrowableType))
      {
        GrowableType sother = (GrowableType)other;
        this.lowerBound = ((BoundaryType)this.lowerBound.union(sother.lowerBound));
        this.upperBound = this.lowerBound;
        sother.lowerBound = this.lowerBound;
        sother.upperBound = this.lowerBound;
      }
      else
      {
        check(other instanceof BoundaryType, this, other);
        BoundaryType b_other = (BoundaryType)other;
        BoundaryType new_lowerBound = (BoundaryType)this.lowerBound.union(b_other);
        if (grow)
        {
          this.lowerBound = new_lowerBound;
          this.upperBound = this.lowerBound;
        }
      }
    }
  }
  
  public boolean isSubTypeOf(Type other, Map renamings)
  {
    return this.lowerBound.isSubTypeOf(other, renamings);
  }
  
  public Type intersect(Type other)
    throws TypeModeError
  {
    if ((other instanceof GrowableType))
    {
      GrowableType sother = (GrowableType)other;
      BoundaryType max = 
        (BoundaryType)this.upperBound.union(sother.upperBound);
      BoundaryType min = 
        (BoundaryType)this.lowerBound.intersect(sother.lowerBound);
      if (max.equals(min)) {
        return min;
      }
      return new GrowableType(min, max);
    }
    if ((other instanceof BoundaryType))
    {
      BoundaryType cother = (BoundaryType)other;
      BoundaryType result = (BoundaryType)this.lowerBound.intersect(other);
      if (cother.isStrict())
      {
        check(cother.isSuperTypeOf(this.upperBound), this, other);
        return result;
      }
      if (!result.isSuperTypeOf(this.upperBound)) {
        return new GrowableType(result, this.upperBound);
      }
      return result;
    }
    return this.lowerBound.intersect(other);
  }
  
  public boolean isFreeFor(TVar var)
  {
    return this.upperBound.isFreeFor(var);
  }
  
  public Type clone(Map tfact)
  {
    return new GrowableType((BoundaryType)this.lowerBound.clone(tfact), 
      (BoundaryType)this.upperBound.clone(tfact));
  }
  
  public Type union(Type other)
    throws TypeModeError
  {
    if ((other instanceof TVar)) {
      return other.union(this);
    }
    if ((other instanceof BoundaryType))
    {
      BoundaryType b_other = (BoundaryType)other;
      return new GrowableType((BoundaryType)this.lowerBound.union(b_other), 
        (BoundaryType)this.upperBound.union(b_other));
    }
    check(other instanceof GrowableType, this, other);
    BoundaryType otherLower = ((GrowableType)other).lowerBound;
    BoundaryType otherUpper = ((GrowableType)other).upperBound;
    return new GrowableType(
      (BoundaryType)this.lowerBound.union(otherLower), 
      (BoundaryType)this.upperBound.union(otherUpper));
  }
  
  public Type copyStrictPart()
  {
    throw new Error("This should not be called!");
  }
  
  public boolean hasOverlapWith(Type other)
  {
    return this.lowerBound.hasOverlapWith(other);
  }
  
  public Type getParamType(String currName, Type repAs)
  {
    if ((repAs instanceof TVar))
    {
      if (currName.equals(((TVar)repAs).getName())) {
        return this;
      }
      return null;
    }
    return this.lowerBound.getParamType(currName, repAs);
  }
}
