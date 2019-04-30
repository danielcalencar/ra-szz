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
package lsd.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import lsd.rule.LSDBinding;
import lsd.rule.LSDInvalidTypeException;
import lsd.rule.LSDLiteral;
import lsd.rule.LSDPredicate;
import lsd.rule.LSDRule;
import lsd.rule.LSDVariable;

public class LSDAlchemyRuleReader
{
  public static void main(String[] args)
  {
    LSDRule r = parseAlchemyRule("before_type(y) ^ before_fieldtype(y, X) => before_fieldtype(y, \"z\")");
    if (r != null)
    {
      System.out.println("LSDRule\n" + r.toString());
      System.out.println("Tyruba Query\n" + r.toTyrubaQuery(true));
    }
    r = parseAlchemyRule("before_type(x) ^ before_fieldtype( \"foo()\", x) => before_fieldtype(y, x)");
    if (r != null)
    {
      System.out.println("LSDRule\n" + r.toString());
      System.out.println("Tyruba Query\n" + r.toTyrubaQuery(true));
    }
    System.out.println("Parser tests completed.");
  }
  
  private ArrayList<LSDRule> rules = null;
  
  public LSDAlchemyRuleReader(File inputFile)
  {
    ArrayList<LSDRule> rs = new ArrayList();
    try
    {
      if (inputFile.exists())
      {
        BufferedReader in = new BufferedReader(
          new FileReader(inputFile));
        String line = null;
        while ((line = in.readLine()) != null) {
          if ((!line.trim().equals("")) && (line.trim().charAt(0) != '#'))
          {
            LSDRule rule = parseAlchemyRule(line);
            rs.add(rule);
          }
        }
        in.close();
      }
      this.rules = rs;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public ArrayList<LSDRule> getRules()
  {
    return this.rules;
  }
  
  static int quoteCount(String s)
  {
    int quoteCount = 0;
    char[] arrayOfChar;
    int j = (arrayOfChar = s.toCharArray()).length;
    for (int i = 0; i < j; i++)
    {
      char c = arrayOfChar[i];
      if (c == '"') {
        quoteCount++;
      }
    }
    return quoteCount;
  }
  
  public static LSDRule parseAlchemyRule(String line)
  {
    line = line.replace("?", "");
    if (line.lastIndexOf("\t") > 0) {
      line = line.substring(0, line.lastIndexOf("\t"));
    }
    LSDRule rule = new LSDRule();
    
    String ruleString = line.substring(line.indexOf('\t') + 1).trim();
    while (!ruleString.equals(""))
    {
      boolean negated = false;
      if (ruleString.charAt(0) == '!')
      {
        negated = true;
        ruleString = ruleString.substring(1);
      }
      String predicateName = ruleString.substring(0, ruleString.indexOf('(')).trim();
      ruleString = ruleString.substring(ruleString.indexOf('(') + 1).trim();
      int endOfArgs = ruleString.indexOf(')');
      int firstQuote = ruleString.indexOf('"');
      int secondQuote = ruleString.indexOf('"', firstQuote + 1);
      if ((secondQuote == -1) && (firstQuote != -1))
      {
        System.err.println("Mismatched quotes in the rule");
        System.err.println("Line: " + line);
        System.exit(-1);
      }
      while (quoteCount(ruleString.substring(0, endOfArgs)) % 2 != 0)
      {
        endOfArgs = ruleString.indexOf(')', endOfArgs + 1);
        assert (endOfArgs != -1);
      }
      String arguments = ruleString.substring(0, endOfArgs).trim();
      ruleString = ruleString.substring(endOfArgs + 1).trim();
      if (!ruleString.equals("")) {
        if (ruleString.charAt(0) == 'v')
        {
          ruleString = ruleString.substring(1).trim();
        }
        else if (ruleString.charAt(0) == '^')
        {
          ruleString = ruleString.substring(1).trim();
          negated = !negated;
        }
        else if (ruleString.charAt(0) == '=')
        {
          assert (ruleString.charAt(1) == '>');
          ruleString = ruleString.substring(2).trim();
          negated = !negated;
        }
        else
        {
          System.err.println("Rule ill defined...");
          System.err.println("Line: " + line);
          System.err.println("Remaining: " + ruleString);
          System.exit(-1);
        }
      }
      LSDPredicate predicate = LSDPredicate.getPredicate(predicateName);
      if (predicate == null)
      {
        System.err.println("Predicate " + predicateName + " is not defined.");
        System.err.println("Line: " + line);
        System.exit(-1);
      }
      ArrayList<LSDBinding> bindings = new ArrayList();
      char[] types = predicate.getTypes();
      for (int i = 0; i < types.length; i++) {
        if (arguments.charAt(0) == '"')
        {
          String constant = arguments.substring(0, arguments.indexOf('"', 1) + 1);
          arguments = arguments.substring(arguments.indexOf('"', 1) + 1).trim();
          if (i != types.length - 1)
          {
            assert (arguments.charAt(0) == ',');
            arguments = arguments.substring(1).trim();
          }
          bindings.add(new LSDBinding(constant));
        }
        else if (Character.isUpperCase(arguments.charAt(0)))
        {
          String constant = "";
          if (i != types.length - 1)
          {
            assert (arguments.contains(","));
            constant = arguments.substring(0, arguments.indexOf(',', 1));
            arguments = arguments.substring(arguments.indexOf(',') + 1).trim();
          }
          else
          {
            assert (!arguments.contains(","));
            constant = arguments;
          }
          bindings.add(new LSDBinding(constant));
        }
        else
        {
          String varName = "";
          if (i != types.length - 1)
          {
            assert (arguments.contains(","));
            varName = arguments.substring(0, arguments.indexOf(',', 1));
            arguments = arguments.substring(arguments.indexOf(',') + 1).trim();
          }
          else
          {
            if (arguments.contains(","))
            {
              System.err.println("Error: we think '" + arguments + "' shouldn't contain a comma.");
              System.err.println("Line: " + line);
              System.exit(-1);
            }
            varName = arguments;
          }
          bindings.add(new LSDBinding(new LSDVariable(varName, types[i])));
        }
      }
      try
      {
        boolean success = rule.addLiteral(new LSDLiteral(predicate, bindings, !negated));
        if (!success)
        {
          System.err.println("Error, rules cannot contain facts.");
          System.err.println("Line: " + line);
          System.exit(-1);
        }
      }
      catch (LSDInvalidTypeException e)
      {
        e.printStackTrace();
        System.err.println("Line: " + line);
        System.exit(-1);
      }
    }
    if (!rule.isValid())
    {
      System.err.println("Rule skipped because it's not valid: isHornClause " + 
        rule.isHornClause() + 
        "\tdoesTypeChecks " + 
        rule.typeChecks() + 
        "\tMight also not be properly interrelated.");
      
      System.err.println("Rule parsed as: " + rule);
      System.err.println("Line: " + line);
      return null;
    }
    return rule;
  }
}
