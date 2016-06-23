package loongplugin.typing.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.recommendation.typesystem.typing.jdt.model.AbstractCachingEvaluationStrategy;
import loongplugin.recommendation.typesystem.typing.jdt.model.DebugTyping;

import org.sat4j.specs.TimeoutException;



/**
 * evaluation strategy for the typechecker that delegates checks to the guidsl
 * implementation which then used a SAT solver to check implications on the
 * background of the feature model
 * 
 * this strategy must cache results because queries to the SAT solver are
 * expensive
 * 
 * @author ckaestne
 * 
 */
public class SatEvaluationStrategy extends AbstractCachingEvaluationStrategy {

	@Override
	public boolean implies(FeatureModel featureModel, Set<Feature> source,
			Set<Feature> target) {

		DebugTyping.debug_requests++;
		// if (source.containall(target))
		// return true;

		if (source.isEmpty() && target.isEmpty()) {
			DebugTyping.debug_emptycounter++;
			return true;
		}
		if (source.equals(target)) {
			DebugTyping.debug_equalcounter++;
			return true;
		}

		return super.implies(featureModel, source, target);
	}

	@Override
	protected boolean calcImplies(FeatureModel featureModel,
			Set<Feature> source, Set<Feature> target) {
		DebugTyping.debug_cache_miss++;
		// ignore empty feature models

		DebugTyping.debug_satcounter++;

		if (source.containsAll(target)) {
			DebugTyping.debug_subsetcounter++;
			return true;
		}

		long start = System.currentTimeMillis();// debug only

		FeatureModel guidslModel = featureModel;
		if (guidslModel == null)
			return true;

		Set<Feature> guidslSourceFeatures = convertToGuidslFeatures(source);
		Set<Feature> guidslTargetFeatures = convertToGuidslFeatures(target);

		try {
			boolean result = guidslModel.checkImplies(guidslSourceFeatures,
					guidslTargetFeatures);
			long end = System.currentTimeMillis();// debug only
			DebugTyping.satTime(end - start, source, target);
			return result;
		} catch (TimeoutException e) {
			e.printStackTrace();
			// in case of a timeout assume everything is fine and the
			// implication is true. idea is not to report false positive
			return true;
		}
	}

	public boolean areMutualExclusive(FeatureModel featureModel,
			Set<Feature> context, List<Set<Feature>> featureSets) {
		// ignore empty feature models
		FeatureModel guidslModel = featureModel;
		if (guidslModel == null)
			return true;

		List<Set<Feature>> guidslFeatureSets = new LinkedList<Set<Feature>>();
		for (Set<Feature> features : featureSets) {
			guidslFeatureSets.add(convertToGuidslFeatures(features));
		}

		try {
			return guidslModel.areMutualExclusive(
					convertToGuidslFeatures(context), guidslFeatureSets);
		} catch (TimeoutException e) {
			e.printStackTrace();
			// in case of a timeout assume everything is fine and the
			// formula is true. idea is not to report false positive
			return true;
		}
	}

	public boolean mayBeMissing(FeatureModel featureModel,
			Set<Feature> context, List<Set<Feature>> featureSets) {
		// ignore empty feature models
		FeatureModel guidslModel = featureModel;
		if (guidslModel == null)
			return true;

		List<Set<Feature>> guidslFeatureSets = new LinkedList<Set<Feature>>();
		for (Set<Feature> features : featureSets) {
			guidslFeatureSets.add(convertToGuidslFeatures(features));
		}

		try {
			return guidslModel.mayBeMissing(convertToGuidslFeatures(context),
					guidslFeatureSets);
		} catch (TimeoutException e) {
			e.printStackTrace();
			// in case of a timeout assume everything is fine and the
			// result is false. idea is not to report false positive
			return false;
		}
	}

	@Override
	protected boolean calcExists(FeatureModel featureModel,
			Set<Feature> features) {
		// ignore empty feature models
		FeatureModel guidslModel = featureModel;
		if (guidslModel == null)
			return true;
		try {
			return guidslModel.exists(convertToGuidslFeatures(features));
		} catch (TimeoutException e) {
			e.printStackTrace();
			// in case of a timeout assume everything is fine and the
			// formula is true. idea is not to report false positive
			return true;
		}
	}

	private Set<Feature> convertToGuidslFeatures(Set<Feature> features) {
		if (features == null)
			return null;

		Set<Feature> guidslFeatures = new HashSet<Feature>(features.size());
		for (Feature f : features) {
			guidslFeatures.add(f);
		}

		return guidslFeatures;
	}
}
