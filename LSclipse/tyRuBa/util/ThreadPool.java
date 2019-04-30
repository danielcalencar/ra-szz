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

import java.util.LinkedList;

public class ThreadPool
{
  private LinkedList jobs;
  private Object mutex;
  private int workers;
  private boolean halt = false;
  private boolean exit = false;
  
  private class Worker
    implements Runnable
  {
    private Worker() {}
    
    public void run()
    {
      for (;;)
      {
        synchronized (ThreadPool.this.mutex)
        {
          if (ThreadPool.this.halt)
          {
            cleanUp();return;
            if (ThreadPool.this.exit)
            {
              cleanUp();return;
            }
            try
            {
              ThreadPool.this.mutex.wait();
            }
            catch (InterruptedException localInterruptedException) {}
            if (ThreadPool.this.halt)
            {
              cleanUp();return;
            }
          }
          if (ThreadPool.this.jobs.size() == 0) {
            continue;
          }
          Runnable job = (Runnable)ThreadPool.this.jobs.removeFirst();
          try
          {
            job.run();
          }
          catch (Throwable localThrowable) {}
        }
        Runnable job = null;
      }
    }
    
    private void cleanUp()
    {
      ThreadPool.this.workers -= 1;
      ThreadPool.this.mutex.notifyAll();
    }
  }
  
  public ThreadPool(int workers)
  {
    if (workers <= 0) {
      throw new IllegalArgumentException("There must be at least 1 worker thread");
    }
    this.workers = workers;
    
    this.jobs = new LinkedList();
    
    this.mutex = this.jobs;
    
    int i = workers;
    while (i-- != 0) {
      new Thread(new Worker(null)).start();
    }
  }
  
  public void delegate(Runnable job)
  {
    synchronized (this.mutex)
    {
      if ((this.halt) || (this.exit)) {
        throw new IllegalStateException("ThreadPool is being shutdown");
      }
      this.jobs.addLast(job);
      this.mutex.notify();
    }
  }
  
  public int waiting()
  {
    return this.jobs.size();
  }
  
  public void halt()
  {
    synchronized (this.mutex)
    {
      this.halt = true;
      this.mutex.notifyAll();
    }
  }
  
  public void exit()
  {
    synchronized (this.mutex)
    {
      this.exit = true;
      this.mutex.notifyAll();
    }
  }
  
  public void waitExit()
  {
    synchronized (this.mutex)
    {
      this.exit = true;
      this.mutex.notifyAll();
      while (this.workers != 0) {
        try
        {
          this.mutex.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
  }
  
  public void waitHalt()
  {
    synchronized (this.mutex)
    {
      this.halt = true;
      this.mutex.notifyAll();
      while (this.workers != 0) {
        try
        {
          this.mutex.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
  }
  
  protected void finalize()
  {
    exit();
  }
}
