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
package lsd.rule;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class LSDPredicate
{
  public static int DELETED;
  public static int ADDED;
  public static int BEFORE;
  public static int AFTER;
  public static int MODIFIED;
  public static int DELETED_P;
  public static int ADDED_P;
  public static int MODIFIED_P;
  public static int UNDEFINED;
  public static int PACKAGELEVEL;
  public static int CLASSLEVEL;
  public static int METHODLEVEL;
  private static HashMap<String, LSDPredicate> allowedPredicate;
  
  static
  {
    DELETED = 1;
    ADDED = 2;
    BEFORE = 3;
    AFTER = 4;
    MODIFIED = 5;
    DELETED_P = 6;
    ADDED_P = 7;
    MODIFIED_P = 8;
    UNDEFINED = 9;
    
    PACKAGELEVEL = 0;
    CLASSLEVEL = 1;
    METHODLEVEL = 2;
    
    newPredicates();
  }
  
  private static void newPredicates()
  {
    allowedPredicate = new HashMap();
    
    addAllowedPredicate("package", "p");
    
    addAllowedPredicate("type", "tap");
    
    addAllowedPredicate("field", "fbt");
    
    addAllowedPredicate("method", "mct");
    
    addAllowedPredicate("return", "mt");
    addAllowedPredicate("subtype", "tt");
    
    addAllowedPredicate("accesses", "fm");
    addAllowedPredicate("calls", "mm");
    
    addAllowedPredicate("inheritedfield", "btt");
    
    addAllowedPredicate("inheritedmethod", "ctt");
    addAllowedPredicate("fieldoftype", "ft");
    
    addAllowedPredicate("typeintype", "tt");
    
    addAllowedPredicate("extends", "tt");
    addAllowedPredicate("implements", "tt");
    
    addAllowedPredicate("before_package", "p");
    addAllowedPredicate("before_type", "tap");
    addAllowedPredicate("before_field", "fbt");
    addAllowedPredicate("before_method", "mct");
    
    addAllowedPredicate("before_return", "mt");
    addAllowedPredicate("before_subtype", "tt");
    addAllowedPredicate("before_accesses", "fm");
    addAllowedPredicate("before_calls", "mm");
    
    addAllowedPredicate("before_inheritedfield", "btt");
    addAllowedPredicate("before_inheritedmethod", "ctt");
    addAllowedPredicate("before_fieldoftype", "ft");
    addAllowedPredicate("before_typeintype", "tt");
    
    addAllowedPredicate("before_extends", "tt");
    addAllowedPredicate("before_implements", "tt");
    
    addAllowedPredicate("after_package", "p");
    addAllowedPredicate("after_type", "tap");
    addAllowedPredicate("after_field", "fbt");
    addAllowedPredicate("after_method", "mct");
    
    addAllowedPredicate("after_return", "mt");
    addAllowedPredicate("after_subtype", "tt");
    addAllowedPredicate("after_accesses", "fm");
    addAllowedPredicate("after_calls", "mm");
    
    addAllowedPredicate("after_inheritedfield", "btt");
    addAllowedPredicate("after_inheritedmethod", "ctt");
    addAllowedPredicate("after_fieldoftype", "ft");
    addAllowedPredicate("after_typeintype", "tt");
    
    addAllowedPredicate("after_extends", "tt");
    addAllowedPredicate("after_implements", "tt");
    
    addAllowedPredicate("deleted_package", "p");
    addAllowedPredicate("deleted_type", "tap");
    addAllowedPredicate("deleted_field", "fbt");
    addAllowedPredicate("deleted_method", "mct");
    
    addAllowedPredicate("deleted_return", "mt");
    addAllowedPredicate("deleted_subtype", "tt");
    addAllowedPredicate("deleted_accesses", "fm");
    addAllowedPredicate("deleted_calls", "mm");
    addAllowedPredicate("deleted_inheritedfield", "btt");
    addAllowedPredicate("deleted_inheritedmethod", "ctt");
    addAllowedPredicate("deleted_fieldoftype", "ft");
    addAllowedPredicate("deleted_typeintype", "tt");
    
    addAllowedPredicate("deleted_extends", "tt");
    addAllowedPredicate("deleted_implements", "tt");
    
    addAllowedPredicate("added_package", "p");
    addAllowedPredicate("added_type", "tap");
    addAllowedPredicate("added_field", "fbt");
    addAllowedPredicate("added_method", "mct");
    
    addAllowedPredicate("added_return", "mt");
    addAllowedPredicate("added_subtype", "tt");
    addAllowedPredicate("added_accesses", "fm");
    addAllowedPredicate("added_calls", "mm");
    addAllowedPredicate("added_inheritedfield", "btt");
    addAllowedPredicate("added_inheritedmethod", "ctt");
    addAllowedPredicate("added_fieldoftype", "ft");
    addAllowedPredicate("added_typeintype", "tt");
    
    addAllowedPredicate("added_extends", "tt");
    addAllowedPredicate("added_implements", "tt");
    
    addAllowedPredicate("modified_package", "p");
    addAllowedPredicate("modified_type", "tap");
    addAllowedPredicate("modified_method", "mct");
    addAllowedPredicate("modified_field", "fbt");
    
    addAllowedPredicate("before_conditional", "hiet");
    addAllowedPredicate("after_conditional", "hiet");
    addAllowedPredicate("added_conditional", "hiet");
    addAllowedPredicate("deleted_conditional", "hiet");
    addAllowedPredicate("modified_conditional", "hiet");
    addAllowedPredicate("before_methodbody", "ti");
    addAllowedPredicate("after_methodbody", "ti");
    addAllowedPredicate("added_methodbody", "ti");
    addAllowedPredicate("deleted_methodbody", "ti");
    addAllowedPredicate("before_parameter", "cmi");
    addAllowedPredicate("after_parameter", "cmi");
    addAllowedPredicate("added_parameter", "cmi");
    addAllowedPredicate("deleted_parameter", "cmi");
    addAllowedPredicate("before_methodmodifier", "mi");
    addAllowedPredicate("after_methodmodifier", "mi");
    addAllowedPredicate("added_methodmodifier", "mi");
    addAllowedPredicate("deleted_methodmodifier", "mi");
    addAllowedPredicate("before_fieldmodifier", "mi");
    addAllowedPredicate("after_fieldmodifier", "mi");
    addAllowedPredicate("added_fieldmodifier", "mi");
    addAllowedPredicate("deleted_fieldmodifier", "mi");
    
    addAllowedPredicate("before_cast", "etm");
    addAllowedPredicate("after_cast", "etm");
    addAllowedPredicate("added_cast", "etm");
    addAllowedPredicate("deleted_cast", "etm");
    addAllowedPredicate("before_trycatch", "abcm");
    addAllowedPredicate("after_trycatch", "abcm");
    addAllowedPredicate("added_trycatch", "abcm");
    addAllowedPredicate("deleted_trycatch", "abcm");
    addAllowedPredicate("before_throws", "mt");
    addAllowedPredicate("after_throws", "mt");
    addAllowedPredicate("added_throws", "mt");
    addAllowedPredicate("deleted_throws", "mt");
    addAllowedPredicate("before_getter", "mf");
    addAllowedPredicate("after_getter", "mf");
    addAllowedPredicate("added_getter", "mf");
    addAllowedPredicate("deleted_getter", "mf");
    addAllowedPredicate("before_setter", "mf");
    addAllowedPredicate("after_setter", "mf");
    addAllowedPredicate("added_setter", "mf");
    addAllowedPredicate("deleted_setter", "mf");
    
    addAllowedPredicate("before_localvar", "mtab");
    addAllowedPredicate("after_localvar", "mtab");
    addAllowedPredicate("added_localvar", "mtab");
    addAllowedPredicate("deleted_localvar", "mtab");
  }
  
  private int kind = UNDEFINED;
  private final String predName;
  private final char[] types;
  private int level;
  
  public int arity()
  {
    return this.types.length;
  }
  
  private LSDPredicate(String pred, char[] types)
    throws LSDInvalidTypeException
  {
    this.types = types;
    for (int i = 0; i < types.length; i++) {
      if (!LSDVariable.isValidType(types[i])) {
        throw new LSDInvalidTypeException();
      }
    }
    this.predName = pred;
    if (pred.indexOf("before_") != -1) {
      this.kind = BEFORE;
    } else if (pred.indexOf("after_") != -1) {
      this.kind = AFTER;
    } else if (pred.indexOf("deleted_") != -1) {
      this.kind = DELETED;
    } else if (pred.indexOf("added_") != -1) {
      this.kind = ADDED;
    } else if (pred.indexOf("modified_") != -1) {
      this.kind = MODIFIED;
    } else {
      this.kind = UNDEFINED;
    }
    this.level = setLevel(pred, types);
  }
  
  private int setLevel(String pred, char[] types)
  {
    if (types[0] == 'p') {
      return PACKAGELEVEL;
    }
    if (types[0] == 't') {
      return CLASSLEVEL;
    }
    return METHODLEVEL;
  }
  
  public boolean isElement()
  {
    return arity() == 1;
  }
  
  public String getName()
  {
    return this.predName;
  }
  
  public String getDisplayName()
  {
    return is_pPredicate() ? this.predName.replaceFirst("_p_", "_") : this.predName;
  }
  
  public char[] getTypes()
  {
    return this.types;
  }
  
  public String toString()
  {
    StringBuilder typeString = new StringBuilder();
    for (int i = 0; i < this.types.length; i++)
    {
      if (i >= 1) {
        typeString.append(",");
      }
      typeString.append(this.types[i]);
    }
    return this.predName + "(" + typeString + ")";
  }
  
  private static void addAllowedPredicate(String predName, String types)
  {
    try
    {
      allowedPredicate.put(predName, new LSDPredicate(predName, types.toCharArray()));
    }
    catch (LSDInvalidTypeException e)
    {
      e.printStackTrace();
    }
  }
  
  public static LSDPredicate getPredicate(String predName)
  {
    return (LSDPredicate)allowedPredicate.get(predName);
  }
  
  public LSDPredicate getPrefixPredicate(String prefix)
  {
    return getPredicate(prefix + "_" + getSuffix());
  }
  
  public static List<LSDPredicate> getPredicates()
  {
    List<LSDPredicate> predicates = new ArrayList();
    for (LSDPredicate predicate : allowedPredicate.values()) {
      if (predicate.kind != UNDEFINED) {
        predicates.add(predicate);
      }
    }
    return predicates;
  }
  
  public boolean isKBBeforePredicate()
  {
    return (this.kind == DELETED) || (this.kind == BEFORE) || (this.kind == DELETED_P);
  }
  
  public boolean isKBAfterPredicate()
  {
    return (this.kind == ADDED) || (this.kind == AFTER) || (this.kind == ADDED_P);
  }
  
  public boolean isConclusionPredicate()
  {
    return (this.kind == DELETED) || (this.kind == ADDED) || (this.kind == MODIFIED);
  }
  
  public boolean is2KBPredicate()
  {
    return (this.kind == BEFORE) || (this.kind == AFTER) || (is_pPredicate());
  }
  
  public boolean isAntecedentPredicate()
  {
    return this.kind == BEFORE;
  }
  
  public boolean is_pPredicate()
  {
    return (this.kind == ADDED_P) || (this.kind == DELETED_P) || (this.kind == MODIFIED_P);
  }
  
  public boolean allowedInSameRule(LSDPredicate conclusion, LSDPredicate antecedant)
  {
    if (antecedant != null)
    {
      boolean x = this.kind == antecedant.kind;
      return x;
    }
    if (conclusion.kind == DELETED) {
      return this.kind == BEFORE;
    }
    if (conclusion.kind == ADDED) {
      return (this.kind == DELETED) || (this.kind == BEFORE) || (this.kind == AFTER);
    }
    if (conclusion.kind == MODIFIED) {
      return this.kind == BEFORE;
    }
    return false;
  }
  
  public boolean typeChecks(ArrayList<LSDBinding> bindings)
  {
    if (bindings.size() != this.types.length) {
      return false;
    }
    for (int i = 0; i < bindings.size(); i++)
    {
      char type = this.types[i];
      LSDBinding binding = (LSDBinding)bindings.get(i);
      if (!binding.typeChecks(type)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean typeMatches(Collection<Character> types)
  {
    char[] arrayOfChar;
    int j = (arrayOfChar = this.types).length;
    for (int i = 0; i < j; i++)
    {
      char type = arrayOfChar[i];
      if (types.contains(Character.valueOf(type))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean equalsIgnoringPrimes(Object other)
  {
    if (!(other instanceof LSDPredicate)) {
      return false;
    }
    return getDisplayName().equals(((LSDPredicate)other).getDisplayName());
  }
  
  public boolean equals(Object other)
  {
    if (!(other instanceof LSDPredicate)) {
      return false;
    }
    return toString().equals(((LSDPredicate)other).toString());
  }
  
  public static void main(String[] args)
  {
    LSDPredicate foo = getPredicate("deleted_field");
    assert (foo.getName() == "deleted_field");
    assert (foo.arity() == 1);
    assert (getPredicate("added_bogusMethod") == null);
    foo = getPredicate("added_inheritedmethod");
    assert (foo.getName() == "added_inheritedmethod");
    assert (foo.arity() == 3);
    ArrayList<LSDBinding> bindings = new ArrayList();
    LSDBinding binding = new LSDBinding(new LSDVariable("a", 'm'));
    bindings.add(binding);
    binding = new LSDBinding(new LSDVariable("c", 't'));
    bindings.add(binding);
    assert (!foo.typeChecks(bindings));
    binding = new LSDBinding(new LSDVariable("b", 't'));
    bindings.add(binding);
    assert (foo.typeChecks(bindings));
    bindings = new ArrayList();
    binding = new LSDBinding(new LSDVariable("a", 'f'));
    bindings.add(binding);
    binding = new LSDBinding(new LSDVariable("c", 't'));
    bindings.add(binding);
    binding = new LSDBinding(new LSDVariable("b", 't'));
    bindings.add(binding);
    assert (!foo.typeChecks(bindings));
    assert (foo.toString().equals("added_inheritedmethod(m,t,t)"));
    System.out.println("Predicate tests succeeded.");
  }
  
  public static ArrayList<LSDPredicate> getPredicates(int kind, int arity)
  {
    Collection<LSDPredicate> predicates = allowedPredicate.values();
    ArrayList<LSDPredicate> results = new ArrayList();
    for (LSDPredicate pred : predicates) {
      if ((pred.kind == kind) && (pred.arity() == arity)) {
        results.add(pred);
      }
    }
    if (results.size() == 0) {
      return null;
    }
    return results;
  }
  
  public static ArrayList<LSDPredicate> getPredicates(int kind, char type)
  {
    Collection<LSDPredicate> predicates = allowedPredicate.values();
    ArrayList<LSDPredicate> results = new ArrayList();
    for (LSDPredicate pred : predicates)
    {
      boolean includeType = false;
      for (int i = 0; i < pred.types.length; i++) {
        if (pred.types[i] == type) {
          includeType = true;
        }
      }
      if ((pred.kind == kind) && (includeType)) {
        results.add(pred);
      }
    }
    if (results.size() == 0) {
      return null;
    }
    return results;
  }
  
  public char[] getPrimaryTypes()
  {
    String name = getName();
    if (name.indexOf("_") > 0) {
      name = name.substring(name.indexOf("_") + 1);
    }
    if (name.equals("type")) {
      return "t".toCharArray();
    }
    if (name.equals("dependency")) {
      return "t".toCharArray();
    }
    if (name.equals("field")) {
      return "f".toCharArray();
    }
    if (name.equals("method")) {
      return "m".toCharArray();
    }
    if (name.equals("typeintype")) {
      return "t".toCharArray();
    }
    if (name.equals("inheritedmethod")) {
      return "mt".toCharArray();
    }
    if (name.equals("inheritedfield")) {
      return "ft".toCharArray();
    }
    return getTypes();
  }
  
  protected String getSuffix()
  {
    String name = getName();
    if (name.lastIndexOf("_") > 0) {
      name = name.substring(name.lastIndexOf("_") + 1);
    }
    return name;
  }
  
  protected String getPrefix()
  {
    String name = getName();
    if (name.lastIndexOf("_") > 0) {
      name = name.substring(0, name.lastIndexOf("_"));
    }
    return name;
  }
  
  public int[][] getPrimaryArguments()
  {
    String name = getSuffix();
    if (name.equals("type"))
    {
      int[][] s = { new int[1] };
      return s;
    }
    if (name.equals("field"))
    {
      int[][] s = { new int[1], { 1, 2 } };
      return s;
    }
    if (name.equals("method"))
    {
      int[][] s = { new int[1], { 1, 2 } };
      return s;
    }
    if (name.equals("typeintype"))
    {
      int[][] s = { new int[1] };
      return s;
    }
    int[] s = new int[getTypes().length];
    for (int i = 0; i < s.length; i++) {
      s[i] = i;
    }
    int[][] ss = { s };
    return ss;
  }
  
  public int getReferenceArgument()
  {
    String name = getSuffix();
    if (name.equals("subtype")) {
      return 1;
    }
    if (name.equals("accesses")) {
      return 1;
    }
    if (name.equals("inheritedfield")) {
      return 2;
    }
    if (name.equals("inheritedmethod")) {
      return 2;
    }
    return 0;
  }
  
  public boolean isMethodLevel()
  {
    if (this.level == METHODLEVEL) {
      return true;
    }
    return false;
  }
  
  public String getConvertedArgs(String arg)
  {
    StringTokenizer tokenizer = new StringTokenizer(arg, ",", false);
    if (tokenizer.countTokens() != 2)
    {
      while (arg.contains("("))
      {
        String temp = arg.substring(0, arg.indexOf("("));
        arg = arg.substring(arg.indexOf("(") + 1);
        arg = temp + arg.substring(arg.indexOf(")") + 1);
      }
      while (arg.contains("<"))
      {
        String temp = arg.substring(0, arg.indexOf("<"));
        arg = arg.substring(arg.indexOf("<") + 1);
        arg = temp + arg.substring(arg.indexOf(">") + 1);
      }
    }
    tokenizer = new StringTokenizer(arg, ",", false);
    if (isConclusionPredicate())
    {
      String arg0 = tokenizer.nextToken();
      if ((getSuffix().equalsIgnoreCase("typeintype")) || (getSuffix().equalsIgnoreCase("accesses"))) {
        arg0 = tokenizer.nextToken();
      }
      if (getSuffix().contains("inherited"))
      {
        arg0 = tokenizer.nextToken();
        arg0 = tokenizer.nextToken();
      }
      if (arg0.indexOf("#") != -1) {
        arg0 = arg0.substring(1, arg0.indexOf("#"));
      } else if (arg0.indexOf("\"") != -1) {
        arg0 = arg0.substring(1, arg0.lastIndexOf("\""));
      }
      String arg1 = arg0.substring(arg0.indexOf("%.") + 2);
      String arg2;
      String arg2;
      if (arg0.indexOf("%") == 0) {
        arg2 = "null";
      } else {
        arg2 = arg0.substring(0, arg0.indexOf("%."));
      }
      if ((arg2 == null) || (arg2.length() == 0)) {
        arg2 = "null";
      }
      arg0 = "\"" + arg0 + "\",\"" + arg1 + "\",\"" + arg2 + "\"";
      return arg0;
    }
    String arg0 = tokenizer.nextToken();
    String arg1 = tokenizer.nextToken();
    if (getSuffix().equalsIgnoreCase("accesses"))
    {
      String temp = arg1;
      arg1 = arg0;
      arg0 = temp;
    }
    if (arg0.indexOf("#") != -1) {
      arg0 = arg0.substring(1, arg0.indexOf("#"));
    } else if (arg0.indexOf("\"") != -1) {
      arg0 = arg0.substring(1, arg0.lastIndexOf("\""));
    }
    if (arg1.indexOf("#") != -1) {
      arg1 = arg1.substring(1, arg1.indexOf("#"));
    } else if (arg1.indexOf("\"") != -1) {
      arg1 = arg1.substring(1, arg1.lastIndexOf("\""));
    }
    arg0 = "\"" + arg0 + "\",\"" + arg1 + "\"";
    return arg0;
  }
  
  public LSDPredicate toClassLevel()
  {
    if (isConclusionPredicate()) {
      return getPredicate("changed_type");
    }
    if ((this.predName.contains("accesses")) || (this.predName.contains("calls")))
    {
      String newPred = this.predName.substring(0, this.predName.indexOf('_')) + "_dependency";
      return getPredicate(newPred);
    }
    return this;
  }
  
  public boolean isDependencyPredicate()
  {
    if ((this.predName.contains("accesses")) || (this.predName.contains("calls"))) {
      return true;
    }
    return false;
  }
  
  public boolean isCompatibleMethodLevel()
  {
    if (getSuffix().equalsIgnoreCase("dependency")) {
      return false;
    }
    return true;
  }
  
  public ArrayList<LSDPredicate> getMethodLevelDependency()
  {
    String prefix = getPrefix();
    ArrayList<LSDPredicate> preds = new ArrayList();
    preds.add(getPredicate(prefix + "_calls"));
    preds.add(getPredicate(prefix + "_accesses"));
    return preds;
  }
  
  public void updateBindings(ArrayList<LSDBinding> bindings)
  {
    if (getSuffix() == "accesses")
    {
      ArrayList<LSDBinding> temp = new ArrayList();
      temp.add((LSDBinding)bindings.get(1));
      temp.add((LSDBinding)bindings.get(0));
      bindings = temp;
    }
  }
  
  public static String combineArguments(String arg0, String arg1, String arg2)
  {
    return "\"" + arg0 + "\", \"" + arg1 + "\" ,\"" + arg2 + "\"";
  }
}
