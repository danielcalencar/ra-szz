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
package tyRuBa.engine;

import java.io.Serializable;

public class Validator
  implements Serializable
{
  private boolean isOutdated = true;
  private boolean isValid = true;
  private long handle = -1L;
  
  public long handle()
  {
    return this.handle;
  }
  
  public void setHandle(long handle)
  {
    this.handle = handle;
  }
  
  public boolean isValid()
  {
    return this.isValid;
  }
  
  public void invalidate()
  {
    this.isValid = false;
  }
  
  public String toString()
  {
    return 
    
      "Validator(" + this.handle + "," + (this.isOutdated ? "OUTDATED" : "UPTODATE") + "," + (this.isValid ? "VALID" : "INALIDATED") + ")";
  }
  
  public boolean isOutdated()
  {
    return this.isOutdated;
  }
  
  public void setOutdated(boolean flag)
  {
    this.isOutdated = flag;
  }
  
  private boolean hasAssociatedFacts = false;
  
  public boolean hasAssociatedFacts()
  {
    return this.hasAssociatedFacts;
  }
  
  public void setHasAssociatedFacts(boolean flag)
  {
    this.hasAssociatedFacts = flag;
  }
}
