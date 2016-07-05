package loongplugin.CIDEbridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import loongplugin.color.ColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.guidsl.GuidslReader;
import loongplugin.feature.guidsl.UnsupportedModelException;
import loongplugin.modelcolor.ModelIDCLRFile;
import loongplugin.nature.CIDEProjectNature;
import loongplugin.nature.LoongProjectNature;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;

public class CreateLoongProjectJob extends WorkspaceJob {

	private final IProject sourceProject;
	private final IProject targetProject;
	private final IWorkspaceRoot root;
	
	private IFile modelm;
	private IFile modelcolors;
	private FeatureModel fmodel;
	private ColorManager clrmanager;
	private IPath workspaceRoot;
	
	public CreateLoongProjectJob(IProject sourceProject) {
		super("Creating Loong Project:"+sourceProject.getName()+"_loong");
		// TODO Auto-generated constructor stub
		this.sourceProject = sourceProject;
		root = ResourcesPlugin.getWorkspace().getRoot();
		String projectName = sourceProject.getName()+"_loong";
		this.targetProject = root.getProject(projectName);
		this.workspaceRoot = root.getRawLocation();
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		IJavaProject sourceJavaProject = JavaCore.create(sourceProject);
		int compUnitCount = countJavaProject(sourceJavaProject);
		monitor.beginTask("Creating Loong Project", compUnitCount+3);
		if (targetProject.exists()) {
			monitor.subTask("Removing existing project.");
			targetProject.delete(true,  new SubProgressMonitor(monitor, 0));
		}
		
		monitor.worked(1);

		modelm = sourceProject.getFile("model.m");
		if(modelm.exists()){
			//分析源代码 生产相应的内容
			//CIDE create feature model
			fmodel = new FeatureModel();
			GuidslReader gReader = new GuidslReader(fmodel);
			if(modelm!=null){
				try {
					gReader.parseInputStream(modelm.getContents());
				} catch (UnsupportedModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					gReader.parseInputStream(modelm.getContents());
				} catch (UnsupportedModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clrmanager = new ColorManager(fmodel);
			
		}else{
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot convert to Loong, not model.m file found");
			monitor.done();
			return Status.OK_STATUS;
		}
		
		modelcolors = sourceProject.getFile("model.colors");
		CIDEmodelcolorsReader modelcolorsReader;
		if(modelcolors.exists()){
			modelcolorsReader = new CIDEmodelcolorsReader(modelcolors);
		}else{
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot convert to Loong, not model.colors file found");
			monitor.done();
			return Status.OK_STATUS;
		}
		
		// 配置feature model 包括两点 设置 颜色 和 ID
		// clrmanager.featureColorInit();
		Set<Feature>featurerequiresColor = new HashSet<Feature>();
		Set<RGB>usedColors = new HashSet<RGB>();
		for(Feature f:fmodel.getFeatures()){
			f.setId(modelcolorsReader.getFeatureId(f));
			fmodel.setFeatureId(f,modelcolorsReader.getFeatureId(f));
			RGB fcolor = modelcolorsReader.getFeatureColor(f);
			if(fcolor!=null){
				f.setRGB(fcolor);
				clrmanager.setRGB(f, fcolor);
				usedColors.add(fcolor);
			}else{
				featurerequiresColor.add(f);
			}
		}
		setColorForUncoloredfeatures(featurerequiresColor,usedColors,clrmanager);
		
		monitor.subTask("Creating target project.");
		targetProject.create(sourceProject.getDescription(),  new SubProgressMonitor(monitor, 0));
		targetProject.open( new SubProgressMonitor(monitor, 0));
		monitor.worked(1);
		
		monitor.subTask("Configuring new project.");
		IFile cpFile = sourceProject.getFile(".classpath");
		if (cpFile.exists())
			cpFile.copy(targetProject.getFile(".classpath").getFullPath(),
					true, new SubProgressMonitor(monitor, 0));
		monitor.worked(1);
		
		IJavaProject targetJavaProject = JavaCore.create(targetProject);
		modifyAnnotationDocuments(sourceJavaProject, targetJavaProject, monitor);
		fixProjectNature(targetProject);
		
		// 创建colormodelmanger文件
		ModelIDCLRFile modelIDCLR = new ModelIDCLRFile(fmodel,targetProject);
				
		monitor.done();
		return Status.OK_STATUS;
	}
	
	private void setColorForUncoloredfeatures(Set<Feature>featurerequiresColor,Set<RGB>usedColors,ColorManager clrmanager){
		for(Feature f:featurerequiresColor){
			RGB rgb = generatedRGBNotInSet(usedColors);
			f.setRGB(rgb);
			clrmanager.setRGB(f, rgb);
			usedColors.add(rgb);
		}
	}
	private RGB generatedRGBNotInSet(Set<RGB>usedColors){
		RGB rgb = null;
		Random rand = new Random();
		int r = rand.nextInt(256);
		int g = rand.nextInt(256);
		int b = rand.nextInt(256);
		rgb = new RGB(r,g,b);
		while(usedColors.contains(rgb)){
			rand = new Random();
			r = rand.nextInt();
			g = rand.nextInt();
			b = rand.nextInt();
			rgb = new RGB(r,g,b);
		}
		return rgb;
	}
	
	private void modifyAnnotationDocuments(IJavaProject sourceJavaProject,
			IJavaProject targetJavaProject, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		monitor.subTask("Configuring Project "+ targetJavaProject.getProject().getName());
		
		
		for (IPackageFragmentRoot root : sourceJavaProject.getPackageFragmentRoots()) {
			if (monitor.isCanceled())
				return;
			if (!root.exists())
				continue;
			if (root.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;
			IPackageFragmentRoot targetRoot = copySourceFolder(root,targetJavaProject);
			configurePackageFragementRoot(sourceJavaProject, root, targetRoot,
					monitor);
		}
		
		processContainer(sourceJavaProject.getProject(),targetJavaProject.getProject(),monitor);
	}

	void processContainer(IContainer container,IProject targetProject,IProgressMonitor monitor)
	{
	   try {
		   IResource[] members = container.members();
		   for (IResource member : members)
		   {
		      if (member instanceof IContainer) 
		      {
		    	  processContainer((IContainer)member,targetProject,monitor);
		      }
		      else if (member instanceof IFile)
		      {	
		    	  IFile memberfile = (IFile)member;
		    	  
		    	  IPath relativefilePath = memberfile.getProjectRelativePath();
		    	  String fileName = relativefilePath.toOSString();
		    	 
				  if(fileName.endsWith(".java.color")){
					  if(relativefilePath.segment(0).contains("bin"))
						  continue;
					  IPath targetProjectParth = targetProject.getFullPath();
					  IPath targetPath = targetProjectParth.append(relativefilePath);
					  targetPath = targetPath.removeFileExtension();
					  if(targetPath.getFileExtension().equals("java"))
						targetPath = targetPath.removeFileExtension();
					  targetPath = targetPath.addFileExtension("clr");
					  CIDEcolorFileReader cidecolorfilereader = new CIDEcolorFileReader(memberfile,fmodel,sourceProject,targetProject,monitor);
					  continue;
				  }
				  if(fileName.endsWith(".color"))
					  continue;
				  IResource resouce  = targetProject.findMember(relativefilePath);
		    	  if(resouce==null){
		    		  IFile targetfile = targetProject.getFile(relativefilePath);
		    		  // 没有找到这个文件 可以复制过来
		    		  IFileStore sourcefileLocation = EFS.getLocalFileSystem().getStore(memberfile.getFullPath());
		    		  
		    		  //EFS.getLocalFileSystem().fromLocalFile(memberfile.getFullPath().toFile()).copy(targetfileLocation, EFS.SHALLOW, new SubProgressMonitor(monitor, 0));
		    		 
		    		  if(memberfile.exists()){
		    			  if(!targetfile.exists()){
		    				  IFileStore targetfileLocation = EFS.getLocalFileSystem().getStore(workspaceRoot.append(targetProject.getFullPath()).append(relativefilePath));
		    				  if(!targetfileLocation.getParent().fetchInfo().exists()){
		    					  IPath parentPath = targetfile.getProjectRelativePath().removeLastSegments(1);
		    					  IFolder folder = targetProject.getFolder(parentPath);
		    					  Stack<IFolder>needtobecreated = new Stack<IFolder>();
		    					  if(!folder.exists()){
		    						  needtobecreated.push(folder);
		    						  if(folder.getProjectRelativePath().isRoot())
		    							  continue;
		    						  IPath tmpparentPath = folder.getProjectRelativePath().removeLastSegments(1);
		    						  if(tmpparentPath.isEmpty())
		    							  continue;
		    						  IFolder tempfolder = targetProject.getFolder(tmpparentPath);
		    						  while(!tempfolder.exists()){
		    							  needtobecreated.push(tempfolder);
		    							  tmpparentPath = tempfolder.getProjectRelativePath().removeLastSegments(1);
		    							  if(tmpparentPath.isEmpty())
			    							  break;
		    							  tempfolder = targetProject.getFolder(tmpparentPath);
		    						  }
		    					  }
		    					  while(!needtobecreated.isEmpty()){
		    						  IFolder needtocreate = needtobecreated.pop();
		    						  needtocreate.create(EFS.NONE, true, new SubProgressMonitor(monitor, 0));
		    					  }
		    				  }
		    				  memberfile.copy(targetProject.getFullPath().append(relativefilePath), EFS.SHALLOW, new SubProgressMonitor(monitor, 0));
		    			  }
		    		  }
		    		 
		    	 }
		      }
		   }
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	   
	}
	
	private void configurePackage(IPackageFragment sourcePackage,
			IPackageFragment targetPackage, IProgressMonitor monitor)
			throws CoreException {
		IPath targetProjectParth = targetPackage.getJavaProject().getPath();
		
		for (Object object:sourcePackage.getNonJavaResources()){
			if(object instanceof IFile){
				IFile objectfile = (IFile)object;
				IPath relativefilePath = objectfile.getProjectRelativePath();
				
				String fileName = objectfile.getFullPath().toOSString();
				if(fileName.endsWith(".java.color")){
					//cidecolorfilereader.getCreatedCLRIFile().copy(targetPath, true, monitor);
					continue;
				}
				if(fileName.endsWith(".color")){
					continue;
				}
				
				IPath targetPath = targetProjectParth.append(relativefilePath);
				objectfile.copy(targetPath, true, monitor);
			}
		}
		
		for (ICompilationUnit compUnit : sourcePackage.getCompilationUnits()) {
			if (monitor.isCanceled())
				return;
			IPath path = compUnit.getPath();
			IFile sourceFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			IDocumentProvider provider = new TextFileDocumentProvider();
			provider.connect(sourceFile);
			String contents = provider.getDocument(sourceFile).get();
			targetPackage.createCompilationUnit(compUnit.getElementName(), contents, true, new SubProgressMonitor(monitor, 0));
			monitor.worked(1);
		}
		
		
		
	}
	

	
	private void configurePackageFragementRoot(IJavaProject sourceJavaProject,
			IPackageFragmentRoot sourceRoot, IPackageFragmentRoot targetRoot,
			IProgressMonitor monitor) throws CoreException {
		for (IPackageFragment pkg : sourceJavaProject.getPackageFragments()) {
			if (monitor.isCanceled())
				return;
			if (pkg.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;
			if (!sourceRoot.getPackageFragment(pkg.getElementName()).exists())
				continue;

			IPackageFragment targetPackage = targetRoot.createPackageFragment(
					pkg.getElementName(), true,  new SubProgressMonitor(monitor, 0));
			configurePackage(pkg, targetPackage, monitor);
			if (pkg.getCompilationUnits().length == 0)
				pkg.delete(false,  new SubProgressMonitor(monitor, 0));
		}

	}
	
	
	
	
	private IPackageFragmentRoot copySourceFolder(IPackageFragmentRoot source,
			IJavaProject targetJavaProject) throws CoreException {
		IPackageFragmentRoot result = null;
		if (source.getResource() instanceof IFolder) {
			IPath path = source.getPath().makeAbsolute();
			path = path.removeFirstSegments(1);// remove project
			IFolder folder = targetJavaProject.getProject().getFolder(path);
			folder.create(false, true, null);
			result = targetJavaProject.getPackageFragmentRoot(folder);
		}
		if (source.getResource() instanceof IProject) {
			result = targetJavaProject.getPackageFragmentRoot(targetJavaProject
					.getProject());
		}
		if (result != null) {
			IClasspathEntry[] oldEntries = targetJavaProject.getRawClasspath();
			boolean containsPath = false;
			for (IClasspathEntry entry : oldEntries) {
				if (entry.getPath().equals(result.getPath()))
					containsPath = true;
			}
			if (!containsPath) {
				IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
				System.arraycopy(oldEntries, 0, newEntries, 0,
						oldEntries.length);
				newEntries[oldEntries.length] = JavaCore.newSourceEntry(result
						.getPath());
				targetJavaProject.setRawClasspath(newEntries, null);
			}
		}
		return result;
	}

	private void fixProjectNature(IProject targetProject){
		IProjectDescription description;
		try {
			description = targetProject.getDescription();
			List<String> natures = new ArrayList<String>(Arrays.asList(description.getNatureIds()));
			if (natures.contains(CIDEProjectNature.NATURE_ID)) {
				natures.remove(CIDEProjectNature.NATURE_ID);
				description.setNatureIds(natures.toArray(new String[natures.size()]));
				targetProject.setDescription(description, null);
			}
			if(!natures.contains(LoongProjectNature.NATURE_ID)){
				natures.add(LoongProjectNature.NATURE_ID);
				description.setNatureIds(natures.toArray(new String[natures.size()]));
				targetProject.setDescription(description, null);
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
