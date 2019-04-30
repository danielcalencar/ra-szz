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
import java.util.List;

public class Node
{
  private List<Node> children;
  private boolean visited;
  private RefactoringQuery refQry;
  private int numFound_;
  
  public Node()
  {
    this.children = new ArrayList();
    this.visited = false;
    this.refQry = null;
    this.numFound_ = 0;
  }
  
  public Node(RefactoringQuery refQry)
  {
    this.children = new ArrayList();
    this.visited = false;
    this.refQry = refQry;
    this.numFound_ = 0;
  }
  
  public Node(List<Node> children, boolean visited, RefactoringQuery refQry)
  {
    this.children = children;
    this.visited = visited;
    this.refQry = refQry;
  }
  
  public void incrementNumFound()
  {
    this.numFound_ += 1;
  }
  
  public int numFound()
  {
    return this.numFound_;
  }
  
  public String toString()
  {
    return this.refQry.getName() + "(" + this.numFound_ + ")";
  }
  
  public List<Node> getChildren()
  {
    return this.children;
  }
  
  public void addChild(Node child)
  {
    this.children.add(child);
  }
  
  public void setChildren(List<Node> children)
  {
    this.children = children;
  }
  
  public boolean isVisited()
  {
    return this.visited;
  }
  
  public void setVisited(boolean visited)
  {
    this.visited = visited;
  }
  
  public RefactoringQuery getRefQry()
  {
    return this.refQry;
  }
  
  public void setRefQry(RefactoringQuery refQry)
  {
    this.refQry = refQry;
  }
}
