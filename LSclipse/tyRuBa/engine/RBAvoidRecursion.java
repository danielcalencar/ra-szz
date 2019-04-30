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

import java.io.PrintStream;

public class RBAvoidRecursion
  extends RBContext
{
  protected RBContext guarded;
  protected RBRule rule;
  private int depth;
  private static int maxDepth = 0;
  public static int depthLimit = 250;
  
  public RBAvoidRecursion(RBContext aContext, RBRule r)
  {
    this.rule = r;
    this.guarded = aContext;
    this.depth = (aContext.depth() + 1);
    if (this.depth > maxDepth)
    {
      maxDepth = this.depth;
      if (this.depth == depthLimit)
      {
        System.err.print(this);
        throw new Error("To deep recursion in rule application");
      }
    }
  }
  
  int depth()
  {
    return this.depth;
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer(this.rule + "\n");
    if ((this.guarded instanceof RBAvoidRecursion)) {
      result.append(this.guarded.toString());
    } else {
      result.append("--------------------");
    }
    return result.toString();
  }
}
