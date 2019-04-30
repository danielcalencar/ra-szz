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
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import tyRuBa.util.DoubleLinkedList;
import tyRuBa.util.DoubleLinkedList.Entry;

public class Pager
{
  public static abstract class Task
  {
    private boolean isChangedResource = false;
    private Pager.Resource updatedResource = null;
    private final boolean mayChangeResource;
    
    public Task(boolean mayChangeResource)
    {
      this.mayChangeResource = mayChangeResource;
    }
    
    public abstract Object doIt(Pager.Resource paramResource);
    
    protected final void changedResource(Pager.Resource changedResource)
    {
      Assert.assertTrue(this.mayChangeResource);
      this.isChangedResource = true;
      this.updatedResource = changedResource;
    }
    
    final boolean resourceIsChanged()
    {
      return this.isChangedResource;
    }
    
    public Pager.Resource getChangedResource()
    {
      return this.updatedResource;
    }
    
    public boolean mayChangeResource()
    {
      return this.mayChangeResource;
    }
  }
  
  private final class ResourceReferenceInfo
    extends DoubleLinkedList.Entry
  {
    private Pager.ResourceId resId;
    private Pager.Resource resource;
    private boolean referenced;
    private long timeLastReferenced;
    private int numReferences;
    private boolean dirty;
    
    public ResourceReferenceInfo(Pager.ResourceId resId, Pager.Resource resource)
    {
      this.resId = resId;
      this.resource = resource;
      this.referenced = false;
      this.timeLastReferenced = 0L;
      this.dirty = false;
      this.numReferences = 0;
    }
    
    public Pager.ResourceId getResourceID()
    {
      return this.resId;
    }
    
    public Pager.Resource getResource()
    {
      return this.resource;
    }
    
    public void updateResource(Pager.Resource newResource)
    {
      this.resource = newResource;
    }
    
    public boolean isReferenced()
    {
      return this.referenced;
    }
    
    public void setReferenced(boolean referenced)
    {
      if (referenced)
      {
        incrementReferenceCounter();
        setTimeLastReferenced(System.currentTimeMillis());
      }
      this.referenced = referenced;
    }
    
    private void setTimeLastReferenced(long timeLastReferenced)
    {
      this.timeLastReferenced = timeLastReferenced;
    }
    
    private void incrementReferenceCounter()
    {
      this.numReferences += 1;
    }
    
    public void resetReferenceCounter()
    {
      this.numReferences = 0;
    }
    
    public boolean isDirty()
    {
      return this.dirty;
    }
    
    public void setDirty(boolean dirty)
    {
      synchronized (Pager.this.dirtyResources)
      {
        if (this.dirty == dirty) {
          return;
        }
        this.dirty = dirty;
        if (dirty) {
          Pager.this.dirtyResources.add(this);
        } else {
          Pager.this.dirtyResources.remove(this);
        }
      }
    }
    
    public int compareTo(Object o)
    {
      ResourceReferenceInfo other = (ResourceReferenceInfo)o;
      
      long myCompareNumber = this.timeLastReferenced + this.numReferences;
      long otherCompareNumber = other.timeLastReferenced + other.numReferences;
      if (otherCompareNumber > myCompareNumber) {
        return -1;
      }
      if (otherCompareNumber < myCompareNumber) {
        return 1;
      }
      return 0;
    }
    
    public String toString()
    {
      return "Rsrc(" + this.resId + (this.dirty ? "=DIRTY" : "") + ")";
    }
  }
  
  private long lastTaskTime = System.currentTimeMillis();
  private int cacheSize;
  private Map inMemory = new HashMap();
  private Set dirtyResources = new HashSet();
  private DiskManager diskMan;
  private DoubleLinkedList lruQueue;
  private boolean needToCallBackup;
  private PageCleaner pageCleaner = null;
  
  public static abstract class ResourceId
  {
    public abstract boolean equals(Object paramObject);
    
    public abstract int hashCode();
    
    public abstract InputStream readResource()
      throws IOException;
    
    public abstract OutputStream writeResource()
      throws IOException;
    
    public abstract void removeResource();
    
    public abstract boolean resourceExists();
  }
  
  public static abstract interface Resource
    extends Serializable
  {}
  
