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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import lsd.io.LSDAlchemyRuleReader;
import lsd.io.LSDTyrubaFactReader;
import lsd.io.LSDTyrubaRuleChecker;
import lsd.rule.LSDBinding;
import lsd.rule.LSDFact;
import lsd.rule.LSDInvalidTypeException;
import lsd.rule.LSDLiteral;
import lsd.rule.LSDPredicate;
import lsd.rule.LSDRule;
import lsd.rule.LSDRule.LSDRuleComparator;
import lsd.rule.LSDVariable;
import metapackage.MetaInfo;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class LSDRuleEnumerator
{
  private LSDTyrubaRuleChecker ruleChecker;
  private LSDTyrubaRuleChecker remainingRuleChecker;
  private int minMatches = 1;
  private int minMatchesPerLiteral = 0;
  private int maxExceptions = 10;
  private double minAccuracy = 0.0D;
  private int beamSize = 100;
  private ArrayList<LSDFact> read2kbFacts = new ArrayList();
  private ArrayList<LSDFact> readDeltaFacts = new ArrayList();
  private ArrayList<LSDRule> winnowingRules = new ArrayList();
  private ArrayList<LSDFact> workingSet2KB = new ArrayList();
  private ArrayList<LSDFact> workingSetDeltaKB = new ArrayList();
  
  public static ArrayList<LSDPredicate> getUniquePredicates(Collection<LSDFact> facts, boolean antedecedent)
  {
    TreeSet<String> predNames = new TreeSet();
    LSDPredicate p;
    for (LSDFact f : facts)
    {
      p = f.getPredicate();
      predNames.add(p.getName());
    }
    ArrayList<LSDPredicate> preds = new ArrayList();
    for (String s : predNames)
    {
      LSDPredicate p = LSDPredicate.getPredicate(s);
      if ((antedecedent) && (p.isAntecedentPredicate())) {
        preds.add(p);
      } else {
        preds.add(p);
      }
    }
    return preds;
  }
  
  private ArrayList<LSDRule> modifiedWinnowingRules = new ArrayList();
  final LSdiffDistanceFactBase onDemand2KB;
  final LSdiffHierarchialDeltaKB onDemandDeltaKB;
  public int statsGeneratedPartials = 0;
  public int statsEnqueuedPartials = 0;
  public int statsSavedPartials = 0;
  int statsGeneratedGroundings = 0;
  int statsEnqueuedGroundings = 0;
  int statsSavedGroundings = 0;
  int statsPartialValidQueryCount = 0;
  int statsGroundingConstantsQueryCount = 0;
  int statsGroundingValidQueryCount = 0;
  int statsGroundingExceptionsQueryCount = 0;
  double timeUngroundRuleGeneration;
  double timePartiallyGroundRuleGeneration;
  int numValidRules;
  int numRulesWithException;
  int num2KBSize;
  int numDeltaKBSize;
  int numWinnowDeltaKBSize;
  int numRemainingDeltaKBSize;
  int numFinalRules;
  private long enumerationTimestamp = 0L;
  private LSDFactBase fb;
  static int varNum;
  public BufferedWriter output;
  private String resString;
  private int antecedantSize;
  private static final boolean isConclusion = true;
  private static final boolean isAntecedent = false;
  long timer = 0L;
  long lastStart = 0L;
  
  static
  {
    varNum = 0;
    
    tyRuBa.engine.RuleBase.silent = true;
  }
  
  public LSDRuleEnumerator(File twoKBFile, File deltaKBFile, File winnowingRulesFile, File resultsFile, int minConcFact, double accuracy, int k, int beamSize2, int maxException, File modifiedWinnowingRulesFile, BufferedWriter output)
    throws Exception
  {
    setMinMatchesPerLiteral(0);
    setMaxExceptions(maxException);
    setBeamSize(this.beamSize);
    setMinMatches(minConcFact);
    setMinAccuracy(accuracy);
    setAntecedentSize(k);
    this.output = output;
    this.fb = new LSDFactBase();
    
    startTimer();
    this.read2kbFacts = new LSDTyrubaFactReader(twoKBFile).getFacts();
    this.readDeltaFacts = new LSDTyrubaFactReader(deltaKBFile).getFacts();
    this.winnowingRules = new LSDAlchemyRuleReader(winnowingRulesFile)
      .getRules();
    
    this.onDemand2KB = new LSdiffDistanceFactBase(this.read2kbFacts, this.readDeltaFacts);
    this.onDemandDeltaKB = new LSdiffHierarchialDeltaKB(this.readDeltaFacts);
    
    this.modifiedWinnowingRules = new LSDAlchemyRuleReader(new File(
      MetaInfo.modifiedWinnowings)).getRules();
    stopTimer();
  }
  
  public LSDRuleEnumerator(ArrayList<LSDFact> input2kbFacts, ArrayList<LSDFact> inputDeltaFacts, int minConcFact, double accuracy, int k, int beamSize2, int maxException, BufferedWriter output)
    throws Exception
  {
    setMinMatchesPerLiteral(0);
    setMaxExceptions(maxException);
    setBeamSize(this.beamSize);
    setMinMatches(minConcFact);
    setMinAccuracy(accuracy);
    setAntecedentSize(k);
    this.output = output;
    this.fb = new LSDFactBase();
    
    startTimer();
    this.read2kbFacts = input2kbFacts;
    this.readDeltaFacts = inputDeltaFacts;
    
    this.onDemand2KB = new LSdiffDistanceFactBase(this.read2kbFacts, this.readDeltaFacts);
    this.onDemandDeltaKB = new LSdiffHierarchialDeltaKB(this.readDeltaFacts);
    
    this.modifiedWinnowingRules = new LSDAlchemyRuleReader(new File(
      MetaInfo.modifiedWinnowings)).getRules();
    stopTimer();
  }
  
  public void setAntecedentSize(int k)
  {
    this.antecedantSize = k;
  }
  
  public void setMinMatches(int minMatches)
  {
    this.minMatches = minMatches;
  }
  
  public void setMinMatchesPerLiteral(int minMatchesPerLiteral)
  {
    this.minMatchesPerLiteral = minMatchesPerLiteral;
  }
  
  public void setMaxExceptions(int maxExceptions)
  {
    this.maxExceptions = maxExceptions;
  }
  
  public void setMinAccuracy(double minAccuracy)
  {
    this.minAccuracy = minAccuracy;
  }
  
  public void setBeamSize(int beamSize)
  {
    this.beamSize = beamSize;
  }
  
  public void loadFactBases(int hopDistance2KB, LSdiffFilter filter)
    throws Exception
  {
    this.onDemand2KB.expand(hopDistance2KB);
    this.workingSet2KB = this.onDemand2KB.getWorking2KBFacts();
    
    TreeSet<LSDFact> workingDelta = new TreeSet();
    this.onDemandDeltaKB.filterFacts(null, workingDelta, filter);
    this.workingSetDeltaKB = new ArrayList(workingDelta);
    
    this.fb = new LSDFactBase();
    
    this.fb.load2KBFactBase(this.workingSet2KB);
    this.fb.loadDeltaKBFactBase(this.workingSetDeltaKB);
    this.fb.loadWinnowingRules(this.modifiedWinnowingRules);
    
    List<LSDFact> afterWinnowing = this.fb.getRemainingFacts(true);
    
    this.num2KBSize = this.fb.num2KBFactSize();
    this.numDeltaKBSize = this.fb.numDeltaKBFactSize();
    this.numWinnowDeltaKBSize = afterWinnowing.size();
    
    this.ruleChecker = createRuleChecker();
    this.remainingRuleChecker = createReducedRuleChecker(new ArrayList());
    
    System.out.println("Number of 2kbFacts: " + this.num2KBSize);
    System.out.println("Number of deltaFacts: " + this.numDeltaKBSize);
  }
  
  private void swapFactBase(TreeSet<LSDFact> delta)
    throws Exception
  {
    LSDTyrubaRuleChecker newRuleChecker = new LSDTyrubaRuleChecker();
    ArrayList<LSDFact> twoKB = this.workingSet2KB;
    ArrayList<LSDFact> deltaKB = new ArrayList(delta);
    this.workingSetDeltaKB = deltaKB;
    newRuleChecker.loadAdditionalDB(MetaInfo.included2kb);
    for (LSDFact fact : twoKB) {
      newRuleChecker.loadFact(fact);
    }
    newRuleChecker.loadAdditionalDB(MetaInfo.includedDelta);
    for (LSDFact fact : deltaKB) {
      newRuleChecker.loadFact(fact);
    }
    this.ruleChecker = newRuleChecker;
    this.remainingRuleChecker = createReducedRuleChecker(new ArrayList());
    System.out.println("[swapFactBase: Number of working 2kbFacts]\t: " + twoKB.size());
    System.out.println("[swapFactBase: Number of working deltaFacts]\t: " + delta.size());
  }
  
  private LSDTyrubaRuleChecker createRuleChecker()
    throws ParseException, TypeModeError, IOException
  {
    LSDTyrubaRuleChecker newRuleChecker = new LSDTyrubaRuleChecker();
    ArrayList<LSDFact> twoKB = this.workingSet2KB;
    ArrayList<LSDFact> deltaKB = this.workingSetDeltaKB;
    newRuleChecker.loadAdditionalDB(MetaInfo.included2kb);
    for (LSDFact fact : twoKB) {
      newRuleChecker.loadFact(fact);
    }
    newRuleChecker.loadAdditionalDB(MetaInfo.includedDelta);
    for (LSDFact fact : deltaKB) {
      newRuleChecker.loadFact(fact);
    }
    return newRuleChecker;
  }
  
  public LSDTyrubaRuleChecker createReducedRuleChecker(Collection<LSDRule> additionalRules)
    throws IOException, TypeModeError, ParseException
  {
    LSDTyrubaRuleChecker newRuleChecker = new LSDTyrubaRuleChecker();
    newRuleChecker.loadAdditionalDB(MetaInfo.included2kb);
    ArrayList<LSDFact> twoKB = this.workingSet2KB;
    ArrayList<LSDFact> deltaKB = this.workingSetDeltaKB;
    ArrayList<LSDRule> winnowing = this.modifiedWinnowingRules;
    for (LSDFact fact : twoKB) {
      newRuleChecker.loadFact(fact);
    }
    newRuleChecker.loadAdditionalDB(MetaInfo.includedDelta);
    LSDFactBase localFB = new LSDFactBase();
    localFB.load2KBFactBase(twoKB);
    localFB.loadDeltaKBFactBase(deltaKB);
    localFB.loadWinnowingRules(winnowing);
    localFB.loadWinnowingRules(additionalRules);
    
    Object afterWinnowing = localFB.getRemainingFacts(true);
    this.fb = localFB;
    this.num2KBSize = this.fb.num2KBFactSize();
    this.numDeltaKBSize = this.fb.numDeltaKBFactSize();
    this.numWinnowDeltaKBSize = ((List)afterWinnowing).size();
    for (LSDFact fact : (List)afterWinnowing) {
      newRuleChecker.loadFact(fact);
    }
    return newRuleChecker;
  }
  
  public LSDTyrubaRuleChecker createRuleChecker(ArrayList<String> cluster)
    throws IOException, TypeModeError, ParseException
  {
    LSDTyrubaRuleChecker newRuleChecker = new LSDTyrubaRuleChecker();
    newRuleChecker.loadAdditionalDB(MetaInfo.included2kb);
    Iterator localIterator2;
    for (Iterator localIterator1 = this.read2kbFacts.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDFact fact = (LSDFact)localIterator1.next();
      localIterator2 = cluster.iterator(); continue;String str = (String)localIterator2.next();
      if (fact.toString().contains(str)) {
        newRuleChecker.loadFact(fact);
      }
    }
    newRuleChecker.loadAdditionalDB(MetaInfo.includedDelta);
    for (localIterator1 = this.readDeltaFacts.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      LSDFact fact = (LSDFact)localIterator1.next();
      localIterator2 = cluster.iterator(); continue;String str = (String)localIterator2.next();
      if (fact.toString().contains(str)) {
        newRuleChecker.loadFact(fact);
      }
    }
    return newRuleChecker;
  }
  
  private void startTimer()
  {
    this.lastStart = new Date().getTime();
  }
  
  private void stopTimer()
  {
    long temp = new Date().getTime() - this.lastStart;
    this.timer += temp;
  }
  
  private LSDVariable newFreeVariable(Collection<LSDVariable> variables, char type)
  {
    Set<String> varNames = new HashSet();
    for (LSDVariable variable : variables) {
      varNames.add(variable.getName());
    }
    for (int i = 0; varNames.contains("x" + i); i++) {}
    return new LSDVariable("x" + i, type);
  }
  
  private double nextEnumerationTiming()
  {
    long nowTime = new Date().getTime();
    double delta = (nowTime - this.enumerationTimestamp) / 1000.0D;
    this.enumerationTimestamp = nowTime;
    return delta;
  }
  
  private List<LSDRule> groundRule(LSDRule ungroundedRule)
  {
    ArrayList<LSDRule> rules = new ArrayList();
    
    Stack<Grounding> groundings = new Stack();
    
    groundings.add(new Grounding(ungroundedRule));
    this.statsEnqueuedGroundings += 1;
    this.statsGeneratedGroundings += 1;
    while (!groundings.isEmpty())
    {
      Grounding grounding = (Grounding)groundings.pop();
      LSDVariable variable = 
        (LSDVariable)grounding.remainingVariables.iterator().next();
      
      startTimer();
      Set<String> constants = this.ruleChecker.getReplacementConstants(
        grounding.rule, variable);
      this.statsGroundingConstantsQueryCount += 1;
      constants.add(null);
      for (String constant : constants) {
        if ((constant == null) || 
          (!grounding.usedConstants.contains(constant))) {
          if ((constant == null) || (constant.indexOf("java.") <= 0))
          {
            Grounding newGrounding = grounding.addGrounding(variable, 
              constant);
            this.statsGeneratedGroundings += 1;
            if (!newGrounding.rule.containsFacts())
            {
              int minMatchesByLength = this.minMatchesPerLiteral * (
                newGrounding.rule.getLiterals().size() - 1);
              startTimer();
              int numMatches = countRemainingMatches(newGrounding.rule);
              this.statsGroundingValidQueryCount += 1;
              if ((numMatches >= this.minMatches) && (numMatches >= minMatchesByLength)) {
                if (newGrounding.remainingVariables.size() > 0)
                {
                  if (newGrounding.scanned)
                  {
                    if ((newGrounding.isGrounded()) && (newGrounding.scanned) && 
                      (newGrounding.rule.isValid())) {
                      rules = addRule(rules, grounding, 
                        grounding.numMatches);
                    } else if (grounding.scanned) {
                      break;
                    }
                  }
                  else
                  {
                    this.statsEnqueuedGroundings += 1;
                    newGrounding.scanned = true;
                    newGrounding.numMatches = numMatches;
                    groundings.add(newGrounding);
                  }
                }
                else if ((newGrounding.rule.isValid()) && 
                  (newGrounding.isGrounded())) {
                  addRule(rules, newGrounding, numMatches);
                }
              }
            }
          }
        }
      }
    }
    return rules;
  }
  
  private List<LSDRule> groundRules(List<LSDRule> ungroundedRules)
  {
    ArrayList<LSDRule> rules = new ArrayList();
    
    int rulesGrounded = 0;
    for (LSDRule ungroundedRule : ungroundedRules)
    {
      if (rulesGrounded % 10 == 0)
      {
        System.err.println(rulesGrounded * 100 / ungroundedRules
          .size() + 
          "% done.");
        System.err.flush();
      }
      rules.addAll(groundRule(ungroundedRule));
      rulesGrounded++;
    }
    return rules;
  }
  
  private List<LSDRule> extendUngroundedRules(List<LSDRule> oldPartialRules, List<LSDRule> newPartialRules)
  {
    Set<LSDRule> ungroundedRules = new LinkedHashSet();
    
    List<LSDPredicate> predicates = getUniquePredicates(this.workingSet2KB, true);
    System.out.println("[extendUngroundRules: predicates to add]\t" + predicates);
    for (Iterator localIterator1 = oldPartialRules.iterator(); localIterator1.hasNext(); ???.hasNext())
    {
      LSDRule partialRule = (LSDRule)localIterator1.next();
      List<LSDLiteral> previousLiterals = partialRule.getLiterals();
      LSDPredicate conclusionPredicate = 
        ((LSDLiteral)partialRule.getConclusions().getLiterals().get(0)).getPredicate();
      Set<Character> currentTypes = new HashSet();
      for (LSDVariable variable : partialRule.getFreeVariables()) {
        currentTypes.add(Character.valueOf(variable.getType()));
      }
      ??? = predicates.iterator(); continue;LSDPredicate predicate = (LSDPredicate)???.next();
      
      LSDPredicate antecedant = null;
      if ((partialRule.getAntecedents() != null) && 
        (partialRule.getAntecedents().getLiterals().size() > 0)) {
        antecedant = 
          ((LSDLiteral)partialRule.getAntecedents().getLiterals().get(0)).getPredicate();
      }
      if (predicate.allowedInSameRule(conclusionPredicate, 
        antecedant)) {
        if (predicate.typeMatches(currentTypes))
        {
          List<List<LSDBinding>> bindingsList = enumerateUngroundedBindings(
            partialRule, predicate);
          for (List<LSDBinding> bindings : bindingsList)
          {
            this.statsGeneratedPartials += 1;
            LSDLiteral newLiteral = null;
            try
            {
              newLiteral = new LSDLiteral(predicate, bindings, 
                false);
            }
            catch (LSDInvalidTypeException localLSDInvalidTypeException)
            {
              System.err.println("We're taking types directly from the predicates, so we should never have this type error.");
              System.exit(-7);
            }
            for (LSDLiteral oldLiteral : previousLiterals) {
              if (oldLiteral.identifiesSameIgnoringNegation(newLiteral)) {
                break;
              }
            }
            LSDRule newPartialRule = new LSDRule(partialRule);
            newPartialRule.addLiteral(newLiteral);
            if ((newPartialRule.literalsLinked()) && 
              (newPartialRule.hasValidLinks()))
            {
              int minMatchesByLength = this.minMatchesPerLiteral * (
                newPartialRule.getLiterals().size() - 1);
              startTimer();
              int numMatches = countRemainingMatches(newPartialRule, 
                Math.max(this.minMatches, minMatchesByLength));
              this.statsPartialValidQueryCount += 1;
              if ((numMatches >= this.minMatches) && 
                (numMatches >= minMatchesByLength))
              {
                this.statsSavedPartials += 1;
                ungroundedRules.add(newPartialRule);
                this.statsEnqueuedPartials += 1;
                newPartialRules.add(newPartialRule);
              }
            }
          }
        }
      }
    }
    return new ArrayList(ungroundedRules);
  }
  
  private List<LSDRule> narrowSearch(List<LSDRule> partialRules, int currentLength)
  {
    ArrayList<LSDRule> chosenRules = new ArrayList();
    ArrayList<LSDRule> sortedRules = sortRules(partialRules);
    int max = Math.min(this.beamSize, sortedRules.size());
    for (int i = 0; i < max; i++) {
      chosenRules.add((LSDRule)sortedRules.get(i));
    }
    return chosenRules;
  }
  
  public List<LSDFact> getRelevantFacts(LSDRule rule)
  {
    return this.fb.getRelevantFacts(rule);
  }
  
  public List<Map<LSDVariable, String>> getExceptions(LSDRule rule)
  {
    return this.fb.getExceptions(rule);
  }
  
  public List<LSDRule> levelIncrementLearning(PrintStream result)
  {
    List<LSDRule> rules = null;
    try
    {
      for (int level = 0; level <= 3; level++)
      {
        if (level == 0) {
          loadFactBases(1, new LSdiffFilter(true, false, false, false, false));
        }
        TreeSet<LSDFact> workingDeltaKB = this.onDemandDeltaKB
          .expandCluster(null, level);
        swapFactBase(workingDeltaKB);
        
        rules = enumerateRules(1);
        if (rules != null)
        {
          this.fb.loadWinnowingRules(rules);
          this.fb.forceWinnowing();
        }
        int cnt = 0;
        Iterator localIterator2;
        for (Iterator localIterator1 = rules.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
        {
          LSDRule r = (LSDRule)localIterator1.next();
          result.println(r.toString());
          int matches = countMatches(r);
          int exceptions = countExceptions(r);
          if (exceptions > 0) {
            this.numRulesWithException += 1;
          }
          result.println("#" + cnt++ + "\t(" + matches + "/" + (matches + exceptions) + ")");
          result.println(r);
          localIterator2 = this.fb.getRelevantFacts(r).iterator(); continue;LSDFact pfact = (LSDFact)localIterator2.next();
          result.println("#P:\t" + pfact);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return rules;
  }
  
  public List<LSDRule> levelIncrementLearning2()
  {
    List<LSDRule> packageLevelRules = null;
    List<LSDRule> typeLevelRules = null;
    List<LSDRule> typeDependencyLevelRules = null;
    List<LSDRule> methodLevelRules = null;
    List<LSDRule> methodBodyLevelRules = null;
    List<LSDRule> fieldLevelRules = null;
    try
    {
      for (int level = 0; level <= 5; level++)
      {
        if (level == 0) {
          loadFactBases(1, new LSdiffFilter(true, false, false, false, false));
        }
        TreeSet<LSDFact> workingDeltaKB = this.onDemandDeltaKB
          .expandCluster(null, level);
        switch (level)
        {
        case 0: 
          System.out.println("**PACKAGE_LEVEL**");
          packageLevelRules = enumerateRules(1);
          this.fb.loadWinnowingRules(packageLevelRules);
          break;
        case 1: 
          System.out.println("**TYPE_LEVEL**");
          assert (packageLevelRules != null);
          typeLevelRules = extendPreviouslyLearnedRules(packageLevelRules);
          this.fb.loadWinnowingRules(typeLevelRules);
          break;
        case 2: 
          System.out.println("**TYPE_DEP_LEVEL**");
          
          assert (typeLevelRules != null);
          typeDependencyLevelRules = extendPreviouslyLearnedRules(typeLevelRules);
          this.fb.loadWinnowingRules(typeDependencyLevelRules);
          break;
        case 3: 
          System.out.println("**METHOD_LEVEL**");
          
          methodLevelRules = extendPreviouslyLearnedRules(typeLevelRules);
          this.fb.loadWinnowingRules(methodLevelRules);
          break;
        case 4: 
          System.out.println("**FIELD_LEVEL**");
          
          fieldLevelRules = extendPreviouslyLearnedRules(typeLevelRules);
          this.fb.loadWinnowingRules(fieldLevelRules);
          break;
        case 5: 
          System.out.println("**BODY_LEVEL**");
          
          methodBodyLevelRules = extendPreviouslyLearnedRules(methodLevelRules);
          this.fb.loadWinnowingRules(methodBodyLevelRules);
          break;
        default: 
          if (!$assertionsDisabled) {
            throw new AssertionError();
          }
          break;
        }
        this.fb.forceWinnowing();
        
        swapFactBase(workingDeltaKB);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public List<LSDRule> onDemandLearning(List<LSDFact> cluster, int level)
  {
    try
    {
      TreeSet<LSDFact> nextLevelWorkingDeltaKB = null;
      if (level == 0)
      {
        this.onDemandDeltaKB.expandCluster(null, level);
      }
      else
      {
        if (level > 5) {
          return null;
        }
        this.onDemandDeltaKB.expandCluster(cluster, level);
      }
      swapFactBase(nextLevelWorkingDeltaKB);
      List<LSDRule> rules = enumerateRules(1);
      if (rules != null)
      {
        this.fb.loadWinnowingRules(rules);
        this.fb.forceWinnowing();
      }
      System.err.println("Found Rules:" + rules.size());
      this.numValidRules = rules.size();
      List<LSDFact> factUncoveredByRules = this.fb.getRemainingFacts(true);
      this.numRemainingDeltaKBSize = factUncoveredByRules.size();
      
      int cnt = 0;
      Iterator localIterator2;
      for (Iterator localIterator1 = rules.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
      {
        LSDRule r = (LSDRule)localIterator1.next();
        System.err.println(r.toString());
        int matches = countMatches(r);
        int exceptions = countExceptions(r);
        if (exceptions > 0) {
          this.numRulesWithException += 1;
        }
        System.err.println("#" + cnt++ + "\t(" + matches + "/" + (matches + exceptions) + ")");
        System.err.println(r);
        localIterator2 = this.fb.getRelevantFacts(r).iterator(); continue;LSDFact pfact = (LSDFact)localIterator2.next();
        System.err.println("#P:\t" + pfact);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return null;
  }
  
  public void bruteForceLearning(int hopDistance2KB, LSdiffFilter filter)
  {
    try
    {
      long st = new Date().getTime();
      
      loadFactBases(hopDistance2KB, filter);
      
      List<LSDRule> rules = enumerateRules(this.antecedantSize);
      this.numValidRules = rules.size();
      
      this.fb.loadWinnowingRules(rules);
      this.fb.forceWinnowing();
      
      List<LSDFact> remainingFacts = this.fb.getRemainingFacts(true);
      this.numRemainingDeltaKBSize = remainingFacts.size();
      
      System.err.println("Found Rules:" + rules.size());
      int cnt = 1;
      int matches;
      Iterator localIterator2;
      for (Iterator localIterator1 = rules.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
      {
        LSDRule r = (LSDRule)localIterator1.next();
        
        System.err.println(r.toString());
        matches = countMatches(r);
        int exceptions = countExceptions(r);
        if (exceptions > 0) {
          this.numRulesWithException += 1;
        }
        System.err.println("#" + cnt++ + "\t(" + matches + "/" + (matches + exceptions) + ")");
        System.err.println(r);
        localIterator2 = this.fb.getRelevantFacts(r).iterator(); continue;LSDFact pfact = (LSDFact)localIterator2.next();
        System.err.println("#P:\t" + pfact);
      }
      Collection<LSDRule> selectedSubset = coverSet(rules, true, null);
      
      System.err.println("Selected Rules:" + selectedSubset.size());
      for (LSDRule r : selectedSubset) {
        System.err.println(r.toString());
      }
      System.err.println("Remaining Facts:" + remainingFacts.size());
      for (LSDFact f : remainingFacts) {
        System.err.print(f);
      }
      int cInfo = counttextual(selectedSubset);
      long en = new Date().getTime();
      this.output.write(Double.valueOf(en - st).doubleValue() / 1000.0D + " \t " + 
        rules.size() + " \t " + selectedSubset.size() + " \t " + 
        this.numRemainingDeltaKBSize + " \t " + this.resString + cInfo);
      this.output.newLine();
      
      shutdown();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    System.out.println("Done");
  }
  
  Collection<LSDRule> coverSet(Collection<LSDRule> rules, boolean print, File rf)
    throws TypeModeError, FileNotFoundException, ParseException, IOException
  {
    List<LSDRule> chosenRules = new ArrayList();
    List<LSDRule> remainingRules = new ArrayList(rules);
    
    HashMap<LSDRule, Integer> alreadyFoundExceptionCounts = new HashMap();
    int startingNumFacts = -1;
    List<LSDFact> remainingFacts;
    do
    {
      LSDFactBase fb = new LSDFactBase();
      LSDRule bestRule = null;
      int bestCount = 0;
      fb.load2KBFactBase(this.workingSet2KB);
      fb.loadDeltaKBFactBase(this.workingSetDeltaKB);
      fb.loadWinnowingRules(this.modifiedWinnowingRules);
      fb.loadWinnowingRules(chosenRules);
      remainingFacts = fb.getRemainingFacts(true);
      if (startingNumFacts == -1) {
        startingNumFacts = remainingFacts.size();
      }
      fb.loadWinnowingRules(remainingRules);
      for (Iterator<LSDRule> i = remainingRules.iterator(); i.hasNext();)
      {
        LSDRule rule = (LSDRule)i.next();
        List<LSDFact> facts = fb.getRelevantFacts(rule);
        facts.retainAll(remainingFacts);
        int count = facts.size();
        if (count == 0)
        {
          i.remove();
        }
        else if (count > bestCount)
        {
          bestCount = count;
          bestRule = rule;
        }
        else if (count == bestCount)
        {
          Integer preFound = 
            (Integer)alreadyFoundExceptionCounts.get(bestRule);
          int exBestRule;
          if (preFound == null)
          {
            int exBestRule = countExceptions(bestRule);
            alreadyFoundExceptionCounts.put(bestRule, Integer.valueOf(exBestRule));
          }
          else
          {
            exBestRule = preFound.intValue();
          }
          preFound = (Integer)alreadyFoundExceptionCounts.get(bestRule);
          int exRule;
          if (preFound == null)
          {
            int exRule = countExceptions(rule);
            alreadyFoundExceptionCounts.put(rule, Integer.valueOf(exRule));
          }
          else
          {
            exRule = preFound.intValue();
          }
          if (exBestRule > exRule) {
            bestRule = rule;
          } else if (rule.generalityCompare(bestRule) < 0) {
            bestRule = rule;
          }
        }
      }
      if (bestRule != null)
      {
        chosenRules.add(bestRule);
        remainingRules.remove(bestRule);
        remainingFacts.removeAll(fb.getRelevantFacts(bestRule));
      }
    } while ((!remainingFacts.isEmpty()) && (!remainingRules.isEmpty()));
    LSDFactBase fb = new LSDFactBase();
    if (print)
    {
      fb.load2KBFactBase(this.workingSet2KB);
      fb.loadDeltaKBFactBase(this.workingSetDeltaKB);
      fb.loadWinnowingRules(this.modifiedWinnowingRules);
      fb.loadWinnowingRules(chosenRules);
      fb.forceWinnowing();
    }
    double coverage = Double.valueOf(startingNumFacts - remainingFacts
      .size()).doubleValue() / 
      Double.valueOf(startingNumFacts).doubleValue();
    double conciseness = Double.valueOf(startingNumFacts).doubleValue() / 
      Double.valueOf(chosenRules.size() + remainingFacts.size()).doubleValue();
    this.resString = 
      (startingNumFacts + " \t " + coverage + " \t " + conciseness + " \t ");
    
    return chosenRules;
  }
  
  private ArrayList<LSDRule> addRule(ArrayList<LSDRule> rules, Grounding grounding, int numMatches)
  {
    double accuracy = measureAccuracy(grounding.rule, this.minAccuracy, 
      this.maxExceptions, numMatches);
    this.statsGroundingExceptionsQueryCount += 1;
    if (accuracy >= this.minAccuracy)
    {
      this.statsSavedGroundings += 1;
      grounding.rule.setAccuracy(accuracy);
      grounding.rule.setNumMatches(numMatches);
      grounding.rule.setScore();
      rules.add(grounding.rule);
    }
    return rules;
  }
  
  List<List<LSDBinding>> enumerateUngroundedBindings(LSDRule partialRule, LSDPredicate predicate)
  {
    List<List<LSDBinding>> bindingsList = new ArrayList();
    bindingsList.add(new ArrayList());
    Set<LSDVariable> ruleFreeVars = new HashSet(partialRule
      .getFreeVariables());
    char[] arrayOfChar;
    int j = (arrayOfChar = predicate.getTypes()).length;
    List<List<LSDBinding>> newBindingsList;
    for (int i = 0; i < j; i++)
    {
      char type = arrayOfChar[i];
      newBindingsList = new ArrayList();
      for (Iterator localIterator1 = bindingsList.iterator(); localIterator1.hasNext(); ???.hasNext())
      {
        List<LSDBinding> prevBindings = (List)localIterator1.next();
        
        Set<LSDVariable> freeVariables = new HashSet(
          ruleFreeVars);
        for (LSDBinding b : prevBindings) {
          freeVariables.add(b.getVariable());
        }
        List<LSDVariable> variableChoices = new ArrayList();
        for (LSDVariable v : freeVariables) {
          if (v.getType() == type) {
            variableChoices.add(v);
          }
        }
        variableChoices.add(newFreeVariable(freeVariables, type));
        
        ??? = variableChoices.iterator(); continue;LSDVariable nextVariable = (LSDVariable)???.next();
        
        ArrayList<LSDBinding> newBindings = new ArrayList(
          prevBindings);
        newBindings.add(new LSDBinding(nextVariable));
        newBindingsList.add(newBindings);
      }
      bindingsList = newBindingsList;
    }
    for (Iterator<List<LSDBinding>> i = bindingsList.iterator(); i
          .hasNext();)
    {
      Object bindings = (List)i.next();
      boolean linked = false;
      for (LSDBinding b : (List)bindings) {
        if (ruleFreeVars.contains(b.getVariable()))
        {
          linked = true;
          break;
        }
      }
      if (!linked) {
        i.remove();
      }
    }
    return bindingsList;
  }
  
  List<List<LSDBinding>> enumerateUngroundedBindings(LSDRule partialRule, LSDLiteral literal)
  {
    List<List<LSDBinding>> bindingsList = new ArrayList();
    bindingsList.add(new ArrayList());
    Set<LSDVariable> ruleFreeVars = new HashSet(partialRule
      .getFreeVariables());
    Iterator localIterator2;
    for (LSDBinding binding : literal.getBindings()) {
      if (!binding.isBound())
      {
        List<List<LSDBinding>> newBindingsList = new ArrayList();
        for (localIterator2 = bindingsList.iterator(); localIterator2.hasNext(); ???.hasNext())
        {
          List<LSDBinding> prevBindings = (List)localIterator2.next();
          
          Set<LSDVariable> freeVariables = new HashSet(
            ruleFreeVars);
          for (LSDBinding b : prevBindings) {
            freeVariables.add(b.getVariable());
          }
          List<LSDVariable> variableChoices = new ArrayList();
          for (LSDVariable v : freeVariables) {
            if (v.getType() == binding.getType()) {
              variableChoices.add(v);
            }
          }
          variableChoices.add(newFreeVariable(freeVariables, binding
            .getType()));
          
          ??? = variableChoices.iterator(); continue;LSDVariable nextVariable = (LSDVariable)???.next();
          
          ArrayList<LSDBinding> newBindings = new ArrayList(
            prevBindings);
          newBindings.add(new LSDBinding(nextVariable));
          newBindingsList.add(newBindings);
        }
        bindingsList = newBindingsList;
      }
    }
    for (Iterator<List<LSDBinding>> i = bindingsList.iterator(); i
          .hasNext();)
    {
      Object bindings = (List)i.next();
      boolean linked = false;
      for (LSDBinding b : (List)bindings) {
        if (ruleFreeVars.contains(b.getVariable()))
        {
          linked = true;
          break;
        }
      }
      if (!linked) {
        i.remove();
      }
    }
    return bindingsList;
  }
  
  int countRemainingMatches(LSDRule rule)
  {
    return this.remainingRuleChecker.countTrueConclusions(rule);
  }
  
  int countRemainingMatches(LSDRule rule, int i)
  {
    return this.remainingRuleChecker.countTrueConclusions(rule, i);
  }
  
  public int countMatches(LSDRule rule)
  {
    return this.ruleChecker.countTrueConclusions(rule);
  }
  
  public int countExceptions(LSDRule rule)
  {
    return this.ruleChecker.countCounterExamples(rule);
  }
  
  int countExceptions(LSDRule rule, int max)
  {
    return this.ruleChecker.countCounterExamples(rule, max);
  }
  
  double measureAccuracy(LSDRule rule, double min, int maxExceptions, double matches)
  {
    int accuracyMaxExceptions = 
      (int)Math.floor(matches / min - matches) + 1;
    double exceptions = countExceptions(rule, Math.min(maxExceptions, 
      accuracyMaxExceptions));
    if (exceptions >= maxExceptions) {
      return 0.0D;
    }
    return matches / (matches + exceptions);
  }
  
  public void shutdown()
  {
    this.ruleChecker.shutdown();
  }
  
  protected class Grounding
  {
    public int numMatches;
    public boolean scanned = false;
    public Set<LSDVariable> remainingVariables;
    public Set<String> usedConstants = new HashSet();
    public LSDRule rule;
    
    public Grounding(LSDRule rule)
    {
      this.remainingVariables = new LinkedHashSet(rule
        .getFreeVariables());
      this.rule = rule;
    }
    
    public boolean isGrounded()
    {
      ArrayList<LSDLiteral> literalsList = this.rule.getLiterals();
      Iterator localIterator2;
      for (Iterator localIterator1 = literalsList.iterator(); localIterator1.hasNext(); localIterator2.hasNext())
      {
        LSDLiteral literal = (LSDLiteral)localIterator1.next();
        List<LSDBinding> bindingsList = literal.getBindings();
        localIterator2 = bindingsList.iterator(); continue;LSDBinding binding = (LSDBinding)localIterator2.next();
        if (binding.getGroundConst() != null) {
          return true;
        }
      }
      return false;
    }
    
    public Grounding(Grounding oldGrounding)
    {
      this.remainingVariables = new HashSet(
        oldGrounding.remainingVariables);
      this.usedConstants = new HashSet(oldGrounding.usedConstants);
      this.rule = oldGrounding.rule;
    }
    
    public Grounding addGrounding(LSDVariable variable, String constant)
    {
      Grounding newGrounding = new Grounding(LSDRuleEnumerator.this, this);
      
      assert (this.remainingVariables.contains(variable)) : 
        ("Error: " + this.remainingVariables + " doesn't contain " + variable);
      newGrounding.remainingVariables.remove(variable);
      if (constant != null)
      {
        assert (!this.usedConstants.contains(constant));
        newGrounding.remainingVariables.remove(variable);
        newGrounding.usedConstants.add(constant);
        try
        {
          newGrounding.rule = this.rule.substitute(variable, 
            new LSDBinding(constant));
        }
        catch (LSDInvalidTypeException localLSDInvalidTypeException)
        {
          System.err.println("We're dealing with consts, so why type mismatch?");
          System.exit(-15);
        }
      }
      return newGrounding;
    }
  }
  
  private void LogData(File rf, List<LSDFact> remainingFacts, List<LSDRule> chosenRules, int startingNumFacts)
    throws IOException
  {
    BufferedWriter output = new BufferedWriter(new FileWriter(rf));
    if (!remainingFacts.isEmpty())
    {
      output.write("The following facts (" + remainingFacts.size() + "/" + 
        startingNumFacts + ")were not matched by any rule:");
      output.newLine();
      for (LSDFact fact : remainingFacts)
      {
        output.write("\t" + fact);
        output.newLine();
      }
    }
    else
    {
      output.write("Complete coverage.");
    }
    output.newLine();
    for (LSDRule rule : chosenRules)
    {
      int matches = rule.getNumMatches();
      int exceptions = countExceptions(rule);
      output.newLine();
      output.write("\t" + rule + "\t(" + matches + "/" + (
        matches + exceptions) + ")");
      output.newLine();
      for (LSDFact pfact : this.fb.getRelevantFacts(rule))
      {
        output.write("\t#P:\t" + pfact);
        output.newLine();
      }
      if (exceptions > 0)
      {
        output.write("\t    Except:");
        output.newLine();
        
        ??? = this.fb.getExceptions(rule).iterator();
        while (???.hasNext())
        {
          Map<LSDVariable, String> exception = (Map)???.next();
          output.newLine();
          output.write("\t\t(");
          boolean first = true;
          for (LSDVariable var : exception.keySet())
          {
            output.write((first ? "" : ", ") + var + "=" + 
              (String)exception.get(var));
            first = false;
          }
          output.write(")");
        }
        output.write("");
        output.newLine();
      }
    }
    output.close();
  }
  
  private int counttextual(Collection<LSDRule> selectedSubset)
  {
    int count = 0;
    for (LSDRule rule : selectedSubset)
    {
      Iterator localIterator3;
      for (Iterator localIterator2 = rule.getLiterals().iterator(); localIterator2.hasNext(); localIterator3.hasNext())
      {
        LSDLiteral literal = (LSDLiteral)localIterator2.next();
        localIterator3 = literal.getBindings().iterator(); continue;LSDBinding bind = (LSDBinding)localIterator3.next();
        int i = 0;
        if (bind.isBound()) {
          for (LSDFact delta : this.readDeltaFacts)
          {
            if (delta.toString().contains(bind.getGroundConst())) {
              break;
            }
            i++;
          }
        }
        if (i == this.readDeltaFacts.size())
        {
          count++;
          break;
        }
      }
    }
    return count;
  }
  
  private ArrayList<LSDRule> sortRules(List<LSDRule> rules)
  {
    LSDRule[] temp = new LSDRule[rules.size()];
    for (int i = 0; i < temp.length; i++) {
      temp[i] = ((LSDRule)rules.get(i));
    }
    void tmp49_46 = new LSDRule();tmp49_46.getClass();Arrays.sort(temp, new LSDRule.LSDRuleComparator(tmp49_46));
    ArrayList<LSDRule> sortedList = new ArrayList();
    LSDRule[] arrayOfLSDRule1;
    int j = (arrayOfLSDRule1 = temp).length;
    for (int i = 0; i < j; i++)
    {
      LSDRule rule = arrayOfLSDRule1[i];
      sortedList.add(rule);
    }
    return sortedList;
  }
  
  public List<LSDRule> enumerateRules(int maxLiterals)
  {
    List<LSDRule> rules = new ArrayList();
    List<LSDRule> partialRules = new ArrayList(
      enumerateConclusions());
    this.statsGeneratedPartials += partialRules.size();
    this.statsEnqueuedPartials += partialRules.size();
    for (int currentLength = 1; currentLength <= maxLiterals; currentLength++)
    {
      System.out.println("Finding rules of length " + currentLength);
      List<LSDRule> newPartialRules = new ArrayList();
      nextEnumerationTiming();
      List<LSDRule> ungroundedRules = extendUngroundedRules(partialRules, 
        newPartialRules);
      double iterationTimeUngroundRuleGeneration = nextEnumerationTiming();
      this.timeUngroundRuleGeneration += iterationTimeUngroundRuleGeneration;
      
      System.out.println("Ungrounded rules, length " + currentLength + 
        ": " + iterationTimeUngroundRuleGeneration + " s");
      System.out.println("Total ungrounded rules generated: " + ungroundedRules.size());
      partialRules = newPartialRules;
      rules.addAll(groundRules(ungroundedRules));
      
      double iterationTimePartiallyGroundRuleGeneration = nextEnumerationTiming();
      this.timePartiallyGroundRuleGeneration += iterationTimePartiallyGroundRuleGeneration;
      System.out.println("Rule grounding, length " + currentLength + ": " + 
        iterationTimePartiallyGroundRuleGeneration + " s");
      System.out.println("Total grounded rules generated: " + 
        rules.size() + " rules");
      if (currentLength == maxLiterals) {
        break;
      }
      try
      {
        this.remainingRuleChecker.shutdown();
        this.remainingRuleChecker = createReducedRuleChecker(rules);
      }
      catch (Exception e)
      {
        System.out.println(e);
        e.printStackTrace();
      }
      System.out.println("Creating new rule checker: " + 
        nextEnumerationTiming() + " s");
      System.out.println("Enqueued partial rules: " + partialRules.size() + 
        " rules");
      
      partialRules = narrowSearch(newPartialRules, currentLength);
      System.out.println("Reduced enqueued partial rules: " + 
        partialRules.size() + " rules");
      System.out.println("Reducing partial rule set: " + 
        nextEnumerationTiming() + " s");
      if (partialRules.size() == 0) {
        break;
      }
    }
    return rules;
  }
  
  public List<LSDRule> extendPreviouslyLearnedRules(List<LSDRule> previouslyLearnedRules)
  {
    List<LSDRule> outcome = new ArrayList();
    List<LSDRule> combineNewConsequentToPreviouslyLearnedRules = new ArrayList();
    if (previouslyLearnedRules.size() > 0)
    {
      Iterator localIterator2;
      for (Iterator localIterator1 = getUniquePredicates(this.workingSetDeltaKB, false).iterator(); localIterator1.hasNext(); localIterator2.hasNext())
      {
        LSDPredicate newConsequentPred = (LSDPredicate)localIterator1.next();
        
        localIterator2 = previouslyLearnedRules.iterator(); continue;LSDRule previousRule = (LSDRule)localIterator2.next();
        
        LSDRule previousAntecedents = previousRule.getAntecedents();
        
        List<List<LSDBinding>> potentialBindingsForNewConsequentPredicate = enumerateUngroundedBindings(
          previousAntecedents, newConsequentPred);
        for (List<LSDBinding> bindingsForNewConsequent : potentialBindingsForNewConsequentPredicate)
        {
          LSDLiteral newConsequentLiteral = null;
          try
          {
            newConsequentLiteral = new LSDLiteral(newConsequentPred, bindingsForNewConsequent, 
              true);
          }
          catch (LSDInvalidTypeException localLSDInvalidTypeException)
          {
            System.err.println("We're taking types directly from the predicates, so we should never have this type error.");
            System.exit(-7);
          }
          LSDRule previousAntecedentsNewConsequent = new LSDRule(previousAntecedents);
          previousAntecedentsNewConsequent.addLiteral(newConsequentLiteral);
          
          combineNewConsequentToPreviouslyLearnedRules.add(previousAntecedentsNewConsequent);
        }
      }
    }
    else
    {
      combineNewConsequentToPreviouslyLearnedRules = enumerateConclusions();
    }
    System.out.println("[# combineNewConsequentToPreviouslyLearnedRules]:\t" + combineNewConsequentToPreviouslyLearnedRules.size());
    
    List<LSDRule> newPartialRules = new ArrayList();
    Object ungroundedRules = extendUngroundedRules(
      combineNewConsequentToPreviouslyLearnedRules, newPartialRules);
    
    System.out.println("[# ungroundedRules]:\t" + ((List)ungroundedRules).size());
    
    List<LSDRule> groundRules = groundRules((List)ungroundedRules);
    System.out.println("[# groundRules]:\t" + groundRules.size());
    
    outcome.addAll(groundRules);
    try
    {
      this.remainingRuleChecker.shutdown();
      this.remainingRuleChecker = createReducedRuleChecker(outcome);
    }
    catch (Exception e)
    {
      System.out.println(e);
      e.printStackTrace();
    }
    return outcome;
  }
  
  private List<LSDRule> enumerateConclusions()
  {
    List<LSDRule> conclusions = new ArrayList();
    
    System.out.println("[enumerateConclusion: getUniquePredicates]:\t" + getUniquePredicates(this.workingSetDeltaKB, false));
    for (LSDPredicate predicate : getUniquePredicates(this.workingSetDeltaKB, false))
    {
      ArrayList<LSDBinding> bindings = new ArrayList();
      ArrayList<LSDVariable> variables = new ArrayList();
      char[] arrayOfChar;
      int j = (arrayOfChar = predicate.getTypes()).length;
      for (int i = 0; i < j; i++)
      {
        char type = arrayOfChar[i];
        LSDVariable nextVar = newFreeVariable(variables, type);
        variables.add(nextVar);
        bindings.add(new LSDBinding(nextVar));
      }
      LSDRule rule = new LSDRule();
      try
      {
        rule.addLiteral(new LSDLiteral(predicate, bindings, 
          true));
      }
      catch (LSDInvalidTypeException localLSDInvalidTypeException)
      {
        System.err.println("We're taking types directly from the predicates, so we should never have this type error.");
        System.exit(-7);
      }
      startTimer();
      int numMatches = countRemainingMatches(rule, this.minMatches);
      this.statsPartialValidQueryCount += 1;
      if (numMatches >= this.minMatches) {
        conclusions.add(rule);
      }
    }
    return conclusions;
  }
}
