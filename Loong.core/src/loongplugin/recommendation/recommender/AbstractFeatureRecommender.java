package loongplugin.recommendation.recommender;

import java.util.Map;

import loongplugin.feature.Feature;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;

public abstract class AbstractFeatureRecommender {

	protected ApplicationObserver AOB;

	public AbstractFeatureRecommender() {
		AOB = ApplicationObserver.getInstance();
	}

	protected boolean isValidRecommendation(LElement element, Feature color) {
		if (AOB.getElementFeatures(element).contains(color))
			return false;
/*
		if (AOB.getElementNonFeatures(element).contains(color))
			return false;

		
		for (Feature relNonColor : AOB.getRelatedNonFeatures(color)) {
			if (AOB.getElementNonFeatures(element).contains(relNonColor))
					return false;
		}
*/
		for (Feature relColor : AOB.getRelatedFeatures(color)) {
			if (AOB.getElementFeatures(element).contains(relColor))
				return false;
		}
		

		return true;
	}

	public abstract String getRecommendationType();

	public abstract Map<LElement, RecommendationContext> getRecommendations(
			Feature color);
}
