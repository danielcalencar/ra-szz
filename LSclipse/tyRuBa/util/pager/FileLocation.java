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
package tyRuBa.util.pager;

import java.io.File;

public class FileLocation
  extends Location
{
  File base = null;
  int myHashCode;
  
  public FileLocation(File theBasePath)
  {
    this.base = theBasePath;
    this.myHashCode = this.base.hashCode();
  }
  
  public FileLocation(String filename)
  {
    this(new File(filename));
  }
  
  public File getBase()
  {
    return this.base;
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof FileLocation))
    {
      FileLocation flOther = (FileLocation)other;
      return flOther.base.equals(this.base);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.myHashCode;
  }
  
  public Pager.ResourceId getResourceID(String relativeID)
  {
    return new FileResourceID(this, relativeID);
  }
  
  public String toString()
  {
    return this.base.toString();
  }
}
