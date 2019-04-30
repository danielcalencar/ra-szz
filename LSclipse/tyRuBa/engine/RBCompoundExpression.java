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
import java.util.Vector;
import tyRuBa.util.ElementSource;

public abstract class RBCompoundExpression
  extends RBExpression
{
  private ArrayList subexps;
  
  public RBCompoundExpression(Vector exps)
  {
    this.subexps = new ArrayList();
    for (int i = 0; i < exps.size(); i++) {
      addSubexp((RBExpression)exps.elementAt(i));
    }
  }
  
  public RBCompoundExpression(Object[] exps)
  {
    this.subexps = new ArrayList();
    for (int i = 0; i < exps.length; i++) {
      addSubexp((RBExpression)exps[i]);
    }
  }
  
  public RBCompoundExpression()
  {
    this.subexps = new ArrayList();
  }
  
  public RBCompoundExpression(RBExpression e1, RBExpression e2)
  {
    this();
    addSubexp(e1);
    addSubexp(e2);
  }
  
  public int getNumSubexps()
  {
    return this.subexps.size();
  }
  
  public RBExpression getSubexp(int i)
  {
    return (RBExpression)this.subexps.get(i);
  }
  
  public ElementSource getSubexps()
  {
    return ElementSource.with(this.subexps);
  }
  
  public ArrayList getSubexpsArrayList()
  {
    return (ArrayList)this.subexps.clone();
  }
  
  public void addSubexp(RBExpression e)
  {
    if (getClass().isInstance(e))
    {
      RBCompoundExpression ce = (RBCompoundExpression)e;
      for (int i = 0; i < ce.getNumSubexps(); i++) {
        addSubexp(ce.getSubexp(i));
      }
    }
    else
    {
      this.subexps.add(e);
    }
  }
  
  public Object clone()
  {
    try
    {
      cl = (RBCompoundExpression)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      RBCompoundExpression cl;
      throw new Error("This should not happen");
    }
    RBCompoundExpression cl;
    cl.subexps = ((ArrayList)this.subexps.clone());
    return cl;
  }
  
  public String toString()
  {
    return toString(false);
  }
  
  public String toString(boolean brackets)
  {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < getNumSubexps(); i++)
    {
      RBExpression currExp = getSubexp(i);
      if (i > 0) {
        result.append(separator() + " ");
      }
      if ((currExp instanceof RBCompoundExpression)) {
        result.append(((RBCompoundExpression)currExp).toString(true));
      } else {
        result.append(getSubexp(i).toString());
      }
    }
    if (brackets) {
      return "(" + result.toString() + ")";
    }
    return result.toString();
  }
  
  protected abstract String separator();
}
