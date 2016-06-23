package loongplugin.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import loongplugin.dialog.MiningStrategyConfDialog;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelManager;
import loongplugin.recommendation.recommender.AbstractFeatureRecommender;
import loongplugin.recommendation.recommender.AbstractLElementRecommnder;
import loongplugin.recommendation.resolvebinding.ResolveBindingLElementRecommender;
import loongplugin.recommendation.textcomparsion.TextComparisionLElementRecommender;
import loongplugin.recommendation.topology.TopologyLElementRecommender;
import loongplugin.recommendation.typesystem.TypeCheckLElementRecommender;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;

public class LElementRecommendationManager implements Observer{
	
	private ApplicationObserver aAO;
    private ProgramDatabase aDB;
	private Map<Feature,Map<LElement,RecommendationContextCollection>> element2Recommendation;
    private Set<AbstractLElementRecommnder> elementRecommenders;
    private Set<AbstractFeatureRecommender> featureRecommenders;
    public static boolean USE_TYPESYSTEM = false;
	public static boolean USE_TOPOLOGYANALYSIS =false;
	public static boolean USE_SUBSTRINGCOMP = false;
	public static boolean USE_RESOLVEBIND = false;
	
    public LElementRecommendationManager(){
		aAO = ApplicationObserver.getInstance();
		aDB = aAO.getProgramDatabase();
		elementRecommenders = new HashSet<AbstractLElementRecommnder>();
		featureRecommenders = new HashSet<AbstractFeatureRecommender>();
		USE_TYPESYSTEM = MiningStrategyConfDialog.getDefault().isTypeCheckSelected();
		USE_TOPOLOGYANALYSIS = MiningStrategyConfDialog.getDefault().isTopologySelected();
		USE_SUBSTRINGCOMP = MiningStrategyConfDialog.getDefault().isSubStringSelected();
		USE_RESOLVEBIND = MiningStrategyConfDialog.getDefault().isResolvebindSelected();
		
		
		
		
		if(USE_TYPESYSTEM)
			elementRecommenders.add(new TypeCheckLElementRecommender());
		if(USE_TOPOLOGYANALYSIS)
			elementRecommenders.add(new TopologyLElementRecommender());
		if(USE_RESOLVEBIND)
			elementRecommenders.add(new ResolveBindingLElementRecommender());
			
		if(USE_SUBSTRINGCOMP)	
			featureRecommenders.add(new TextComparisionLElementRecommender());
			
		aAO.addObserver(this);
	}
	
    public Map<Feature,RecommendationContextCollection> getAllRecommendations(LElement element){
    	 Map<Feature,RecommendationContextCollection> result = new HashMap<Feature,RecommendationContextCollection>();
    	 for (Feature color : element2Recommendation.keySet()) {
 			Map<LElement, RecommendationContextCollection> recommendations = element2Recommendation
 					.get(color);

 			if (recommendations == null || recommendations.size() == 0)
 				continue;

 			if (!recommendations.containsKey(element))
 				continue;

 			result.put(color, recommendations.get(element));

 		}
    	 
    	 
    	 return result;
    }
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	public Map<LElement, RecommendationContextCollection> getRecommendations(
			Feature color, LElement element) {
		Map<LElement, RecommendationContextCollection> colorRecommendations = element2Recommendation
				.get(color);

		if (colorRecommendations == null)
			return new HashMap<LElement, RecommendationContextCollection>();
		colorRecommendations = new HashMap<LElement, RecommendationContextCollection>(
				colorRecommendations);

		Map<LElement, RecommendationContextCollection> resultRecommendations = new HashMap<LElement, RecommendationContextCollection>();

		for (Entry<LElement, RecommendationContextCollection> entry : colorRecommendations
				.entrySet()) {
			LElement recElement = entry.getKey();
			RecommendationContextCollection collection = entry.getValue();
			for (RecommendationContext context : collection.getContexts()) {
				if (!element.equals(context.getSupporter()))
					continue;

				resultRecommendations.put(recElement, collection);
				break;
			}
		}

		return resultRecommendations;
	}

