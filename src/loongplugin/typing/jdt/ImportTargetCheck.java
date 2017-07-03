package loongplugin.typing.jdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import loongplugin.typing.model.IEvaluationStrategy;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.typing.model.ITypingMarkerResolution;
import loongplugin.typing.jdt.JDTTypingProvider;
import loongplugin.typing.jdt.checks.*;
import loongplugin.typing.jdt.checks.resolutions.AbstractJDTTypingCheckWithResolution;
import loongplugin.typing.jdt.checks.resolutions.OrganizeImportColorsResolution;

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
		Set<Feature> targetColors = Collections.emptySet();
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
		return "loongplugin.typing.jdt.importtarget";
	}

	@Override
	protected void addResolutions(ArrayList<ITypingMarkerResolution> resolutions,HashSet<Feature> colorDiff) {
		OrganizeImportColorsResolution resolution = new OrganizeImportColorsResolution(file, typingProvider.getBindingColors());
		resolutions.add((ITypingMarkerResolution)resolution);
	}

	@Override
	protected Set<Feature> getTargetColors() {
		return getTargetColor();
	}

	
}
