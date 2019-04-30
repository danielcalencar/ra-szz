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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class URLResourceID
  extends Pager.ResourceId
{
  private URL actualLocation;
  
  public URLResourceID(URLLocation location, String relativeID)
  {
    try
    {
      this.actualLocation = new URL(location.getBase(), relativeID);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new Error("Malformed URL for URLResource: " + location + " ::: " + relativeID);
    }
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof URLResourceID))
    {
      URLResourceID id_other = (URLResourceID)other;
      return id_other.actualLocation.equals(this.actualLocation);
    }
    return false;
  }
  
  public int hashCode()
  {
    return 29 * this.actualLocation.hashCode();
  }
  
  public InputStream readResource()
    throws IOException
  {
    return this.actualLocation.openStream();
  }
  
  public OutputStream writeResource()
    throws IOException
  {
    return null;
  }
  
  public void removeResource() {}
  
  public boolean resourceExists()
  {
    try
    {
      this.actualLocation.openStream().close();
      return true;
    }
    catch (IOException localIOException) {}
    return false;
  }
}
