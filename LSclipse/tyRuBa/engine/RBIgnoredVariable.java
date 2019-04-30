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

import java.io.ObjectStreamException;
import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;

public class RBIgnoredVariable
  extends RBVariable
{
  public static final RBIgnoredVariable the = new RBIgnoredVariable();
  
  private RBIgnoredVariable()
  {
    super("?");
  }
  
  public Frame unify(RBTerm other, Frame f)
  {
    return f;
  }
  
  boolean freefor(RBVariable v)
  {
    return true;
  }
  
  protected boolean sameForm(RBTerm other, Frame lr, Frame rl)
  {
    return this == other;
  }
  
  public int formHashCode()
  {
    return 1;
  }
  
  public boolean equals(Object obj)
  {
    return obj == this;
  }
  
  public int hashCode()
  {
    return 66727982;
  }
  
  public Object clone()
  {
    return this;
  }
  
  protected Type getType(TypeEnv env)
  {
    return Factory.makeTVar("");
  }
  
  public Object accept(TermVisitor v)
  {
    return v.visit(this);
  }
  
  public Object readResolve()
    throws ObjectStreamException
  {
    return the;
  }
}
