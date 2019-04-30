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

import java.util.Vector;

public class AtomicChange
{
  public static enum ChangeTypes
  {
    ADD_PACKAGE,  ADD_TYPE,  ADD_METHOD,  ADD_FIELD,  ADD_RETURN,  ADD_FIELDOFTYPE,  ADD_ACCESSES,  ADD_CALLS,  ADD_SUBTYPE,  ADD_EXTENDS,  ADD_IMPLEMENTS,  ADD_INHERITEDFIELD,  ADD_INHERITEDMETHOD,  ADD_TYPEINTYPE,  ADD_CAST,  ADD_TRYCATCH,  ADD_THROWN,  ADD_GETTER,  ADD_SETTER,  ADD_LOCALVAR,  ADD_METHODBODY,  ADD_CONDITIONAL,  ADD_PARAMETER,  ADD_METHODMODIFIER,  ADD_FIELDMODIFIER,  DEL_PACKAGE,  DEL_TYPE,  DEL_METHOD,  DEL_FIELD,  DEL_RETURN,  DEL_FIELDOFTYPE,  DEL_ACCESSES,  DEL_CALLS,  DEL_SUBTYPE,  DEL_EXTENDS,  DEL_IMPLEMENTS,  DEL_INHERITEDFIELD,  DEL_INHERITEDMETHOD,  DEL_TYPEINTYPE,  DEL_CAST,  DEL_TRYCATCH,  DEL_THROWN,  DEL_GETTER,  DEL_SETTER,  DEL_LOCALVAR,  DEL_METHODBODY,  DEL_CONDITIONAL,  DEL_PARAMETER,  DEL_METHODMODIFIER,  DEL_FIELDMODIFIER,  MOD_PACKAGE,  MOD_TYPE,  MOD_METHOD,  MOD_FIELD,  CHANGE_METHODSIGNATURE;
  }
  
  public int[] changecount = new int[ChangeTypes.values().length];
  public ChangeTypes type;
  public Vector<String> params;
  
  private AtomicChange(ChangeTypes mytype, Vector<String> myparams)
  {
    this.type = mytype;
    this.params = new Vector(myparams);
  }
  
  public AtomicChange(AtomicChange f)
  {
    this.type = f.type;
    this.params = f.params;
  }
  
  public int hashCode()
  {
    return this.type.ordinal();
  }
  
  public boolean equals(Object o)
  {
    if (o.getClass() != getClass()) {
      return false;
    }
    AtomicChange f = (AtomicChange)o;
    if (!this.type.equals(f.type)) {
      return false;
    }
    for (int i = 0; i < this.params.size(); i++) {
      if ((!((String)this.params.get(i)).equals(f.params.get(i))) && 
        (!((String)this.params.get(i)).equals("*")) && (!((String)f.params.get(i)).equals("*"))) {
        return false;
      }
    }
    return true;
  }
  
  public String toString()
  {
    StringBuilder res = new StringBuilder();
    if ((this.type.ordinal() >= ChangeTypes.ADD_PACKAGE.ordinal()) && 
      (this.type.ordinal() <= ChangeTypes.ADD_TYPEINTYPE.ordinal())) {
      res.append("+");
    } else if ((this.type.ordinal() >= ChangeTypes.DEL_PACKAGE.ordinal()) && 
      (this.type.ordinal() <= ChangeTypes.DEL_TYPEINTYPE.ordinal())) {
      res.append("-");
    } else {
      res.append("*");
    }
    res.append(this.type.toString());
    res.append("(");
    boolean first = true;
    for (String arg : this.params)
    {
      if (!first) {
        res.append(", ");
      }
      res.append(arg);
      first = false;
    }
    res.append(")");
    
    return res.toString();
  }
  
  public static AtomicChange makePackageChange(char typ, String packageFullName)
  {
    Vector<String> params = new Vector();
    params.add(packageFullName);
    return new AtomicChange(
      typ == 'D' ? ChangeTypes.DEL_PACKAGE : typ == 'A' ? ChangeTypes.ADD_PACKAGE : 
      ChangeTypes.MOD_PACKAGE, params);
  }
  
