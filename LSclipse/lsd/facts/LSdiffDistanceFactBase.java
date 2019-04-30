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

public class LSdiffDistanceFactBase
{
  public static void main(String[] args)
  {
    String[] v = { "0.9.9_0.9.10", "0.9.10_0.9.11" };
    String[] arrayOfString1;
    int j = (arrayOfString1 = v).length;
    for (int i = 0; i < j; i++)
    {
      String v_i = arrayOfString1[i];
      
      File twoKBFile = new File(
        "input/jfreechart/" + v_i + "2KB.rub");
      ArrayList<LSDFact> twoKB = new LSDTyrubaFactReader(twoKBFile)
        .getFacts();
      File deltaKBFile = new File(
        "input/jfreechart/" + v_i + "delta.rub");
      ArrayList<LSDFact> deltaKB = new LSDTyrubaFactReader(deltaKBFile)
        .getFacts();
      
      System.out.println("Original 2KB Size:\t" + twoKB.size());
      System.out.println("Original Delta KB Size:\t" + deltaKB.size());
      
      LSdiffDistanceFactBase filter2KB = new LSdiffDistanceFactBase(twoKB, 
        deltaKB);
      filter2KB.expand(1);
      System.out.println("Working Set Binding Size:" + filter2KB.workingSetBinding.size());
      System.out.println("Working 2KB Size:" + filter2KB.working2KB.size());
      
      System.out.println("\n");
    }
  }
  
  private TreeSet<LSDFact> working2KB = new TreeSet();
  private TreeSet<LSDBinding> workingSetBinding = new TreeSet();
  private final ArrayList<LSDFact> original2KB;
  private final ArrayList<LSDFact> originalDeltaKB;
  private final LSdiffHierarchialDeltaKB hdelta;
  
  public LSdiffDistanceFactBase(ArrayList<LSDFact> twoKB, ArrayList<LSDFact> deltaKB)
  {
    this.original2KB = twoKB;
    this.originalDeltaKB = deltaKB;
    this.hdelta = new LSdiffHierarchialDeltaKB(deltaKB);
  }
  
  public void expand(int depth)
  {
    initializedFromDirtyCodeElements();
    for (int i = 1; i <= depth; i++)
    {
      System.out.println("Iteration " + i);
      System.out.println("Working Set Binding Size:\t" + this.workingSetBinding.size());
      
      boolean stop = expandOneHopViaDependencies();
      System.out.println("Working 2KB Size:\t" + this.working2KB.size());
      if (stop) {
        break;
      }
    }
  }
  
  public ArrayList<LSDFact> getWorking2KBFacts()
  {
    ArrayList<LSDFact> facts = new ArrayList(this.working2KB);
    return facts;
  }
  
  private void printWorkingSetBinding(PrintStream p)
  {
    for (LSDBinding b : this.workingSetBinding)
    {
      char c = b.getType();
      p.println(c + "\t:\t" + b.getGroundConst());
    }
  }
  
  private void printWorking2KBFact(PrintStream p)
  {
    for (LSDFact f : this.working2KB) {
      p.println(f);
    }
  }
  
  public void initializeFromDeltaKB()
  {
    for (LSDFact fact : this.originalDeltaKB) {
      addBindingsFromFact(this.workingSetBinding, fact);
    }
    System.out.println("Initial Working Set Binding Size:\t" + this.workingSetBinding.size());
  }
  
