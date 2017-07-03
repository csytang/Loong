package loongplugin.typing.internal.manager;

import java.util.List;
import java.util.Set;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.typing.model.IEvaluationStrategy;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;



public class EvaluationStrategyProxy implements IEvaluationStrategy {

	private final IConfigurationElement configElement;
	private final String featureModelProviderId;

	public EvaluationStrategyProxy(IConfigurationElement configurationElement) {
		this.configElement = configurationElement;
		name = configElement.getAttribute("name");
		id = configElement.getAttribute("id");
		featureModelProviderId = configElement
				.getAttribute("featureModelProvider");
	}

	private final String name;
	private final String id;
	private IEvaluationStrategy target = null;

	private void loadTarget() {
		try {
			target = (IEvaluationStrategy) configElement
					.createExecutableExtension("strategy");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Evaluation Strategy Extension: " + name + " (" + id + ")";
	}

	public boolean equal(FeatureModel featureModel, Set<Feature> source,
			Set<Feature> targete) {
		if (target == null)
			loadTarget();
		return target.equal(featureModel, source, targete);
	}

	public boolean implies(FeatureModel featureModel, Set<Feature> source,
			Set<Feature> targete) {
		if (target == null)
			loadTarget();
		return target.implies(featureModel, source, targete);
	}
	
	public boolean areMutualExclusive(FeatureModel featureModel, Set<Feature> context, List<Set<Feature>> featureSets) {
		if (target == null)
			loadTarget();
		return target.areMutualExclusive(featureModel, context, featureSets);
	}
	
	public boolean mayBeMissing(FeatureModel featureModel, Set<Feature> context, List<Set<Feature>> featureSets) {
		if (target == null)
			loadTarget();
		return target.mayBeMissing(featureModel, context, featureSets);
	}
	
	public boolean exists(FeatureModel featureModel, Set<Feature> features) {
		if (target == null)
			loadTarget();
		return target.exists(featureModel, features);
	}

	public boolean isResponsible(String featureModelId) {
		return featureModelProviderId.equals(featureModelId);
	}

	public void clearCache(FeatureModel featureModel) {
		if (target == null)
			loadTarget();
		target.clearCache(featureModel);
	}

	@Override
	public boolean equal(FeatureModel featureModel, Set<Feature> context, Set<Feature> source, Set<Feature> target) {
		// TODO Auto-generated method stub
		return false;
	}
}
