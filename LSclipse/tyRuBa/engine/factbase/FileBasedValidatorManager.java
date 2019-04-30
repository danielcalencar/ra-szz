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
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import tyRuBa.engine.Validator;

public class FileBasedValidatorManager
  implements ValidatorManager
{
  private String storagePath;
  private Map validators;
  private Map identifiers;
  private Map handles;
  private long lastInvalidateTime;
  private long validatorCounter;
  
  public FileBasedValidatorManager(String storagePath)
  {
    this.storagePath = storagePath;
    this.validators = new HashMap();
    this.identifiers = new HashMap();
    this.handles = new HashMap();
    this.lastInvalidateTime = -1L;
    this.validatorCounter = 0L;
    
    File validatorFile = new File(storagePath + "/validators.data");
    if (validatorFile.exists()) {
      try
      {
        FileInputStream fis = new FileInputStream(validatorFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        int size = ois.readInt();
        for (int i = 0; i < size; i++)
        {
          String id = (String)ois.readObject();
          Validator validator = (Validator)ois.readObject();
          
          Long handle = new Long(validator.handle());
          this.handles.put(handle, id);
          this.identifiers.put(id, handle);
          this.validators.put(handle, validator);
        }
        this.lastInvalidateTime = ois.readLong();
        this.validatorCounter = ois.readLong();
        
        ois.close();
        fis.close();
      }
      catch (IOException localIOException)
      {
        throw new Error("Could not load validators because of IOException");
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new Error("Could not load validators because of ClassNotFoundException");
      }
    }
  }
  
  public FileBasedValidatorManager(URL url)
  {
    this.storagePath = url.toString();
    this.validators = new HashMap();
    this.identifiers = new HashMap();
    this.handles = new HashMap();
    try
    {
      ObjectInputStream ois = new ObjectInputStream(url.openStream());
      int size = ois.readInt();
      for (int i = 0; i < size; i++)
      {
        String id = (String)ois.readObject();
        Validator validator = (Validator)ois.readObject();
        Long handle = new Long(validator.handle());
        this.handles.put(handle, id);
        this.identifiers.put(id, handle);
        this.validators.put(handle, validator);
      }
      this.lastInvalidateTime = ois.readLong();
      this.validatorCounter = ois.readLong();
      ois.close();
    }
    catch (IOException localIOException)
    {
      throw new Error("Could not load validators because of IOException");
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new Error("Could not load validators because of ClassNotFoundException");
    }
  }
  
  public void add(Validator v, String identifier)
  {
    v.setHandle(this.validatorCounter++);
    Long handle = new Long(v.handle());
    if (!this.validators.containsKey(handle))
    {
      this.validators.put(handle, v);
      this.identifiers.put(identifier, handle);
      this.handles.put(handle, identifier);
    }
  }
  
  public void update(long validatorHandle, Boolean outdated, Boolean hasFacts)
  {
    Validator v = (Validator)this.validators.get(new Long(validatorHandle));
    if (v != null)
    {
      if (outdated != null) {
        v.setOutdated(outdated.booleanValue());
      }
      if (hasFacts != null) {
        v.setHasAssociatedFacts(hasFacts.booleanValue());
      }
    }
  }
  
  public void remove(long validatorHandle)
  {
    Long handle = new Long(validatorHandle);
    ((Validator)this.validators.get(handle));
    this.validators.remove(handle);
    String identifier = (String)this.handles.get(handle);
    this.identifiers.remove(identifier);
    this.handles.remove(handle);
    this.lastInvalidateTime = System.currentTimeMillis();
  }
  
  public void remove(String identifier)
  {
    Long handle = (Long)this.identifiers.get(identifier);
    if (handle != null)
    {
      this.identifiers.remove(identifier);
      remove(handle.longValue());
    }
  }
  
  public Validator get(long validatorHandle)
  {
    Long handle = new Long(validatorHandle);
    Validator result = (Validator)this.validators.get(handle);
    return result;
  }
  
  public Validator get(String identifier)
  {
    Long handle = (Long)this.identifiers.get(identifier);
    if (handle != null) {
      return get(handle.longValue());
    }
    return null;
  }
  
  public String getIdentifier(long validatorHandle)
  {
    return (String)this.handles.get(new Long(validatorHandle));
  }
  
  public void printOutValidators()
  {
    Iterator it = this.validators.values().iterator();
    while (it.hasNext())
    {
      Validator v = (Validator)it.next();
      System.err.println("[Validator] " + v);
    }
  }
  
  public void backup()
  {
    try
    {
      FileOutputStream fos = new FileOutputStream(new File(this.storagePath + "/validators.data"), false);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      int size = 0;
      for (Iterator iter = this.identifiers.keySet().iterator(); iter.hasNext();)
      {
        String element = (String)iter.next();
        if (!element.startsWith("TMP")) {
          size++;
        }
      }
      oos.writeInt(size);
      for (Iterator iter = this.identifiers.entrySet().iterator(); iter.hasNext();)
      {
        Map.Entry element = (Map.Entry)iter.next();
        String identifier = (String)element.getKey();
        if (!identifier.startsWith("TMP"))
        {
          oos.writeObject(identifier);
          oos.writeObject(this.validators.get(element.getValue()));
        }
      }
      oos.writeLong(this.lastInvalidateTime);
      oos.writeLong(this.validatorCounter);
      oos.close();
      fos.close();
    }
    catch (IOException localIOException)
    {
      throw new Error("Could not backup validator manager because of IOException");
    }
  }
  
  public long getLastInvalidatedTime()
  {
    return this.lastInvalidateTime;
  }
}
