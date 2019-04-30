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
package tyRuBa.tdbc;

import tyRuBa.engine.QueryEngine;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;

public class Connection
{
  private QueryEngine queryEngine;
  
  public Connection(QueryEngine queryEngine)
  {
    this.queryEngine = queryEngine;
  }
  
  public Query createQuery()
  {
    return new Query(this.queryEngine);
  }
  
  public Insert createInsert()
  {
    return new Insert(this.queryEngine);
  }
  
  public PreparedQuery prepareQuery(String qry)
    throws TyrubaException
  {
    try
    {
      return this.queryEngine.prepareForRunning(qry);
    }
    catch (ParseException e)
    {
      throw new TyrubaException(e);
    }
    catch (TypeModeError e)
    {
      throw new TyrubaException(e);
    }
  }
  
  public PreparedInsert prepareInsert(String fact)
    throws ParseException, TypeModeError
  {
    return this.queryEngine.prepareForInsertion(fact);
  }
}
