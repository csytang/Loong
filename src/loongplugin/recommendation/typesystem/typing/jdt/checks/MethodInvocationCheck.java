package loongplugin.recommendation.typesystem.typing.jdt.checks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.recommendation.typesystem.typing.jdt.BindingProjectColorCache;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions.ASTBindingFinderHelper;
import loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions.AbstractJDTTypingCheckWithResolution;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingMarkerResolution;
import loongplugin.utils.MethodPathItem;
import loongplugin.utils.OverridingRelationUtils;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;



/**
 * checks colors between a field and references to it
 * 
 * @author ckaestne & adreilin
 * 
 */
public class MethodInvocationCheck extends AbstractJDTTypingCheckWithResolution {

	// TODO CHECK RESOLUTION

	private final IMethodBinding targetMethod;

	private final List<ASTNode> arguments;

	public MethodInvocationCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source,
			List<ASTNode> args, IMethodBinding target) {
		super(file, typingProvider, source);
		this.arguments = args;
		this.targetMethod = target;
	}

	private boolean checkSourceAndTargetCondition(IEvaluationStrategy strategy,
			IMethodBinding targetBinding) {

		if (!strategy.implies(file.getFeatureModel(), file.getColorManager()
				.getColors(source), typingProvider.getBindingColors()
				.getColors(targetBinding)))
			return false;

		// check each parameter same condition
		for (int j = 0; j < arguments.size(); j++) {

			if (strategy.equal(file.getFeatureModel(), file.getColorManager()
					.getColors(arguments.get(j)), file.getColorManager()
					.getColors(source)))
				continue;

			// check the default case
			if (strategy.equal(file.getFeatureModel(), file.getColorManager()
					.getColors(arguments.get(j)), typingProvider
					.getBindingColors().getColors(
							BindingProjectColorCache.getParamKey(targetBinding
									.getKey(), j))))
				continue;

			return false;

		}

		return true;
	}

	public boolean evaluate(IEvaluationStrategy strategy) {

		// check the whole method default case
		if (checkSourceAndTargetCondition(strategy, targetMethod))
			return true;

		// checks if target method overrides other methods for which condition
		// is true
		List<MethodPathItem> inherMethods = new ArrayList<MethodPathItem>();

		// get overridden method keys
		OverridingRelationUtils.collectOverriddenMethodKeysInSuperClasses(
				targetMethod, inherMethods);

		// checks "OR" condition for all found keys
		for (MethodPathItem tmpItem : inherMethods) {

			// checks for each overridden method the implies condition
			if (checkSourceAndTargetCondition(strategy, tmpItem.getBinding()))
				return true;
		}

		return false;

	}

	public String getErrorMessage() {
		return "Invoking method which is not present in some variants: "
				+ targetMethod.getName();
	}

	public String getProblemType() {
		return "loong.typing.jdt.methodinvocationname";
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
		ASTNode methodDecl = ASTBindingFinderHelper
				.getMethodDecl(targetMethod);
		if (methodDecl != null)
			resolutions.addAll(createChangeNodeColorResolution(methodDecl,
					colorDiff, false, "method declaration", 14));
	}

	@Override
	protected Set<Feature> getTargetColors() {
		return typingProvider.getBindingColors().getColors(targetMethod);
	}

}
