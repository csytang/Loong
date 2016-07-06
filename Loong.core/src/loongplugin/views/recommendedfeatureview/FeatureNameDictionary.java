package loongplugin.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

public class FeatureNameDictionary {
	
	private final Map<String,Map<IJavaElement,Set<ASTNode>>>featureNameDictionary = new HashMap<String,Map<IJavaElement,Set<ASTNode>>>();
	private final Map<String,Map<IJavaElement,Set<ASTNode>>>nonfeaturetextMapping = new HashMap<String,Map<IJavaElement,Set<ASTNode>>>();
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
	
	public void addDictBuiltElement(String associatedString,IJavaElement element,ASTNode astnode){
		if(featureNameDictionary.containsKey(associatedString)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = featureNameDictionary.get(associatedString);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(associatedString, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(associatedString, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.add(astnode);
			iJavaElementBindings.put(element, bindingastnodes);
			featureNameDictionary.put(associatedString, iJavaElementBindings);
		}
	}
	public void addDictBuiltElement(String associatedString,IJavaElement element,Set<ASTNode> astnodes){
		if(featureNameDictionary.containsKey(associatedString)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = featureNameDictionary.get(associatedString);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(associatedString, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				featureNameDictionary.put(associatedString, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.addAll(astnodes);
			iJavaElementBindings.put(element, bindingastnodes);
			featureNameDictionary.put(associatedString, iJavaElementBindings);
		}
	}
	
	
	public void addAnyElement(String associatedString,IJavaElement element,ASTNode astnode){
		if(nonfeaturetextMapping.containsKey(associatedString)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = nonfeaturetextMapping.get(associatedString);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(associatedString, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.add(astnode);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(associatedString, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.add(astnode);
			iJavaElementBindings.put(element, bindingastnodes);
			nonfeaturetextMapping.put(associatedString, iJavaElementBindings);
		}
	}
	public void addAnyElement(String associatedString,IJavaElement element,Set<ASTNode> astnodes){
		if(nonfeaturetextMapping.containsKey(associatedString)){
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = nonfeaturetextMapping.get(associatedString);
			if(iJavaElementBindings.containsKey(element)){
				Set<ASTNode> bindingastnodes = iJavaElementBindings.get(element);
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(associatedString, iJavaElementBindings);
			}else{
				Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
				bindingastnodes.addAll(astnodes);
				iJavaElementBindings.put(element, bindingastnodes);
				nonfeaturetextMapping.put(associatedString, iJavaElementBindings);
			}
		}else{
			Map<IJavaElement,Set<ASTNode>> iJavaElementBindings = new HashMap<IJavaElement,Set<ASTNode>>();
			Set<ASTNode> bindingastnodes = new HashSet<ASTNode>();
			bindingastnodes.addAll(astnodes);
			iJavaElementBindings.put(element, bindingastnodes);
			nonfeaturetextMapping.put(associatedString, iJavaElementBindings);
		}
	}
	
	public void normalizationTrack1(){
		// use lowercase to represent all words in feature name set
		
	}
	public void normalizationTrack2(){
		// use stemming to normalize all words in feature name set
		
	}
	
	/*
	 * Insert a dict to dictionary
	 */
	public void mergeAndOptimizeDict(){
		
	}


	public void setProject(IProject selectProject) {
		// TODO Auto-generated method stub
		this.project = selectProject;
	}
	
	
	
}
