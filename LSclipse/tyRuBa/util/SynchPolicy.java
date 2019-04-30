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

import junit.framework.Assert;

public final class SynchPolicy
{
  SynchResource resource;
  
  public SynchPolicy(SynchResource res)
  {
    this.resource = res;
  }
  
  int stopSources = 0;
  int busySources = 0;
  
  public void sourceDone()
  {
    synchronized (this.resource)
    {
      this.busySources -= 1;
      debug_message("--");
      Assert.assertTrue(this.busySources >= 0);
      if (this.busySources == 0) {
        this.resource.notifyAll();
      }
    }
  }
  
  public void newSource()
  {
    while (this.stopSources > 0) {
      try
      {
        this.resource.wait();
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
    this.busySources += 1;
    debug_message("++");
  }
  
  public void stopSources()
  {
    long waitTime = 100L;
    if (Aurelizer.debug_sounds != null) {
      Aurelizer.debug_sounds.enter_loop("temporizing");
    }
    try
    {
      synchronized (this.resource)
      {
        this.stopSources += 1;
        debug_message("stop");
        while (this.busySources > 0)
        {
          try
          {
            this.resource.wait(waitTime);
            if (waitTime > 100L) {
              System.gc();
            }
            waitTime *= 2L;
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }
          if ((this.busySources > 0) && (waitTime > 33000L))
          {
            this.stopSources -= 1;
            if (Aurelizer.debug_sounds != null) {
              Aurelizer.debug_sounds.enter("error");
            }
            throw new Error("I've lost my patience waiting for all queries to be released");
          }
        }
      }
    }
    finally
    {
      if (Aurelizer.debug_sounds != null) {
        Aurelizer.debug_sounds.exit("temporizing");
      }
    }
    if (Aurelizer.debug_sounds != null) {
      Aurelizer.debug_sounds.exit("temporizing");
    }
  }
  
  private void debug_message(String msg) {}
  
  public String toString()
  {
    return "SynchPolicy(busy=" + this.busySources + ",stop=" + this.stopSources + ")";
  }
  
  public void allowSources()
  {
    synchronized (this.resource)
    {
      Assert.assertTrue(this.stopSources > 0);
      this.stopSources -= 1;
      debug_message("allow");
      this.resource.notifyAll();
    }
  }
}
