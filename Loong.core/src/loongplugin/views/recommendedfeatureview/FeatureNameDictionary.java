package loongplugin.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import loongplugin.utils.StringListToFile;
import loongplugin.word2vec.word2vecUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;

public class FeatureNameDictionary {
	
	private final Map<String,Set<IJavaElement>>featureNameDictionary = new HashMap<String,Set<IJavaElement>>();
	private final Map<String,Set<IJavaElement>>nonfeatureString = new HashMap<String,Set<IJavaElement>>();
	private List<String>allStringList = new LinkedList<String>();
	private String tempfilePath = "input.text";
	private IProject project = null;
	private static FeatureNameDictionary instance;
	private IProgressMonitor monitor;
	
	public static FeatureNameDictionary getInstance(IProgressMonitor pmonitor){
		if(instance==null)
			instance = new FeatureNameDictionary(pmonitor);
		return instance;
	}
	public FeatureNameDictionary(IProgressMonitor pmonitor){
		this.monitor = pmonitor;
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
	public void normalizationTrack1(){
		// use lowercase to represent all words in feature name set
		Map<String,Set<IJavaElement>>featureNameDictionary_shadow = new HashMap<String,Set<IJavaElement>>();
		featureNameDictionary_shadow.putAll(featureNameDictionary);
		for(Map.Entry<String, Set<IJavaElement>>entry:featureNameDictionary_shadow.entrySet()){
			String str = entry.getKey();
			String str_ref = str.toLowerCase();
			if(featureNameDictionary.containsKey(str_ref)){
				Set<IJavaElement>associated = featureNameDictionary.get(str);
				Set<IJavaElement>exists = featureNameDictionary.get(str_ref);
				featureNameDictionary.remove(str);
				exists.addAll(associated);
				featureNameDictionary.put(str_ref, exists);
			}else{
				Set<IJavaElement>exists = featureNameDictionary.get(str);
				featureNameDictionary.remove(str);
				featureNameDictionary.put(str_ref, exists);
			}
		}
		
		Map<String,Set<IJavaElement>>nonfeatureString_shadow = new HashMap<String,Set<IJavaElement>>();
		nonfeatureString_shadow.putAll(nonfeatureString);
		for(Map.Entry<String, Set<IJavaElement>>entry:nonfeatureString_shadow.entrySet()){
			String str = entry.getKey();
			String str_ref = str.toLowerCase();
			if(nonfeatureString.containsKey(str_ref)){
				Set<IJavaElement>associated = nonfeatureString.get(str);
				Set<IJavaElement>exists = nonfeatureString.get(str_ref);
				nonfeatureString.remove(str);
				exists.addAll(associated);
				nonfeatureString.put(str_ref, exists);
			}else{
				Set<IJavaElement>exists = nonfeatureString.get(str);
				nonfeatureString.remove(str);
				nonfeatureString.put(str_ref, exists);
			}
		}
		
	}
	public void normalizationTrack2(){
		// use stemming to normalize all words in feature name set
		Map<String,Set<IJavaElement>>featureNameDictionary_shadow = new HashMap<String,Set<IJavaElement>>();
		featureNameDictionary_shadow.putAll(featureNameDictionary);
		for(Map.Entry<String, Set<IJavaElement>>entry:featureNameDictionary_shadow.entrySet()){
			String str = entry.getKey();
			String str_ref = str.toLowerCase();
			if(featureNameDictionary.containsKey(str_ref)){
				Set<IJavaElement>associated = featureNameDictionary.get(str);
				Set<IJavaElement>exists = featureNameDictionary.get(str_ref);
				featureNameDictionary.remove(str);
				exists.addAll(associated);
				featureNameDictionary.put(str_ref, exists);
			}else{
				Set<IJavaElement>exists = featureNameDictionary.get(str);
				featureNameDictionary.remove(str);
				featureNameDictionary.put(str_ref, exists);
			}
		}
		
	}
	
	/*
	 * Insert a dict to dictionary
	 */
	public void mergeAndOptimizeDict(){
		// 1. 将所有的字典元素预先 构建
		//
		assert(project!=null);
		IPath path = project.getLocation();
		path = path.append("/"+tempfilePath);
		tempfilePath = path.toOSString();
		String tempfileoutPath = tempfilePath.substring(0, tempfilePath.length()-"input.text".length());
		tempfileoutPath+="vector.text";
		if(project.getFile("vector.text").exists()){
			IFile vectorfile = project.getFile("vector.text");
			try {
				vectorfile.delete(true, this.monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringListToFile strFile = new StringListToFile(allStringList,tempfilePath);
		strFile.writeToFile();
		
		word2vecUtil w2vUtil = new word2vecUtil(tempfilePath,tempfileoutPath);
	}

	public void convertToList() {
		// TODO Auto-generated method stub
		if(!featureNameDictionary.isEmpty())
			allStringList.addAll(featureNameDictionary.keySet());
		if(!nonfeatureString.isEmpty())
			allStringList.addAll(nonfeatureString.keySet());
	}

	public void setProject(IProject selectProject) {
		// TODO Auto-generated method stub
		this.project = selectProject;
	}
	
	
	
}
