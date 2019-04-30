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

import java.util.LinkedList;

public class Strings
{
  private static final Object[][] _codes = {
    { Byte.TYPE, "byte" }, 
    { Character.TYPE, "char" }, 
    { Double.TYPE, "double" }, 
    { Float.TYPE, "float" }, 
    { Integer.TYPE, "int" }, 
    { Long.TYPE, "long" }, 
    { Short.TYPE, "short" }, 
    { Boolean.TYPE, "boolean" }, 
    { Void.TYPE, "void" } };
  
  public static String[] split(String str, String token, int max)
  {
    if ((str == null) || (str.length() == 0)) {
      return new String[0];
    }
    if ((token == null) || (token.length() == 0)) {
      throw new IllegalArgumentException("token: [" + token + "]");
    }
    LinkedList ret = new LinkedList();
    int start = 0;
    for (int split = 0; split != -1;)
    {
      split = str.indexOf(token, start);
      if ((split == -1) && (start >= str.length()))
      {
        ret.add("");
      }
      else if (split == -1)
      {
        ret.add(str.substring(start));
      }
      else
      {
        ret.add(str.substring(start, split));
        start = split + token.length();
      }
    }
    if (max == 0)
    {
      while (ret.getLast().equals("")) {
        ret.removeLast();
      }
    }
    else if ((max > 0) && (ret.size() > max))
    {
      StringBuffer buf = new StringBuffer(ret.removeLast().toString());
      while (ret.size() >= max)
      {
        buf.insert(0, token);
        buf.insert(0, ret.removeLast());
      }
      ret.add(buf.toString());
    }
    return (String[])ret.toArray(new String[ret.size()]);
  }
  
  public static String join(Object[] strings, String token)
  {
    if (strings == null) {
      return null;
    }
    StringBuffer buf = new StringBuffer(20 * strings.length);
    for (int i = 0; i < strings.length; i++)
    {
      if (i > 0) {
        buf.append(token);
      }
      if (strings[i] != null) {
        buf.append(strings[i]);
      }
    }
    return buf.toString();
  }
  
  public static String capitalize(String str)
  {
    if ((str == null) || (str.length() == 0)) {
      return str;
    }
    char first = Character.toUpperCase(str.charAt(0));
    if (str.length() == 1) {
      return String.valueOf(first);
    }
    return first + str.substring(1);
  }
  
  public static Class toClass(String str, ClassLoader loader)
  {
    if (str == null) {
      throw new NullPointerException("str = null");
    }
    for (int i = 0; i < _codes.length; i++) {
      if (_codes[i][1].toString().equals(str)) {
        return (Class)_codes[i][0];
      }
    }
    if (loader == null) {
      loader = Thread.currentThread().getContextClassLoader();
    }
    try
    {
      return Class.forName(str, true, loader);
    }
    catch (Throwable t)
    {
      throw new IllegalArgumentException(t.getClass().getName() + ": " + 
        t.getMessage());
    }
  }
}
