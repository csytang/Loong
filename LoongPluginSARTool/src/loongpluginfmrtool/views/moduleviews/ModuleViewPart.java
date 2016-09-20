package loongpluginfmrtool.views.moduleviews;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import loongplugin.source.database.ApplicationObserver;
import loongpluginfmrtool.module.builder.ModuleBuilder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;

public class ModuleViewPart extends ViewPart {
/**
 * Module - id
 * 	-Import
 *  -Configuration Operation
 *  -Variability
 *  -Function and Variables
 *  ----Assess Matter----
 *  -In deep
 *  -Number of Valid Configurations
 *  -Number of Configuration Options
 *  -Scope of Affected.
 */
	
	private Action generateAction; 
	private TreeViewer fViewer;	
	private Tree tree;
	private String[]columnNames={"properties","value"};
	private IProject selectedProject=null;
	public static ModuleViewPart instance;
	public static ModuleViewPart getInstance(){
		if(instance==null)
			instance = new ModuleViewPart();
		return instance;
	}
	
	public ModuleViewPart() {
		// TODO Auto-generated constructor stub
		instance = this;
		selectedProject = getSelectedProject();
		if(selectedProject==null){
			// Obtain the selectedProject from active editor
			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(activeEditor==null)
				return;
			IEditorInput input = activeEditor.getEditorInput();
			if (input instanceof IFileEditorInput){
				IFileEditorInput editorinput = (IFileEditorInput)input;
				selectedProject = editorinput.getFile().getProject();
			}
		}
		
	}
	
	private IProject getSelectedProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
	    ISelection selection = selectionService.getSelection();    
	    IProject project = null; 
	    if(selection==null)
	    	return null;
		Object element = ((IStructuredSelection)selection).getFirstElement();    

        if (element instanceof IResource) {    
            project= ((IResource)element).getProject();    
        } else if (element instanceof PackageFragmentRootContainer) {    
            IJavaProject jProject =  ((PackageFragmentRootContainer)element).getJavaProject();    
            project = jProject.getProject();    
        } else if (element instanceof IJavaElement) {    
            IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
            project = jProject.getProject();    
        }    
        return project;
	}
	
	public TreeViewer getTreeViewer(){
		return fViewer;
	}
	

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		createTree(parent);
		createTableViewer();
		
		
	}
	
	private void createTree(Composite parent) {
		tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);

		TreeColumn column;
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Module/Attributes");
		column.setWidth(120);
		column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Value");
		column.setWidth(80);

		// Pack the columns
	    for (int i = 0, n = tree.getColumnCount(); i < n; i++) {
	    	tree.getColumn(i).pack();
	    }
	    
	    tree.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				int width = tree.getClientArea().width;
				if (width > 200) {
					tree.getColumn(0).setWidth(width - 60);
					tree.getColumn(1).setWidth(80);
				} else {
					tree.getColumn(0).setWidth(width / 2);
					tree.getColumn(1).setWidth(width / 2);
				}
			}
		});
	}
	
	private void createTableViewer() {
		fViewer = new TreeViewer(tree);
		fViewer.setColumnProperties(columnNames);
		
		
	}
	
	
	
	

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		fViewer.getControl().setFocus();
	}

	
	
}