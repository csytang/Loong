package loongplugin.views.recommendedfeatureview;

import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

public class RSFeature {
	private String afeatureName;
	private double aweight = 0.0;
	private Map<IJavaElement,Set<ASTNode>>abindings;
	
	public RSFeature(String pfeatureName,double pweight,Map<IJavaElement,Set<ASTNode>>pbindings){
		afeatureName = pfeatureName;
		aweight = pweight;
		abindings = pbindings;
	}
	
	public String getFeatureName(){
		return afeatureName;
	}
	
	public double getWeight(){
		return aweight;
	}
	
	public Map<IJavaElement,Set<ASTNode>> getAllBindings(){
		return abindings;
	}
	
}
