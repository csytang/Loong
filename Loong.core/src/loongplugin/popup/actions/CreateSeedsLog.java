package loongplugin.popup.actions;

import loongplugin.feature.FeatureModelManager;
import loongplugin.seeds.SeedsXMLWriter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CreateSeedsLog implements IObjectActionDelegate{
	private IProject selectedProject;
	private ISelection selection;
	private IWorkbenchPart part;
	
	public CreateSeedsLog() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		selectedProject =  getSelectedProject();
		// 收集所有现有的被CLR内容
		
		try {
			@SuppressWarnings("unused")
			SeedsXMLWriter seedswriter = new SeedsXMLWriter(FeatureModelManager.getInstance().getFeatureModel(),selectedProject);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
