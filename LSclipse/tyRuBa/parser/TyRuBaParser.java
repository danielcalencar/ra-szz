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
package tyRuBa.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;
import tyRuBa.engine.Frame;
import tyRuBa.engine.FrontEnd;
import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.QueryEngine;
import tyRuBa.engine.RBCompoundExpression;
import tyRuBa.engine.RBCountAll;
import tyRuBa.engine.RBDisjunction;
import tyRuBa.engine.RBExistsQuantifier;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBFact;
import tyRuBa.engine.RBFindAll;
import tyRuBa.engine.RBJavaObjectCompoundTerm;
import tyRuBa.engine.RBModeSwitchExpression;
import tyRuBa.engine.RBNotFilter;
import tyRuBa.engine.RBPair;
import tyRuBa.engine.RBPredicateExpression;
import tyRuBa.engine.RBQuoted;
import tyRuBa.engine.RBRule;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTestFilter;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RBUniqueQuantifier;
import tyRuBa.engine.RBVariable;
import tyRuBa.modes.BindingList;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.CompositeType;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCase;
import tyRuBa.modes.PredInfo;
import tyRuBa.modes.TVar;
import tyRuBa.modes.TVarFactory;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeModeError;
import tyRuBa.util.ElementSource;

public class TyRuBaParser
  implements TyRuBaParserConstants
{
  private PrintStream outputStream;
  private URL baseURL = null;
  private boolean interactive;
  public TyRuBaParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  public Token token;
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos;
  private Token jj_lastpos;
  private int jj_la;
  
  public TyRuBaParser(InputStream is, PrintStream os)
  {
    this(is, os, null);
  }
  
  public TyRuBaParser(InputStream is, PrintStream os, URL base)
  {
    this(is);
    this.outputStream = os;
    
    this.interactive = (is == System.in);
  }
  
  public static void parse(QueryEngine rules, String fileName, PrintStream os)
    throws ParseException, IOException, TypeModeError
  {
    parse(rules, new File(fileName).toURL(), os);
  }
  
  public static void parse(QueryEngine rules, InputStream is, PrintStream os)
    throws ParseException, TypeModeError
  {
    TyRuBaParser parser = new TyRuBaParser(is, os);
    parser.CompilationUnit(rules);
  }
  
  public static RBExpression parseExpression(InputStream is, PrintStream os, QueryEngine rules)
    throws ParseException, TypeModeError
  {
    TyRuBaParser parser = new TyRuBaParser(is, os);
    return parser.ExpressionAndEOF(rules);
  }
  
  static String internalStringLiteral(String src)
  {
    StringBuffer trg = new StringBuffer(src.length());
    for (int i = 0; i < src.length(); i++) {
      if (src.charAt(i) == '\\')
      {
        i++;
        
        trg.append(src.charAt(i));
      }
      else
      {
        trg.append(src.charAt(i));
      }
    }
    return trg.toString();
  }
  
  static String stringLiteral(String src)
  {
    return internalStringLiteral(stripQuotes(src));
  }
  
  static String stripQuotes(String src)
  {
    return src.substring(1, src.length() - 1);
  }
  
  static String javaClassName(String classToken)
  {
    if (classToken.endsWith("[]")) {
      return "[L" + classToken.substring(1, classToken.length() - 2) + ";";
    }
    return classToken.substring(1);
  }
  
  private static String undoubleQuestionMarks(String src)
  {
    StringBuffer trg = new StringBuffer(src.length());
    for (int i = 0; i < src.length(); i++) {
      if ((src.charAt(i) == '?') && (i + 1 < src.length()) && 
        (src.charAt(i + 1) == '?')) {
        trg.append(src.charAt(i++));
      } else {
        trg.append(src.charAt(i));
      }
    }
    return trg.toString();
  }
  
  private static RBTerm makeQuotedCodeName(String s, int startName)
  {
    int startVar = s.indexOf('?', startName);
    while ((startVar < s.length()) && (s.charAt(startVar + 1) == '?')) {
      startVar = s.indexOf('?', startVar + 2);
    }
    if (startVar == -1)
    {
      if (s.length() - startName > 1) {
        return FrontEnd.makeName(
          undoubleQuestionMarks(s.substring(startName, s.length() - 1)));
      }
      return FrontEnd.theEmptyList;
    }
    if (startVar == startName) {
      return makeQuotedCodeVar(s, startVar);
    }
    RBTerm car = FrontEnd.makeName(
      undoubleQuestionMarks(s.substring(startName, startVar)));
    return new RBPair(car, makeQuotedCodeVar(s, startVar));
  }
  
  private static RBTerm makeQuotedCodeVar(String s, int startVar)
  {
    int startName = startVar + 1;
    while ((startName < s.length() - 1) && (
      Character.isJavaIdentifierPart(s.charAt(startName)))) {
      startName++;
    }
    RBVariable car = FrontEnd.makeVar(s.substring(startVar, startName));
    return new RBPair(car, makeQuotedCodeName(s, startName));
  }
  
  static RBTerm makeQuotedCode(String s)
  {
    return new RBQuoted(makeQuotedCodeName(s, 1));
  }
  
  public final void CompilationUnit(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    for (;;)
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 13: 
      case 14: 
      case 26: 
      case 54: 
      case 60: 
        break;
      default: 
        this.jj_la1[0] = this.jj_gen;
        break;
      }
      if (jj_2_1(2))
      {
        PredInfoRules(rules);
        if (this.interactive) {
          System.err.println("** predicate info added to db **");
        }
      }
      else
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 26: 
          UserDefinedTypeDeclaration(rules);
          if (this.interactive) {
            System.err.println("** I have defined your type! **");
          }
          break;
        case 60: 
          Rule(rules);
          if (this.interactive) {
            System.err.println("** assertion added to rulebase **");
          }
          break;
        case 54: 
          Query(rules);
          break;
        case 13: 
          IncludeDirective(rules);
          break;
        case 14: 
          LibraryDirective(rules);
        }
      }
    }
    this.jj_la1[1] = this.jj_gen;
    jj_consume_token(-1);
    throw new ParseException();
    
    jj_consume_token(0);
  }
  
  public final void LibraryDirective(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(14);
    Token t = jj_consume_token(12);
    try
    {
      String fileName = stringLiteral(t.image);
      System.err.println("LOADING LIBRARY " + fileName);
      rules.loadLibrary(fileName);
      System.err.println("LOADING LIBRARY " + fileName + " Done");
    }
    catch (MalformedURLException e)
    {
      System.err.println("Warning: MalformedURL in #library");
      System.err.println(e.getMessage());
    }
    catch (IOException e)
    {
      System.err.println("Warning: IOException in #library");
      System.err.println(e.getMessage());
    }
  }
  
  public final void IncludeDirective(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(13);
    Token t = jj_consume_token(12);
    try
    {
      String fileName = stringLiteral(t.image);
      URL url;
      URL url;
      if (this.baseURL != null) {
        url = new URL(this.baseURL, fileName);
      } else {
        url = new URL(fileName);
      }
      System.err.println("INCLUDING " + url.toString());
      parse(rules, url, this.outputStream);
      System.err.println("INCLUDING " + url.toString() + " Done");
    }
    catch (MalformedURLException e)
    {
      System.err.println("Warning: MalformedURL in #include");
      System.err.println(e.getMessage());
    }
    catch (IOException e)
    {
      System.err.println("Warning: IOException in #include");
      System.err.println(e.getMessage());
    }
  }
  
  public final RBFact Fact(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBPredicateExpression pred = SimplePredicate(rules);
    return new RBFact(pred);
  }
  
  public final void Rule(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBExpression exp = null;
    RBPredicateExpression pred = SimplePredicate(rules);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 54: 
      jj_consume_token(54);
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 16: 
      case 17: 
      case 19: 
      case 20: 
      case 21: 
      case 22: 
      case 41: 
      case 60: 
        exp = Expression(rules);
        break;
      case 33: 
        exp = ModeSwitchExpression(rules);
        break;
      default: 
        this.jj_la1[2] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default: 
      this.jj_la1[3] = this.jj_gen;
    }
    jj_consume_token(49);
    if (exp == null) {
      rules.insert(pred);
    } else {
      rules.insert(new RBRule(pred, exp));
    }
  }
  
  public final RBPredicateExpression SimplePredicate(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    Token t = jj_consume_token(60);
    jj_consume_token(41);
    ArrayList terms = new ArrayList();
    TermList(terms, rules);
    RBPredicateExpression e = new RBPredicateExpression(t.image, terms);
    jj_consume_token(42);
    return e;
  }
  
  public final void predNameList(ArrayList names)
    throws ParseException
  {
    Token t = jj_consume_token(60);
    names.add(t.image);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 48: 
      jj_consume_token(48);
      predNameList(names);
      break;
    default: 
      this.jj_la1[4] = this.jj_gen;
    }
  }
  
  public final void PredInfoRules(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    ArrayList names = new ArrayList();
    
    ArrayList predModes = new ArrayList();
    boolean isPersistent = false;
    predNameList(names);
    jj_consume_token(40);
    TupleType types = Factory.makeTupleType();
    TVarFactory tfact = new TVarFactory();
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 43: 
    case 45: 
    case 57: 
    case 60: 
    case 61: 
      TypeList(types, tfact, rules);
      break;
    case 41: 
      jj_consume_token(41);
      jj_consume_token(42);
      break;
    default: 
      this.jj_la1[5] = this.jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 37: 
      jj_consume_token(37);
      isPersistent = true;
      break;
    default: 
      this.jj_la1[6] = this.jj_gen;
    }
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 23: 
      jj_consume_token(23);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 41: 
          break;
        default: 
          this.jj_la1[7] = this.jj_gen;
          break;
        }
        ModeRule(predModes, types.size(), names);
      }
      jj_consume_token(28);
      break;
    default: 
      this.jj_la1[8] = this.jj_gen;
    }
    for (int i = 0; i < names.size(); i++)
    {
      PredInfo p = Factory.makePredInfo(rules, (String)names.get(i), types, predModes, isPersistent);
      rules.insertPredInfo(p);
    }
  }
  
  public final void TypeList(TupleType types, TVarFactory tfact, QueryEngine rules)
    throws ParseException, TypeModeError
  {
    Type t = Type(tfact, rules);
    types.add(t);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 48: 
      jj_consume_token(48);
      TypeList(types, tfact, rules);
      break;
    default: 
      this.jj_la1[9] = this.jj_gen;
    }
  }
  
  public final Type Type(TVarFactory tfact, QueryEngine rules)
    throws ParseException, TypeModeError
  {
    Type t;
    if (jj_2_2(3))
    {
      t = CompositeType(tfact, rules);
    }
    else
    {
      Type t;
      Type t;
      Type t;
      Type t;
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 57: 
      case 60: 
        t = AtomicType(rules);
        break;
      case 61: 
        t = TypeVariable(tfact);
        break;
      case 45: 
        t = TupleType(tfact, rules);
        break;
      case 43: 
        t = ListType(tfact, rules);
        break;
      default: 
        this.jj_la1[10] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    Type t;
    return t;
  }
  
  public final Type AtomicType(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    boolean strict = false;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 57: 
      jj_consume_token(57);
      strict = true;
      break;
    default: 
      this.jj_la1[11] = this.jj_gen;
    }
    Token t = jj_consume_token(60);
    Type type;
    Type type;
    if (strict) {
      type = Factory.makeStrictAtomicType(rules.findType(t.image));
    } else {
      type = Factory.makeAtomicType(rules.findType(t.image));
    }
    return type;
  }
  
  public final Type CompositeType(TVarFactory tfact, QueryEngine rules)
    throws ParseException, TypeModeError
  {
    boolean strict = false;
    int arity = -1;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 57: 
      jj_consume_token(57);
      strict = true;
      break;
    default: 
      this.jj_la1[12] = this.jj_gen;
    }
    Token t = jj_consume_token(60);
    
    TupleType args = TupleType(tfact, rules);
    arity = args.size();
    Type type;
    Type type;
    if (strict) {
      type = rules.findTypeConst(t.image, arity).applyStrict(args, false);
    } else {
      type = rules.findTypeConst(t.image, arity).apply(args, false);
    }
    return type;
  }
  
  public final Type ListType(TVarFactory tfact, QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(43);
    Type t = Type(tfact, rules);
    jj_consume_token(44);
    return Factory.makeListType(t);
  }
  
  public final TupleType TupleType(TVarFactory tfact, QueryEngine rules)
    throws ParseException, TypeModeError
  {
    TupleType types = Factory.makeTupleType();
    jj_consume_token(45);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 43: 
    case 45: 
    case 57: 
    case 60: 
    case 61: 
      TypeList(types, tfact, rules);
      break;
    default: 
      this.jj_la1[13] = this.jj_gen;
    }
    jj_consume_token(46);
    return types;
  }
  
  public final TVar TypeVariable(TVarFactory tfact)
    throws ParseException
  {
    Token t = jj_consume_token(61);
    return tfact.makeTVar(t.image.substring(1));
  }
  
  public final void ModeRule(ArrayList predModes, int numArgs, ArrayList names)
    throws ParseException, TypeModeError
  {
    BindingList bList = Factory.makeBindingList();
    
    boolean toBeCheck = true;
    jj_consume_token(41);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 33: 
    case 60: 
      bList = ModeElem(bList);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 48: 
          break;
        default: 
          this.jj_la1[14] = this.jj_gen;
          break;
        }
        jj_consume_token(48);
        bList = ModeElem(bList);
      }
    }
    this.jj_la1[15] = this.jj_gen;
    
    jj_consume_token(42);
    if (bList.size() != numArgs) {
      throw new TypeModeError(
        "Number of arguments in mode declaration is different from type declaration in predicate(s) " + 
        names);
    }
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 25: 
      jj_consume_token(25);
      toBeCheck = false;
      break;
    default: 
      this.jj_la1[16] = this.jj_gen;
    }
    jj_consume_token(24);
    Mode mode = PredMode();
    if ((bList.size() == 0) && (
      (mode.isMulti()) || (mode.isNondet()))) {
      throw new TypeModeError(
        "Predicate with no argument can never return more than one result in the predicate(s)" + 
        names);
    }
    predModes.add(Factory.makePredicateMode(bList, mode, toBeCheck));
  }
  
  public final BindingList ModeElem(BindingList bList)
    throws ParseException
  {
    BindingMode bm = Mode();
    bList.add(bm);
    return bList;
  }
  
  public final BindingMode Mode()
    throws ParseException
  {
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 60: 
      Token t = jj_consume_token(60);
      if (t.image.equals("B")) {
        return Factory.makeBound();
      }
      if ((t.image.equals("F")) || (t.image.equals("FREE"))) {
        return Factory.makeFree();
      }
      throw new ParseException("Unknow binding mode " + t.image);
    case 33: 
      jj_consume_token(33);
      return Factory.makeBound();
    }
    this.jj_la1[17] = this.jj_gen;
    jj_consume_token(-1);
    throw new ParseException();
  }
  
  public final Mode PredMode()
    throws ParseException
  {
    Mode m;
    Mode m;
    Mode m;
    Mode m;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 29: 
      m = Det();
      break;
    case 30: 
      m = SemiDet();
      break;
    case 31: 
      m = Multi();
      break;
    case 32: 
      m = NonDet();
      break;
    default: 
      this.jj_la1[18] = this.jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    Mode m;
    return m;
  }
  
  public final Mode Det()
    throws ParseException
  {
    jj_consume_token(29);
    return Mode.makeDet();
  }
  
  public final Mode SemiDet()
    throws ParseException
  {
    jj_consume_token(30);
    return Mode.makeSemidet();
  }
  
  public final Mode Multi()
    throws ParseException
  {
    jj_consume_token(31);
    return Mode.makeMulti();
  }
  
  public final Mode NonDet()
    throws ParseException
  {
    jj_consume_token(32);
    return Mode.makeNondet();
  }
  
  public final TypeConstructor ExistingTypeAtomName(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    Token t = jj_consume_token(60);
    return rules.findType(t.image);
  }
  
  public final void UserDefinedTypeDeclaration(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    TVarFactory tfact = new TVarFactory();
    jj_consume_token(26);
    CompositeType t1 = NewCompositeType(rules, tfact);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 57: 
      jj_consume_token(57);
      Type t2 = ExistingType(rules, tfact);
      t1.addSubType(t2);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 52: 
          break;
        default: 
          this.jj_la1[19] = this.jj_gen;
          break;
        }
        jj_consume_token(52);
        t2 = ExistingType(rules, tfact);
        t1.addSubType(t2);
      }
    case 27: 
      jj_consume_token(27);
      Type representedBy = Type(tfact, rules);
      t1.setRepresentationType(representedBy);
      rules.addFunctorConst(representedBy, t1);
      break;
    default: 
      this.jj_la1[20] = this.jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }
  
  public final CompositeType NewCompositeType(QueryEngine rules, TVarFactory tfact)
    throws ParseException, TypeModeError
  {
    TupleType tuple = Factory.makeTupleType();
    Token t = jj_consume_token(60);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 45: 
      tuple = tupleOfTVars(tfact);
      break;
    default: 
      this.jj_la1[21] = this.jj_gen;
    }
    return rules.addNewType(
      (CompositeType)Factory.makeTypeConstructor(t.image, tuple.size()).apply(tuple, false));
  }
  
  public final Type ExistingType(QueryEngine rules, TVarFactory tfact)
    throws ParseException, TypeModeError
  {
    TupleType tuple = Factory.makeTupleType();
    Token t = jj_consume_token(60);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 45: 
      tuple = tupleOfTVars(tfact);
      break;
    default: 
      this.jj_la1[22] = this.jj_gen;
    }
    return rules.findTypeConst(t.image, tuple.size()).apply(tuple, false);
  }
  
  public final TupleType tupleOfTVars(TVarFactory tfact)
    throws ParseException
  {
    TupleType tuple = Factory.makeTupleType();
    jj_consume_token(45);
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 61: 
      Type arg = TypeVariable(tfact);
      tuple.add(arg);
      for (;;)
      {
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 48: 
          break;
        default: 
          this.jj_la1[23] = this.jj_gen;
          break;
        }
        jj_consume_token(48);
        arg = TypeVariable(tfact);
        tuple.add(arg);
      }
    }
    this.jj_la1[24] = this.jj_gen;
    
    jj_consume_token(46);
    return tuple;
  }
  
  public final void Query(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(54);
    RBExpression q = Expression(rules);
    jj_consume_token(49);
    System.err.println("##QUERY : " + q);
    ElementSource solutions = rules.frameQuery(q);
    if (!solutions.hasMoreElements())
    {
      System.err.println();
      System.err.println("FAILURE");
    }
    else
    {
      while (solutions.hasMoreElements())
      {
        System.err.println();
        Frame solution = (Frame)solutions.nextElement();
        
        System.err.print(solution.toString());
      }
      System.err.println();
    }
    System.err.println("##END QUERY");
  }
  
  public final ModeCase ModeCase(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    Collection boundVars = new HashSet();
    
    jj_consume_token(33);
    for (;;)
    {
      RBVariable var = Variable();
      boundVars.add(var);
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      }
    }
    this.jj_la1[25] = this.jj_gen;
    
    jj_consume_token(74);
    RBExpression exp = Expression(rules);
    return new ModeCase(boundVars, exp);
  }
  
  public final RBExpression ModeSwitchExpression(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    ModeCase mc = ModeCase(rules);
    RBModeSwitchExpression msExp = new RBModeSwitchExpression(mc);
    while (jj_2_3(2))
    {
      jj_consume_token(52);
      mc = ModeCase(rules);
      msExp.addModeCase(mc);
    }
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 52: 
      jj_consume_token(52);
      jj_consume_token(34);
      jj_consume_token(74);
      RBExpression defaultExp = Expression(rules);
      msExp.addDefaultCase(defaultExp);
      break;
    default: 
      this.jj_la1[26] = this.jj_gen;
    }
    return msExp;
  }
  
  public final RBExpression ExpressionAndEOF(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBExpression e = Expression(rules);
    jj_consume_token(0);
    return e;
  }
  
  public final RBExpression Expression(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBExpression e;
    RBExpression e;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 17: 
    case 21: 
      e = Quantifier(rules);
      break;
    case 16: 
    case 19: 
    case 20: 
    case 22: 
    case 41: 
    case 60: 
      e = Disjunction(rules);
      break;
    default: 
      this.jj_la1[27] = this.jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    RBExpression e;
    return e;
  }
  
  public final RBExpression Disjunction(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBCompoundExpression ce = null;
    RBExpression e1 = Conjunction(rules);
    for (;;)
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 47: 
        break;
      default: 
        this.jj_la1[28] = this.jj_gen;
        break;
      }
      jj_consume_token(47);
      RBExpression e2 = Conjunction(rules);
      if (ce == null) {
        ce = new RBDisjunction(e1, e2);
      } else {
        ce.addSubexp(e2);
      }
    }
    if (ce == null) {
      return e1;
    }
    return ce;
  }
  
  public final RBExpression Conjunction(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBCompoundExpression ce = null;
    RBExpression e1 = Predicate(rules);
    for (;;)
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 48: 
        break;
      default: 
        this.jj_la1[29] = this.jj_gen;
        break;
      }
      jj_consume_token(48);
      RBExpression e2 = Predicate(rules);
      if (ce == null) {
        ce = FrontEnd.makeAnd(e1, e2);
      } else {
        ce.addSubexp(e2);
      }
    }
    if (ce == null) {
      return e1;
    }
    return ce;
  }
  
  public final RBExpression Predicate(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBExpression e;
    RBExpression e;
    RBExpression e;
    RBExpression e;
    RBExpression e;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 60: 
      e = PredicateExpression(rules);
      break;
    case 16: 
      e = NotFilter(rules);
      break;
    case 22: 
      e = TestFilter(rules);
      break;
    case 19: 
      e = FindAll(rules);
      break;
    case 20: 
      e = CountAll(rules);
      break;
    case 41: 
      jj_consume_token(41);
      RBExpression e = Expression(rules);
      jj_consume_token(42);
      break;
    default: 
      this.jj_la1[30] = this.jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    RBExpression e;
    return e;
  }
  
  public final RBExpression PredicateExpression(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    ArrayList terms = new ArrayList();
    
    Token t = jj_consume_token(60);
    jj_consume_token(41);
    TermList(terms, rules);
    jj_consume_token(42);
    return new RBPredicateExpression(t.image, terms);
  }
  
  public final RBExpression FindAll(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(19);
    jj_consume_token(41);
    RBExpression p = Predicate(rules);
    jj_consume_token(48);
    RBTerm t1 = Term(rules);
    jj_consume_token(48);
    RBTerm t2 = Term(rules);
    jj_consume_token(42);
    return new RBFindAll(p, t1, t2);
  }
  
  public final RBExpression CountAll(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(20);
    jj_consume_token(41);
    RBExpression p = Predicate(rules);
    jj_consume_token(48);
    RBTerm t1 = Term(rules);
    jj_consume_token(48);
    RBTerm t2 = Term(rules);
    jj_consume_token(42);
    return new RBCountAll(p, t1, t2);
  }
  
  public final RBExpression NotFilter(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(16);
    jj_consume_token(41);
    RBExpression e = Expression(rules);
    jj_consume_token(42);
    return new RBNotFilter(e);
  }
  
  public final RBExpression TestFilter(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(22);
    jj_consume_token(41);
    RBExpression e = Expression(rules);
    jj_consume_token(42);
    return new RBTestFilter(e);
  }
  
  public final RBExpression Quantifier(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    ArrayList vars = new ArrayList();
    Token t;
    Token t;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 17: 
      t = jj_consume_token(17);
      break;
    case 21: 
      t = jj_consume_token(21);
      break;
    case 18: 
    case 19: 
    case 20: 
    default: 
      this.jj_la1[31] = this.jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    Token t;
    varList(vars);
    jj_consume_token(74);
    RBExpression e = Expression(rules);
    if (t.image == "EXISTS") {
      return new RBExistsQuantifier(vars, e);
    }
    if (t.image == "UNIQUE") {
      return new RBUniqueQuantifier(vars, e);
    }
    throw new Error("Missing return statement in function");
  }
  
  public final void varList(ArrayList v)
    throws ParseException
  {
    RBVariable var = Variable();
    v.add(var);
    for (;;)
    {
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 48: 
        break;
      default: 
        this.jj_la1[32] = this.jj_gen;
        break;
      }
      jj_consume_token(48);
      var = Variable();
      v.add(var);
    }
  }
  
  public final RBTerm Term(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBTerm t;
    if (jj_2_4(2))
    {
      t = CompoundTerm(rules);
    }
    else
    {
      RBTerm t;
      RBTerm t;
      RBTerm t;
      RBTerm t;
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 6: 
      case 10: 
      case 12: 
      case 60: 
      case 61: 
      case 62: 
      case 63: 
      case 64: 
        t = SimpleTerm(rules);
        break;
      case 45: 
        t = Tuple(rules);
        break;
      case 73: 
        t = QuotedCode();
        break;
      case 43: 
        t = List(rules);
        break;
      default: 
        this.jj_la1[33] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    RBTerm t;
    return t;
  }
  
  public final RBTerm Tuple(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    ArrayList terms = new ArrayList();
    jj_consume_token(45);
    TermList(terms, rules);
    jj_consume_token(46);
    return RBTuple.make(terms);
  }
  
  public final RBTerm CompoundTerm(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    ArrayList terms = new ArrayList();
    if (jj_2_5(2))
    {
      Token typeName = jj_consume_token(60);
      RBTerm t;
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 45: 
        jj_consume_token(45);
        TermList(terms, rules);
        RBTerm t = rules.findConstructorType(new FunctorIdentifier(typeName.image, terms.size()))
          .apply(terms);
        jj_consume_token(46);
        break;
      case 43: 
        jj_consume_token(43);
        RBTerm t = RealTermList(rules);
        t = rules.findConstructorType(new FunctorIdentifier(typeName.image, 1))
          .apply(t);
        jj_consume_token(44);
        break;
      case 44: 
      default: 
        this.jj_la1[34] = this.jj_gen;
        jj_consume_token(-1);
        throw new ParseException();break;
      }
    }
    else
    {
      RBTerm t;
      if (jj_2_6(2))
      {
        t = SimpleTerm(rules);
      }
      else
      {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    RBTerm t;
    return t;
  }
  
  public final RBTerm SimpleTerm(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBTerm t;
    RBTerm t;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 6: 
    case 10: 
    case 12: 
    case 60: 
    case 62: 
    case 63: 
    case 64: 
      t = Constant();
      break;
    case 61: 
      t = Variable();
      break;
    default: 
      this.jj_la1[35] = this.jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    RBTerm t;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 40: 
      jj_consume_token(40);
      TypeConstructor typeAtom = ExistingTypeAtomName(rules);
      t = t.addTypeCast(typeAtom);
      break;
    default: 
      this.jj_la1[36] = this.jj_gen;
    }
    return t;
  }
  
  public final RBTerm List(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    jj_consume_token(43);
    RBTerm r = RealTermList(rules);
    jj_consume_token(44);
    return r;
  }
  
  public final RBTerm Constant()
    throws ParseException
  {
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 6: 
      Token t = jj_consume_token(6);
      return FrontEnd.makeInteger(t.image);
    case 10: 
      Token t = jj_consume_token(10);
      return FrontEnd.makeReal(t.image);
    case 12: 
      Token t = jj_consume_token(12);
      return FrontEnd.makeName(stringLiteral(t.image));
    case 60: 
      Token t = jj_consume_token(60);
      return FrontEnd.makeName(t.image);
    case 62: 
      Token t = jj_consume_token(62);
      return FrontEnd.makeTemplateVar(t.image);
    case 63: 
      Token t = jj_consume_token(63);
      return RBJavaObjectCompoundTerm.javaClass(javaClassName(t.image));
    case 64: 
      Token t = jj_consume_token(64);
      return RBJavaObjectCompoundTerm.regexp(stripQuotes(t.image));
    }
    this.jj_la1[37] = this.jj_gen;
    jj_consume_token(-1);
    throw new ParseException();
  }
  
  public final void TermList(ArrayList terms, QueryEngine rules)
    throws ParseException, TypeModeError
  {
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 6: 
    case 10: 
    case 12: 
    case 43: 
    case 45: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 64: 
    case 73: 
      RBTerm t = Term(rules);
      terms.add(t);
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 48: 
        jj_consume_token(48);
        TermList(terms, rules);
        break;
      default: 
        this.jj_la1[38] = this.jj_gen;
      }
      break;
    default: 
      this.jj_la1[39] = this.jj_gen;
    }
  }
  
  public final RBTerm RealTermList(QueryEngine rules)
    throws ParseException, TypeModeError
  {
    RBTerm t2 = FrontEnd.theEmptyList;
    switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
    {
    case 6: 
    case 10: 
    case 12: 
    case 43: 
    case 45: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 64: 
    case 73: 
      RBTerm t1 = Term(rules);
      switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
      {
      case 48: 
      case 52: 
        switch (this.jj_ntk == -1 ? jj_ntk() : this.jj_ntk)
        {
        case 48: 
          jj_consume_token(48);
          t2 = RealTermList(rules);
          break;
        case 52: 
          jj_consume_token(52);
          t2 = Term(rules);
          break;
        case 49: 
        case 50: 
        case 51: 
        default: 
          this.jj_la1[40] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      case 49: 
      case 50: 
      case 51: 
      default: 
        this.jj_la1[41] = this.jj_gen;
      }
      return new RBPair(t1, t2);
    }
    this.jj_la1[42] = this.jj_gen;
    return FrontEnd.theEmptyList;
  }
  
  public final RBVariable Variable()
    throws ParseException
  {
    Token t = jj_consume_token(61);
    RBVariable var;
    RBVariable var;
    if (t.image.length() == 1) {
      var = FrontEnd.makeIgnoredVar();
    } else {
      var = FrontEnd.makeVar(t.image);
    }
    return var;
  }
  
  public final RBTerm QuotedCode()
    throws ParseException
  {
    Token t = jj_consume_token(73);
    return makeQuotedCode(t.image);
  }
  
  private final boolean jj_2_1(int xla)
  {
    this.jj_la = xla;this.jj_lastpos = (this.jj_scanpos = this.token);
    try
    {
      return !jj_3_1();
    }
    catch (LookaheadSuccess localLookaheadSuccess)
    {
      return true;
    }
    finally
    {
      jj_save(0, xla);
    }
  }
  
  private final boolean jj_2_2(int xla)
  {
    this.jj_la = xla;this.jj_lastpos = (this.jj_scanpos = this.token);
    try
    {
      return !jj_3_2();
    }
    catch (LookaheadSuccess localLookaheadSuccess)
    {
      return true;
    }
    finally
    {
      jj_save(1, xla);
    }
  }
  
  private final boolean jj_2_3(int xla)
  {
    this.jj_la = xla;this.jj_lastpos = (this.jj_scanpos = this.token);
    try
    {
      return !jj_3_3();
    }
    catch (LookaheadSuccess localLookaheadSuccess)
    {
      return true;
    }
    finally
    {
      jj_save(2, xla);
    }
  }
  
  private final boolean jj_2_4(int xla)
  {
    this.jj_la = xla;this.jj_lastpos = (this.jj_scanpos = this.token);
    try
    {
      return !jj_3_4();
    }
    catch (LookaheadSuccess localLookaheadSuccess)
    {
      return true;
    }
    finally
    {
      jj_save(3, xla);
    }
  }
  
  private final boolean jj_2_5(int xla)
  {
    this.jj_la = xla;this.jj_lastpos = (this.jj_scanpos = this.token);
    try
    {
      return !jj_3_5();
    }
    catch (LookaheadSuccess localLookaheadSuccess)
    {
      return true;
    }
    finally
    {
      jj_save(4, xla);
    }
  }
  
  private final boolean jj_2_6(int xla)
  {
    this.jj_la = xla;this.jj_lastpos = (this.jj_scanpos = this.token);
    try
    {
      return !jj_3_6();
    }
    catch (LookaheadSuccess localLookaheadSuccess)
    {
      return true;
    }
    finally
    {
      jj_save(5, xla);
    }
  }
  
  private final boolean jj_3R_11()
  {
    if (jj_3R_18()) {
      return true;
    }
    if (jj_scan_token(40)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_42()
  {
    if (jj_scan_token(61)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_25()
  {
    if (jj_3R_28()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_24()
  {
    if (jj_scan_token(48)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_20()
  {
    if (jj_scan_token(45)) {
      return true;
    }
    Token xsp = this.jj_scanpos;
    if (jj_3R_25()) {
      this.jj_scanpos = xsp;
    }
    if (jj_scan_token(46)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_22()
  {
    if (jj_3R_27()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_35()
  {
    if (jj_scan_token(64)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_13()
  {
    if (jj_scan_token(33)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_34()
  {
    if (jj_scan_token(63)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_18()
  {
    if (jj_scan_token(60)) {
      return true;
    }
    Token xsp = this.jj_scanpos;
    if (jj_3R_24()) {
      this.jj_scanpos = xsp;
    }
    return false;
  }
  
  private final boolean jj_3R_26()
  {
    Token xsp = this.jj_scanpos;
    if (jj_3R_29())
    {
      this.jj_scanpos = xsp;
      if (jj_3R_30())
      {
        this.jj_scanpos = xsp;
        if (jj_3R_31())
        {
          this.jj_scanpos = xsp;
          if (jj_3R_32())
          {
            this.jj_scanpos = xsp;
            if (jj_3R_33())
            {
              this.jj_scanpos = xsp;
              if (jj_3R_34())
              {
                this.jj_scanpos = xsp;
                if (jj_3R_35()) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }
  
  private final boolean jj_3R_29()
  {
    if (jj_scan_token(6)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_33()
  {
    if (jj_scan_token(62)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_32()
  {
    if (jj_scan_token(60)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_31()
  {
    if (jj_scan_token(12)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_30()
  {
    if (jj_scan_token(10)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_43()
  {
    if (jj_scan_token(43)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_21()
  {
    if (jj_3R_26()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_17()
  {
    Token xsp = this.jj_scanpos;
    if (jj_3R_21())
    {
      this.jj_scanpos = xsp;
      if (jj_3R_22()) {
        return true;
      }
    }
    xsp = this.jj_scanpos;
    if (jj_3R_23()) {
      this.jj_scanpos = xsp;
    }
    return false;
  }
  
  private final boolean jj_3_6()
  {
    if (jj_3R_17()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_23()
  {
    if (jj_scan_token(40)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_19()
  {
    if (jj_scan_token(57)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_12()
  {
    Token xsp = this.jj_scanpos;
    if (jj_3R_19()) {
      this.jj_scanpos = xsp;
    }
    if (jj_scan_token(60)) {
      return true;
    }
    if (jj_3R_20()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_16()
  {
    if (jj_scan_token(43)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_15()
  {
    if (jj_scan_token(45)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3_5()
  {
    if (jj_scan_token(60)) {
      return true;
    }
    Token xsp = this.jj_scanpos;
    if (jj_3R_15())
    {
      this.jj_scanpos = xsp;
      if (jj_3R_16()) {
        return true;
      }
    }
    return false;
  }
  
  private final boolean jj_3_1()
  {
    if (jj_3R_11()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_44()
  {
    if (jj_scan_token(57)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_41()
  {
    Token xsp = this.jj_scanpos;
    if (jj_3R_44()) {
      this.jj_scanpos = xsp;
    }
    if (jj_scan_token(60)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_14()
  {
    Token xsp = this.jj_scanpos;
    if (jj_3_5())
    {
      this.jj_scanpos = xsp;
      if (jj_3_6()) {
        return true;
      }
    }
    return false;
  }
  
  private final boolean jj_3R_40()
  {
    if (jj_3R_43()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_39()
  {
    if (jj_3R_20()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_38()
  {
    if (jj_3R_42()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_37()
  {
    if (jj_3R_41()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3_2()
  {
    if (jj_3R_12()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_36()
  {
    Token xsp = this.jj_scanpos;
    if (jj_3_2())
    {
      this.jj_scanpos = xsp;
      if (jj_3R_37())
      {
        this.jj_scanpos = xsp;
        if (jj_3R_38())
        {
          this.jj_scanpos = xsp;
          if (jj_3R_39())
          {
            this.jj_scanpos = xsp;
            if (jj_3R_40()) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  private final boolean jj_3R_28()
  {
    if (jj_3R_36()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3_4()
  {
    if (jj_3R_14()) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3R_27()
  {
    if (jj_scan_token(61)) {
      return true;
    }
    return false;
  }
  
  private final boolean jj_3_3()
  {
    if (jj_scan_token(52)) {
      return true;
    }
    if (jj_3R_13()) {
      return true;
    }
    return false;
  }
  
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  private final int[] jj_la1 = new int[43];
  private static int[] jj_la1_0;
  private static int[] jj_la1_1;
  private static int[] jj_la1_2;
  
  static
  {
    jj_la1_0();
    jj_la1_1();
    jj_la1_2();
  }
  
  private static void jj_la1_0()
  {
    jj_la1_0 = new int[] { 67133440, 67133440, 8060928, 0, 0, 0, 0, 0, 8388608, 0, 0, 0, 0, 0, 0, 0, 33554432, 0, -536870912, 0, 134217728, 0, 0, 0, 0, 0, 0, 8060928, 0, 0, 5832704, 2228224, 0, 5184, 0, 5184, 0, 5184, 0, 5184, 0, 0, 5184 };
  }
  
  private static void jj_la1_1()
  {
    jj_la1_1 = new int[] { 272629760, 272629760, 268435970, 4194304, 65536, 838871552, 32, 512, 0, 65536, 838871040, 33554432, 33554432, 838871040, 65536, 268435458, 0, 268435458, 1, 1048576, 33554432, 8192, 8192, 65536, 536870912, 536870912, 1048576, 268435968, 32768, 65536, 268435968, 0, 65536, -268425216, 10240, -268435456, 256, -805306368, 65536, -268425216, 1114112, 1114112, -268425216 };
  }
  
  private static void jj_la1_2()
  {
    jj_la1_2 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 513, 0, 1, 0, 1, 0, 513, 0, 0, 513 };
  }
  
  private final JJCalls[] jj_2_rtns = new JJCalls[6];
  private boolean jj_rescan = false;
  private int jj_gc = 0;
  
  public TyRuBaParser(InputStream stream)
  {
    this.jj_input_stream = new JavaCharStream(stream, 1, 1);
    this.token_source = new TyRuBaParserTokenManager(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 43; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(InputStream stream)
  {
    this.jj_input_stream.ReInit(stream, 1, 1);
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 43; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public TyRuBaParser(Reader stream)
  {
    this.jj_input_stream = new JavaCharStream(stream, 1, 1);
    this.token_source = new TyRuBaParserTokenManager(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 43; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(Reader stream)
  {
    this.jj_input_stream.ReInit(stream, 1, 1);
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 43; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public TyRuBaParser(TyRuBaParserTokenManager tm)
  {
    this.token_source = tm;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 43; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(TyRuBaParserTokenManager tm)
  {
    this.token_source = tm;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 43; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }
  
  private final Token jj_consume_token(int kind)
    throws ParseException
  {
    Token oldToken;
    if ((oldToken = this.token).next != null) {
      this.token = this.token.next;
    } else {
      this.token = (this.token.next = this.token_source.getNextToken());
    }
    this.jj_ntk = -1;
    if (this.token.kind == kind)
    {
      this.jj_gen += 1;
      if (++this.jj_gc > 100)
      {
        this.jj_gc = 0;
        for (int i = 0; i < this.jj_2_rtns.length; i++)
        {
          JJCalls c = this.jj_2_rtns[i];
          while (c != null)
          {
            if (c.gen < this.jj_gen) {
              c.first = null;
            }
            c = c.next;
          }
        }
      }
      return this.token;
    }
    this.token = oldToken;
    this.jj_kind = kind;
    throw generateParseException();
  }
  
  private final LookaheadSuccess jj_ls = new LookaheadSuccess(null);
  
  private final boolean jj_scan_token(int kind)
  {
    if (this.jj_scanpos == this.jj_lastpos)
    {
      this.jj_la -= 1;
      if (this.jj_scanpos.next == null) {
        this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken());
      } else {
        this.jj_lastpos = (this.jj_scanpos = this.jj_scanpos.next);
      }
    }
    else
    {
      this.jj_scanpos = this.jj_scanpos.next;
    }
    if (this.jj_rescan)
    {
      int i = 0;
      for (Token tok = this.token; (tok != null) && (tok != this.jj_scanpos); tok = tok.next) {
        i++;
      }
      if (tok != null) {
        jj_add_error_token(kind, i);
      }
    }
    if (this.jj_scanpos.kind != kind) {
      return true;
    }
    if ((this.jj_la == 0) && (this.jj_scanpos == this.jj_lastpos)) {
      throw this.jj_ls;
    }
    return false;
  }
  
  public final Token getNextToken()
  {
    if (this.token.next != null) {
      this.token = this.token.next;
    } else {
      this.token = (this.token.next = this.token_source.getNextToken());
    }
    this.jj_ntk = -1;
    this.jj_gen += 1;
    return this.token;
  }
  
  public final Token getToken(int index)
  {
    Token t = this.lookingAhead ? this.jj_scanpos : this.token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) {
        t = t.next;
      } else {
        t = t.next = this.token_source.getNextToken();
      }
    }
    return t;
  }
  
  private final int jj_ntk()
  {
    if ((this.jj_nt = this.token.next) == null) {
      return this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind;
    }
    return this.jj_ntk = this.jj_nt.kind;
  }
  
  private Vector jj_expentries = new Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;
  
  private void jj_add_error_token(int kind, int pos)
  {
    if (pos >= 100) {
      return;
    }
    if (pos == this.jj_endpos + 1)
    {
      this.jj_lasttokens[(this.jj_endpos++)] = kind;
    }
    else if (this.jj_endpos != 0)
    {
      this.jj_expentry = new int[this.jj_endpos];
      for (int i = 0; i < this.jj_endpos; i++) {
        this.jj_expentry[i] = this.jj_lasttokens[i];
      }
      boolean exists = false;
      for (Enumeration e = this.jj_expentries.elements(); e.hasMoreElements();)
      {
        int[] oldentry = (int[])e.nextElement();
        if (oldentry.length == this.jj_expentry.length)
        {
          exists = true;
          for (int i = 0; i < this.jj_expentry.length; i++) {
            if (oldentry[i] != this.jj_expentry[i])
            {
              exists = false;
              break;
            }
          }
          if (exists) {
            break;
          }
        }
      }
      if (!exists) {
        this.jj_expentries.addElement(this.jj_expentry);
      }
      if (pos != 0) {
        this.jj_lasttokens[((this.jj_endpos = pos) - 1)] = kind;
      }
    }
  }
  
  public ParseException generateParseException()
  {
    this.jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[75];
    for (int i = 0; i < 75; i++) {
      la1tokens[i] = false;
    }
    if (this.jj_kind >= 0)
    {
      la1tokens[this.jj_kind] = true;
      this.jj_kind = -1;
    }
    for (int i = 0; i < 43; i++) {
      if (this.jj_la1[i] == this.jj_gen) {
        for (int j = 0; j < 32; j++)
        {
          if ((jj_la1_0[i] & 1 << j) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & 1 << j) != 0) {
            la1tokens[(32 + j)] = true;
          }
          if ((jj_la1_2[i] & 1 << j) != 0) {
            la1tokens[(64 + j)] = true;
          }
        }
      }
    }
    for (int i = 0; i < 75; i++) {
      if (la1tokens[i] != 0)
      {
        this.jj_expentry = new int[1];
        this.jj_expentry[0] = i;
        this.jj_expentries.addElement(this.jj_expentry);
      }
    }
    this.jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[this.jj_expentries.size()][];
    for (int i = 0; i < this.jj_expentries.size(); i++) {
      exptokseq[i] = ((int[])this.jj_expentries.elementAt(i));
    }
    return new ParseException(this.token, exptokseq, tokenImage);
  }
  
  private final void jj_rescan_token()
  {
    this.jj_rescan = true;
    for (int i = 0; i < 6; i++)
    {
      JJCalls p = this.jj_2_rtns[i];
      do
      {
        if (p.gen > this.jj_gen)
        {
          this.jj_la = p.arg;this.jj_lastpos = (this.jj_scanpos = p.first);
          switch (i)
          {
          case 0: 
            jj_3_1(); break;
          case 1: 
            jj_3_2(); break;
          case 2: 
            jj_3_3(); break;
          case 3: 
            jj_3_4(); break;
          case 4: 
            jj_3_5(); break;
          case 5: 
            jj_3_6();
          }
        }
        p = p.next;
      } while (p != null);
    }
    this.jj_rescan = false;
  }
  
  private final void jj_save(int index, int xla)
  {
    JJCalls p = this.jj_2_rtns[index];
    while (p.gen > this.jj_gen)
    {
      if (p.next == null)
      {
        p = p.next = new JJCalls(); break;
      }
      p = p.next;
    }
    p.gen = (this.jj_gen + xla - this.jj_la);p.first = this.token;p.arg = xla;
  }
  
  /* Error */
  public static void parse(QueryEngine rules, URL url, PrintStream os)
    throws ParseException, IOException, TypeModeError
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 97	java/net/URL:openStream	()Ljava/io/InputStream;
    //   4: astore_3
    //   5: new 1	tyRuBa/parser/TyRuBaParser
    //   8: dup
    //   9: aload_3
    //   10: aload_2
    //   11: aload_1
    //   12: invokespecial 58	tyRuBa/parser/TyRuBaParser:<init>	(Ljava/io/InputStream;Ljava/io/PrintStream;Ljava/net/URL;)V
    //   15: astore 4
    //   17: aload 4
    //   19: aload_0
    //   20: invokevirtual 101	tyRuBa/parser/TyRuBaParser:CompilationUnit	(LtyRuBa/engine/QueryEngine;)V
    //   23: goto +12 -> 35
    //   26: astore 5
    //   28: aload_3
    //   29: invokevirtual 105	java/io/InputStream:close	()V
    //   32: aload 5
    //   34: athrow
    //   35: aload_3
    //   36: invokevirtual 105	java/io/InputStream:close	()V
    //   39: return
    // Line number table:
    //   Java source line #40	-> byte code offset #0
    //   Java source line #42	-> byte code offset #5
    //   Java source line #43	-> byte code offset #17
    //   Java source line #45	-> byte code offset #26
    //   Java source line #46	-> byte code offset #28
    //   Java source line #47	-> byte code offset #32
    //   Java source line #46	-> byte code offset #35
    //   Java source line #48	-> byte code offset #39
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	rules	QueryEngine
    //   0	40	1	url	URL
    //   0	40	2	os	PrintStream
    //   4	32	3	is	InputStream
    //   15	3	4	parser	TyRuBaParser
    //   26	7	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   5	26	26	finally
  }
  
  public final void enable_tracing() {}
  
  public final void disable_tracing() {}
  
  static final class JJCalls
  {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }
  
  private static final class LookaheadSuccess
    extends Error
  {}
}
