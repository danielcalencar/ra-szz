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

import tyRuBa.engine.Frame;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBIgnoredVariable;
import tyRuBa.engine.RBPair;
import tyRuBa.engine.RBQuoted;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RBVariable;
import tyRuBa.modes.ConstructorType;

public class SubstantiateVisitor
  implements TermVisitor
{
  Frame subst;
  Frame inst;
  
  public SubstantiateVisitor(Frame subst, Frame inst)
  {
    this.subst = subst;
    this.inst = inst;
  }
  
  public Object visit(RBCompoundTerm compoundTerm)
  {
    ConstructorType typeConst = compoundTerm.getConstructorType();
    return typeConst.apply(
      (RBTerm)compoundTerm.getArg().accept(this));
  }
  
  public Object visit(RBTuple tuple)
  {
    RBTerm[] subterms = new RBTerm[tuple.getNumSubterms()];
    for (int i = 0; i < subterms.length; i++) {
      subterms[i] = ((RBTerm)tuple.getSubterm(i).accept(this));
    }
    return RBTuple.make(subterms);
  }
  
  public Object visit(RBPair pair)
  {
    RBPair head = new RBPair((RBTerm)pair.getCar().accept(this));
    
    RBPair prev = head;
    
    RBTerm cdr = pair.getCdr();
    while ((cdr instanceof RBPair))
    {
      pair = (RBPair)cdr;
      RBPair next = new RBPair((RBTerm)pair.getCar().accept(this));
      prev.setCdr(next);
      prev = next;
      cdr = pair.getCdr();
    }
    prev.setCdr((RBTerm)cdr.accept(this));
    
    return head;
  }
  
  public Object visit(RBQuoted quoted)
  {
    return new RBQuoted(
      (RBTerm)quoted.getQuotedParts().accept(this));
  }
  
  public Object visit(RBVariable var)
  {
    RBTerm val = this.subst.get(var);
    if (val == null) {
      return (RBTerm)var.accept(new InstantiateVisitor(this.inst));
    }
    return (RBTerm)val.accept(this);
  }
  
  public Object visit(RBIgnoredVariable ignoredVar)
  {
    return ignoredVar;
  }
  
  public Object visit(RBTemplateVar templVar)
  {
    throw new Error("Unsupported operation");
  }
}
