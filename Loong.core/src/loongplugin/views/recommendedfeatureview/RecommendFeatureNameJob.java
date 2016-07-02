package loongplugin.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongplugin.LoongPlugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;

public class RecommendFeatureNameJob extends WorkspaceJob{

	
	private List<IJavaElement> unifiedIJavaElements = new LinkedList<IJavaElement>();
	private List<IJavaElement> ununifiedIJavaElements;
	private FeatureNameDictionary dict = new FeatureNameDictionary();
	 
	public RecommendFeatureNameJob(List<IJavaElement>elements,IProject project) {
		super("Building recommended name list for project:"+project.getName());
		ununifiedIJavaElements = elements;
	}
	public RecommendFeatureNameJob(List<IJavaElement>elements){
		super("Building recommended name list");
		ununifiedIJavaElements = elements;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		// Extract all List into a java file fashion(at least)
		for(IJavaElement element:ununifiedIJavaElements){
			if(element instanceof IPackageFragment){
				ICompilationUnit[]allcompilationUnit = ((IPackageFragment)element).getCompilationUnits();
				if(allcompilationUnit!=null){
					for(ICompilationUnit unit:allcompilationUnit){
						unifiedIJavaElements.add(unit);
					}
				}
			}else if(element instanceof IJavaProject){
				// 获取里面所有的java文件
				IJavaProject projectelement = (IJavaProject)element;
				IPackageFragment[] allfragment = projectelement.getPackageFragments();
				for(IPackageFragment fragment:allfragment){
					ICompilationUnit[]allcompilationUnit = fragment.getCompilationUnits();
					if(allcompilationUnit!=null){
						for(ICompilationUnit unit:allcompilationUnit){
							unifiedIJavaElements.add(unit);
						}
					}
				}
			}else if(element instanceof IPackageFragmentRoot){
				continue;
			}else{
				unifiedIJavaElements.add(element);
			}
		}
		int processUnit = unifiedIJavaElements.size();
		monitor.beginTask("Building recommended name list", processUnit+2);
		for(IJavaElement element:unifiedIJavaElements){
			if(element instanceof IAnnotation){
				monitor.worked(1);
				continue;
			}else if(element instanceof IClassFile){
				monitor.worked(1);
				continue;
			}else if(element instanceof ICompilationUnit){
				ASTParser parser = ASTParser.newParser(LoongPlugin.AST_VERSION);
				parser.setKind( ASTParser.K_COMPILATION_UNIT );
				parser.setResolveBindings( true );
				parser.setBindingsRecovery( true );
				parser.setSource((ICompilationUnit)element);
			    ASTNode rootNode = parser.createAST( null );
			    dict.addDictBuiltElement(((ICompilationUnit)element).getElementName(), element);
			    ASTStringTracker astTracker = new ASTStringTracker(rootNode);
				List<String> recommendfeatureNames = astTracker.getRecommendedFeatureNameList();
				List<String> recommendnonfeatureNames = astTracker.getRecommendedNonFeatureNameList();
				for(String str:recommendfeatureNames){
					dict.addDictBuiltElement(str, element);
				}
				for(String str:recommendnonfeatureNames){
					dict.addAnyElement(str, element);
				}
			}else if(element instanceof IField){
				String name = ((IField)element).getElementName();
				dict.addDictBuiltElement(name, element);
			}else if(element instanceof IImportContainer){
				monitor.worked(1);
				continue;
			}else if(element instanceof IImportDeclaration){
				String name = ((IImportDeclaration)element).getElementName();
				dict.addDictBuiltElement(name, element);
			}else if(element instanceof IInitializer){
				monitor.worked(1);
				continue;
			}else if(element instanceof IJavaModel){
				monitor.worked(1);
				continue;
			}else if(element instanceof IJavaProject){
				monitor.worked(1);
				continue;
			}else if(element instanceof ILocalVariable){
				String name = ((ILocalVariable)element).getElementName();
				dict.addDictBuiltElement(name, element);
			}else if(element instanceof IMember){
				monitor.worked(1);
				continue;
			}else if(element instanceof IMethod){
				String methodname = ((IMethod)element).getElementName();
				ICompilationUnit unit = ((IMethod)element).getCompilationUnit();
				dict.addDictBuiltElement(methodname, element);
				MethodDeclaration methodDecl = convertIMethodToMethodDecl((IMethod)element,unit);
				ASTStringTracker astTracker = new ASTStringTracker(methodDecl);
				List<String> recommendfeatureNames = astTracker.getRecommendedFeatureNameList();
				List<String> recommendnonfeatureNames = astTracker.getRecommendedNonFeatureNameList();
				for(String str:recommendfeatureNames){
					dict.addDictBuiltElement(str, element);
				}
				for(String str:recommendnonfeatureNames){
					dict.addAnyElement(str, element);
				}
			}else if(element instanceof IPackageDeclaration){
				String name = ((IPackageDeclaration)element).getElementName();
				dict.addDictBuiltElement(name, element);
			}else if(element instanceof IPackageFragment){
				monitor.worked(1);
				continue;
			}else if(element instanceof IPackageFragmentRoot){
				monitor.worked(1);
				continue;
			}else if(element instanceof IType){
				String name = ((IType)element).getElementName();
				dict.addDictBuiltElement(name, element);
			}else if(element instanceof ITypeParameter){
				monitor.worked(1);
				continue;
			}else if(element instanceof ITypeRoot){
				String name = (((ITypeRoot)element).findPrimaryType()).getElementName();
				dict.addDictBuiltElement(name, element);
			}
			monitor.worked(1);
		}
		// build dictionary
		
		
		monitor.worked(1);
		// extract name
		
		monitor.done();
		return Status.OK_STATUS;
	}
	
	
	
	public MethodDeclaration convertIMethodToMethodDecl(IMethod method,ICompilationUnit unit){
		MethodDeclaration methodDecl = null;
		ASTParser parser = ASTParser.newParser(LoongPlugin.AST_VERSION);
		parser.setKind( ASTParser.K_COMPILATION_UNIT );
		parser.setResolveBindings( true );
		parser.setBindingsRecovery( true );
		parser.setSource(unit);
	    final ASTNode rootNode = parser.createAST( null );

	    final CompilationUnit compilationUnitNode = (CompilationUnit) rootNode;

	    final String key = method.getKey();
	    ASTNode javaElement=null;
	    if(method.isResolved()){
	    	javaElement = compilationUnitNode.findDeclaringNode( key );
	    }else{
	    	try {
				javaElement= NodeFinder.perform(rootNode, method.getSourceRange());
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    methodDecl = (MethodDeclaration) javaElement;
		
		return methodDecl;
	}
	

	public void processContainer(IContainer container,final Set<IFile>allContainedFile)
	{
		IResource[] members = null;
		try {
			members = container.members();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   for (IResource member : members)
	   {
		   	if (member instanceof IContainer) 
	       	{
	    	  	processContainer((IContainer)member,allContainedFile);
	       	}
	      	else if (member instanceof IFile)
	      	{
	      		allContainedFile.add((IFile)member);
	      	}
	   }
	}
}
