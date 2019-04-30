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
package lsclipse.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.eclipse.jdt.core.IJavaElement;

public class Node
{
  private String nodeName;
  private Vector<Node> children;
  private Node parent;
  private String message;
  private String fileName;
  private String projectName;
  private String baseProjectName;
  private String newProjectName;
  private String basePath;
  private String newPath;
  private String basePackageName;
  private String newPackageName;
  private String refactoring;
  private boolean isParent;
  private List<String> dependents_;
  public String params;
  public Map<String, IJavaElement> oldFacts = new HashMap();
  public Map<String, IJavaElement> newFacts = new HashMap();
  
  public Node(String name, Node p)
  {
    this.children = new Vector();
    this.nodeName = name;
    this.isParent = false;
    this.basePackageName = "";
    this.newPackageName = "";
    if (p != null) {
      p.addChild(this);
    }
  }
  
  public void setDependents(List<String> dependents_)
  {
    this.dependents_ = dependents_;
  }
  
  public List<String> getDependents()
  {
    return this.dependents_;
  }
  
  public void setParentStatus(boolean item)
  {
    this.isParent = true;
  }
  
  public boolean isParent()
  {
    return this.isParent;
  }
  
  public void setProjectName(String item)
  {
    this.projectName = item;
  }
  
  public String getProjectName()
  {
    return this.projectName;
  }
  
  public void setBasePackageName(String item)
  {
    this.basePackageName = item;
  }
  
  public String getBasePackageName()
  {
    return this.basePackageName;
  }
  
  public void setRefactoring(String item)
  {
    this.refactoring = item;
  }
  
  public String getRefactoring()
  {
    return this.refactoring;
  }
  
  public void setNewPackageName(String item)
  {
    this.newPackageName = item;
  }
  
  public String getNewPackageName()
  {
    return this.newPackageName;
  }
  
  public void setBaseProjectName(String item)
  {
    this.baseProjectName = item;
  }
  
  public String getBaseProjectName()
  {
    return this.baseProjectName;
  }
  
  public void setNewProjectName(String item)
  {
    this.newProjectName = item;
  }
  
  public String getNewProjectName()
  {
    return this.newProjectName;
  }
  
  public void setBasePath(String basePath1)
  {
    this.basePath = basePath1;
  }
  
  public String getBasePath()
  {
    return this.basePath;
  }
  
  public void setNewPath(String newPath1)
  {
    this.newPath = newPath1;
  }
  
  public String getNewPath()
  {
    return this.newPath;
  }
  
  public void setFile(String item)
  {
    this.fileName = item;
  }
  
  public String getFile()
  {
    return this.fileName;
  }
  
  public String getName()
  {
    return this.nodeName;
  }
  
  public void setMessage(String s)
  {
    this.message = s;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public String toString()
  {
    return getName();
  }
  
  public void setParent(Node p)
  {
    this.parent = p;
  }
  
  public Node getParent()
  {
    return this.parent;
  }
  
  public void addChild(Node child)
  {
    this.children.add(child);
    if (child.getParent() == null) {
      child.setParent(this);
    }
  }
  
  public Node getChild(int index)
  {
    return (Node)this.children.get(index);
  }
  
  public Vector<Node> getChildren()
  {
    return this.children;
  }
  
  public boolean hasChildren()
  {
    return !this.children.isEmpty();
  }
}
