package loongplugin.recommendation.typesystem.typing.jdt.checks;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;



/**
 * checks colors between a field and references to it
 * 
 * @author ckaestne
 * 
 */
public class TypeReferenceCheck extends AbstractJDTTypingCheck {

	private final ITypeBinding target;

	public TypeReferenceCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			ITypeBinding target) {
		super(file, typingProvider, source);
		this.target = target;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		return strategy.implies(file.getFeatureModel(), file.getColorManager()
				.getColors(source), typingProvider.getBindingColors()
				.getColors(target));
	}

	public String getErrorMessage() {
		return "Referencing type which is not present in some variants: "
				+ target.getName();
	}

	public String getProblemType() {
		return "loong.typing.jdt.typereference";
	}

}
