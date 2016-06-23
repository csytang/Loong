package loongplugin.recommendation.typesystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.recommendation.recommender.AbstractLElementRecommnder;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;
import loongplugin.typing.internal.manager.EvaluationStrategyManager;

public class TypeCheckLElementRecommender extends AbstractLElementRecommnder{

	
	private FeatureModel featuremodel;
	private IEvaluationStrategy strategy;
	
	public TypeCheckLElementRecommender(){
		featuremodel = FeatureModelManager.getInstance().getFeatureModel();
		try {
			strategy = EvaluationStrategyManager.getInstance()
					.getEvaluationStrategy(AOB.getInstance().getInitializedProject());
		} catch (FeatureModelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	@Override
	public Map<LElement, RecommendationContext> getRecommendations(
			LElement element, Feature feature) {
		// TODO Auto-generated method stub
		Map<LElement, RecommendationContext> recommendations = new HashMap<LElement,RecommendationContext>();
		
		// CHECK REFERENCE CHECK
		Set<LElement> accessElements = AOB.getRange(element,LRelation.T_BELONGS_TO);
		for (LElement accessElement : accessElements) {

			ReferenceCheck check = new ReferenceCheck(element, accessElement,
					featuremodel);
			if (!check.evaluate(strategy)
					&& isValidRecommendation(accessElement, feature)) {
				// access element should be recommended
				RecommendationContext context = new RecommendationContext(element, "Check Accesses", getRecommendationType(), 1);
				recommendations.put(accessElement, context);
			}

		}
		
		// CHECK PARAM ACCESS - VIEWPOINT PARAM DECLARATION
		Set<LElement> paramTargetElements = AOB.getRange(element,LRelation.REQUIRES);
		for (LElement paramTargetElement : paramTargetElements) {
			Object[] bodyTargetElements = AOB.getRange(paramTargetElement,LRelation.T_DECLARES_PARAMETER).toArray();
			LElement bodyTargetElement = null;
			if (bodyTargetElements.length > 0)
				bodyTargetElement = (LElement) bodyTargetElements[0];

			Set<LElement> bodySourceElements = AOB.getRange(bodyTargetElement,LRelation.BELONGS_TO);
			LElement bodySourceElement = null;
			for (LElement tmpElement : bodySourceElements) {
				if (tmpElement.getCategory() == LICategories.METHOD) {
					bodySourceElement = tmpElement;
					break;
				}
			}

			InvocationCheck check = new InvocationCheck(element,paramTargetElement, bodySourceElement, bodyTargetElement,featuremodel);

			if (!check.evaluate(strategy)) {
				// access element should be recommended
				int solutionsCount = 0;
				boolean[] solutions = new boolean[2];

				// AFTER EVALUATION
				// if (bodyTargetElement != null &&
				// isValidRecommendation(bodyTargetElement, color)) {
				// solutionsCount++;
				// solutions[0] = true;
				// }

				if (paramTargetElement != null&& isValidRecommendation(paramTargetElement, feature)) {
					solutionsCount++;
					solutions[1] = true;
				}

				if (solutionsCount > 0) {
					RecommendationContext context = new RecommendationContext(element, "Check Param Access",
									getRecommendationType(), (double) 1
											/ (double) solutionsCount);

					if (solutions[0])
						recommendations.put(bodyTargetElement, context);

					if (solutions[1])
						recommendations.put(paramTargetElement, context);
				}
			}
		}
		// CHECK PARAM ACCESS
		if (element.getCategory().equals(LICategories.PARAMETER_ACCESS)) {
			LElement paramTargetElement = element;

			Object[] bodyTargetElements = AOB.getRange(paramTargetElement,LRelation.T_DECLARES_PARAMETER).toArray();
			LElement bodyTargetElement = null;
			if (bodyTargetElements.length > 0)
				bodyTargetElement = (LElement) bodyTargetElements[0];

			Object[] paramSourceElements = AOB.getRange(paramTargetElement,LRelation.T_REQUIRES).toArray();
			LElement paramSourceElement = null;
			if (paramSourceElements.length > 0)
				paramSourceElement = (LElement) paramSourceElements[0];

			Set<LElement> bodySourceElements = AOB.getRange(bodyTargetElement,LRelation.BELONGS_TO);
			LElement bodySourceElement = null;
			for (LElement tmpElement : bodySourceElements) {
				if (tmpElement.getCategory() == LICategories.METHOD) {
					bodySourceElement = tmpElement;
					break;
				}
			}

			InvocationCheck check = new InvocationCheck(paramSourceElement,paramTargetElement, bodySourceElement, bodyTargetElement,
							featuremodel);

			if (!check.evaluate(strategy)) {
				// create recommendations

				int solutionsCount = 0;
				boolean[] solutions = new boolean[3];

				if (bodyTargetElement != null && isValidRecommendation(bodyTargetElement, feature)) {
					solutionsCount++;
					solutions[0] = true;
				}

				if (paramSourceElement != null
								&& isValidRecommendation(paramSourceElement, feature)) {
					solutionsCount++;
					solutions[1] = true;
				}
				// AFTER EVALUATION
				// if (bodySourceElement != null &&
				// isValidRecommendation(bodySourceElement, color)) {
				// solutionsCount++;
				// solutions[2] = true;
				// }

				if (solutionsCount > 0) {

					if (solutions[0]) {
								RecommendationContext context = new RecommendationContext(
										element, "Check Param Access",
										getRecommendationType(), (double) 1
												/ (double) solutionsCount);
								recommendations.put(bodyTargetElement, context);
							}

					RecommendationContext context = new RecommendationContext(
									element, "Check Decl.", getRecommendationType(),
									(double) 1 / (double) solutionsCount);

							if (solutions[1])
								recommendations.put(paramSourceElement, context);

							if (solutions[2])
								recommendations.put(bodySourceElement, context);
						}
					}

		}		
		
		
		
		
		return recommendations;
	}

	@Override
	public String getRecommendationType() {
		// TODO Auto-generated method stub
		return "TC";
	}

}
