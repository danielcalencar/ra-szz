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

public class LSDVariable
{
  private char type;
  private String variableName;
  
  public static boolean isValidType(char type)
  {
    return "hieptmfabc".contains(Character.toString(type));
  }
  
  public LSDVariable(String variableName, char type)
  {
    this.variableName = variableName;
    this.type = type;
  }
  
  public String getName()
  {
    return this.variableName;
  }
  
  public boolean typeChecks(char type)
  {
    return this.type == type;
  }
  
  public boolean typeChecks(LSDVariable match)
  {
    return this.type == match.type;
  }
  
  public boolean typeConflicts(LSDVariable toBeMatched)
  {
    return (this.variableName.equals(toBeMatched.variableName)) && (!typeChecks(toBeMatched));
  }
  
  public String toString()
  {
    return "?" + this.variableName;
  }
  
  public boolean equals(LSDVariable other)
  {
    return (this.variableName.equals(other.variableName)) && (this.type == other.type);
  }
  
  public boolean equals(Object other)
  {
    if ((other instanceof LSDVariable)) {
      return equals((LSDVariable)other);
    }
    return false;
  }
  
  public int hashCode()
  {
    String identity = this.variableName + this.type;
    return identity.hashCode();
  }
  
  public int compareTo(Object o)
  {
    return o.hashCode() - hashCode();
  }
  
  public static void main(String[] args) {}
  
  public char getType()
  {
    return this.type;
  }
}
