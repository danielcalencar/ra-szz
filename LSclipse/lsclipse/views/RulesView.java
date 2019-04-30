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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import lsclipse.LSDResult;
import lsclipse.LSDiffRunner;
import lsclipse.LSclipse;
import lsclipse.dialogs.ProgressBarDialog;
import lsclipse.dialogs.SelectProjectDialog;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

public class RulesView
  extends ViewPart
{
  Action selectAction;
  Action explainAction;
  Action englishAction;
  Action filterAction;
  Action sortAction;
  TabFolder tabFolder;
  GridData layoutData1;
  GridData layoutHidden;
  Composite parent;
  Table rulesTable;
  TabItem tabItemExamples;
  TabItem tabItemExceptions;
  org.eclipse.swt.widgets.List examplesList;
  org.eclipse.swt.widgets.List exceptionsList;
  ProgressBarDialog progbar;
  java.util.List<LSDResult> rules = new ArrayList();
  IProject baseproj = null;
  IProject newproj = null;
  
  public void createPartControl(Composite parent)
  {
    this.parent = parent;
    
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    parent.setLayout(layout);
    
    this.layoutData1 = new GridData(2);
    this.layoutData1.grabExcessHorizontalSpace = true;
    this.layoutData1.grabExcessVerticalSpace = true;
    this.layoutData1.horizontalAlignment = 4;
    this.layoutData1.verticalAlignment = 4;
    this.layoutData1.exclude = false;
    
    this.layoutHidden = new GridData(2);
    this.layoutHidden.grabExcessHorizontalSpace = true;
    this.layoutHidden.grabExcessVerticalSpace = true;
    this.layoutHidden.horizontalAlignment = 4;
    this.layoutHidden.verticalAlignment = 4;
    this.layoutHidden.exclude = true;
    
    this.rulesTable = new Table(parent, 65540);
    TableColumn col1 = new TableColumn(this.rulesTable, 0);
    this.rulesTable.setHeaderVisible(true);
    col1.setText("Accuracy");
    col1.pack();
    TableColumn col2 = new TableColumn(this.rulesTable, 0);
    col2.setText("Rule");
    col2.setWidth(430);
    this.rulesTable.setLayoutData(this.layoutData1);
    this.rulesTable.addListener(13, new Listener()
    {
      public void handleEvent(Event e)
      {
        RulesView.this.refreshExamples();
      }
    });
    this.tabFolder = new TabFolder(parent, 0);
    this.tabItemExamples = new TabItem(this.tabFolder, 0);
    this.tabItemExamples.setText("Changes");
    this.examplesList = new org.eclipse.swt.widgets.List(this.tabFolder, 4);
    this.examplesList.addMouseListener(new MouseListener()
    {
      public void mouseDoubleClick(MouseEvent arg0)
      {
        MessageDialog.openError(RulesView.this.examplesList.getShell(), "File selection error", RulesView.this.examplesList.getSelection()[0]);
      }
      
      public void mouseDown(MouseEvent arg0) {}
      
      public void mouseUp(MouseEvent arg0) {}
    });
    this.tabItemExamples.setControl(this.examplesList);
    this.tabItemExceptions = new TabItem(this.tabFolder, 0);
    this.tabItemExceptions.setText("Exceptions");
    this.exceptionsList = new org.eclipse.swt.widgets.List(this.tabFolder, 4);
    this.tabItemExceptions.setControl(this.exceptionsList);
    this.tabFolder.setLayoutData(this.layoutHidden);
    
    parent.layout();
    
    createActions();
    createMenu();
    createToolbar();
  }
  
  public void createActions()
  {
    this.selectAction = new Action("Select version...")
    {
      public void run()
      {
        SelectProjectDialog seldiag = new SelectProjectDialog(RulesView.this.parent.getShell());
        int returncode = seldiag.open();
        if (returncode > 0) {
          return;
        }
        RulesView.this.baseproj = ResourcesPlugin.getWorkspace().getRoot().getProject(seldiag.getProj1());
        RulesView.this.newproj = ResourcesPlugin.getWorkspace().getRoot().getProject(seldiag.getProj2());
        
        ProgressBarDialog pbdiag = new ProgressBarDialog(RulesView.this.parent.getShell());
        pbdiag.open();
        pbdiag.setStep(0);
        
        RulesView.this.rules = new LSDiffRunner().doLSDiff(seldiag.getProj1(), seldiag.getProj2(), pbdiag);
        
        RulesView.this.refreshRules();
        RulesView.this.refreshExamples();
      }
    };
    this.selectAction.setImageDescriptor(PlatformUI.getWorkbench()
      .getSharedImages().getImageDescriptor(
      "IMG_OBJ_FOLDER"));
    
    this.explainAction = new Action("Explain")
    {
      public void run()
      {
        if (RulesView.this.tabFolder.getLayoutData().equals(RulesView.this.layoutHidden)) {
          RulesView.this.showRulesList();
        } else {
          RulesView.this.hideRulesList();
        }
      }
    };
    this.explainAction.setImageDescriptor(LSclipse.getImageDescriptor("icons/explain.gif"));
    
    this.englishAction = new Action("Translate to English")
    {
      public void run() {}
    };
    this.englishAction.setImageDescriptor(LSclipse.getImageDescriptor("icons/english.gif"));
    
    this.sortAction = new Action("Sort")
    {
      public void run() {}
    };
    this.sortAction.setImageDescriptor(LSclipse.getImageDescriptor("icons/sort.gif"));
    
    this.filterAction = new Action("Filter")
    {
      public void run() {}
    };
    this.filterAction.setImageDescriptor(LSclipse.getImageDescriptor("icons/filter.gif"));
  }
  
  private void createToolbar()
  {
    IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    mgr.add(this.selectAction);
    mgr.add(this.explainAction);
    mgr.add(this.englishAction);
    mgr.add(this.sortAction);
    mgr.add(this.filterAction);
  }
  
  private void createMenu() {}
  
  public void setFocus() {}
  
  private void showRulesList()
  {
    this.tabFolder.setLayoutData(this.layoutData1);
    this.tabFolder.layout();
    this.parent.layout();
  }
  
  private void hideRulesList()
  {
    this.tabFolder.setLayoutData(this.layoutHidden);
    this.tabFolder.layout();
    this.parent.layout();
  }
  
  private void refreshRules()
  {
    this.rulesTable.removeAll();
    for (int i = 0; i < this.rules.size(); i++)
    {
      LSDResult rule = (LSDResult)this.rules.get(i);
      TableItem ti = new TableItem(this.rulesTable, 0);
      ti.setText(new String[] { rule.num_matches + "/" + (rule.num_matches + rule.num_counter), rule.desc });
    }
    this.rulesTable.layout();
  }
  
  private void refreshExamples()
  {
    refreshExamples(this.rulesTable.getSelectionIndex());
  }
  
  private void refreshExamples(int index)
  {
    if ((index < 0) || (index >= this.rules.size())) {
      return;
    }
    LSDResult rule = (LSDResult)this.rules.get(index);
    
    this.tabItemExamples.setText("Changes (" + rule.examples.size() + ")");
    this.examplesList.removeAll();
    String s;
    for (Iterator localIterator = rule.getExampleStr().iterator(); localIterator.hasNext(); this.examplesList.add(s)) {
      s = (String)localIterator.next();
    }
    this.tabItemExceptions.setText("Exceptions (" + rule.exceptions.size() + ")");
    this.exceptionsList.removeAll();
    String s;
    for (localIterator = rule.getExceptionsString().iterator(); localIterator.hasNext(); this.exceptionsList.add(s)) {
      s = (String)localIterator.next();
    }
  }
  
  public static void openInEditor(IFile file, int startpos, int length)
  {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    HashMap map = new HashMap();
    map.put("charStart", new Integer(startpos));
    map.put("charEnd", new Integer(startpos + length));
    map.put("org.eclipse.ui.editorID", 
      "org.eclipse.ui.DefaultTextEditor");
    try
    {
      IMarker marker = file.createMarker("org.eclipse.core.resources.textmarker");
      marker.setAttributes(map);
      
      IDE.openEditor(page, marker);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
