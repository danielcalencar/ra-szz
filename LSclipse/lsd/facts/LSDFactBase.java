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
package lsd.facts;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import lsd.io.LSDTyrubaRuleChecker;
import lsd.rule.LSDFact;
import lsd.rule.LSDLiteral;
import lsd.rule.LSDRule;
import lsd.rule.LSDVariable;
import metapackage.MetaInfo;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class LSDFactBase
{
  public static final boolean deltaKB = true;
  public static final boolean twoKB = false;
  private LSDTyrubaRuleChecker ruleChecker = new LSDTyrubaRuleChecker();
  private LinkedHashSet<LSDFact> factsDeltaKB = new LinkedHashSet();
  private LinkedHashSet<LSDFact> facts2KB = new LinkedHashSet();
  private ArrayList<LSDRule> winnowingRules = new ArrayList();
  private boolean winnowed = true;
  private HashSet<LSDFact> matched = new HashSet();
  private HashMap<LSDRule, List<LSDFact>> ruleMatches = new HashMap();
  private HashMap<LSDRule, List<Map<LSDVariable, String>>> ruleExceptions = new HashMap();
  
  public LinkedHashSet<LSDFact> get2KBFact()
  {
    return this.facts2KB;
  }
  
  public LinkedHashSet<LSDFact> getDeltaKBFact()
  {
    return this.factsDeltaKB;
  }
  
  public List<LSDFact> getRemainingFacts(boolean deltaKB)
  {
    if (!this.winnowed) {
      winnow();
    }
    ArrayList<LSDFact> remainingFacts = new ArrayList();
    for (LSDFact f : deltaKB ? this.factsDeltaKB : this.facts2KB) {
      if (!this.matched.contains(f)) {
        remainingFacts.add(f);
      }
    }
    return remainingFacts;
  }
  
  public List<LSDFact> getRelevantFacts(LSDRule rule)
  {
    if (!this.winnowed) {
      winnow();
    }
    if (!this.ruleMatches.containsKey(rule))
    {
      System.err.println("The requested rule (" + rule + 
        ") is not in the list.");
      return null;
    }
    ArrayList<LSDFact> relevantFacts = new ArrayList();
    for (LSDFact f : (List)this.ruleMatches.get(rule)) {
      if ((this.factsDeltaKB.contains(f)) || (this.facts2KB.contains(f))) {
        relevantFacts.add(f);
      }
    }
    return relevantFacts;
  }
  
  public List<Map<LSDVariable, String>> getExceptions(LSDRule rule)
  {
    if (!this.winnowed) {
      winnow();
    }
    if (!this.ruleExceptions.containsKey(rule))
    {
      System.err.println("The requested rule (" + rule + 
        ") is not in the list.");
      return null;
    }
    return (List)this.ruleExceptions.get(rule);
  }
  
  public void loadDeltaKBFactBase(ArrayList<LSDFact> facts)
    throws ParseException, TypeModeError, IOException
  {
    this.ruleChecker.loadAdditionalDB(MetaInfo.includedDelta);
    for (LSDFact fact : facts)
    {
      this.ruleChecker.loadFact(fact);
      this.factsDeltaKB.add(fact);
    }
    resetWinnowing();
  }
  
  public void loadFilteredDeltaFactBase(ArrayList<LSDFact> facts, ArrayList<String> typeNames)
    throws Exception
  {
    this.ruleChecker.loadAdditionalDB(MetaInfo.includedDelta);
    String line = null;
    Iterator localIterator2;
    for (Iterator localIterator1 = facts.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDFact fact = (LSDFact)localIterator1.next();
      line = fact.toString();
      localIterator2 = typeNames.iterator(); continue;String str = (String)localIterator2.next();
      if (line.contains(str))
      {
        this.ruleChecker.loadFact(fact);
        this.factsDeltaKB.add(fact);
      }
    }
    resetWinnowing();
  }
  
  public void loadFiltered2KBFactBase(ArrayList<LSDFact> facts, ArrayList<String> typeNames)
    throws Exception
  {
    this.ruleChecker.loadAdditionalDB(MetaInfo.included2kb);
    String line = null;
    Iterator localIterator2;
    for (Iterator localIterator1 = facts.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDFact fact = (LSDFact)localIterator1.next();
      line = fact.toString();
      localIterator2 = typeNames.iterator(); continue;String str = (String)localIterator2.next();
      if (line.contains(str))
      {
        this.ruleChecker.loadFact(fact);
        this.facts2KB.add(fact);
      }
    }
    resetWinnowing();
  }
  
  public void load2KBFactBase(ArrayList<LSDFact> facts)
    throws ParseException, TypeModeError, IOException
  {
    this.ruleChecker.loadAdditionalDB(MetaInfo.included2kb);
    for (LSDFact fact : facts)
    {
      this.ruleChecker.loadFact(fact);
      this.facts2KB.add(fact);
    }
    resetWinnowing();
  }
  
  public void loadWinnowingRules(Collection<LSDRule> rules)
  {
    this.winnowingRules.addAll(rules);
    resetWinnowing();
  }
  
  private void winnow()
  {
    this.ruleMatches = new HashMap();
    for (LSDRule rule : this.winnowingRules)
    {
      ArrayList<LSDFact> thisRuleMatches = new ArrayList();
      List<Map<LSDVariable, String>> counterExamples = this.ruleChecker.getCounterExamples(rule);
      this.ruleExceptions.put(rule, counterExamples);
      ArrayList<LSDRule> resultingConclusions = this.ruleChecker.getTrueConclusions(rule);
      Iterator localIterator3;
      for (Iterator localIterator2 = resultingConclusions.iterator(); localIterator2.hasNext(); localIterator3.hasNext())
      {
        LSDRule matchedRule = (LSDRule)localIterator2.next();
        localIterator3 = matchedRule.getLiterals().iterator(); continue;LSDLiteral generatedLiteral = (LSDLiteral)localIterator3.next();
        if (!(generatedLiteral instanceof LSDFact))
        {
          System.out.println("Not a fact:" + generatedLiteral);
        }
        else
        {
          LSDFact fact = ((LSDFact)generatedLiteral)
            .nonNegatedCopy();
          if (this.factsDeltaKB.contains(fact))
          {
            thisRuleMatches.add(fact);
            this.matched.add(fact);
          }
          if (this.facts2KB.contains(fact))
          {
            thisRuleMatches.add(fact);
            this.matched.add(fact);
          }
        }
      }
      this.ruleMatches.put(rule, thisRuleMatches);
    }
    this.winnowed = true;
  }
  
  void forceWinnowing()
  {
    winnow();
  }
  
  private void resetWinnowing()
  {
    this.winnowed = false;
  }
  
  public int num2KBFactSize()
  {
    return this.facts2KB.size();
  }
  
  public int numDeltaKBFactSize()
  {
    return this.factsDeltaKB.size();
  }
}
