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
 * Created on Feb 24, 2004
 */
package tyRuBa.tdbc;

import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.tdbc.PreparedStatement;

import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBPredicateExpression;

/**
 * @author kdvolder
 */
public class PreparedInsert extends PreparedStatement {
	
	RBPredicateExpression fact;
	  // A fact which may contain templateVars
	
	public PreparedInsert(QueryEngine engine,RBPredicateExpression fact,TypeEnv tEnv) {
		super(engine,tEnv);
		this.fact = fact;
	}

	public void executeInsert() throws TyrubaException {
		checkReadyToRun();
		try {
			getEngine().insert((RBPredicateExpression)fact.substitute(putMap));
		} catch (TypeModeError e) {
			throw new TyrubaException(e);
		}
	}
	
	public String toString() {
		return "PrepIns("+fact+", "+putMap+")";
	}

}
