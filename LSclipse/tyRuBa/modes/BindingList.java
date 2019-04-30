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

import java.util.ArrayList;

public class BindingList
{
  private ArrayList parts;
  
  public BindingList()
  {
    this.parts = new ArrayList();
  }
  
  public BindingList(BindingMode bm)
  {
    this();
    this.parts.add(bm);
  }
  
  public int hashCode()
  {
    int size = size();
    int hash = getClass().hashCode();
    for (int i = 0; i < size; i++) {
      hash = hash * 19 + get(i).hashCode();
    }
    return hash;
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof BindingList))
    {
      BindingList cother = (BindingList)other;
      if (size() != cother.size()) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (!get(i).equals(cother.get(i))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer("(");
    int size = size();
    for (int i = 0; i < size; i++)
    {
      if (i > 0) {
        result.append(",");
      }
      result.append(get(i).toString());
    }
    result.append(")");
    return result.toString();
  }
  
  public String getBFString()
  {
    StringBuffer result = new StringBuffer();
    int size = size();
    for (int i = 0; i < size; i++) {
      if (get(i).isBound()) {
        result.append("B");
      } else {
        result.append("F");
      }
    }
    return result.toString();
  }
  
  public void add(BindingMode newPart)
  {
    this.parts.add(newPart);
  }
  
  public BindingMode get(int i)
  {
    return (BindingMode)this.parts.get(i);
  }
  
  public int size()
  {
    return this.parts.size();
  }
  
  public boolean satisfyBinding(BindingList other)
  {
    for (int i = 0; i < size(); i++) {
      if (!get(i).satisfyBinding(other.get(i))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean hasFree()
  {
    for (int i = 0; i < size(); i++) {
      if (!get(i).isBound()) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isAllBound()
  {
    for (int i = 0; i < size(); i++) {
      if (!get(i).isBound()) {
        return false;
      }
    }
    return true;
  }
  
  public int getNumBound()
  {
    int result = 0;
    for (int i = 0; i < size(); i++) {
      if (get(i).isBound()) {
        result++;
      }
    }
    return result;
  }
  
  public int getNumFree()
  {
    return size() - getNumBound();
  }
}
