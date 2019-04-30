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
package tyRuBa.applications;

class Group
  implements Comparable
{
  public String id;
  public Integer order;
  public String label;
  public String description;
  
  public Group(String id, Integer order, String label, String description)
  {
    this.id = id;
    this.order = order;
    this.label = label;
    this.description = description;
  }
  
  public boolean equals(Object obj)
  {
    if ((obj == null) || (!(obj instanceof Group))) {
      return false;
    }
    Group o = (Group)obj;
    
    return (this.id.equals(o.id)) && (this.order.equals(o.order)) && (this.label.equals(o.label)) && (this.description.equals(o.description));
  }
  
  public int hashCode()
  {
    return this.id.hashCode();
  }
  
  public int compareTo(Object o)
  {
    Group g = (Group)o;
    
    int i = this.order.compareTo(g.order);
    if (i != 0) {
      return i;
    }
    i = this.label.compareTo(g.label);
    if (i != 0) {
      return i;
    }
    i = this.id.compareTo(g.id);
    if (i != 0) {
      return i;
    }
    i = this.description.compareTo(g.description);
    return i;
  }
}
