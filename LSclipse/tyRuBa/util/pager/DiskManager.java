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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import serp.util.Semaphore;
import tyRuBa.util.Aurelizer;
import tyRuBa.util.DoubleLinkedList;
import tyRuBa.util.DoubleLinkedList.Entry;

public class DiskManager
  extends Thread
{
  private int maxSize;
  Semaphore queueMutex;
  Semaphore queueAvailable;
  Semaphore queueSize;
  Semaphore resourceLocksMutex;
  boolean alive;
  DoubleLinkedList taskQueue = new DoubleLinkedList();
  Map resourceLocks = new HashMap();
  public int couldHaveCanceledPageout;
  private int highWaterMark = 0;
  private int pageOutRequests = 0;
  private int pageInRequests = 0;
  
  private static class Task
    extends DoubleLinkedList.Entry
  {
    Pager.Resource rsrc;
    Pager.ResourceId resourceID;
    
    Task(Pager.ResourceId resourceID, Pager.Resource rsrc)
    {
      this.resourceID = resourceID;
      this.rsrc = rsrc;
    }
    
    void doIt()
    {
      try
      {
        OutputStream os = this.resourceID.writeResource();
        if (os != null)
        {
          ObjectOutputStream oos = new ObjectOutputStream(os);
          oos.writeObject(this.rsrc);
          oos.close();
        }
      }
      catch (IOException e)
      {
        throw new Error("Could not page because of an IOException: " + e.getMessage());
      }
    }
  }
  
  public DiskManager(int maxQueueSize)
  {
    this.maxSize = maxQueueSize;
    this.resourceLocksMutex = new Semaphore();
    this.queueMutex = new Semaphore();
    this.queueAvailable = new Semaphore(this.maxSize);
    this.queueSize = new Semaphore(0);
    this.alive = true;
  }
  
  public synchronized boolean isIdle()
  {
    return this.taskQueue.isEmpty();
  }
  
  public void run()
  {
    while (this.alive)
    {
      this.queueSize.down();
      if (!this.alive) {
        break;
      }
      this.queueMutex.down();
      Task nextTask = (Task)this.taskQueue.dequeue();
      this.queueMutex.up();
      
      this.queueAvailable.up();
      
      nextTask.doIt();
      releaseResourceLock(nextTask.resourceID);
    }
  }
  
  void getResourceLock(Pager.ResourceId resID)
  {
    this.resourceLocksMutex.down();
    Semaphore lock = (Semaphore)this.resourceLocks.get(resID);
    if (lock == null)
    {
      lock = new Semaphore(1);
      this.resourceLocks.put(resID, lock);
    }
    this.resourceLocksMutex.up();
    lock.down();
  }
  
  void releaseResourceLock(Pager.ResourceId resID)
  {
    this.resourceLocksMutex.down();
    Semaphore lock = (Semaphore)this.resourceLocks.get(resID);
    if (lock != null)
    {
      this.resourceLocks.remove(resID);
      lock.up();
    }
    this.resourceLocksMutex.up();
  }
  
  public synchronized void writeOut(Pager.ResourceId resourceID, Pager.Resource rsrc)
  {
    Task task = new Task(resourceID, rsrc);
    getResourceLock(resourceID);
    this.queueAvailable.down();
    this.queueMutex.down();
    this.taskQueue.enqueue(task);
    this.queueMutex.up();
    this.queueSize.up();
    int waterLevel;
    if ((waterLevel = this.queueSize.getAvailable()) > this.highWaterMark) {
      this.highWaterMark = waterLevel;
    }
    this.pageOutRequests += 1;
  }
  
  public synchronized boolean resourceExists(Pager.ResourceId rsrcID)
  {
    return (this.resourceLocks.get(rsrcID) != null) || (rsrcID.resourceExists());
  }
  
  public synchronized Pager.Resource readIn(Pager.ResourceId rsrcID)
  {
    this.pageInRequests += 1;
    if (this.resourceLocks.get(rsrcID) != null) {
      this.couldHaveCanceledPageout += 1;
    }
    getResourceLock(rsrcID);
    try
    {
      ObjectInputStream ois = new ObjectInputStream(rsrcID.readResource());
      resource = (Pager.Resource)ois.readObject();
    }
    catch (IOException e)
    {
      Pager.Resource resource;
      throw new Error("Could not page in because of IOException: " + e.getMessage());
    }
    catch (ClassNotFoundException e)
    {
      throw new Error("Could not page in because of ClassNotFoundException: " + e.getMessage());
    }
    Pager.Resource resource;
    releaseResourceLock(rsrcID);
    return resource;
  }
  
  public synchronized void killMe()
  {
    this.alive = false;
    this.queueSize.up();
  }
  
  public void crash()
  {
    stop();
    this.taskQueue = null;
    this.resourceLocks = null;
    this.resourceLocksMutex = null;
    this.queueSize = null;
    this.queueMutex = null;
    this.queueAvailable = null;
  }
  
  public synchronized void flush()
  {
    while (this.queueSize.getAvailable() > 0) {
      try
      {
        sleep(500L);
      }
      catch (InterruptedException localInterruptedException)
      {
        if (Aurelizer.debug_sounds != null) {
          Aurelizer.debug_sounds.enter("error");
        }
        throw new Error("Don't interrupt me!!!");
      }
    }
  }
  
  public void printStats()
  {
    System.err.println("Diskman.couldHaveCanceledPageout = " + this.couldHaveCanceledPageout);
    this.couldHaveCanceledPageout = 0;
    System.err.println("Diskman.biggestQueueSize = " + this.highWaterMark);
    this.highWaterMark = 0;
    System.err.println("Diskman.pageOutRequests = " + this.pageOutRequests);
    this.pageOutRequests = 0;
    System.err.println("Diskman.pageInRequests = " + this.pageInRequests);
    this.pageInRequests = 0;
  }
}
