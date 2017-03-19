package loongplugin.popup.actions;

import java.io.File;

import loongplugin.utils.StatusRollbackJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

public class StatusRollBack implements IObjectActionDelegate{

	private IProject currProject;
	private ISelection selection;
	private IWorkbenchPart part;
	private Shell shell;
	
	public StatusRollBack() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		currProject = getSelectedProject();
		IFile seedfile = currProject.getFile("seed.xml");
		
		if(!seedfile.exists()){
			// 允许用户选择任意文件
			FileDialog fsd = new FileDialog(shell);
			fsd.setFilterExtensions(new String[] {"*.xml"});
			String chosenFilePath= fsd.open();
			File targetfile = new File(chosenFilePath);
			StatusRollbackJob job = new StatusRollbackJob(currProject,targetfile);
			job.setUser(true);
			job.setPriority(Job.LONG);
			job.schedule();
		}else{
			// 使用seed文件
			StatusRollbackJob job = new StatusRollbackJob(currProject,seedfile);
			job.setUser(true);
			job.setPriority(Job.LONG);
			job.schedule();
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.part = targetPart;
		shell = targetPart.getSite().getShell();
		
	}

	private IProject getSelectedProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
	    ISelection selection = selectionService.getSelection();    
	    IProject project = null; 
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
	
}
