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

import java.util.Enumeration;
import java.util.Hashtable;

public class Frame
  extends Hashtable
{
  public RBTerm get(RBSubstitutable v)
  {
    return (RBTerm)super.get(v);
  }
  
  public Object clone()
  {
    Frame cl = (Frame)super.clone();
    
    return cl;
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer("|");
    Enumeration keys = keys();
    if (!keys.hasMoreElements()) {
      result.append(" SUCCESS");
    } else {
      while (keys.hasMoreElements())
      {
        result.append(" ");
        RBSubstitutable key = (RBSubstitutable)keys.nextElement();
        result.append(key + "=" + get(key));
      }
    }
    result.append(" |");
    return result.toString();
  }
  
  public Frame callResult(Frame body)
  {
    Frame result = new Frame();
    Enumeration keys = keys();
    Frame instAux = new Frame();
    while (keys.hasMoreElements())
    {
      RBSubstitutable key = (RBSubstitutable)keys.nextElement();
      RBTerm value = get(key);
      result.put(key, value.substantiate(body, instAux));
    }
    return result;
  }
  
  public Frame append(Frame other)
  {
    Frame f = (Frame)clone();
    Enumeration others = other.keys();
    while (others.hasMoreElements())
    {
      Object key = others.nextElement();
      Object val = other.get(key);
      f.put(key, val);
    }
    return f;
  }
  
  public boolean equals(Object x)
  {
    if (x.getClass() != getClass()) {
      return false;
    }
    boolean equal = true;
    Frame other = (Frame)x;
    Frame l = new Frame();
    Frame r = new Frame();
    Enumeration keys = keys();
    while ((equal) && (keys.hasMoreElements()))
    {
      RBSubstitutable key = (RBSubstitutable)keys.nextElement();
      RBTerm value = get(key);
      equal = value.sameForm(other.get(key), l, r);
    }
    return equal;
  }
  
  public int hashCode()
  {
    int hash = 0;
    Enumeration keys = keys();
    while (keys.hasMoreElements())
    {
      RBSubstitutable key = (RBSubstitutable)keys.nextElement();
      RBTerm value = get(key);
      hash += key.hashCode() * value.formHashCode();
    }
    return hash;
  }
  
  public Frame removeVars(RBSubstitutable[] vars)
  {
    Frame result = (Frame)clone();
    for (int i = 0; i < vars.length; i++) {
      result.remove(vars[i]);
    }
    return result;
  }
}
