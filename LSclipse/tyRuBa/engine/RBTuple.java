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

import java.util.ArrayList;
import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.util.ObjectTuple;
import tyRuBa.util.TwoLevelKey;

public class RBTuple
  extends RBTerm
  implements TwoLevelKey
{
  RBTerm[] subterms;
  
  public RBTuple(RBTerm t1)
  {
    this.subterms = new RBTerm[1];
    this.subterms[0] = t1;
  }
  
  public RBTuple(RBTerm t1, RBTerm t2)
  {
    this.subterms = new RBTerm[] { t1, t2 };
  }
  
  public RBTuple(RBTerm t1, RBTerm t2, RBTerm t3)
  {
    this.subterms = new RBTerm[] { t1, t2, t3 };
  }
  
  public RBTuple(RBTerm t1, RBTerm t2, RBTerm t3, RBTerm t4)
  {
    this.subterms = new RBTerm[] { t1, t2, t3, t4 };
  }
  
  public RBTuple(RBTerm t1, RBTerm t2, RBTerm t3, RBTerm t4, RBTerm t5)
  {
    this.subterms = new RBTerm[] { t1, t2, t3, t4, t5 };
  }
  
  public static RBTuple theEmpty = new RBTuple(new RBTerm[0]);
  
  public static RBTuple make(ArrayList terms)
  {
    return make((RBTerm[])terms.toArray(new RBTerm[terms.size()]));
  }
  
  public static RBTuple make(RBTerm[] terms)
  {
    if (terms.length == 0) {
      return theEmpty;
    }
    return new RBTuple(terms);
  }
  
  private RBTuple(RBTerm[] terms)
  {
    this.subterms = ((RBTerm[])terms.clone());
  }
  
  public static RBTuple makeSingleton(RBTerm term)
  {
    return new RBTuple(new RBTerm[] { term });
  }
  
  public int getNumSubterms()
  {
    return this.subterms.length;
  }
  
  public RBTerm getSubterm(int i)
  {
    return this.subterms[i];
  }
  
  public boolean equals(Object x)
  {
    if (x.getClass() != getClass()) {
      return false;
    }
    RBTuple cx = (RBTuple)x;
    if (cx.subterms.length != this.subterms.length) {
      return false;
    }
    for (int i = 0; i < this.subterms.length; i++) {
      if (!this.subterms[i].equals(cx.subterms[i])) {
        return false;
      }
    }
    return true;
  }
  
  public int formHashCode()
  {
    int hash = this.subterms.length;
    for (int i = 0; i < this.subterms.length; i++) {
      hash = hash * 19 + this.subterms[i].formHashCode();
    }
    return hash;
  }
  
  public int hashCode()
  {
    int hash = this.subterms.length;
    for (int i = 0; i < this.subterms.length; i++) {
      hash = hash * 19 + this.subterms[i].hashCode();
    }
    return hash;
  }
  
  boolean freefor(RBVariable v)
  {
    for (int i = 0; i < this.subterms.length; i++) {
      if (!this.subterms[i].freefor(v)) {
        return false;
      }
    }
    return true;
  }
  
  public BindingMode getBindingMode(ModeCheckContext context)
  {
    for (int i = 0; i < getNumSubterms(); i++) {
      if (!getSubterm(i).getBindingMode(context).isBound()) {
        return Factory.makePartiallyBound();
      }
    }
    return Factory.makeBound();
  }
  
  public boolean isGround()
  {
    for (int i = 0; i < getNumSubterms(); i++) {
      if (!getSubterm(i).isGround()) {
        return false;
      }
    }
    return true;
  }
  
  public boolean sameForm(RBTerm other, Frame lr, Frame rl)
  {
    if (other.getClass() != getClass()) {
      return false;
    }
    RBTuple cother = (RBTuple)other;
    if (getNumSubterms() != cother.getNumSubterms()) {
      return false;
    }
    for (int i = 0; i < this.subterms.length; i++) {
      if (!this.subterms[i].sameForm(cother.subterms[i], lr, rl)) {
        return false;
      }
    }
    return true;
  }
  
  public Frame unify(RBTerm other, Frame f)
  {
    if (!(other instanceof RBTuple))
    {
      if ((other instanceof RBVariable)) {
        return other.unify(this, f);
      }
      return null;
    }
    RBTuple cother = (RBTuple)other;
    int sz = getNumSubterms();
    if (sz != cother.getNumSubterms()) {
      return null;
    }
    for (int i = 0; i < sz; i++)
    {
      f = this.subterms[i].unify(cother.subterms[i], f);
      if (f == null) {
        return null;
      }
    }
    return f;
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer("<");
    for (int i = 0; i < this.subterms.length; i++)
    {
      if (i > 0) {
        result.append(",");
      }
      result.append(this.subterms[i].toString());
    }
    result.append(">");
    return result.toString();
  }
  
  protected Type getType(TypeEnv env)
    throws TypeModeError
  {
    TupleType tlst = Factory.makeTupleType();
    for (int i = 0; i < this.subterms.length; i++) {
      tlst.add(this.subterms[i].getType(env));
    }
    return tlst;
  }
  
  public void makeAllBound(ModeCheckContext context)
  {
    for (int i = 0; i < getNumSubterms(); i++) {
      getSubterm(i).makeAllBound(context);
    }
  }
  
  public Object accept(TermVisitor v)
  {
    return v.visit(this);
  }
  
  public RBTuple append(RBTuple other)
  {
    RBTerm[] parts = new RBTerm[getNumSubterms() + other.getNumSubterms()];
    for (int i = 0; i < getNumSubterms(); i++) {
      parts[i] = getSubterm(i);
    }
    for (int i = 0; i < other.getNumSubterms(); i++) {
      parts[(i + getNumSubterms())] = getSubterm(i);
    }
    return FrontEnd.makeTuple(parts);
  }
  
  public String getFirst()
  {
    if (this.subterms.length > 0) {
      return this.subterms[0].getFirst();
    }
    return "";
  }
  
  public Object getSecond()
  {
    if (this.subterms.length > 1)
    {
      Object second = this.subterms[0].getSecond();
      Object[] objs = new Object[this.subterms.length];
      objs[0] = second;
      for (int i = 1; i < this.subterms.length; i++) {
        if ((this.subterms[i] instanceof RBRepAsJavaObjectCompoundTerm)) {
          objs[i] = ((RBRepAsJavaObjectCompoundTerm)this.subterms[i]).getValue();
        } else if ((this.subterms[i] instanceof RBJavaObjectCompoundTerm)) {
          objs[i] = ((RBJavaObjectCompoundTerm)this.subterms[i]).getObject();
        } else {
          objs[i] = this.subterms[i];
        }
      }
      return ObjectTuple.make(objs);
    }
    if (this.subterms.length > 0) {
      return this.subterms[0].getSecond();
    }
    return ObjectTuple.theEmpty;
  }
  
  public Object up()
  {
    Object[] objs = new Object[this.subterms.length];
    for (int i = 0; i < objs.length; i++) {
      objs[i] = this.subterms[i].up();
    }
    return objs;
  }
}
