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

import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBQuoted
  extends RBAbstractPair
{
  private static final RBTerm quotedName = FrontEnd.makeName("{}");
  
  public RBQuoted(RBTerm listOfParts)
  {
    super(quotedName, listOfParts);
  }
  
  public Object up()
  {
    return quotedToString();
  }
  
  public String toString()
  {
    return "{" + getQuotedParts().quotedToString() + "}";
  }
  
  public String quotedToString()
  {
    return getQuotedParts().quotedToString();
  }
  
  public RBTerm getQuotedParts()
  {
    return getCdr();
  }
  
  protected Type getType(TypeEnv env)
    throws TypeModeError
  {
    return Factory.makeSubAtomicType(Factory.makeTypeConstructor(String.class));
  }
  
  public Object accept(TermVisitor v)
  {
    return v.visit(this);
  }
  
  public String getFirst()
  {
    return getCdr().getFirst();
  }
  
  public Object getSecond()
  {
    return getCdr().getSecond();
  }
}
