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
package tyRuBa.engine.compilation;

import tyRuBa.engine.Frame;
import tyRuBa.engine.FrontEnd;
import tyRuBa.engine.RBContext;
import tyRuBa.engine.RBTerm;
import tyRuBa.modes.Mode;
import tyRuBa.util.Action;
import tyRuBa.util.ElementSource;

public class CompiledFindAll
  extends SemiDetCompiled
{
  private final Compiled query;
  private final RBTerm extract;
  private final RBTerm result;
  
  public CompiledFindAll(Compiled query, RBTerm extract, RBTerm result)
  {
    super(Mode.makeDet());
    this.query = query;
    this.extract = extract;
    this.result = result;
  }
  
  public Frame runSemiDet(Object input, RBContext context)
  {
    ElementSource res = this.query.runNonDet(((Frame)input).clone(), context);
    res = res.map(new Action()
    {
      public Object compute(Object arg)
      {
        return CompiledFindAll.this.extract.substitute((Frame)arg);
      }
    });
    RBTerm resultList = FrontEnd.makeList(res);
    return this.result.unify(resultList, (Frame)input);
  }
  
  public String toString()
  {
    return "COMPILED FINDALL(" + this.query + "," + this.extract + "," + this.result + ")";
  }
}
