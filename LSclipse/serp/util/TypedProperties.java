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

import java.util.Properties;

public class TypedProperties
  extends Properties
{
  public TypedProperties() {}
  
  public TypedProperties(Properties defaults)
  {
    super(defaults);
  }
  
  public boolean getBooleanProperty(String key)
  {
    return getBooleanProperty(key, false);
  }
  
  public boolean getBooleanProperty(String key, boolean def)
  {
    String val = getProperty(key);
    return val == null ? def : Boolean.valueOf(val).booleanValue();
  }
  
  public float getFloatProperty(String key)
  {
    return getFloatProperty(key, 0.0F);
  }
  
  public float getFloatProperty(String key, float def)
  {
    String val = getProperty(key);
    return val == null ? def : Float.parseFloat(val);
  }
  
  public double getDoubleProperty(String key)
  {
    return getDoubleProperty(key, 0.0D);
  }
  
  public double getDoubleProperty(String key, double def)
  {
    String val = getProperty(key);
    return val == null ? def : Double.parseDouble(val);
  }
  
  public long getLongProperty(String key)
  {
    return getLongProperty(key, 0L);
  }
  
  public long getLongProperty(String key, long def)
  {
    String val = getProperty(key);
    return val == null ? def : Long.parseLong(val);
  }
  
  public int getIntProperty(String key)
  {
    return getIntProperty(key, 0);
  }
  
  public int getIntProperty(String key, int def)
  {
    String val = getProperty(key);
    return val == null ? def : Integer.parseInt(val);
  }
  
  public Object setProperty(String key, String val)
  {
    if (val == null) {
      return remove(key);
    }
    return super.setProperty(key, val);
  }
  
  public void setProperty(String key, boolean val)
  {
    setProperty(key, String.valueOf(val));
  }
  
  public void setProperty(String key, double val)
  {
    setProperty(key, String.valueOf(val));
  }
  
  public void setProperty(String key, float val)
  {
    setProperty(key, String.valueOf(val));
  }
  
  public void setProperty(String key, int val)
  {
    setProperty(key, String.valueOf(val));
  }
  
  public void setProperty(String key, long val)
  {
    setProperty(key, String.valueOf(val));
  }
}
