package loongplugin.recommendation.typesystem.typing.jdt.checks;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.utils.MethodPathItem;



/**
 * checks colors between method declaration and inherited methods. as necessary,
 * throws an error according to the strategy that overriding relationship is
 * changed in some variants
 * 
 * @author adreilin
 * 
 */
public class InheritedMethodCheck extends AbstractJDTTypingCheck {

	private final String name;
	private final MethodPathItem inherMethod;
	private final List<ASTNode> paramList;

	public InheritedMethodCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			List<ASTNode> paramList, String name, MethodPathItem inherMethod) {

		super(file, typingProvider, source);

		this.inherMethod = inherMethod;
		this.name = name;
		this.paramList = paramList;

	}

	public boolean evaluate(IEvaluationStrategy strategy) {

		// checks colors for method name
		if (!strategy.equal(file.getFeatureModel(), typingProvider
				.getBindingColors().getColors(inherMethod.getKey()), file
				.getColorManager().getColors(source)))
			return false;

		// PARAM CHECK
		for (int j = 0; j < paramList.size(); j++) {

			if (!strategy.equal(file.getFeatureModel(), typingProvider
					.getBindingColors().getColors(
							inherMethod.getInheritedParamKeys().get(j)), file
					.getColorManager().getColors(paramList.get(j))))
				return false;

		}

		return true;

	}

	public String getErrorMessage() {
		return "Overriding Relationship of " + name
				+ " is changed in some variants.";
	}

	public String getProblemType() {
		return "loong.typing.jdt.methodnameimplementation";
	}

	@Override
	public Severity getSeverity() {
		return Severity.WARNING;
	}

}




