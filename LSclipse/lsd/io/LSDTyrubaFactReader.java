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
import java.util.List;
import lsd.rule.LSDFact;
import lsd.rule.LSDPredicate;

public class LSDTyrubaFactReader
{
  private ArrayList<LSDFact> facts = null;
  
  public LSDTyrubaFactReader(File inputFile)
  {
    ArrayList<LSDFact> fs = new ArrayList();
    try
    {
      if (inputFile.exists())
      {
        BufferedReader in = new BufferedReader(
          new FileReader(inputFile));
        String line = null;
        while ((line = in.readLine()) != null) {
          if ((!line.trim().equals("")) && (line.trim().charAt(0) != '#') && 
            (!line.trim().startsWith("//")))
          {
            LSDFact fact = parseTyrubaFact(line);
            fs.add(fact);
          }
        }
        in.close();
      }
      this.facts = fs;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static ArrayList<LSDFact> convertToClassLevel(ArrayList<LSDFact> readDeltaFacts)
  {
    ArrayList<LSDFact> facts = new ArrayList();
    for (LSDFact fact : readDeltaFacts) {
      if (fact.getPredicate().isMethodLevel())
      {
        LSDFact tempFact = fact.convertToClassLevel();
        if (tempFact != null) {
          facts.add(tempFact);
        }
      }
      else
      {
        facts.add(fact);
      }
    }
    return facts;
  }
  
  public ArrayList<LSDFact> getFacts()
  {
    return this.facts;
  }
  
  public static LSDFact parseTyrubaFact(String line)
  {
    String factString = line.trim();
    
    String predicateName = factString.substring(0, factString.indexOf('('))
      .trim();
    LSDPredicate predicate = LSDPredicate.getPredicate(predicateName);
    factString = factString.substring(factString.indexOf('(') + 1).trim();
    int endOfArgs = factString.lastIndexOf(')');
    String arguments = factString.substring(0, endOfArgs).trim();
    factString = factString.substring(endOfArgs + 1).trim();
    if (!factString.equals("."))
    {
      System.err.println("Facts must be in the form 'predicate(const, const, ...).'");
      System.err.println("Line: " + line);
      System.exit(-3);
    }
    if (predicate == null)
    {
      System.err.println("Predicate " + predicateName + 
        " is not defined.");
      System.err.println("Line: " + line);
      System.exit(-1);
    }
    String[] params = arguments.split("\", \"");
    List<String> binds = new ArrayList();
    String[] arrayOfString1;
    int j = (arrayOfString1 = params).length;
    for (int i = 0; i < j; i++)
    {
      String p = arrayOfString1[i];
      if (p.startsWith("\"")) {
        binds.add(p.substring(1));
      } else if (p.endsWith("\"")) {
        binds.add(p.substring(0, p.length() - 2));
      } else {
        binds.add(p);
      }
    }
    return LSDFact.createLSDFact(predicate, binds, true);
  }
}
