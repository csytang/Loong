/**
    Copyright 2010 Christian K�stner

    This file is part of CIDE.

    CIDE is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.

    CIDE is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CIDE.  If not, see <http://www.gnu.org/licenses/>.

    See http://www.fosd.de/cide/ for further information.
*/

package loongplugin.typing.model;

import java.util.HashMap;
import java.util.HashSet;
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

	public boolean equal(FeatureModel featureModel, Set<Feature> context,
			Set<Feature> source, Set<Feature> target) {

		HashSet<Feature> sourceWithContext = new HashSet<Feature>(source);
		sourceWithContext.addAll(context);
		HashSet<Feature> targetWithContext = new HashSet<Feature>(target);
		targetWithContext.addAll(context);

		return implies(featureModel, sourceWithContext, target)
				&& implies(featureModel, targetWithContext, source);
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
