package loongplugin.recommendation.textcomparsion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.recommendation.recommender.AbstractFeatureRecommender;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;

public class TextComparisionLElementRecommender extends AbstractFeatureRecommender{
	private static final double TRESHOLD = 0.05;
	// private static final double ATTENUATOR = 0.05;

	private static final LICategories[] registerElement = new LICategories[] {
		LICategories.TYPE, LICategories.METHOD, LICategories.FIELD,
		LICategories.LOCAL_VARIABLE };

	private static final LICategories[] primaryElement = new LICategories[] {
		LICategories.TYPE, LICategories.METHOD, LICategories.FIELD,
		LICategories.LOCAL_VARIABLE };

	Map<LElement, Map<LElement, RecommendationContext>> cache;

	public TextComparisionLElementRecommender() {
		super();

		cache = new HashMap<LElement, Map<LElement, RecommendationContext>>();

	}

	@Override
	public String getRecommendationType() {
		return "TPF";
	}

	private boolean isPrimaryElement(LElement element) {

		for (int i = 0; i < primaryElement.length; i++) {
			if (primaryElement[i] == element.getCategory())
				return true;
		}

		return false;
	}

	private boolean isRegisterElement(LElement element) {

		for (int i = 0; i < registerElement.length; i++) {
			if (registerElement[i] == element.getCategory())
				return true;
		}

		return false;
	}

	// private int matchCount(String source, String substring){
	// if ( source == null || source.isEmpty() || substring == null ||
	// substring.isEmpty() )
	// return 0;
	//
	// int count = 0;
	// for ( int pos = 0; (pos = source.indexOf( substring, pos )) != -1;
	// count++ )
	// pos += substring.length();
	//
	// return count;
	// }

	private List<String> getSubStrings(String source) {
		List<String> substrings = new ArrayList<String>();

		String curSubString = "";
		char curChar;
		boolean curUpper, lastUpper = false;
		int changer = 1;

		for (int i = 0; i < source.length(); i++) {

			curChar = source.charAt(i);
			curUpper = Character.isUpperCase(curChar);

			if (lastUpper != curUpper && i > changer) {
				substrings.add(curSubString.toUpperCase());
				changer = i + 1;
				curSubString = "";
			}

			if (Character.isLetter(curChar))
				curSubString += curChar;

			lastUpper = curUpper;

		}

		// add last element
		substrings.add(curSubString.toUpperCase());

		return substrings;

	}

	private String removeNamingConvention(String source) {
		String[] filter = source.split(":");

		// filter naming convention for elements
		if (filter.length == 2)
			source = filter[1].substring(1);

		return source;
	}

