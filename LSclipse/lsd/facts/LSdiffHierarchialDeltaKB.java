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
package lsd.facts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lsd.io.LSDTyrubaFactReader;
import lsd.rule.LSDBinding;
import lsd.rule.LSDConst;
import lsd.rule.LSDFact;
import lsd.rule.LSDPredicate;

public class LSdiffHierarchialDeltaKB
{
  public String ADDED = "ADD";
  public String DELETED = "DELETE";
  public String MODIFIED = "MODIFY";
  public static final int PACKAGE_LEVEL = 0;
  public static final int TYPE_LEVEL = 1;
  public static final int TYPE_DEPENDENCY_LEVEL = 2;
  public static final int METHOD_LEVEL = 3;
  public static final int FIELD_LEVEL = 4;
  public static final int BODY_LEVEL = 5;
  HashMap<String, TreeSet<LSDFact>> packageLevel = new HashMap();
  HashMap<String, TreeSet<LSDFact>> typeLevel = new HashMap();
  HashMap<String, TreeSet<LSDFact>> methodLevel = new HashMap();
  HashMap<String, TreeSet<LSDFact>> fieldLevel = new HashMap();
  private LSdiffFilter filter = new LSdiffFilter(true, true, true, true, true);
  private ArrayList<LSDFact> originalDeltaKB;
  
  public static void main(String[] args)
  {
    File deltaKBFile = new File("input/jfreechart/1.0.12_1.0.13delta.rub");
    ArrayList<LSDFact> deltaKB = new LSDTyrubaFactReader(deltaKBFile).getFacts();
    LSdiffHierarchialDeltaKB modifiedFB = new LSdiffHierarchialDeltaKB(deltaKB);
    TreeSet<LSDFact> ontheflyDeltaKB = new TreeSet();
    TreeSet<LSDFact> ontheflyDeltaKB2 = new TreeSet();
    
    File temp = new File("temp-fileterdDelta");
    File temp2 = new File("temp-hFilteredDelta");
    try
    {
      PrintStream p = new PrintStream(temp);
      PrintStream p2 = new PrintStream(temp2);
      
      modifiedFB.filterFacts(p, ontheflyDeltaKB);
      modifiedFB.topDownTraversal2(p2, ontheflyDeltaKB2);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    boolean result1 = modifiedFB.checkEquivalence(deltaKB, ontheflyDeltaKB);
    
    boolean result2 = modifiedFB.checkEquivalence(deltaKB, ontheflyDeltaKB2);
    System.err.println(result1);
    System.err.println(result2);
  }
  
  public LSdiffHierarchialDeltaKB(ArrayList<LSDFact> deltaKB)
  {
    this.originalDeltaKB = deltaKB;
    constructFieldLevel();
    constructMethodLevel();
    constructTypeLevel();
    constructPackageLevel();
  }
  
  public TreeSet<LSDFact> expandCluster(List<LSDFact> cluster, int level)
  {
    switch (level)
    {
    case 0: 
      return getPackageLevelFacts(null);
    case 1: 
      return expandPackageLevelCluster2TypeElements(null, cluster);
    case 2: 
      return expandTypeLevelCluster2TypeDependencies(null, cluster);
    case 3: 
      return expandTypeLevelCluster2Methods(null, cluster);
    case 4: 
      return expandTypeLevelCluster2Fields(null, cluster);
    case 5: 
      return expandMethodLevelCluster2Bodies(null, cluster);
    }
    return null;
  }
  
  private boolean checkEquivalence(ArrayList<LSDFact> deltaKB, TreeSet<LSDFact> ontheflyDeltaKB)
  {
    ArrayList<LSDFact> tempOriginal = new ArrayList(deltaKB);
    System.out.println("original:\t" + tempOriginal.size());
    tempOriginal.removeAll(ontheflyDeltaKB);
    ArrayList<LSDFact> tempOntheFly = new ArrayList();
    for (LSDFact f : ontheflyDeltaKB) {
      if (f.getPredicate().getName().indexOf("modified_") < 0) {
        tempOntheFly.add(f);
      }
    }
    System.out.println("onthefly:\t" + tempOntheFly.size());
    tempOntheFly.removeAll(deltaKB);
    
    System.out.println("onthefly - original:\t" + tempOntheFly.size());
    for (LSDFact f : tempOntheFly) {
      System.out.println(f);
    }
    System.out.println("original - onthefly:\t" + tempOriginal.size());
    for (LSDFact f : tempOriginal) {
      System.out.println(f);
    }
    return (tempOriginal.size() == 0) && (tempOntheFly.size() == 0);
  }
  
  private void filterFacts(PrintStream p, TreeSet<LSDFact> output)
  {
    output.addAll(getPackageLevelFacts(p));
    output.addAll(expandPackageLevelCluster2TypeElements(p, null));
    output.addAll(expandTypeLevelCluster2TypeDependencies(p, null));
    output.addAll(expandTypeLevelCluster2Methods(p, null));
    output.addAll(expandTypeLevelCluster2Fields(p, null));
    output.addAll(expandMethodLevelCluster2Bodies(p, null));
  }
  
  public void filterFacts(PrintStream p, TreeSet<LSDFact> output, LSdiffFilter filter)
  {
    assert (filter != null);
    if (filter.packageLevel) {
      output.addAll(getPackageLevelFacts(p));
    }
    if (filter.typeLevel) {
      output.addAll(expandPackageLevelCluster2TypeElements(p, null));
    }
    if (filter.typeLevel) {
      output.addAll(expandTypeLevelCluster2TypeDependencies(p, null));
    }
    if (filter.methodLevel) {
      output.addAll(expandTypeLevelCluster2Methods(p, null));
    }
    if (filter.fieldLevel) {
      output.addAll(expandTypeLevelCluster2Fields(p, null));
    }
    if (filter.bodyLevel) {
      output.addAll(expandMethodLevelCluster2Bodies(p, null));
    }
  }
  
  private TreeSet<LSDFact> getPackageLevelFacts(PrintStream p)
  {
    TreeSet<LSDFact> ontheflyDeltaKB = new TreeSet();
    Iterator localIterator2;
    for (Iterator localIterator1 = this.packageLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.packageLevel.get(kind)).iterator(); continue;LSDFact packageF = (LSDFact)localIterator2.next();
      if (p != null) {
        p.println(packageF);
      }
      ontheflyDeltaKB.add(packageF);
    }
    return ontheflyDeltaKB;
  }
  
