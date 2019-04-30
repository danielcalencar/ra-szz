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
package lsd.rule;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lsd.io.LSDAlchemyRuleReader;

public class LSDRule
{
  private double score = 0.0D;
  private int numMatches = 0;
  private double accuracy = 0.0D;
  double numDeltaFacts = 0.0D;
  private ArrayList<LSDLiteral> literals = new ArrayList();
  private Set<LSDVariable> freeVars = new HashSet();
  private static HashMap<Character, Integer> penaltyLookup;
  
  static
  {
    penaltyLookup = new HashMap();
    
    penaltyLookup.put(Character.valueOf('p'), Integer.valueOf(1));
    penaltyLookup.put(Character.valueOf('t'), Integer.valueOf(2));
    penaltyLookup.put(Character.valueOf('m'), Integer.valueOf(3));
    penaltyLookup.put(Character.valueOf('f'), Integer.valueOf(3));
    penaltyLookup.put(Character.valueOf('a'), Integer.valueOf(4));
    penaltyLookup.put(Character.valueOf('b'), Integer.valueOf(4));
    penaltyLookup.put(Character.valueOf('c'), Integer.valueOf(4));
  }
  
  public LSDRule() {}
  
  public class LSDRuleComparator
    implements Comparator<LSDRule>
  {
    public LSDRuleComparator() {}
    
    public int compare(LSDRule r1, LSDRule r2)
    {
      return 
        r2.score - r1.score == 0.0D ? 0 : r2.score - r1.score > 0.0D ? 1 : -1;
    }
  }
  
  public LSDRule(LSDRule oldRule)
  {
    for (LSDLiteral literal : oldRule.literals) {
      addLiteral(literal);
    }
  }
  
