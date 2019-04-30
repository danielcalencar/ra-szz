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

import java.io.IOException;
import java.io.ObjectInputStream;
import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.Factory;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.RepAsJavaConstructorType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.util.TwoLevelKey;

public class RBRepAsJavaObjectCompoundTerm
  extends RBCompoundTerm
{
  Object javaObject;
  RepAsJavaConstructorType typeTag;
  
  public RBRepAsJavaObjectCompoundTerm(RepAsJavaConstructorType type, Object obj)
  {
    this.typeTag = type;
    this.javaObject = obj;
  }
  
  public RBTerm getArg()
  {
    return RBCompoundTerm.makeJava(this.javaObject);
  }
  
  public RBTerm getArg(int i)
  {
    if (i == 0) {
      return this;
    }
    throw new Error("Argument not found " + i);
  }
  
  public int getNumArgs()
  {
    return 1;
  }
  
  boolean freefor(RBVariable v)
  {
    return true;
  }
  
  public boolean isGround()
  {
    return true;
  }
  
  protected boolean sameForm(RBTerm other, Frame lr, Frame rl)
  {
    return equals(other);
  }
  
  protected Type getType(TypeEnv env)
    throws TypeModeError
  {
    return this.typeTag.apply(Factory.makeSubAtomicType(this.typeTag.getTypeConst()));
  }
  
  public int formHashCode()
  {
    return 17 * this.typeTag.hashCode() + this.javaObject.hashCode();
  }
  
  public int hashCode()
  {
    return 17 * this.typeTag.hashCode() + this.javaObject.hashCode();
  }
  
  public BindingMode getBindingMode(ModeCheckContext context)
  {
    return Factory.makeBound();
  }
  
  public void makeAllBound(ModeCheckContext context) {}
  
  public boolean equals(Object x)
  {
    if (x.getClass().equals(getClass()))
    {
      RBRepAsJavaObjectCompoundTerm cx = (RBRepAsJavaObjectCompoundTerm)x;
      return (this.javaObject.equals(cx.javaObject)) && (this.typeTag.equals(cx.typeTag));
    }
    return false;
  }
  
  public Object accept(TermVisitor v)
  {
    return this;
  }
  
  public boolean isOfType(TypeConstructor t)
  {
    return t.isSuperTypeOf(getTypeConstructor());
  }
  
  public Frame unify(RBTerm other, Frame f)
  {
    if (((other instanceof RBVariable)) || ((other instanceof RBGenericCompoundTerm))) {
      return other.unify(this, f);
    }
    if (equals(other)) {
      return f;
    }
    return null;
  }
  
  public ConstructorType getConstructorType()
  {
    return this.typeTag;
  }
  
  public String getFirst()
  {
    if ((this.javaObject instanceof String))
    {
      String str = (String)this.javaObject;
      int firstindexofhash = str.indexOf('#');
      if (firstindexofhash == -1) {
        return " ";
      }
      return str.substring(0, firstindexofhash).intern();
    }
    if ((this.javaObject instanceof Number)) {
      return ((Number)this.javaObject).toString();
    }
    if ((this.javaObject instanceof TwoLevelKey)) {
      return ((TwoLevelKey)this.javaObject).getFirst();
    }
    throw new Error("This object does not support TwoLevelKey indexing: " + this.javaObject);
  }
  
  public Object getSecond()
  {
    if ((this.javaObject instanceof String))
    {
      String str = (String)this.javaObject;
      int firstindexofhash = str.indexOf('#');
      if (firstindexofhash == -1) {
        return this.typeTag.getFunctorId().toString() + str;
      }
      return this.typeTag.getFunctorId().toString() + str.substring(firstindexofhash).intern();
    }
    if ((this.javaObject instanceof Number)) {
      return ((Number)this.javaObject).toString();
    }
    if ((this.javaObject instanceof TwoLevelKey)) {
      return ((TwoLevelKey)this.javaObject).getSecond();
    }
    throw new Error("This object does not support TwoLevelKey indexing: " + this.javaObject);
  }
  
  public String toString()
  {
    if ((this.javaObject instanceof String))
    {
      String javaString = (String)this.javaObject;
      return "\"" + javaString + "\"" + 
        "::" + this.typeTag.getFunctorId().getName();
    }
    return this.javaObject.toString() + "::" + this.typeTag.getFunctorId().getName();
  }
  
  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    if ((this.javaObject instanceof String)) {
      this.javaObject = ((String)this.javaObject).intern();
    }
  }
  
  public int intValue()
  {
    if ((this.javaObject instanceof Integer)) {
      return ((Integer)this.javaObject).intValue();
    }
    return super.intValue();
  }
  
  public Object getValue()
  {
    return this.javaObject;
  }
}
