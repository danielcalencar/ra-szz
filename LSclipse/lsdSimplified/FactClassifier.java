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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lsd.facts.LSDFactBase;
import lsd.rule.LSDBinding;
import lsd.rule.LSDFact;
import lsd.rule.LSDLiteral;
import lsd.rule.LSDRule;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class FactClassifier
  implements Iterator<ArrayList<String>>
{
  private ArrayList<LSDFact> t_twoKbFacts;
  private ArrayList<LSDFact> t_deltaKbFacts;
  private List<LSDRule> typeLevelRules;
  private List<LSDRule> winnowingRules;
  private ArrayList<ClusterInfo> clusters = new ArrayList();
  private Iterator<ClusterInfo> clusterIterator;
  private ClusterInfo currentCluster = null;
  
  public FactClassifier(ArrayList<LSDFact> t2kb, ArrayList<LSDFact> tdelta, List<LSDRule> twinnoing, List<LSDRule> rules)
    throws ParseException, TypeModeError, IOException
  {
    this.t_deltaKbFacts = tdelta;
    this.t_twoKbFacts = t2kb;
    this.typeLevelRules = rules;
    this.winnowingRules = twinnoing;
    this.currentCluster = null;
    classifier();
    this.clusterIterator = this.clusters.iterator();
  }
  
  private void classifier()
    throws ParseException, TypeModeError, IOException
  {
    LSDFactBase fb = new LSDFactBase();
    fb.load2KBFactBase(this.t_twoKbFacts);
    fb.loadDeltaKBFactBase(this.t_deltaKbFacts);
    fb.loadWinnowingRules(this.winnowingRules);
    fb.loadWinnowingRules(this.typeLevelRules);
    List<LSDFact> remainingFacts = fb.getRemainingFacts(true);
    for (LSDRule rule : this.typeLevelRules)
    {
      ArrayList<String> cluster = new ArrayList();
      List<LSDFact> facts = fb.getRelevantFacts(rule);
      
      facts.addAll(remainingFacts);
      Iterator localIterator3;
      for (Iterator localIterator2 = facts.iterator(); localIterator2.hasNext(); localIterator3.hasNext())
      {
        LSDFact fact = (LSDFact)localIterator2.next();
        List<LSDBinding> bindings = fact.getBindings();
        localIterator3 = bindings.iterator(); continue;LSDBinding binding = (LSDBinding)localIterator3.next();
        if (!cluster.contains(binding.getGroundConst())) {
          cluster.add(binding.getGroundConst());
        }
      }
      for (localIterator2 = rule.getLiterals().iterator(); localIterator2.hasNext(); localIterator3.hasNext())
      {
        LSDLiteral literal = (LSDLiteral)localIterator2.next();
        List<LSDBinding> bindings = literal.getBindings();
        localIterator3 = bindings.iterator(); continue;LSDBinding binding = (LSDBinding)localIterator3.next();
        if ((!cluster.contains(binding.getGroundConst())) && (binding.getGroundConst() != null)) {
          cluster.add(binding.getGroundConst());
        }
      }
      ClusterInfo info = new ClusterInfo(null);
      info.rule = rule;
      info.cluster = cluster;
      this.clusters.add(info);
    }
  }
  
  public boolean hasNext()
  {
    return this.clusterIterator.hasNext();
  }
  
  public ArrayList<String> next()
  {
    this.currentCluster = ((ClusterInfo)this.clusterIterator.next());
    return this.currentCluster.cluster;
  }
  
  public LSDRule getRule()
  {
    return this.currentCluster.rule;
  }
  
  public void remove()
  {
    this.clusterIterator.remove();
  }
  
  public int size()
  {
    return this.clusters.size();
  }
  
  private class ClusterInfo
  {
    LSDRule rule;
    ArrayList<String> cluster;
    
    private ClusterInfo() {}
  }
}
