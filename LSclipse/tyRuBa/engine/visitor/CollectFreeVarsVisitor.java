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
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.ModeCheckContext;

public class CollectFreeVarsVisitor
  extends AbstractCollectVarsVisitor
{
  public CollectFreeVarsVisitor(ModeCheckContext context)
  {
    super(new HashSet(), context);
  }
  
  public Object visit(RBDisjunction disjunction)
  {
    for (int i = 0; i < disjunction.getNumSubexps(); i++) {
      disjunction.getSubexp(i).accept(this);
    }
    return null;
  }
  
  public Object visit(RBExistsQuantifier exists)
  {
    exists.getExp().accept(this);
    for (int i = 0; i < exists.getNumVars(); i++) {
      this.vars.remove(exists.getVarAt(i));
    }
    return null;
  }
  
  public Object visit(RBFindAll findAll)
  {
    findAll.getQuery().accept(this);
    findAll.getResult().accept(this);
    return null;
  }
  
  public Object visit(RBCountAll count)
  {
    count.getQuery().accept(this);
    count.getResult().accept(this);
    return null;
  }
  
  public Object visit(RBNotFilter notFilter)
  {
    return notFilter.getNegatedQuery().accept(this);
  }
  
  public Object visit(RBTestFilter testFilter)
  {
    return testFilter.getQuery().accept(this);
  }
  
  public Object visit(RBUniqueQuantifier unique)
  {
    unique.getExp().accept(this);
    for (int i = 0; i < unique.getNumVars(); i++) {
      this.vars.remove(unique.getVarAt(i));
    }
    return null;
  }
  
  public Object visit(RBVariable var)
  {
    if (!var.getBindingMode(this.context).isBound()) {
      this.vars.add(var);
    }
    return null;
  }
  
  public Object visit(RBIgnoredVariable ignoredVar)
  {
    return null;
  }
  
  public Object visit(RBTemplateVar ignoredVar)
  {
    return null;
  }
}
