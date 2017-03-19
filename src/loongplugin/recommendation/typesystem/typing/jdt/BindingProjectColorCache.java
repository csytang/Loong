package loongplugin.recommendation.typesystem.typing.jdt;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.source.database.JDTParserWrapper;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;



/**
 * this class caches the assignment of colors to JavaElement features.
 * 
 * otherwise only nodes inside one file are colored. but to check bindings it is
 * necessary to know the colors of the target methods in other classes. to not
 * need to read the colors for the whole ast of the target classes, this
 * JavaElementColorManager caches the colors for all elements that are potential
 * targets for bindings.
 * 
 * the data is stored between eclipse runs and can be updated asynchronously
 * with a builder.
 * 
 * @author cKaestner
 * 
 */
public class BindingProjectColorCache implements Serializable {

	private static final long serialVersionUID = 3L;

	private final HashMap<String, Set<Feature>> bindingKeys2colors = new HashMap<String, Set<Feature>>();

	private IJavaProject project;

	public BindingProjectColorCache(IJavaProject project) {
		this.project = project;
	}

	/**
	 * called after an item's color is changed. cycles through all children an
	 * searches for java elements that need to be updated.
	 * 
	 * @param nodes
	 * @param file
	 */
	void updateASTColors(ASTNode node, final CLRAnnotatedSourceFile file) {
		node.accept(new ASTVisitor() {
			public boolean visit(MethodDeclaration node) {
				String key = null;
				IMethodBinding binding = node.resolveBinding();
				if (binding != null) {
					IJavaElement javaElement = binding.getJavaElement();
					if (javaElement instanceof IMethod)
						key = ((IMethod) javaElement).getKey();

				}
				if (key != null) {
					Set<Feature> colors = getColor(file, node);
					update(bindingKeys2colors, key, colors);

					// add param keys
					for (int paramIdx = 0; paramIdx < node.parameters().size(); paramIdx++) {
						ASTNode param = (ASTNode) node.parameters().get(
								paramIdx);

						update(bindingKeys2colors, getParamKey(key, paramIdx),
								getColor(file, param));

					}

					// add exception keys
					for (int excIdx = 0; excIdx < node.thrownExceptions()
							.size(); excIdx++) {
						Name exception = (Name) node.thrownExceptions().get(
								excIdx);

						ITypeBinding excBinding = exception
								.resolveTypeBinding();

						if (excBinding == null)
							continue;

						update(bindingKeys2colors, getExceptionKey(key,
								excBinding.getKey()), getColor(file, exception));

					}

				}
				return super.visit(node);
			}

			private Set<Feature> getColor(final CLRAnnotatedSourceFile file,
					ASTNode node) {
				return file.getColorManager().getColors(node);
			}

			private void update(HashMap<String, Set<Feature>> map, String key,
					Set<Feature> colors) {

				if (colors != null && colors.size() > 0)
					map.put(key, colors);
				else
					map.remove(key);
			}

			public boolean visit(VariableDeclarationFragment node) {
				visitVD(node);
				return super.visit(node);
			}

			public boolean visit(SingleVariableDeclaration node) {
				visitVD(node);
				return super.visit(node);
			}

			public void visitVD(VariableDeclaration node) {
				String key = null;
				IVariableBinding binding = node.resolveBinding();
				if (binding != null) {
					IJavaElement javaElement = binding.getJavaElement();
					if (javaElement instanceof IField)
						key = ((IField) javaElement).getKey();
				}
				if (key != null)
					update(bindingKeys2colors, key, getColor(file, node));
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				ITypeBinding binding = node.resolveBinding();
				if (binding != null) {
					update(bindingKeys2colors, binding.getKey(), getColor(file,
							node));

				}
				return super.visit(node);
			}
		});
	}

	private final static Set<Feature> NOCOLORS = Collections.EMPTY_SET;

	public Set<Feature> getColors(String bindingKey) {
		Set<Feature> colors = bindingKeys2colors.get(bindingKey);
		if (colors != null)
			return colors;
		return NOCOLORS;
	}

	public Set<Feature> getColors(IMethodBinding method) {
		return getColors(method.getKey());
	}

	public Set<Feature> getColors(IVariableBinding field) {
		return getColors(field.getKey());
	}

	// colors for a parameter
	public Set<Feature> getColors(IMethodBinding method, int paramIdx) {
		return getColors(getParamKey(method.getKey(), paramIdx));
	}

	// colors for a parameter
	public Set<Feature> getColors(String methodKey, int paramIdx) {
		return getColors(getParamKey(methodKey, paramIdx));
	}

	public static String getParamKey(String methodKey, int paramIdx) {
		return methodKey + "/" + paramIdx;
	}

	public static String getExceptionKey(String methodKey, String exceptionKey) {
		return methodKey + "/" + exceptionKey;
	}

	public Set<Feature> getColors(ITypeBinding type) {
		return getColors(type.getKey());

	}

	/**
	 * rebuilds the cache for the entire java project
	 * 
	 * @throws JavaModelException
	 * @throws FeatureModelNotFoundException
	 */
	public void rebuildEntireProject() throws JavaModelException,
			FeatureModelNotFoundException {
		bindingKeys2colors.clear();
		for (IPackageFragment packageFragment : project.getPackageFragments())
			for (ICompilationUnit compilationUnit : packageFragment
					.getCompilationUnits()) {
				if (!(compilationUnit.getResource() instanceof IFile))
					continue;

				CLRAnnotatedSourceFile file = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile
						.getColoredJavaSourceFile((IFile) compilationUnit
								.getResource());

				rebuildFile(compilationUnit, file);
			}
	}

	public void clear() {
		bindingKeys2colors.clear();
	}

	protected void rebuildFile(ICompilationUnit compilationUnit,
			CLRAnnotatedSourceFile file) {
		
			CompilationUnit ast = JDTParserWrapper
					.parseCompilationUnit(compilationUnit);
			updateASTColors(ast, file);
		
	}

}
