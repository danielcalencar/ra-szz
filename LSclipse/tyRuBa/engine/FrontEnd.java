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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import tyRuBa.engine.factbase.FactLibraryManager;
import tyRuBa.engine.factbase.FileBasedValidatorManager;
import tyRuBa.engine.factbase.NamePersistenceManager;
import tyRuBa.engine.factbase.ValidatorManager;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.util.Aurelizer;
import tyRuBa.util.ElementSource;
import tyRuBa.util.SynchPolicy;
import tyRuBa.util.SynchResource;
import tyRuBa.util.pager.Pager;

public class FrontEnd
  extends QueryEngine
  implements SynchResource
{
  public long updateCounter = 0L;
  boolean someOutdated = false;
  ProgressMonitor progressMonitor = null;
  private BasicModedRuleBaseIndex rules;
  PrintStream os = System.out;
  private Collection holdingPen = makeBucketCollection();
  private Collection myBuckets = makeBucketCollection();
  private int progressBar = 0;
  private int updatedBuckets = 0;
  private static Pager pager;
  private ValidatorManager validatorManager;
  private NamePersistenceManager namePersistenceManager;
  private FactLibraryManager factLibraryManager;
  private File path;
  private String identifier;
  public static final int defaultPagerCacheSize = 5000;
  private static final int defaultPagerQueueSize = 1000;
  private long lastBackupTime;
  
  public FrontEnd(boolean loadInitFile, File path, boolean persistent, ProgressMonitor mon, boolean clean, boolean enableBackgroundCleaning)
  {
    this.progressMonitor = mon;
    this.path = path;
    if (pager != null) {
      pager.shutdown();
    }
    if (!checkAndFixConsistency()) {
      clean = true;
    }
    if (clean) {
      deleteDirectory(path);
    }
    pager = new Pager(5000, 1000, this.lastBackupTime, enableBackgroundCleaning);
    if (!path.exists()) {
      path.mkdirs();
    }
    try
    {
      new File(path.getPath() + "/running.data").createNewFile();
    }
    catch (IOException localIOException1)
    {
      throw new Error("Could not create running \"lock\" file");
    }
    this.validatorManager = new FileBasedValidatorManager(path.getPath());
    this.namePersistenceManager = new NamePersistenceManager(path.getPath());
    this.factLibraryManager = new FactLibraryManager(this);
    
    this.identifier = "**frontend**";
    if (persistent) {
      this.rules = RuleBase.make(this, "GLOBAL", false);
    } else {
      this.rules = RuleBase.make(this);
    }
    RuleBase.silent = true;
    try
    {
      System.err.println("Loading metabase decls");
      parse(MetaBase.declarations);
      MetaBase.addTypeMappings(this);
      System.err.println("DONE Loading metabase decls");
      if (loadInitFile)
      {
        boolean silent = RuleBase.silent;
        RuleBase.silent = true;
        NativePredicate.defineNativePredicates(this);
        URL initfile = 
          getClass().getClassLoader().getResource(
          "lib/initfile.rub");
        load(initfile);
        RuleBase.silent = silent;
      }
    }
    catch (ParseException e)
    {
      System.err.println(e.getMessage());
    }
    catch (IOException e)
    {
      System.err.println(e.getMessage());
    }
    catch (TypeModeError e)
    {
      System.err.println(e.getMessage());
    }
  }
  
  private static Collection makeBucketCollection()
  {
    return new LinkedHashSet();
  }
  
  public FrontEnd(boolean loadInitFile)
  {
    this(loadInitFile, new File("./fdb/"), false, null, false, false);
  }
  
  public FrontEnd(boolean loadInitFile, ProgressMonitor mon)
  {
    this(loadInitFile, new File("./fdb/"), false, mon, false, false);
  }
  
  public FrontEnd(boolean initfile, boolean clean)
  {
    this(initfile, new File("./fdb/"), false, null, clean, false);
  }
  
  public void setCacheSize(int cacheSize)
  {
    pager.setCacheSize(cacheSize);
  }
  
  public int getCacheSize()
  {
    return pager.getCacheSize();
  }
  
  private boolean checkAndFixConsistency()
  {
    boolean result = true;
    String stPath = this.path.getPath();
    File crashed = new File(stPath + "/running.data");
    File checkFile = new File(stPath + "/lastBackup.data");
    if (checkFile.exists()) {
      this.lastBackupTime = checkFile.lastModified();
    } else {
      this.lastBackupTime = -1L;
    }
    System.err.println("Checking consistency...");
    if (crashed.exists())
    {
      System.err.println("System was running.");
      if (!checkFile.exists())
      {
        System.err.println("We hadn't backed up before....");
        System.err.println("Data is in an inconsistent state, must delete everything.");
        deleteDirectory(this.path);
        this.path.mkdirs();
      }
      else
      {
        long backupTime = checkFile.lastModified();
        
        System.err.println("Trying to restore backup files.");
        if (!restoreBackups(this.path, backupTime))
        {
          System.err.println("Data is in an inconsistent state, must delete everything.");
          deleteDirectory(this.path);
          this.path.mkdirs();
          result = false;
        }
        else
        {
          System.err.println("Restoring backup file successfull!");
          try
          {
            FileWriter fw = new FileWriter(checkFile, false);
            fw.write("Nothing to see here... move along. ;)");
            fw.close();
          }
          catch (IOException localIOException)
          {
            throw new Error("Could not create backup file");
          }
          this.lastBackupTime = checkFile.lastModified();
        }
      }
      crashed.delete();
    }
    return result;
  }
  
  public long getLastBackupTime()
  {
    return this.lastBackupTime;
  }
  
  private boolean restoreBackups(File f, long backupTime)
  {
    if (f.isDirectory())
    {
      boolean success = true;
      File[] files = f.listFiles();
      for (int i = 0; i < files.length; i++) {
        success &= restoreBackups(files[i], backupTime);
      }
      return success;
    }
    String name = f.getName();
    if ((!name.endsWith(".bup")) && (!name.endsWith(".data")))
    {
      long fileModifiedTime = f.lastModified();
      if (fileModifiedTime > backupTime)
      {
        File backup = new File(f.getAbsolutePath() + ".bup");
        if (backup.exists())
        {
          f.delete();
          backup.renameTo(f);
          return true;
        }
        System.err.println(f.getAbsolutePath() + ": " + backupTime + " ::: " + fileModifiedTime);
        return false;
      }
      return true;
    }
    return true;
  }
  
  private void deleteDirectory(File dir)
  {
    if (dir.isDirectory())
    {
      File[] children = dir.listFiles();
      for (int i = 0; i < children.length; i++) {
        deleteDirectory(children[i]);
      }
    }
    dir.delete();
  }
  
  Validator obtainGroupValidator(Object identifier, boolean temporary)
  {
    if (!(identifier instanceof String)) {
      throw new Error("[ERROR] - obtainGroupValidator - ID needs to be a String");
    }
    Validator result = this.validatorManager.get((String)identifier);
    if ((result != null) && 
      (!result.isValid()))
    {
      this.validatorManager.remove((String)identifier);
      result = null;
    }
    if (result == null)
    {
      result = new Validator();
      this.validatorManager.add(result, (String)identifier);
    }
    return result;
  }
  
  public boolean fastBackupFactBase()
    throws BackupFailedException
  {
    if (pager.isDirty()) {
      return false;
    }
    backupFactBase();
    return true;
  }
  
  public synchronized void backupFactBase()
    throws BackupFailedException
  {
    System.err.println("[DEBUG] - backupFactBase - Entering Backup Method");
    getSynchPolicy().stopSources();
    try
    {
      System.err.println("[DEBUG] - backupFactBase - Backup: Actually Backing Up");
      
      this.rules.backup();
      Object[] buckets = getBuckets().toArray();
      for (int i = 0; i < buckets.length; i++)
      {
        RuleBaseBucket bucket = (RuleBaseBucket)buckets[i];
        bucket.backup();
      }
      pager.backup();
      this.validatorManager.backup();
      this.namePersistenceManager.backup();
      
      File lastBackup = new File(this.path.getPath() + "/lastBackup.data");
      if (lastBackup.exists()) {
        lastBackup.delete();
      }
      try
      {
        FileWriter fw = new FileWriter(lastBackup, false);
        fw.write("Nothing to see here... move along. ;)");
        fw.close();
      }
      catch (IOException localIOException)
      {
        throw new Error("Could not create backup file");
      }
    }
    finally
    {
      getSynchPolicy().allowSources();
    }
    getSynchPolicy().allowSources();
    
    System.err.println("[DEBUG] - backupFactBase - Done Backup Method");
  }
  
  /* Error */
  public synchronized void shutdown()
  {
    // Byte code:
    //   0: getstatic 188	java/lang/System:err	Ljava/io/PrintStream;
    //   3: ldc_w 461
    //   6: invokevirtual 193	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   9: aload_0
    //   10: aconst_null
    //   11: invokevirtual 463	tyRuBa/engine/FrontEnd:setLogger	(LtyRuBa/util/QueryLogger;)V
    //   14: aload_0
    //   15: invokevirtual 418	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   18: invokevirtual 422	tyRuBa/util/SynchPolicy:stopSources	()V
    //   21: aload_0
    //   22: getfield 180	tyRuBa/engine/FrontEnd:rules	LtyRuBa/engine/BasicModedRuleBaseIndex;
    //   25: invokevirtual 429	tyRuBa/engine/BasicModedRuleBaseIndex:backup	()V
    //   28: aload_0
    //   29: invokevirtual 433	tyRuBa/engine/FrontEnd:getBuckets	()Ljava/util/Collection;
    //   32: invokeinterface 467 1 0
    //   37: ifne +50 -> 87
    //   40: aload_0
    //   41: invokevirtual 433	tyRuBa/engine/FrontEnd:getBuckets	()Ljava/util/Collection;
    //   44: invokeinterface 436 1 0
    //   49: astore_1
    //   50: iconst_0
    //   51: istore_2
    //   52: goto +29 -> 81
    //   55: aload_1
    //   56: iload_2
    //   57: aaload
    //   58: checkcast 442	tyRuBa/engine/RuleBaseBucket
    //   61: astore_3
    //   62: aload_3
    //   63: aconst_null
    //   64: invokevirtual 470	tyRuBa/engine/RuleBaseBucket:setLogger	(LtyRuBa/util/QueryLogger;)V
    //   67: aload_3
    //   68: invokevirtual 471	tyRuBa/engine/RuleBaseBucket:isTemporary	()Z
    //   71: ifeq +7 -> 78
    //   74: aload_3
    //   75: invokevirtual 474	tyRuBa/engine/RuleBaseBucket:destroy	()V
    //   78: iinc 2 1
    //   81: iload_2
    //   82: aload_1
    //   83: arraylength
    //   84: if_icmplt -29 -> 55
    //   87: getstatic 90	tyRuBa/engine/FrontEnd:pager	LtyRuBa/util/pager/Pager;
    //   90: invokevirtual 445	tyRuBa/util/pager/Pager:backup	()V
    //   93: aload_0
    //   94: getfield 154	tyRuBa/engine/FrontEnd:validatorManager	LtyRuBa/engine/factbase/ValidatorManager;
    //   97: invokeinterface 446 1 0
    //   102: aload_0
    //   103: getfield 159	tyRuBa/engine/FrontEnd:namePersistenceManager	LtyRuBa/engine/factbase/NamePersistenceManager;
    //   106: invokevirtual 447	tyRuBa/engine/factbase/NamePersistenceManager:backup	()V
    //   109: new 111	java/io/File
    //   112: dup
    //   113: new 118	java/lang/StringBuilder
    //   116: dup
    //   117: aload_0
    //   118: getfield 88	tyRuBa/engine/FrontEnd:path	Ljava/io/File;
    //   121: invokevirtual 120	java/io/File:getPath	()Ljava/lang/String;
    //   124: invokestatic 124	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   127: invokespecial 130	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   130: ldc -123
    //   132: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   138: invokespecial 142	java/io/File:<init>	(Ljava/lang/String;)V
    //   141: invokevirtual 326	java/io/File:delete	()Z
    //   144: pop
    //   145: goto +15 -> 160
    //   148: astore 4
    //   150: aload_0
    //   151: invokevirtual 418	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   154: invokevirtual 448	tyRuBa/util/SynchPolicy:allowSources	()V
    //   157: aload 4
    //   159: athrow
    //   160: aload_0
    //   161: invokevirtual 418	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   164: invokevirtual 448	tyRuBa/util/SynchPolicy:allowSources	()V
    //   167: getstatic 188	java/lang/System:err	Ljava/io/PrintStream;
    //   170: ldc_w 477
    //   173: invokevirtual 193	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   176: return
    // Line number table:
    //   Java source line #353	-> byte code offset #0
    //   Java source line #354	-> byte code offset #9
    //   Java source line #355	-> byte code offset #14
    //   Java source line #357	-> byte code offset #21
    //   Java source line #358	-> byte code offset #28
    //   Java source line #360	-> byte code offset #40
    //   Java source line #361	-> byte code offset #50
    //   Java source line #362	-> byte code offset #55
    //   Java source line #363	-> byte code offset #62
    //   Java source line #364	-> byte code offset #67
    //   Java source line #365	-> byte code offset #74
    //   Java source line #361	-> byte code offset #78
    //   Java source line #369	-> byte code offset #87
    //   Java source line #370	-> byte code offset #93
    //   Java source line #371	-> byte code offset #102
    //   Java source line #374	-> byte code offset #109
    //   Java source line #375	-> byte code offset #148
    //   Java source line #376	-> byte code offset #150
    //   Java source line #377	-> byte code offset #157
    //   Java source line #376	-> byte code offset #160
    //   Java source line #378	-> byte code offset #167
    //   Java source line #379	-> byte code offset #176
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	177	0	this	FrontEnd
    //   49	34	1	buckets	Object[]
    //   51	31	2	i	int
    //   61	14	3	bucket	RuleBaseBucket
    //   148	10	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	148	148	finally
  }
  
  public void redirectOutput(PrintStream output)
  {
    closeOutput();
    this.os = output;
  }
  
  public void flush()
  {
    this.updateCounter += 1L;
  }
  
  public String toString()
  {
    return "FrontEnd: " + this.rules;
  }
  
  public static RBTerm makeCompound(ConstructorType cons, RBTerm[] args)
  {
    return cons.apply(makeTuple(args));
  }
  
  public static RBVariable makeVar(String name)
  {
    return RBVariable.make(name);
  }
  
  public static RBTerm makeTemplateVar(String name)
  {
    return new RBTemplateVar(name);
  }
  
  public static RBVariable makeUniqueVar(String name)
  {
    return RBVariable.makeUnique(name);
  }
  
  public static RBVariable makeIgnoredVar()
  {
    return RBIgnoredVariable.the;
  }
  
  public static RBTerm makeName(String n)
  {
    return RBCompoundTerm.makeJava(n);
  }
  
  public static RBTerm makeInteger(String n)
  {
    return RBCompoundTerm.makeJava(new Integer(n));
  }
  
  public static RBTerm makeInteger(int n)
  {
    return RBCompoundTerm.makeJava(new Integer(n));
  }
  
  public static RBTerm makeReal(String n)
  {
    return RBCompoundTerm.makeJava(new Float(n));
  }
  
  public static RBConjunction makeAnd(RBExpression e1, RBExpression e2)
  {
    return new RBConjunction(e1, e2);
  }
  
  public static RBPredicateExpression makePredicateExpression(String pred, RBTerm[] terms)
  {
    return new RBPredicateExpression(pred, terms);
  }
  
  public static RBTerm makeList(ElementSource els)
  {
    if (els.hasMoreElements())
    {
      RBTerm first = (RBTerm)els.nextElement();
      return new RBPair(first, makeList(els));
    }
    return theEmptyList;
  }
  
  public static RBTerm makeList(RBTerm[] elements)
  {
    return RBPair.make(elements);
  }
  
  public static RBTuple makeTuple(RBTerm[] elements)
  {
    return RBTuple.make(elements);
  }
  
  public static RBTuple makeTuple(ArrayList args)
  {
    return RBTuple.make(args);
  }
  
  public static final RBTerm theEmptyList = RBJavaObjectCompoundTerm.theEmptyList;
  private static final int PROGRESS_BAR_LEN = 100;
  
  public void finalize()
    throws Throwable
  {
    try
    {
      closeOutput();
      super.finalize();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
      if (Aurelizer.debug_sounds != null) {
        Aurelizer.debug_sounds.enter("error");
      }
    }
  }
  
  private void closeOutput()
  {
    if ((this.os != null) && (this.os != System.out) && (this.os != System.err)) {
      this.os.close();
    }
  }
  
  FrontEnd frontend()
  {
    return this;
  }
  
  synchronized void addBucket(RuleBaseBucket bucket)
  {
    if (this.holdingPen == null) {
      this.holdingPen = makeBucketCollection();
    }
    this.holdingPen.add(bucket);
    this.someOutdated |= bucket.isOutdated();
  }
  
  synchronized void removeBucket(RuleBaseBucket bucket)
  {
    if (!getBuckets().remove(bucket)) {
      throw new Error("Attempted to remove bucket which is not present");
    }
    Map predMap = bucket.rulebase.localRuleBase.typeInfoBase.predicateMap;
    Iterator it = predMap.values().iterator();
    while (it.hasNext()) {
      ((PredInfo)it.next());
    }
  }
  
  public Collection getBuckets()
  {
    if (this.holdingPen != null)
    {
      this.myBuckets.addAll(this.holdingPen);
      this.holdingPen = null;
    }
    return this.myBuckets;
  }
  
  private synchronized int bucketCount()
  {
    return this.myBuckets.size() + (this.holdingPen == null ? 0 : this.holdingPen.size());
  }
  
  ModedRuleBaseIndex rulebase()
  {
    return this.rules;
  }
  
  /* Error */
  public synchronized void updateBuckets()
    throws TypeModeError, ParseException
  {
    // Byte code:
    //   0: lconst_0
    //   1: lstore_1
    //   2: aload_0
    //   3: iconst_0
    //   4: putfield 82	tyRuBa/engine/FrontEnd:progressBar	I
    //   7: aload_0
    //   8: iconst_0
    //   9: putfield 84	tyRuBa/engine/FrontEnd:updatedBuckets	I
    //   12: aload_0
    //   13: getfield 63	tyRuBa/engine/FrontEnd:someOutdated	Z
    //   16: ifeq +216 -> 232
    //   19: aload_0
    //   20: invokevirtual 418	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   23: invokevirtual 422	tyRuBa/util/SynchPolicy:stopSources	()V
    //   26: invokestatic 689	java/lang/System:currentTimeMillis	()J
    //   29: lstore_1
    //   30: getstatic 188	java/lang/System:err	Ljava/io/PrintStream;
    //   33: ldc_w 692
    //   36: invokevirtual 193	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   39: aload_0
    //   40: getfield 65	tyRuBa/engine/FrontEnd:progressMonitor	LtyRuBa/engine/ProgressMonitor;
    //   43: ifnull +17 -> 60
    //   46: aload_0
    //   47: getfield 65	tyRuBa/engine/FrontEnd:progressMonitor	LtyRuBa/engine/ProgressMonitor;
    //   50: ldc_w 694
    //   53: bipush 100
    //   55: invokeinterface 696 3 0
    //   60: aload_0
    //   61: invokevirtual 433	tyRuBa/engine/FrontEnd:getBuckets	()Ljava/util/Collection;
    //   64: astore_3
    //   65: aload_0
    //   66: aload_3
    //   67: invokespecial 700	tyRuBa/engine/FrontEnd:updateSomeBuckets	(Ljava/util/Collection;)V
    //   70: goto +18 -> 88
    //   73: aload_0
    //   74: getfield 78	tyRuBa/engine/FrontEnd:holdingPen	Ljava/util/Collection;
    //   77: astore_3
    //   78: aload_0
    //   79: invokevirtual 433	tyRuBa/engine/FrontEnd:getBuckets	()Ljava/util/Collection;
    //   82: pop
    //   83: aload_0
    //   84: aload_3
    //   85: invokespecial 700	tyRuBa/engine/FrontEnd:updateSomeBuckets	(Ljava/util/Collection;)V
    //   88: aload_0
    //   89: getfield 78	tyRuBa/engine/FrontEnd:holdingPen	Ljava/util/Collection;
    //   92: ifnonnull -19 -> 73
    //   95: aload_0
    //   96: iconst_0
    //   97: putfield 63	tyRuBa/engine/FrontEnd:someOutdated	Z
    //   100: goto +70 -> 170
    //   103: astore 4
    //   105: aload_0
    //   106: invokevirtual 418	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   109: invokevirtual 448	tyRuBa/util/SynchPolicy:allowSources	()V
    //   112: aload_0
    //   113: getfield 65	tyRuBa/engine/FrontEnd:progressMonitor	LtyRuBa/engine/ProgressMonitor;
    //   116: ifnull +12 -> 128
    //   119: aload_0
    //   120: getfield 65	tyRuBa/engine/FrontEnd:progressMonitor	LtyRuBa/engine/ProgressMonitor;
    //   123: invokeinterface 704 1 0
    //   128: getstatic 188	java/lang/System:err	Ljava/io/PrintStream;
    //   131: new 118	java/lang/StringBuilder
    //   134: dup
    //   135: ldc_w 707
    //   138: invokespecial 130	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   141: invokestatic 689	java/lang/System:currentTimeMillis	()J
    //   144: lload_1
    //   145: lsub
    //   146: invokevirtual 364	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   149: ldc_w 709
    //   152: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   155: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   158: invokevirtual 193	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   161: getstatic 90	tyRuBa/engine/FrontEnd:pager	LtyRuBa/util/pager/Pager;
    //   164: invokevirtual 711	tyRuBa/util/pager/Pager:printStats	()V
    //   167: aload 4
    //   169: athrow
    //   170: aload_0
    //   171: invokevirtual 418	tyRuBa/engine/FrontEnd:getSynchPolicy	()LtyRuBa/util/SynchPolicy;
    //   174: invokevirtual 448	tyRuBa/util/SynchPolicy:allowSources	()V
    //   177: aload_0
    //   178: getfield 65	tyRuBa/engine/FrontEnd:progressMonitor	LtyRuBa/engine/ProgressMonitor;
    //   181: ifnull +12 -> 193
    //   184: aload_0
    //   185: getfield 65	tyRuBa/engine/FrontEnd:progressMonitor	LtyRuBa/engine/ProgressMonitor;
    //   188: invokeinterface 704 1 0
    //   193: getstatic 188	java/lang/System:err	Ljava/io/PrintStream;
    //   196: new 118	java/lang/StringBuilder
    //   199: dup
    //   200: ldc_w 707
    //   203: invokespecial 130	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   206: invokestatic 689	java/lang/System:currentTimeMillis	()J
    //   209: lload_1
    //   210: lsub
    //   211: invokevirtual 364	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   214: ldc_w 709
    //   217: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   220: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   223: invokevirtual 193	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   226: getstatic 90	tyRuBa/engine/FrontEnd:pager	LtyRuBa/util/pager/Pager;
    //   229: invokevirtual 711	tyRuBa/util/pager/Pager:printStats	()V
    //   232: return
    // Line number table:
    //   Java source line #547	-> byte code offset #0
    //   Java source line #548	-> byte code offset #2
    //   Java source line #549	-> byte code offset #12
    //   Java source line #550	-> byte code offset #19
    //   Java source line #552	-> byte code offset #26
    //   Java source line #553	-> byte code offset #30
    //   Java source line #554	-> byte code offset #39
    //   Java source line #555	-> byte code offset #46
    //   Java source line #556	-> byte code offset #60
    //   Java source line #557	-> byte code offset #65
    //   Java source line #558	-> byte code offset #70
    //   Java source line #560	-> byte code offset #73
    //   Java source line #561	-> byte code offset #78
    //   Java source line #562	-> byte code offset #83
    //   Java source line #558	-> byte code offset #88
    //   Java source line #564	-> byte code offset #95
    //   Java source line #566	-> byte code offset #103
    //   Java source line #567	-> byte code offset #105
    //   Java source line #568	-> byte code offset #112
    //   Java source line #569	-> byte code offset #119
    //   Java source line #570	-> byte code offset #128
    //   Java source line #571	-> byte code offset #161
    //   Java source line #572	-> byte code offset #167
    //   Java source line #567	-> byte code offset #170
    //   Java source line #568	-> byte code offset #177
    //   Java source line #569	-> byte code offset #184
    //   Java source line #570	-> byte code offset #193
    //   Java source line #571	-> byte code offset #226
    //   Java source line #574	-> byte code offset #232
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	233	0	this	FrontEnd
    //   1	209	1	startTime	long
    //   64	21	3	bucketColl	Collection
    //   103	65	4	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   26	103	103	finally
  }
  
  private void updateSomeBuckets(Collection bucketColl)
    throws TypeModeError, ParseException
  {
    for (Iterator buckets = bucketColl.iterator(); buckets.hasNext();)
    {
      RuleBaseBucket bucket = (RuleBaseBucket)buckets.next();
      if (bucket.isOutdated()) {
        bucket.clear();
      }
    }
    for (Iterator buckets = bucketColl.iterator(); buckets.hasNext();)
    {
      RuleBaseBucket bucket = (RuleBaseBucket)buckets.next();
      if (bucket.isOutdated()) {
        bucket.doUpdate();
      }
      if (this.progressMonitor != null)
      {
        this.updatedBuckets += 1;
        int newProgressBar = this.updatedBuckets * 100 / bucketCount();
        if (newProgressBar > this.progressBar)
        {
          this.progressMonitor.worked(newProgressBar - this.progressBar);
          this.progressBar = newProgressBar;
        }
      }
    }
  }
  
  void autoUpdateBuckets()
    throws TypeModeError, ParseException
  {
    if (RuleBase.autoUpdate) {
      updateBuckets();
    }
  }
  
  private SynchPolicy synchPol = null;
  
  public SynchPolicy getSynchPolicy()
  {
    if (this.synchPol == null) {
      this.synchPol = new SynchPolicy(this);
    }
    return this.synchPol;
  }
  
  public String getStoragePath()
  {
    return this.path.getPath();
  }
  
  public ValidatorManager getValidatorManager()
  {
    return this.validatorManager;
  }
  
  public NamePersistenceManager getNamePersistenceManager()
  {
    return this.namePersistenceManager;
  }
  
  public FactLibraryManager getFactLibraryManager()
  {
    return this.factLibraryManager;
  }
  
  public Pager getPager()
  {
    return pager;
  }
  
  public String getIdentifier()
  {
    return this.identifier;
  }
  
  public void crash()
  {
    pager.crash();
    pager = null;
  }
  
  public void enableMetaData()
  {
    this.rules.enableMetaData();
  }
}
