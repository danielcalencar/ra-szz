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

public class ThreadLock
  implements Serializable
{
  private transient int _count = 0;
  private transient Thread _owner = null;
  
  public synchronized void lock()
  {
    Thread thread = Thread.currentThread();
    if (thread != this._owner) {
      while (this._count > 0) {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    this._count += 1;
    this._owner = thread;
  }
  
  public synchronized boolean lock(long timeout)
  {
    if (timeout == 0L)
    {
      lock();
      return true;
    }
    Thread thread = Thread.currentThread();
    if ((thread != this._owner) && (this._count > 0))
    {
      long time = System.currentTimeMillis();
      long end = time + timeout;
      while ((this._count > 0) && (time < end))
      {
        try
        {
          wait(end - time);
        }
        catch (InterruptedException localInterruptedException) {}
        time = System.currentTimeMillis();
      }
    }
    if ((thread != this._owner) && (this._count > 0)) {
      return false;
    }
    this._count += 1;
    this._owner = thread;
    return true;
  }
  
  public synchronized void unlock()
  {
    Thread thread = Thread.currentThread();
    if (thread != this._owner) {
      throw new IllegalStateException();
    }
    this._count -= 1;
    if (this._count == 0) {
      notify();
    }
  }
  
  public boolean isLocked()
  {
    return this._count > 0;
  }
}
