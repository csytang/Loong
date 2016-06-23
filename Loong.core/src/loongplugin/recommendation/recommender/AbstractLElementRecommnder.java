package loongplugin.recommendation.recommender;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import loongplugin.feature.Feature;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;

public abstract class AbstractLElementRecommnder {
	protected ApplicationObserver AOB;
	
	public AbstractLElementRecommnder(){
		AOB = ApplicationObserver.getInstance();
	}
	
	protected boolean isValidRecommendation(LElement element, Feature feature) {
		if(AOB.getElementFeatures(element).contains(feature)){
			return false;
		}
	/*	
		if(AOB.getElementNonFeatures(element).contains(feature)){
			return false;
		}
	*/	
		for(Feature relColor:AOB.getRelatedFeatures(feature)){
			if(AOB.getElementFeatures(element).contains(relColor))
				return false;
		}
		
		/*
		for(Feature relNonColor:AOB.getRelatedNonFeatures(feature)){
			if(AOB.getElementFeatures(element).contains(relNonColor))
				return false;
		}
		*/
		return true;
		
	}

	protected boolean isInFeature(LElement element, Feature color) {
		if (AOB.getElementFeatures(element).contains(color))
			return true;

		

		for (Feature relColor : AOB.getRelatedFeatures(color)) {
			if (AOB.getElementFeatures(element).contains(relColor))
					return true;
		}
		

		return false;
	}
	
	

	protected Map<LElement, RecommendationContext> filterValidRecommendations(
			Feature color,
			Map<LElement, RecommendationContext> recommendations) {
		Map<LElement, RecommendationContext> actualRecom = new HashMap<LElement, RecommendationContext>();

		if (recommendations == null)
			return actualRecom;

		for (Entry<LElement, RecommendationContext> entry : recommendations
				.entrySet())
			if (isValidRecommendation(entry.getKey(), color))
				actualRecom.put(entry.getKey(), entry.getValue());

		return actualRecom;

	}
	
	public abstract Map<LElement, RecommendationContext> getRecommendations(
			LElement element, Feature feature);
	public abstract String getRecommendationType();

}
