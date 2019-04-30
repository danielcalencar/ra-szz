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
package lsd.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lsd.rule.LSDBinding;
import lsd.rule.LSDFact;
import lsd.rule.LSDLiteral;
import lsd.rule.LSDPredicate;
import lsd.rule.LSDRule;
import lsd.rule.LSDVariable;
import metapackage.MetaInfo;
import tyRuBa.engine.Frame;
import tyRuBa.engine.FrontEnd;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBVariable;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.util.ElementSource;

public class LSDTyrubaRuleChecker
{
  private FrontEnd frontend = null;
  private File dbDir = null;
  private boolean backgroundPageCleaning = false;
  boolean loadInitFile = true;
  int cachesize = 5000;
  
  public LSDTyrubaRuleChecker()
  {
    if (this.frontend == null) {
      if (this.dbDir == null) {
        this.frontend = new FrontEnd(this.loadInitFile, MetaInfo.fdbDir, true, 
          null, true, this.backgroundPageCleaning);
      } else {
        this.frontend = new FrontEnd(this.loadInitFile, this.dbDir, true, null, false, 
          this.backgroundPageCleaning);
      }
    }
    this.frontend.setCacheSize(this.cachesize);
  }
  
  public void loadAdditionalDB(File inputDBFile)
    throws ParseException, TypeModeError, IOException
  {
    this.frontend.load(inputDBFile.getAbsolutePath());
  }
  
  public void loadAdditionalDB(String input)
    throws ParseException, TypeModeError, IOException
  {
    this.frontend.load(input);
  }
  
  public void loadPrimedAdditionalDB(File inputDBFile)
    throws ParseException, TypeModeError, IOException
  {
    List<LSDFact> facts = null;
    try
    {
      facts = new LSDTyrubaFactReader(inputDBFile).getFacts();
    }
    catch (Exception localException) {}
    if (facts != null) {
      for (LSDFact fact : facts) {
        if (fact.getPredicate().isConclusionPredicate()) {
          this.frontend.parse(fact.toString().replaceFirst("_", "_p_") + ".");
        }
      }
    }
  }
  
  public void loadFact(LSDFact fact)
    throws TypeModeError, ParseException
  {
    this.frontend.parse(fact.toString() + ".");
  }
  
  public List<Map<LSDVariable, String>> getCounterExamples(LSDRule rule)
  {
    return (List)invokeQuery(rule, false);
  }
  
  public ArrayList<LSDRule> getTrueConclusions(LSDRule rule)
  {
    return (ArrayList)invokeQuery(rule, true);
  }
  
