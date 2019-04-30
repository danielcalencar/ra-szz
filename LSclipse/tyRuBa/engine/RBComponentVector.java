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
import java.util.Iterator;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;

public class RBComponentVector
{
  public ArrayList contents;
  
  public RBComponentVector()
  {
    this.contents = new ArrayList();
  }
  
  public void clear()
  {
    this.contents = new ArrayList();
  }
  
  public RBComponentVector(int predictedSize)
  {
    this.contents = new ArrayList(predictedSize);
  }
  
  public RBComponentVector(ArrayList vect)
  {
    this.contents = vect;
  }
  
  public void insert(RBComponent c)
  {
    if (c == null) {
      throw new NullPointerException("Not allowed to insert null");
    }
    this.contents.add(c);
  }
  
  public String toString()
  {
    int len = this.contents.size();
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < len; i++) {
      result.append(this.contents.get(i) + "\n");
    }
    return result.toString();
  }
  
  public Compiled compile(CompilationContext context)
  {
    Compiled result = Compiled.fail;
    for (Iterator iter = iterator(); iter.hasNext();)
    {
      RBComponent element = (RBComponent)iter.next();
      result = result.disjoin(element.compile(context));
    }
    return result;
  }
  
  private Iterator iterator()
  {
    new Iterator()
    {
      int pos;
      
      public boolean hasNext()
      {
        return this.pos < RBComponentVector.this.contents.size();
      }
      
      private void skipInvalids()
      {
        while ((this.pos < RBComponentVector.this.contents.size()) && (!
          ((RBComponent)RBComponentVector.this.contents.get(this.pos)).isValid())) {
          this.pos += 1;
        }
      }
      
      public Object next()
      {
        Object result = RBComponentVector.this.contents.get(this.pos++);
        skipInvalids();
        return result;
      }
      
      public void remove()
      {
        throw new Error("This operation is not supported");
      }
    };
  }
}
