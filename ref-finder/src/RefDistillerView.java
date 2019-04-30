import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import edu.utexas.seal.refChecker.RefChecker;
import edu.utexas.seal.refSeparator.RefSeparator;
import edu.utexas.seal.reffinder.Application;
import edu.utexas.seal.util.Change;
import edu.utexas.seal.util.ChangesFilter;
import edu.utexas.seal.util.ModelProvider;
import edu.utexas.seal.util.RefDistillerStrings;
import edu.utexas.seal.util.StringConstants;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.List;
import org.w3c.dom.Element;


public class RefDistillerView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.utexas.seal.refdistiller2.views.RefDistillerView";
	private TableViewer viewer;
	protected String xmlFilePath;
	private int selectedIndex = -1;
	private int selectedIndexDelta = -1;
	private IProject projOrig;
	private IProject projDelta;
	private IProject projTemp;

	private ArrayList<String> renamedMeths;

	private final String srcFolder = "src";

	public RefDistillerView() {
		parent = null;
	}


	Composite parent;

	private List list;
	public void createPartControl(Composite p) {
		this.parent = p;
		//Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		//Get all projects in the Workspace
		final IProject [] projects = root.getProjects();
		GridLayout layout = new GridLayout(4, false);
		parent.setLayout(layout);

		firstPage(p, projects);
	}


	private void firstPage(Composite p, final IProject[] projects) {
		///Label original
		final Label labelProjSelect = new Label(parent, SWT.NONE);
		labelProjSelect.setText("Original Version");

		// Combo original
		final Combo comboProjects = new Combo(parent, SWT.SCROLL_LINE);
		comboProjects.setVisibleItemCount(5);
		for (int i=0; i < projects.length; i++){
			comboProjects.add(projects[i].getName());
		}

		//label target
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		final Label labelProjSelectDelta = new Label(parent, SWT.NONE);
		labelProjSelectDelta.setText("Target Version");

		//Combo target
		final Combo comboProjectsDelta = new Combo(parent, SWT.SCROLL_LINE);
		comboProjectsDelta.setVisibleItemCount(5);
		for (int i=0; i < projects.length; i++){
			comboProjectsDelta.add(projects[i].getName());
		}
		new Label(parent, SWT.NONE);
		new Label(p, SWT.NONE);

		//RefFinder
		final Button btnRunReffinder = new Button(parent, SWT.NONE);
		btnRunReffinder.setText("Run RefFinder");
		//		final ArrayList<String> refStrings =  new ArrayList<String>();
		btnRunReffinder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Application refFinder = new Application();
				getProjInfo(projects, comboProjects, comboProjectsDelta);
				try {
					refFinder.myStart(projOrig, projDelta, null);
					String refs = refFinder.getInfo().refsOnlyFile;
					Scanner input = new Scanner (new File(refs));
					ArrayList<String> refStrings =  new ArrayList<String>();
					showRefs(input, refStrings);
					xmlFilePath = projDelta.getLocation().toOSString()+System.getProperty("file.separator")+"RefList.xml";

				} catch (Exception e1) {
				}
			}
		});
		new Label(p, SWT.NONE);
	}

		private void showRefs(Scanner input, ArrayList<String> refStrings) {
		while(input.hasNext()){
			String line = input.nextLine();
			if (!line.equals("") && line.length() > 0)
				refStrings.add(line);

		}
		if (refStrings.size() == 0){
			MessageBox dialog = 
					new MessageBox(parent.getShell(), SWT.ERROR | SWT.OK);
			dialog.setText("Error");
			dialog.setMessage("No refactoring information was found!");
			dialog.open();
		} else{
			list = new List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
			list.setBounds (20, 100, 250, 250);
			list.setVisible(true);
			for (int i = 0; i < refStrings.size(); i++) {
				list.add(refStrings.get(i));
			}
		}
	}
}