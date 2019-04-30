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

import java.util.ArrayList;

public class RefactoringQuery
{
  private String name;
  private String query;
  private ArrayList<String> types;
  
  public RefactoringQuery(String name, String query)
  {
    this.name = name;
    this.query = query;
    this.types = new ArrayList();
  }
  
  public ArrayList<String> getTypes()
  {
    return this.types;
  }
  
  public void setTypes(ArrayList<String> types)
  {
    this.types = types;
  }
  
  public void addType(String type)
  {
    this.types.add(type);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getQuery()
  {
    return this.query;
  }
  
  public void setQuery(String query)
  {
    this.query = query;
  }
}
