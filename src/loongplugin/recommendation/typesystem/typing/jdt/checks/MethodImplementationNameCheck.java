package loongplugin.recommendation.typesystem.typing.jdt.checks;

import java.util.List;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.utils.MethodPathItem;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;



/**
 * checks colors between method declaration / implementation and related or
 * rather inherited (abstract) method declarations in interfaces and super
 * classes. as necessary, throws according to the strategy an error that method
 * is not implemented in some variants.
 * 
 * @author adreilin
 * 
 */
public class MethodImplementationNameCheck extends AbstractJDTTypingCheck {

	private final List<MethodPathItem> inherMethods;
	private final String name;

	public MethodImplementationNameCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			IMethodBinding sourceBinding, List<MethodPathItem> inherMethods) {
		super(file, typingProvider, source);
		this.inherMethods = inherMethods;
		this.name = sourceBinding.getName();
	}

	public boolean evaluate(IEvaluationStrategy strategy) {

		// checks "AND" condition for all found methods
		for (MethodPathItem tmpItem : inherMethods) {

			if (!tmpItem.isDeclaringClassAbstract())
				return true;

			// check relationship between each declaring and implemented method
			if (strategy.implies(file.getFeatureModel(), typingProvider
					.getBindingColors().getColors(tmpItem.getKey()), file
					.getColorManager().getColors(source)))
				continue;

			// we have found one overriden method for which "target -> source"
			// is false

			// checks if current item is abstract
			if (tmpItem.isAbstract())
				// check failed
				return false;
			else
				// another method implementation exists
				return true;

		}

		return true;

	}

	public String getErrorMessage() {
		return "Declaring method "
				+ name
				+ " does not implement inherited abstract methods in some variants";
	}

	public String getProblemType() {
		return "loong.typing.jdt.methodimplementationname";
	}

}