  public static AtomicChange makeConditionalChange(char typ, String condition, String ifBlockName, String elseBlockName, String typeFullName)
  {
    Vector<String> params = new Vector();
    params.add(condition);
    params.add(ifBlockName);
    params.add(elseBlockName);
    params.add(typeFullName);
    
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_CONDITIONAL : 
      ChangeTypes.DEL_CONDITIONAL, params);
  }
  
  public static AtomicChange makeParameterChange(char typ, String methodShortName, String methodFullName, String paramList)
  {
    Vector<String> params = new Vector();
    params.add(methodShortName);
    params.add(methodFullName);
    params.add(paramList);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_PARAMETER : 
      ChangeTypes.DEL_PARAMETER, params);
  }
  
  public static AtomicChange makeThrownExceptionChange(char typ, String methodQualifiedName, String exceptionQualifiedName)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(exceptionQualifiedName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_THROWN : 
      ChangeTypes.DEL_THROWN, params);
  }
  
  public static AtomicChange makeGetterChange(char typ, String methodQualifiedName, String fieldQualifiedName)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(fieldQualifiedName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_GETTER : 
      ChangeTypes.DEL_GETTER, params);
  }
  
  public static AtomicChange makeSetterChange(char typ, String methodQualifiedName, String fieldQualifiedName)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(fieldQualifiedName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_SETTER : 
      ChangeTypes.DEL_SETTER, params);
  }
  
  public static AtomicChange makeMethodModifierChange(char typ, String methodFullName, String modifier)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(modifier);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_METHODMODIFIER : 
      ChangeTypes.DEL_METHODMODIFIER, params);
  }
  
  public static AtomicChange makeFieldModifierChange(char typ, String fFullName, String modifier)
  {
    Vector<String> params = new Vector();
    params.add(fFullName);
    params.add(modifier);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_FIELDMODIFIER : 
      ChangeTypes.DEL_FIELDMODIFIER, params);
  }
  
  public static AtomicChange makeTypeChange(char typ, String typeFullName, String typeShortName, String packageFullName)
  {
    Vector<String> params = new Vector();
    params.add(typeFullName);
    params.add(typeShortName);
    params.add(packageFullName);
    return new AtomicChange(
      typ == 'D' ? ChangeTypes.DEL_TYPE : typ == 'A' ? ChangeTypes.ADD_TYPE : 
      ChangeTypes.MOD_TYPE, params);
  }
  
  public static AtomicChange makeFieldChange(char typ, String fieldFullName, String fieldShortName, String typeFullName)
  {
    Vector<String> params = new Vector();
    params.add(fieldFullName);
    params.add(fieldShortName);
    params.add(typeFullName);
    return new AtomicChange(
      typ == 'D' ? ChangeTypes.DEL_FIELD : typ == 'A' ? ChangeTypes.ADD_FIELD : 
      ChangeTypes.MOD_FIELD, params);
  }
  
  public static AtomicChange makeMethodChange(char typ, String methodFullName, String methodShortName, String typeFullName)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(methodShortName);
    params.add(typeFullName);
    return new AtomicChange(
      typ == 'D' ? ChangeTypes.DEL_METHOD : typ == 'A' ? ChangeTypes.ADD_METHOD : 
      ChangeTypes.MOD_METHOD, params);
  }
  
  public static AtomicChange makeFieldTypeChange(char typ, String fieldFullName, String declaredTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(fieldFullName);
    params.add(declaredTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_FIELDOFTYPE : 
      ChangeTypes.DEL_FIELDOFTYPE, params);
  }
  
  public static AtomicChange makeTypeInTypeChange(char typ, String innerTypeFullName, String outerTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(innerTypeFullName);
    params.add(outerTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_TYPEINTYPE : 
      ChangeTypes.DEL_TYPEINTYPE, params);
  }
  
  public static AtomicChange makeReturnsChange(char typ, String methodFullName, String returnTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(returnTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_RETURN : 
      ChangeTypes.DEL_RETURN, params);
  }
  
  public static AtomicChange makeSubtypeChange(char typ, String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_SUBTYPE : 
      ChangeTypes.DEL_SUBTYPE, params);
  }
  
  public static AtomicChange makeImplementsChange(char typ, String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_IMPLEMENTS : 
      ChangeTypes.DEL_IMPLEMENTS, params);
  }
  
  public static AtomicChange makeExtendsChange(char typ, String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_EXTENDS : 
      ChangeTypes.DEL_EXTENDS, params);
  }
  
  public static AtomicChange makeInheritedFieldChange(char typ, String fieldShortName, String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(fieldShortName);
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_INHERITEDFIELD : 
      ChangeTypes.DEL_INHERITEDFIELD, params);
  }
  
  public static AtomicChange makeInheritedMethodChange(char typ, String methodShortName, String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(methodShortName);
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_INHERITEDMETHOD : 
      ChangeTypes.DEL_INHERITEDMETHOD, params);
  }
  
  public static AtomicChange makeMethodBodyChange(char typ, String methodFullName, String methodBody)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(methodBody);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_METHODBODY : 
      ChangeTypes.DEL_METHODBODY, params);
  }
  
  public static AtomicChange makeMethodArgsChange(String methodFullName, String methodSignature)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(methodSignature);
    return new AtomicChange(ChangeTypes.CHANGE_METHODSIGNATURE, params);
  }
  
  public static AtomicChange makeCallsChange(char typ, String callerMethodFullName, String calleeMethodFullName)
  {
    Vector<String> params = new Vector();
    params.add(callerMethodFullName);
    params.add(calleeMethodFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_CALLS : 
      ChangeTypes.DEL_CALLS, params);
  }
  
  public static AtomicChange makeAccessesChange(char typ, String fieldFullName, String accessorMethodFullName)
  {
    Vector<String> params = new Vector();
    params.add(fieldFullName);
    params.add(accessorMethodFullName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_ACCESSES : 
      ChangeTypes.DEL_ACCESSES, params);
  }
  
  public static AtomicChange makeCastChange(char typ, String expression, String type, String methodName)
  {
    Vector<String> params = new Vector();
    params.add(expression);
    params.add(type);
    params.add(methodName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_CAST : 
      ChangeTypes.DEL_CAST, params);
  }
  
  public static AtomicChange makeTryCatchChange(char typ, String tryBlock, String catchClauses, String finallyBlock, String methodName)
  {
    Vector<String> params = new Vector();
    params.add(tryBlock);
    params.add(catchClauses);
    params.add(finallyBlock);
    params.add(methodName);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_TRYCATCH : 
      ChangeTypes.DEL_TRYCATCH, params);
  }
  
  public static AtomicChange makeLocalVarChange(char typ, String methodQualifiedName, String typeQualifiedName, String identifier, String expression)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(typeQualifiedName);
    params.add(identifier);
    params.add(expression);
    return new AtomicChange(typ == 'A' ? ChangeTypes.ADD_LOCALVAR : 
      ChangeTypes.DEL_LOCALVAR, params);
  }
}
