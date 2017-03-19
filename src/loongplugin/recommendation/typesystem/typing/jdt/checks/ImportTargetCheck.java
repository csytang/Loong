package loongplugin.recommendation.typesystem.typing.jdt.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions.AbstractJDTTypingCheckWithResolution;
import loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions.OrganizeImportColorsResolution;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingMarkerResolution;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;



/**
 * checks colors between an import statement and the type it imports
 * 
 * @author ckaestne
 * 
 */
public class ImportTargetCheck extends AbstractJDTTypingCheckWithResolution {

	private final IBinding targetBinding;

	public ImportTargetCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source, IBinding binding) {
		super(file, typingProvider, source);
		this.targetBinding = binding;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		Set<Feature> importColors = file.getColorManager().getColors(source);

		Set<Feature> targetColors = getTargetColor();
		return strategy.implies(file.getFeatureModel(), importColors,
				targetColors);
	}

	private Set<Feature> getTargetColor() {
		Set<Feature> targetColors = Collections.EMPTY_SET;
		if (targetBinding instanceof ITypeBinding) {
			targetColors = typingProvider.getBindingColors().getColors(
					(ITypeBinding) targetBinding);
		}
		if (targetBinding instanceof IMethodBinding) {
			targetColors = typingProvider.getBindingColors().getColors(
					(IMethodBinding) targetBinding);
		}
		if (targetBinding instanceof IVariableBinding) {
			targetColors = typingProvider.getBindingColors().getColors(
					(IVariableBinding) targetBinding);
		}
		return targetColors;
	}

	public String getErrorMessage() {
		return "Import of type which is not present in some variants: "
				+ targetBinding.getName();
	}

	public String getProblemType() {
		return "loong.typing.jdt.importtarget";
	}

	@Override
	protected void addResolutions(
			ArrayList<ITypingMarkerResolution> resolutions,
			HashSet<Feature> colorDiff) {
		resolutions.add(new OrganizeImportColorsResolution(file, typingProvider
				.getBindingColors()));
	}

	@Override
	protected Set<Feature> getTargetColors() {
		return getTargetColor();
	}

}