  private Object invokeQuery(LSDRule rule, boolean returnConclusions)
  {
    LSDRule substitute = returnConclusions ? rule.getConclusions() : rule;
    String query = (returnConclusions ? rule.convertAllToAntecedents() : rule).toTyrubaQuery(false);
    ArrayList<Map<LSDVariable, String>> exceptions = new ArrayList();
    ArrayList<LSDRule> newSubstitutedRules = new ArrayList();
    ArrayList<LSDVariable> freeVars = rule.getConclusions().getFreeVariables();
    
    Set<Set<String>> exceptionMatches = new HashSet();
    Set<String> foundConclusionMatches = new HashSet();
    try
    {
      RBExpression exp = this.frontend.makeExpression(query);
      ElementSource es = this.frontend.frameQuery(exp);
      try
      {
        if (es.status() == -1)
        {
          if (returnConclusions) {
            return newSubstitutedRules;
          }
          return exceptions;
        }
        while (es.status() == 1)
        {
          Frame frame = (Frame)es.nextElement();
          Set<String> exceptionMatchStrings = new HashSet();
          LinkedHashMap<LSDVariable, String> exception = new LinkedHashMap();
          LSDRule newRule = null;
          for (RBVariable matchedVar : frame.keySet())
          {
            RBTerm term = frame.get(matchedVar);
            String constant = "\"" + term.toString() + "\"";
            LSDVariable toReplace = null;
            for (LSDVariable freeVar : new LinkedHashSet(freeVars)) {
              if (freeVar != null) {
                if (freeVar.toString().equals(matchedVar.toString()))
                {
                  exceptionMatchStrings.add(freeVar.toString() + constant);
                  toReplace = freeVar;
                }
              }
            }
            if (toReplace != null)
            {
              exception.put(toReplace, constant);
              newRule = (newRule == null ? substitute : newRule)
                .substitute(toReplace, new LSDBinding(constant));
            }
          }
          if (!exceptionMatches.contains(exceptionMatchStrings))
          {
            exceptions.add(exception);
            exceptionMatches.add(exceptionMatchStrings);
          }
          if ((newRule != null) && 
            (!foundConclusionMatches.contains(newRule.toString())))
          {
            newSubstitutedRules.add(newRule);
            foundConclusionMatches.add(newRule.toString());
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      catch (Error e)
      {
        e.printStackTrace();
      }
      if (!returnConclusions) {
        break label484;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return newSubstitutedRules;
    label484:
    return exceptions;
  }
  
  public List<LSDFact> get2KBMatches(LSDRule rule)
  {
    LSDRule substitute = rule;
    String query = rule.convertAllToAntecedents().toTyrubaQuery(false);
    ArrayList<LSDRule> newSubstitutedRules = new ArrayList();
    ArrayList<LSDVariable> freeVars = rule.getFreeVariables();
    RBVariable matchedVar;
    try
    {
      RBExpression exp = this.frontend.makeExpression(query);
      ElementSource es = this.frontend.frameQuery(exp);
      try
      {
        if (es.status() == -1) {
          return new ArrayList();
        }
        while (es.status() == 1)
        {
          Frame frame = (Frame)es.nextElement();
          LSDRule newRule = null;
          for (Iterator localIterator1 = frame.keySet().iterator(); localIterator1.hasNext();)
          {
            matchedVar = (RBVariable)localIterator1.next();
            RBTerm term = frame.get(matchedVar);
            String constant = "\"" + term.toString() + "\"";
            LSDVariable toReplace = null;
            for (LSDVariable freeVar : new LinkedHashSet(freeVars)) {
              if (freeVar != null) {
                if (freeVar.toString().equals(matchedVar.toString())) {
                  toReplace = freeVar;
                }
              }
            }
            if (toReplace != null) {
              newRule = 
                (newRule == null ? substitute : newRule).substitute(toReplace, new LSDBinding(constant));
            }
          }
          if (newRule != null) {
            newSubstitutedRules.add(newRule);
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      catch (Error e)
      {
        e.printStackTrace();
      }
      foundFacts = new ArrayList();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    List<LSDFact> foundFacts;
    for (e = newSubstitutedRules.iterator(); e.hasNext(); matchedVar.hasNext())
    {
      LSDRule r = (LSDRule)e.next();
      matchedVar = r.getLiterals().iterator(); continue;LSDLiteral literal = (LSDLiteral)matchedVar.next();
      if (((literal instanceof LSDFact)) && (literal.getPredicate().is2KBPredicate()) && (!foundFacts.contains((LSDFact)literal))) {
        foundFacts.add((LSDFact)literal);
      }
    }
    return foundFacts;
  }
  
  private int countMatches(String query, List<LSDVariable> freeVars, int max)
  {
    Set<Set<String>> matches = new HashSet();
    try
    {
      RBExpression exp = this.frontend.makeExpression(query);
      ElementSource es = this.frontend.frameQuery(exp);
      if (es.status() == -1) {
        return 0;
      }
      do
      {
        Frame frame = (Frame)es.nextElement();
        Set<String> matchStrings = new HashSet();
        Iterator localIterator2;
        for (Iterator localIterator1 = frame.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
        {
          RBVariable matchedVar = (RBVariable)localIterator1.next();
          RBTerm term = frame.get(matchedVar);
          String constant = "\"" + term.toString() + "\"";
          
          localIterator2 = new LinkedHashSet(freeVars).iterator(); continue;LSDVariable freeVar = (LSDVariable)localIterator2.next();
          if (freeVar.toString().equals(matchedVar.toString())) {
            matchStrings.add(freeVar.toString() + constant);
          }
        }
        matches.add(matchStrings);
        if (es.status() != 1) {
          break;
        }
      } while ((max == 0) || (matches.size() < max));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return matches.size();
  }
  
  public int countTrueConclusions(LSDRule rule)
  {
    return countTrueConclusions(rule, 0);
  }
  
  public int countTrueConclusions(LSDRule rule, int max)
  {
    String query = rule.convertAllToAntecedents().toTyrubaQuery(false);
    List<LSDVariable> freeVars = rule.getConclusions().getFreeVariables();
    return countMatches(query, freeVars, max);
  }
  
  public int countCounterExamples(LSDRule rule)
  {
    return countCounterExamples(rule, 0);
  }
  
  public int countCounterExamples(LSDRule rule, int max)
  {
    String query = rule.toTyrubaQuery(false);
    List<LSDVariable> freeVars = rule.getConclusions().getFreeVariables();
    return countMatches(query, freeVars, max);
  }
  
  public Set<String> getReplacementConstants(LSDRule rule, LSDVariable match)
  {
    assert (rule.getFreeVariables().contains(match));
    String query = rule.convertAllToAntecedents().toTyrubaQuery(false);
    replacements = new LinkedHashSet();
    try
    {
      RBExpression exp = this.frontend.makeExpression(query);
      ElementSource es = this.frontend.frameQuery(exp);
      try
      {
        if (es.status() == -1) {
          return replacements;
        }
        while (es.status() == 1)
        {
          Frame frame = (Frame)es.nextElement();
          for (RBVariable matchedVar : frame.keySet()) {
            if (matchedVar.toString().equals(match.toString()))
            {
              RBTerm term = frame.get(matchedVar);
              if (term == null) {
                break;
              }
              replacements.add("\"" + term.toString() + "\"");
              break;
            }
          }
        }
      }
      catch (Exception localException1) {}catch (Error localError) {}
      return replacements;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void shutdown()
  {
    this.frontend.shutdown();
    this.frontend.crash();
  }
  
  public void loadRelatedFacts(ArrayList<LSDFact> facts, ArrayList<String> typeNames)
    throws Exception
  {
    loadAdditionalDB(MetaInfo.included2kb);
    String line = null;
    Iterator localIterator2;
    for (Iterator localIterator1 = facts.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDFact fact = (LSDFact)localIterator1.next();
      line = fact.toString() + ".";
      localIterator2 = typeNames.iterator(); continue;String str = (String)localIterator2.next();
      if (line.contains(str)) {
        loadFact(LSDTyrubaFactReader.parseTyrubaFact(line));
      }
    }
  }
}
