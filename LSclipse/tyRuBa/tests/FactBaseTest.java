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
package tyRuBa.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Random;
import junit.framework.Assert;
import tyRuBa.engine.BackupFailedException;
import tyRuBa.engine.FrontEnd;
import tyRuBa.engine.RuleBaseBucket;
import tyRuBa.modes.TypeModeError;
import tyRuBa.parser.ParseException;
import tyRuBa.util.Aurelizer;

public class FactBaseTest
  extends TyrubaTest
{
  private class TesterThread
    extends Thread
  {
    Throwable crash = null;
    
    private TesterThread(String string)
    {
      super();
    }
  }
  
  private class RandomQueriesThread
    extends FactBaseTest.TesterThread
  {
    int howmany;
    
    RandomQueriesThread(int name, int howmany)
    {
      super(name, null);
      this.howmany = howmany;
    }
    
    public void run()
    {
      try
      {
        FactBaseTest.this.doRandomQueries(this.howmany);
      }
      catch (Throwable e)
      {
        this.crash = e;
        e.printStackTrace();
        if (Aurelizer.debug_sounds != null) {
          Aurelizer.debug_sounds.enter("error");
        }
      }
    }
  }
  
  private class OutdatingThread
    extends FactBaseTest.TesterThread
  {
    int howmany;
    
    OutdatingThread(String name, int howmany)
    {
      super(name, null);
      this.howmany = howmany;
    }
    
    public void run()
    {
      try
      {
        for (int i = 0; i < this.howmany; i++)
        {
          FactBaseTest.this.doRandomQueries(1);
          FactBaseTest.this.outdateSomebuckets(i, i + 1);
        }
      }
      catch (Throwable e)
      {
        this.crash = e;
        throw new Error("Thread " + getName() + " crashed: " + e);
      }
    }
  }
  
  private class KillingThread
    extends FactBaseTest.TesterThread
  {
    int howmany;
    
    KillingThread(String name)
    {
      super(name, null);
    }
    
    public void run()
    {
      try
      {
        while (FactBaseTest.this.test_atoms.length > 1)
        {
          FactBaseTest.this.doRandomQueries(1);
          FactBaseTest.this.shrinkAtoms();
        }
      }
      catch (Throwable e)
      {
        this.crash = e;
        e.printStackTrace();
        throw new Error("Thread " + getName() + " crashed: " + e);
      }
    }
  }
  
  private class DoRandomThingsThread
    extends FactBaseTest.TesterThread
  {
    int howmany;
    String name;
    
    DoRandomThingsThread(String name, int howmany)
    {
      super(name, null);
      this.name = name;
      this.howmany = howmany;
    }
    
    public void run()
    {
      Random rnd = new Random();
      try
      {
        for (int i = 0; i < this.howmany; i++) {
          switch (rnd.nextInt(10))
          {
          case 0: 
            System.err.println(this.name + " " + i + " BEG backup");
            FactBaseTest.this.frontend.backupFactBase();
            System.err.println(this.name + " " + i + " END backup");
            break;
          case 1: 
            System.err.println(this.name + " " + i + " BEG outdating");
            FactBaseTest.this.outdateSomebuckets(rnd.nextInt(2), rnd.nextInt(4) + 1);
            System.err.println(this.name + " " + i + " END outdating");
            break;
          default: 
            System.err.println(this.name + " " + i + " BEG query");
            FactBaseTest.this.doRandomQueries(1);
            System.err.println(this.name + " " + i + " END query");
          }
        }
      }
      catch (BackupFailedException e)
      {
        System.err.print("Backup fact base failed, but this is normal behavior");
        e.printStackTrace();
      }
      catch (Throwable e)
      {
        this.crash = e;
        e.printStackTrace();
        throw new Error("Thread " + getName() + " crashed: " + e);
      }
    }
  }
  
  static String test_space = "test_space";
  private static final boolean regenrubs = true;
  private static int init_numatoms = 10;
  private static int maxarity = 4;
  private int bucketLoads;
  File factstore = new File(test_space, "fact_store");
  String declarations_fle = test_space + "/declarations.rub";
  String[] bucket_fle;
  private RuleBaseBucket[] buckets;
  String[] test_preds = {
    "foo", "bar", "zor", "snol", "brol", "wols" };
  int[] pred_arity;
  String[] test_atoms;
  String[] initial_test_atoms;
  
  public FactBaseTest(String arg0)
  {
    super(arg0);
  }
  
  protected void setUp(boolean reconnect)
    throws Exception
  {
    FrontEnd old_frontend = this.frontend;
    if ((reconnect) && (this.frontend != null)) {
      try
      {
        this.frontend.backupFactBase();
      }
      catch (BackupFailedException localBackupFailedException)
      {
        super.setUpNoFrontend();
        this.frontend = old_frontend;
        return;
      }
    }
    super.setUpNoFrontend();
    if (!reconnect)
    {
      File test_dir = new File(test_space);
      
      makeEmptyDir(test_dir);
      makeEmptyDir(this.factstore);
      this.frontend = new FrontEnd(true, this.factstore, true, null, true, false);
    }
    else
    {
      this.frontend = new FrontEnd(true, this.factstore, true, null, false, false);
    }
    if (!reconnect) {
      generateRubFiles();
    }
    this.frontend.load(this.declarations_fle);
    
    this.buckets = new RuleBaseBucket[init_numatoms];
    for (int i = 0; i < this.bucket_fle.length; i++)
    {
      System.err.println("Making bucket: " + this.bucket_fle[i]);
      this.buckets[i] = new RubFileBucket(this.frontend, this.bucket_fle[i]);
    }
  }
  
  private void generateRubFiles()
    throws IOException
  {
    int countFacts = 0;
    PrintWriter declarations = makeFile(this.declarations_fle);
    for (int i = 0; i < this.test_preds.length; i++)
    {
      declarations.print(this.test_preds[i] + " :: ");
      for (int j = 0; j < this.pred_arity[i]; j++)
      {
        if (j > 0) {
          declarations.print(", ");
        }
        declarations.print("String");
      }
      declarations.println();
      
      declarations.print("PERSISTENT MODES (");
      for (int j = 0; j < this.pred_arity[i]; j++)
      {
        if (j > 0) {
          declarations.print(", ");
        }
        declarations.print("F");
      }
      declarations.println(") IS NONDET END");
    }
    declarations.close();
    
    PrintWriter[] bucket = new PrintWriter[this.bucket_fle.length];
    for (int i = 0; i < bucket.length; i++) {
      bucket[i] = makeFile(this.bucket_fle[i]);
    }
    for (int currPred = 0; currPred < this.test_preds.length; currPred++)
    {
      int[] currAtom = new int[this.pred_arity[currPred]];
      for (int i = 0; i < currAtom.length; i++) {
        currAtom[i] = 0;
      }
      boolean stop = false;
      while (!stop)
      {
        int currBucket = currAtom[(currAtom.length - 1)];
        bucket[currBucket].print(this.test_preds[currPred] + "(");
        for (int i = 0; i < currAtom.length; i++)
        {
          if (i > 0) {
            bucket[currBucket].print(",");
          }
          bucket[currBucket].print(this.test_atoms[currAtom[i]]);
        }
        bucket[currBucket].println(").");
        
        countFacts++;
        
        stop = nextParamList(0, currAtom);
      }
    }
    for (int i = 0; i < bucket.length; i++) {
      bucket[i].close();
    }
    System.err.println("========= generated " + countFacts + " test facts =======");
  }
  
  protected void setUp()
    throws Exception
  {
    this.bucketLoads = 0;
    
    this.test_atoms = new String[init_numatoms];
    this.initial_test_atoms = this.test_atoms;
    for (int i = 0; i < this.test_atoms.length; i++) {
      this.test_atoms[i] = (this.test_preds[(i % this.test_preds.length)] + i);
    }
    this.pred_arity = new int[this.test_preds.length];
    for (int i = 0; i < this.test_preds.length; i++)
    {
      int arity = i % maxarity + 1;
      this.pred_arity[i] = arity;
    }
    this.bucket_fle = new String[this.test_atoms.length];
    for (int i = 0; i < this.test_atoms.length; i++) {
      this.bucket_fle[i] = (test_space + "/" + this.test_atoms[i] + ".rub");
    }
    setUp(false);
  }
  
  protected void tearDown()
    throws Exception
  {
    super.tearDown();
  }
  
  private void makeEmptyDir(File dir)
  {
    if (dir.exists()) {
      deleteDir(dir);
    }
    dir.mkdir();
  }
  
  private boolean deleteDir(File dir)
  {
    if (dir.isDirectory())
    {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++)
      {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete();
  }
  
  public void testRandomConcurrency()
    throws Throwable
  {
    doRandomQueries(1);
    
    TesterThread[] thread = new TesterThread[10];
    for (int i = 0; i < thread.length; i++)
    {
      thread[i] = new DoRandomThingsThread(i, 40);
      thread[i].start();
      System.out.println("Thread " + i + " started");
    }
    for (int i = 0; i < thread.length; i++)
    {
      thread[i].join();
      if (thread[i].crash != null) {
        throw thread[i].crash;
      }
      System.out.println("Thread " + i + " ended");
    }
  }
  
  public void testConcurrentKilling()
    throws Throwable
  {
    System.err.println("====== TEST: testConcurrentKilling ===");
    
    doRandomQueries(1);
    
    TesterThread[] thread = new TesterThread[20];
    for (int i = 0; i < thread.length - 1; i++)
    {
      thread[i] = new RandomQueriesThread(i, 20);
      thread[i].start();
      System.out.println("Thread " + i + " started");
    }
    thread[(thread.length - 1)] = new KillingThread("killing");
    thread[(thread.length - 1)].start();
    System.out.println("Killing thread started");
    for (int i = 0; i < thread.length; i++)
    {
      thread[i].join();
      if (thread[i].crash != null) {
        throw thread[i].crash;
      }
      System.out.println("Thread " + i + " ended");
    }
  }
  
  public void testReconnecting()
    throws Exception
  {
    int expectedBucketLoads = numbuckets();
    
    doSomeQueries();
    setUp(true);
    doSomeQueries();
    for (int i = 2; i <= 5; i++)
    {
      expectedBucketLoads += outdateSomebuckets(0, i);
      
      setUp(true);
      System.err.println(i);
      doSomeQueries();
    }
    assertEquals("Number of buckets loaded", expectedBucketLoads, this.bucketLoads);
    
    this.frontend.backupFactBase();
    
    doSomeQueries();
    this.frontend.parse(this.test_preds[0] + "(ThisIsNewAfterSave).");
    test_must_succeed(this.test_preds[0] + "(ThisIsNewAfterSave)");
    
    this.frontend.crash();
    this.frontend = null;
    
    setUp(true);
    doSomeQueries();
    test_must_fail(this.test_preds[0] + "(ThisIsNewAfterSave)");
  }
  
  public void testBackupTestsCase0()
    throws Exception
  {
    this.bucketLoads = 0;
    doSomeQueries();
    assertEquals(numbuckets(), this.bucketLoads);
    this.frontend.backupFactBase();
    this.frontend.shutdown();
    this.frontend = null;
    setUp(true);
    this.bucketLoads = 0;
    doSomeQueries();
    assertEquals(0, this.bucketLoads);
  }
  
  public void testBackupTestsCase1()
    throws Exception
  {
    this.bucketLoads = 0;
    doSomeQueries();
    assertEquals(numbuckets(), this.bucketLoads);
    this.frontend.crash();
    this.frontend = null;
    setUp(true);
    this.bucketLoads = 0;
    doSomeQueries();
    assertEquals(numbuckets(), this.bucketLoads);
  }
  
  public void testBackupTestsCase2()
    throws Exception
  {
    this.bucketLoads = 0;
    doSomeQueries();
    assertEquals(numbuckets(), this.bucketLoads);
    this.frontend.backupFactBase();
    this.frontend.crash();
    this.frontend = null;
    setUp(true);
    this.bucketLoads = 0;
    doSomeQueries();
    assertEquals(0, this.bucketLoads);
  }
  
  public void testBackupTestsCase3()
    throws Exception
  {
    this.bucketLoads = 0;
    doSomeQueries();
    assertEquals(numbuckets(), this.bucketLoads);
    this.frontend.backupFactBase();
    this.buckets[0].parse(this.test_preds[0] + "(newnewnew).");
    test_must_succeed(this.test_preds[0] + "(newnewnew)");
    this.frontend.crash();
    this.frontend = null;
    setUp(true);
    this.bucketLoads = 0;
    doSomeQueries();
    
    assertTrue((this.bucketLoads == 10) || (this.bucketLoads == 0));
    test_must_fail(this.test_preds[0] + "(newnewnew)");
  }
  
  public void testStress()
    throws ParseException, TypeModeError
  {
    int expectedBucketLoads = 0;
    for (int space = 5; space <= 1000; space += 50)
    {
      System.out.println("Run with cache size target = " + space);
      
      expectedBucketLoads += outdateSomebuckets(0, 1);
      doSomeQueries();
    }
    assertEquals("Number of buckets loaded ", expectedBucketLoads, this.bucketLoads);
  }
  
  public void testJustSomeQueries()
    throws ParseException, TypeModeError
  {
    System.err.println("==== TEST: JustSomeQueries ===");
    doSomeQueries();
    assertEquals("Number of buckets loaded ", numbuckets(), this.bucketLoads);
  }
  
  public void testBucketKilling()
    throws ParseException, TypeModeError
  {
    System.err.println("==== TEST: BucketKilling ===");
    doSomeQueries();
    while (this.test_atoms.length > 1)
    {
      shrinkAtoms();
      doSomeQueries();
    }
  }
  
  public void testBucketKillingMany()
    throws ParseException, TypeModeError
  {
    System.err.println("==== TEST: BucketKillingMany ===");
    doSomeQueries();
    while (this.test_atoms.length > 1) {
      shrinkAtoms();
    }
    doSomeQueries();
  }
  
  private void shrinkAtoms()
  {
    RuleBaseBucket toDestroy = this.buckets[(this.test_atoms.length - 1)];
    synchronized (this.frontend)
    {
      toDestroy.destroy();
      
      System.err.println("Destroying bucket: " + toDestroy);
      String[] old = this.test_atoms;
      this.test_atoms = new String[old.length - 1];
      System.arraycopy(old, 0, this.test_atoms, 0, this.test_atoms.length);
    }
  }
  
  private void doSomeQueries()
    throws ParseException, TypeModeError
  {
    for (int i = 0; i < this.test_preds.length; i++)
    {
      int predictCount = 1;
      String query = this.test_preds[i] + "(";
      for (int j = 0; j < this.pred_arity[i]; j++)
      {
        if (j > 0) {
          query = query + ",";
        }
        query = query + "?x" + j;
        if (j + 1 == this.pred_arity[i]) {
          predictCount *= this.test_atoms.length;
        } else {
          predictCount *= init_numatoms;
        }
      }
      query = query + ")";
      System.err.println(query);
      if (predictCount > 0) {
        test_must_succeed(query);
      }
      test_resultcount(query, predictCount);
    }
    for (int i = 0; i < this.test_preds.length; i++)
    {
      String query = this.test_preds[i] + "(?x";
      for (int j = 1; j < this.pred_arity[i]; j++) {
        query = query + "," + this.test_atoms[(j * 113 % this.test_atoms.length)];
      }
      query = query + ")";
      System.out.println(query);
      if (this.pred_arity[i] > 1) {
        test_must_findall(query, "?x", this.initial_test_atoms);
      } else {
        test_must_findall(query, "?x", this.test_atoms);
      }
    }
  }
  
  public void testBucketOutdating()
    throws ParseException, TypeModeError
  {
    int expectedBucketLoads = numbuckets();
    
    doSomeQueries();
    
    expectedBucketLoads += outdateSomebuckets(0, 1);
    doSomeQueries();
    
    expectedBucketLoads += outdateSomebuckets(1, 2);
    doSomeQueries();
    
    expectedBucketLoads += outdateSomebuckets(0, 2);
    doSomeQueries();
    
    expectedBucketLoads += outdateSomebuckets(1, 4);
    doSomeQueries();
    
    assertEquals("Number of buckets loaded ", expectedBucketLoads, this.bucketLoads);
  }
  
  private int numbuckets()
  {
    return this.test_atoms.length;
  }
  
  void doRandomQueries(int howmany)
    throws ParseException, TypeModeError
  {
    Random rnd = new Random();
    for (int i = 0; i < howmany; i++) {
      doRandomQuery(rnd);
    }
  }
  
  private void doRandomQuery(Random rnd)
    throws ParseException, TypeModeError
  {
    int prednum = rnd.nextInt(this.test_preds.length);
    String query = this.test_preds[prednum] + "(";
    int predicted_results = 1;
    boolean var = false;
    int atom = -1;
    synchronized (this.frontend)
    {
      int atoms_at_start = this.test_atoms.length;
      for (int argNum = 0; argNum < this.pred_arity[prednum]; argNum++)
      {
        if (argNum > 0) {
          query = query + ",";
        }
        var = rnd.nextBoolean();
        atom = rnd.nextInt(this.test_atoms.length);
        if (var)
        {
          query = query + "?x" + argNum;
          predicted_results *= init_numatoms;
        }
        else
        {
          query = query + this.test_atoms[atom];
        }
      }
      query = query + ")";
    }
    int atoms_at_start;
    System.err.println("Doing Query");
    if (var) {
      test_must_succeed(query);
    }
    int results = get_resultcount(query);
    synchronized (this.frontend)
    {
      int kill_adjusted = kill_adjust_predicted(predicted_results, atoms_at_start, var, atom);
      if (results != kill_adjusted) {
        System.err.println("Q = " + query + " R = " + results + " P = " + kill_adjusted + " A = " + atoms_at_start);
      }
      while ((results != kill_adjusted) && (atoms_at_start > this.test_atoms.length))
      {
        kill_adjusted = 
          kill_adjust_predicted(predicted_results, --atoms_at_start, var, atom);
        System.err.println("retry Q = " + query + " R = " + results + " P = " + kill_adjusted + " A = " + atoms_at_start);
        if (results == kill_adjusted) {
          return;
        }
      }
      Assert.assertEquals("Result count wrong for " + query, kill_adjusted, results);
    }
    System.err.println("Done Query");
  }
  
  private int kill_adjust_predicted(int predicted_results, int numatoms, boolean var, int atom)
  {
    int kill_adjusted = 
      var ? predicted_results / init_numatoms * numatoms : 
      predicted_results;
    if ((!var) && (atom >= numatoms)) {
      kill_adjusted = 0;
    }
    return kill_adjusted;
  }
  
  public void testConcurrentQueries()
    throws Throwable
  {
    doRandomQueries(1);
    
    RandomQueriesThread[] thread = new RandomQueriesThread[10];
    for (int i = 0; i < thread.length; i++)
    {
      thread[i] = new RandomQueriesThread(i, 10);
      thread[i].start();
      System.out.println("Thread " + i + " started");
    }
    for (int i = 0; i < thread.length; i++)
    {
      thread[i].join();
      if (thread[i].crash != null) {
        throw thread[i].crash;
      }
      System.out.println("Thread " + i + " ended");
    }
  }
  
  public void testConcurrentOutdating()
    throws Throwable
  {
    doRandomQueries(1);
    
    TesterThread[] thread = new TesterThread[11];
    for (int i = 0; i < thread.length - 1; i++)
    {
      thread[i] = new RandomQueriesThread(i, 20);
      thread[i].start();
      System.out.println("Thread " + i + " started");
    }
    thread[(thread.length - 1)] = new OutdatingThread("outdating", 20);
    thread[(thread.length - 1)].start();
    System.out.println("Outdating thread started");
    for (int i = 0; i < thread.length; i++)
    {
      thread[i].join();
      if (thread[i].crash != null) {
        throw thread[i].crash;
      }
      System.out.println("Thread " + i + " ended");
    }
  }
  
  private int outdateSomebuckets(int ofset, int mod)
  {
    int count = 0;
    for (int i = ofset; i < this.buckets.length; i += mod)
    {
      this.buckets[i].setOutdated();
      count++;
    }
    return count;
  }
  
  private boolean nextParamList(int i, int[] currAtom)
  {
    if (i >= currAtom.length) {
      return true;
    }
    currAtom[i] = ((currAtom[i] + 1) % this.test_atoms.length);
    if (currAtom[i] == 0) {
      return nextParamList(i + 1, currAtom);
    }
    return false;
  }
  
  private static PrintWriter makeFile(String name)
  {
    File path = new File(name);
    try
    {
      return new PrintWriter(new FileWriter(path));
    }
    catch (IOException e)
    {
      throw new Error("Error making logfile: " + e.getMessage());
    }
  }
  
  class RubFileBucket
    extends RuleBaseBucket
  {
    String myfile;
    
    RubFileBucket(FrontEnd fe, String filename)
    {
      super(filename);
      this.myfile = filename;
    }
    
    public void update()
      throws ParseException, TypeModeError
    {
      try
      {
        load(this.myfile);
        FactBaseTest.this.bucketLoads += 1;
      }
      catch (IOException e)
      {
        throw new Error("IOError for file " + this.myfile + ": " + e.getMessage());
      }
    }
    
    public String toString()
    {
      return "RubFileBucket(" + this.myfile + ")";
    }
  }
}