  public LSDRule(LSDRule rule, boolean b)
  {
    for (LSDLiteral literal : rule.literals)
    {
      ArrayList<LSDBinding> newBindings = new ArrayList();
      for (LSDBinding binding : literal.getBindings())
      {
        LSDBinding newBinding = new LSDBinding(new LSDVariable(binding.getVariable().getName(), binding.getType()));
        newBindings.add(newBinding);
      }
      try
      {
        LSDLiteral newLiteral = new LSDLiteral(literal.predicate, newBindings, !literal.isNegated());
        addLiteral(newLiteral);
      }
      catch (LSDInvalidTypeException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public ArrayList<LSDVariable> getFreeVariables()
  {
    ArrayList<LSDVariable> fv = new ArrayList(this.freeVars);
    return fv;
  }
  
  public boolean addLiteral(LSDLiteral newLiteral)
  {
    Collection<LSDVariable> newFreeVars = newLiteral.freeVars();
    this.literals.add(newLiteral);
    this.freeVars.addAll(newFreeVars);
    return true;
  }
  
  public ArrayList<LSDLiteral> getLiterals()
  {
    return new ArrayList(this.literals);
  }
  
  public LSDRule convertAllToAntecedents()
  {
    LSDRule antecedents = new LSDRule();
    for (LSDLiteral literal : this.literals) {
      if (literal.isNegated()) {
        antecedents.addLiteral(literal);
      } else {
        antecedents.addLiteral(literal.negatedCopy());
      }
    }
    return antecedents;
  }
  
  public LSDRule getAntecedents()
  {
    LSDRule antecedents = new LSDRule();
    for (LSDLiteral literal : this.literals) {
      if (literal.isNegated()) {
        antecedents.addLiteral(literal);
      }
    }
    return antecedents;
  }
  
  public LSDRule getConclusions()
  {
    LSDRule conclusions = new LSDRule();
    for (LSDLiteral literal : this.literals) {
      if (!literal.isNegated()) {
        conclusions.addLiteral(literal);
      }
    }
    return conclusions;
  }
  
  public boolean literalsLinked()
  {
    Map<LSDVariable, Integer> freeVarCount = new HashMap();
    Iterator localIterator2;
    for (Iterator localIterator1 = this.literals.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDLiteral l = (LSDLiteral)localIterator1.next();
      localIterator2 = l.freeVars().iterator(); continue;LSDVariable v = (LSDVariable)localIterator2.next();
      if (freeVarCount.get(v) == null) {
        freeVarCount.put(v, Integer.valueOf(1));
      } else {
        freeVarCount.put(v, Integer.valueOf(((Integer)freeVarCount.get(v)).intValue() + 1));
      }
    }
    for (LSDLiteral l : this.literals)
    {
      boolean invalid = true;
      for (LSDVariable v : l.freeVars()) {
        if (((Integer)freeVarCount.get(v)).intValue() > 1)
        {
          invalid = false;
          
          break;
        }
      }
      if (invalid) {
        return false;
      }
    }
    return true;
  }
  
  public boolean hasValidLinks()
  {
    LSDLiteral l;
    int i;
    for (Iterator localIterator = this.literals.iterator(); localIterator.hasNext(); i < l.bindings.size())
    {
      l = (LSDLiteral)localIterator.next();
      i = 0; continue;
      LSDBinding temp = (LSDBinding)l.bindings.get(i);
      if (!temp.isBound()) {
        for (int j = i + 1; j < l.bindings.size(); j++) {
          if (temp.getVariable() == ((LSDBinding)l.bindings.get(j)).getVariable()) {
            return false;
          }
        }
      }
      i++;
    }
    return true;
  }
  
  public boolean isSamePreds()
  {
    ArrayList<LSDLiteral> conclusions = getConclusions().getLiterals();
    for (LSDLiteral conc : conclusions)
    {
      boolean allDuplicate = true;
      for (LSDLiteral literal : getAntecedents().getLiterals()) {
        if (!literal.getPredicate().getSuffix().equalsIgnoreCase(conc.getPredicate().getSuffix())) {
          allDuplicate = false;
        }
      }
      if (allDuplicate) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isValid()
  {
    if ((!isHornClause()) || (!typeChecks())) {
      return false;
    }
    if (!literalsLinked()) {
      return false;
    }
    Set<LSDVariable> antecedentVars = new HashSet(getAntecedents().getFreeVariables());
    for (LSDLiteral literal : getConclusions().getLiterals())
    {
      boolean primaryMatched = false;
      for (List<LSDBinding> bindingSet : literal.getPrimaryBindings())
      {
        boolean anyUnmatched = false;
        for (LSDBinding binding : bindingSet) {
          if ((!binding.isBound()) && (!antecedentVars.contains(binding.getVariable())))
          {
            anyUnmatched = true;
            break;
          }
        }
        if (!anyUnmatched)
        {
          primaryMatched = true;
          break;
        }
      }
      if (!primaryMatched) {
        return false;
      }
    }
    return true;
  }
  
  public boolean containsFacts()
  {
    for (LSDLiteral literal : this.literals) {
      if ((literal instanceof LSDFact)) {
        return true;
      }
    }
    return false;
  }
  
  public LSDRule substitute(LSDVariable toReplace, LSDBinding replacement)
    throws LSDInvalidTypeException
  {
    LSDRule newRule = new LSDRule();
    for (LSDLiteral literal : this.literals) {
      newRule.addLiteral(literal.substitute(toReplace, replacement));
    }
    return newRule;
  }
  
  public boolean typeChecks()
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.freeVars.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDVariable fv_i = (LSDVariable)localIterator1.next();
      localIterator2 = this.freeVars.iterator(); continue;LSDVariable fv_j = (LSDVariable)localIterator2.next();
      if (fv_i.typeConflicts(fv_j)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean isHornClause()
  {
    int nonNegatedLiterals = 0;
    for (LSDLiteral literal : this.literals) {
      if (!literal.isNegated()) {
        nonNegatedLiterals++;
      }
    }
    return nonNegatedLiterals == 1;
  }
  
  public String toString()
  {
    StringBuilder output = new StringBuilder();
    for (LSDLiteral literal : this.literals) {
      if (literal.isNegated())
      {
        if (output.length() != 0) {
          output.append(" ^ ");
        }
        output.append(literal.nonNegatedCopy().toString());
      }
    }
    output.append(" => ");
    boolean first = true;
    for (LSDLiteral literal : this.literals) {
      if (!literal.isNegated())
      {
        if (!first) {
          output.append(" ^ ");
        }
        output.append(literal.nonNegatedCopy().toString());
        first = false;
      }
    }
    return output.toString();
  }
  
  public String toTyrubaQuery(boolean commandLine)
  {
    StringBuilder output = new StringBuilder();
    if (commandLine) {
      output.append(":-");
    }
    Hashtable<LSDVariable, Integer> freeVarCount = new Hashtable();
    Iterator localIterator2;
    for (Iterator localIterator1 = this.literals.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDLiteral l = (LSDLiteral)localIterator1.next();
      localIterator2 = l.freeVars().iterator(); continue;LSDVariable v = (LSDVariable)localIterator2.next();
      if (freeVarCount.get(v) == null) {
        freeVarCount.put(v, Integer.valueOf(1));
      } else {
        freeVarCount.put(v, Integer.valueOf(((Integer)freeVarCount.get(v)).intValue() + 1));
      }
    }
    for (int i = 0; i < this.literals.size(); i++)
    {
      if (i > 0) {
        output.append(",");
      }
      output.append(((LSDLiteral)this.literals.get(i)).toTyrubaString(freeVarCount));
    }
    return output.toString() + (commandLine ? "." : "");
  }
  
  private String canonicalRepresentation()
  {
    return canonicalRepresentation(getLiterals(), new HashMap(), 0);
  }
  
  private String canonicalRepresentation(List<LSDLiteral> literals, Map<LSDVariable, String> varMap, int nextVarNum)
  {
    if (literals.size() == 0) {
      return "";
    }
    List<LSDPredicate> predicates = LSDPredicate.getPredicates();
    int firstPredicateIndex = predicates.size();
    List<Integer> firstPredicateList = null;
    for (int i = 0; i < literals.size(); i++)
    {
      LSDLiteral literal = (LSDLiteral)literals.get(i);
      thisIndex = predicates.indexOf(literal.getPredicate());
      if ((thisIndex < firstPredicateIndex) && (thisIndex >= 0))
      {
        firstPredicateIndex = thisIndex;
        firstPredicateList = new ArrayList();
        firstPredicateList.add(Integer.valueOf(i));
      }
      else if (thisIndex == firstPredicateIndex)
      {
        firstPredicateList.add(Integer.valueOf(i));
      }
    }
    String repr = null;
    for (int thisIndex = firstPredicateList.iterator(); thisIndex.hasNext();)
    {
      int index = ((Integer)thisIndex.next()).intValue();
      
      StringBuilder thisRepr = new StringBuilder();
      Map<LSDVariable, String> thisVarMap = new HashMap(varMap);
      int thisNextVarNum = nextVarNum;
      LSDLiteral literal = (LSDLiteral)literals.get(index);
      if (literal.isNegated()) {
        thisRepr.append("!");
      }
      thisRepr.append(literal.getPredicate().getName());
      thisRepr.append("(");
      for (LSDBinding binding : literal.getBindings())
      {
        if (binding.isBound())
        {
          thisRepr.append(binding.toString());
        }
        else
        {
          LSDVariable variable = binding.getVariable();
          if (!thisVarMap.containsKey(variable))
          {
            thisVarMap.put(variable, "?x" + thisNextVarNum);
            thisNextVarNum++;
          }
          thisRepr.append((String)thisVarMap.get(variable));
        }
        thisRepr.append(",");
      }
      thisRepr.append(")");
      List<LSDLiteral> newLiterals = new ArrayList(literals);
      newLiterals.remove(index);
      thisRepr.append(canonicalRepresentation(newLiterals, thisVarMap, thisNextVarNum));
      if ((repr == null) || (thisRepr.toString().compareTo(repr) < 0)) {
        repr = thisRepr.toString();
      }
    }
    return repr;
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof LSDRule)) {
      return false;
    }
    return canonicalRepresentation().equals(((LSDRule)o).canonicalRepresentation());
  }
  
  public int hashCode()
  {
    return canonicalRepresentation().hashCode();
  }
  
  public int generalityCompare(LSDRule r2)
  {
    int penalty1 = 0;
    int penalty2 = 0;
    for (LSDVariable var : getFreeVariables()) {
      penalty1 += ((Integer)penaltyLookup.get(Character.valueOf(var.getType()))).intValue();
    }
    for (LSDVariable var : r2.getFreeVariables()) {
      penalty2 += ((Integer)penaltyLookup.get(Character.valueOf(var.getType()))).intValue();
    }
    return penalty1 - penalty2;
  }
  
  public static void main(String[] args)
  {
    LSDPredicate foo = LSDPredicate.getPredicate("added_inheritedmethod");
    ArrayList<LSDBinding> bindings = new ArrayList();
    LSDVariable a = new LSDVariable("a", 'm');
    LSDBinding binding = new LSDBinding(a);
    bindings.add(binding);
    binding = new LSDBinding(new LSDVariable("b", 't'));
    bindings.add(binding);
    binding = new LSDBinding(new LSDVariable("c", 't'));
    bindings.add(binding);
    try
    {
      LSDLiteral bar = new LSDLiteral(foo, bindings, false);
      System.out.println(bar.freeVars());
      
      foo = LSDPredicate.getPredicate("deleted_accesses");
      bindings = new ArrayList();
      binding = new LSDBinding(new LSDVariable("d", 'f'));
      bindings.add(binding);
      binding = new LSDBinding(new LSDVariable("a", 'm'));
      bindings.add(binding);
      LSDLiteral baz = new LSDLiteral(foo, bindings, true);
      
      foo = LSDPredicate.getPredicate("deleted_accesses");
      bindings = new ArrayList();
      binding = new LSDBinding(new LSDVariable("r", 'f'));
      bindings.add(binding);
      binding = new LSDBinding(new LSDVariable("s", 'm'));
      bindings.add(binding);
      LSDLiteral quxx = new LSDLiteral(foo, bindings, true);
      
      LSDRule r = new LSDRule();
      assert (r.addLiteral(bar));
      System.out.println(r);
      assert (!r.isHornClause());
      assert (r.freeVars.contains(a));
      LSDVariable b = new LSDVariable("a", 'm');
      assert (a.hashCode() == b.hashCode());
      assert (a.equals(b));
      assert (a != b);
      assert (r.freeVars.contains(b));
      assert (a.equals(b));
      assert (r.addLiteral(baz));
      System.out.println(r);
      assert (r.isHornClause());
      assert (r.isValid());
      assert (r.addLiteral(quxx));
      System.out.println(r);
      if ((!$assertionsDisabled) && (r.isValid())) {
        throw new AssertionError();
      }
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      e.printStackTrace();
      
      System.out.println(LSDAlchemyRuleReader.parseAlchemyRule("before_typeintype(z, x) ^ before_typeintype(x, z) => added_type(x)").canonicalRepresentation());
      
      System.out.println("Rule tests succeeded.");
    }
  }
  
  public String[] getClassLevelGrounding()
  {
    HashSet<String> res = new HashSet();
    ArrayList<LSDLiteral> temp = getLiterals();
    Iterator localIterator2;
    for (Iterator localIterator1 = temp.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDLiteral literal = (LSDLiteral)localIterator1.next();
      List<LSDBinding> bindings = literal.getBindings();
      localIterator2 = bindings.iterator(); continue;LSDBinding binding = (LSDBinding)localIterator2.next();
      if (binding.isBound()) {
        res.add(binding.getGroundConst());
      }
    }
    String[] results = new String[res.size()];
    int i = 0;
    for (String str : res) {
      results[(i++)] = str;
    }
    return results;
  }
  
  public double getScore()
  {
    return this.score;
  }
  
  public void setScore()
  {
    boolean hasLanguageBinding = false;
    int bindingScore = 0;
    Iterator localIterator2;
    for (Iterator localIterator1 = this.literals.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDLiteral literal = (LSDLiteral)localIterator1.next();
      localIterator2 = literal.getBindings().iterator(); continue;LSDBinding binding = (LSDBinding)localIterator2.next();
      if (binding.getGroundConst() != null)
      {
        if ((binding.getGroundConst().startsWith("java")) && (!hasLanguageBinding)) {
          hasLanguageBinding = true;
        }
        bindingScore += literal.getBindingScore(binding);
      }
    }
    this.score = (2 * bindingScore);
    this.score += this.accuracy;
    this.score += 2.0D * (this.numMatches / 250.0D);
    if (hasLanguageBinding) {
      this.score -= 2.0D;
    }
  }
  
  public void setNumMatches(int numMatches)
  {
    this.numMatches = numMatches;
  }
  
  public int getNumMatches()
  {
    return this.numMatches;
  }
  
  public void setAccuracy(double a)
  {
    this.accuracy = a;
  }
  
  public void removeFreeVar(LSDVariable variable)
  {
    this.freeVars.remove(variable);
  }
}
