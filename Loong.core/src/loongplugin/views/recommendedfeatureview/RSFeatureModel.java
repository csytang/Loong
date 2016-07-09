package loongplugin.views.recommendedfeatureview;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class RSFeatureModel {
	
	public Set<RSFeature> allRSFeatures = new HashSet<RSFeature>();
	
	
	public RSFeatureModel(){
		
	}
	
	public void addRSFeature(RSFeature feature){
		allRSFeatures.add(feature);
	}

	public Set<RSFeature> getFeatures() {
		// TODO Auto-generated method stub
		return allRSFeatures;
	}
	
	
}