  class PageCleaner
    extends Thread
  {
    public PageCleaner()
    {
      super();
      setPriority(1);
    }
    
    public void run()
    {
      for (;;)
      {
        Pager.ResourceReferenceInfo toClean = null;
        if (!Pager.this.diskMan.isAlive()) {
          return;
        }
        synchronized (Pager.this.dirtyResources)
        {
          boolean cleaningTime = (!Pager.this.dirtyResources.isEmpty()) && (Pager.this.diskMan.isIdle()) && (isIdle());
          if (cleaningTime) {
            toClean = (Pager.ResourceReferenceInfo)Pager.this.dirtyResources.iterator().next();
          }
        }
        boolean cleaningTime;
        if (cleaningTime) {
          Pager.this.writeResourceToDisk(toClean);
        }
        try
        {
          sleep(300L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    
    private boolean isIdle()
    {
      long idletime = System.currentTimeMillis() - Pager.this.lastTaskTime;
      
      return idletime > 4000L;
    }
  }
  
  public Pager(int cacheSize, int queueSize, long lastBackupTime, boolean backgrounCleaning)
  {
    this.cacheSize = cacheSize;
    this.needToCallBackup = false;
    this.lruQueue = new DoubleLinkedList();
    this.diskMan = new DiskManager(queueSize);
    this.diskMan.setPriority(10);
    this.diskMan.start();
    if (backgrounCleaning)
    {
      this.pageCleaner = new PageCleaner();
      this.pageCleaner.start();
    }
  }
  
  public void enableBackGroundPaging() {}
  
  /* Error */
  public Object synchDoTask(ResourceId rsrcID, Task task)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aload_0
    //   3: invokestatic 27	java/lang/System:currentTimeMillis	()J
    //   6: putfield 33	tyRuBa/util/pager/Pager:lastTaskTime	J
    //   9: aload_0
    //   10: aload_1
    //   11: invokespecial 86	tyRuBa/util/pager/Pager:getResource	(LtyRuBa/util/pager/Pager$ResourceId;)LtyRuBa/util/pager/Pager$Resource;
    //   14: astore 4
    //   16: aload_2
    //   17: invokevirtual 90	tyRuBa/util/pager/Pager$Task:mayChangeResource	()Z
    //   20: ifeq +33 -> 53
    //   23: aload_0
    //   24: getfield 61	tyRuBa/util/pager/Pager:diskMan	LtyRuBa/util/pager/DiskManager;
    //   27: aload_1
    //   28: invokevirtual 96	tyRuBa/util/pager/DiskManager:getResourceLock	(LtyRuBa/util/pager/Pager$ResourceId;)V
    //   31: iconst_1
    //   32: istore_3
    //   33: goto +20 -> 53
    //   36: astore 5
    //   38: iload_3
    //   39: ifeq +11 -> 50
    //   42: aload_0
    //   43: getfield 61	tyRuBa/util/pager/Pager:diskMan	LtyRuBa/util/pager/DiskManager;
    //   46: aload_1
    //   47: invokevirtual 100	tyRuBa/util/pager/DiskManager:releaseResourceLock	(LtyRuBa/util/pager/Pager$ResourceId;)V
    //   50: aload 5
    //   52: athrow
    //   53: iload_3
    //   54: ifeq +11 -> 65
    //   57: aload_0
    //   58: getfield 61	tyRuBa/util/pager/Pager:diskMan	LtyRuBa/util/pager/DiskManager;
    //   61: aload_1
    //   62: invokevirtual 100	tyRuBa/util/pager/DiskManager:releaseResourceLock	(LtyRuBa/util/pager/Pager$ResourceId;)V
    //   65: aload_2
    //   66: aload 4
    //   68: invokevirtual 103	tyRuBa/util/pager/Pager$Task:doIt	(LtyRuBa/util/pager/Pager$Resource;)Ljava/lang/Object;
    //   71: astore 5
    //   73: aload_2
    //   74: invokevirtual 107	tyRuBa/util/pager/Pager$Task:resourceIsChanged	()Z
    //   77: ifeq +15 -> 92
    //   80: aload_0
    //   81: aload_1
    //   82: aload_2
    //   83: invokevirtual 110	tyRuBa/util/pager/Pager$Task:getChangedResource	()LtyRuBa/util/pager/Pager$Resource;
    //   86: invokespecial 114	tyRuBa/util/pager/Pager:changeResource	(LtyRuBa/util/pager/Pager$ResourceId;LtyRuBa/util/pager/Pager$Resource;)V
    //   89: goto +8 -> 97
    //   92: aload_0
    //   93: aload_1
    //   94: invokespecial 118	tyRuBa/util/pager/Pager:referenceResource	(LtyRuBa/util/pager/Pager$ResourceId;)V
    //   97: aload 5
    //   99: areturn
    // Line number table:
    //   Java source line #317	-> byte code offset #0
    //   Java source line #318	-> byte code offset #2
    //   Java source line #319	-> byte code offset #9
    //   Java source line #321	-> byte code offset #16
    //   Java source line #322	-> byte code offset #23
    //   Java source line #323	-> byte code offset #31
    //   Java source line #326	-> byte code offset #36
    //   Java source line #327	-> byte code offset #38
    //   Java source line #328	-> byte code offset #42
    //   Java source line #329	-> byte code offset #50
    //   Java source line #327	-> byte code offset #53
    //   Java source line #328	-> byte code offset #57
    //   Java source line #330	-> byte code offset #65
    //   Java source line #331	-> byte code offset #73
    //   Java source line #332	-> byte code offset #80
    //   Java source line #334	-> byte code offset #92
    //   Java source line #336	-> byte code offset #97
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	100	0	this	Pager
    //   0	100	1	rsrcID	ResourceId
    //   0	100	2	task	Task
    //   1	53	3	lockHeld	boolean
    //   14	53	4	rsrc	Resource
    //   36	15	5	localObject1	Object
    //   71	27	5	result	Object
    // Exception table:
    //   from	to	target	type
    //   16	36	36	finally
  }
  
  public void asynchDoTask(ResourceId rsrcID, Task task)
  {
    this.lastTaskTime = System.currentTimeMillis();
    synchDoTask(rsrcID, task);
  }
  
  private void referenceResource(ResourceId rsrcID)
  {
    ResourceReferenceInfo rsrc_ref = (ResourceReferenceInfo)this.inMemory.get(rsrcID);
    if (rsrc_ref != null)
    {
      rsrc_ref.setReferenced(true);
      rsrc_ref.incrementReferenceCounter();
      this.lruQueue.remove(rsrc_ref);
      this.lruQueue.enqueue(rsrc_ref);
    }
  }
  
  private void changeResource(ResourceId rsrcID, Resource newResource)
  {
    this.needToCallBackup = true;
    ResourceReferenceInfo rsrc_ref = (ResourceReferenceInfo)this.inMemory.get(rsrcID);
    if (rsrc_ref == null)
    {
      ResourceReferenceInfo newInfo = new ResourceReferenceInfo(rsrcID, newResource);
      newInfo.setDirty(true);
      newInfo.setReferenced(true);
      if (needToPageOutIfOneMoreAdded()) {
        pageOutOne();
      }
      this.inMemory.put(rsrcID, newInfo);
      this.lruQueue.enqueue(newInfo);
    }
    else if (newResource == null)
    {
      rsrcID.removeResource();
      this.inMemory.remove(rsrcID);
      this.lruQueue.remove(rsrc_ref);
    }
    else
    {
      rsrc_ref.updateResource(newResource);
      rsrc_ref.setDirty(true);
      rsrc_ref.setReferenced(true);
      this.lruQueue.remove(rsrc_ref);
      this.lruQueue.enqueue(rsrc_ref);
    }
  }
  
  private Resource getResource(ResourceId rsrcID)
  {
    ResourceReferenceInfo rsrc_ref = (ResourceReferenceInfo)this.inMemory.get(rsrcID);
    if (rsrc_ref == null)
    {
      Resource result = getResourceFromDisk(rsrcID);
      return result;
    }
    Resource result = rsrc_ref.getResource();
    return result;
  }
  
  public void backup()
  {
    if (this.needToCallBackup)
    {
      for (Iterator iter = this.inMemory.values().iterator(); iter.hasNext();)
      {
        ResourceReferenceInfo refInfo = (ResourceReferenceInfo)iter.next();
        if (refInfo.isDirty()) {
          writeResourceToDisk(refInfo);
        }
      }
      this.needToCallBackup = false;
    }
    this.diskMan.flush();
  }
  
  public void shutdown()
  {
    backup();
    this.diskMan.killMe();
  }
  
  public void crash()
  {
    this.diskMan.crash();
  }
  
  public void setCacheSize(int cacheSize)
  {
    this.cacheSize = cacheSize;
    pageUntilNonNeeded();
  }
  
  public int getCacheSize()
  {
    return this.cacheSize;
  }
  
  private boolean needToPageOut()
  {
    return this.inMemory.size() > this.cacheSize;
  }
  
  private boolean needToPageOutIfOneMoreAdded()
  {
    return this.inMemory.size() + 1 > this.cacheSize;
  }
  
  private void pageOutOne()
  {
    if (this.inMemory.size() > 0)
    {
      ResourceReferenceInfo victim = null;
      
      victim = (ResourceReferenceInfo)this.lruQueue.dequeue();
      this.inMemory.remove(victim.getResourceID());
      if (victim.isDirty()) {
        writeResourceToDisk(victim);
      }
    }
  }
  
  private void pageUntilNonNeeded()
  {
    while (needToPageOut()) {
      pageOutOne();
    }
  }
  
  private void writeResourceToDisk(ResourceReferenceInfo victim)
  {
    this.diskMan.writeOut(victim.getResourceID(), victim.getResource());
    victim.setDirty(false);
  }
  
  private Resource getResourceFromDisk(ResourceId rsrcID)
  {
    if (this.diskMan.resourceExists(rsrcID))
    {
      if (needToPageOutIfOneMoreAdded()) {
        pageOutOne();
      }
      Resource resource = this.diskMan.readIn(rsrcID);
      changeResource(rsrcID, resource);
      return resource;
    }
    return null;
  }
  
  public void printStats()
  {
    this.diskMan.printStats();
  }
  
  public boolean isDirty()
  {
    synchronized (this.dirtyResources)
    {
      return !this.dirtyResources.isEmpty();
    }
  }
}
