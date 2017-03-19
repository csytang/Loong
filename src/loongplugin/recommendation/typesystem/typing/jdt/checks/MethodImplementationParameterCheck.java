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
 * checks colors of parameters between method declaration / implementation and
 * related or rather inherited (abstract) method declarations in interfaces and
 * super classes. as necessary, throws according to the strategy an error that
 * method is not implemented in some variants.
 * 
 * @author adreilin
 * 
 */
public class MethodImplementationParameterCheck extends AbstractJDTTypingCheck {

	private final int paramIndex;
	private final List<MethodPathItem> inherMethods;
	private final String name;

	public MethodImplementationParameterCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			IMethodBinding methodBinding, int paramIndex,
			List<MethodPathItem> inherMethods) {
		super(file, typingProvider, source);
		this.paramIndex = paramIndex;
		this.inherMethods = inherMethods;
		this.name = methodBinding.getName();
	}

	public boolean evaluate(IEvaluationStrategy strategy) {

		// checks "AND" condition for all found methods
		for (MethodPathItem tmpItem : inherMethods) {

			if (!tmpItem.isDeclaringClassAbstract())
				return true;

			if (strategy.equal(file.getFeatureModel(), typingProvider
					.getBindingColors().getColors(
							tmpItem.getInheritedParamKeys().get(paramIndex)),
					file.getColorManager().getColors(source)))
				continue;

			// we have found one overridden method for which "target -> source"
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
				+ "does not implement inherited abstract methods in some variants. "
				+ "Check param list.";
	}

	public String getProblemType() {
		return "loong.typing.jdt.methodimplementationparameter";
	}

}
