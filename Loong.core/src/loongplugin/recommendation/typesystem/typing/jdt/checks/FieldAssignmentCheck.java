package loongplugin.recommendation.typesystem.typing.jdt.checks;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;


/**
 * checks colors between a field and references to it
 * 
 * @author ckaestne & adreilin
 * 
 */
public class FieldAssignmentCheck extends AbstractJDTTypingCheck {

	private final IVariableBinding targetField;

	public FieldAssignmentCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			IVariableBinding target) {
		super(file, typingProvider, source);
		this.targetField = target;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		return strategy.implies(file.getFeatureModel(), typingProvider
				.getBindingColors().getColors(targetField), file
				.getColorManager().getColors(source));
	}

	public String getErrorMessage() {
		return "Final field " + targetField.getName()
				+ "  is not intialized in some variants.";
	}

	public String getProblemType() {
		return "loong.typing.jdt.finalfieldassignment";
	}

	// @Override
	// protected void addResolutions(
	//
	// ArrayList<ITypingMarkerResolution> resolutions,
	// HashSet<IFeature> colorDiff) {
	// resolutions
	// .addAll(createChangeNodeColorResolution(
	// findCallingStatement(source), colorDiff, true,
	// "statement", 20));
	// resolutions.addAll(createChangeNodeColorResolution(
	// findCallingMethod(source), colorDiff, true, "method", 18));
	// resolutions.addAll(createChangeNodeColorResolution(
	// findCallingType(source), colorDiff, true, "type", 16));
	//
	// // add resolution for target (field declaration)
	// IASTNode fieldDecl = ASTBindingFinderHelper.getFieldDecl(targetField);
	// if (fieldDecl != null)
	// resolutions.addAll(createChangeNodeColorResolution(fieldDecl,
	// colorDiff, false, "field declaration", 14));
	//					
	// }
	//
	// @Override
	// protected Set<IFeature> getTargetColors() {
	// return typingProvider.getBindingColors().getColors(targetField);
	// }

}
