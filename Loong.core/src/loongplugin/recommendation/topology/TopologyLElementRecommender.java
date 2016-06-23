package loongplugin.recommendation.topology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongplugin.feature.Feature;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.recommendation.recommender.AbstractLElementRecommnder;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;


public class TopologyLElementRecommender extends AbstractLElementRecommnder{

	@Override
	public Map<LElement, RecommendationContext> getRecommendations(LElement element, Feature feature) {
		// TODO Auto-generated method stub
		//
		
		Map<LElement,RecommendationContext> recommendations = new HashMap<LElement,RecommendationContext>();
		/*
		 * get after relation for the element.
		 */
		Set<LRelation> validTransponseRelations = LRelation.getAllRelations(element.getCategory(), true, false);
		
		// ADDED AFTER EVALUATION
		validTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), true, true));

		for (LICategories cat : element.getSubCategories()) {
			validTransponseRelations.addAll(LRelation.getAllRelations(cat,
					true, false));
			// ADDED AFTER EVALUATION
			validTransponseRelations.addAll(LRelation.getAllRelations(cat,
					true, true));
		}
		
		// check all relations
		for (LRelation tmpTransRelation : validTransponseRelations) {
			try {
				// get the forward elements
				/*
				 * 做转化  将当前的LElement 通过关系 转化到相关的 element
				 */
				Set<LElement> forwardElements = AOB.getRange(element,tmpTransRelation);
				Set<LElement> validRecommendationElements = new HashSet<LElement>();
				int forwardColorElements = 0;
				int forwardNonColorElements = 0;

				// 查看有多少 LElement 已经被上色
				// int validRecommendationCount = 0;
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

				int invalidForwardRecommendations = forwardElements.size() - validRecommendationElements.size();

				for (LElement validForwardElement : validRecommendationElements) {

					// get backward elements for transpose
					Set<LElement> backwardElements = AOB.getRange(validForwardElement, tmpTransRelation.getInverseRelation());

					// calc how much of backward is already in color
					int backwardColorElements = 0;
					int backwardNonColorElements = 0;

					for (LElement backwardElement : backwardElements) {

						if (isInFeature(backwardElement, feature)) {
								backwardColorElements++;
						}else{
							backwardNonColorElements++;
						}
					}

						

					// calc color degree
					double colorDegree = ((double) (1 + forwardColorElements) / (double) forwardElements.size())
									* ((double) backwardColorElements / (double) backwardElements.size());

					// calc non color degree
					double nonColorDegree = ((double) (1 + forwardNonColorElements) / (double) forwardElements.size())
									* ((double) backwardNonColorElements / (double) backwardElements.size());

					double degree = colorDegree - nonColorDegree;

					if (degree <= 0)
							continue;
					//System.out.println("Degree:"+degree+" with the recommended element:"+validForwardElement.getShortName());
					// add / merge recommendation with alreay available ones
					RecommendationContext newContext = new RecommendationContext(element, tmpTransRelation.getName(),
									getRecommendationType(), degree);
					RecommendationContext oldContext = recommendations
									.get(validForwardElement);

					if (oldContext != null) {
						newContext = new RecommendationContext(newContext,
										oldContext, getRecommendationType());
					}
					recommendations.put(validForwardElement, newContext);

				}

			} catch (Exception e) {

			}
		}
		return recommendations;
	}
	public String getRecommendationType(){
		return "RB";
	}
}
