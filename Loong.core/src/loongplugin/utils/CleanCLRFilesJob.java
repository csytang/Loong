package loongplugin.utils;

import java.util.Stack;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class CleanCLRFilesJob extends WorkspaceJob {
	private IProject project;
	
	public CleanCLRFilesJob(IProject sourceProject) {
		super("Clean Color Annotations in:"+sourceProject.getName());
		// TODO Auto-generated constructor stub
		project = sourceProject;
		
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		int compUnitCount = countJavaProject(JavaCore.create(project));
		monitor.beginTask("Cleaning color annotation for Loong project:"+project.getName(), compUnitCount);
		processContainer(project,monitor);
		monitor.done();
		return Status.OK_STATUS;
	}
	
	void processContainer(IContainer container,IProgressMonitor monitor)
	{
		try {
			   IResource[] members = container.members();
			   for (IResource member : members)
			   {
			      if (member instanceof IContainer) 
			      {
			    	  processContainer((IContainer)member,monitor);
			      }
			      else if (member instanceof IFile)
			      {	
			    	  IFile memberfile = (IFile)member;
			    	  
			    	  IPath relativefilePath = memberfile.getProjectRelativePath();
			    	  String extension = memberfile.getFileExtension();
			    	  if(extension==null){
			    		  
			    	  }
			    	  else if(extension.equals("clr")){
						  memberfile.delete(true, monitor);  
					  }
					  monitor.worked(1);
			      }
			   }
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		   
	}
	
	private int countJavaProject(IJavaProject sourceJavaProject)
			throws CoreException {
		int sum = 0;
		for (IPackageFragmentRoot root : sourceJavaProject
				.getPackageFragmentRoots()) {
			if (!root.exists())
				continue;
			if (root.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;

			sum += countPackageFragementRoot(sourceJavaProject, root);
		}
		return sum;
	}
	
	private int countPackageFragementRoot(IJavaProject sourceJavaProject,
			IPackageFragmentRoot sourceRoot) throws CoreException {
		int sum = 0;
		for (IPackageFragment pkg : sourceJavaProject.getPackageFragments()) {
			if (pkg.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;
			if (!sourceRoot.getPackageFragment(pkg.getElementName()).exists())
				continue;

			sum += countPackage(pkg);
		}
		return sum;
	}
	
	private int countPackage(IPackageFragment sourcePackage)
			throws CoreException {
		return sourcePackage.getCompilationUnits().length;
	}
	
	

}
