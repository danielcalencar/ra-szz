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

public class Fact
{
  public static final String PRIVATE = "private";
  public static final String PROTECTED = "protected";
  public static final String PUBLIC = "public";
  public static final String PACKAGE = "package";
  public static final String INTERFACE = "interface";
  public static final String CLASS = "class";
  public FactTypes type;
  public Vector<String> params;
  public int params_length;
  
  public static enum FactTypes
  {
    PACKAGE,  TYPE,  METHOD,  FIELD,  RETURN,  FIELDOFTYPE,  ACCESSES,  CALLS,  SUBTYPE,  EXTENDS,  IMPLEMENTS,  INHERITEDFIELD,  INHERITEDMETHOD,  TYPEINTYPE,  METHODBODY,  METHODSIGNATURE,  CONDITIONAL,  PARAMETER,  METHODMODIFIER,  FIELDMODIFIER,  CAST,  TRYCATCH,  THROWN,  GETTER,  SETTER,  LOCALVAR;
  }
  
  public String filename = "";
  public int startposition;
  public int length;
  
  private Fact(FactTypes mytype, Vector<String> myparams)
  {
    this.type = mytype;
    this.params = new Vector(myparams);
  }
  
  public Fact(Fact f)
  {
    this.type = f.type;
    this.params = f.params;
  }
  
  public int hashCode()
  {
    return this.params.hashCode() + this.type.ordinal() * 1000;
  }
  
  public boolean equals(Object o)
  {
    if (o.getClass() != getClass()) {
      return false;
    }
    Fact f = (Fact)o;
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
  
  public static Fact makeModifierMethodFact(String mFullName, String modifier)
  {
    Vector<String> params = new Vector();
    params.add(mFullName);
    params.add(modifier);
    return new Fact(FactTypes.METHODMODIFIER, params);
  }
  
  public static Fact makeFieldModifierFact(String fFullName, String modifier)
  {
    Vector<String> params = new Vector();
    params.add(fFullName);
    params.add(modifier);
    return new Fact(FactTypes.FIELDMODIFIER, params);
  }
  
  public static Fact makeConditionalFact(String condition, String ifBlockName, String elseBlockName, String typeFullName)
  {
    Vector<String> params = new Vector();
    params.add(condition);
    params.add(ifBlockName);
    params.add(elseBlockName);
    params.add(typeFullName);
    return new Fact(FactTypes.CONDITIONAL, params);
  }
  
  public static Fact makeParameterFact(String methodFullName, String paramList, String paramChange)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(paramList);
    params.add(paramChange);
    return new Fact(FactTypes.PARAMETER, params);
  }
  
  public static Fact makePackageFact(String packageFullName)
  {
    Vector<String> params = new Vector();
    params.add(packageFullName);
    return new Fact(FactTypes.PACKAGE, params);
  }
  
  public static Fact makeTypeFact(String typeFullName, String typeShortName, String packageFullName, String typeKind)
  {
    Vector<String> params = new Vector();
    params.add(typeFullName);
    params.add(typeShortName);
    params.add(packageFullName);
    params.add(typeKind);
    return new Fact(FactTypes.TYPE, params);
  }
  
  public static Fact makeFieldFact(String fieldFullName, String fieldShortName, String typeFullName, String visibility)
  {
    Vector<String> params = new Vector();
    params.add(fieldFullName);
    params.add(fieldShortName);
    params.add(typeFullName);
    params.add(visibility);
    return new Fact(FactTypes.FIELD, params);
  }
  
  public static Fact makeMethodFact(String methodFullName, String methodShortName, String typeFullName, String visibility)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(methodShortName);
    params.add(typeFullName);
    params.add(visibility);
    return new Fact(FactTypes.METHOD, params);
  }
  
  public static Fact makeFieldTypeFact(String fieldFullName, String declaredTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(fieldFullName);
    params.add(declaredTypeFullName);
    return new Fact(FactTypes.FIELDOFTYPE, params);
  }
  
  public static Fact makeTypeInTypeFact(String innerTypeFullName, String outerTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(innerTypeFullName);
    params.add(outerTypeFullName);
    return new Fact(FactTypes.TYPEINTYPE, params);
  }
  
  public static Fact makeReturnsFact(String methodFullName, String returnTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(returnTypeFullName);
    return new Fact(FactTypes.RETURN, params);
  }
  
  public static Fact makeSubtypeFact(String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new Fact(FactTypes.SUBTYPE, params);
  }
  
  public static Fact makeImplementsFact(String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new Fact(FactTypes.IMPLEMENTS, params);
  }
  
  public static Fact makeExtendsFact(String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new Fact(FactTypes.EXTENDS, params);
  }
  
  public static Fact makeInheritedFieldFact(String fieldShortName, String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(fieldShortName);
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new Fact(FactTypes.INHERITEDFIELD, params);
  }
  
  public static Fact makeInheritedMethodFact(String methodShortName, String superTypeFullName, String subTypeFullName)
  {
    Vector<String> params = new Vector();
    params.add(methodShortName);
    params.add(superTypeFullName);
    params.add(subTypeFullName);
    return new Fact(FactTypes.INHERITEDMETHOD, params);
  }
  
  public static Fact makeMethodBodyFact(String methodFullName, String methodBody)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(methodBody);
    return new Fact(FactTypes.METHODBODY, params);
  }
  
  public static Fact makeMethodArgsFact(String methodFullName, String methodSignature)
  {
    Vector<String> params = new Vector();
    params.add(methodFullName);
    params.add(methodSignature);
    return new Fact(FactTypes.METHODSIGNATURE, params);
  }
  
  public static Fact makeCallsFact(String callerMethodFullName, String calleeMethodFullName)
  {
    Vector<String> params = new Vector();
    params.add(callerMethodFullName);
    params.add(calleeMethodFullName);
    return new Fact(FactTypes.CALLS, params);
  }
  
  public static Fact makeAccessesFact(String fieldFullName, String accessorMethodFullName)
  {
    Vector<String> params = new Vector();
    params.add(fieldFullName);
    params.add(accessorMethodFullName);
    return new Fact(FactTypes.ACCESSES, params);
  }
  
  public static Fact makeCastFact(String expression, String type, String methodName)
  {
    Vector<String> params = new Vector();
    params.add(expression);
    params.add(type);
    params.add(methodName);
    return new Fact(FactTypes.CAST, params);
  }
  
  public static Fact makeTryCatchFact(String tryBlock, String catchClauses, String finallyBlock, String methodName)
  {
    Vector<String> params = new Vector();
    params.add(tryBlock);
    params.add(catchClauses);
    params.add(finallyBlock);
    params.add(methodName);
    return new Fact(FactTypes.TRYCATCH, params);
  }
  
  public static Fact makeThrownExceptionFact(String methodQualifiedName, String exceptionQualifiedName)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(exceptionQualifiedName);
    return new Fact(FactTypes.THROWN, params);
  }
  
  public static Fact makeGetterFact(String methodQualifiedName, String fieldQualifiedName)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(fieldQualifiedName);
    return new Fact(FactTypes.GETTER, params);
  }
  
  public static Fact makeSetterFact(String methodQualifiedName, String fieldQualifiedName)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(fieldQualifiedName);
    return new Fact(FactTypes.SETTER, params);
  }
  
  public static Fact makeLocalVarFact(String methodQualifiedName, String typeQualifiedName, String identifier, String expression)
  {
    Vector<String> params = new Vector();
    params.add(methodQualifiedName);
    params.add(typeQualifiedName);
    params.add(identifier);
    params.add(expression);
    return new Fact(FactTypes.LOCALVAR, params);
  }
}
