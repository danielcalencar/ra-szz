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
package lsclipse.dialogs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SelectProjectDialog
  extends Dialog
{
  private String proj1 = "";
  private String proj2 = "";
  private Combo cmbProj1;
  private Combo cmbProj2;
  
  public SelectProjectDialog(Shell parentShell)
  {
    super(parentShell);
  }
  
  public String getProj1()
  {
    return this.proj1;
  }
  
  public String getProj2()
  {
    return this.proj2;
  }
  
  public void okPressed()
  {
    this.proj1 = this.cmbProj1.getText();
    this.proj2 = this.cmbProj2.getText();
    
    super.okPressed();
  }
  
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText("Select Versions");
    
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    parent.setLayout(layout);
    
    GridData ldtDefault = new GridData(2);
    ldtDefault.grabExcessHorizontalSpace = true;
    ldtDefault.grabExcessVerticalSpace = true;
    ldtDefault.horizontalAlignment = 4;
    ldtDefault.verticalAlignment = 4;
    ldtDefault.exclude = false;
    
    GridLayout panelLayout = new GridLayout();
    panelLayout.numColumns = 1;
    
    Composite leftPanel = new Composite(parent, 0);
    leftPanel.setLayoutData(new GridData(1808));
    
    leftPanel.setLayout(panelLayout);
    
    Label base = new Label(leftPanel, 0);
    base.setText("Base Version:");
    
    this.cmbProj1 = new Combo(leftPanel, 8);
    this.cmbProj1.setLayoutData(ldtDefault);
    
    Label changed = new Label(leftPanel, 0);
    changed.setText("Changed Version:");
    
    this.cmbProj2 = new Combo(leftPanel, 8);
    this.cmbProj2.setLayoutData(ldtDefault);
    IProject[] arrayOfIProject;
    int j = (arrayOfIProject = ResourcesPlugin.getWorkspace().getRoot().getProjects()).length;
    for (int i = 0; i < j; i++)
    {
      IProject proj = arrayOfIProject[i];
      this.cmbProj1.add(proj.getName());
      this.cmbProj2.add(proj.getName());
    }
    return parent;
  }
}
