package loongplugin.utils;

import java.io.File;
import java.util.Map;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.seeds.SeedsXMLReader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;

public class StatusRollbackJob extends WorkspaceJob {
	
	private IProject aproject;
	private FeatureModel fmodel;
	private Map<Feature,Map<IFile,Set<ASTNode>>> recoveredseed;
	public StatusRollbackJob(IProject project,IFile file){
		super("Roll back to a stutus on project:"+project.getName());
		aproject = project;
		fmodel = FeatureModelManager.getInstance(aproject).getFeatureModel();
		parseStatusFile(file);
	}
	public StatusRollbackJob(IProject project,File file){
		super("Roll back to a stutus on project:"+project.getName());
		aproject = project;
		fmodel = FeatureModelManager.getInstance(aproject).getFeatureModel();
		parseStatusFile(file);
	}
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
		int compUnitCount = countJavaProject(JavaCore.create(aproject));
		monitor.beginTask("Cleaning color annotation for Loong project:"+aproject.getName(), compUnitCount+1);
		processContainer(aproject,monitor);
		recoverySeeds(recoveredseed);
		monitor.worked(1);
		monitor.done();
		return Status.OK_STATUS;
	}
	
	private void recoverySeeds(Map<Feature, Map<IFile, Set<ASTNode>>> recoveredseed) {
		// TODO Auto-generated method stub
		/*
		 * 使用恢复的信息 进行状态恢复
		 */
		for(Map.Entry<Feature, Map<IFile,Set<ASTNode>>>entry:recoveredseed.entrySet()){
			Feature f = entry.getKey();
			Map<IFile,Set<ASTNode>> bindingannotation = entry.getValue();
			for(Map.Entry<IFile, Set<ASTNode>>bindentry:bindingannotation.entrySet()){
				IFile file = bindentry.getKey();
				Set<ASTNode> associatedASTNodes = bindentry.getValue();
				ICompilationUnit unit = JavaCore.createCompilationUnitFrom(file);
				CLRAnnotatedSourceFile clrannotatedfile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(unit);
				CompilationUnitColorManager colormanager = (CompilationUnitColorManager) clrannotatedfile.getColorManager();
				colormanager.beginBatch();
				for(ASTNode node:associatedASTNodes){
					colormanager.addColor(node, f);
					f.addASTNodeToFeature(unit, node);
				}
				colormanager.endBatch();
			}
		}
		
	}
	
	private void parseStatusFile(IFile file){
		SeedsXMLReader seedsreader = new SeedsXMLReader(file,fmodel,aproject);
		recoveredseed = seedsreader.getSeeds();
	}
	
	private void parseStatusFile(File file){
		SeedsXMLReader seedsreader = new SeedsXMLReader(file,fmodel,aproject);
		recoveredseed = seedsreader.getSeeds();
	}
	
	
	
	private void processContainer(IContainer container,IProgressMonitor monitor)
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
