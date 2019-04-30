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
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package tyRuBa.tdbc;

import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.util.ElementSource;

/**
 * @author dsjanzen
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreparedQuery extends PreparedStatement{
	
	private RBExpression preparedExp; 
	   // A prepared exp: has been mode and typechecked.
	
	private Compiled     compiled;
	   // Compiled version of the above.
	
	public PreparedQuery(QueryEngine engine,RBExpression preparedExp,TypeEnv tEnv) {
		super(engine,tEnv);
		this.preparedExp = preparedExp;
		this.compiled = preparedExp.compile(new CompilationContext());
	}
	
	public static PreparedQuery prepare(QueryEngine queryEngine,String queryTemplate) 
	throws ParseException, TypeModeError {
		return queryEngine.prepareForRunning(queryTemplate);
	}

	public ResultSet executeQuery() throws TyrubaException {
		checkReadyToRun();
		return new ResultSet(compiled.start(putMap));
	}

	public ElementSource start() {
		return compiled.start(putMap);
	}

}
