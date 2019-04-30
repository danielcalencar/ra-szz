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
package lsdSimplified;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;
import lsd.rule.LSDPredicate;
import metapackage.MetaInfo;

public class Converter
{
  static String tempRes = MetaInfo.srcDir + "temp.rub";
  
  public static File convertDeltaFacts(File src)
  {
    File res = new File(tempRes);
    String line = null;
    BufferedWriter writer = null;
    try
    {
      BufferedReader input = new BufferedReader(new FileReader(src));
      writer = new BufferedWriter(new FileWriter(res));
      while ((line = input.readLine()) != null) {
        if ((line.contains("//")) || (line.contains("include")) || (line.length() == 0))
        {
          writer.write(line);
          writer.newLine();
        }
        else
        {
          String pred = line.substring(0, line.indexOf("("));
          String arg = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")"));
          if (shouldChange(pred))
          {
            writer.write(changeArguments(pred, arg));
            writer.newLine();
          }
          else
          {
            writer.write(line);
            writer.newLine();
          }
        }
      }
      writer.close();
      return res;
    }
    catch (Exception localException)
    {
      System.err.println("error" + line);
      try
      {
        writer.close();
      }
      catch (IOException e1)
      {
        e1.printStackTrace();
      }
    }
    return null;
  }
  
  private static boolean shouldChange(String predicateName)
  {
    LSDPredicate predicate = LSDPredicate.getPredicate(predicateName);
    if (predicate.isMethodLevel()) {
      return true;
    }
    return false;
  }
  
  public static String changeArguments(String predicateName, String arguments)
  {
    StringTokenizer tokenizer = new StringTokenizer(arguments, ",", false);
    String arg0 = tokenizer.nextToken();
    try
    {
      if ((predicateName.contains("typeintype")) || (predicateName.contains("accesses"))) {
        arg0 = tokenizer.nextToken();
      }
      if (predicateName.contains("inherited"))
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
      return "changed_type(\"" + arg0 + "\",\"" + arg1 + "\",\"" + arg2 + "\").";
    }
    catch (Exception localException)
    {
      arg0 = predicateName + "(\"" + arguments + ").";
      System.out.println(arg0);
    }
    return arg0;
  }
  
  public static void clear()
  {
    File res = new File(tempRes);
    
    res.delete();
  }
}
