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

import java.util.ArrayList;
import java.util.List;

public class LSDFact extends LSDLiteral implements Comparable<LSDFact> {
	private LSDFact(LSDPredicate pred, ArrayList<LSDBinding> bindings,
			boolean nonNegated) throws LSDInvalidTypeException {
		super(pred, bindings, nonNegated);
	}

	public boolean contains(String filter) {
		return (super.toString().contains(filter));
	}

	public boolean equals(Object o) {
		if (o instanceof LSDFact) {
			LSDFact of = (LSDFact) o;
			if (of.toString().equals(this.toString())) {
				return true;
			}
		}
		return false;
	}

	public static LSDFact createLSDFact(LSDPredicate pred, ArrayList<LSDBinding> bindings) { 
		LSDFact theFact = null;
		try {
			theFact = new LSDFact(pred,bindings, true); 
			
		} catch (LSDInvalidTypeException e) {
			System.err.println("LSDFact cannot have an invalid type.");
			System.exit(1);
		}
		return theFact;
	}

	public int compareTo(LSDFact arg0) {
		return (toString().compareTo(arg0.toString()));
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public static LSDFact createLSDFact(LSDPredicate pred, List<String> constants,
			boolean nonNegated) {
		ArrayList<LSDBinding> bindings = new ArrayList<LSDBinding>();
		for (String s : constants) {
			bindings.add(new LSDBinding("\""+s+"\""));
		}
		LSDFact theFact = null;
		try {
			//System.out.println("Niki....creating the new LSD Fact");
			theFact = new LSDFact(pred, bindings, nonNegated);
			//System.out.println("Niki...finished creating....");
		} catch (LSDInvalidTypeException e) {
			System.err.println("LSDFact cannot have an invalid type.");
			System.exit(1);
		}
		if (theFact.bindings == null)
			return null;
		return theFact;
	}

	public LSDFact nonNegatedCopy() {
		try {
			return new LSDFact(predicate, bindings, true);
		} catch (LSDInvalidTypeException e) {
			System.err.println("LSDFact cannot have an invalid type.");
			System.exit(1);
			return null;
		}
	}

	public LSDFact addedCopy() {
		LSDPredicate newPredicate = predicate.getPrefixPredicate("added");
		if (newPredicate == null) {
			System.err
					.println("All predicates should have an added/deleted version.");
			System.exit(1);
			return null;
		}
		try {
			return new LSDFact(newPredicate, bindings, true);
		} catch (LSDInvalidTypeException e) {
			System.err.println("LSDFact cannot have an invalid type.");
			System.exit(1);
			return null;
		}
	}

	public LSDFact deletedCopy() {
		LSDPredicate newPredicate = predicate.getPrefixPredicate("deleted");
		if (newPredicate == null) {
			System.err
					.println("All predicates should have an added/deleted version.");
			System.exit(1);
			return null;
		}
		try {
			return new LSDFact(newPredicate, bindings, true);
		} catch (LSDInvalidTypeException e) {
			System.err.println("LSDFact cannot have an invalid type.");
			System.exit(1);
			return null;
		}
	}

	// FIXME:PREDICATE CONTENT DEPENT
	public String[] getPrimaryConstants() {
		String name = this.getPredicate().getSuffix();
		if (name.equals("type")) {
			String s[] = { this.bindings.get(0).toString() };
			return s;
		} else if (name.equals("field")) {
			String s[] = { this.bindings.get(0).toString() };
			return s;
		} else if (name.equals("method")) {
			String s[] = { this.bindings.get(0).toString() };
			return s;
		} else if (name.equals("typeintype")) {
			String s[] = { this.bindings.get(0).toString() };
			return s;
		} else if (name.equals("inheritedmethod")) {
			String n = this.bindings.get(0).toString();
			String s[] = { this.bindings.get(1).toString() + "#" + n,
					this.bindings.get(2).toString() };
			return s;
		} else if (name.equals("inheritedfield")) {
			String n = this.bindings.get(0).toString();
			String s[] = { this.bindings.get(1).toString() + "#" + n,
					this.bindings.get(2).toString() };
			return s;
		}else if (name.equals("conditional")){
			System.out.println("conditional within LSD Fact was called\n");
		}
		String s[] = new String[this.bindings.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = this.bindings.get(i).toString();
		}
		return s;
	}

	public String getReferenceConstant() {
		return this.bindings.get(this.predicate.getReferenceArgument())
				.toString();
	}

	// TODO(kprete): function not used; commented out to ignore compile errors.
	public LSDFact convertToClassLevel() {
		return null;
//		try {
//			List<String> bindings = new ArrayList<String>();
//			for (LSDBinding b : this.bindings) {
//				bindings.add(b.toString());
//			}
//			if (predicate.isConclusionPredicate()
//					|| predicate.isDependencyPredicate()) {
//				LSDPredicate changedPred = predicate.toClassLevel();
//				bindings = predicate.getConvertedArgs(bindings);
//				return LSDFact.createLSDFact(changedPred, bindings, true);
//			} else {
//				return null;
//			}
//		} catch (Exception e) {
//			System.err.println("Conversion Error");
//			return this;
//		}
	}

}