  private void initializedFromDirtyCodeElements()
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = this.hdelta.packageLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.hdelta.packageLevel.get(kind)).iterator(); continue;LSDFact fact = (LSDFact)localIterator2.next();
      addBindingsFromFact(this.workingSetBinding, fact);
    }
    System.out.println("Initial Working Set Binding Size:\t" + 
      this.workingSetBinding.size());
    for (localIterator1 = this.hdelta.typeLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.hdelta.typeLevel.get(kind)).iterator(); continue;LSDFact fact = (LSDFact)localIterator2.next();
      addBindingsFromFact(this.workingSetBinding, fact);
    }
    System.out.println("After Type Level: Working Set Binding Size:\t" + 
      this.workingSetBinding.size());
    for (localIterator1 = this.hdelta.methodLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.hdelta.methodLevel.get(kind)).iterator(); continue;LSDFact fact = (LSDFact)localIterator2.next();
      addBindingsFromFact(this.workingSetBinding, fact);
    }
    System.out.println("After Method Level: Working Set Binding Size:\t" + 
      this.workingSetBinding.size());
    for (localIterator1 = this.hdelta.fieldLevel.keySet().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      String kind = (String)localIterator1.next();
      localIterator2 = ((TreeSet)this.hdelta.fieldLevel.get(kind)).iterator(); continue;LSDFact fact = (LSDFact)localIterator2.next();
      addBindingsFromFact(this.workingSetBinding, fact);
    }
    System.out.println("After Field Level: Working Set Binding Size:\t" + 
      this.workingSetBinding.size());
  }
  
  private void addBindingsFromFact(TreeSet<LSDBinding> storage, LSDFact fact)
  {
    List<LSDBinding> bindings = fact.getBindings();
    char[] types = fact.getPredicate().getTypes();
    for (int i = 0; i < types.length; i++)
    {
      LSDBinding b = (LSDBinding)bindings.get(i);
      b.setType(types[i]);
      storage.add(b);
    }
  }
  
  private boolean expandOneHopViaDependencies()
  {
    TreeSet<LSDBinding> temp = new TreeSet();
    for (LSDFact twoKBfact : this.original2KB)
    {
      LSDPredicate tk_pred = twoKBfact.getPredicate();
      String tk_predName = tk_pred.getName();
      if ((tk_predName.endsWith("_accesses")) || 
        (tk_predName.endsWith("_calls")) || 
        (tk_predName.endsWith("_implements")) || 
        (tk_predName.endsWith("_extends")))
      {
        char[] types = tk_pred.getTypes();
        List<LSDBinding> bindings = twoKBfact.getBindings();
        for (int i = 0; i < types.length; i++)
        {
          LSDBinding tk_binding = (LSDBinding)bindings.get(i);
          tk_binding.setType(types[i]);
          if (this.workingSetBinding.contains(tk_binding))
          {
            this.working2KB.add(twoKBfact);
            addBindingsFromFact(temp, twoKBfact);
          }
        }
      }
      else if (tk_predName.endsWith("_inheritedfield"))
      {
        List<LSDBinding> bindings = twoKBfact.getBindings();
        
        LSDBinding tk_binding_A = (LSDBinding)bindings.get(0);
        LSDBinding tk_binding_B = (LSDBinding)bindings.get(2);
        String fullName = LSDConst.createFullMethodOrFieldName(
          tk_binding_A.getGroundConst(), tk_binding_B
          .getGroundConst());
        LSDBinding tk_binding = 
          (LSDBinding)LSDConst.createModifiedField(fullName).getBindings().get(0);
        char[] types = tk_pred.getTypes();
        tk_binding.setType(types[0]);
        if (this.workingSetBinding.contains(tk_binding))
        {
          this.working2KB.add(twoKBfact);
          addBindingsFromFact(temp, twoKBfact);
        }
      }
      else if (tk_predName.endsWith("_inheritedmethod"))
      {
        List<LSDBinding> bindings = twoKBfact.getBindings();
        LSDBinding tk_binding_A = (LSDBinding)bindings.get(0);
        LSDBinding tk_binding_B = (LSDBinding)bindings.get(2);
        String fullName = LSDConst.createFullMethodOrFieldName(
          tk_binding_A.getGroundConst(), tk_binding_B
          .getGroundConst());
        LSDBinding tk_binding = 
          (LSDBinding)LSDConst.createModifiedMethod(fullName).getBindings().get(0);
        char[] types = tk_pred.getTypes();
        tk_binding.setType(types[0]);
        if (this.workingSetBinding.contains(tk_binding))
        {
          this.working2KB.add(twoKBfact);
          addBindingsFromFact(temp, twoKBfact);
        }
      }
      else if ((tk_predName.endsWith("_typeintype")) || 
        (tk_predName.endsWith("_fieldoftype")) || 
        (tk_predName.endsWith("_return")))
      {
        List<LSDBinding> bindings = twoKBfact.getBindings();
        LSDBinding tk_binding_A = (LSDBinding)bindings.get(0);
        char[] types = tk_pred.getTypes();
        tk_binding_A.setType(types[0]);
        if (this.workingSetBinding.contains(tk_binding_A))
        {
          this.working2KB.add(twoKBfact);
          addBindingsFromFact(temp, twoKBfact);
        }
      }
      else if ((!tk_predName.endsWith("_package")) && 
        (!tk_predName.endsWith("_type")) && 
        (!tk_predName.endsWith("_method")))
      {
        tk_predName.endsWith("_field");
      }
    }
    System.err.println("temp Size: " + temp.size());
    this.workingSetBinding.addAll(temp);
    System.err.println("workingSetBinding: " + this.workingSetBinding.size());
    if (temp.size() == 0) {
      return true;
    }
    return false;
  }
}
