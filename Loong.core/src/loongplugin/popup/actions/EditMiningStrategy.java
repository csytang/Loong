package loongplugin.popup.actions;

import loongplugin.dialog.MiningStrategyConfDialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

public class EditMiningStrategy implements IObjectActionDelegate{
	private IProject project;
	private Shell shell;
	private static ISelection selection;
	
	public EditMiningStrategy() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		project = getCurrentProject();
		if (project != null) {
			MiningStrategyConfDialog.getDefault(shell);
			MiningStrategyConfDialog.getDefault().create();
			MiningStrategyConfDialog.getDefault().open();
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
		shell = targetPart.getSite().getShell();
	}
	public static IProject getCurrentProject(){    
        ISelectionService selectionService =     
            Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    

         

        IProject project = null;    
        if(selection instanceof IStructuredSelection) {    
            Object element = ((IStructuredSelection)selection).getFirstElement();    

            if (element instanceof IResource) {    
                project= ((IResource)element).getProject();    
            } else if (element instanceof PackageFragmentRootContainer) {    
                IJavaProject jProject =     
                    ((PackageFragmentRootContainer)element).getJavaProject();    
                project = jProject.getProject();    
            } else if (element instanceof IJavaElement) {    
                IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
                project = jProject.getProject();    
            }    
        }     
        return project;    
    }
}
