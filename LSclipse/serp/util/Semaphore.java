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

import java.io.Serializable;

public class Semaphore
  implements Serializable
{
  private int _available = 1;
  
  public Semaphore() {}
  
  public Semaphore(int available)
  {
    if (available < 0) {
      throw new IllegalArgumentException("available = " + available);
    }
    this._available = available;
  }
  
  public int getAvailable()
  {
    return this._available;
  }
  
  public synchronized void down()
  {
    while (this._available == 0) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
    this._available -= 1;
  }
  
  public synchronized boolean down(long timeout)
  {
    if (timeout == 0L)
    {
      down();
      return true;
    }
    if (this._available == 0)
    {
      long time = System.currentTimeMillis();
      long end = time + timeout;
      while ((this._available == 0) && (time < end))
      {
        try
        {
          wait(end - time);
        }
        catch (InterruptedException localInterruptedException) {}
        time = System.currentTimeMillis();
      }
    }
    if (this._available == 0) {
      return false;
    }
    this._available -= 1;
    return true;
  }
  
  public synchronized void up()
  {
    this._available += 1;
    if (this._available == 1) {
      notify();
    }
  }
}
