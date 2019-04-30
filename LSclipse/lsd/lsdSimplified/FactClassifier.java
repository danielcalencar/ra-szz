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

public class FactClassifier implements Iterator<ArrayList<String>>{

	private ArrayList<LSDFact> t_twoKbFacts;
	private ArrayList<LSDFact> t_deltaKbFacts;
	private List<LSDRule> typeLevelRules;
	private List<LSDRule> winnowingRules;
	private ArrayList<ClusterInfo> clusters = new ArrayList<ClusterInfo>();
	private Iterator<ClusterInfo> clusterIterator;
	private ClusterInfo currentCluster = null;
	
	private class ClusterInfo{
		//Each cluster info is a type level rule 
		//And a list of all type names that are covered with this rule
		LSDRule rule;
		ArrayList<String> cluster;
	}
	
	
	public FactClassifier(ArrayList<LSDFact> t2kb, ArrayList<LSDFact> tdelta,List<LSDRule> twinnoing, List<LSDRule> rules) throws ParseException, TypeModeError, IOException {
		t_deltaKbFacts = tdelta;
		t_twoKbFacts = t2kb;
		typeLevelRules = rules;
		winnowingRules = twinnoing;
		currentCluster = null;
		classifier();
		clusterIterator = clusters.iterator();
		
	}

	//Builds all cluster based on type level rules and their supporting facts
	private void classifier() throws ParseException, TypeModeError, IOException {
		LSDFactBase fb = new LSDFactBase();
		fb.load2KBFactBase(t_twoKbFacts);
		fb.loadDeltaKBFactBase(t_deltaKbFacts);
		fb.loadWinnowingRules(winnowingRules);
		fb.loadWinnowingRules(typeLevelRules);
		List<LSDFact> remainingFacts = fb.getRemainingFacts(true);
		for (LSDRule rule : typeLevelRules) {
			ArrayList<String> cluster = new ArrayList<String>();
			List<LSDFact> facts = fb.getRelevantFacts(rule);
			//We add all remaining facts to each cluster to increase coverage
			facts.addAll(remainingFacts);
			for (LSDFact fact : facts) {
				List<LSDBinding> bindings = fact.getBindings();
				for (LSDBinding binding : bindings) {
					if (!cluster.contains(binding.getGroundConst()))
						cluster.add(binding.getGroundConst());
				}
			}
			for (LSDLiteral literal : rule.getLiterals()) {
				List<LSDBinding> bindings = literal.getBindings();
				for (LSDBinding binding : bindings) {
					if (!cluster.contains(binding.getGroundConst()) && binding.getGroundConst()!=null)
						cluster.add(binding.getGroundConst());
				}
			}
			ClusterInfo info = new ClusterInfo();
			info.rule = rule;
			info.cluster = cluster;
			clusters.add(info);
		}		
		
	}

	public boolean hasNext() {
		return clusterIterator.hasNext();
	}


	public ArrayList<String> next() {
		currentCluster = clusterIterator.next();
		return currentCluster.cluster;
	}
	
	public LSDRule getRule(){
		return currentCluster.rule;
	}


	public void remove() {
		clusterIterator.remove();		
	}


	public int size() {
		return clusters.size();
	}

}
