package loongplugin.popup.actions;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

import loongplugin.dialog.FeatureConfDialog;
import loongplugin.feature.FeatureModelManager;
import loongplugin.featuremodeleditor.IFeatureModelChangeListener;
import loongplugin.featuremodeleditor.event.FeatureModelChangedEvent;




@SuppressWarnings("restriction")
public class EditFeatureColorAction implements IObjectActionDelegate{
	//singleton pattern
	private Shell shell;
	private static ISelection selection;
	private IProject project;
	private FeatureConfDialog configDialog;
	public EditFeatureColorAction() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		// Initialized and open dialog
		
		project = getCurrentProject();
		
		if (project != null) {
			//configDialog = new FeatureConfigurationDialog(shell,project.getProject(),fmodel);
			//configDialog.create();
			configDialog = new FeatureConfDialog(shell,project);
			configDialog.create();
			configDialog.open();
		} else {
			MessageDialog.openInformation(shell, "Loong","No Java project selected.");
		}
		
		
	}

	
	public static IProject getCurrentProject(){    
         

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
	
	
	
	
	
	
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
		project = getCurrentProject();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		shell = targetPart.getSite().getShell();
	}
	
}
