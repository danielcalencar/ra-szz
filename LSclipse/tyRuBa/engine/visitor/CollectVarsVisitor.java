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
package tyRuBa.engine.visitor;

import java.util.Collection;
import java.util.HashSet;
import tyRuBa.engine.RBCountAll;
import tyRuBa.engine.RBDisjunction;
import tyRuBa.engine.RBExistsQuantifier;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBFindAll;
import tyRuBa.engine.RBIgnoredVariable;
import tyRuBa.engine.RBNotFilter;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTestFilter;
import tyRuBa.engine.RBUniqueQuantifier;
import tyRuBa.engine.RBVariable;

public class CollectVarsVisitor
  extends AbstractCollectVarsVisitor
{
  public CollectVarsVisitor(Collection vars)
  {
    super(vars, null);
  }
  
  public CollectVarsVisitor()
  {
    super(new HashSet(), null);
  }
  
  public Object visit(RBDisjunction disjunction)
  {
    Collection oldVars = getVars();
    Collection intersection = null;
    for (int i = 0; i < disjunction.getNumSubexps(); i++)
    {
      Collection next = disjunction.getSubexp(i).getVariables();
      if (intersection == null) {
        intersection = next;
      } else {
        intersection.retainAll(next);
      }
    }
    if (intersection != null) {
      oldVars.addAll(intersection);
    }
    return null;
  }
  
  public Object visit(RBExistsQuantifier exists)
  {
    return exists.getExp().accept(this);
  }
  
  public Object visit(RBFindAll findAll)
  {
    return findAll.getResult().accept(this);
  }
  
  public Object visit(RBCountAll count)
  {
    return count.getResult().accept(this);
  }
  
  public Object visit(RBNotFilter notFilter)
  {
    return null;
  }
  
  public Object visit(RBTestFilter testFilter)
  {
    return null;
  }
  
  public Object visit(RBUniqueQuantifier unique)
  {
    return unique.getExp().accept(this);
  }
  
  public Object visit(RBVariable var)
  {
    getVars().add(var);
    return null;
  }
  
  public Object visit(RBIgnoredVariable ignoredVar)
  {
    getVars().add(ignoredVar);
    return null;
  }
  
  public Object visit(RBTemplateVar templVar)
  {
    return null;
  }
}
