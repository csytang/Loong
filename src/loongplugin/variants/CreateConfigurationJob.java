package loongplugin.variants;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.IColoredJavaSourceFile;
import loongplugin.configuration.ConfigurationException;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.utils.EmbeddedASTNodeCollector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import colordide.cideconfiguration.DefaultConfigurationMechanism;

public class CreateConfigurationJob extends WorkspaceJob {

	private final IWorkspaceRoot root;

	private final IProject sourceProject;

	private final Set<Feature> selectedFeatures;

	private final IProject targetProject;

	private FeatureModel featureModel;


	public CreateConfigurationJob(IProject sourceProject, Set<Feature> selectedFeatures, String projectName) {
		super("Generating Variant: " + sourceProject.getName() + " -> "
				+ projectName);
		this.sourceProject = sourceProject;
		root = ResourcesPlugin.getWorkspace().getRoot();
		this.targetProject = root.getProject(projectName);
		this.selectedFeatures = selectedFeatures;

	}

	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		int coloredFileCount = countColoredFiles(sourceProject);

		monitor.beginTask("Generating Variant", coloredFileCount + 3);

		if (targetProject.exists()) {
			monitor.subTask("Removing existing project.");
			targetProject.delete(true, new SubProgressMonitor(monitor, 0));
		}
		monitor.worked(1);

		monitor.subTask("Creating target project.");
		targetProject.create(sourceProject.getDescription(),
				new SubProgressMonitor(monitor, 0));
		targetProject.open(new SubProgressMonitor(monitor, 0));
		monitor.worked(1);
		monitor.subTask("Generating new project variant.");
		IFile cpFile = sourceProject.getFile(".classpath");
		if (cpFile.exists())
			cpFile.copy(targetProject.getFile(".classpath").getFullPath(),
					true, new SubProgressMonitor(monitor, 0));
		monitor.worked(1);

		featureModel = FeatureModelManager.getInstance(sourceProject).getFeatureModel();
		
		configureProject(sourceProject, targetProject, monitor);

