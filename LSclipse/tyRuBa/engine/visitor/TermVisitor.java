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

import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBIgnoredVariable;
import tyRuBa.engine.RBPair;
import tyRuBa.engine.RBQuoted;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RBVariable;

public abstract interface TermVisitor
{
  public abstract Object visit(RBCompoundTerm paramRBCompoundTerm);
  
  public abstract Object visit(RBIgnoredVariable paramRBIgnoredVariable);
  
  public abstract Object visit(RBPair paramRBPair);
  
  public abstract Object visit(RBQuoted paramRBQuoted);
  
  public abstract Object visit(RBTuple paramRBTuple);
  
  public abstract Object visit(RBVariable paramRBVariable);
  
  public abstract Object visit(RBTemplateVar paramRBTemplateVar);
}
