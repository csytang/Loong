package loongplugin.recommendation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RecommendationContextCollection {

	private Set<RecommendationContext> contexts;

	public RecommendationContextCollection() {
		contexts = new HashSet<RecommendationContext>();
	}

	public void addContext(RecommendationContext context) {
		contexts.add(context);
	}

	public void addContexts(Set<RecommendationContext> contexts) {
		this.contexts.addAll(contexts);
	}

	public Set<RecommendationContext> getContexts() {
		return contexts;
	}

	// public boolean hasSupport() {
	// return !reasons.isEmpty();
	// }

	public double getSupportValue() {
		double supportValue = 0;
		Map<String, Double> max4Type = new HashMap<String, Double>();
		double curMaxValue = 0;

		for (RecommendationContext context : contexts) {
			// //FUZZY STANDARD
			
			Double maxValue = max4Type.get(context.getRecommenderType());
			if (maxValue == null) {
				curMaxValue = context.getSupportValue();
			} else {
				curMaxValue = Math.max(maxValue, context.getSupportValue());
			}

			max4Type.put(context.getRecommenderType(), curMaxValue);

		}

		for (Entry<String, Double> entry : max4Type.entrySet()) {
			supportValue = supportValue + entry.getValue()
					- (supportValue * entry.getValue());
		}

		return supportValue;
	}
	
	//special version for a specific recommender
	public double getSupportValue(String recommenderKind) {
		for (RecommendationContext context : contexts) {
			if (context.getRecommenderType().equals(recommenderKind))
				return context.getSupportValue();
		}
		return 0;
	}

	public String getSupportReasons() {

		Map<String, Integer> reasonMap = new HashMap<String, Integer>();

		for (RecommendationContext context : contexts) {
			Integer value = reasonMap.get(context.getRecommenderType() + ":"
					+ context.getReason());
			if (value == null)
				value = 0;

			reasonMap.put(context.getRecommenderType() + ":"
					+ context.getReason(), ++value);

		}

		String reasons = "";
		for (Entry<String, Integer> entry : reasonMap.entrySet()) {
			reasons += entry.getKey() + "(" + entry.getValue() + "), ";
		}

		return reasons.substring(0, reasons.length() - 2);
	}


}
