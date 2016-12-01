package loongplugin.source.database.model;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.source.database.ApplicationObserver;

public class LElementColorManager{
	

	private Map<Feature, Set<Feature>> feature2relatedFeatures;
	private Map<Feature, Set<Feature>> feature2alternativeFeatures;
	
	
	private final static Set<Feature> NOCOLORS = Collections.EMPTY_SET;

	private ApplicationObserver AO;

	public LElementColorManager(ApplicationObserver AO) {
		this.AO = AO;
		
		feature2relatedFeatures = new HashMap<Feature, Set<Feature>>();
		feature2alternativeFeatures = new HashMap<Feature, Set<Feature>>();
		FeatureModelManager manager = FeatureModelManager.getInstance();
		
		FeatureModel model = manager.getFeatureModel();

		Set<Feature> alwaysTrueElements = new HashSet<Feature>();
		Set<Feature> alwaysFalseElements = new HashSet<Feature>();
		model.getSelectedAndUnselectedFeatures(new HashSet<Feature>(),alwaysTrueElements, alwaysFalseElements);
		
		System.out.println("--------------------------------");
		for (Feature curFeature : model.getFeatures()) {

			//ININT
			//System.out.println("Feature:"+curFeature.getName());

			if (alwaysFalseElements.contains(curFeature))
						continue;

			if (alwaysTrueElements.contains(curFeature))
						continue;
			//ININT
			//System.out.println("Feature(2):"+curFeature.getName());
			Set<Feature> s = new HashSet<Feature>();
			s.add(curFeature);

			Set<Feature> selectedFeatures = new HashSet<Feature>();
			Set<Feature> unselectedFeatures = new HashSet<Feature>();

			model.getSelectedAndUnselectedFeatures(s, selectedFeatures,	unselectedFeatures);
			//DEBUG
			//System.out.println("Selected features are:"+selectedFeatures.size());
			//for(Feature featue:selectedFeatures){
			//	System.out.println("Feature:"+featue.getName());
			//}
			//FINISH
			// remove the current selected
			selectedFeatures.remove(curFeature);

			// remove features which are always true
			selectedFeatures.removeAll(alwaysTrueElements);

			// store relations
			for (Feature selectedFeature : selectedFeatures) {
				// check if transpose relation is already stored
				Set<Feature> tRelatedFeatures = feature2relatedFeatures.get(curFeature);
				if (tRelatedFeatures != null) {
					if (tRelatedFeatures.contains(selectedFeature)) {
						// if transpose relation is stored, remove it!
						tRelatedFeatures.remove(selectedFeature);

						// remove set if there are no more relations
						if (tRelatedFeatures.isEmpty()) {
							feature2relatedFeatures.remove(curFeature);
						}
						continue;
					}
				}
						// check end
				Set<Feature> relatedFeatures = feature2relatedFeatures.get(selectedFeature);
				if (relatedFeatures == null) {
					relatedFeatures = new HashSet<Feature>();
					feature2relatedFeatures.put(selectedFeature,relatedFeatures);
					for(Feature feature:relatedFeatures){
						System.out.println("Put:"+selectedFeature.getName()+" related to "+feature.getName());
					}
				}
				relatedFeatures.add(curFeature);
				System.out.println("    --> SELECTED:"+ selectedFeature.getName());
			}

			// remove features which are always false
			unselectedFeatures.remove(alwaysFalseElements);

			// add alternative relations
			for (Feature unselectedFeature : unselectedFeatures) {

				Set<Feature> alternativeFeatures = feature2relatedFeatures.get(curFeature);

				if (alternativeFeatures == null) {
					alternativeFeatures = new HashSet<Feature>();
					feature2alternativeFeatures.put(curFeature,alternativeFeatures);
				}

				alternativeFeatures.add(unselectedFeature);
				System.out.println("    --> UNSELECTED:"+ unselectedFeature.getName());
			}

		}
		System.out.println("--------------------------------");
		
		
	}

	
	public Set<Feature> getRelatedFeatures(Feature color) {
		Set<Feature> result = feature2relatedFeatures.get(color);

		if (result == null)
			return new HashSet<Feature>();

		return result;
	}
/*
	public Set<Feature> getRelatedNonFeatures(Feature color) {
		Set<Feature> result = feature2alternativeFeatures.get(color);

		if (result == null)
			return new HashSet<Feature>();

		return result;
	}
*/
	public Set<Feature> getElementFeatures(LElement element) {
		if(element!=null)
			return element.getAssociatedFeatures();
		else
			return new HashSet<Feature>();
	}

	public Set<LElement> getElementsOfFeature(Feature color) {

		Set<LElement> elements = color.getLElementBelongs();
		
		return elements;
	}

	public Set<Feature> getAvailableFeatures() {
		return new HashSet<Feature>(FeatureModelManager.getInstance().getFeatures());
	}

	public boolean addElementToColor(Feature color, LElement element) {
		CLRAnnotatedSourceFile aColorSourceFile = element.getCLRFile();
		CompilationUnitColorManager aColorManager = (CompilationUnitColorManager) aColorSourceFile.getColorManager();
		aColorManager.addColor(element.getASTNode(), color);
		return true;
	}

	private boolean removeElementFromColor(Feature color, LElement element) {
		CLRAnnotatedSourceFile aColorSourceFile = element.getCLRFile();
		CompilationUnitColorManager aColorManager = (CompilationUnitColorManager) aColorSourceFile.getColorManager();
		if(aColorManager.getOwnColors(element.getASTNode()).contains(color)){
			aColorManager.removeColor(element.getASTNode(), color);
		}
		return false;

	}
}