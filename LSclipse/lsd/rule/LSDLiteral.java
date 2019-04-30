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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class LSDLiteral
{
  private boolean nonNegated;
  protected final LSDPredicate predicate;
  protected final ArrayList<LSDBinding> bindings;
  
  public LSDLiteral(LSDPredicate pred, List<LSDBinding> bindings, boolean nonNegated)
    throws LSDInvalidTypeException
  {
    this.nonNegated = nonNegated;
    if (pred.arity() != bindings.size())
    {
      this.predicate = null;
      this.bindings = null; return;
    }
    ArrayList<LSDBinding> ALBindings;
    ArrayList<LSDBinding> ALBindings;
    if ((bindings instanceof ArrayList)) {
      ALBindings = (ArrayList)bindings;
    } else {
      ALBindings = new ArrayList(bindings);
    }
    if (!pred.typeChecks(ALBindings)) {
      throw new LSDInvalidTypeException();
    }
    this.predicate = pred;
    this.bindings = ALBindings;
  }
  
  public LSDLiteral nonNegatedCopy()
  {
    try
    {
      return new LSDLiteral(this.predicate, this.bindings, true);
    }
    catch (LSDInvalidTypeException localLSDInvalidTypeException)
    {
      System.err.println("We're creating a non-negated copy of our valid self.  This can't happen..");
      System.exit(1);
    }
    return null;
  }
  
  public LSDLiteral negatedCopy()
  {
    try
    {
      return new LSDLiteral(this.predicate, this.bindings, false);
    }
    catch (LSDInvalidTypeException localLSDInvalidTypeException)
    {
      System.err.println("We're creating a negated copy of our valid self.  This can't happen..");
      System.exit(1);
    }
    return null;
  }
  
  public String toString()
  {
    StringBuilder bs = new StringBuilder();
    for (int i = 0; i < this.bindings.size(); i++)
    {
      if (i >= 1) {
        bs.append(",");
      }
      bs.append(((LSDBinding)this.bindings.get(i)).toString());
    }
    return 
      (this.nonNegated ? "" : "!") + this.predicate.getDisplayName() + "(" + bs.toString() + ")";
  }
  
  public String toTyrubaString(Hashtable<LSDVariable, Integer> freeVarCount)
  {
    String output = "";
    for (int i = 0; i < this.bindings.size(); i++)
    {
      if (i >= 1) {
        output = output + ",";
      }
      output = output + ((LSDBinding)this.bindings.get(i)).toString();
    }
    output = this.predicate.getName() + "(" + output + ")";
    if (this.nonNegated)
    {
      boolean quantified = false;
      for (int i = 0; i < this.bindings.size(); i++)
      {
        LSDVariable var = ((LSDBinding)this.bindings.get(i)).getVariable();
        if ((var != null) && (((Integer)freeVarCount.get(var)).intValue() == 1))
        {
          output = 
            var.toString() + (output.charAt(0) == '?' ? ", " : " : ") + output;
          quantified = true;
        }
      }
      if (quantified) {
        output = "EXISTS " + output;
      }
      output = "NOT(" + output + ")";
    }
    return output;
  }
  
  public LSDLiteral substitute(LSDVariable toReplace, LSDBinding replacement)
    throws LSDInvalidTypeException
  {
    ArrayList<LSDBinding> newbs = new ArrayList();
    boolean freeVariables = false;
    LSDBinding nb;
    for (LSDBinding oldBinding : this.bindings)
    {
      nb = oldBinding.substitute(toReplace, 
        replacement);
      newbs.add(nb);
      if (!nb.isBound()) {
        freeVariables = true;
      }
    }
    if (freeVariables) {
      return new LSDLiteral(this.predicate, newbs, this.nonNegated);
    }
    List<String> binds = new ArrayList();
    for (LSDBinding binding : newbs) {
      binds.add(binding.toString());
    }
    return LSDFact.createLSDFact(this.predicate, binds, this.nonNegated);
  }
  
  public ArrayList<LSDVariable> freeVars()
  {
    ArrayList<LSDVariable> freeVars = new ArrayList();
    for (int i = 0; i < this.bindings.size(); i++) {
      if (!((LSDBinding)this.bindings.get(i)).isBound()) {
        freeVars.add(((LSDBinding)this.bindings.get(i)).getVariable());
      }
    }
    return freeVars;
  }
  
  public boolean isNegated()
  {
    return !this.nonNegated;
  }
  
  public LSDPredicate getPredicate()
  {
    return this.predicate;
  }
  
  public List<LSDBinding> getBindings()
  {
    return new ArrayList(this.bindings);
  }
  
  public boolean equalsIgnoringNegation(Object other)
  {
    if (!(other instanceof LSDLiteral)) {
      return false;
    }
    LSDLiteral otherLit = (LSDLiteral)other;
    if (!this.predicate.equalsIgnoringPrimes(otherLit.predicate)) {
      return false;
    }
    if (this.bindings.size() != otherLit.bindings.size()) {
      return false;
    }
    for (int i = 0; i < this.bindings.size(); i++) {
      if (!((LSDBinding)this.bindings.get(i)).equals(otherLit.bindings.get(i))) {
        return false;
      }
    }
    return true;
  }
  
  public boolean identifiesSameIgnoringNegation(Object other)
  {
    if (!(other instanceof LSDLiteral)) {
      return false;
    }
    LSDLiteral otherLit = (LSDLiteral)other;
    if (!this.predicate.equalsIgnoringPrimes(otherLit.predicate)) {
      return false;
    }
    List<List<LSDBinding>> thisBindingsLists = getPrimaryBindings();
    List<List<LSDBinding>> otherBindingsLists = otherLit.getPrimaryBindings();
    if (thisBindingsLists.size() != otherBindingsLists.size()) {
      return false;
    }
    boolean anyMatch = false;
    for (int i = 0; i < thisBindingsLists.size(); i++)
    {
      boolean thisMatches = true;
      List<LSDBinding> thisBindings = (List)thisBindingsLists.get(i);
      List<LSDBinding> otherBindings = (List)otherBindingsLists.get(i);
      if (thisBindings.size() == otherBindings.size())
      {
        for (int j = 0; j < thisBindings.size(); j++) {
          if (!((LSDBinding)thisBindings.get(j)).equals(otherBindings.get(j))) {
            thisMatches = false;
          }
        }
        if (thisMatches) {
          anyMatch = true;
        }
      }
    }
    return anyMatch;
  }
  
  public boolean equals(Object other)
  {
    if (!(other instanceof LSDLiteral)) {
      return false;
    }
    LSDLiteral otherLit = (LSDLiteral)other;
    if (this.nonNegated != otherLit.nonNegated) {
      return false;
    }
    return equalsIgnoringNegation(otherLit);
  }
  
  public static LSDLiteral createDefaultLiteral(LSDPredicate predicate, boolean nonNegated)
  {
    ArrayList<LSDBinding> bindings = new ArrayList();
    char[] types = predicate.getTypes();
    for (int i = 0; i < types.length; i++)
    {
      String fvName = types[i] + i;
      LSDVariable fv = new LSDVariable(fvName, types[i]);
      LSDBinding binding = new LSDBinding(fv);
      
      bindings.add(binding);
    }
    try
    {
      return new LSDLiteral(predicate, bindings, nonNegated);
    }
    catch (LSDInvalidTypeException localLSDInvalidTypeException) {}
    return null;
  }
  
  public boolean hasSamePred(LSDLiteral other)
  {
    return this.predicate.getSuffix().equals(other.predicate.getSuffix());
  }
  
  public List<List<LSDBinding>> getPrimaryBindings()
  {
    int[][] primaryArguments = this.predicate.getPrimaryArguments();
    List<List<LSDBinding>> primaryBindings = new ArrayList();
    int[][] arrayOfInt1;
    int j = (arrayOfInt1 = primaryArguments).length;
    for (int i = 0; i < j; i++)
    {
      int[] argumentSet = arrayOfInt1[i];
      List<LSDBinding> primaryBindingSet = new ArrayList();
      int[] arrayOfInt2;
      int m = (arrayOfInt2 = argumentSet).length;
      for (int k = 0; k < m; k++)
      {
        int argument = arrayOfInt2[k];
        assert (argument < this.bindings.size());
        primaryBindingSet.add((LSDBinding)this.bindings.get(argument));
      }
      primaryBindings.add(primaryBindingSet);
    }
    return primaryBindings;
  }
  
  public char[] getPrimaryTypes()
  {
    return getPredicate().getPrimaryTypes();
  }
  
  public static void main(String[] args)
  {
    LSDPredicate foo = LSDPredicate.getPredicate("added_inheritedMethod");
    ArrayList<LSDBinding> bindings = new ArrayList();
    LSDBinding binding = new LSDBinding(new LSDVariable("a", 'm'));
    bindings.add(binding);
    binding = new LSDBinding(new LSDVariable("b", 't'));
    bindings.add(binding);
    binding = new LSDBinding(new LSDVariable("c", 't'));
    bindings.add(binding);
    try
    {
      LSDLiteral bar = new LSDLiteral(foo, bindings, false);
      System.out.println(bar);
      bar = bar
        .substitute(new LSDVariable("a", 'm'), new LSDBinding("X"));
      bar = bar
        .substitute(new LSDVariable("b", 't'), new LSDBinding("X"));
      bar = bar
        .substitute(new LSDVariable("c", 't'), new LSDBinding("X"));
      bar.nonNegated = false;
      assert ((bar instanceof LSDFact));
      System.out.println(bar);
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
    }
    System.out.println("Literal tests succeeded.");
  }
  
  public int getBindingScore(LSDBinding binding)
  {
    int i = 0;
    if (this.predicate.getSuffix().equalsIgnoreCase("type"))
    {
      if (getLocation(binding) == 3) {
        i++;
      }
    }
    else if (this.predicate.getSuffix().equalsIgnoreCase("field"))
    {
      if (getLocation(binding) == 3) {
        i++;
      }
    }
    else if (this.predicate.getSuffix().equalsIgnoreCase("method"))
    {
      if (getLocation(binding) == 3) {
        i++;
      }
    }
    else if (this.predicate.getSuffix().equalsIgnoreCase("subtype"))
    {
      i += 2;
      if (getLocation(binding) == 1) {
        i++;
      }
    }
    else if (this.predicate.getSuffix().equalsIgnoreCase("accesses"))
    {
      i++;
    }
    else if (this.predicate.getSuffix().equalsIgnoreCase("calls"))
    {
      i++;
    }
    else if (this.predicate.getSuffix().equalsIgnoreCase("dependency"))
    {
      i += 2;
    }
    return i;
  }
  
  private int getLocation(LSDBinding binding)
  {
    String literal = toString();
    literal = literal.substring(literal.indexOf("(") + 1, literal.length() - 1);
    StringTokenizer tokenizer = new StringTokenizer(literal, ",");
    int i = 1;
    String temp = null;
    while (tokenizer.hasMoreTokens())
    {
      temp = tokenizer.nextToken();
      if (!temp.startsWith("?"))
      {
        temp = temp.substring(1, temp.length() - 1);
        if (binding.getGroundConst().equalsIgnoreCase(temp)) {
          return i;
        }
      }
      i++;
    }
    return 0;
  }
  
  public boolean isConclusion()
  {
    return this.predicate.isConclusionPredicate();
  }
  
  public boolean isDependency()
  {
    return this.predicate.isDependencyPredicate();
  }
  
  public List<LSDLiteral> getCompatibles()
  {
    ArrayList<LSDLiteral> newliterals = new ArrayList();
    for (LSDPredicate pred : this.predicate.getMethodLevelDependency())
    {
      ArrayList<LSDBinding> bindings = new ArrayList();
      int i = 0;
      char[] types = pred.getTypes();
      for (Iterator localIterator2 = getBindings().iterator(); localIterator2.hasNext();)
      {
        ((LSDBinding)localIterator2.next());
        bindings.add(new LSDBinding(new LSDVariable("t" + i, types[i])));
        i++;
      }
      pred.updateBindings(bindings);
      try
      {
        newliterals.add(new LSDLiteral(pred, bindings, !isNegated()));
      }
      catch (LSDInvalidTypeException e)
      {
        e.printStackTrace();
      }
    }
    return newliterals;
  }
}
