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
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package tyRuBa.tdbc;

import java.io.ByteArrayInputStream;

import tyRuBa.engine.QueryEngine;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.parser.TyRuBaParser;

/**
 * @author kdvolder
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Insert {

	private QueryEngine queryEngine;
	
	Insert(QueryEngine queryEngine) {
		this.queryEngine = queryEngine;
	}
	
	public void executeInsert(String insertString) throws TyrubaException {
		TyRuBaParser parser = new TyRuBaParser(new ByteArrayInputStream(insertString.getBytes()),System.err);
		try {
			parser.Rule(queryEngine);
		} catch (ParseException e) {
			throw new TyrubaException(e);
		} catch (TypeModeError e) {
			throw new TyrubaException(e);
		}
	}

}
