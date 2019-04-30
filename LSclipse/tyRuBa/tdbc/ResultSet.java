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

import tyRuBa.engine.Frame;
import tyRuBa.engine.FrontEnd;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBRepAsJavaObjectCompoundTerm;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBVariable;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.util.ElementSource;

public class ResultSet
{
  private ElementSource eltSource;
  private Frame frame = null;
  
  public ResultSet(ElementSource eltSource)
  {
    this.eltSource = eltSource;
  }
  
  ResultSet(QueryEngine queryEngine, String query)
    throws TyrubaException
  {
    try
    {
      this.eltSource = queryEngine.frameQuery(query);
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
  
  public ResultSet(QueryEngine queryEngine, RBExpression query)
    throws TyrubaException
  {
    try
    {
      this.eltSource = queryEngine.frameQuery(query);
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
  
  public boolean next()
    throws TyrubaException
  {
    boolean more = this.eltSource.hasMoreElements();
    if (more) {
      this.frame = ((Frame)this.eltSource.nextElement());
    } else {
      this.frame = null;
    }
    return more;
  }
  
  public Object getObject(String variableName)
    throws TyrubaException
  {
    if (this.frame == null) {
      throw new TyrubaException("There are no more elements in the current result set.");
    }
    RBVariable var = FrontEnd.makeVar(variableName);
    RBTerm term = this.frame.get(var);
    if ((term instanceof RBCompoundTerm))
    {
      if ((term instanceof RBRepAsJavaObjectCompoundTerm)) {
        return ((RBRepAsJavaObjectCompoundTerm)term).getValue();
      }
      if (((RBCompoundTerm)term).getNumArgs() == 1)
      {
        if (((RBCompoundTerm)term).getArg() != null) {
          return ((RBCompoundTerm)term).getArg().up();
        }
        return term.up();
      }
      return term;
    }
    return term;
  }
  
  public String getString(String variableName)
    throws TyrubaException
  {
    Object o = getObject(variableName);
    if (!(o instanceof String)) {
      throw wrongType(variableName, o, "String");
    }
    return (String)o;
  }
  
  public int getInt(String variableName)
    throws TyrubaException
  {
    Object o = getObject(variableName);
    if ((o instanceof Integer)) {
      return ((Integer)o).intValue();
    }
    throw wrongType(variableName, o, "int");
  }
  
  private TyrubaException wrongType(String varName, Object found, String expectedType)
  {
    return new TyrubaException("Variable " + varName + " is bound to an object of type " + found.getClass().getName() + " not " + expectedType + ".");
  }
}
