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
package lsdSimplified;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import lsd.facts.LSDFactBase;
import lsd.io.LSDAlchemyRuleReader;
import lsd.io.LSDTyrubaFactReader;
import lsd.rule.LSDFact;
import lsd.rule.LSDPredicate;
import lsd.rule.LSDRule;

public class LSdiffOutputExaminer
{
  LSDFactBase localFB = new LSDFactBase();
  ArrayList<LSDRule> lsdiffRules = new ArrayList();
  File winnowingRulesFile = new File("input/winnowingRules.rub");
  
  public static void main(String[] args)
  {
    String oldVersion = "0.9.10";
    String newVersion = "0.9.11";
    File twoKBFile = new File(
      "/Volumes/gorillaHD2/LSdiff/Tyruba/lsd/jfreechart/" + oldVersion + "_" + newVersion + "2KB.rub");
    File deltaKBFile = new File("/Volumes/gorillaHD2/LSdiff/Tyruba/lsd/jfreechart/" + oldVersion + "_" + newVersion + "delta.rub");
    
    LSdiffOutputExaminer lsdiffExaminer = new LSdiffOutputExaminer(twoKBFile, deltaKBFile, null);
    lsdiffExaminer.compute_LSdiff_FACTTYPE();
    lsdiffExaminer.print(System.out);
  }
  
  public LSdiffOutputExaminer(File twoKBFile, File deltaKBFile, File lsdiffRuleFile)
  {
    try
    {
      if (twoKBFile != null)
      {
        ArrayList<LSDFact> twoKB = new LSDTyrubaFactReader(twoKBFile).getFacts();
        this.localFB.load2KBFactBase(twoKB);
      }
      if (deltaKBFile != null)
      {
        ArrayList<LSDFact> deltaKB = new LSDTyrubaFactReader(deltaKBFile).getFacts();
        this.localFB.loadDeltaKBFactBase(deltaKB);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    ArrayList<LSDRule> winnowingRules = new LSDAlchemyRuleReader(
      this.winnowingRulesFile).getRules();
    if ((twoKBFile != null) && (deltaKBFile != null)) {
      this.localFB.loadWinnowingRules(winnowingRules);
    }
    this.localFB.getRemainingFacts(true);
    if (lsdiffRuleFile != null) {
      this.lsdiffRules = new LSDAlchemyRuleReader(lsdiffRuleFile).getRules();
    }
  }
  
  private HashMap<String, ArrayList<LSDFact>> predicateToFacts = new HashMap();
  
  public void compute_LSdiff_FACTTYPE()
  {
    LinkedHashSet<LSDFact> deltaKBFacts = this.localFB.getDeltaKBFact();
    for (LSDFact fact : deltaKBFacts)
    {
      LSDPredicate predicate = fact.getPredicate();
      String predicateType = predicate.getName();
      
      ArrayList<LSDFact> facts = (ArrayList)this.predicateToFacts.get(predicateType);
      if (facts == null)
      {
        facts = new ArrayList();
        this.predicateToFacts.put(predicateType, facts);
      }
      facts.add(fact);
    }
  }
  
  public void print(PrintStream p)
  {
    p.println("2KB Size:\t" + this.localFB.num2KBFactSize());
    p.println("DeltaKB Size:\t" + this.localFB.numDeltaKBFactSize());
    p.println("Categorization of Delta KB Facts");
    for (String predicateType : this.predicateToFacts.keySet())
    {
      ArrayList<LSDFact> facts = (ArrayList)this.predicateToFacts.get(predicateType);
      p.println(predicateType);
      p.println("# Facts:\t" + facts.size());
    }
  }
}
