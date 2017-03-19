package loongplugin.recommendation.typesystem.typing.jdt.checks;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;



/**
 * checks colors of an exception in throws clause (between method declaration
 * and inherited method ). as necessary, throws according to the strategy an
 * error that overriding relationship is broken
 * 
 * @author adreilin
 * 
 */
public class InheritedMethodExceptionCheck extends AbstractJDTTypingCheck {

	private final String excName;
	private final List<String> exceptionKeys;

	public InheritedMethodExceptionCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source, String excName,
			List<String> excepKeys) {

		super(file, typingProvider, source);

		this.exceptionKeys = excepKeys;
		this.excName = excName;

	}

	public boolean evaluate(IEvaluationStrategy strategy) {

		// checks "OR" condition for all found keys
		for (String tmpKey : exceptionKeys) {

			// checks for each overridden method the implies condition
			if (strategy.implies(file.getFeatureModel(), file.getColorManager()
					.getColors(source), typingProvider.getBindingColors()
					.getColors(tmpKey)))
				return true;

		}

		return false;
	}

	public String getErrorMessage() {
		return "Exception "
				+ excName
				+ "  is not compatible with throws clause of overridden method in some variants";
	}

	public String getProblemType() {
		return "loong.typing.jdt.methodexceptionimplementation";
	}

}
