package loongplugin.recommendation.typesystem.typing.jdt.checks;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;

import org.eclipse.jdt.core.dom.ASTNode;



/**
 * checks colors between a local type reference in a file and the import
 * declaration
 * 
 * @author ckaestne
 * 
 */
public class TypeImportedCheck extends AbstractJDTTypingCheck {

	private final ASTNode targetImportDeclaration;
	private final String name;

	public TypeImportedCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			ASTNode targetImportDeclaration, String name) {
		super(file, typingProvider, source);
		this.targetImportDeclaration = targetImportDeclaration;
		this.name = name;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		return strategy.implies(file.getFeatureModel(), file.getColorManager()
				.getColors(source), file.getColorManager().getColors(
				targetImportDeclaration));
	}

	public String getErrorMessage() {
		return "Type used for which the import declaration is not present in some variants: "
				+ name;
	}

	public String getProblemType() {
		return "loong.typing.jdt.importtypereference";
	}

}
