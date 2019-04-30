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
import tyRuBa.engine.Frame;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBConjunction;
import tyRuBa.engine.RBCountAll;
import tyRuBa.engine.RBDisjunction;
import tyRuBa.engine.RBExistsQuantifier;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBFindAll;
import tyRuBa.engine.RBIgnoredVariable;
import tyRuBa.engine.RBModeSwitchExpression;
import tyRuBa.engine.RBNotFilter;
import tyRuBa.engine.RBPair;
import tyRuBa.engine.RBPredicateExpression;
import tyRuBa.engine.RBQuoted;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTestFilter;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RBVariable;
import tyRuBa.modes.ConstructorType;

public abstract class SubstituteOrInstantiateVisitor
  implements ExpressionVisitor, TermVisitor
{
  Frame frame;
  
  public SubstituteOrInstantiateVisitor(Frame frame)
  {
    this.frame = frame;
  }
  
  public Frame getFrame()
  {
    return this.frame;
  }
  
  public Object visit(RBConjunction conjunction)
  {
    RBConjunction result = new RBConjunction();
    for (int i = 0; i < conjunction.getNumSubexps(); i++) {
      result.addSubexp(
        (RBExpression)conjunction.getSubexp(i).accept(this));
    }
    return result;
  }
  
  public Object visit(RBDisjunction disjunction)
  {
    RBDisjunction result = new RBDisjunction();
    for (int i = 0; i < disjunction.getNumSubexps(); i++) {
      result.addSubexp(
        (RBExpression)disjunction.getSubexp(i).accept(this));
    }
    return result;
  }
  
  public Object visit(RBExistsQuantifier exists)
  {
    RBExpression exp = (RBExpression)exists.getExp().accept(this);
    Collection vars = new HashSet();
    for (int i = 0; i < exists.getNumVars(); i++) {
      vars.add(exists.getVarAt(i).accept(this));
    }
    return new RBExistsQuantifier(vars, exp);
  }
  
  public Object visit(RBFindAll findAll)
  {
    RBExpression query = (RBExpression)findAll.getQuery().accept(this);
    RBTerm extract = (RBTerm)findAll.getExtract().accept(this);
    RBTerm result = (RBTerm)findAll.getResult().accept(this);
    return new RBFindAll(query, extract, result);
  }
  
  public Object visit(RBCountAll count)
  {
    RBExpression query = (RBExpression)count.getQuery().accept(this);
    RBTerm extract = (RBTerm)count.getExtract().accept(this);
    RBTerm result = (RBTerm)count.getResult().accept(this);
    return new RBCountAll(query, extract, result);
  }
  
  public Object visit(RBModeSwitchExpression modeSwitch)
  {
    throw new Error("Should not happen: a mode case should have been selected before any substitution or instantiation is performed");
  }
  
  public Object visit(RBNotFilter notFilter)
  {
    RBExpression negatedQuery = 
      (RBExpression)notFilter.getNegatedQuery().accept(this);
    return new RBNotFilter(negatedQuery);
  }
  
  public Object visit(RBPredicateExpression predExp)
  {
    return predExp.withNewArgs((RBTuple)predExp.getArgs().accept(this));
  }
  
  public Object visit(RBTestFilter testFilter)
  {
    RBExpression testQuery = 
      (RBExpression)testFilter.getQuery().accept(this);
    return new RBTestFilter(testQuery);
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
  
  public Object visit(RBIgnoredVariable ignoredVar)
  {
    return ignoredVar;
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
}
