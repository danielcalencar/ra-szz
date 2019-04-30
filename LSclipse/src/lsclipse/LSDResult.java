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
package lsclipse;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import lsd.rule.LSDFact;
import lsd.rule.LSDVariable;

public class LSDResult {
	public int num_matches;
	public int num_counter;
	public String desc;
	public java.util.List<LSDFact> examples;
	public java.util.List<Map<LSDVariable, String>> exceptions;
	private ArrayList<String> examplesString = null;
	private ArrayList<String> exceptionsString = null;
	public ArrayList<String> getExampleStr() {
		if (examplesString==null) {	//contruct strings from examples
			examplesString = new ArrayList<String>();
			for (LSDFact fact : examples) {
				examplesString.add(fact.toString());
			}
		}
		return examplesString;
	}
	public ArrayList<String> getExceptionsString() {
		if (exceptionsString==null) {	//contruct strings from examples
			exceptionsString = new ArrayList<String>();
			for (Map<LSDVariable, String> exception : exceptions) {
				StringBuilder s = new StringBuilder();
				s.append("[ ");
				for (Entry<LSDVariable, String> entry : exception.entrySet()) {
					s.append(entry.getKey());
					s.append("=\"");
					s.append(entry.getValue());
					s.append("\" ");
				}
				s.append("]");
				exceptionsString.add(s.toString());
			}
		}
		return exceptionsString;
	}
}
