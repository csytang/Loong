package loongplugin.popup.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loongplugin.nature.LoongProjectNature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

public class UninstallLoongNature implements IObjectActionDelegate {

	public UninstallLoongNature() {
		// TODO Auto-generated constructor stub
	}
	private IProject currProject;
	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		currProject = getSelectedProject();
		System.out.println("Unstalling nature for " + currProject.getName());
			try {
				IProjectDescription description = currProject.getDescription();
				List<String> natures = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
				if (natures.contains(LoongProjectNature.NATURE_ID)) {
					natures.remove(LoongProjectNature.NATURE_ID);
					description.setNatureIds(natures.toArray(new String[natures.size()]));
					currProject.setDescription(description, null);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
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
