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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Localizer
{
  private static Map _bundles = new HashMap();
  private String _file = null;
  private Locale _locale = null;
  
  public static Localizer forPackage(Class cls)
  {
    return forPackage(cls, null);
  }
  
  public static Localizer forPackage(Class cls, Locale locale)
  {
    Localizer loc = new Localizer();
    
    int dot = cls == null ? -1 : cls.getName().lastIndexOf('.');
    if (dot == -1) {
      loc._file = "localizer";
    } else {
      loc._file = (cls.getName().substring(0, dot + 1) + "localizer");
    }
    loc._locale = locale;
    
    return loc;
  }
  
  public String get(String key)
  {
    return get(key, this._locale);
  }
  
  public String get(String key, Object sub)
  {
    return get(key, new Object[] { sub }, this._locale);
  }
  
  public String get(String key, Object[] subs)
  {
    return get(key, subs, this._locale);
  }
  
  public String get(String key, Object sub, Locale locale)
  {
    return get(key, new Object[] { sub }, locale);
  }
  
  public String get(String key, Object[] subs, Locale locale)
  {
    String str = get(key, locale);
    return MessageFormat.format(str, subs);
  }
  
  public String get(String key, Locale locale)
  {
    String cacheKey = this._file;
    if (locale != null) {
      cacheKey = cacheKey + locale.toString();
    }
    ResourceBundle bundle = (ResourceBundle)_bundles.get(cacheKey);
    if (bundle == null)
    {
      if (locale != null) {
        bundle = ResourceBundle.getBundle(this._file, locale);
      } else {
        bundle = ResourceBundle.getBundle(this._file);
      }
      _bundles.put(cacheKey, bundle);
    }
    return bundle.getString(key);
  }
}
