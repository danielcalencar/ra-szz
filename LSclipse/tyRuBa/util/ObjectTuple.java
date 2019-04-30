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
package tyRuBa.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

public class ObjectTuple
  implements Serializable
{
  private boolean isSingleton;
  private Object[] objects;
  private Object singletonObj;
  public static ObjectTuple theEmpty = new ObjectTuple(new Object[0]);
  
  private ObjectTuple(Object[] objects)
  {
    this.objects = objects;
  }
  
  private ObjectTuple(Object object, boolean isSingleton)
  {
    this.singletonObj = object;
    this.isSingleton = isSingleton;
    System.err.println("MAKING A SINGLETON ObjectTuple, something probably isn't right");
  }
  
  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeBoolean(this.isSingleton);
    if (this.isSingleton)
    {
      out.writeObject(this.singletonObj);
    }
    else
    {
      out.writeInt(this.objects.length);
      for (int i = 0; i < this.objects.length; i++) {
        out.writeObject(this.objects[i]);
      }
    }
  }
  
  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    this.isSingleton = in.readBoolean();
    if (this.isSingleton)
    {
      this.singletonObj = in.readObject();
      if ((this.singletonObj instanceof String)) {
        this.singletonObj = ((String)this.singletonObj).intern();
      }
    }
    else
    {
      this.objects = new Object[in.readInt()];
      for (int i = 0; i < this.objects.length; i++)
      {
        this.objects[i] = in.readObject();
        if ((this.objects[i] instanceof String)) {
          this.objects[i] = ((String)this.objects[i]).intern();
        }
      }
    }
  }
  
  public static ObjectTuple make(Object[] objs)
  {
    if (objs.length == 0) {
      return theEmpty;
    }
    if (objs.length == 1) {
      return new ObjectTuple(objs[0], true);
    }
    return new ObjectTuple(objs);
  }
  
  public static ObjectTuple makeSingleton(Object o)
  {
    return new ObjectTuple(o, true);
  }
  
  public int size()
  {
    if (this.isSingleton) {
      return 1;
    }
    return this.objects.length;
  }
  
  public Object get(int i)
  {
    if (this.isSingleton)
    {
      if (i != 0) {
        throw new Error("Index out of bounds");
      }
      return this.singletonObj;
    }
    return this.objects[i];
  }
  
  public boolean equals(Object obj)
  {
    if (obj.getClass() == getClass())
    {
      if (this == obj) {
        return true;
      }
      ObjectTuple other = (ObjectTuple)obj;
      if ((this.isSingleton) && (other.isSingleton)) {
        return this.singletonObj.equals(other.singletonObj);
      }
      if (this.isSingleton != other.isSingleton) {
        return false;
      }
      for (int i = 0; i < this.objects.length; i++) {
        if (!this.objects[i].equals(other.objects[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public static ObjectTuple append(ObjectTuple first, ObjectTuple second)
  {
    Object[] result = new Object[first.size() + second.size()];
    for (int i = 0; i < first.size(); i++) {
      result[i] = first.get(i);
    }
    for (int i = 0; i < second.size(); i++) {
      result[(first.size() + i)] = second.get(i);
    }
    return make(result);
  }
  
  public String toString()
  {
    StringBuffer result = new StringBuffer();
    result.append("<<");
    if (this.isSingleton) {
      result.append(this.singletonObj);
    } else {
      for (int i = 0; i < this.objects.length; i++)
      {
        if (i > 0) {
          result.append(", ");
        }
        result.append(this.objects[i].toString());
      }
    }
    result.append(">>");
    return result.toString();
  }
  
  public int hashCode()
  {
    if (this.isSingleton)
    {
      int hash = 1;
      return hash * 83 + this.singletonObj.hashCode();
    }
    int hash = this.objects.length;
    for (int i = 0; i < this.objects.length; i++) {
      hash = hash * 83 + this.objects[i].hashCode();
    }
    return hash;
  }
}
