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
package tyRuBa.engine;

import java.util.NoSuchElementException;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.ModeCheckContext;

public abstract class RBAbstractPair
  extends RBTerm
{
  private RBTerm car;
  private RBTerm cdr;
  
  public RBAbstractPair(RBTerm aCar, RBTerm aCdr)
  {
    this.car = aCar;
    this.cdr = aCdr;
  }
  
  public RBTerm getCar()
  {
    return this.car;
  }
  
  public RBTerm getCdr()
  {
    return this.cdr;
  }
  
  public void setCdr(RBTerm aCdr)
  {
    if (this.cdr == null)
    {
      if (aCdr != null) {
        this.cdr = aCdr;
      } else {
        throw new IllegalArgumentException("Cannot set cdr to null");
      }
    }
    else {
      throw new IllegalStateException("Cannot set cdr more than once");
    }
  }
  
  public RBTerm getCddr()
  {
    return ((RBAbstractPair)this.cdr).getCdr();
  }
  
  public int getNumSubterms()
    throws ImproperListException
  {
    RBTerm cdr = getCdr();
    if ((cdr instanceof RBAbstractPair)) {
      return ((RBAbstractPair)cdr).getNumSubterms() + 1;
    }
    if (cdr.equals(FrontEnd.theEmptyList)) {
      return 1;
    }
    throw new ImproperListException();
  }
  
  public RBTerm getSubterm(int i)
  {
    if (i == 0) {
      return getCar();
    }
    try
    {
      return ((RBAbstractPair)getCdr()).getSubterm(i - 1);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new NoSuchElementException();
    }
  }
  
  public RBTerm[] getSubterms()
    throws ImproperListException
  {
    int sz = getNumSubterms();
    RBTerm[] result = new RBTerm[sz];
    RBTerm current = this;
    for (int i = 0; (current instanceof RBAbstractPair); i++)
    {
      result[i] = ((RBAbstractPair)current).car;
      current = ((RBAbstractPair)current).cdr;
    }
    return result;
  }
  
  protected final String cdrToString(boolean begin, RBTerm cdr)
  {
    String result = "";
    if (cdr.getClass() == getClass())
    {
      RBAbstractPair pcdr = (RBAbstractPair)cdr;
      if (!begin) {
        result = result + ",";
      }
      result = result + pcdr.getCar() + cdrToString(false, pcdr.getCdr());
    }
    else if (!cdr.equals(FrontEnd.theEmptyList))
    {
      result = result + "|" + cdr;
    }
    return result;
  }
  
  public Frame unify(RBTerm other, Frame f)
  {
    if (other.getClass() == getClass())
    {
      RBAbstractPair cother = (RBAbstractPair)other;
      f = getCar().unify(cother.getCar(), f);
      if (f != null) {
        f = getCdr().unify(cother.getCdr(), f);
      }
      return f;
    }
    if ((other instanceof RBVariable)) {
      return other.unify(this, f);
    }
    return null;
  }
  
  protected boolean sameForm(RBTerm other, Frame lr, Frame rl)
  {
    if (other.getClass() == getClass())
    {
      RBAbstractPair cother = (RBAbstractPair)other;
      
      return (getCar().sameForm(cother.getCar(), lr, rl)) && (getCdr().sameForm(cother.getCdr(), lr, rl));
    }
    return false;
  }
  
  boolean freefor(RBVariable v)
  {
    return (this.car.freefor(v)) && (this.cdr.freefor(v));
  }
  
  public boolean equals(Object x)
  {
    if (x == null) {
      return false;
    }
    if (x.getClass() != getClass()) {
      return false;
    }
    RBAbstractPair cx = (RBAbstractPair)x;
    return (getCar().equals(cx.getCar())) && (getCdr().equals(cx.getCdr()));
  }
  
  public int hashCode()
  {
    return this.car.hashCode() + 11 * this.cdr.hashCode();
  }
  
  public int formHashCode()
  {
    return this.car.formHashCode() + 11 * this.cdr.formHashCode();
  }
  
  public void makeAllBound(ModeCheckContext context)
  {
    getCar().makeAllBound(context);
    getCdr().makeAllBound(context);
  }
  
  public BindingMode getBindingMode(ModeCheckContext context)
  {
    BindingMode carMode = getCar().getBindingMode(context);
    BindingMode cdrMode = getCdr().getBindingMode(context);
    if ((carMode.isBound()) && (cdrMode.isBound())) {
      return carMode;
    }
    return Factory.makePartiallyBound();
  }
  
  public boolean isGround()
  {
    return (getCar().isGround()) && (getCdr().isGround());
  }
}
