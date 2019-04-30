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
package tyRuBa.engine;

import java.io.Serializable;

public abstract class Identifier
  implements Serializable
{
  protected String name;
  protected int arity;
  
  public Identifier(String name, int arity)
  {
    this.name = name;
    this.arity = arity;
  }
  
  public boolean equals(Object arg)
  {
    if (arg.getClass().equals(getClass()))
    {
      Identifier other = (Identifier)arg;
      return (this.name.equals(other.name)) && (this.arity == other.arity);
    }
    return false;
  }
  
  public int hashCode()
  {
    return getClass().hashCode() * this.arity + this.name.hashCode();
  }
  
  public String toString()
  {
    return this.name + "/" + this.arity;
  }
  
  public int getArity()
  {
    return this.arity;
  }
  
  public String getName()
  {
    return this.name;
  }
}
