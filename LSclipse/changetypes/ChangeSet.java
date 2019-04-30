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
package changetypes;

import java.io.PrintStream;
import java.util.HashSet;

public class ChangeSet
  extends HashSet<AtomicChange>
{
  private static final long serialVersionUID = 1L;
  public final int[] changecount = new int[AtomicChange.ChangeTypes.values().length];
  
  public void print(PrintStream out)
  {
    if (size() > 0)
    {
      out.println("~~~Changes~~~");
      for (AtomicChange ac : this) {
        out.println(ac.toString());
      }
    }
    else
    {
      out.println("No changes");
    }
  }
  
  public void normalize() {}
}
