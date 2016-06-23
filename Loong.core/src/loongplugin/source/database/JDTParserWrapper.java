package loongplugin.source.database;


import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class JDTParserWrapper {


	public static CompilationUnit parseCompilationUnit(ICompilationUnit compUnit) {

		ASTParser parser = ASTParser.newParser(AST.JLS3);// TODO: find
		parser.setResolveBindings(true);
		parser.setSource(compUnit);
		parser.setStatementsRecovery(false);
		CompilationUnit root = (CompilationUnit) parser.createAST(null);
		return root;
	}

	public static CompilationUnit parseJavaFile(IFile file)  {
		// TODO Auto-generated method stub
		ICompilationUnit compUnit = getICompilationUnit(file);
		if (compUnit == null)
		try {
			throw new Exception("Not a java file");
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}

		return parseCompilationUnit(compUnit);
	}
	
	public static ICompilationUnit getICompilationUnit(IFile file) {
		IJavaProject javaProject = JavaCore.create(file.getProject());
		if (javaProject == null)
			return null;
		return JavaCore.createCompilationUnitFrom(file);
	}

}
