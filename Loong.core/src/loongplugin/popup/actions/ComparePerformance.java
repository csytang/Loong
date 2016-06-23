package loongplugin.popup.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import loongplugin.feature.Feature;
import loongplugin.nature.LoongProjectNature;
import loongplugin.performance.BenchmarkProject;
import loongplugin.performance.ComputePerformanceJob;
import loongplugin.performance.ResultXML;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ComparePerformance implements IObjectActionDelegate{

	private IProject selectedProject;
	private IProject benchmarkProject;
	private ISelection selection;
	private IWorkbenchPart part;
	private Shell shell;
	private String selectedDir;
	
	public ComparePerformance() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
	   selectedProject =  getSelectedProject();
	   BenchmarkProject instance = BenchmarkProject.getDefult(shell);
	   instance.create();
	   instance.open();
	   
	   String benmarkProjectName = instance.getProjectName();
        
	   //System.out.println("benchmarkProjectName:"+benmarkProjectName);
       IProject benchmarkProject = ResourcesPlugin.getWorkspace().getRoot().getProject(benmarkProjectName);
       if(!benchmarkProject.exists()){
		   MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot find benchmark project, please import the benchmark to this workspace");
    	   return;
       }
       
       //security check
       /*
        * 1. 是否为 Loong Project
        */
       if(!hasLoongNature(benchmarkProject)){
		   MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot compare, since the benchmark is not a Loong project");
    	   return;
       }
       /*
        * 是否有相同的 feature model 及 ID color annotation
        */
       try {
    	   if(!hasSameFeatureIDModel(selectedProject,benchmarkProject)){
			   MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot compare, two project have different feature model, feature ids or feature binding colors");
			   return;
		   }
       } catch (ParserConfigurationException | SAXException | IOException e) {
    	   // TODO Auto-generated catch block
    	   e.printStackTrace();
       }
       ComputePerformanceJob job = new ComputePerformanceJob(selectedProject,benchmarkProject);
       job.setUser(true);
       job.setPriority(Job.LONG);
       job.schedule();
       
       
       
       
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}
	
	public boolean hasLoongNature(IProject project){
		IProjectDescription description;
		try {
			description = project.getDescription();
			List<String> natures = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			if (!natures.contains(LoongProjectNature.NATURE_ID)) {
				return false;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean hasSameFeatureIDModel(IProject source,IProject target) throws ParserConfigurationException, SAXException, IOException{
		IFile sourcemodelclr = source.getFile("modelidclr.xml");
		IFile targetmodelclr = target.getFile("modelidclr.xml");
		if(!sourcemodelclr.exists())
			return false;
		if(!targetmodelclr.exists()){
			return false;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		dbf.setIgnoringElementContentWhitespace(true);
		dbf.setIgnoringComments(true);
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc1 = db.parse(sourcemodelclr.getRawLocation().toFile());
		doc1.normalizeDocument();

		Document doc2 = db.parse(targetmodelclr.getRawLocation().toFile());

		doc2.normalizeDocument();
		return doc1.isEqualNode(doc2);
		
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
