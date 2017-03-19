package loongplugin.typing.internal.manager;

import loongplugin.LoongPlugin;
import loongplugin.configuration.ExtensionPointManager;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.feature.FeatureModelProviderProxy;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;



public class EvaluationStrategyManager extends
		ExtensionPointManager<EvaluationStrategyProxy> {

	private static EvaluationStrategyManager instance;

	protected EvaluationStrategyManager() {
		super(LoongPlugin.PLUGIN_ID, "evaluationStrategy");
	}

	public static EvaluationStrategyManager getInstance() {
		if (instance == null)
			instance = new EvaluationStrategyManager();
		return instance;
	}

	/**
	 * returns the current evaluation strategy for the selected project
	 * (determined by the feature model in this project).
	 * 
	 * @param project
	 * @return
	 * @throws FeatureModelNotFoundException
	 *             if there is no feature model in this project or there is no
	 *             according evaluation strategy by this feature model
	 */
	public IEvaluationStrategy getEvaluationStrategy(IProject project)
			throws FeatureModelNotFoundException {
		// just get the feature model to make sure there is one
		FeatureModelManager.getInstance().getFeatureModel();
		FeatureModelProviderProxy featureModelProvider = FeatureModelManager
				.getInstance().getActiveFeatureModelProvider();
		assert featureModelProvider != null;
		String featureModelProviderId = featureModelProvider.getId();

		for (EvaluationStrategyProxy strategy : getProviders()) {
			if (strategy.isResponsible(featureModelProviderId))
				return strategy;
		}

		throw new FeatureModelNotFoundException(
				"There is no evaluation strategy installed for feature model "
						+ featureModelProviderId);
	}

	@Override
	protected EvaluationStrategyProxy parseExtension(
			IConfigurationElement configurationElement) {
		if (!configurationElement.getName().equals("evaluationStrategy"))
			return null;
		return new EvaluationStrategyProxy(configurationElement);
	}

}
