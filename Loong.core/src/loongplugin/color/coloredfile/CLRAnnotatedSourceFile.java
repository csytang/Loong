package loongplugin.color.coloredfile;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.utils.ASTCreator;



public class CLRAnnotatedSourceFile  implements IColoredJavaSourceFile {

	protected final IFile colorFile;// .clr file

	protected final ICompilationUnit compilationUnit;

	protected WeakReference<CompilationUnit> astRef = null;

	protected IColorManager colorManager = null;

	
	protected CLRAnnotatedSourceFile(IFile colorFile) {
		this.colorFile = colorFile;
		this.compilationUnit = getCompilationUnit(getJavaFile(colorFile));
		
	}

	protected CLRAnnotatedSourceFile(ICompilationUnit compUnit) {
		this.compilationUnit = compUnit;
		this.colorFile = getColorFile(getResource(compUnit));
	}

	/* (non-Javadoc)
	 * @see coloredide.features.source.IColoredJavaSourceFile#getAST()
	 */
	public CompilationUnit getAST() throws JavaModelException, CoreException {
		return getAST(new ASTCreator());
	}

	/* (non-Javadoc)
	 * @see coloredide.features.source.IColoredJavaSourceFile#getAST(coloredide.utils.ASTCreator)
	 */
	public CompilationUnit getAST(ASTCreator creator)
			throws JavaModelException, CoreException {
		if (astRef != null) {
			CompilationUnit r = astRef.get();
			if (r != null)
				return r;
		}

		CompilationUnit r = creator.createAST(compilationUnit);
		astRef = new WeakReference<CompilationUnit>(r);
		return r;
	}

	/* (non-Javadoc)
	 * @see coloredide.features.source.IColoredJavaSourceFile#refreshAST()
	 */
	public void refreshAST() {
		astRef = null;
	}

	/* (non-Javadoc)
	 * @see coloredide.features.source.IColoredJavaSourceFile#getCompilationUnit()
	 */
	public ICompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public static ICompilationUnit getCompilationUnit(IFile javaFile) {
		if (!javaFile.exists())
			return null;
		return JavaCore.createCompilationUnitFrom(javaFile);
	}

	public static CompilationUnit getASTRoot(ASTNode node) {
		return (CompilationUnit) node.getRoot();
	}

	public static ICompilationUnit getCompilationUnit(CompilationUnit astRoot) {
		return (ICompilationUnit) astRoot.getJavaElement();
	}

	public static IFile getResource(ICompilationUnit cu) {
		IPath path = cu.getPath();
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}

	public IFile getResource() {
		return getJavaFile(colorFile);
	}
	
	public static IFile getResource(ASTNode node) {
		return getResource(getCompilationUnit(getASTRoot(node)));
	}

	public int hashCode() {
		return colorFile.hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof CLRAnnotatedSourceFile))
			return false;
		return colorFile.equals(((CLRAnnotatedSourceFile) obj).colorFile);
	}

	/* (non-Javadoc)
	 * @see coloredide.features.source.IColoredJavaSourceFile#getColorManager()
	 */
	public IColorManager getColorManager() {
		if (colorManager == null) {
			colorManager = new CompilationUnitColorManager(colorFile, this);
		}
		return colorManager;
	}

	private static final WeakHashMap<IFile, WeakReference<CLRAnnotatedSourceFile>> fileCache = new WeakHashMap<IFile, WeakReference<CLRAnnotatedSourceFile>>();

	private static final WeakHashMap<ICompilationUnit, WeakReference<CLRAnnotatedSourceFile>> compUnitCache = new WeakHashMap<ICompilationUnit, WeakReference<CLRAnnotatedSourceFile>>();

	private static void cache(CLRAnnotatedSourceFile sourceFile) {
		WeakReference<CLRAnnotatedSourceFile> r = new WeakReference<CLRAnnotatedSourceFile>(
				sourceFile);
		fileCache.put(sourceFile.colorFile, r);
		compUnitCache.put(sourceFile.compilationUnit, r);
	}

	public static IColoredJavaSourceFile getColoredJavaSourceFile(IFile colorFile) {
		//colorFile should be .clr
		CLRAnnotatedSourceFile cachedCJSF = null;
		WeakReference<CLRAnnotatedSourceFile> r = fileCache.get(colorFile);
		if (r != null)
			cachedCJSF = r.get();
		if (cachedCJSF == null) {
			cachedCJSF = new CLRAnnotatedSourceFile(colorFile);
			cache(cachedCJSF);
		}
		return cachedCJSF;
	}

	/**
	 * returns whether a source object is available for the unit. for processes
	 * that must not create the source object if not created yet.
	 * 
	 * @param compUnit
	 * @return
	 */
	public static boolean existsColoredJavaSourceFile(ICompilationUnit compUnit) {
		return compUnitCache.get(compUnit) != null;
	}

	public static IColoredJavaSourceFile getColoredJavaSourceFile(ICompilationUnit compUnit) {
		CLRAnnotatedSourceFile cachedCJSF = null;
		WeakReference<CLRAnnotatedSourceFile> r = compUnitCache.get(compUnit);
		if (r != null)
			cachedCJSF = r.get();
		if (cachedCJSF == null) {
			cachedCJSF = new CLRAnnotatedSourceFile(compUnit);
			cache(cachedCJSF);
		}
		return cachedCJSF;
	}

	protected static IFile getJavaFile(IFile colorFile) {
		IPath javaFilePath = colorFile.getFullPath().removeFileExtension()
				.addFileExtension("java");
		return ResourcesPlugin.getWorkspace().getRoot().getFile(javaFilePath);
	}
	//DEBUG
	public IFile getColorFile(){
		return colorFile;
	}

	protected static IFile getColorFile(IFile javaFile) {
		IPath colorFilePath = javaFile.getFullPath().removeFileExtension()
				.addFileExtension("clr");
		return ResourcesPlugin.getWorkspace().getRoot().getFile(colorFilePath);
	}

	/* (non-Javadoc)
	 * @see coloredide.features.source.IColoredJavaSourceFile#getProject()
	 */
	public IProject getProject() {
		return colorFile.getProject();
	}

	/* (non-Javadoc)
	 * @see coloredide.features.source.IColoredJavaSourceFile#hasColors()
	 */
	public boolean hasColors() {
		return getColorManager().hasColors();
	}

	public FeatureModel getFeatureModel() {
		// TODO Auto-generated method stub
		
		return FeatureModelManager.getInstance(colorFile.getProject()).getFeatureModel();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return colorFile.getFullPath().toOSString();
	}

	

	

	
}
