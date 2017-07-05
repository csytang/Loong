package loongplugin.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import loongplugin.CIDEbridge.CIDEASTNodeCollector;
import loongplugin.color.ColorManager;
import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.SourceFileColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.seeds.SeedsXMLWriter;

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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.xml.sax.SAXException;

public class LegacyAnnotationCaptureJob extends WorkspaceJob {
	
	private IProject project;
	private static boolean hasbeencaptured = false;
	private FeatureModel fmodel;
	private ColorManager clrmanager;
	private Set<ASTNode> compilationASTNodeSet = new HashSet<ASTNode>();
	private Map<String,ASTNode> tempASTIDcache = new HashMap<String, ASTNode>();
	private Set<String> tempASTIDs = new HashSet<String>();
	private static final long serialVersionUID = 1L;
	private SeedsXMLWriter aseedwriter;
	
	public LegacyAnnotationCaptureJob(IProject currentProject,SeedsXMLWriter seedwriter){
		super("Capture Legacy infomormation in project:"+currentProject.getName());
		this.project = currentProject;
		fmodel = FeatureModelManager.getInstance(currentProject).getFeatureModel();
		aseedwriter = seedwriter;
		hasbeencaptured = true;
	}
	
	
	public static boolean getCaptureStatus(){
		return hasbeencaptured;
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
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		IJavaProject sourceJavaProject = JavaCore.create(project);
		int compUnitCount = countJavaProject(sourceJavaProject);
		monitor.beginTask("Capture Legacy information in project", compUnitCount);	
		processContainer(project,monitor);
		FeatureModelManager.getInstance().setFeatureModel(fmodel);
		try {
			aseedwriter.seedswrite();
		} catch (SAXException | ParserConfigurationException
				| TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			    	  if(extension==null)
			    		  continue;
					  if(extension.equals("clr")){
						  CLRAnnotatedSourceFile clrfile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(memberfile);
						  SourceFileColorManager compUnitColorManager = (SourceFileColorManager)clrfile.getColorManager();
						  HashMap<ASTID, Set<Feature>> astIDFeature = compUnitColorManager.getNode2Colors();
						 
						  
						  // collect astnodes for this file
						  IPath javafilePath = relativefilePath.removeFileExtension().addFileExtension("java");
						  IFile javaFile = project.getFile(javafilePath);
						  if(!javaFile.exists()){
							  monitor.worked(1);
							  continue;
						  }
						  ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(javaFile);
						  compilationASTNodeSet = EmbeddedASTNodeCollector.collectASTNodes(compilationUnit);
						  // compute ASTID
						  computeASTID();
						  synchronizeASTNodeWithColorAnnotation(astIDFeature,compUnitColorManager,compilationUnit);
						//  compUnitColorManager.setNode2Colors(astIDFeature);
					  }
					  monitor.worked(1);
			      }
			     
			   }
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		   
	}
	
	
	private void synchronizeASTNodeWithColorAnnotation(Map<ASTID, Set<Feature>> astIDFeature,SourceFileColorManager manager,ICompilationUnit unit) {
		// TODO Auto-generated method stub
		/*
		 * 
		 */
		Map<ASTID, Set<Feature>> astIDFeature_copy = new HashMap<ASTID,Set<Feature>>(astIDFeature);
		for(Map.Entry<ASTID, Set<Feature>>entry:astIDFeature_copy.entrySet()){
			ASTID id = entry.getKey();
			Set<Feature> featureassociated = astIDFeature.get(id);
			ASTNode astNode = tempASTIDcache.get(id.id);
			if(astNode==null)
				continue;
			for(Feature f:featureassociated){
				Feature finModel = fmodel.getFeatureById(f.getId());
				finModel.addASTNodeToFeature(unit,astNode);
				manager.addColor(astNode, f);
			}		
		}
		
	}


	public void collectASTNodes(ICompilationUnit unit){
		compilationASTNodeSet.clear();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(unit);
		CompilationUnit result = (CompilationUnit) parser.createAST(null);
		Set<ASTNode> astnodes = new HashSet<ASTNode>();
		CIDEASTNodeCollector cidecollector = new CIDEASTNodeCollector();
		result.accept(cidecollector);
		astnodes = cidecollector.getASTNodeSet();
		compilationASTNodeSet.addAll(astnodes);
	}
	
	public void computeASTID(){
		tempASTIDcache.clear();
		tempASTIDs.clear();
		for(ASTNode node:compilationASTNodeSet){
			String nodeastID = ASTID.calculateId(node).toString();
			tempASTIDcache.put(nodeastID, node);
			tempASTIDs.add(nodeastID);
		}
	}
	
	
}