  private TreeSet<LSDFact> expandPackageLevelCluster2TypeElements(PrintStream p, List<LSDFact> packageLevelCluster)
  {
    TreeSet<LSDFact> ontheflyDeltaKB = new TreeSet();
    TreeSet<String> packageConstants = null;
    if (packageLevelCluster != null)
    {
      packageConstants = new TreeSet();
      for (LSDFact packageF : packageLevelCluster) {
        packageConstants.add(((LSDBinding)packageF.getBindings().get(0)).getGroundConst());
      }
    }
    Iterator localIterator2;
    for (??? = this.typeLevel.keySet().iterator(); ???.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)???.next();
      localIterator2 = ((TreeSet)this.typeLevel.get(kind)).iterator(); continue;LSDFact typeF = (LSDFact)localIterator2.next();
      String containerPackage = ((LSDBinding)typeF.getBindings().get(2)).getGroundConst();
      if ((packageConstants == null) || (packageConstants.contains(containerPackage)))
      {
        if (p != null) {
          p.println("\t" + typeF);
        }
        ontheflyDeltaKB.add(typeF);
      }
    }
    return ontheflyDeltaKB;
  }
  
  private TreeSet<LSDFact> expandTypeLevelCluster2TypeDependencies(PrintStream p, List<LSDFact> typeLevelCluster)
  {
    TreeSet<LSDFact> ontheflyDeltaKB = new TreeSet();
    TreeSet<String> typeConstants = null;
    if (typeLevelCluster != null)
    {
      typeConstants = new TreeSet();
      for (LSDFact typeF : typeLevelCluster) {
        typeConstants.add(((LSDBinding)typeF.getBindings().get(0)).getGroundConst());
      }
    }
    for (LSDFact fact : this.originalDeltaKB)
    {
      String involvedType = null;
      if (fact.getPredicate().getName().indexOf("_typeintype") > 0) {
        involvedType = ((LSDBinding)fact.getBindings().get(1)).getGroundConst();
      } else if (fact.getPredicate().getName().indexOf("_extends") > 0) {
        involvedType = ((LSDBinding)fact.getBindings().get(1)).getGroundConst();
      } else if (fact.getPredicate().getName().indexOf("_implements") > 0) {
        involvedType = ((LSDBinding)fact.getBindings().get(1)).getGroundConst();
      } else if (fact.getPredicate().getName().indexOf("_inheritedfield") > 0) {
        involvedType = ((LSDBinding)fact.getBindings().get(2)).getGroundConst();
      } else if (fact.getPredicate().getName().indexOf("_inheritedmethod") > 0) {
        involvedType = ((LSDBinding)fact.getBindings().get(2)).getGroundConst();
      }
      if ((involvedType != null) && ((typeConstants == null) || (typeConstants.contains(involvedType))))
      {
        if (p != null) {
          p.println("\t\t\t" + fact);
        }
        ontheflyDeltaKB.add(fact);
      }
    }
    return ontheflyDeltaKB;
  }
  
  private TreeSet<LSDFact> expandTypeLevelCluster2Methods(PrintStream p, List<LSDFact> typeLevelCluster)
  {
    TreeSet<LSDFact> ontheflyDeltaKB = new TreeSet();
    TreeSet<String> typeConstants = null;
    TreeSet<String> methodConstants = new TreeSet();
    if (typeLevelCluster != null)
    {
      typeConstants = new TreeSet();
      for (LSDFact typeF : typeLevelCluster) {
        typeConstants.add(((LSDBinding)typeF.getBindings().get(0)).getGroundConst());
      }
    }
    Iterator localIterator2;
    for (??? = this.methodLevel.keySet().iterator(); ???.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)???.next();
      localIterator2 = ((TreeSet)this.methodLevel.get(kind)).iterator(); continue;LSDFact methodF = (LSDFact)localIterator2.next();
      String containerType = ((LSDBinding)methodF.getBindings().get(2)).getGroundConst();
      if ((typeConstants == null) || (typeConstants.contains(containerType)))
      {
        if (p != null) {
          p.println("\t\t" + methodF);
        }
        ontheflyDeltaKB.add(methodF);
        methodConstants.add(((LSDBinding)methodF.getBindings().get(0)).getGroundConst());
      }
    }
    for (LSDFact fact : this.originalDeltaKB) {
      if (fact.getPredicate().getName().indexOf("_return") > 0)
      {
        String involvedMethod = ((LSDBinding)fact.getBindings().get(0)).getGroundConst();
        if (methodConstants.contains(involvedMethod)) {
          ontheflyDeltaKB.add(fact);
        }
      }
    }
    return ontheflyDeltaKB;
  }
  
  private TreeSet<LSDFact> expandTypeLevelCluster2Fields(PrintStream p, List<LSDFact> typeLevelCluster)
  {
    TreeSet<LSDFact> ontheflyDeltaKB = new TreeSet();
    TreeSet<String> typeConstants = null;
    TreeSet<String> fieldConstants = new TreeSet();
    if (typeLevelCluster != null)
    {
      typeConstants = new TreeSet();
      for (LSDFact typeF : typeLevelCluster) {
        typeConstants.add(((LSDBinding)typeF.getBindings().get(0)).getGroundConst());
      }
    }
    Iterator localIterator2;
    for (??? = this.fieldLevel.keySet().iterator(); ???.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)???.next();
      localIterator2 = ((TreeSet)this.fieldLevel.get(kind)).iterator(); continue;LSDFact fieldF = (LSDFact)localIterator2.next();
      String containerType = ((LSDBinding)fieldF.getBindings().get(2))
        .getGroundConst();
      if ((typeConstants == null) || 
        (typeConstants.contains(containerType)))
      {
        if (p != null) {
          p.println("\t\t" + fieldF);
        }
        ontheflyDeltaKB.add(fieldF);
        fieldConstants.add(((LSDBinding)fieldF.getBindings().get(0))
          .getGroundConst());
      }
    }
    for (LSDFact fact : this.originalDeltaKB) {
      if (fact.getPredicate().getName().indexOf("_fieldoftype") > 0)
      {
        String involvedField = ((LSDBinding)fact.getBindings().get(0)).getGroundConst();
        if (fieldConstants.contains(involvedField)) {
          ontheflyDeltaKB.add(fact);
        }
      }
    }
    return ontheflyDeltaKB;
  }
  
  private TreeSet<LSDFact> expandMethodLevelCluster2Bodies(PrintStream p, List<LSDFact> methodLevelCluster)
  {
    TreeSet<LSDFact> ontheflyDeltaKB = new TreeSet();
    TreeSet<String> methodConstants = null;
    if (methodLevelCluster != null)
    {
      methodConstants = new TreeSet();
      for (LSDFact methodF : methodLevelCluster) {
        methodConstants.add(((LSDBinding)methodF.getBindings().get(0)).getGroundConst());
      }
    }
    for (LSDFact fact : this.originalDeltaKB)
    {
      String involvedMethod = null;
      if (fact.getPredicate().getName().indexOf("_calls") > 0) {
        involvedMethod = ((LSDBinding)fact.getBindings().get(0)).getGroundConst();
      } else if (fact.getPredicate().getName().indexOf("_accesses") > 0) {
        involvedMethod = ((LSDBinding)fact.getBindings().get(1)).getGroundConst();
      }
      if ((involvedMethod != null) && ((methodConstants == null) || (methodConstants.contains(involvedMethod))))
      {
        if (p != null) {
          p.println("\t\t\t" + fact);
        }
        ontheflyDeltaKB.add(fact);
      }
    }
    return ontheflyDeltaKB;
  }
  
  private void printPakcageLevelFactStat(PrintStream p)
  {
    if (p != null) {
      p.println("# added_package:\t" + ((TreeSet)this.packageLevel.get(this.ADDED)).size());
    }
    if (p != null) {
      p.println("# deleted_package:\t" + ((TreeSet)this.packageLevel.get(this.DELETED)).size());
    }
    if (p != null) {
      p.println("# modified_package:\t" + ((TreeSet)this.packageLevel.get(this.MODIFIED)).size());
    }
  }
  
  private void printTypeLevelFactStat(PrintStream p)
  {
    if (p != null) {
      p.println("# added_type:\t" + ((TreeSet)this.typeLevel.get(this.ADDED)).size());
    }
    if (p != null) {
      p.println("# deleted_type:\t" + ((TreeSet)this.typeLevel.get(this.DELETED)).size());
    }
    if (p != null) {
      p.println("# modified_type:\t" + ((TreeSet)this.typeLevel.get(this.MODIFIED)).size());
    }
  }
  
  private void printMethodLevelFactStat(PrintStream p)
  {
    if (p != null) {
      p.println("# added_method:\t" + ((TreeSet)this.methodLevel.get(this.ADDED)).size());
    }
    if (p != null) {
      p.println("# deleted_method:\t" + ((TreeSet)this.methodLevel.get(this.DELETED)).size());
    }
    if (p != null) {
      p.println("# modified_method:\t" + ((TreeSet)this.methodLevel.get(this.MODIFIED)).size());
    }
  }
  
  private void printFieldLevelFactStat(PrintStream p)
  {
    if (p != null) {
      p.println("# added_field:\t" + ((TreeSet)this.fieldLevel.get(this.ADDED)).size());
    }
    if (p != null) {
      p.println("# deleted_field:\t" + ((TreeSet)this.fieldLevel.get(this.DELETED)).size());
    }
    if (p != null) {
      p.println("# modified_field:\t" + ((TreeSet)this.fieldLevel.get(this.MODIFIED)).size());
    }
  }
  
  private void constructFieldLevel()
  {
    TreeSet<LSDFact> addedField = new TreeSet();
    TreeSet<LSDFact> deletedField = new TreeSet();
    TreeSet<LSDFact> modifiedField = new TreeSet();
    String predName;
    for (LSDFact fact : this.originalDeltaKB)
    {
      predName = fact.getPredicate().getName();
      if (predName.equals("added_field")) {
        addedField.add(fact);
      } else if (predName.equals("deleted_field")) {
        deletedField.add(fact);
      }
    }
    int counter = 0;
    for (LSDFact fact : this.originalDeltaKB)
    {
      String predName = fact.getPredicate().getName();
      counter++;
      System.out.println(counter + ". \"" + predName + "\":" + fact);
      if ((predName.equals("added_fieldoftype")) || 
        (predName.equals("deleted_fieldoftype")))
      {
        List<LSDBinding> bindings = fact.getBindings();
        LSDBinding firstBinding = (LSDBinding)bindings.get(0);
        LSDFact mfield = LSDConst.createModifiedField(firstBinding
          .getGroundConst());
        if (!containsTheSameFact(addedField, deletedField, mfield)) {
          modifiedField.add(mfield);
        }
      }
    }
    this.fieldLevel.put(this.ADDED, addedField);
    this.fieldLevel.put(this.DELETED, deletedField);
    this.fieldLevel.put(this.MODIFIED, modifiedField);
  }
  
  private void constructMethodLevel()
  {
    TreeSet<LSDFact> addedMethod = new TreeSet();
    TreeSet<LSDFact> deletedMethod = new TreeSet();
    TreeSet<LSDFact> modifiedMethod = new TreeSet();
    for (LSDFact fact : this.originalDeltaKB)
    {
      String predName = fact.getPredicate().getName();
      if (predName.equals("added_method")) {
        addedMethod.add(fact);
      } else if (predName.equals("deleted_method")) {
        deletedMethod.add(fact);
      }
    }
    for (LSDFact fact : this.originalDeltaKB)
    {
      String predName = fact.getPredicate().getName();
      if ((predName.equals("added_return")) || 
        (predName.equals("deleted_return")))
      {
        List<LSDBinding> bindings = fact.getBindings();
        LSDBinding firstBinding = (LSDBinding)bindings.get(0);
        LSDFact mmethod = LSDConst.createModifiedMethod(firstBinding
          .getGroundConst());
        if (!containsTheSameFact(addedMethod, deletedMethod, mmethod)) {
          modifiedMethod.add(mmethod);
        }
      }
      else if ((predName.equals("deleted_calls")) || 
        (predName.equals("added_calls")))
      {
        List<LSDBinding> bindings = fact.getBindings();
        LSDBinding firstBinding = (LSDBinding)bindings.get(0);
        LSDFact mmethod = LSDConst.createModifiedMethod(firstBinding
          .getGroundConst());
        if (!containsTheSameFact(addedMethod, deletedMethod, mmethod)) {
          modifiedMethod.add(mmethod);
        }
      }
      else if ((predName.equals("added_accesses")) || 
        (predName.equals("deleted_accesses")))
      {
        List<LSDBinding> bindings = fact.getBindings();
        LSDBinding secondBinding = (LSDBinding)bindings.get(1);
        LSDFact mmethod = LSDConst.createModifiedMethod(secondBinding
          .getGroundConst());
        if (!containsTheSameFact(addedMethod, deletedMethod, mmethod)) {
          modifiedMethod.add(mmethod);
        }
      }
    }
    this.methodLevel.put(this.ADDED, addedMethod);
    this.methodLevel.put(this.DELETED, deletedMethod);
    this.methodLevel.put(this.MODIFIED, modifiedMethod);
  }
  
  private void constructTypeLevel()
  {
    TreeSet<LSDFact> addedType = new TreeSet();
    TreeSet<LSDFact> deletedType = new TreeSet();
    TreeSet<LSDFact> modifiedType = new TreeSet();
    for (LSDFact fact : this.originalDeltaKB)
    {
      String predName = fact.getPredicate().getName();
      if (predName.equals("added_type")) {
        addedType.add(fact);
      } else if (predName.equals("deleted_type")) {
        deletedType.add(fact);
      }
    }
    List<LSDBinding> bindings;
    for (LSDFact fact : this.originalDeltaKB)
    {
      String predName = fact.getPredicate().getName();
      if (predName.endsWith("_typeintype"))
      {
        List<LSDBinding> bindings = fact.getBindings();
        LSDBinding secondBinding = (LSDBinding)bindings.get(1);
        LSDFact mtype = LSDConst.createModifiedType(secondBinding.getGroundConst());
        if (!containsTheSameFact(addedType, deletedType, mtype)) {
          modifiedType.add(mtype);
        }
      }
      else if ((predName.endsWith("_extends")) || (predName.endsWith("_implements")))
      {
        List<LSDBinding> bindings = fact.getBindings();
        LSDBinding secondBinding = (LSDBinding)bindings.get(1);
        LSDFact mtype = LSDConst.createModifiedType(secondBinding.getGroundConst());
        if (!containsTheSameFact(addedType, deletedType, mtype)) {
          modifiedType.add(mtype);
        }
      }
      else if (predName.endsWith("_inheritedmethod"))
      {
        List<LSDBinding> bindings = fact.getBindings();
        LSDBinding secondBinding = (LSDBinding)bindings.get(1);
        LSDFact mtype = LSDConst.createModifiedType(secondBinding.getGroundConst());
        modifiedType.add(mtype);
        
        LSDBinding thirdBinding = (LSDBinding)bindings.get(2);
        LSDFact m2type = LSDConst.createModifiedType(thirdBinding.getGroundConst());
        if (!containsTheSameFact(addedType, deletedType, m2type)) {
          modifiedType.add(m2type);
        }
      }
      else if (predName.endsWith("_inheritedfield"))
      {
        bindings = fact.getBindings();
        LSDBinding secondBinding = (LSDBinding)bindings.get(1);
        LSDFact mtype = LSDConst.createModifiedType(secondBinding.getGroundConst());
        if (!containsTheSameFact(addedType, deletedType, mtype)) {
          modifiedType.add(mtype);
        }
        LSDBinding thirdBinding = (LSDBinding)bindings.get(2);
        LSDFact m2type = LSDConst.createModifiedType(thirdBinding.getGroundConst());
        if (!containsTheSameFact(addedType, deletedType, m2type)) {
          modifiedType.add(m2type);
        }
      }
    }
    for (??? = this.methodLevel.keySet().iterator(); ???.hasNext(); bindings.hasNext())
    {
      String kind = (String)???.next();
      bindings = ((TreeSet)this.methodLevel.get(kind)).iterator(); continue;LSDFact fact = (LSDFact)bindings.next();
      
      List<LSDBinding> bindings = fact.getBindings();
      LSDBinding thirdBinding = (LSDBinding)bindings.get(2);
      LSDFact mtype = LSDConst.createModifiedType(thirdBinding.getGroundConst());
      if (!containsTheSameFact(addedType, deletedType, mtype)) {
        modifiedType.add(mtype);
      }
    }
    for (??? = this.fieldLevel.keySet().iterator(); ???.hasNext(); bindings.hasNext())
    {
      String kind = (String)???.next();
      bindings = ((TreeSet)this.fieldLevel.get(kind)).iterator(); continue;LSDFact fact = (LSDFact)bindings.next();
      
      List<LSDBinding> bindings = fact.getBindings();
      LSDBinding thirdBinding = (LSDBinding)bindings.get(2);
      LSDFact mtype = LSDConst.createModifiedType(thirdBinding
        .getGroundConst());
      if (!containsTheSameFact(addedType, deletedType, mtype)) {
        modifiedType.add(mtype);
      }
    }
    this.typeLevel.put(this.ADDED, addedType);
    this.typeLevel.put(this.DELETED, deletedType);
    this.typeLevel.put(this.MODIFIED, modifiedType);
  }
  
  private void constructPackageLevel()
  {
    TreeSet<LSDFact> addedPackage = new TreeSet();
    TreeSet<LSDFact> deletedPackage = new TreeSet();
    TreeSet<LSDFact> modifiedPackage = new TreeSet();
    for (LSDFact fact : this.originalDeltaKB)
    {
      String predName = fact.getPredicate().getName();
      if (predName.equals("added_package")) {
        addedPackage.add(fact);
      } else if (predName.equals("deleted_package")) {
        deletedPackage.add(fact);
      }
    }
    Iterator localIterator2;
    for (??? = this.typeLevel.keySet().iterator(); ???.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)???.next();
      localIterator2 = ((TreeSet)this.typeLevel.get(kind)).iterator(); continue;LSDFact fact = (LSDFact)localIterator2.next();
      
      List<LSDBinding> bindings = fact.getBindings();
      LSDBinding thirdBinding = (LSDBinding)bindings.get(2);
      LSDFact mpackage = LSDConst.createModifiedPackage(thirdBinding
        .getGroundConst());
      if (!containsTheSameFact(addedPackage, deletedPackage, mpackage)) {
        modifiedPackage.add(mpackage);
      }
    }
    this.packageLevel.put(this.ADDED, addedPackage);
    this.packageLevel.put(this.DELETED, deletedPackage);
    this.packageLevel.put(this.MODIFIED, modifiedPackage);
  }
  
  private boolean containsTheSameFact(TreeSet<LSDFact> addSet, TreeSet<LSDFact> deletedSet, LSDFact mf)
  {
    LSDFact add = LSDConst.convertModifiedToAdded(mf);
    LSDFact del = LSDConst.convertModifiedToDeleted(mf);
    return (addSet.contains(add)) || (deletedSet.contains(del));
  }
  
  private void filterFacts2(PrintStream p, TreeSet<LSDFact> output, LSdiffFilter filter)
  {
    if (filter == null) {
      return;
    }
    this.filter = filter;
    topDownTraversal(p, output);
  }
  
  private void filterPerType(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact typeF)
  {
    printSubtypes(p, ontheflyDeltaKB, typeF);
    
    printInnerTypes(p, ontheflyDeltaKB, typeF);
    
    printInheritedMethods(p, ontheflyDeltaKB, typeF);
    
    printInheritedFields(p, ontheflyDeltaKB, typeF);
    printMethodsInType(p, ontheflyDeltaKB, typeF);
    printFieldsInType(p, ontheflyDeltaKB, typeF);
  }
  
  private void filterPerMethod(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact methodF)
  {
    printCallsInMethod(p, ontheflyDeltaKB, methodF);
    printAccessesInMethod(p, ontheflyDeltaKB, methodF);
    printReturnInMethod(p, ontheflyDeltaKB, methodF);
  }
  
  private void filterPerField(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact fieldF)
  {
    printFieldOfType(p, ontheflyDeltaKB, fieldF);
  }
  
  private void topDownTraversal(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB)
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.packageLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.packageLevel.get(kind)).iterator(); continue;LSDFact packageF = (LSDFact)localIterator2.next();
      if ((this.filter.packageLevel) && (p != null)) {
        p.println(packageF);
      }
      if (this.filter.packageLevel) {
        ontheflyDeltaKB.add(packageF);
      }
      filterPerPackage(p, ontheflyDeltaKB, packageF);
    }
  }
  
  public void filterPerPackage(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact packageF)
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.typeLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.typeLevel.get(kind)).iterator(); continue;LSDFact typeF = (LSDFact)localIterator2.next();
      if (((LSDBinding)typeF.getBindings().get(2)).getGroundConst().equals(
        ((LSDBinding)packageF.getBindings().get(0)).getGroundConst()))
      {
        if ((this.filter.typeLevel) && (p != null)) {
          p.println("\t" + typeF);
        }
        if (this.filter.typeLevel) {
          ontheflyDeltaKB.add(typeF);
        }
        filterPerType(p, ontheflyDeltaKB, typeF);
      }
    }
  }
  
  private void printMethodsInType(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact typeF)
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.methodLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.methodLevel.get(kind)).iterator(); continue;LSDFact methodF = (LSDFact)localIterator2.next();
      if (((LSDBinding)methodF.getBindings().get(2)).getGroundConst().equals(((LSDBinding)typeF.getBindings().get(0)).getGroundConst()))
      {
        if ((this.filter.methodLevel) && (p != null)) {
          p.println("\t\t" + methodF);
        }
        if (this.filter.methodLevel) {
          ontheflyDeltaKB.add(methodF);
        }
        filterPerMethod(p, ontheflyDeltaKB, methodF);
      }
    }
  }
  
  private void printFieldsInType(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact typeF)
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.fieldLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.fieldLevel.get(kind)).iterator(); continue;LSDFact fieldF = (LSDFact)localIterator2.next();
      if (((LSDBinding)fieldF.getBindings().get(2)).getGroundConst().equals(((LSDBinding)typeF.getBindings().get(0)).getGroundConst()))
      {
        if ((this.filter.fieldLevel) && (p != null)) {
          p.println("\t\t" + fieldF);
        }
        if (this.filter.fieldLevel) {
          ontheflyDeltaKB.add(fieldF);
        }
        filterPerField(p, ontheflyDeltaKB, fieldF);
      }
    }
  }
  
  private void printInnerTypes(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact typeF)
  {
    LSDFact fact;
    label133:
    for (Iterator localIterator = this.originalDeltaKB.iterator(); localIterator.hasNext(); p.println("\t\t\t" + fact))
    {
      fact = (LSDFact)localIterator.next();
      if ((fact.getPredicate().getName().indexOf("_typeintype") <= 0) || (!((LSDBinding)fact.getBindings().get(1)).getGroundConst().equals(((LSDBinding)typeF.getBindings().get(0)).getGroundConst()))) {
        break label133;
      }
      if (this.filter.typeLevel) {
        ontheflyDeltaKB.add(fact);
      }
      if ((!this.filter.typeLevel) || (p == null)) {
        break label133;
      }
    }
  }
  
  private void printSubtypes(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact typeF)
  {
    for (LSDFact fact : this.originalDeltaKB) {
      if ((fact.getPredicate().getName().indexOf("_extends") > 0) && (((LSDBinding)fact.getBindings().get(1)).getGroundConst().equals(((LSDBinding)typeF.getBindings().get(0)).getGroundConst())))
      {
        if (this.filter.typeLevel) {
          ontheflyDeltaKB.add(fact);
        }
        if ((this.filter.typeLevel) && (p != null)) {
          p.println("\t\t\t" + fact);
        }
      }
      else if ((fact.getPredicate().getName().indexOf("_implements") > 0) && (((LSDBinding)fact.getBindings().get(1)).getGroundConst().equals(((LSDBinding)typeF.getBindings().get(0)).getGroundConst())))
      {
        if (this.filter.typeLevel) {
          ontheflyDeltaKB.add(fact);
        }
        if ((this.filter.typeLevel) && (p != null)) {
          p.println("\t\t\t" + fact);
        }
      }
    }
  }
  
  private void printCallsInMethod(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact methodF)
  {
    LSDFact fact;
    label133:
    for (Iterator localIterator = this.originalDeltaKB.iterator(); localIterator.hasNext(); p.println("\t\t\t" + fact))
    {
      fact = (LSDFact)localIterator.next();
      if ((fact.getPredicate().getName().indexOf("_calls") <= 0) || (!((LSDBinding)fact.getBindings().get(0)).getGroundConst().equals(((LSDBinding)methodF.getBindings().get(0)).getGroundConst()))) {
        break label133;
      }
      if (this.filter.bodyLevel) {
        ontheflyDeltaKB.add(fact);
      }
      if ((!this.filter.bodyLevel) || (p == null)) {
        break label133;
      }
    }
  }
  
  private void printAccessesInMethod(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact methodF)
  {
    LSDFact fact;
    label133:
    for (Iterator localIterator = this.originalDeltaKB.iterator(); localIterator.hasNext(); p.println("\t\t\t" + fact))
    {
      fact = (LSDFact)localIterator.next();
      if ((fact.getPredicate().getName().indexOf("_accesses") <= 0) || (!((LSDBinding)fact.getBindings().get(1)).getGroundConst().equals(((LSDBinding)methodF.getBindings().get(0)).getGroundConst()))) {
        break label133;
      }
      if (this.filter.bodyLevel) {
        ontheflyDeltaKB.add(fact);
      }
      if ((!this.filter.bodyLevel) || (p == null)) {
        break label133;
      }
    }
  }
  
  private void printReturnInMethod(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact methodF)
  {
    LSDFact fact;
    label133:
    for (Iterator localIterator = this.originalDeltaKB.iterator(); localIterator.hasNext(); p.println("\t\t\t" + fact))
    {
      fact = (LSDFact)localIterator.next();
      if ((fact.getPredicate().getName().indexOf("_return") <= 0) || (!((LSDBinding)fact.getBindings().get(0)).getGroundConst().equals(((LSDBinding)methodF.getBindings().get(0)).getGroundConst()))) {
        break label133;
      }
      if (this.filter.methodLevel) {
        ontheflyDeltaKB.add(fact);
      }
      if ((!this.filter.methodLevel) || (p == null)) {
        break label133;
      }
    }
  }
  
  private void printInheritedFields(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact typeF)
  {
    LSDFact fact;
    label133:
    for (Iterator localIterator = this.originalDeltaKB.iterator(); localIterator.hasNext(); p.println("\t\t\t" + fact))
    {
      fact = (LSDFact)localIterator.next();
      if ((fact.getPredicate().getName().indexOf("_inheritedfield") <= 0) || (!((LSDBinding)fact.getBindings().get(2)).getGroundConst().equals(((LSDBinding)typeF.getBindings().get(0)).getGroundConst()))) {
        break label133;
      }
      if (this.filter.fieldLevel) {
        ontheflyDeltaKB.add(fact);
      }
      if ((!this.filter.fieldLevel) || (p == null)) {
        break label133;
      }
    }
  }
  
  private void printInheritedMethods(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact typeF)
  {
    LSDFact fact;
    label133:
    for (Iterator localIterator = this.originalDeltaKB.iterator(); localIterator.hasNext(); p.println("\t\t\t" + fact))
    {
      fact = (LSDFact)localIterator.next();
      if ((fact.getPredicate().getName().indexOf("_inheritedmethod") <= 0) || (!((LSDBinding)fact.getBindings().get(2)).getGroundConst().equals(((LSDBinding)typeF.getBindings().get(0)).getGroundConst()))) {
        break label133;
      }
      if (this.filter.methodLevel) {
        ontheflyDeltaKB.add(fact);
      }
      if ((!this.filter.methodLevel) || (p == null)) {
        break label133;
      }
    }
  }
  
  private void printFieldOfType(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB, LSDFact fieldF)
  {
    LSDFact fact;
    label133:
    for (Iterator localIterator = this.originalDeltaKB.iterator(); localIterator.hasNext(); p.println("\t\t\t" + fact))
    {
      fact = (LSDFact)localIterator.next();
      if ((fact.getPredicate().getName().indexOf("_fieldoftype") <= 0) || (!((LSDBinding)fact.getBindings().get(0)).getGroundConst().equals(((LSDBinding)fieldF.getBindings().get(0)).getGroundConst()))) {
        break label133;
      }
      if (this.filter.fieldLevel) {
        ontheflyDeltaKB.add(fact);
      }
      if ((!this.filter.fieldLevel) || (p == null)) {
        break label133;
      }
    }
  }
  
  private void topDownTraversal2(PrintStream p, TreeSet<LSDFact> ontheflyDeltaKB)
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.packageLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.packageLevel.get(kind)).iterator(); continue;LSDFact packageF = (LSDFact)localIterator2.next();
      if ((this.filter.packageLevel) && (p != null)) {
        p.println(packageF);
      }
      if (this.filter.packageLevel) {
        ontheflyDeltaKB.add(packageF);
      }
      filterPerPackage(p, ontheflyDeltaKB, packageF);
    }
  }
}