	public int getRecommendationsCount(Feature color, LElement element) {
		return getRecommendations(color, element).size();
	}


	public Map<LElement, RecommendationContextCollection> getRecommendations(Feature color) {

		Map<LElement, RecommendationContextCollection> recommendations = new HashMap<LElement, RecommendationContextCollection>();

		
		Map<LElement, RecommendationContextCollection> colorRecommendations = element2Recommendation.get(color);
		if (colorRecommendations != null && colorRecommendations.size() > 0) {
				recommendations = colorRecommendations;
		}

		Set<LElement> elements = aAO.getElementsOfFeature(color);

		for (LElement tmpElement : new ArrayList<LElement>(elements)) {

			Map<LElement, RecommendationContextCollection> tmpRecommendations = getRecommendations(
					color, tmpElement);

			if (tmpRecommendations == null)
				continue;

			mergeRecommendations(tmpRecommendations, recommendations);

		}

		return recommendations;
	}

	/**
	 * This generation algorithm original contains the following bug:
	 * when a new recommended element is added to then the seed set will be updated accordingly.
	 */
	public void generateRecommendations() {

		element2Recommendation = new HashMap<Feature, Map<LElement, RecommendationContextCollection>>();

		for (Feature color : FeatureModelManager.getInstance().getFeatures()) {

			// RESET RECOMMENDATIONS
			Map<LElement, RecommendationContextCollection> recommendations = new HashMap<LElement, RecommendationContextCollection>();
			element2Recommendation.put(color, recommendations);

			// RECOMMENDATION BASED ON LOCAL ELEMENT DATA
			Set<LElement> elements = aAO.getElementsOfFeature(color);
			

			Set<LElement> tmpElements = new HashSet<LElement>();
			tmpElements.addAll(elements);

			// ADD ELEMENTS OF RELATED COLORS
			for (Feature relatedColor : aAO.getRelatedFeatures(color)) {
					tmpElements.addAll(aAO.getElementsOfFeature(relatedColor));
			}
			elements = tmpElements;

			

			// generate recommendations for all elements
			for (LElement element : elements) {

				// recommend elements according to recommendation type
				for (AbstractLElementRecommnder recommender : elementRecommenders) {

					Map<LElement, RecommendationContext> tmpRecommendations = recommender
							.getRecommendations(element, color);
					addRecommendations(tmpRecommendations, recommendations);

				}

			}

			// RECOMMENDATION BASED ON GLOBAL FEATURE DATA
			// recommend elements according to recommendation type
			for (AbstractFeatureRecommender recommender : featureRecommenders) {
				Map<LElement, RecommendationContext> tmpRecommendations = recommender
						.getRecommendations(color);
				addRecommendations(tmpRecommendations, recommendations);
			}

		}

	}

	public void mergeRecommendations(
			Map<LElement, RecommendationContextCollection> newRecommendations,
			Map<LElement, RecommendationContextCollection> oldRecommendations) {
		for (LElement tmpRecElement : newRecommendations.keySet()) {

			RecommendationContextCollection oldCollection = oldRecommendations
					.get(tmpRecElement);

			if (oldCollection == null) {
				oldRecommendations.put(tmpRecElement, newRecommendations
						.get(tmpRecElement));
			}

		}
	}

	public void addRecommendations(
			Map<LElement, RecommendationContext> newRecommendations,
			Map<LElement, RecommendationContextCollection> oldRecommendations) {

		for (LElement tmpRecElement : newRecommendations.keySet()) {

			RecommendationContextCollection collection = oldRecommendations
					.get(tmpRecElement);

			if (collection == null) {
				collection = new RecommendationContextCollection();
				oldRecommendations.put(tmpRecElement, collection);
			}

			// add the new context
			collection.addContext(newRecommendations.get(tmpRecElement));

		}
	}
}
