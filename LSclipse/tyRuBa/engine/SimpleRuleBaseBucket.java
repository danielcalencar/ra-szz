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

import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class SimpleRuleBaseBucket
  extends RuleBaseBucket
{
  StringBuffer mystuff = null;
  
  public SimpleRuleBaseBucket(FrontEnd frontEnd)
  {
    super(frontEnd, null);
  }
  
  public synchronized void addStuff(String toParse)
  {
    if (this.mystuff == null) {
      this.mystuff = new StringBuffer();
    }
    this.mystuff.append(toParse + "\n");
    setOutdated();
  }
  
  public synchronized void clearStuff()
  {
    this.mystuff = null;
    setOutdated();
  }
  
  public synchronized void update()
    throws ParseException, TypeModeError
  {
    if (this.mystuff != null) {
      parse(this.mystuff.toString());
    }
  }
}