	public Map<LElement, RecommendationContext> getRecommendations(
			final Feature color) {
		// PREPARATION PART!

		Set<LElement> featureElements = AOB.getElementsOfFeature(color);
		Map<String, Double> featureSubStringRegister = new HashMap<String, Double>();
		double featureRegisterSize = 0.0;

//		Set<LElement> nonFeatureElements = AOB.getElementsOfNonFeature(color);
		Map<String, Double> nonFeatureSubStringRegister = new HashMap<String, Double>();
		double nonFeatureRegisterSize = 0.0;

	

			// ADD ELEMENTS OF RELATED COLORS
		Set<LElement> tmpElements = new HashSet<LElement>();
		tmpElements.addAll(featureElements);

		for (Feature relatedColor : AOB.getRelatedFeatures(color)) {
				tmpElements.addAll(AOB.getElementsOfFeature(relatedColor));
		}
		featureElements = tmpElements;
			// ADD ELEMENTS OF NON RELATED COLORS
		Set<LElement> tmpNonFeatureElements = new HashSet<LElement>();
		//tmpNonFeatureElements.addAll(nonFeatureElements);

		/*
		for (Feature nonRelatedColor : AOB.getRelatedNonFeatures(color)) {
				tmpNonFeatureElements.addAll(AOB
						.getElementsOfFeature(nonRelatedColor));
		}*/
		
		//nonFeatureElements = tmpNonFeatureElements;

		

		if (featureElements.size() == 0)
			return new HashMap<LElement, RecommendationContext>();

		// create SubString Register for Feature Elements
		for (LElement curElement : featureElements) {
			if (!isRegisterElement(curElement))
				continue;

			String source = removeNamingConvention(curElement.getASTID());
			List<String> subStrings = getSubStrings(source);

			for (String subString : subStrings) {
				Double value = featureSubStringRegister.get(subString);

				if (value == null)
					value = 0.0;

				featureSubStringRegister.put(subString, ++value);
				featureRegisterSize++;
			}

		}

		if (featureRegisterSize == 0)
			featureRegisterSize = 1.0;

		

		if (nonFeatureRegisterSize == 0)
			nonFeatureRegisterSize = 1.0;

		// normalize registers
		double tmpFeatureRegisterSize = featureRegisterSize;
		double tmpNonFeatureRegisterSize = nonFeatureRegisterSize;

		for (String matchSubString : new ArrayList<String>(
				nonFeatureSubStringRegister.keySet())) {
			// check if non feature string is also feature string
			if (!featureSubStringRegister.containsKey(matchSubString))
				continue;

			double featureValue = featureSubStringRegister.get(matchSubString)
					/ tmpFeatureRegisterSize;
			double nonFeatureValue = nonFeatureSubStringRegister
					.get(matchSubString)
					/ tmpNonFeatureRegisterSize;
			double normValue;

			if (featureValue > nonFeatureValue) {
				normValue = nonFeatureValue * tmpFeatureRegisterSize;
				featureRegisterSize -= normValue;
				featureSubStringRegister.put(matchSubString,
						featureSubStringRegister.get(matchSubString)
								- normValue);

				nonFeatureRegisterSize -= nonFeatureSubStringRegister
						.remove(matchSubString);

			} else if (featureValue < nonFeatureValue) {
				normValue = featureValue * tmpNonFeatureRegisterSize;
				nonFeatureRegisterSize -= normValue;
				nonFeatureSubStringRegister.put(matchSubString,
						nonFeatureSubStringRegister.get(matchSubString)
								- normValue);

				featureRegisterSize -= featureSubStringRegister
						.remove(matchSubString);

			} else {
				nonFeatureRegisterSize -= nonFeatureSubStringRegister
						.get(matchSubString);
				nonFeatureSubStringRegister.put(matchSubString, 0.0);
				featureRegisterSize -= featureSubStringRegister
						.get(matchSubString);
				featureSubStringRegister.put(matchSubString, 0.0);
			}

		}

		// RECOMMENDATION PART!
		Map<LElement, RecommendationContext> recommendations = new HashMap<LElement, RecommendationContext>();

		for (LElement curElement : AOB.getAllElements()) {

			// check only primary elements
			if (!isPrimaryElement(curElement))
				continue;

			// check only if element can be recommended
			if (!isValidRecommendation(curElement, color))
				continue;

			String target = removeNamingConvention(curElement.getASTID());
			List<String> subStrings = getSubStrings(target);
			double subStringCount = subStrings.size();
			double support = 0;
			int unknownStrings = 0;
			for (String subString : subStrings) {
				double lenFactor;
				if (subString.length() >= 6) {
					lenFactor = (double) 1.0;
				} else {
					lenFactor = Math.max((double) 1.0, Math.log10(subString
							.length())
							/ Math.log10(6));
				}

				Double featureSubStringCount = featureSubStringRegister
						.get(subString);
				if (featureSubStringCount == null)
					featureSubStringCount = 0.0;
				double featureIndex = (double) featureSubStringCount
						/ (double) featureRegisterSize;

				Double nonFeatureSubStringCount = nonFeatureSubStringRegister
						.get(subString);
				if (nonFeatureSubStringCount == null)
					nonFeatureSubStringCount = 0.0;
				double nonFeatureIndex = (double) nonFeatureSubStringCount
						/ (double) nonFeatureRegisterSize;

				if ((featureIndex - nonFeatureIndex) == 0)
					unknownStrings++;
				else
					support += (lenFactor * (featureIndex - nonFeatureIndex));
			}

			// support = support * (subStringCount-unknownStrings) /
			// subStringCount;

			if (support > 1)
				support = (double) 1;

			if (support >= TRESHOLD) {
				RecommendationContext context = new RecommendationContext(
						curElement, "Text Match", getRecommendationType(),
						support);
				recommendations.put(curElement, context);
			}

		}

		return recommendations;

	}

}
