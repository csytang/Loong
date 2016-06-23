package loongplugin.recommendation.typesystem.typing.jdt.checks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions.ASTBindingFinderHelper;
import loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions.AbstractJDTTypingCheckWithResolution;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingMarkerResolution;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IVariableBinding;


/**
 * checks colors between a field and references to it
 * 
 * @author ckaestne
 * 
 */
public class FieldAccessCheck extends AbstractJDTTypingCheckWithResolution {

	private final IVariableBinding targetField;

	public FieldAccessCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			IVariableBinding target) {
		super(file, typingProvider, source);
		this.targetField = target;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		return strategy.implies(file.getFeatureModel(), file.getColorManager()
				.getColors(source), typingProvider.getBindingColors()
				.getColors(targetField));
	}

	public String getErrorMessage() {
		return "Access to field which is not present in some variants: "
				+ targetField.getName();
	}

	public String getProblemType() {
		return "loong.typing.jdt.fieldaccess";
	}

	@Override
	protected void addResolutions(
			ArrayList<ITypingMarkerResolution> resolutions,
			HashSet<Feature> colorDiff) {
		resolutions
				.addAll(createChangeNodeColorResolution(
						findCallingStatement(source), colorDiff, true,
						"statement", 20));
		resolutions.addAll(createChangeNodeColorResolution(
				findCallingMethod(source), colorDiff, true, "method", 18));
		resolutions.addAll(createChangeNodeColorResolution(
				findCallingType(source), colorDiff, true, "type", 16));

		// add resolution for target (field declaration)
		ASTNode fieldDecl = ASTBindingFinderHelper.getFieldDecl(targetField);
		if (fieldDecl != null)
			resolutions.addAll(createChangeNodeColorResolution(fieldDecl,
					colorDiff, false, "field declaration", 14));
	}

	@Override
	protected Set<Feature> getTargetColors() {
		return typingProvider.getBindingColors().getColors(targetField);
	}

}
