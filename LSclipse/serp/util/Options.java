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
package serp.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class Options
  extends TypedProperties
{
  private static Object[][] _primWrappers = {
    { Boolean.TYPE, Boolean.class, Boolean.FALSE }, 
    { Byte.TYPE, Byte.class, new Byte(0) }, 
    { Character.TYPE, Character.class, new Character('\000') }, 
    { Double.TYPE, Double.class, new Double(0.0D) }, 
    { Float.TYPE, Float.class, new Float(0.0F) }, 
    { Integer.TYPE, Integer.class, new Integer(0) }, 
    { Long.TYPE, Long.class, new Long(0L) }, 
    { Short.TYPE, Short.class, new Short(0) } };
  
  public Options() {}
  
  public Options(Properties defaults)
  {
    super(defaults);
  }
  
  public String[] setFromCmdLine(String[] args)
  {
    if ((args == null) || (args.length == 0)) {
      return args;
    }
    String key = null;
    String value = null;
    List remainder = new LinkedList();
    for (int i = 0; i < args.length + 1; i++) {
      if ((i == args.length) || (args[i].startsWith("-")))
      {
        key = trimQuote(key);
        if (key != null) {
          if ((value != null) && (value.length() > 0)) {
            setProperty(key, trimQuote(value));
          } else {
            setProperty(key, "true");
          }
        }
        if (i == args.length) {
          break;
        }
        key = args[i].substring(1);
        value = null;
      }
      else if (key != null)
      {
        setProperty(key, trimQuote(args[i]));
        key = null;
      }
      else
      {
        remainder.add(args[i]);
      }
    }
    return (String[])remainder.toArray(new String[remainder.size()]);
  }
  
  public void setInto(Object obj)
  {
    Map.Entry entry = null;
    if (this.defaults != null) {
      for (Iterator itr = this.defaults.entrySet().iterator(); itr.hasNext();)
      {
        entry = (Map.Entry)itr.next();
        if (!containsKey(entry.getKey())) {
          setInto(obj, entry);
        }
      }
    }
    for (Iterator itr = entrySet().iterator(); itr.hasNext();) {
      setInto(obj, (Map.Entry)itr.next());
    }
  }
  
  private void setInto(Object obj, Map.Entry entry)
  {
    if (entry.getKey() == null) {
      return;
    }
    try
    {
      Object[] match = { obj };
      if (!matchOptionToSetter(entry.getKey().toString(), match)) {
        return;
      }
      Method setter = (Method)match[1];
      Class[] paramTypes = setter.getParameterTypes();
      Object[] values = new Object[paramTypes.length];
      String[] strValues = entry.getValue() == null ? new String[1] : 
        Strings.split(entry.getValue().toString(), ",", -1);
      for (int i = 0; i < strValues.length; i++) {
        values[i] = stringToObject(strValues[i], paramTypes[i]);
      }
      for (int i = strValues.length; i < values.length; i++) {
        values[i] = getDefaultValue(paramTypes[i]);
      }
      setter.invoke(match[0], values);
    }
    catch (Throwable localThrowable)
    {
      throw new IllegalArgumentException(obj + "." + entry.getKey() + 
        " = " + entry.getValue());
    }
  }
  
  private static String trimQuote(String val)
  {
    if ((val != null) && (val.startsWith("'")) && (val.endsWith("'"))) {
      return val.substring(1, val.length() - 1);
    }
    return val;
  }
  
  private static boolean matchOptionToSetter(String key, Object[] match)
    throws Exception
  {
    if ((key == null) || (key.length() == 0)) {
      return false;
    }
    String[] find = Strings.split(key, ".", 2);
    String base = Strings.capitalize(find[0]);
    String set = "set" + base;
    String get = "get" + base;
    
    Class type = match[0].getClass();
    Method[] meths = type.getMethods();
    Method setter = null;
    Method getter = null;
    for (int i = 0; i < meths.length; i++) {
      if (meths[i].getName().equals(set)) {
        setter = meths[i];
      } else if (meths[i].getName().equals(get)) {
        getter = meths[i];
      }
    }
    if ((setter == null) && (getter == null)) {
      return false;
    }
    if (find.length > 1)
    {
      Object inner = null;
      if (getter != null) {
        inner = getter.invoke(match[0], null);
      }
      if (inner == null)
      {
        Class innerType = setter.getParameterTypes()[0];
        inner = innerType.newInstance();
        setter.invoke(match[0], new Object[] { inner });
      }
      match[0] = inner;
      return matchOptionToSetter(find[1], match);
    }
    match[1] = setter;
    return match[1] != null;
  }
  
  private static Object stringToObject(String str, Class type)
    throws Exception
  {
    if ((str == null) || (type == String.class)) {
      return str;
    }
    if (type == Class.class) {
      return Class.forName(str);
    }
    if (((type.isPrimitive()) || (Number.class.isAssignableFrom(type))) && 
      (str.length() > 2) && (str.endsWith(".0"))) {
      str = str.substring(0, str.length() - 2);
    }
    if (type.isPrimitive()) {
      for (int i = 0; i < _primWrappers.length; i++) {
        if (type == _primWrappers[i][0]) {
          return stringToObject(str, (Class)_primWrappers[i][1]);
        }
      }
    }
    Exception err = null;
    try
    {
      Constructor cons = type.getConstructor(
        new Class[] { String.class });
      return cons.newInstance(new Object[] { str });
    }
    catch (Exception e)
    {
      err = e;
      
      Class subType = null;
      try
      {
        subType = Class.forName(str);
      }
      catch (Exception localException1)
      {
        throw err;
      }
      if (!type.isAssignableFrom(subType)) {
        throw err;
      }
      return subType.newInstance();
    }
  }
  
  private Object getDefaultValue(Class type)
  {
    for (int i = 0; i < _primWrappers.length; i++) {
      if (_primWrappers[i][0] == type) {
        return _primWrappers[i][2];
      }
    }
    return null;
  }
}
