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

import tyRuBa.engine.RBConjunction;
import tyRuBa.engine.RBCountAll;
import tyRuBa.engine.RBDisjunction;
import tyRuBa.engine.RBExistsQuantifier;
import tyRuBa.engine.RBFindAll;
import tyRuBa.engine.RBModeSwitchExpression;
import tyRuBa.engine.RBNotFilter;
import tyRuBa.engine.RBPredicateExpression;
import tyRuBa.engine.RBTestFilter;
import tyRuBa.engine.RBUniqueQuantifier;

public abstract interface ExpressionVisitor
{
  public abstract Object visit(RBConjunction paramRBConjunction);
  
  public abstract Object visit(RBDisjunction paramRBDisjunction);
  
  public abstract Object visit(RBExistsQuantifier paramRBExistsQuantifier);
  
  public abstract Object visit(RBFindAll paramRBFindAll);
  
  public abstract Object visit(RBCountAll paramRBCountAll);
  
  public abstract Object visit(RBModeSwitchExpression paramRBModeSwitchExpression);
  
  public abstract Object visit(RBNotFilter paramRBNotFilter);
  
  public abstract Object visit(RBPredicateExpression paramRBPredicateExpression);
  
  public abstract Object visit(RBTestFilter paramRBTestFilter);
  
  public abstract Object visit(RBUniqueQuantifier paramRBUniqueQuantifier);
}
