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
package lsclipse;

import changetypes.ASTVisitorAtomicChange;
import changetypes.AtomicChange;
import changetypes.AtomicChange.ChangeTypes;
import changetypes.ChangeSet;
import changetypes.Fact;
import changetypes.Fact.FactTypes;
import changetypes.FactBase;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lsclipse.dialogs.ProgressBarDialog;
import lsclipse.utils.StringCleaner;
import lsd.facts.LSDRuleEnumerator;
import lsd.rule.LSDFact;
import lsd.rule.LSDPredicate;
import lsd.rule.LSDRule;
import metapackage.MetaInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class LSDiffRunner
{
  private static final int NUM_THREADS = 4;
  private static final long TIMEOUT = 60L;
  private static Map<String, IJavaElement> oldTypeToFileMap_ = new ConcurrentHashMap();
  
  public static Map<String, IJavaElement> getOldTypeToFileMap()
  {
    return Collections.unmodifiableMap(oldTypeToFileMap_);
  }
  
  private static Map<String, IJavaElement> newTypeToFileMap_ = new ConcurrentHashMap();
  
  public static Map<String, IJavaElement> getNewTypeToFileMap()
  {
    return Collections.unmodifiableMap(newTypeToFileMap_);
  }
  
  public boolean doFactExtractionForRefFinder(String proj1, String proj2, ProgressBarDialog progbar)
  {
    if (!doFactExtraction(proj1, proj2, progbar)) {
      return false;
    }
    installLSDiff();
    return true;
  }
  
  public List<LSDResult> doLSDiff(String proj1, String proj2, ProgressBarDialog progbar)
  {
    if (!doFactExtraction(proj1, proj2, progbar)) {
      return null;
    }
    progbar.setMessage("Invoking LSDiff... ");
    BufferedWriter output = null;
    LSDRuleEnumerator enumerator = null;
    List<LSDRule> rules = null;
    File resultsFile;
    try
    {
      installLSDiff();
      
      File winnowingRulesFile = new File(MetaInfo.winnowings);
      File typeLevelWinnowingRulesFile = new File(MetaInfo.modifiedWinnowings);
      resultsFile = new File(MetaInfo.resultsFile);
      File twoKBFile = new File(MetaInfo.lsclipse2KB);
      File deltaKBFile = new File(MetaInfo.lsclipseDelta);
      enumerator = new LSDRuleEnumerator(twoKBFile, deltaKBFile, winnowingRulesFile, resultsFile, 
        3, 0.75D, 1, 100, 10, typeLevelWinnowingRulesFile, output);
      rules = enumerator.levelIncrementLearning(System.out);
    }
    catch (Exception localException)
    {
      progbar.appendError("Unable to do LSDiff analysis");
      progbar.dispose();
      return null;
    }
    if (rules == null)
    {
      progbar.appendError("Unable to derive any rules!");
      progbar.dispose();
      return null;
    }
    progbar.appendLog("OK\n");
    progbar.appendLog("Found " + rules.size() + " rules\n");
    
    List<LSDResult> res = new ArrayList();
    for (LSDRule r : rules)
    {
      LSDResult result = new LSDResult();
      result.num_matches = enumerator.countMatches(r);
      result.num_counter = enumerator.countExceptions(r);
      result.desc = r.toString();
      result.examples = enumerator.getRelevantFacts(r);
      result.exceptions = enumerator.getExceptions(r);
      
      res.add(result);
    }
    progbar.setStep(5);
    progbar.setMessage("Cleaning up... ");
    progbar.appendLog("OK\n");
    
    progbar.dispose();
    
    return res;
  }
  
  private boolean doFactExtraction(String proj1, String proj2, ProgressBarDialog progbar)
  {
    Set<ICompilationUnit> allFiles = null;
    
    progbar.setStep(1);
    progbar.setMessage("Retrieving facts for FB1... \n");
    FactBase fb1 = new FactBase();
    long beforefacts1 = System.currentTimeMillis();
    try
    {
      allFiles = getFiles(proj1);
    }
    catch (Exception localException1) {}
    if (allFiles == null) {
      return false;
    }
    progbar.appendLog("Scanning " + allFiles.size() + " files...");
    Iterator<ICompilationUnit> iter = allFiles.iterator();
    ExecutorService execService = Executors.newFixedThreadPool(4);
    List<Future<FactBase>> futures = new LinkedList();
    FactGetter fg;
    while (iter.hasNext())
    {
      ICompilationUnit file = (ICompilationUnit)iter.next();
      fg = new FactGetter(file, oldTypeToFileMap_);
      futures.add(execService.submit(fg));
    }
    execService.shutdown();
    try
    {
      execService.awaitTermination(60L, TimeUnit.MINUTES);
    }
    catch (InterruptedException e1)
    {
      e1.printStackTrace();
    }
    for (Future<FactBase> f : futures) {
      try
      {
        fb1.addAll((Collection)f.get());
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      catch (ExecutionException e)
      {
        e.printStackTrace();
      }
    }
    int numFacts1 = fb1.size();
    progbar.appendLog("Extraction OK! Extracted " + numFacts1 + " facts for FB1\n");
    
    System.currentTimeMillis();
    
    progbar.setMessage("Adding derived facts for FB1... \n");
    fb1.deriveFacts();
    progbar.appendLog("Derivation OK! Added " + (fb1.size() - numFacts1) + " facts to FB1\n");
    progbar.appendLog("All done! FB1 contains " + fb1.size() + " facts\n");
    
    long afterderivedfacts1 = System.currentTimeMillis();
    
    progbar.setStep(2);
    progbar.setMessage("Retrieving facts for FB2... \n");
    FactBase fb2 = new FactBase();
    long beforefacts2 = System.currentTimeMillis();
    try
    {
      allFiles = getFiles(proj2);
    }
    catch (Exception localException2) {}
    if (allFiles == null) {
      return false;
    }
    progbar.appendLog("Scanning " + allFiles.size() + " files...");
    iter = allFiles.iterator();
    execService = Executors.newFixedThreadPool(4);
    futures.clear();
    FactGetter fg;
    while (iter.hasNext())
    {
      ICompilationUnit file = (ICompilationUnit)iter.next();
      fg = new FactGetter(file, newTypeToFileMap_);
      futures.add(execService.submit(fg));
    }
    execService.shutdown();
    try
    {
      execService.awaitTermination(60L, TimeUnit.MINUTES);
    }
    catch (InterruptedException e1)
    {
      e1.printStackTrace();
    }
    for (Future<FactBase> f : futures) {
      try
      {
        fb2.addAll((Collection)f.get());
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      catch (ExecutionException e)
      {
        e.printStackTrace();
      }
    }
    int numFacts2 = fb2.size();
    progbar.appendLog("Extraction OK! Extracted " + numFacts2 + " facts for FB2\n");
    
    System.currentTimeMillis();
    
    progbar.setMessage("Adding derived facts for FB2... \n");
    fb2.deriveFacts();
    progbar.appendLog("Derivation OK! Added " + (fb2.size() - numFacts2) + " facts to FB2\n");
    progbar.appendLog("All done! FB2 contains " + fb2.size() + " facts\n");
    long afterderivedfacts2 = System.currentTimeMillis();
    
    progbar.setMessage("Doing post processing for FB2... ");
    progbar.appendLog("OK\n");
    
    progbar.setStep(3);
    progbar.setMessage("Computing factbase differences... ");
    long beforediff = System.currentTimeMillis();
    ChangeSet cs = fb2.diff(fb1);
    progbar.appendLog("All done! " + cs.size() + " changes found\n");
    
    long afterdiff = System.currentTimeMillis();
    
    progbar.setStep(4);
    
    progbar.setMessage("Preparing to run LSDiff...\n");
    progbar.appendLog("Converting atomic change to LSDiff changes... ");
    long beforeconversion = System.currentTimeMillis();
    ArrayList<LSDFact> input2kbFacts = new ArrayList();
    ArrayList<LSDFact> inputDeltaFacts = new ArrayList();
    for (Fact f : fb1) {
      input2kbFacts.add(makeLSDFact(f, "before"));
    }
    System.out.println("***************************************");
    for (Fact f : fb2) {
      input2kbFacts.add(makeLSDFact(f, "after"));
    }
    for (AtomicChange ac : cs)
    {
      LSDFact f = makeLSDFact(ac);
      if (f != null) {
        inputDeltaFacts.add(f);
      }
    }
    progbar.appendLog("OK\n");
    long afterconversion = System.currentTimeMillis();
    
    progbar.appendLog("Writing to LSDiff input files... \n");
    BufferedWriter lsd2kbfile = null;
    long beforeoutput = System.currentTimeMillis();
    int counter;
    try
    {
      File f2KBfile = new File(MetaInfo.lsclipse2KB);
      File dir = f2KBfile.getParentFile();
      if (!dir.exists()) {
        dir.mkdirs();
      }
      progbar.appendLog("  Writing 2KB to " + MetaInfo.lsclipse2KB + "\n");
      lsd2kbfile = new BufferedWriter(new FileWriter(MetaInfo.lsclipse2KB));
      
      counter = 0;
      for (LSDFact f : input2kbFacts)
      {
        counter++;
        if (f != null) {
          lsd2kbfile.append(f.toString() + ".\n");
        }
      }
      lsd2kbfile.close();
    }
    catch (IOException localIOException1)
    {
      progbar.appendError("Unable to create 2KB input file! Exiting...");
      progbar.dispose();
      return false;
    }
    BufferedWriter lsddeltafile = null;
    try
    {
      progbar.appendLog("  Writing deltas to " + MetaInfo.lsclipseDelta + "\n");
      lsddeltafile = new BufferedWriter(new FileWriter(MetaInfo.lsclipseDelta));
      lsddeltafile.close();
      for (LSDFact f : inputDeltaFacts)
      {
        lsddeltafile = new BufferedWriter(new FileWriter(MetaInfo.lsclipseDelta, true));
        lsddeltafile.append(f.toString() + ".\n");
        lsddeltafile.close();
      }
    }
    catch (IOException localIOException2)
    {
      progbar.appendError("Unable to create delta KB input file! Exiting...");
      progbar.dispose();
      return false;
    }
    progbar.appendLog("OK\n");
    long afteroutput = System.currentTimeMillis();
    progbar.appendLog("\nTotal time for fb1 extraction(ms): " + (afterderivedfacts1 - beforefacts1));
    progbar.appendLog("\nTotal time for fb2 extraction(ms): " + (afterderivedfacts2 - beforefacts2));
    progbar.appendLog("\nTotal time for diff(ms): " + (afterdiff - beforediff));
    progbar.appendLog("\nTotal time for conversion to LSD(ms): " + (afterconversion - beforeconversion));
    progbar.appendLog("\nTotal time for write to file(ms): " + (afteroutput - beforeoutput));
    
    return true;
  }
  
  private static void installLSDiff()
  {
    File srcfile = MetaInfo.srcDir;
    srcfile.mkdirs();
    File resfile = MetaInfo.resDir;
    resfile.mkdirs();
    File fdbfile = MetaInfo.fdbDir;
    fdbfile.mkdirs();
    
    File included2KBFile = MetaInfo.included2kb;
    if (!included2KBFile.exists())
    {
      InputStream is = LSclipse.getDefault().getClass().getResourceAsStream("/lib/" + included2KBFile.getName());
      writeStreamToFile(is, included2KBFile);
    }
    File includedDeltaKBFile = MetaInfo.includedDelta;
    if (!includedDeltaKBFile.exists())
    {
      InputStream is = LSclipse.getDefault().getClass().getResourceAsStream("/lib/" + includedDeltaKBFile.getName());
      writeStreamToFile(is, includedDeltaKBFile);
    }
    File winnowingRulesFile = new File(MetaInfo.winnowings);
    if (!winnowingRulesFile.exists())
    {
      InputStream is = LSclipse.getDefault().getClass().getResourceAsStream("/lib/" + winnowingRulesFile.getName());
      writeStreamToFile(is, winnowingRulesFile);
    }
    File typeLevelWinnowingRulesFile = new File(MetaInfo.winnowings);
    if (!typeLevelWinnowingRulesFile.exists())
    {
      InputStream is = LSclipse.getDefault().getClass().getResourceAsStream("/lib/" + typeLevelWinnowingRulesFile.getName());
      writeStreamToFile(is, typeLevelWinnowingRulesFile);
    }
    File includedPrimedDeltaKBFile = new File(MetaInfo.lsclipseRefactorDeltaPrimed);
    if (!includedPrimedDeltaKBFile.exists())
    {
      InputStream is = LSclipse.getDefault().getClass().getResourceAsStream("/lib/" + includedPrimedDeltaKBFile.getName());
      writeStreamToFile(is, includedPrimedDeltaKBFile);
    }
    File includedPred1File = new File(MetaInfo.lsclipseRefactorPred);
    if (!includedPred1File.exists())
    {
      InputStream is = LSclipse.getDefault().getClass().getResourceAsStream("/lib/" + includedPred1File.getName());
      writeStreamToFile(is, includedPred1File);
    }
  }
  
  static class FactGetter
    implements Callable<FactBase>
  {
    Map<String, IJavaElement> typeToFileMap_;
    ICompilationUnit file_;
    
    public FactGetter(ICompilationUnit file, Map<String, IJavaElement> typeToFileMap)
    {
      this.file_ = file;
      this.typeToFileMap_ = typeToFileMap;
    }
    
    public FactBase call()
      throws Exception
    {
      ASTParser parser = ASTParser.newParser(3);
      parser.setResolveBindings(true);
      parser.setSource(this.file_);
      try
      {
        parser.setUnitName(this.file_.getUnderlyingResource().getProjectRelativePath().toOSString());
      }
      catch (JavaModelException localJavaModelException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      try
      {
        ASTVisitorAtomicChange acvisitor = new ASTVisitorAtomicChange();
        ASTNode ast = parser.createAST(new NullProgressMonitor());
        ast.accept(acvisitor);
        this.typeToFileMap_.putAll(acvisitor.getTypeToFileMap());
        
        return acvisitor.facts;
      }
      catch (Exception e)
      {
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("Exception: " + e.getMessage());
      }
      return new FactBase();
    }
  }
  
  private static LSDPredicate makeLSDPredicate(Fact.FactTypes type, String modifier)
  {
    switch (type)
    {
    case ACCESSES: 
      return LSDPredicate.getPredicate(modifier + "_" + "package");
    case CALLS: 
      return LSDPredicate.getPredicate(modifier + "_" + "type");
    case CONDITIONAL: 
      return LSDPredicate.getPredicate(modifier + "_" + "field");
    case CAST: 
      return LSDPredicate.getPredicate(modifier + "_" + "method");
    case EXTENDS: 
      return LSDPredicate.getPredicate(modifier + "_" + "return");
    case GETTER: 
      return LSDPredicate.getPredicate(modifier + "_" + "subtype");
    case FIELDMODIFIER: 
      return LSDPredicate.getPredicate(modifier + "_" + "accesses");
    case FIELDOFTYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "calls");
    case INHERITEDMETHOD: 
      return LSDPredicate.getPredicate(modifier + "_" + "inheritedfield");
    case LOCALVAR: 
      return LSDPredicate.getPredicate(modifier + "_" + "inheritedmethod");
    case FIELD: 
      return LSDPredicate.getPredicate(modifier + "_" + "fieldoftype");
    case METHOD: 
      return LSDPredicate.getPredicate(modifier + "_" + "typeintype");
    case IMPLEMENTS: 
      return LSDPredicate.getPredicate(modifier + "_" + "extends");
    case INHERITEDFIELD: 
      return LSDPredicate.getPredicate(modifier + "_" + "implements");
    case METHODSIGNATURE: 
      return LSDPredicate.getPredicate(modifier + "_" + "conditional");
    case METHODBODY: 
      return LSDPredicate.getPredicate(modifier + "_" + "methodbody");
    case PACKAGE: 
      return LSDPredicate.getPredicate(modifier + "_" + "parameter");
    case PARAMETER: 
      return LSDPredicate.getPredicate(modifier + "_" + "methodmodifier");
    case RETURN: 
      return LSDPredicate.getPredicate(modifier + "_" + "fieldmodifier");
    case SETTER: 
      return LSDPredicate.getPredicate(modifier + "_" + "cast");
    case SUBTYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "trycatch");
    case THROWN: 
      return LSDPredicate.getPredicate(modifier + "_" + "throws");
    case TRYCATCH: 
      return LSDPredicate.getPredicate(modifier + "_" + "getter");
    case TYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "setter");
    case TYPEINTYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "localvar");
    }
    return null;
  }
  
  private static LSDFact makeLSDFact(Fact f, String modifier)
  {
    LSDPredicate pred = makeLSDPredicate(f.type, modifier);
    List<String> constants = new ArrayList();
    int numparams = f.params.size();
    if ((f.type == Fact.FactTypes.METHOD) || (f.type == Fact.FactTypes.FIELD) || (f.type == Fact.FactTypes.TYPE)) {
      numparams--;
    }
    for (int i = 0; i < numparams; i++) {
      constants.add(StringCleaner.cleanupString((String)f.params.get(i)));
    }
    return LSDFact.createLSDFact(pred, constants, true);
  }
  
  private static LSDPredicate makeLSDPredicate(AtomicChange ac)
  {
    String modifier = "";
    if ((ac.type.ordinal() >= AtomicChange.ChangeTypes.ADD_PACKAGE.ordinal()) && 
      (ac.type.ordinal() <= AtomicChange.ChangeTypes.ADD_FIELDMODIFIER.ordinal())) {
      modifier = "added";
    } else if ((ac.type.ordinal() >= AtomicChange.ChangeTypes.DEL_PACKAGE.ordinal()) && 
      (ac.type.ordinal() <= AtomicChange.ChangeTypes.DEL_FIELDMODIFIER.ordinal())) {
      modifier = "deleted";
    } else {
      modifier = "modified";
    }
    switch (ac.type)
    {
    case ADD_ACCESSES: 
    case CHANGE_METHODSIGNATURE: 
    case DEL_TYPEINTYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "package");
    case ADD_CALLS: 
    case DEL_ACCESSES: 
    case MOD_FIELD: 
      return LSDPredicate.getPredicate(modifier + "_" + "type");
    case ADD_CONDITIONAL: 
    case DEL_CAST: 
    case MOD_PACKAGE: 
      return LSDPredicate.getPredicate(modifier + "_" + "field");
    case ADD_CAST: 
    case DEL_CALLS: 
    case MOD_METHOD: 
      return LSDPredicate.getPredicate(modifier + "_" + "method");
    case ADD_EXTENDS: 
    case DEL_CONDITIONAL: 
      return LSDPredicate.getPredicate(modifier + "_" + "return");
    case ADD_GETTER: 
    case DEL_FIELDOFTYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "subtype");
    case ADD_FIELDMODIFIER: 
    case DEL_FIELD: 
      return LSDPredicate.getPredicate(modifier + "_" + "accesses");
    case ADD_FIELDOFTYPE: 
    case DEL_FIELDMODIFIER: 
      return LSDPredicate.getPredicate(modifier + "_" + "calls");
    case ADD_INHERITEDMETHOD: 
    case DEL_INHERITEDFIELD: 
      return LSDPredicate.getPredicate(modifier + "_" + "inheritedfield");
    case ADD_LOCALVAR: 
    case DEL_INHERITEDMETHOD: 
      return LSDPredicate.getPredicate(modifier + "_" + "inheritedmethod");
    case ADD_FIELD: 
    case DEL_EXTENDS: 
      return LSDPredicate.getPredicate(modifier + "_" + "fieldoftype");
    case ADD_METHOD: 
    case DEL_LOCALVAR: 
      return LSDPredicate.getPredicate(modifier + "_" + "typeintype");
    case ADD_IMPLEMENTS: 
    case DEL_GETTER: 
      return LSDPredicate.getPredicate(modifier + "_" + "extends");
    case ADD_INHERITEDFIELD: 
    case DEL_IMPLEMENTS: 
      return LSDPredicate.getPredicate(modifier + "_" + "implements");
    case ADD_THROWN: 
    case DEL_SUBTYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "conditional");
    case ADD_SUBTYPE: 
    case DEL_SETTER: 
      return LSDPredicate.getPredicate(modifier + "_" + "methodbody");
    case ADD_TRYCATCH: 
    case DEL_THROWN: 
      return LSDPredicate.getPredicate(modifier + "_" + "parameter");
    case ADD_TYPE: 
    case DEL_TRYCATCH: 
      return LSDPredicate.getPredicate(modifier + "_" + "methodmodifier");
    case ADD_TYPEINTYPE: 
    case DEL_TYPE: 
      return LSDPredicate.getPredicate(modifier + "_" + "fieldmodifier");
    case ADD_METHODBODY: 
    case DEL_METHOD: 
      return LSDPredicate.getPredicate(modifier + "_" + "cast");
    case ADD_METHODMODIFIER: 
    case DEL_METHODBODY: 
      return LSDPredicate.getPredicate(modifier + "_" + "trycatch");
    case ADD_PACKAGE: 
    case DEL_METHODMODIFIER: 
      return LSDPredicate.getPredicate(modifier + "_" + "throws");
    case ADD_PARAMETER: 
    case DEL_PACKAGE: 
      return LSDPredicate.getPredicate(modifier + "_" + "getter");
    case ADD_RETURN: 
    case DEL_PARAMETER: 
      return LSDPredicate.getPredicate(modifier + "_" + "setter");
    case ADD_SETTER: 
    case DEL_RETURN: 
      return LSDPredicate.getPredicate(modifier + "_" + "localvar");
    }
    return null;
  }
  
  private static LSDFact makeLSDFact(AtomicChange ac)
  {
    LSDPredicate pred = makeLSDPredicate(ac);
    List<String> constants = new ArrayList();
    for (String s : ac.params) {
      constants.add(StringCleaner.cleanupString(s));
    }
    return LSDFact.createLSDFact(pred, constants, true);
  }
  
  private static Set<ICompilationUnit> getFiles(String projname)
    throws CoreException
  {
    IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
    IProject proj = ws.getProject(projname);
    IJavaProject javaProject = JavaCore.create(proj);
    Set<ICompilationUnit> files = new HashSet();
    javaProject.open(new NullProgressMonitor());
    IPackageFragment[] arrayOfIPackageFragment;
    int j = (arrayOfIPackageFragment = javaProject.getPackageFragments()).length;
    for (int i = 0; i < j; i++)
    {
      IPackageFragment packFrag = arrayOfIPackageFragment[i];
      ICompilationUnit[] arrayOfICompilationUnit;
      int m = (arrayOfICompilationUnit = packFrag.getCompilationUnits()).length;
      for (int k = 0; k < m; k++)
      {
        ICompilationUnit icu = arrayOfICompilationUnit[k];
        files.add(icu);
      }
    }
    javaProject.close();
    return files;
  }
  
  private static void writeStreamToFile(InputStream is, File file)
  {
    try
    {
      OutputStream out = new FileOutputStream(file);
      byte[] buf = new byte['Ѐ'];
      int len;
      while ((len = is.read(buf)) > 0)
      {
        int len;
        out.write(buf, 0, len);
      }
      out.close();
      is.close();
    }
    catch (IOException localIOException) {}
  }
}
