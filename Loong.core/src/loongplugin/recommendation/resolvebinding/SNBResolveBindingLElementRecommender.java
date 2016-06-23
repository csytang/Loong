package loongplugin.recommendation.resolvebinding;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.recommendation.recommender.AbstractLElementRecommnder;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LRelation;

public class SNBResolveBindingLElementRecommender extends AbstractLElementRecommnder {
	
	private FeatureModel featuremodel;
	private ProgramDatabase programDB;
	private Set<LElement> allelements;
	private IProject aProject;
	private LFlyweightElementFactory lflyweightElementFactory;
	
	public SNBResolveBindingLElementRecommender(){
		featuremodel = FeatureModelManager.getInstance().getFeatureModel();
		programDB = ApplicationObserver.getInstance().getProgramDatabase();
		aProject = ApplicationObserver.getInstance().getInitializedProject();
		lflyweightElementFactory = ApplicationObserver.getInstance().getLFlyweightElementFactory();
	}
	
	@Override
	public Map<LElement, RecommendationContext> getRecommendations(
			LElement element, Feature feature) {
		// TODO Auto-generated method stub
		Map<LElement,RecommendationContext> recommendations = new HashMap<LElement,RecommendationContext>();
		Set<LRelation> validTransponseRelations = LRelation.getAllRelations(element.getCategory(), true, false);
		validTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), true, true));
		int forwardColorElements = 0;
		
		
		for (LRelation tmpTransRelation : validTransponseRelations) {
			Set<LElement> forwardElements = AOB.getRange(element,tmpTransRelation);
			Set<LElement> validRecommendationElements = new HashSet<LElement>();
			
			for (LElement forwardElement : forwardElements) {
				if (isInFeature(forwardElement, feature)) {
					forwardColorElements++;
					continue;
				}
				
				if (isValidRecommendation(forwardElement, feature))
					validRecommendationElements.add(forwardElement);
			}
			
			// if they are all already in color, skip to next relation
			if (validRecommendationElements.size() == 0)
				continue;
			
			for (LElement validForwardElement : validRecommendationElements) {
				
				
				
			}
			
			
		}
		
		
		
		
		
		
		
		return null;
	}

	@Override
	public String getRecommendationType() {
		// TODO Auto-generated method stub
		return "SNBRB";
	}

}
