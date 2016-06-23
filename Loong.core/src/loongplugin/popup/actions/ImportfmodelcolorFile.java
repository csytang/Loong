package loongplugin.popup.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JFileChooser;

import loongplugin.feature.FeatureModelManager;
import loongplugin.modelcolor.ModelIDCLRFileReader;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.internal.Workbench;

public class ImportfmodelcolorFile implements IObjectActionDelegate{
	/*
	 * modelidclr.xml
	 */
	private ISelection selection;
	private IWorkbenchPart part;
	private Shell shell;
	private File remotecolormodelxmlFile;
	private IFile projectcolormodelxmlFile;
	private IProject selectedProject;
	
	/*
	 * TODO 颜色重复检查
	 */
	public ImportfmodelcolorFile() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		// 打开一个文件选择器
		selectedProject =  getSelectedProject();
		
		FileDialog fsd = new FileDialog(shell);
		fsd.setFilterExtensions(new String[] {"*.xml"});
		
		fsd.setText("Select Feature model colored file<modelidclr.xml>");
		String chosenFilePath= fsd.open();
		
		remotecolormodelxmlFile = new File(chosenFilePath);
		if(!checkcompatitableForFile(remotecolormodelxmlFile)){
			return;
		}
		//将目标文件导入到当前地址
		
		projectcolormodelxmlFile = selectedProject.getFile("modelidclr.xml");
			
		
		InputStream targetStream;
		try {
			targetStream = new FileInputStream(remotecolormodelxmlFile);
			if(projectcolormodelxmlFile.exists()){
				projectcolormodelxmlFile.setContents(targetStream, EFS.NONE, null);
			}else
				projectcolormodelxmlFile.create(targetStream, EFS.NONE, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new ModelIDCLRFileReader(projectcolormodelxmlFile,FeatureModelManager.getInstance(selectedProject).getFeatureModel());
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
	
	private boolean checkcompatitableForFile(File colorfmodelFile){
		//File Name check along with file extenstion
		if(!colorfmodelFile.exists()){
			return false;
		}
		String fileName = colorfmodelFile.getName();
		if(!fileName.equals("modelidclr.xml")){
			return false;
		}
		
		return true;
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
