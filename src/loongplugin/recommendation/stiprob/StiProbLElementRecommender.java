package loongplugin.recommendation.stiprob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.jdt.internal.ui.util.MainMethodSearchEngine;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.recommendation.RecommendationContext;
import loongplugin.recommendation.recommender.AbstractLElementRecommnder;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;
import loongplugin.source.database.model.SlicingVariable;
import loongplugin.source.database.model.LRelation.Type;
import loongplugin.utils.LElementMethodFinder;

public class StiProbLElementRecommender extends AbstractLElementRecommnder{
	
	private FeatureModel featuremodel;
	private ProgramDatabase programDB;
	private Set<LElement> allelements;
	private boolean debug = true;
	private IProject aProject;
	private LFlyweightElementFactory lflyweightElementFactory;
	private double threshold = 0.6;
	
	
	private OutputStream output =null;
	
	public StiProbLElementRecommender(){
		
		featuremodel = FeatureModelManager.getInstance().getFeatureModel();
		programDB = ApplicationObserver.getInstance().getProgramDatabase();
		aProject = ApplicationObserver.getInstance().getInitializedProject();
		lflyweightElementFactory = ApplicationObserver.getInstance().getLFlyweightElementFactory();
		
		computeslicingvariables();	
	}
	
	/**
	 * 计算分割变量
	 */
	public void computeslicingvariables(){
		allelements = new HashSet<LElement>();
		allelements = programDB.getAllElements();
		LFlyweightElementFactory elementFactory = ApplicationObserver.getInstance().getLFlyweightElementFactory();
		for(LElement element:allelements){
			element.computeSlicingVariable(elementFactory);
		}
	}
	
	
	
	
	public boolean hasmethodReference(IMethod method){
		CallHierarchy callHierarchy = CallHierarchy.getDefault();
		 
		IMember[] members = {method};
		 
		MethodWrapper[] methodWrappers = callHierarchy.getCallerRoots(members);
		HashSet<IMethod> callers = new HashSet<IMethod>();
		  for (MethodWrapper mw : methodWrappers) {
		    MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
		    HashSet<IMethod> temp = getIMethods(mw2);
		    callers.addAll(temp);    
		}
		//当为空时 表示没有引用
		return !callers.isEmpty();
	}
	
	HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
		 HashSet<IMethod> c = new HashSet<IMethod>(); 
		 for (MethodWrapper m : methodWrappers) {
			 IMethod im = getIMethodFromMethodWrapper(m);	
			 if (im != null) {
				 c.add(im);
			 }
		 }
		 return c;
	}
	IMethod getIMethodFromMethodWrapper(MethodWrapper m) {
		  try {
			  IMember im = m.getMember();
			  if (im.getElementType() == IJavaElement.METHOD) {
				  return (IMethod)m.getMember();
			  }
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
		  return null;
	}
	
	
	
	private IJavaProject convertToIJavaProject(IProject project){
		if(project!=null){
			try {
				if(project.hasNature(JavaCore.NATURE_ID)){
					IJavaProject targetProject = JavaCore.create(project);
					return targetProject;
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}
	
	
	@SuppressWarnings("incomplete-switch")
	public double computeforwardProbability(LElement element,LElement forwardelement,LRelation lrelation){
		
		Map<IBinding,Set<ASTNode>> elementBindingLElement = element.computeIBindingASTs();
		Map<IBinding,Set<ASTNode>> forwardBindingLElement = forwardelement.computeIBindingASTs();
		Map<IMethodBinding,Set<ASTNode>> elementMethodBindingLElement = collectIMethodBinding(elementBindingLElement);
		Map<ITypeBinding,Set<ASTNode>> elementTypeBindingLElement = collectITypeBinding(elementBindingLElement);
		Map<IVariableBinding,Set<ASTNode>> elementVariableBindingLElement = collectIVariableBinding(elementBindingLElement);
		
		Map<IMethodBinding,Set<ASTNode>> forwardMethodBindingLElement = collectIMethodBinding(forwardBindingLElement);
		Map<ITypeBinding,Set<ASTNode>> forwardTypeBindingLElement = collectITypeBinding(forwardBindingLElement);
		Map<IVariableBinding,Set<ASTNode>> forwardVariableBindingLElement = collectIVariableBinding(forwardBindingLElement);
		
		LICategories element_category = element.getCategory();
		LICategories forward_category = forwardelement.getCategory();
		
		double share = 0.0;
		double total = 0.0;
		
		switch(lrelation){
		case DECLARES_TYPE: 
		case DECLARES_IMPORT:
		case DECLARES_FIELD:
		case DECLARES_METHOD:
		case DECLARES_LOCAL_VARIABLE:
		case DECLARES_FIELD_ACCESS:
		case DECLARES_METHOD_ACCESS:
		case DECLARES_TYPE_ACCESS:
		case DECLARES_LOCAL_VARIABLE_ACCESS:
		case DECLARES_PARAMETER:

		case DECLARES:
			return 1.0;
		
		

		case ACCESS_TYPE:
		case ACCESS_TYPE_TRANSITIVE:{ //从 类型 到 类型的访问
			for(ITypeBinding typebinding:elementTypeBindingLElement.keySet()){
				
				if(forwardTypeBindingLElement.containsKey(typebinding)){
					share+=elementTypeBindingLElement.get(typebinding).size();
				}
				total+=elementTypeBindingLElement.get(typebinding).size();
				
			}
			if(total==0){
				return 1;
			}else{
				return share/total;
			}
		}
		case ACCESS_FIELD:
		case ACCESS_FIELD_TRANSITIVE:{
			// A-->B  A 是field 那么 要知道 A中多少个field
			for(IVariableBinding variablebinding:elementVariableBindingLElement.keySet()){
				if(variablebinding.isField()){
					if(forwardVariableBindingLElement.containsKey(variablebinding)){
						share+=elementVariableBindingLElement.get(variablebinding).size();
					}
					total+=elementVariableBindingLElement.get(variablebinding).size();
				}
			}
			if(total==0){
				return 1;
			}else{
				return share/total;
			}
		}
		
		case ACCESS_LOCAL_VARIABLE:
		case ACCESS_LOCAL_VARIABLE_TRANSITIVE:{
			for(IVariableBinding variablebinding:elementVariableBindingLElement.keySet()){
				if(variablebinding.isField())
					continue;
				if(forwardVariableBindingLElement.containsKey(variablebinding)){
					share+=elementVariableBindingLElement.get(variablebinding).size();
				}
				total+=elementVariableBindingLElement.get(variablebinding).size();
				
			}
			if(total==0){
				return 1;
			}else{
				return share/total;
			}
		}
		case ACCESS_METHOD:
		case ACCESS_METHOD_TRANSITIVE:{
			for(IMethodBinding methodbinding:elementMethodBindingLElement.keySet()){
				if(forwardMethodBindingLElement.containsKey(methodbinding)){
					share+=elementMethodBindingLElement.get(methodbinding).size();
				}
				total+=elementMethodBindingLElement.get(methodbinding).size();
			}
			if(total==0){
				return 1;
			}else{
				return share/total;
			}
		}
		
		case OVERRIDES_METHOD:{
			return 1.0;
		}
		case OVERRIDES_METHOD_TRANSITIVE:{
			return 1.0;
		}
		case IMPLEMENTS_METHOD:{
			return 1.0;
		}
		case IMPLEMENTS_METHOD_TRANSITIVE:{
			return 1.0;
		}

		case EXTENDS_TYPE:{
			return 1.0;
		}
		
		case IMPLEMENTS_TYPE:{
			return 1.0;
		}
		
		case EXTENDS_TYPE_TRANSITIVE:{
			return 1.0;
		}
		case IMPLEMENTS_TYPE_TRANSITIVE:{
			return 1.0;
		}

		case DECLARES_TYPE_TRANSITIVE:
		case DECLARES_FIELD_TRANSITIVE:
		case DECLARES_METHOD_TRANSITIVE:
		case DECLARES_LOCAL_VARIABLE_TRANSITIVE:
			return 1.0;
		
		case REFERENCES:
		case ACCESSES:
		case BELONGS_TO:
		case REQUIRES:{
			switch(forward_category){
			
			case FIELD:{
				for(IVariableBinding variablebinding:elementVariableBindingLElement.keySet()){
					if(variablebinding.isField()){
						if(forwardVariableBindingLElement.containsKey(variablebinding)){
							share+=elementVariableBindingLElement.get(variablebinding).size();
						}
						total+=elementVariableBindingLElement.get(variablebinding).size();
					}
				}
				if(total==0){
					return 1;
				}else{
					return share/total;
				}
			}
			case METHOD:{
				for(IMethodBinding methodbinding:elementMethodBindingLElement.keySet()){
					if(forwardMethodBindingLElement.containsKey(methodbinding)){
						share+=elementMethodBindingLElement.get(methodbinding).size();
					}
					total+=elementMethodBindingLElement.get(methodbinding).size();
				}
				if(total==0){
					return 1;
				}else{
					return share/total;
				}
			}
			case TYPE:
			case CLASS:{
				for(ITypeBinding typebinding:elementTypeBindingLElement.keySet()){
					
					if(forwardTypeBindingLElement.containsKey(typebinding)){
						share+=elementTypeBindingLElement.get(typebinding).size();
					}
					total+=elementTypeBindingLElement.get(typebinding).size();
					
				}
				if(total==0){
					return 1;
				}else{
					return share/total;
				}
			}
			case LOCAL_VARIABLE:{
				for(IVariableBinding variablebinding:elementVariableBindingLElement.keySet()){
					if(variablebinding.isField())
						continue;
						if(forwardVariableBindingLElement.containsKey(variablebinding)){
							share+=elementVariableBindingLElement.get(variablebinding).size();
						}
						total+=elementVariableBindingLElement.get(variablebinding).size();
					
				}
				if(total==0){
					return 1;
				}else{
					return share/total;
				}
			}
			case IMPORT:{
				return 1.0;
			}
			case COMPILATION_UNIT:{
				//error
				try {
					throw new Exception("Uncepted forward element");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		}
		
		default:
			return 0;
		}
		
		
	}
	/**
	 * backwardelement---> elements
	 * @param element
	 * @param backwardelement
	 * @param lrelation
	 * @return
	 */
	public double computebackwardProbability(LElement element,LElement backwardelement,LRelation lrelation){
		Set<LElement> sourceElements = AOB.getRange(element,lrelation);
		double sum = 0.0;
		double share = 0.0;
		for(LElement source:sourceElements){
			double value = computeforwardProbability(source, element,lrelation.getInverseRelation());
			if(source.equals(backwardelement)){
				share = value;
			}
			sum+=value;
		}
		if(sum==0.0)
			return 1.0;
		return share/sum;
	}
	
	
	
	public Map<IMethodBinding,Set<ASTNode>> collectIMethodBinding(Map<IBinding,Set<ASTNode>> elementBindingLElement){
		Map<IMethodBinding,Set<ASTNode>> methodbindings_collection = new HashMap<IMethodBinding,Set<ASTNode>>();
		for(IBinding binding:elementBindingLElement.keySet()){
			if(binding instanceof IMethodBinding){
				Set<ASTNode>associatedASTNodes = elementBindingLElement.get(binding);
				methodbindings_collection.put((IMethodBinding)binding, associatedASTNodes);
			}
		}
		return methodbindings_collection;
	}
	public Map<ITypeBinding,Set<ASTNode>> collectITypeBinding(Map<IBinding,Set<ASTNode>> elementBindingLElement){
		Map<ITypeBinding,Set<ASTNode>> typebindings_collection = new HashMap<ITypeBinding,Set<ASTNode>>();
		for(IBinding binding:elementBindingLElement.keySet()){
			if(binding instanceof ITypeBinding){
				Set<ASTNode>associatedASTNodes = elementBindingLElement.get(binding);
				typebindings_collection.put((ITypeBinding)binding, associatedASTNodes);
			}
		}
		return typebindings_collection;
	}
	public Map<IVariableBinding,Set<ASTNode>> collectIVariableBinding(Map<IBinding,Set<ASTNode>> elementBindingLElement){
		Map<IVariableBinding,Set<ASTNode>> variablebindings_collection = new HashMap<IVariableBinding,Set<ASTNode>>();
		for(IBinding binding:elementBindingLElement.keySet()){
			if(binding instanceof IVariableBinding){
				Set<ASTNode>associatedASTNodes = elementBindingLElement.get(binding);
				variablebindings_collection.put((IVariableBinding)binding, associatedASTNodes);
			}
		}
		return variablebindings_collection;
	}
	
	private boolean slicecontains(Set<SlicingVariable> bindset,SlicingVariable slice) {
		// TODO Auto-generated method stub
		for(SlicingVariable bindslice:bindset){
			if(bindslice.equals(slice)){
				return true;
			}
		}
		return false;
	}

	
	@Override
	public Map<LElement, RecommendationContext> getRecommendations(LElement element, Feature feature)  {
		// TODO Auto-generated method stub
		Map<LElement,RecommendationContext> recommendations = new HashMap<LElement,RecommendationContext>();
		/*
		 * get after relation for the element.
		*/
		Set<LRelation> validTransponseRelations = LRelation.getAllRelations(element.getCategory(), true, false);
				
		// ADDED AFTER EVALUATION
		validTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), true, true));
/*
		for (LICategories cat : element.getSubCategories()) {
			validTransponseRelations.addAll(LRelation.getAllRelations(cat,true, false));
			// ADDED AFTER EVALUATION
			validTransponseRelations.addAll(LRelation.getAllRelations(cat,true, true));
		}*/
				
		// check all relations
		for (LRelation tmpTransRelation : validTransponseRelations) {
				// get the forward elements
				/*
				 * 做转化  将当前的LElement 通过关系 转化到相关的 element
				*/
			Set<LElement> forwardElements = new HashSet<LElement>();
			try{
				forwardElements = AOB.getRange(element,tmpTransRelation);
			}catch(Exception e){
				continue;
			}
			Set<LElement> validRecommendationElements = new HashSet<LElement>();
				int forwardColorElements = 0;
				int forwardNonColorElements = 0;

				// 查看有多少 LElement 已经被上色
				// int validRecommendationCount = 0;
				for (LElement forwardElement : forwardElements) {
					if (isValidRecommendation(forwardElement, feature))
						validRecommendationElements.add(forwardElement);	
				}

				// if they are all already in color, skip to next relation
				if (validRecommendationElements.size() == 0)
						continue;
				for (LElement validForwardElement : validRecommendationElements) {
					double value = 0.0;
					
					if(!tmpTransRelation.isDirect()){
						value = computebackwardProbability(element,validForwardElement,tmpTransRelation);
						if(value > threshold){
							RecommendationContext context = new RecommendationContext(element, tmpTransRelation.getName(),
									getRecommendationType(), value);
							recommendations.put(validForwardElement, context);
						}
					}else{
						value = computeforwardProbability(element,validForwardElement,tmpTransRelation);
						if(value > threshold){
							RecommendationContext context = new RecommendationContext(element, tmpTransRelation.getName(),
									getRecommendationType(), value);
							recommendations.put(validForwardElement, context);
						}
					}
					
				}
		}
		
		return recommendations;
	}
	
	

	@Override
	public String getRecommendationType() {
		// TODO Auto-generated method stub
		return "RB";
	}
	
	

}
