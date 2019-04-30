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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileResourceID
  extends Pager.ResourceId
{
  private String relativeId;
  private FileLocation base;
  private File lazyActualFile = null;
  
  public FileResourceID(FileLocation location, String relativeID)
  {
    this.base = location;
    this.relativeId = relativeID;
  }
  
  public String toString()
  {
    return "FileResourceID(" + this.base + "/" + this.relativeId + ")";
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof FileResourceID))
    {
      FileResourceID id_other = (FileResourceID)other;
      return (id_other.relativeId.equals(this.relativeId)) && (id_other.base.equals(this.base));
    }
    return false;
  }
  
  public int hashCode()
  {
    return 23 * this.base.hashCode() + 47 * this.relativeId.hashCode();
  }
  
  public InputStream readResource()
    throws IOException
  {
    if (this.lazyActualFile == null) {
      this.lazyActualFile = new File(this.base.getBase(), this.relativeId);
    }
    return new FileInputStream(this.lazyActualFile);
  }
  
  public OutputStream writeResource()
    throws IOException
  {
    File baseFile = this.base.getBase();
    if (!baseFile.exists()) {
      baseFile.mkdirs();
    }
    if (this.lazyActualFile == null) {
      this.lazyActualFile = new File(this.base.getBase(), this.relativeId);
    }
    return new FileOutputStream(this.lazyActualFile);
  }
  
  public void removeResource()
  {
    if (this.lazyActualFile == null) {
      this.lazyActualFile = new File(this.base.getBase(), this.relativeId);
    }
    this.lazyActualFile.delete();
  }
  
  public boolean resourceExists()
  {
    if (this.lazyActualFile == null) {
      this.lazyActualFile = new File(this.base.getBase(), this.relativeId);
    }
    return this.lazyActualFile.exists();
  }
}
