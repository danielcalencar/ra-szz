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
package lsclipse.utils;

import lsclipse.LCS;

public class CodeCompare
{
  public static final double SIMILARITY_THRESHOLD = 0.85D;
  public static final double DIFFERNCE_THRESHOLD = 0.75D;
  
  public static boolean compare(String left, String right)
  {
    String shorter = getShorterString(left, right);
    String lcs = LCS.getLCS(left, right);
    double similarity = lcs.length() / shorter.length();
    if (similarity >= 0.85D) {
      return true;
    }
    return false;
  }
  
  public static boolean contrast(String left, String right)
  {
    String longer = getLongerString(left, right);
    String lcs = LCS.getLCS(left, right);
    double similarity = lcs.length() / longer.length();
    if (similarity <= 0.75D) {
      return true;
    }
    return false;
  }
  
  private static String getShorterString(String left, String right)
  {
    if (left.length() < right.length()) {
      return left;
    }
    return right;
  }
  
  private static String getLongerString(String left, String right)
  {
    if (left.length() > right.length()) {
      return left;
    }
    return right;
  }
}
