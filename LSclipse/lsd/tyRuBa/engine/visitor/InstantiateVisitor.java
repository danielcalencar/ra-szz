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
/*
 * Created on Jul 3, 2003
 */
package tyRuBa.engine.visitor;

import java.util.Collection;
import java.util.HashSet;

import tyRuBa.engine.Frame;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBUniqueQuantifier;
import tyRuBa.engine.RBVariable;

public class InstantiateVisitor extends SubstituteOrInstantiateVisitor {

	public InstantiateVisitor(Frame frame) {
		super(frame);
	}

	public Object visit(RBUniqueQuantifier unique) {
		RBExpression exp = (RBExpression) unique.getExp().accept(this);
		Collection vars = new HashSet();
		for (int i = 0; i < unique.getNumVars(); i++) {
			vars.add(unique.getVarAt(i).accept(this));
		}
		return new RBUniqueQuantifier(vars, exp);
	}

	public Object visit(RBVariable var) {
		RBTerm val = (RBTerm) getFrame().get(var);
		if (val == null) {
			val = (RBVariable) var.clone();
			getFrame().put(var, val);
			return val;
		} else {
			return val;
		}
	}

	public Object visit(RBTemplateVar templVar) {
		//Instantiation only happens at runtime. TemplateVar should not
		//exsit any more at runtime so...
		throw new Error("Unsupported operation");
	}

}
