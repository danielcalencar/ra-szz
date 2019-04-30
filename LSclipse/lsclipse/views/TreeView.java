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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import lsclipse.LSDiffRunner;
import lsclipse.TopologicalSort;
import lsclipse.dialogs.ProgressBarDialog;
import lsclipse.dialogs.SelectProjectDialog;
import metapackage.MetaInfo;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class TreeView
  extends ViewPart
{
  public static final String ID = "lsclipse.views.TreeView";
  private org.eclipse.swt.widgets.List viewer;
  private org.eclipse.swt.widgets.List list;
  private Action doubleClickTreeAction;
  private Action doubleClickListAction;
  private Action selectAction;
  private Composite parent;
  private Vector<Node> nodeList;
  private Map<String, Node> allNodes;
  private HashMap<String, Node> hashNode;
  HashMap<String, Node> strNodeRelation;
  GridData layoutData1;
  ArrayList<EditorInput> listDiffs = new ArrayList();
  IProject baseproj = null;
  IProject newproj = null;
  
  public TreeView()
  {
    this.nodeList = new Vector();
    this.hashNode = new HashMap();
    this.allNodes = new HashMap();
  }
  
  public void createPartControl(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    parent.setLayout(layout);
    
    this.layoutData1 = new GridData(2);
    this.layoutData1.grabExcessHorizontalSpace = true;
    this.layoutData1.grabExcessVerticalSpace = true;
    this.layoutData1.horizontalAlignment = 4;
    this.layoutData1.verticalAlignment = 4;
    this.layoutData1.exclude = false;
    
    this.parent = parent;
    this.viewer = new org.eclipse.swt.widgets.List(this.parent, 516);
    this.list = new org.eclipse.swt.widgets.List(this.parent, 516);
    this.viewer.setLayoutData(this.layoutData1);
    this.list.setLayoutData(this.layoutData1);
    
    parent.layout();
    
    makeActions();
    hookDoubleClickAction();
    contributeToActionBars();
    
    IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    mgr.add(this.selectAction);
  }
  
  private void contributeToActionBars()
  {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }
  
  private void fillLocalPullDown(IMenuManager manager) {}
  
  private void fillLocalToolBar(IToolBarManager manager) {}
  
  class EditorInput
    extends CompareEditorInput
  {
    String base_;
    String mod_;
    
    public EditorInput(CompareConfiguration configuration)
    {
      super();
    }
    
    public void setBase(InputStream inputStream)
    {
      try
      {
        this.base_ = convertToString(inputStream);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    
    public void setBase(String s)
    {
      this.base_ = s;
    }
    
    private String convertToString(InputStream is)
      throws IOException
    {
      char[] buffer = new char[65536];
      StringBuilder out = new StringBuilder();
      Reader in = new InputStreamReader(is, "UTF-8");
      int read;
      while ((read = in.read(buffer, 0, buffer.length)) >= 0)
      {
        int read;
        if (read > 0) {
          out.append(buffer, 0, read);
        }
      }
      return out.toString();
    }
    
    public void setMod(InputStream inputStream)
    {
      try
      {
        this.mod_ = convertToString(inputStream);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    
    public void setMod(String s)
    {
      this.mod_ = s;
    }
    
    public Object prepareInput(IProgressMonitor monitor)
      throws InvocationTargetException, InterruptedException
    {
      Differencer d = new Differencer();
      Object diff = d.findDifferences(false, new NullProgressMonitor(), 
        null, null, new Input(this.base_), new Input(this.mod_));
      return diff;
    }
    
    class Input
      implements ITypedElement, IStreamContentAccessor
    {
      String fContent;
      
      public Input(String s)
      {
        this.fContent = s;
      }
      
      public String getName()
      {
        return "name";
      }
      
      public String getType()
      {
        return "java";
      }
      
      public InputStream getContents()
        throws CoreException
      {
        return new ByteArrayInputStream(this.fContent.getBytes());
      }
      
      public Image getImage()
      {
        return null;
      }
    }
  }
  
  private void makeActions()
  {
    this.doubleClickListAction = new Action()
    {
      public void run()
      {
        int selind = TreeView.this.list.getSelectionIndex();
        if (selind >= TreeView.this.listDiffs.size()) {
          return;
        }
        TreeView.EditorInput file = (TreeView.EditorInput)TreeView.this.listDiffs.get(selind);
        if (file == null) {
          return;
        }
        CompareUI.openCompareEditor(file);
      }
    };
    this.doubleClickTreeAction = new Action()
    {
      public void run()
      {
        TreeView.this.list.removeAll();
        TreeView.this.listDiffs.clear();
        
        int index = TreeView.this.viewer.getSelectionIndex();
        if ((index < 0) || (index >= TreeView.this.nodeList.size())) {
          return;
        }
        Node node = (Node)TreeView.this.nodeList.get(index);
        
        TreeView.this.list.add(node.getName());
        TreeView.this.listDiffs.add(null);
        TreeView.this.list.add(node.params);
        TreeView.this.listDiffs.add(null);
        
        TreeView.this.list.add(" ");
        TreeView.this.listDiffs.add(null);
        
        int numtabs = 0;
        for (String statement : node.getDependents())
        {
          StringBuilder output = new StringBuilder();
          if (statement.equals(")")) {
            numtabs--;
          }
          for (int i = 0; i < numtabs; i++) {
            output.append("\t");
          }
          if (statement.equals("("))
          {
            output.append("(");
            numtabs++;
          }
          else
          {
            output.append(statement);
          }
          TreeView.this.list.add(output.toString());
          TreeView.this.listDiffs.add(null);
        }
        if (!node.oldFacts.isEmpty())
        {
          TreeView.this.list.add("");
          TreeView.this.listDiffs.add(null);
          TreeView.this.list.add("Compare:");
          TreeView.this.listDiffs.add(null);
          for (String display : node.oldFacts.keySet())
          {
            IJavaElement filenameBase = (IJavaElement)node.oldFacts.get(display);
            IJavaElement filenameMod = (IJavaElement)node.newFacts.get(display);
            TreeView.this.list.add(display);
            TreeView.EditorInput ei = new TreeView.EditorInput(TreeView.this, 
              new CompareConfiguration());
            try
            {
              ei.setBase(
                ((IFile)filenameBase.getCorrespondingResource()).getContents());
            }
            catch (Exception localException1)
            {
              ei.setBase("");
            }
            try
            {
              ei.setMod(
                ((IFile)filenameMod.getCorrespondingResource()).getContents());
            }
            catch (Exception localException2)
            {
              ei.setMod("");
            }
            TreeView.this.listDiffs.add(ei);
          }
        }
      }
    };
    this.selectAction = new Action("Select version...")
    {
      public void run()
      {
        SelectProjectDialog seldiag = new SelectProjectDialog(
          TreeView.this.parent.getShell());
        int returncode = seldiag.open();
        if (returncode > 0) {
          return;
        }
        long start = System.currentTimeMillis();
        
        TreeView.this.baseproj = ResourcesPlugin.getWorkspace().getRoot()
          .getProject(seldiag.getProj1());
        TreeView.this.newproj = ResourcesPlugin.getWorkspace().getRoot()
          .getProject(seldiag.getProj2());
        
        ProgressBarDialog pbdiag = new ProgressBarDialog(
          TreeView.this.parent.getShell());
        pbdiag.open();
        pbdiag.setStep(0);
        if (new LSDiffRunner().doFactExtractionForRefFinder(
          seldiag.getProj1(), seldiag.getProj2(), pbdiag)) {
          TreeView.this.refreshTree();
        } else {
          System.out.println("Something went wrong - fact extraction failed");
        }
        pbdiag.setStep(5);
        pbdiag.setMessage("Cleaning up... ");
        pbdiag.appendLog("OK\n");
        
        pbdiag.dispose();
        long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start));
      }
    };
    this.selectAction.setImageDescriptor(PlatformUI.getWorkbench()
      .getSharedImages()
      .getImageDescriptor("IMG_OBJ_FOLDER"));
  }
  
  public void refreshTree()
  {
    this.list.removeAll();
    this.nodeList.clear();
    this.viewer.removeAll();
    System.out.println(this.baseproj.toString());
    System.out.println(this.newproj.toString());
    
    TopologicalSort tSort = new TopologicalSort();
    
    long beforetime = System.currentTimeMillis();
    tSort.sort(MetaInfo.refsOnlyFile);
    long aftertime = System.currentTimeMillis();
    
    Map<String, java.util.List<String>> dependentMap = tSort.dependents;
    
    Set<String> allchildren = new HashSet();
    for (java.util.List<String> s : dependentMap.values()) {
      allchildren.addAll(s);
    }
    this.strNodeRelation = new HashMap();
    Set<String> parents = new HashSet();
    
    Iterator localIterator2 = dependentMap.entrySet().iterator();
    Node temp;
    while (localIterator2.hasNext())
    {
      Object queryEntry = (Map.Entry)localIterator2.next();
      String filledQuery = (String)((Map.Entry)queryEntry).getKey();
      temp = makeNode(filledQuery, (java.util.List)((Map.Entry)queryEntry).getValue(), this.baseproj, 
        this.newproj);
      this.hashNode.put("[" + temp.getName() + "]", temp);
      this.allNodes.put(filledQuery, temp);
      System.out.println(filledQuery);
      this.strNodeRelation.put(filledQuery, temp);
      
      this.nodeList.add(temp);
      this.viewer.add(temp.getName());
      parents.add(filledQuery);
    }
    System.out.println("\nTotal time for inference(ms): " + (
      aftertime - beforetime));
    Object nodes = tSort.getGraph();
    int totalCount = 0;
    for (lsclipse.Node node : (Set)nodes) {
      if (node.numFound() > 0)
      {
        totalCount += node.numFound();
        System.out.print(node.toString() + ", ");
      }
    }
    System.out.println("\nFor a total of " + totalCount + 
      " refactorings found.");
  }
  
  private String getName(String filledQuery)
  {
    int parenthIndex = filledQuery.indexOf('(');
    return filledQuery.substring(0, parenthIndex);
  }
  
  private Node makeNode(String filledQuery, java.util.List<String> children, IProject baseProject, IProject newProject)
  {
    String name = getName(filledQuery);
    String nicename = name.replace('_', ' ');
    nicename = nicename.substring(0, 1).toUpperCase() + 
      nicename.substring(1);
    int nameIndex = filledQuery.indexOf(name);
    
    Node temp = new Node(nicename, null);
    temp.setDependents(children);
    temp.setFile("test.java.txt");
    temp.setProjectName("LSclipse");
    temp.params = filledQuery.substring(nameIndex + name.length());
    if (temp.params.length() > 4)
    {
      String[] params = temp.params
        .substring(2, temp.params.length() - 2).split("\",\"");
      ArrayList<String> fields = new ArrayList();
      ArrayList<String> methods = new ArrayList();
      ArrayList<String> classes = new ArrayList();
      String[] arrayOfString1;
      int j = (arrayOfString1 = params).length;
      for (int i = 0; i < j; i++)
      {
        String s = arrayOfString1[i];
        if ((!s.contains("{")) && 
          (!s.contains("}")) && 
          (!s.contains(";")) && 
          (!s.contains("=")) && 
          (s.contains("%")) && (
          (!s.contains("(")) || (s.indexOf("(") >= s.indexOf("%")))) {
          if ((s.contains("(")) && (s.contains(")"))) {
            methods.add(s);
          } else if (s.contains("#")) {
            fields.add(s);
          } else if (s.contains("%.")) {
            classes.add(s);
          }
        }
      }
      for (String s : methods)
      {
        int indhex = s.indexOf("#");
        String qualifiedClassName = s.substring(0, indhex);
        temp.oldFacts.put(qualifiedClassName, 
          (IJavaElement)LSDiffRunner.getOldTypeToFileMap().get(qualifiedClassName));
        temp.newFacts.put(qualifiedClassName, 
          (IJavaElement)LSDiffRunner.getNewTypeToFileMap().get(qualifiedClassName));
      }
      for (String s : fields)
      {
        int indhex = s.indexOf("#");
        String qualifiedClassName = s.substring(0, indhex);
        temp.oldFacts.put(qualifiedClassName, 
          (IJavaElement)LSDiffRunner.getOldTypeToFileMap().get(qualifiedClassName));
        temp.newFacts.put(qualifiedClassName, 
          (IJavaElement)LSDiffRunner.getNewTypeToFileMap().get(qualifiedClassName));
      }
      for (String s : classes)
      {
        temp.oldFacts.put(s, (IJavaElement)LSDiffRunner.getOldTypeToFileMap().get(s));
        temp.newFacts.put(s, (IJavaElement)LSDiffRunner.getNewTypeToFileMap().get(s));
      }
    }
    return temp;
  }
  
  private void hookDoubleClickAction()
  {
    this.viewer.addMouseListener(new MouseListener()
    {
      public void mouseDoubleClick(MouseEvent arg0)
      {
        TreeView.this.doubleClickTreeAction.run();
      }
      
      public void mouseDown(MouseEvent arg0) {}
      
      public void mouseUp(MouseEvent arg0) {}
    });
    this.list.addMouseListener(new MouseListener()
    {
      public void mouseDoubleClick(MouseEvent arg0)
      {
        TreeView.this.doubleClickListAction.run();
      }
      
      public void mouseDown(MouseEvent arg0) {}
      
      public void mouseUp(MouseEvent arg0) {}
    });
  }
  
  public void setFocus() {}
}
