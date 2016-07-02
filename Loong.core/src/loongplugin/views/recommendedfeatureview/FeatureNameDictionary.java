package loongplugin.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.IJavaElement;

public class FeatureNameDictionary {
	
	private final Map<String,Set<IJavaElement>>featureNameDictionary = new HashMap<String,Set<IJavaElement>>();
	private final Map<String,Set<IJavaElement>>nonfeatureString = new HashMap<String,Set<IJavaElement>>();
	
	private static FeatureNameDictionary instance;
	
	public static FeatureNameDictionary getInstance(){
		if(instance==null)
			instance = new FeatureNameDictionary();
		return instance;
	}
	
	public void addDictBuiltElement(String associatedString,IJavaElement element){
		if(featureNameDictionary.containsKey(associatedString)){
			if(!featureNameDictionary.get(associatedString).contains(element)){
				Set<IJavaElement>bind = featureNameDictionary.get(associatedString);
				bind.add(element);
				featureNameDictionary.put(associatedString, bind);
			}
		}else{
			Set<IJavaElement>bind = new HashSet<IJavaElement>();
			bind.add(element);
			featureNameDictionary.put(associatedString, bind);
		}
	}
	
	public void addAnyElement(String associatedString,IJavaElement element){
		if(nonfeatureString.containsKey(associatedString)){
			if(!nonfeatureString.get(associatedString).contains(element)){
				Set<IJavaElement>bind = nonfeatureString.get(associatedString);
				bind.add(element);
				nonfeatureString.put(associatedString, bind);
			}
		}else{
			Set<IJavaElement>bind = new HashSet<IJavaElement>();
			bind.add(element);
			nonfeatureString.put(associatedString, bind);
			
		}
	}
	
	/*
	 * Insert a dict to dictionary
	 */
	public void mergeAndOptimizeDict(){
		// 1. 将所有的字典元素预先 构建
		
		
	}
	
	
	
}