		monitor.done();
		return Status.OK_STATUS;
	}

	private int countColoredFiles(IContainer directory) throws CoreException {
		int count = 0;
		for (IResource resource : directory.members()) {
			if (resource.getType() == IResource.FOLDER)
				count += countColoredFiles((IContainer) resource);
			if (resource.getType() == IResource.FILE) {
				IFile file = (IFile) resource;
				if ("clr".equals(file.getFileExtension()))
					count++;
			}
		}
		return count;
	}

	private void configureProject(IProject sourceProject,
			IProject targetProject, IProgressMonitor monitor)
			throws CoreException {
		monitor.subTask("Generating Project Variant " + targetProject.getProject().getName());

		configureContainer(sourceProject, monitor);
	}

	private void configureContainer(IContainer container, IProgressMonitor monitor) throws CoreException {	

		/**
		 * Debug
		 */
		System.out.println("Selected features:");
		for(Feature sel:selectedFeatures){
			System.out.println(sel.getName());
		}
		for (IResource resource : container.members()) {
			if (monitor.isCanceled())
				return;
			if (!resource.exists())
				continue;
			if (resource.getType() == IResource.FOLDER)
				configureContainer((IContainer) resource, monitor);
			if (resource.getType() == IResource.FILE)
				configureFile((IFile) resource, monitor);
		}

	}

	private void configureFile(IFile file, IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled())
			return;

		if (FeatureModelManager.getInstance().isFeatureModelFile(file))
			return;

		// check whether the whole file is colored and should be removed
		if (skipColoredFile(file)) //java file
			return;
		
		if(!file.getFileExtension().equals("java")){
			IFile targetFile = targetProject.getFile(file.getFullPath().removeFirstSegments(1));
			ensureDirectoryExists(targetFile, monitor);
			if (!targetFile.exists())
				file.copy(targetFile.getFullPath(), true, monitor);
			return;
		}
		
		if(getFileAllColors(file).isEmpty()){
			IFile targetFile = targetProject.getFile(file.getFullPath().removeFirstSegments(1));
			ensureDirectoryExists(targetFile, monitor);
			if (!targetFile.exists())
				file.copy(targetFile.getFullPath(), true, monitor);
			return;
		}
		
		IFile clrfile = CLRAnnotatedSourceFile.getColorFile(file);
		
		if(!clrfile.exists()){
			IFile targetFile = targetProject.getFile(file.getFullPath().removeFirstSegments(1));
			ensureDirectoryExists(targetFile, monitor);
			if (!targetFile.exists())
				file.copy(targetFile.getFullPath(), true, monitor);
			return;
		}
		
		CLRAnnotatedSourceFile sourceFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(clrfile);


		String configuredSource;
		try {
			configuredSource = configureSource(sourceFile, monitor);
		} catch (Exception e) {
			System.out.println(file);
			e.printStackTrace();
			System.exit(-1);
			configuredSource = "";
		}
		if (!configuredSource.trim().equals("")) {
			IFile targetFile = targetProject.getFile(file.getFullPath().removeFirstSegments(1));
			ensureDirectoryExists(targetFile, monitor);
			targetFile.create(new ByteArrayInputStream(configuredSource.getBytes()), true, monitor);
		}
	}

	private boolean skipColoredFile(IFile file) {	
		if(file==null){
			return true;
		}
		if(file.getFileExtension()==null)
			return true;
		if(file.getFileExtension().equals("clr")){
			return true;
		}else{
			Set<Feature> hiddenColors = new HashSet<Feature>();
			hiddenColors.addAll(featureModel.getFeatures());
			hiddenColors.removeAll(selectedFeatures);
			Set<Feature> fileColors = getFileColors(file);
			if(fileColors.isEmpty())
				return false;
			if(hiddenColors.containsAll(fileColors)){
				return true;
			}
			
			return false;
		}
	}

	private Set<Feature> getFileColors(IFile file){
		if(!file.getFileExtension().equals("java"))
			return new HashSet<Feature>();
		
		IFile clrfile = CLRAnnotatedSourceFile.getColorFile(file);
		if(!clrfile.exists())
			return new HashSet<Feature>();
		
		CLRAnnotatedSourceFile clr = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(clrfile);
		
		try {
			return clr.getColorManager().getColors(clr.getAST());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HashSet<Feature>();
	}
	
	private Set<Feature> getFileAllColors(IFile file) {
		if(!file.getFileExtension().equals("java"))
			return new HashSet<Feature>();
		
		IFile clrfile = CLRAnnotatedSourceFile.getColorFile(file);
		if(!clrfile.exists())
			return new HashSet<Feature>();
		
		CLRAnnotatedSourceFile clr = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(clrfile);
		ICompilationUnit icompilationunit= null;
		try {
			icompilationunit = clr.getICompilationUnit(clr.getAST());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		Set<Feature> features = new HashSet<Feature>();
		Set<ASTNode> allastNodes = EmbeddedASTNodeCollector.collectASTNodes(icompilationunit);
		for(ASTNode node:allastNodes){
			features.addAll(clr.getColorManager().getColors(node));
		}
		return features;
	}

	private void ensureDirectoryExists(IResource resource, IProgressMonitor monitor) throws CoreException {
		if (resource.getParent() instanceof IFolder) {
			ensureDirectoryExists(resource.getParent(), monitor);
		}

		if (resource instanceof IFolder) {
			IFolder folder = (IFolder) resource;
			if (!folder.exists()) {
				folder.create(true, true, monitor);
			}
		}
	}

	private String configureSource(CLRAnnotatedSourceFile sourceFile, IProgressMonitor monitor) throws ConfigurationException {
		if (monitor.isCanceled())
			return "";
		monitor.subTask("Generating " + sourceFile.getName());

		try {
			DefaultConfigurationMechanism mechanism = new DefaultConfigurationMechanism();
			return mechanism.configureFile(sourceFile, selectedFeatures);
		} finally {
			monitor.worked(1);
		}
	}

}
