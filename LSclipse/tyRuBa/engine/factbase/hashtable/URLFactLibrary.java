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
package tyRuBa.engine.factbase.hashtable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import junit.framework.Assert;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.factbase.FileBasedValidatorManager;
import tyRuBa.engine.factbase.NamePersistenceManager;
import tyRuBa.engine.factbase.ValidatorManager;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.Factory;
import tyRuBa.modes.PredicateMode;
import tyRuBa.util.pager.URLLocation;

public class URLFactLibrary
{
  private String baseURL;
  private HashMap indexes;
  private NamePersistenceManager nameManager;
  private ValidatorManager validatorManager;
  private QueryEngine engine;
  
  public URLFactLibrary(String baseURL, QueryEngine qe)
  {
    this.indexes = new HashMap();
    this.baseURL = baseURL;
    this.engine = qe;
    try
    {
      this.nameManager = new NamePersistenceManager(new URL(baseURL + "names.data"));
      Assert.assertNotNull(this.nameManager);
      this.validatorManager = new FileBasedValidatorManager(new URL(baseURL + "validators.data"));
      Assert.assertNotNull(this.validatorManager);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new Error("Library file does not exist");
    }
  }
  
  public Index getIndex(String predicateName, int arity, PredicateMode mode)
  {
    BindingList bl = mode.getParamModes();
    if (bl.getNumFree() == 0) {
      bl = Factory.makeBindingList(arity, Factory.makeFree());
    }
    Index result = (Index)this.indexes.get(predicateName + arity + bl.getBFString());
    try
    {
      if (result == null) {
        result = new Index(mode, new URLLocation(this.baseURL + this.nameManager.getPersistentName(predicateName) + "/" + 
          arity + "/" + bl.getBFString() + "/"), this.engine, predicateName + "/" + arity + "/" + 
          bl.getBFString(), this.nameManager, this.validatorManager);
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new Error("Malformed URL for Fact Library index: " + predicateName + "/" + arity + "/" + 
        bl.getBFString());
    }
    return result;
  }
}
