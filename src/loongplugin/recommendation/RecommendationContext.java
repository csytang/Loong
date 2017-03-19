package loongplugin.recommendation;

import loongplugin.source.database.model.LElement;

public class RecommendationContext {
	
	private double supportValue;
	private String reason;
	private LElement supporter;
	private String recommenderType;
	
	public RecommendationContext(LElement supporter, String reason,
			String recommenderType, double value){
		this.supporter = supporter;
		this.reason = reason;
		this.recommenderType = recommenderType;
		this.supportValue = value;
	}
	
	public RecommendationContext(RecommendationContext context1,
			RecommendationContext context2, String recommenderType){
		// FUZZY STANDARD
		supportValue = Math.max(context1.getSupportValue(), context2.getSupportValue());

		// ROB08-ANSATZ
		// supportValue = context1.getSupportValue() +
		// context2.getSupportValue() - (context1.getSupportValue() *+
		// context2.getSupportValue());

		supporter = context1.getSupporter();
		reason = context1.getReason() + ", " + context2.getReason();
		this.recommenderType = recommenderType;
	}
	
	public String getReason() {
		return reason;
	}

	public double getSupportValue() {
		if (LElementRecommendationManager.USE_TOPOLOGYANALYSIS
				&& !recommenderType.equals("TC"))
			return supportValue * .9;
		return supportValue;
	}

	public LElement getSupporter() {
		return supporter;
	}

	public String getRecommenderType() {
		return recommenderType;
	}

	// public void setSupportValue(double supportValue) {
	// this.supportValue = supportValue;
	// }

	
}
