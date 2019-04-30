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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import tyRuBa.engine.Frame;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBSubstitutable;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public abstract class PreparedStatement
{
  private TypeEnv tEnv;
  protected Frame putMap = new Frame();
  private Set mustPut = null;
  private QueryEngine engine;
  
  public PreparedStatement(QueryEngine engine, TypeEnv tEnv)
  {
    this.engine = engine;
    this.tEnv = tEnv;
    for (Iterator iter = tEnv.keySet().iterator(); iter.hasNext();)
    {
      RBSubstitutable var = (RBSubstitutable)iter.next();
      if ((var instanceof RBTemplateVar))
      {
        if (this.mustPut == null) {
          this.mustPut = new HashSet();
        }
        this.mustPut.add(var.name());
      }
    }
  }
  
  protected void checkReadyToRun()
    throws TyrubaException
  {
    if (!readyToRun()) {
      throw new TyrubaException("Some input variables left unbound: " + this.mustPut);
    }
  }
  
  public boolean readyToRun()
  {
    return this.mustPut == null;
  }
  
  public void put(String templateVar, Object value)
    throws TyrubaException
  {
    checkVarType(templateVar, value);
    if (this.mustPut != null)
    {
      this.mustPut.remove(templateVar);
      if (this.mustPut.isEmpty()) {
        this.mustPut = null;
      }
    }
    this.putMap.put(new RBTemplateVar(templateVar), this.engine.makeJavaObject(value));
  }
  
  private void checkVarType(String templateVarName, Object value)
    throws TyrubaException
  {
    RBTemplateVar var = new RBTemplateVar(templateVarName);
    Type expected = this.tEnv.basicGet(var);
    if (expected == null) {
      throw new TyrubaException("Trying to put an unknown variable: " + templateVarName);
    }
    try
    {
      Class expectedClass = expected.javaEquivalent();
      if (expectedClass == null) {
        throw new TyrubaException("There is no Java equivalent for tyRuBa type " + expected);
      }
      if (!expectedClass.isAssignableFrom(value.getClass())) {
        throw new TyrubaException("Value: " + value + " of class " + value.getClass().getName() + " expected " + expectedClass.getName());
      }
    }
    catch (TypeModeError e)
    {
      e.printStackTrace();
      throw new TyrubaException(e.getMessage());
    }
  }
  
  public void put(String templateVar, int value)
    throws TyrubaException
  {
    put(templateVar, new Integer(value));
  }
  
  public void put(String templateVar, long value)
    throws TyrubaException
  {
    put(templateVar, new Long(value));
  }
  
  public void put(String templateVar, float value)
    throws TyrubaException
  {
    put(templateVar, new Float(value));
  }
  
  public void put(String templateVar, boolean value)
    throws TyrubaException
  {
    put(templateVar, new Boolean(value));
  }
  
  public QueryEngine getEngine()
  {
    return this.engine;
  }
}
