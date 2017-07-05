package loongplugin.utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.SourceFileColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;



public class SetCompUnitColorJob extends WorkspaceJob{

	private Collection<IResource> resources;

	private Set<Feature> features;

	private Set<Feature> removedfeatures;
	
	public SetCompUnitColorJob(List<IResource> resources, Set<Feature> features, Set<Feature> removedfeatures) {
		// TODO Auto-generated constructor stub
		super("Set Resource Colors");
		this.resources = resources;
		this.removedfeatures = removedfeatures;
		this.features = features;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		
		monitor.beginTask("coloring", resources.size());
		for (IResource resource : resources) {
			if (resource instanceof IFile) {
				IFile file = (IFile)resource;
				String fileExt = file.getFileExtension();
				if(fileExt.equals("java")){
					ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
					ASTParser parser = ASTParser.newParser(AST.JLS3);
					parser.setSource(compilationUnit);
					CompilationUnit result = (CompilationUnit) parser.createAST(null);
					CLRAnnotatedSourceFile clrFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(compilationUnit);
					SourceFileColorManager colormanager = (SourceFileColorManager) clrFile.getColorManager();
					//收集所有的AST节点
					// Set<ASTNode> allastNodes = EmbeddedASTNodeCollector.collectASTNodes(compilationUnit);
					
					colormanager.beginBatch();
					/*
					for(Feature f:features){
						for(ASTNode node:allastNodes){
							if(node instanceof MethodDeclaration||
									node instanceof CompilationUnit){
								colormanager.addColor(node, f);
								f.addASTNodeToFeature(compilationUnit, node);
							}
						}
					}
					*/
					
					for(Feature f:features){
						colormanager.addColor(result,f);
						f.addASTNodeToFeature(compilationUnit, result);
					}
					
					colormanager.endBatch();
				}
			}
			if (resource instanceof IFolder || resource instanceof IProject) {
				IContainer container = (IContainer) resource;
				IResource[] resouces = container.members();
				for(IResource nestedresource:resouces){
					if (nestedresource instanceof IFile) {
						IFile file = (IFile)nestedresource;
						String fileExt = file.getFileExtension();
						if(fileExt.equals("java")){
							ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
							ASTParser parser = ASTParser.newParser(AST.JLS3);
							parser.setSource(compilationUnit);
							CompilationUnit result = (CompilationUnit) parser.createAST(null);
							CLRAnnotatedSourceFile clrFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(compilationUnit);
							SourceFileColorManager colormanager = (SourceFileColorManager) clrFile.getColorManager();
							//收集所有的AST节点
							Set<ASTNode> allastNodes = EmbeddedASTNodeCollector.collectASTNodes(compilationUnit);
							
							colormanager.beginBatch();
							for(Feature f:features){
								for(ASTNode node:allastNodes){
									colormanager.addColor(node, f);
									f.addASTNodeToFeature(compilationUnit, node);
								}
							}
							colormanager.endBatch();
						}
					}
				}
			}
			monitor.worked(1);
		}
		
		
		monitor.done();

		return Status.OK_STATUS;
	}

}
