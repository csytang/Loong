package loongplugin.recommendation.typesystem.typing.jdt.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;


public abstract class AbstractCachingEvaluationStrategy implements
		IEvaluationStrategy {

	private final WeakHashMap<FeatureModel, Map<Set<Feature>, Map<Set<Feature>, Boolean>>> impliesCache = new WeakHashMap<FeatureModel, Map<Set<Feature>, Map<Set<Feature>, Boolean>>>();

	private final WeakHashMap<FeatureModel, Map<Set<Feature>, Boolean>> existsCache = new WeakHashMap<FeatureModel, Map<Set<Feature>, Boolean>>();

	// debug and profiling only
	public void clearCache() {
		impliesCache.clear();
		existsCache.clear();
		DebugTyping.profiling_resetSATCaches = false;
	}

	/**
	 * non-caching, delegated to implies. override if necessary
	 */
	public boolean equal(FeatureModel featureModel, Set<Feature> source,
			Set<Feature> target) {
		return implies(featureModel, source, target)
				&& implies(featureModel, target, source);
	}

	/**
	 * caching, delegates to calcImplies which must be implemented by
	 * subclasses. this method should usually not be overridden
	 */
	public boolean implies(FeatureModel featureModel, Set<Feature> source,
			Set<Feature> target) {
		if (DebugTyping.profiling_resetSATCaches)
			clearCache();
		Map<Set<Feature>, Map<Set<Feature>, Boolean>> cacheForFeatureModel = impliesCache
				.get(featureModel);
		if (cacheForFeatureModel != null) {
			Map<Set<Feature>, Boolean> cacheForSource = cacheForFeatureModel
					.get(source);

			if (cacheForSource != null) {
				Boolean cachedValue = cacheForSource.get(target);
				if (cachedValue != null) {
					return cachedValue.booleanValue();
				}
			}
		}

		boolean result = calcImplies(featureModel, source, target);
		storeInImpliesCache(featureModel, source, target, result);

		return result;
	}

	public boolean exists(FeatureModel featureModel, Set<Feature> features) {
		if (DebugTyping.profiling_resetSATCaches)
			clearCache();
		Map<Set<Feature>, Boolean> cacheForFeatureModel = existsCache
				.get(featureModel);
		if (cacheForFeatureModel != null) {
			Boolean exists = cacheForFeatureModel.get(features);
			if (exists != null)
				return exists.booleanValue();
		}

		boolean result = calcExists(featureModel, features);
		storeInExistsCache(featureModel, features, result);

		return result;
	}

	private void storeInImpliesCache(FeatureModel featureModel,
			Set<Feature> source, Set<Feature> target, boolean result) {
		Map<Set<Feature>, Map<Set<Feature>, Boolean>> cacheForFeatureModel = impliesCache
				.get(featureModel);
		if (cacheForFeatureModel == null) {
			cacheForFeatureModel = new HashMap<Set<Feature>, Map<Set<Feature>, Boolean>>();
			impliesCache.put(featureModel, cacheForFeatureModel);
		}

		Map<Set<Feature>, Boolean> cacheForSource = cacheForFeatureModel
				.get(source);
		if (cacheForSource == null) {
			cacheForSource = new HashMap<Set<Feature>, Boolean>();
			cacheForFeatureModel.put(source, cacheForSource);
		}

		cacheForSource.put(target, new Boolean(result));
	}

	private void storeInExistsCache(FeatureModel featureModel,
			Set<Feature> features, boolean result) {
		Map<Set<Feature>, Boolean> cacheForFeatureModel = existsCache
				.get(featureModel);
		if (cacheForFeatureModel == null) {
			cacheForFeatureModel = new HashMap<Set<Feature>, Boolean>();
			existsCache.put(featureModel, cacheForFeatureModel);
		}

		cacheForFeatureModel.put(features, result);
	}

	protected abstract boolean calcImplies(FeatureModel featureModel,
			Set<Feature> source, Set<Feature> target);

	protected abstract boolean calcExists(FeatureModel featureModel,
			Set<Feature> features);

	public void clearCache(FeatureModel featureModel) {
		impliesCache.remove(featureModel);
		existsCache.remove(featureModel);
	}
}
