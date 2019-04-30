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
package tyRuBa.engine;

/** A context is information passed around in eval and unify 
    */
public class RBContext {

	public RBContext() {
	}

	/*
	public void assert(RBTerm fact) {
	rb.insert(new RBFact(fact));
	}
	
	public void retract(RBTerm fact) {
	rb.retract(new RBFact(fact));
	}
	*/
	public String toString() {
		return "-- RBContext --";
	}
	/*
	RBContext removeFailAvoiders() {
	  return this;
	}
	*/
	//  RuleBase ruleBase() { return rb;}

	int depth() {
		return 0;
	}

	/** Unify this with other 
	public final ElementSource unify(RBTerm other,RBContext context) {
	  return rb.unify(other,context);
	}
	*/

}
