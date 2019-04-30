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

import java.io.PrintStream;
import java.util.Set;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProgressBarDialog
{
  private Shell shell;
  private Label lblStep;
  private Label lblMessage;
  private ProgressBar pbProg;
  private Text txtLog;
  
  public ProgressBarDialog(Shell parentShell, String baseproj, int fetchmethod, String proj2, String svnurl, int svnversion, Set<String> changedFiles)
  {
    createDialogArea(parentShell);
  }
  
  public ProgressBarDialog(Shell parentShell)
  {
    this.shell = new Shell(parentShell, 16777248);
    this.shell.setSize(520, 400);
    this.shell.setText("Running LSDiff...");
    createDialogArea(this.shell);
  }
  
  public synchronized void open()
  {
    this.shell.open();
  }
  
  public synchronized void dispose()
  {
    this.shell.dispose();
  }
  
  public synchronized void setStep(int phaseid)
  {
    String[] phases = { "Preparation", 
      "Extract FB1: Base project facts", 
      "Extract FB2: New project facts", 
      "Compute Difference", 
      "Perform LSDiff", 
      "Cleanup" };
    if ((phaseid < 0) || (phaseid >= phases.length)) {
      return;
    }
    String step = "Step " + (phaseid + 1) + " / " + phases.length + " : " + phases[phaseid] + "\n";
    try
    {
      this.lblStep.setText(step);
    }
    catch (Throwable t)
    {
      System.out.println(t.getMessage());
    }
    this.txtLog.append(step);
  }
  
  public synchronized void setMessage(String msg)
  {
    this.lblMessage.setText(msg);
    this.txtLog.append(msg);
  }
  
  public synchronized void appendLog(String log)
  {
    if (this.txtLog.getLineCount() > 300) {
      this.txtLog.getText().substring(1000);
    }
    this.txtLog.append(log);
    System.out.print(log);
  }
  
  public void appendError(String err)
  {
    this.txtLog.append(err);
    System.out.print(err);
  }
  
  public void setProgressMaxValue(int maxvalue)
  {
    this.pbProg.setMaximum(maxvalue);
  }
  
  public void setProgressCurrValue(int currvalue)
  {
    this.pbProg.setSelection(currvalue);
  }
  
  private Control createDialogArea(Composite shell)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    shell.setLayout(layout);
    
    this.lblStep = new Label(shell, 0);
    this.lblStep.setLayoutData(new GridData(4, 2, true, false));
    this.lblStep.setText("Step 1 / 999");
    
    this.lblMessage = new Label(shell, 0);
    this.lblMessage.setLayoutData(new GridData(4, 2, true, false));
    this.lblMessage.setText("Idle");
    
    this.pbProg = new ProgressBar(shell, 65538);
    this.pbProg.setLayoutData(new GridData(4, 2, true, false));
    this.pbProg.setMaximum(1000);
    this.pbProg.setSelection(0);
    this.pbProg.setSelection(256);
    
    Label lblSeparator = new Label(shell, 258);
    lblSeparator.setLayoutData(new GridData(4, 2, true, false));
    
    this.txtLog = new Text(shell, 2818);
    
    this.txtLog.setLayoutData(new GridData(4, 4, true, true));
    this.txtLog.setEditable(false);
    this.txtLog.setBackground(new Color(shell.getDisplay(), 10, 10, 10));
    this.txtLog.setForeground(new Color(shell.getDisplay(), 200, 200, 200));
    
    shell.layout();
    
    return shell;
  }
}
