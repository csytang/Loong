package loongplugin.popup.actions;

import java.io.ByteArrayInputStream;

import loongplugin.featuremodeleditor.FeatureModelEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.Workbench;

public class EditFeatureModelAction implements IObjectActionDelegate{
	
	private ISelection selection;
	private IWorkbenchPart part;
	private Shell shell;
	private IProject project;
	public EditFeatureModelAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		
		project = getSelectedProject();
		if (project == null) {
			MessageDialog.openInformation(shell, "Loong", "No project selected.");
			return;
		}
		try {
			IFile modelFile = project.getFile("model.m");
			if (!modelFile.exists()) {
				modelFile.create(new ByteArrayInputStream(
						"Project : [Feature1] [Feature2] :: _Project ;".getBytes()), true,
						null);
			}
			IDE.openEditor(part.getSite().getPage(), modelFile, FeatureModelEditor.ID);
		} catch (PartInitException e) {
			MessageDialog.openInformation(shell, "Loong",
					"Error opening model.m file.");
		} catch (CoreException e) {
			MessageDialog.openInformation(shell, "Loong",
					"Error opening model.m file.");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
		project = getSelectedProject();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.part = targetPart;
		shell = targetPart.getSite().getShell();
	}
	private IProject getSelectedProject() { 
	    IProject project = null; 
		Object element = ((IStructuredSelection)this.selection).getFirstElement();    

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

	private IJavaProject getSelectedJavaProject() {
		if (selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection)
					.getFirstElement();
			if (selected instanceof IJavaProject)
				return (IJavaProject) selected;
		}
		return null;
	}
}
