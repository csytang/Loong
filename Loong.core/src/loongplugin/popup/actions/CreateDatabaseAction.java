package loongplugin.popup.actions;

import java.util.Iterator;

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
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import loongplugin.feature.FeatureModelManager;
import loongplugin.seeds.SeedsXMLWriter;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ApplicationObserverException;

public class CreateDatabaseAction implements IObjectActionDelegate{
	/**
	 * Parse the project and create the source database for selected project
	 */
	private IStructuredSelection aSelection;
	private IProject aProject;
	public CreateDatabaseAction() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		aProject = getSelectedProject();
		
		WorkspaceJob op = new WorkspaceJob("CreateDatabaseAction") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				// TODO Auto-generated method stub
				try {
					// get instance and init the database
					ApplicationObserver lDB = ApplicationObserver.getInstance();
					lDB.initialize(aProject, monitor);

				} catch (ApplicationObserverException lException) {
					lException.printStackTrace();
				}
				
				return Status.OK_STATUS;
			}};
		op.setUser(true);
		op.schedule();	
		
		//op.done(Status.OK_STATUS);
		//seedsXMLWriter seedswriter = new seedsXMLWriter(FeatureModelManager.getInstance().getFeatureModel(),aProject);
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
