package loongpluginsartool.popup.actions;

import java.util.Iterator;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ApplicationObserverException;
import loongpluginsartool.architecturerecovery.java.JavaRecoverGen;
import loongpluginsartool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginsartool.editor.configfeaturemodeleditor.model.ConfFeatureModel;
import loongpluginsartool.editor.configfeaturemodeleditor.serializer.DiagramSerializer;
import loongpluginsartool.editor.configfeaturemodeleditor.ui.ConfigurableFeatureModelEditor;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class JavaProjectSARecovery implements IObjectActionDelegate{

	private IStructuredSelection aSelection;
	private IProject aProject;
	private Shell shell;
	private IWorkbenchPart part;
	private ApplicationObserver lDB;
	
	public JavaProjectSARecovery() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		// 1. check whether the program database is built, if not built the program database first automatically 
		aProject = getSelectedProject();
		lDB = ApplicationObserver.getInstance();
		WorkspaceJob op = null;
		if(lDB.isInitialized()){
			if(lDB.getInitializedProject()!=aProject){
				op = new WorkspaceJob("CreateDatabaseAction") {

					@Override
					public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
						// TODO Auto-generated method stub
						try {
							// get instance and init the database
							lDB = ApplicationObserver.getInstance();
							lDB.initialize(aProject, monitor);

						} catch (ApplicationObserverException lException) {
							lException.printStackTrace();
						}
						
						return Status.OK_STATUS;
				}};
				op.setUser(true);
				op.schedule();	
			}
		}else{
			op = new WorkspaceJob("CreateDatabaseAction") {

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					// TODO Auto-generated method stub
					try {
						// get instance and init the database
						lDB = ApplicationObserver.getInstance();
						lDB.initialize(aProject, monitor);

					} catch (ApplicationObserverException lException) {
						lException.printStackTrace();
					}
					
					return Status.OK_STATUS;
			}};
			op.setUser(true);
			op.schedule();	
		}
		
		//2. open temperate feature model editor
		// 2.1 check whether temperate feature model file exist if not, create one
		IFile tempfeaturemodelfile = aProject.getFile("configfeaturemodel.mconfig");
		// 2.2 open the temperate feature model editor editor
		if(!tempfeaturemodelfile.exists()){
			ConfFeatureModel cnfModel = new ConfFeatureModel();
			ConfFeature cnfFeature = new ConfFeature();
			cnfFeature.setText("SPL");
			cnfFeature.setConstraint(new Rectangle(80,80,100,80));
			cnfModel.addChild(cnfFeature);
			InputStream source;
			try {
				source = DiagramSerializer.serialize(cnfModel);
				tempfeaturemodelfile.create(source, IResource.NONE, null);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    try {
			IDE.openEditor(page, tempfeaturemodelfile);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    // sync with the system for program db buit
	    try {
	    	if(op!=null)
	    		op.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		if (selection instanceof IStructuredSelection)
			aSelection = (IStructuredSelection) selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.part = targetPart;
		shell = targetPart.getSite().getShell();
	}
	
	private IProject getSelectedProject() {
		IProject lReturn = null;
		Iterator i = aSelection.iterator();
		if (i.hasNext()) {
			Object lNext = i.next();
			if (lNext instanceof IResource) {
				lReturn = ((IResource) lNext).getProject();
			} else if (lNext instanceof IJavaElement) {
				IJavaProject lProject = ((IJavaElement) lNext).getJavaProject();
				lReturn = lProject.getProject();
			}
		}
		return lReturn;
	}
	

}
