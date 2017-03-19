package loongplugin.recommendation.typesystem.typing.jdt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.recommendation.typesystem.typing.jdt.model.AbstractFileBasedTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingProvider;
import loongplugin.source.database.JDTParserWrapper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;



public class JDTTypingProvider extends AbstractFileBasedTypingProvider
		implements ITypingProvider {

	public JDTTypingProvider(IProject project) {
		super(project);
		bindingColorCache = new BindingProjectColorCache(JavaCore
				.create(project));
	}

	public void updateAll(IProgressMonitor monitor) {
		getBindingColors().clear();
		super.updateAll(monitor);
	}

	private final BindingProjectColorCache bindingColorCache;

	public BindingProjectColorCache getBindingColors() {
		return bindingColorCache;
	}

	@Override
	protected boolean matchFileForUpdate(CLRAnnotatedSourceFile coloredSourceFile) {
		return coloredSourceFile != null;
	}

	@Override
	protected Set<ITypingCheck> checkFile(CLRAnnotatedSourceFile file) {
		IFile resource = file.getResource();// update to java sourcefile
		CompilationUnit e_ast;
		
		e_ast = JDTParserWrapper.parseJavaFile(resource);
		

		// when (re)parsing a file, update the color cache
		getBindingColors().updateASTColors(e_ast, file);

		// generate all necessary checks
		Set<ITypingCheck> result = new HashSet<ITypingCheck>();
		e_ast.accept(new JDTCheckGenerator(file, this, result));

		return result;
	}

	private void updateFileColorCache(Collection<CLRAnnotatedSourceFile> files) {
		for (CLRAnnotatedSourceFile file : files) {
			if (file.getProject() != getProject())
				continue;
			if (!matchFileForUpdate(file))
				continue;

			IJavaElement javaElement = JavaCore.create(file.getResource());
			if (javaElement instanceof ICompilationUnit)
				getBindingColors().rebuildFile((ICompilationUnit) javaElement,
						file);
		}
	}

	public void prepareReevaluation(Collection<CLRAnnotatedSourceFile> files,
			IProgressMonitor monitor) {
		updateFileColorCache(files);
	}

	public void prepareReevaluationAll(IProgressMonitor monitor) {
		try {
			getBindingColors().rebuildEntireProject();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeatureModelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
