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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTVisitorAtomicChange
  extends ASTVisitor
{
  public FactBase facts;
  private Map<String, IJavaElement> typeToFileMap_ = new HashMap();
  
  public Map<String, IJavaElement> getTypeToFileMap()
  {
    return Collections.unmodifiableMap(this.typeToFileMap_);
  }
  
  private List<String> allowedFieldMods_ = Arrays.asList(new String[] {"public", "private", "protected", "static", "final" });
  private Stack<IMethodBinding> mtbStack = new Stack();
  private Stack<ITypeBinding> itbStack = new Stack();
  
  public ASTVisitorAtomicChange()
  {
    this.facts = new FactBase();
  }
  
  public void printFacts(PrintStream out)
  {
    this.facts.print(out);
  }
  
  public boolean visit(PackageDeclaration node)
  {
    try
    {
      this.facts.add(Fact.makePackageFact(node.getName().toString()));
    }
    catch (Exception localException)
    {
      System.err.println("Cannot resolve bindings for package " + 
        node.getName().toString());
    }
    return false;
  }
  
  private static String removeParameters(String name)
  {
    int index = name.indexOf('<');
    if (index <= 0) {
      return name;
    }
    return name.substring(0, index);
  }
  
  private static String getModifier(IBinding ib)
  {
    if ((ib.getModifiers() & 0x1) > 0) {
      return "public";
    }
    if ((ib.getModifiers() & 0x4) > 0) {
      return "protected";
    }
    if ((ib.getModifiers() & 0x2) > 0) {
      return "private";
    }
    return "package";
  }
  
  private String edit_str(String str)
  {
    String newmBody_str = str.replace("{", "");
    newmBody_str = newmBody_str.replace("}", "");
    newmBody_str = newmBody_str.replace(" ", "");
    newmBody_str = newmBody_str.replace(";", "");
    return newmBody_str;
  }
  
  public boolean visit(IfStatement node)
  {
    Statement thenStmt = node.getThenStatement();
    Statement elseStmt = node.getElseStatement();
    Expression condExpr = node.getExpression();
    
    String thenStr = thenStmt.toString().replace('\n', ' ');
    String elseStr = "";
    if (elseStmt != null) {
      elseStr = elseStmt.toString().replace('\n', ' ');
    }
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    IMethodBinding mtb = (IMethodBinding)this.mtbStack.peek();
    String methodStr = getQualifiedName(mtb);
    String condStr = condExpr.toString();
    
    condStr = edit_str(condStr);
    thenStr = edit_str(thenStr);
    elseStr = edit_str(elseStr);
    try
    {
      this.facts.add(Fact.makeConditionalFact(condStr, thenStr, elseStr, 
        methodStr));
    }
    catch (Exception e)
    {
      System.err.println("Cannot resolve conditional \"" + 
        condExpr.toString() + "\"");
      System.out.println("ifStmt: " + thenStr);
      System.out.println("elseStmt: " + elseStr);
      System.err.println(e.getMessage());
    }
    return true;
  }
  
  public void endVisit(IfStatement node) {}
  
  public boolean visit(CastExpression node)
  {
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    Expression expression = node.getExpression();
    ITypeBinding type = node.getType().resolveBinding();
    IMethodBinding mtb = (IMethodBinding)this.mtbStack.peek();
    String exprStr = expression.toString();
    String typeStr = getQualifiedName(type);
    String methodStr = getQualifiedName(mtb);
    
    exprStr = edit_str(exprStr);
    
    this.facts.add(Fact.makeCastFact(exprStr, typeStr, methodStr));
    
    return true;
  }
  
  public boolean visit(TryStatement node)
  {
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    String bodyStr = node.getBody() != null ? node.getBody().toString() : 
      "";
    bodyStr = edit_str(bodyStr);
    StringBuilder catchClauses = new StringBuilder();
    for (Object o : node.catchClauses())
    {
      if (catchClauses.length() > 0) {
        catchClauses.append(",");
      }
      CatchClause c = (CatchClause)o;
      catchClauses.append(getQualifiedName(c.getException().getType()
        .resolveBinding()));
      catchClauses.append(":");
      if (c.getBody() != null) {
        catchClauses.append(edit_str(c.getBody().toString()));
      }
    }
    String finallyStr = node.getFinally() != null ? node.getFinally()
      .toString() : "";
    finallyStr = edit_str(finallyStr);
    
    IMethodBinding mtb = (IMethodBinding)this.mtbStack.peek();
    String methodStr = getQualifiedName(mtb);
    
    this.facts.add(Fact.makeTryCatchFact(bodyStr, catchClauses.toString(), 
      finallyStr, methodStr));
    
    return true;
  }
  
  private static String getQualifiedName(ITypeBinding itb)
  {
    if (itb.isPrimitive()) {
      return itb.getName();
    }
    if (itb.isArray()) {
      try
      {
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < itb.getDimensions(); i++) {
          suffix.append("[]");
        }
        return 
          getQualifiedName(itb.getElementType()) + suffix.toString();
      }
      catch (NullPointerException localNullPointerException1)
      {
        return null;
      }
    }
    if (itb.isNullType()) {
      return "null";
    }
    if ((itb.isClass()) || (itb.isInterface()))
    {
      if (itb.isNested())
      {
        String name = itb.getName();
        if (itb.isAnonymous())
        {
          String binname = itb.getBinaryName();
          int index = binname.indexOf('$');
          name = binname.substring(index + 1, binname.length());
        }
        return getQualifiedName(itb.getDeclaringClass()) + "#" + name;
      }
      try
      {
        String pkg = itb.getPackage().getName();
        String name = itb.getName();
        
        name = removeParameters(itb.getName());
        return pkg + "%." + name;
      }
      catch (NullPointerException localNullPointerException2)
      {
        return null;
      }
    }
    return "java.lang%.Object";
  }
  
  private static String getQualifiedName(IVariableBinding ivb)
  {
    try
    {
      String name = ivb.getName();
      return getQualifiedName(ivb.getDeclaringClass()) + "#" + name;
    }
    catch (NullPointerException localNullPointerException) {}
    return "";
  }
  
  private static String getQualifiedName(IMethodBinding imb)
  {
    return 
      getQualifiedName(imb.getDeclaringClass()) + "#" + getSimpleName(imb);
  }
  
  private static String getSimpleName(ITypeBinding itb)
  {
    if (itb.isNested())
    {
      if (itb.isAnonymous())
      {
        String binname = itb.getBinaryName();
        int index = binname.indexOf('$');
        String name = binname.substring(index + 1, binname.length());
        return getSimpleName(itb.getDeclaringClass()) + "#" + name;
      }
      return 
        getSimpleName(itb.getDeclaringClass()) + "#" + itb.getName();
    }
    return itb.getName();
  }
  
  private static String getSimpleName(IMethodBinding imb)
  {
    try
    {
      String name = imb.getName();
      if (imb.isConstructor()) {
        name = "<init>";
      }
      String args = "";
      
      args = "(" + args + ")";
      return name + args;
    }
    catch (NullPointerException localNullPointerException) {}
    return "";
  }
  
  public boolean visit(CompilationUnit node)
  {
    IJavaElement thisFile = node.getJavaElement();
    for (Object abstractTypeDeclaration : node.types()) {
      if ((abstractTypeDeclaration instanceof TypeDeclaration))
      {
        TypeDeclaration td = (TypeDeclaration)abstractTypeDeclaration;
        this.typeToFileMap_.put(getQualifiedName(td.resolveBinding()), 
          thisFile);
      }
    }
    return true;
  }
  
  public boolean visit(TypeDeclaration node)
  {
    ITypeBinding itb = node.resolveBinding();
    this.itbStack.push(itb);
    try
    {
      this.facts.add(Fact.makeTypeFact(getQualifiedName(itb), 
        getSimpleName(itb), itb.getPackage().getName(), 
        itb.isInterface() ? "interface" : "class"));
    }
    catch (Exception localException1)
    {
      System.err.println("Cannot resolve bindings for class " + 
        node.getName().toString());
    }
    ITypeBinding localITypeBinding1;
    ITypeBinding i2;
    try
    {
      ITypeBinding itb2 = itb.getSuperclass();
      if (itb.getSuperclass() != null)
      {
        this.facts.add(Fact.makeSubtypeFact(getQualifiedName(itb2), 
          getQualifiedName(itb)));
        this.facts.add(Fact.makeExtendsFact(getQualifiedName(itb2), 
          getQualifiedName(itb)));
      }
      ITypeBinding[] arrayOfITypeBinding;
      int i;
      if (node.isInterface())
      {
        i = (arrayOfITypeBinding = itb.getInterfaces()).length;
        for (localITypeBinding1 = 0; localITypeBinding1 < i; localITypeBinding1++)
        {
          ITypeBinding i2 = arrayOfITypeBinding[localITypeBinding1];
          this.facts.add(Fact.makeSubtypeFact(getQualifiedName(i2), 
            getQualifiedName(itb)));
          this.facts.add(Fact.makeExtendsFact(getQualifiedName(i2), 
            getQualifiedName(itb)));
        }
      }
      else
      {
        i = (arrayOfITypeBinding = itb.getInterfaces()).length;
        for (localITypeBinding1 = 0; localITypeBinding1 < i; localITypeBinding1++)
        {
          i2 = arrayOfITypeBinding[localITypeBinding1];
          this.facts.add(Fact.makeSubtypeFact(getQualifiedName(i2), 
            getQualifiedName(itb)));
          this.facts.add(Fact.makeImplementsFact(getQualifiedName(i2), 
            getQualifiedName(itb)));
        }
      }
    }
    catch (Exception localException2)
    {
      System.err.println("Cannot resolve super class bindings for class " + 
        node.getName().toString());
    }
    Object localObject;
    try
    {
      localITypeBinding1 = (localObject = itb.getDeclaredFields()).length;
      for (i2 = 0; i2 < localITypeBinding1; i2++)
      {
        IVariableBinding ivb = localObject[i2];
        String visibility = getModifier(ivb);
        String fieldStr = ivb.toString();
        
        String[] tokens = fieldStr.split(" ");
        String[] arrayOfString1;
        int k = (arrayOfString1 = tokens).length;
        for (int j = 0; j < k; j++)
        {
          String token = arrayOfString1[j];
          if (this.allowedFieldMods_.contains(token)) {
            this.facts.add(Fact.makeFieldModifierFact(
              getQualifiedName(ivb), token));
          }
        }
        this.facts.add(Fact.makeFieldFact(getQualifiedName(ivb), 
          ivb.getName(), getQualifiedName(itb), visibility));
        if (!ivb.getType().isParameterizedType()) {
          this.facts.add(Fact.makeFieldTypeFact(getQualifiedName(ivb), 
            getQualifiedName(ivb.getType())));
        } else {
          this.facts.add(Fact.makeFieldTypeFact(getQualifiedName(ivb), 
            makeParameterizedName(ivb)));
        }
      }
    }
    catch (Exception localException3)
    {
      System.err.println("Cannot resolve field bindings for class " + 
        node.getName().toString());
    }
    try
    {
      ITypeBinding localITypeBinding2 = (localObject = node.getTypes()).length;
      for (i2 = 0; i2 < localITypeBinding2; i2++)
      {
        TypeDeclaration t = localObject[i2];
        ITypeBinding intb = t.resolveBinding();
        this.facts.add(Fact.makeTypeInTypeFact(getQualifiedName(intb), 
          getQualifiedName(itb)));
      }
    }
    catch (Exception localException4)
    {
      System.err.println("Cannot resolve inner type bindings for class " + 
        node.getName().toString());
    }
    return true;
  }
  
  private String makeParameterizedName(IVariableBinding ivb)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getQualifiedName(ivb.getType()) + "<");
    boolean comma = false;
    ITypeBinding[] arrayOfITypeBinding;
    int j = (arrayOfITypeBinding = ivb.getType().getTypeArguments()).length;
    for (int i = 0; i < j; i++)
    {
      ITypeBinding itb = arrayOfITypeBinding[i];
      if (comma) {
        sb.append(",");
      }
      sb.append(getQualifiedName(itb));
      comma = true;
    }
    sb.append(">");
    return sb.toString();
  }
  
  public void endVisit(TypeDeclaration node)
  {
    this.itbStack.pop();
  }
  
  public boolean visit(AnonymousClassDeclaration node)
  {
    ITypeBinding itb = node.resolveBinding();
    this.itbStack.push(itb);
    try
    {
      this.facts.add(Fact.makeTypeFact(getQualifiedName(itb), 
        getSimpleName(itb), itb.getPackage().getName(), 
        itb.isInterface() ? "interface" : "class"));
    }
    catch (Exception localException1)
    {
      System.err.println("Cannot resolve bindings for anonymous class " + 
        itb.getName());
    }
    Object localObject;
    int j;
    int i;
    try
    {
      try
      {
        this.facts.add(Fact.makeSubtypeFact(
          getQualifiedName(itb.getSuperclass()), 
          getQualifiedName(itb)));
        this.facts.add(Fact.makeExtendsFact(
          getQualifiedName(itb.getSuperclass()), 
          getQualifiedName(itb)));
      }
      catch (NullPointerException localNullPointerException1)
      {
        return false;
      }
      j = (localObject = itb.getInterfaces()).length;
      for (i = 0; i < j; i++)
      {
        ITypeBinding i2 = localObject[i];
        try
        {
          this.facts.add(Fact.makeSubtypeFact(getQualifiedName(i2), 
            getQualifiedName(itb)));
          this.facts.add(Fact.makeImplementsFact(getQualifiedName(i2), 
            getQualifiedName(itb)));
        }
        catch (NullPointerException localNullPointerException2)
        {
          return false;
        }
      }
    }
    catch (Exception localException2)
    {
      System.err.println("Cannot resolve super class bindings for anonymous class " + 
        itb.getName());
    }
    try
    {
      j = (localObject = itb.getDeclaredFields()).length;
      for (i = 0; i < j; i++)
      {
        IVariableBinding ivb = localObject[i];
        String visibility = getModifier(ivb);
        this.facts.add(Fact.makeFieldFact(getQualifiedName(ivb), 
          ivb.getName(), getQualifiedName(itb), visibility));
        this.facts.add(Fact.makeFieldTypeFact(getQualifiedName(ivb), 
          getQualifiedName(ivb.getType())));
      }
    }
    catch (Exception localException3)
    {
      System.err.println("Cannot resolve field bindings for anonymous class " + 
        itb.getName());
    }
    try
    {
      if (itb.isNested()) {
        this.facts.add(Fact.makeTypeInTypeFact(getQualifiedName(itb), 
          getQualifiedName(itb.getDeclaringClass())));
      }
    }
    catch (Exception localException4)
    {
      System.err.println("Cannot resolve inner type for anonymous class " + 
        itb.getName());
    }
    return true;
  }
  
  public void endVisit(AnonymousClassDeclaration node)
  {
    this.itbStack.pop();
  }
  
  public boolean visit(MethodDeclaration node)
  {
    IMethodBinding mtb = node.resolveBinding();
    this.mtbStack.push(mtb);
    String nodeStr = node.toString();
    
    String modifier = "protected";
    int dex = nodeStr.indexOf(' ');
    if (dex >= 0)
    {
      String temp = nodeStr.substring(0, dex);
      if (temp.equals("public")) {
        modifier = "public";
      } else if (temp.equals("private")) {
        modifier = "private";
      }
    }
    try
    {
      String visibility = getModifier(mtb);
      this.facts.add(Fact.makeMethodFact(getQualifiedName(mtb), 
        getSimpleName(mtb), 
        getQualifiedName(mtb.getDeclaringClass()), visibility));
    }
    catch (Exception localException1)
    {
      System.err.println("Cannot resolve return method bindings for method " + 
        node.getName().toString());
    }
    try
    {
      String returntype = getQualifiedName(mtb.getReturnType());
      this.facts.add(Fact.makeReturnsFact(getQualifiedName(mtb), returntype));
    }
    catch (Exception localException2)
    {
      System.err.println("Cannot resolve return type bindings for method " + 
        node.getName().toString());
    }
    try
    {
      this.facts.add(Fact.makeModifierMethodFact(getQualifiedName(mtb), 
        modifier));
    }
    catch (Exception localException3)
    {
      System.err.println("Cannot resolve return type bindings for method modifier " + 
        node.getName().toString());
    }
    try
    {
      String bodystring = node.getBody() != null ? node.getBody()
        .toString() : "";
      bodystring = bodystring.replace('\n', ' ');
      
      bodystring = bodystring.replace('"', ' ');
      bodystring = bodystring.replace('"', ' ');
      bodystring = bodystring.replace('\\', ' ');
      
      this.facts.add(
        Fact.makeMethodBodyFact(getQualifiedName(mtb), bodystring));
    }
    catch (Exception localException4)
    {
      System.err.println("Cannot resolve bindings for body");
    }
    SingleVariableDeclaration param;
    try
    {
      List<SingleVariableDeclaration> parameters = node.parameters();
      
      StringBuilder sb = new StringBuilder();
      for (Iterator localIterator = parameters.iterator(); localIterator.hasNext();)
      {
        param = (SingleVariableDeclaration)localIterator.next();
        if (sb.length() != 0) {
          sb.append(", ");
        }
        sb.append(param.getType().toString());
        sb.append(":");
        sb.append(param.getName().toString());
      }
      this.facts.add(Fact.makeParameterFact(getQualifiedName(mtb), 
        sb.toString(), ""));
    }
    catch (Exception localException5)
    {
      System.err.println("Cannot resolve bindings for parameters");
    }
    try
    {
      List<Name> thrownTypes = node.thrownExceptions();
      for (Name n : thrownTypes) {
        this.facts.add(Fact.makeThrownExceptionFact(getQualifiedName(mtb), 
          getQualifiedName(n.resolveTypeBinding())));
      }
    }
    catch (Exception localException6)
    {
      System.err.println("Cannot resolve bindings for exceptions");
    }
    return true;
  }
  
  public void endVisit(MethodDeclaration node)
  {
    this.mtbStack.pop();
  }
  
  public boolean visit(FieldAccess node)
  {
    IVariableBinding ivb = node.resolveFieldBinding();
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    IMethodBinding mtb = (IMethodBinding)this.mtbStack.peek();
    try
    {
      if ((!node.getName().toString().equals("length")) || 
        (ivb.getDeclaringClass() != null)) {
        this.facts.add(Fact.makeAccessesFact(
          getQualifiedName(node.resolveFieldBinding()), 
          getQualifiedName(mtb)));
      }
    }
    catch (Exception localException1)
    {
      System.err.println("Cannot resolve field access \"" + 
        node.getName().toString() + "\"");
    }
    try
    {
      String simpleMethodName = getSimpleName(mtb);
      if (simpleMethodName.toLowerCase().startsWith("get")) {
        this.facts.add(Fact.makeGetterFact(getQualifiedName(mtb), 
          getQualifiedName(node.resolveFieldBinding())));
      } else if (simpleMethodName.toLowerCase().startsWith("set")) {
        this.facts.add(Fact.makeSetterFact(getQualifiedName(mtb), 
          getQualifiedName(node.resolveFieldBinding())));
      }
    }
    catch (Exception localException2)
    {
      System.err.println("Cannot resolve bindings for exceptions");
    }
    return true;
  }
  
  public boolean visit(SimpleName node)
  {
    if ((this.mtbStack.isEmpty()) && (!this.itbStack.isEmpty())) {
      return false;
    }
    if (!this.mtbStack.isEmpty())
    {
      if (node.getIdentifier().equals("length")) {
        return false;
      }
      try
      {
        return visitName(node.resolveBinding(), (IMethodBinding)this.mtbStack.peek());
      }
      catch (Exception localException)
      {
        System.err.println("Cannot resolve simple name \"" + 
          node.getFullyQualifiedName().toString() + "\"");
        return false;
      }
    }
    return false;
  }
  
  public boolean visit(QualifiedName node)
  {
    if ((this.mtbStack.isEmpty()) && (!this.itbStack.isEmpty())) {
      return false;
    }
    if (!this.mtbStack.isEmpty())
    {
      if (node.getName().getIdentifier().equals("length")) {
        return true;
      }
      try
      {
        return visitName(node.resolveBinding(), (IMethodBinding)this.mtbStack.peek());
      }
      catch (Exception localException)
      {
        System.err.println("Cannot resolve qualified name \"" + 
          node.getFullyQualifiedName().toString() + "\"");
        return false;
      }
    }
    return false;
  }
  
  private boolean visitName(IBinding ib, IMethodBinding iMethodBinding)
    throws Exception
  {
    switch (ib.getKind())
    {
    case 3: 
      IVariableBinding ivb = (IVariableBinding)ib;
      if (ivb.isField())
      {
        this.facts.add(Fact.makeAccessesFact(getQualifiedName(ivb), 
          getQualifiedName(iMethodBinding)));
        try
        {
          String simpleMethodName = getSimpleName(iMethodBinding);
          if (simpleMethodName.toLowerCase().startsWith("get")) {
            this.facts.add(Fact.makeGetterFact(
              getQualifiedName(iMethodBinding), 
              getQualifiedName(ivb)));
          } else if (simpleMethodName.toLowerCase().startsWith("set")) {
            this.facts.add(Fact.makeSetterFact(
              getQualifiedName(iMethodBinding), 
              getQualifiedName(ivb)));
          }
        }
        catch (Exception localException)
        {
          System.err.println("Cannot resolve bindings for exceptions");
        }
      }
      break;
    }
    return true;
  }
  
  public boolean visit(MethodInvocation node)
  {
    IMethodBinding mmtb = node.resolveMethodBinding();
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    try
    {
      if (node.getExpression() != null) {
        if (mmtb.getDeclaringClass().getQualifiedName().startsWith("java.awt.geom.Path2D"))
        {
          Expression e = node.getExpression();
          ITypeBinding itb = e.resolveTypeBinding();
          this.facts.add(Fact.makeCallsFact(getQualifiedName((IMethodBinding)this.mtbStack.peek()), 
            getQualifiedName(itb) + "#" + getSimpleName(mmtb)));
          break label179;
        }
      }
      this.facts.add(Fact.makeCallsFact(getQualifiedName((IMethodBinding)this.mtbStack.peek()), 
        getQualifiedName(mmtb)));
    }
    catch (Exception localException)
    {
      System.err.println("Cannot resolve method invocation \"" + 
        node.getName().toString() + "\"");
    }
    label179:
    return true;
  }
  
  public boolean visit(SuperMethodInvocation node)
  {
    IMethodBinding mmtb = node.resolveMethodBinding();
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    try
    {
      this.facts.add(Fact.makeCallsFact(getQualifiedName((IMethodBinding)this.mtbStack.peek()), 
        getQualifiedName(mmtb)));
    }
    catch (Exception localException)
    {
      System.err.println("Cannot resolve method invocation \"" + 
        node.getName().toString() + "\"");
    }
    return true;
  }
  
  public boolean visit(ClassInstanceCreation node)
  {
    IMethodBinding mmtb = node.resolveConstructorBinding();
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    try
    {
      this.facts.add(Fact.makeCallsFact(getQualifiedName((IMethodBinding)this.mtbStack.peek()), 
        getQualifiedName(mmtb)));
    }
    catch (Exception localException)
    {
      System.err.println("Cannot resolve class instance creation \"" + 
        node.getType().toString() + "\"");
    }
    return true;
  }
  
  public boolean visit(ConstructorInvocation node)
  {
    IMethodBinding mmtb = node.resolveConstructorBinding();
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    try
    {
      this.facts.add(Fact.makeCallsFact(getQualifiedName((IMethodBinding)this.mtbStack.peek()), 
        getQualifiedName(mmtb)));
    }
    catch (Exception localException)
    {
      System.err.println("Cannot resolve constructor invocation in \"\"");
    }
    return true;
  }
  
  public boolean visit(SuperConstructorInvocation node)
  {
    IMethodBinding mmtb = node.resolveConstructorBinding();
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    try
    {
      this.facts.add(Fact.makeCallsFact(getQualifiedName((IMethodBinding)this.mtbStack.peek()), 
        getQualifiedName(mmtb)));
    }
    catch (Exception localException)
    {
      System.err.println("Cannot resolve super constructor invocation in \"\"");
    }
    return true;
  }
  
  public boolean visit(VariableDeclarationStatement vds)
  {
    if (this.mtbStack.isEmpty()) {
      return true;
    }
    for (Object ovdf : vds.fragments())
    {
      VariableDeclarationFragment vdf = (VariableDeclarationFragment)ovdf;
      try
      {
        this.facts.add(Fact.makeLocalVarFact(getQualifiedName(
          (IMethodBinding)this.mtbStack.peek()), getQualifiedName(vds.getType()
          .resolveBinding()), vdf.getName().getIdentifier(), vdf
          .getInitializer().toString()));
      }
      catch (Exception localException)
      {
        System.err.println("Cannot resolve variable declaration \"" + 
          vdf.getName().toString() + "\"");
      }
    }
    return true;
  }
}
