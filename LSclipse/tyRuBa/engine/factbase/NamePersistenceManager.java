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
package tyRuBa.engine.factbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NamePersistenceManager
  implements Serializable
{
  private Map nameMap;
  private String storagePath;
  
  public NamePersistenceManager(String storagePath)
  {
    File nameFile = new File(storagePath + "/names.data");
    this.storagePath = storagePath;
    if (nameFile.exists()) {
      try
      {
        FileInputStream fis = new FileInputStream(nameFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        this.nameMap = ((HashMap)ois.readObject());
        
        ois.close();
        fis.close();
      }
      catch (IOException localIOException)
      {
        throw new Error("Could not load names because of IOException");
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new Error("Could not load names because of ClassNotFoundException");
      }
    } else {
      this.nameMap = new HashMap();
    }
  }
  
  public NamePersistenceManager(URL storageLocation)
  {
    this.storagePath = storageLocation.toString();
    try
    {
      ObjectInputStream ois = new ObjectInputStream(storageLocation.openStream());
      this.nameMap = ((HashMap)ois.readObject());
      
      ois.close();
    }
    catch (IOException e)
    {
      System.err.println(this.storagePath);
      throw new Error("Could not load names because of IOException", e);
    }
    catch (ClassNotFoundException e)
    {
      throw new Error("Could not load names because of ClassNotFoundException", e);
    }
  }
  
  public String getPersistentName(String tyRuBaName)
  {
    String result = (String)this.nameMap.get(tyRuBaName);
    if (result == null)
    {
      int nextNum = this.nameMap.size();
      result = String.valueOf(nextNum);
      this.nameMap.put(tyRuBaName, result);
    }
    return result;
  }
  
  public void backup()
  {
    File nameFile = new File(this.storagePath + "/names.data");
    try
    {
      FileOutputStream fos = new FileOutputStream(nameFile, false);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      
      oos.writeObject(this.nameMap);
      
      oos.close();
      fos.close();
    }
    catch (IOException localIOException)
    {
      throw new Error("Could not save names because of IOException");
    }
  }
}
