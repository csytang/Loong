package loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions;

import loongplugin.source.database.JDTParserWrapper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;



/**
 * to resolve a binding, we need to parse (JDT) the target file and find the
 * according node, then convert it back to an IASTNode
 */
public class ASTBindingFinderHelper {

	public static ASTNode getFieldDecl(IBinding binding) {

		CompilationUnit ast = getAST(binding);
		if (ast == null)
			return null;

		ASTBindingFinder bindingFinder = new ASTBindingFinder(binding.getKey());
		ast.accept(bindingFinder);
		ASTNode result = bindingFinder.getResult();
		if (result == null)
			return null;

		return result;
	}

	public static ASTNode getMethodDecl(IMethodBinding binding) {
		CompilationUnit ast = getAST(binding);
		if (ast == null)
			return null;

		ASTBindingFinder bindingFinder = new ASTBindingFinder(binding.getKey());
		ast.accept(bindingFinder);
		ASTNode result = bindingFinder.getResult();
		if (result == null)
			return null;

		return result;
	}

	public static ASTNode getTypeDecl(ITypeBinding binding) {
		CompilationUnit ast = getAST(binding);
		if (ast == null)
			return null;

		ASTBindingFinder bindingFinder = new ASTBindingFinder(binding.getKey());
		ast.accept(bindingFinder);
		ASTNode result = bindingFinder.getResult();
		if (result == null)
			return null;

		return result;
	}

	private static CompilationUnit getAST(IBinding binding) {
		IJavaElement element = binding.getJavaElement();
		if (element == null)
			return null;
		IResource res = element.getResource();
		if (!(res instanceof IFile))
			return null;

		
		
			return JDTParserWrapper.parseJavaFile((IFile) res);
		
		

	}

}
