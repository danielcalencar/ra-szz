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
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.Factory;
import tyRuBa.modes.JavaConstructorType;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.util.TwoLevelKey;

public class RBJavaObjectCompoundTerm
  extends RBCompoundTerm
{
  public static RBTerm javaClass(String name)
  {
    try
    {
      return new RBJavaObjectCompoundTerm(Class.forName(name));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new Error("Class not found:" + name);
    }
  }
  
  public static RBTerm regexp(final String re)
  {
    try
    {
      new RBJavaObjectCompoundTerm(new RE(re)
      {
        private static final long serialVersionUID = 1L;
        
        public String toString()
        {
          return "/" + re + "/";
        }
      });
    }
    catch (RESyntaxException localRESyntaxException)
    {
      throw new Error("Regular expression syntax error");
    }
  }
  
  public static final RBTerm theEmptyList = new RBJavaObjectCompoundTerm("[]")
  {
    public String quotedToString()
    {
      return "";
    }
    
    protected Type getType(TypeEnv env)
    {
      return Factory.makeEmptyListType();
    }
    
    public boolean equals(Object obj)
    {
      return (obj != null) && (obj.getClass() == getClass());
    }
    
    public int hashCode()
    {
      return getClass().hashCode();
    }
    
    public Object up()
    {
      return new Object[0];
    }
  };
  private Object arg;
  
  public RBJavaObjectCompoundTerm(Object arg)
  {
    if ((arg instanceof String)) {
      this.arg = ((String)arg).intern();
    } else {
      this.arg = arg;
    }
  }
  
  public ConstructorType getConstructorType()
  {
    return ConstructorType.makeJava(this.arg.getClass());
  }
  
  public RBTerm getArg()
  {
    return this;
  }
  
  public RBTerm getArg(int i)
  {
    if (i == 0) {
      return this;
    }
    throw new Error("RBJavaObjectCompoundTerms only have one argument");
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
    return ((JavaConstructorType)getConstructorType()).getType();
  }
  
  public int formHashCode()
  {
    return 9595 + this.arg.hashCode();
  }
  
  public int hashCode()
  {
    return 9595 + this.arg.hashCode();
  }
  
  public BindingMode getBindingMode(ModeCheckContext context)
  {
    return Factory.makeBound();
  }
  
  public void makeAllBound(ModeCheckContext context) {}
  
  public boolean equals(Object x)
  {
    if (x.getClass().equals(getClass())) {
      return this.arg.equals(((RBJavaObjectCompoundTerm)x).arg);
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
  
  public Object up()
  {
    return this.arg;
  }
  
  public Frame unify(RBTerm other, Frame f)
  {
    if ((other instanceof RBVariable)) {
      return other.unify(this, f);
    }
    if (equals(other)) {
      return f;
    }
    return null;
  }
  
  public String getFirst()
  {
    if ((this.arg instanceof String))
    {
      String str = (String)this.arg;
      int firstindexofhash = str.indexOf('#');
      if (firstindexofhash == -1) {
        return " ";
      }
      return str.substring(0, firstindexofhash).intern();
    }
    if ((this.arg instanceof Number)) {
      return ((Number)this.arg).toString();
    }
    if ((this.arg instanceof TwoLevelKey)) {
      return ((TwoLevelKey)this.arg).getFirst();
    }
    throw new Error("This object does not support TwoLevelKey indexing: " + this.arg);
  }
  
  public Object getSecond()
  {
    if ((this.arg instanceof String))
    {
      String str = (String)this.arg;
      int firstindexofhash = str.indexOf('#');
      if (firstindexofhash == -1) {
        return str;
      }
      return str.substring(firstindexofhash).intern();
    }
    if ((this.arg instanceof Number)) {
      return ((Number)this.arg).toString();
    }
    if ((this.arg instanceof TwoLevelKey)) {
      return ((TwoLevelKey)this.arg).getSecond();
    }
    throw new Error("This object does not support TwoLevelKey indexing: " + this.arg);
  }
  
  public String toString()
  {
    return this.arg.toString();
  }
  
  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    if ((this.arg instanceof String)) {
      this.arg = ((String)this.arg).intern();
    }
  }
  
  public int intValue()
  {
    if ((this.arg instanceof Integer)) {
      return ((Integer)this.arg).intValue();
    }
    return super.intValue();
  }
  
  public Object getObject()
  {
    return this.arg;
  }
}
