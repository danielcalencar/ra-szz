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
package lsclipse;

import java.io.PrintStream;

public class LCS
{
  public static String getLCS(String x, String y)
  {
    int M = x.length();
    int N = y.length();
    
    int[][] opt = new int[M + 1][N + 1];
    for (int i = M - 1; i >= 0; i--) {
      for (int j = N - 1; j >= 0; j--) {
        if (x.charAt(i) == y.charAt(j)) {
          opt[i][j] = (opt[(i + 1)][(j + 1)] + 1);
        } else {
          opt[i][j] = Math.max(opt[(i + 1)][j], opt[i][(j + 1)]);
        }
      }
    }
    String output = "";
    int i = 0;int j = 0;
    while ((i < M) && (j < N)) {
      if (x.charAt(i) == y.charAt(j))
      {
        output = output + x.charAt(i);
        i++;
        j++;
      }
      else if (opt[(i + 1)][j] >= opt[i][(j + 1)])
      {
        i++;
      }
      else
      {
        j++;
      }
    }
    return output;
  }
  
  public static void allSubSequences(String x, String y) {}
  
  public static void main(String[] args)
  {
    String y = "int output=super.getSpeed() + 200;   return output; } ";
    String x = "";
    System.out.println("The output is: " + getLCS(x, y));
  }
}
