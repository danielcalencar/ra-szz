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
package changetypes;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

public class FactBase
  extends HashSet<Fact>
{
  private static final long serialVersionUID = 1L;
  
  public FactBase() {}
  
  public FactBase(FactBase f)
  {
    super(f);
  }
  
  public void print(PrintStream out)
  {
    if (size() > 0)
    {
      out.println("~~~Facts~~~");
      for (Fact f : this) {
        out.println(f.toString());
      }
    }
    else
    {
      out.println("No facts");
    }
  }
  
  private static String getParentFromFullName(String name)
  {
    int lastdot = name.lastIndexOf('.');
    if (lastdot == -1) {
      return "";
    }
    return name.substring(0, lastdot);
  }
  
  private static String getChildFromFullName(String name)
  {
    int lastdot = name.lastIndexOf('.');
    return name.substring(lastdot + 1);
  }
  
  private void makeChangeFromFact(ChangeSet res, Fact f, char typ)
  {
    switch (f.type)
    {
    case ACCESSES: 
      res.add(AtomicChange.makePackageChange(typ, (String)f.params.get(0)));
      res.changecount[AtomicChange.ChangeTypes.ADD_PACKAGE.ordinal()] += 1;
      break;
    case CALLS: 
      res.add(AtomicChange.makeTypeChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makePackageChange('M', (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.MOD_PACKAGE.ordinal()] += 1;
      break;
    case CAST: 
      res.add(AtomicChange.makeMethodChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.ADD_METHOD.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(2), 
        getChildFromFullName((String)f.params.get(2)), 
        getParentFromFullName((String)f.params.get(2))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case CONDITIONAL: 
      res.add(AtomicChange.makeFieldChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.ADD_FIELD.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(2), 
        getChildFromFullName((String)f.params.get(2)), 
        getParentFromFullName((String)f.params.get(2))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case EXTENDS: 
      res.add(AtomicChange.makeReturnsChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_RETURN.ordinal()] += 1;
      
      res.add(AtomicChange.makeMethodChange('M', (String)f.params.get(0), 
        getChildFromFullName((String)f.params.get(0)), 
        getParentFromFullName((String)f.params.get(0))));
      res.changecount[AtomicChange.ChangeTypes.ADD_METHOD.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange(
        'M', 
        getParentFromFullName((String)f.params.get(0)), 
        getChildFromFullName(getParentFromFullName((String)f.params.get(0))), 
        getParentFromFullName(getParentFromFullName((String)f.params.get(0)))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case FIELD: 
      res.add(AtomicChange.makeFieldTypeChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_FIELDOFTYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makeFieldChange('M', (String)f.params.get(0), 
        getChildFromFullName((String)f.params.get(0)), 
        getParentFromFullName((String)f.params.get(0))));
      res.changecount[AtomicChange.ChangeTypes.ADD_FIELD.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange(
        'M', 
        getParentFromFullName((String)f.params.get(0)), 
        getChildFromFullName(getParentFromFullName((String)f.params.get(0))), 
        getParentFromFullName(getParentFromFullName((String)f.params.get(0)))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case METHOD: 
      res.add(AtomicChange.makeTypeInTypeChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPEINTYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(0), 
        getChildFromFullName((String)f.params.get(0)), 
        getParentFromFullName((String)f.params.get(0))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(1), 
        getChildFromFullName((String)f.params.get(1)), 
        getParentFromFullName((String)f.params.get(1))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case GETTER: 
      res.add(AtomicChange.makeSubtypeChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_SUBTYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(0), 
        getChildFromFullName((String)f.params.get(0)), 
        getParentFromFullName((String)f.params.get(0))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(1), 
        getChildFromFullName((String)f.params.get(1)), 
        getParentFromFullName((String)f.params.get(1))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case INHERITEDFIELD: 
      res.add(AtomicChange.makeImplementsChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_IMPLEMENTS.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(0), 
        getChildFromFullName((String)f.params.get(0)), 
        getParentFromFullName((String)f.params.get(0))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(1), 
        getChildFromFullName((String)f.params.get(1)), 
        getParentFromFullName((String)f.params.get(1))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case IMPLEMENTS: 
      res.add(AtomicChange.makeExtendsChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_EXTENDS.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(0), 
        getChildFromFullName((String)f.params.get(0)), 
        getParentFromFullName((String)f.params.get(0))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(1), 
        getChildFromFullName((String)f.params.get(1)), 
        getParentFromFullName((String)f.params.get(1))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case INHERITEDMETHOD: 
      res.add(AtomicChange.makeInheritedFieldChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.ADD_INHERITEDFIELD
        .ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(2), 
        getChildFromFullName((String)f.params.get(2)), 
        getParentFromFullName((String)f.params.get(2))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case LOCALVAR: 
      res.add(AtomicChange.makeInheritedMethodChange(typ, 
        (String)f.params.get(0), (String)f.params.get(1), (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.ADD_INHERITEDMETHOD
        .ordinal()] += 1;
      
      res.add(AtomicChange.makeTypeChange('M', (String)f.params.get(2), 
        getChildFromFullName((String)f.params.get(2)), 
        getParentFromFullName((String)f.params.get(2))));
      res.changecount[AtomicChange.ChangeTypes.ADD_TYPE.ordinal()] += 1;
      break;
    case FIELDMODIFIER: 
      res.add(AtomicChange.makeAccessesChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_ACCESSES.ordinal()] += 1;
      break;
    case FIELDOFTYPE: 
      res.add(AtomicChange.makeCallsChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_CALLS.ordinal()] += 1;
      break;
    case METHODBODY: 
      res.add(AtomicChange.makeMethodBodyChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_METHODBODY.ordinal()] += 1;
      
      break;
    case METHODMODIFIER: 
      res.add(AtomicChange.makeMethodArgsChange((String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.CHANGE_METHODSIGNATURE
        .ordinal()] += 1;
      
      break;
    case METHODSIGNATURE: 
      res.add(AtomicChange.makeConditionalChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2), (String)f.params.get(3)));
      res.changecount[AtomicChange.ChangeTypes.ADD_CONDITIONAL.ordinal()] += 1;
      break;
    case PACKAGE: 
      res.add(AtomicChange.makeParameterChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.ADD_PARAMETER.ordinal()] += 1;
      break;
    case THROWN: 
      res.add(AtomicChange.makeThrownExceptionChange(typ, 
        (String)f.params.get(0), (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_THROWN.ordinal()] += 1;
      break;
    case TRYCATCH: 
      res.add(AtomicChange.makeGetterChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_GETTER.ordinal()] += 1;
      break;
    case TYPE: 
      res.add(AtomicChange.makeSetterChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_SETTER.ordinal()] += 1;
      break;
    case PARAMETER: 
      res.add(AtomicChange.makeMethodModifierChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_METHODMODIFIER
        .ordinal()] += 1;
      break;
    case RETURN: 
      res.add(AtomicChange.makeFieldModifierChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1)));
      res.changecount[AtomicChange.ChangeTypes.ADD_FIELDMODIFIER
        .ordinal()] += 1;
      break;
    case SETTER: 
      res.add(AtomicChange.makeCastChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2)));
      res.changecount[AtomicChange.ChangeTypes.ADD_CAST.ordinal()] += 1;
      break;
    case SUBTYPE: 
      res.add(AtomicChange.makeTryCatchChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2), (String)f.params.get(3)));
      res.changecount[AtomicChange.ChangeTypes.ADD_TRYCATCH.ordinal()] += 1;
      break;
    case TYPEINTYPE: 
      res.add(AtomicChange.makeLocalVarChange(typ, (String)f.params.get(0), 
        (String)f.params.get(1), (String)f.params.get(2), (String)f.params.get(3)));
      res.changecount[AtomicChange.ChangeTypes.ADD_LOCALVAR.ordinal()] += 1;
      break;
    }
  }
  
  public ChangeSet diff(FactBase oldfacts)
  {
    ChangeSet res = new ChangeSet();
    FactBase added = new FactBase(this);
    added.removeAll(oldfacts);
    FactBase deleted = new FactBase(oldfacts);
    deleted.removeAll(this);
    
    Set<Fact> before_parameters = new HashSet();
    Set<Fact> after_parameters = new HashSet();
    for (Fact f : added) {
      if (f.type == Fact.FactTypes.PARAMETER) {
        before_parameters.add(f);
      } else {
        makeChangeFromFact(res, f, 'A');
      }
    }
    for (Fact f : deleted) {
      if (f.type == Fact.FactTypes.PARAMETER) {
        after_parameters.add(f);
      } else {
        makeChangeFromFact(res, f, 'D');
      }
    }
    for (Fact f : before_parameters) {
      for (Fact fd : after_parameters) {
        if (((String)fd.params.get(0)).equals(f.params.get(0)))
        {
          String[] new_params = ((String)f.params.get(1)).split(",");
          String[] old_params = ((String)fd.params.get(1)).split(",");
          
          Set<String> tempNew = new HashSet();
          Set<String> tempOld = new HashSet();
          
          int j = (localObject1 = new_params).length;
          for (int i = 0; i < j; i++)
          {
            String s = localObject1[i];
            if (!s.equals("")) {
              tempNew.add(s);
            }
          }
          j = (localObject1 = old_params).length;
          for (i = 0; i < j; i++)
          {
            String s = localObject1[i];
            if (!s.equals("")) {
              tempOld.add(s);
            }
          }
          if (tempNew.equals(tempOld)) {
            break;
          }
          Set<String> addedParams = new HashSet(tempNew);
          Object deletedParams = new HashSet(tempOld);
          
          addedParams.removeAll(tempOld);
          ((Set)deletedParams).removeAll(tempNew);
          for (Object localObject1 = addedParams.iterator(); ((Iterator)localObject1).hasNext();)
          {
            String add = (String)((Iterator)localObject1).next();
            Fact fNew = Fact.makeParameterFact((String)f.params.get(0), 
              (String)f.params.get(1), add);
            makeChangeFromFact(res, fNew, 'A');
          }
          for (localObject1 = ((Set)deletedParams).iterator(); ((Iterator)localObject1).hasNext();)
          {
            String rem = (String)((Iterator)localObject1).next();
            Fact fNew = Fact.makeParameterFact((String)f.params.get(0), 
              (String)f.params.get(1), rem);
            makeChangeFromFact(res, fNew, 'D');
          }
        }
      }
    }
    res.normalize();
    return res;
  }
  
  public void deriveFacts()
  {
    deriveInheritedMembers();
    deriveDefaultConstructors();
  }
  
  public void deriveRemoveExternalMethodsCalls()
  {
    System.out.print("Deriving remove external methods... ");
    
    FactBase badMethodCalls = new FactBase();
    for (Fact f : this) {
      if (f.type == Fact.FactTypes.CALLS) {
        if (((String)f.params.get(1)).startsWith("junit.framework")) {
          badMethodCalls.add(f);
        }
      }
    }
    removeAll(badMethodCalls);
    
    System.out.println("OK");
  }
  
  public void deriveDefaultConstructors()
  {
    System.out.print("Deriving default constructors... ");
    
    Set<Fact> typefacts = new HashSet();
    Set<Fact> methodfacts = new HashSet();
    for (Fact f : this) {
      if (f.type == Fact.FactTypes.TYPE) {
        typefacts.add(f);
      } else if (f.type == Fact.FactTypes.METHOD) {
        methodfacts.add(f);
      }
    }
    for (Fact f : typefacts)
    {
      boolean found = false;
      if (!((String)f.params.get(3)).equals("interface"))
      {
        for (Fact f2 : methodfacts) {
          if ((((String)f2.params.get(1)).startsWith("<init>(")) && 
            (((String)f2.params.get(2)).equals(f.params.get(0))))
          {
            found = true;
            break;
          }
        }
        if (!found)
        {
          Fact constfact = 
            Fact.makeMethodFact((String)f.params.get(0) + "#<init>()", 
            "<init>()", (String)f.params.get(0), "public");
          Fact returnfact = Fact.makeReturnsFact((String)f.params.get(0) + 
            "#<init>()", "void");
          add(constfact);
          add(returnfact);
        }
      }
    }
    System.out.println("OK");
  }
  
  public void deriveInheritedMembers()
  {
    System.out.println("Deriving inheritance members... ");
    
    Set<Fact> subtypefacts = new HashSet();
    Set<Fact> methodfacts = new HashSet();
    Set<Fact> fieldfacts = new HashSet();
    Set<Fact> inheritedmethodfacts = new HashSet();
    Set<Fact> inheritedfieldfacts = new HashSet();
    for (Fact f : this) {
      if (f.type == Fact.FactTypes.SUBTYPE) {
        subtypefacts.add(f);
      } else if (f.type == Fact.FactTypes.METHOD) {
        methodfacts.add(f);
      } else if (f.type == Fact.FactTypes.FIELD) {
        fieldfacts.add(f);
      }
    }
    Queue<Fact> worklist = new LinkedList();
    
    System.out.print("  Checking for directly inherited methods... ");
    for (Fact a1 : methodfacts) {
      if (a1.params.get(3) != "private") {
        if (!((String)a1.params.get(1)).startsWith("<init>(")) {
          for (Fact a2 : subtypefacts) {
            if (((String)a2.params.get(0)).equals(a1.params.get(2)))
            {
              Fact b1 = Fact.makeMethodFact("*", (String)a1.params.get(1), 
                (String)a2.params.get(1), "*");
              if (!methodfacts.contains(b1))
              {
                Fact newfact = Fact.makeInheritedMethodFact((String)a1.params.get(1), 
                  (String)a2.params.get(0), (String)a2.params.get(1));
                inheritedmethodfacts.add(newfact);
              }
            }
          }
        }
      }
    }
    System.out.println("OK");
    
    System.out.print("  Checking for directly inherited fields... ");
    Fact a2;
    for (Fact a1 : fieldfacts) {
      if (a1.params.get(3) != "private") {
        for (??? = subtypefacts.iterator(); ???.hasNext();)
        {
          a2 = (Fact)???.next();
          if (((String)a2.params.get(0)).equals(a1.params.get(2)))
          {
            Fact newfact = Fact.makeInheritedFieldFact((String)a1.params.get(1), 
              (String)a2.params.get(0), (String)a2.params.get(1));
            inheritedfieldfacts.add(newfact);
          }
        }
      }
    }
    System.out.println("OK");
    
    System.out.print("  Checking for indirectly inherited methods... ");
    worklist.clear();
    for (Fact f : inheritedmethodfacts) {
      worklist.add(f);
    }
    for (; worklist.size() > 0; a2.hasNext())
    {
      Fact a1 = (Fact)worklist.poll();
      a2 = subtypefacts.iterator(); continue;a2 = (Fact)a2.next();
      if (((String)((Fact)a2).params.get(0)).equals(a1.params.get(2)))
      {
        Fact b1 = Fact.makeMethodFact("*", (String)a1.params.get(0), 
          (String)((Fact)a2).params.get(1), "*");
        if (!methodfacts.contains(b1))
        {
          Fact b2 = Fact.makeInheritedMethodFact((String)a1.params.get(0), 
            (String)a1.params.get(1), (String)((Fact)a2).params.get(1));
          if (!inheritedmethodfacts.contains(b2))
          {
            Fact newfact = Fact.makeInheritedMethodFact((String)a1.params.get(0), 
              (String)a1.params.get(1), (String)((Fact)a2).params.get(1));
            worklist.add(newfact);
            inheritedmethodfacts.add(newfact);
          }
        }
      }
    }
    addAll(inheritedmethodfacts);
    System.out.println("OK");
    
    System.out.print("  Checking for indirectly inherited fields... ");
    worklist.clear();
    for (Object a2 = inheritedfieldfacts.iterator(); ((Iterator)a2).hasNext();)
    {
      Fact f = (Fact)((Iterator)a2).next();
      worklist.add(f);
    }
    for (; worklist.size() > 0; a2.hasNext())
    {
      Fact a1 = (Fact)worklist.poll();
      a2 = subtypefacts.iterator(); continue;Fact a2 = (Fact)a2.next();
      if (((String)a2.params.get(0)).equals(a1.params.get(2)))
      {
        Fact b1 = Fact.makeMethodFact("*", (String)a1.params.get(0), 
          (String)a2.params.get(1), "*");
        if (!methodfacts.contains(b1))
        {
          Fact newfact = Fact.makeInheritedFieldFact((String)a1.params.get(0), 
            (String)a1.params.get(1), (String)a2.params.get(1));
          worklist.add(newfact);
          inheritedfieldfacts.add(newfact);
        }
      }
    }
    addAll(inheritedfieldfacts);
    System.out.println("OK");
  }
}
