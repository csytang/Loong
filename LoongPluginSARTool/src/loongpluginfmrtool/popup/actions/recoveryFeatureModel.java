package loongpluginfmrtool.popup.actions;

import java.util.Iterator;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ApplicationObserverException;
import loongpluginsartool.architecturerecovery.java.JavaRecoverGen;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class recoveryFeatureModel implements IObjectActionDelegate {

	private IStructuredSelection aSelection;
	private IProject aProject;
	private Shell shell;
	private IWorkbenchPart part;
	private ApplicationObserver lDB;
	
	public recoveryFeatureModel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		aProject = getSelectedProject();
		WorkspaceJob op = null;
		// ProgramDB 没有被初始化
		if(!this.lDB.isInitialized()){
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
		}
		
		// 等待ProgramDB  构建完成 
		try {
	    	if(op!=null)
	    		op.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    JavaRecoverGen gen = new JavaRecoverGen(this.lDB);
	    gen.BeginRecovery();
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
