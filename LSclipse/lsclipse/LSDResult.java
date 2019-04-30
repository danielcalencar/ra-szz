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
package lsclipse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lsd.rule.LSDFact;
import lsd.rule.LSDVariable;

public class LSDResult
{
  public int num_matches;
  public int num_counter;
  public String desc;
  public List<LSDFact> examples;
  public List<Map<LSDVariable, String>> exceptions;
  private ArrayList<String> examplesString = null;
  private ArrayList<String> exceptionsString = null;
  
  public ArrayList<String> getExampleStr()
  {
    if (this.examplesString == null)
    {
      this.examplesString = new ArrayList();
      for (LSDFact fact : this.examples) {
        this.examplesString.add(fact.toString());
      }
    }
    return this.examplesString;
  }
  
  public ArrayList<String> getExceptionsString()
  {
    if (this.exceptionsString == null)
    {
      this.exceptionsString = new ArrayList();
      for (Map<LSDVariable, String> exception : this.exceptions)
      {
        StringBuilder s = new StringBuilder();
        s.append("[ ");
        for (Map.Entry<LSDVariable, String> entry : exception.entrySet())
        {
          s.append(entry.getKey());
          s.append("=\"");
          s.append((String)entry.getValue());
          s.append("\" ");
        }
        s.append("]");
        this.exceptionsString.add(s.toString());
      }
    }
    return this.exceptionsString;
  }
}
