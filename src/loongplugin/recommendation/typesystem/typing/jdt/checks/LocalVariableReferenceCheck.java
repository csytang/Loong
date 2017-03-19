package loongplugin.recommendation.typesystem.typing.jdt.checks;

import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;



/**
 * checks colors between a local type reference in a file and the import
 * declaration
 * 
 * @author ckaestne
 * 
 */
public class LocalVariableReferenceCheck extends AbstractJDTTypingCheck {

	private final ASTNode targetVariableDeclaration;
	private final String name;

	public LocalVariableReferenceCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			ASTNode targetImportDeclaration, String variableName) {
		super(file, typingProvider, source);
		this.targetVariableDeclaration = targetImportDeclaration;
		this.name = variableName;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		return strategy.implies(file.getFeatureModel(), file.getColorManager()
				.getColors(source), file.getColorManager().getColors(
				targetVariableDeclaration));
	}

	public String getErrorMessage() {
		return "Variable used which is not present in some variants: " + name;
	}

	public String getProblemType() {
		return "loong.typing.jdt.localvariablereference";
	}

}
