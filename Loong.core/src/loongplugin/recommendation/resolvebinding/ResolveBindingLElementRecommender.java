package loongplugin.recommendation.resolvebinding;

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
import loongplugin.utils.LElementMethodFinder;

public class ResolveBindingLElementRecommender extends AbstractLElementRecommnder{
	
	private FeatureModel featuremodel;
	private ProgramDatabase programDB;
	private Set<LElement> allelements;
	private Set<LElement> allPureCallerElements = new HashSet<LElement>();
	private Set<LElement> forwardSeeds = new HashSet<LElement>();
	private Set<LElement> mainmethodelements = new HashSet<LElement>();
	private boolean debug = true;
	private IProject aProject;
	private LFlyweightElementFactory lflyweightElementFactory;
	private double threshold = 0.7;
	private double posterthreshold = 0.03;
	private File file = new File("/Users/tangchris/Desktop/posterierproability");
	private OutputStream output =null;
	/**
	 *  (1) 如果一个类 被定义 那么他的 probability 就是 这个类的probability
	 */
	/**
	 * (1)先计算 conditional probability
	 * (2) 
	 */
	public ResolveBindingLElementRecommender(){
		
		featuremodel = FeatureModelManager.getInstance().getFeatureModel();
		programDB = ApplicationObserver.getInstance().getProgramDatabase();
		aProject = ApplicationObserver.getInstance().getInitializedProject();
		lflyweightElementFactory = ApplicationObserver.getInstance().getLFlyweightElementFactory();
		try {
			output = new FileOutputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		computeslicingvariables();
		try {
			compPriorProbability();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		compProbability();
		
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
	/**
	 * 计算每一个programming element的概率
	 */
	public void compProbability(){
		
		mainmethodelements = findMainMethodInProject(aProject);
		forwardSeeds.clear();
		/*
		 * 此时分为两种情况 
		 * （1）当程序中存在main函数时，使用main函数为出发点 采取forward propagation
		 * （2）当程序中不存在main函数时，将程序中所有的纯调用函数设置为虚拟的起点 将这些的probability都设置
		 * 为1 然后使用forward propagation
		 */
		if(mainmethodelements.isEmpty()){
			for(LElement element:allPureCallerElements){
				element.setProbability(null,1.0);
			}
			forwardSeeds.addAll(allPureCallerElements);
		}else{
			for(LElement mainelement:mainmethodelements){
				mainelement.setProbability(null,1.0);
			}
			forwardSeeds.addAll(mainmethodelements);
		}
		/*
		 * 在设置后的情况进行forward propagation
		 */
		forwardPropagation();
		
	}
	
	/**
	 * 使用forward propagation 来计算
	 */
	private void forwardPropagation(){
		Queue<LElement>computeQueue = new LinkedList<LElement>();
		computeQueue.addAll(forwardSeeds);
		
		Set<LElement>hasbeenAnalyzed = new HashSet<LElement>();
		hasbeenAnalyzed.addAll(computeQueue);
		// 弹出栈中内容
		while(!computeQueue.isEmpty()){
			LElement top = computeQueue.poll();
			if(top!=null){
				Set<LRelation> validTransponseRelations = LRelation.getAllRelations(top.getCategory(), true, true);
				// 对于所有关联的 下一个节点进行 probability 计算
				for(LRelation lrelation:validTransponseRelations){
					Set<LElement> forwardElements = AOB.getRange(top,lrelation);
					if(forwardElements==null)
						continue;
					for(LElement affected:forwardElements){
						double conditionalprobability = top.getPrior_Probability(affected);
						if(Double.isNaN(conditionalprobability)){
							try{
								throw new Exception("Error");
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						double affectedLElementprobability = top.getProbability()*conditionalprobability;
						try{
							if(Double.isNaN(affectedLElementprobability)){
								throw new Exception("Error");
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						affected.setProbability(affected,affectedLElementprobability);
						if(debug){
							System.out.println("The probability of "+affected.getId()+" is: "+affected.getProbability());
						}
						if(!hasbeenAnalyzed.contains(affected)){
							/*
							 * 如果没有被加入到分析栈 则加入,否则会陷入死循环
							 */
							computeQueue.offer(affected);
							hasbeenAnalyzed.add(affected);
						}
					}
				}
			}
		}
	}
	
	public IType[] findMain(IJavaProject javaProject,
			IProgressMonitor monitor) throws Exception {
		monitor.beginTask("Search main types in project source", 1);
		try {

			MainMethodSearchEngine engine = new MainMethodSearchEngine();
			int constraints = IJavaSearchScope.SOURCES;
			// constraints |= IJavaSearchScope.APPLICATION_LIBRARIES;
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(
					new IJavaElement[] { javaProject }, constraints);
			IType[] types =  engine.searchMainMethods(monitor, scope, 1);
			return types;

		} finally {
			monitor.done();
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
	
	private Set<LElement> findMainMethodInProject(final IProject aProject) {
		// TODO Auto-generated method stub
		Set<LElement> allCalleeMethodElements = new HashSet<LElement>();
		
		final Set<LElement> mainElements = new HashSet<LElement>();
		final IJavaProject javaproject = convertToIJavaProject(aProject);
		// 使用search engine 机制
		WorkspaceJob op = new WorkspaceJob("Find Main Method Action") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				// TODO Auto-generated method stub
				IType[] types = null;
				Set<IMethod> mainmethodset = new HashSet<IMethod>();
				
				if(javaproject==null){
					return Status.CANCEL_STATUS;
				}
				try {
					types = findMain(javaproject,monitor);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(types!=null){
					if(types.length!=0){
						for(IType itype:types){
							IMethod[]methods = itype.getMethods();
							for(IMethod imethod:methods){
								if(imethod.isMainMethod()){
									mainmethodset.add(imethod);
								}
							}
						}
					}
				}
				if(!mainmethodset.isEmpty()){
					for(LElement element:allelements){
						if(element.getASTNode() instanceof MethodDeclaration){
							MethodDeclaration methodDeclareAST = (MethodDeclaration)element.getASTNode();
							IMethod imethod = (IMethod) methodDeclareAST.resolveBinding().getJavaElement();
							if(mainmethodset.contains(imethod)){
								mainElements.add(element);
							}
						}
					}
				}
				return Status.OK_STATUS;
			}
			
		};
		op.setUser(true);
		op.schedule();
		//如果 在这个工作进程中找到了主函数  就返回
		
		try {
			op.join();//等待进程完成
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!mainElements.isEmpty()){
			return mainElements;
		}
		
		// 设计一个子任务 
		for(LElement element:allelements){
			
			Map<LRelation, Set<LElement>> relationmapelemeent = programDB.getRelationMap(element);
			
			if(element.getASTNode() instanceof MethodDeclaration){
				allPureCallerElements.add(element);
				// 使用SearchEngine
				IMethod imethod = element.getIMethod();
				
				if(hasmethodReference(imethod)){
					allCalleeMethodElements.add(element);
				}
				
			}
		}
		// 求差集
		allPureCallerElements.removeAll(allCalleeMethodElements);
		return mainElements;
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
	/**
	 * 构造先验概率
	 * 就要构造从这个当前点出发的关系
	 * @throws IOException 
	 */
	public void compPriorProbability() throws IOException{
		
		
		for(LElement element:allelements){
			/*
			 * get after relation for the element.
			 */
			Set<LRelation> validTransponseRelations = LRelation.getAllRelations(element.getCategory(), true, true);
			
			for(LRelation lrelation:validTransponseRelations){
				/*
				 * 这里我们去除反向类型 只保留正向的关系					 
				 */
				Set<LElement> forwardElements = AOB.getRange(element,lrelation);
				if(forwardElements==null)
					continue;
				for(LElement affected:forwardElements){
					double probability = 0;
					if(debug){
						System.out.println("The conditional probability from"+element.getId()+" to"
								+ "\n"+affected.getId()+":");
					}
					probability = computeConditionalProbability(element,affected,lrelation);
					
					if(debug){
						System.out.println(probability);
					}
					element.setPrior_Probability(affected, probability);
				}
			}
			
		}
		
		
	}
	
	@SuppressWarnings("incomplete-switch")
	public double computeConditionalProbability(LElement element,LElement affected,LRelation lrelation){
		
		
		Map<IBinding,Set<ASTNode>> elementBindingLElement = element.computeIBindingASTs();
		Map<IBinding,Set<ASTNode>> affectedBindingLElement = affected.computeIBindingASTs();
		Map<IMethodBinding,Set<ASTNode>> elementMethodBindingLElement = collectIMethodBinding(elementBindingLElement);
		Map<ITypeBinding,Set<ASTNode>> elementTypeBindingLElement = collectITypeBinding(elementBindingLElement);
		Map<IVariableBinding,Set<ASTNode>> elementVariableBindingLElement = collectIVariableBinding(elementBindingLElement);
		
		double allcountforstarter;
		double totalsize = 0;
		double shares = 0.0;
		double totalshares = 0.0;
		int counter = 0;
		for(IBinding bind:affectedBindingLElement.keySet()){
			totalsize = 0;
			shares = 0.0;
			Set<ASTNode>elementasts = new HashSet<ASTNode>();
			
			for(IBinding elementbind:elementBindingLElement.keySet()){
				if(elementbind==null){
					continue;
				}
				if(elementbind.isEqualTo(bind)){
					elementasts = elementBindingLElement.get(elementbind);
				}
			}
			if(!elementasts.isEmpty()){
				int astsize = elementasts.size();
				if(bind instanceof IMethodBinding){
					for(IMethodBinding methodbinding:elementMethodBindingLElement.keySet()){
						totalsize+=elementMethodBindingLElement.get(methodbinding).size();
					}
				}else if(bind instanceof IVariableBinding){
					for(IVariableBinding variablebinding:elementVariableBindingLElement.keySet()){
						totalsize+=elementVariableBindingLElement.get(variablebinding).size();
					}
				}else if(bind instanceof ITypeBinding){
					for(ITypeBinding typebinding:elementTypeBindingLElement.keySet()){
						totalsize+=elementTypeBindingLElement.get(typebinding).size();
					}
				}
				if(totalsize!=0)
					shares = astsize/totalsize;
				else
					shares = 0;
				totalshares+=shares;
				counter++;
			}
			
		}
		if(counter==0){
			return 1.0;
		}else{
			return totalshares/counter;
		}
		
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
		Set<LRelation> validTransponseRelations = LRelation.getAllRelations(element.getCategory(), true, false);
		validTransponseRelations.addAll(LRelation.getAllRelations(element.getCategory(), true, true));
		int forwardColorElements = 0;
		int forwardNonColorElements = 0;
		Map<LElement,Double> elementProbability = new HashMap<LElement,Double>();
		
		for (LRelation tmpTransRelation : validTransponseRelations) {
			// get the forward elements
			/*
			if(inDeclareRelation(tmpTransRelation)){
				continue;
			}*/
			
			Set<LElement> forwardElements = AOB.getRange(element,tmpTransRelation);
			Set<LElement> validRecommendationElements = new HashSet<LElement>();
			
			for (LElement forwardElement : forwardElements) {
				if (isInFeature(forwardElement, feature)) {
					forwardColorElements++;
					continue;
				}
				
				if (isValidRecommendation(forwardElement, feature))
					validRecommendationElements.add(forwardElement);
			}
			
			
			// if they are all already in color, skip to next relation
			if (validRecommendationElements.size() == 0)
				continue;
			
			//double maxConditionalProbability = 0;
			//LElement maxvadlidForwardElement = null;
			elementProbability.clear();
			double max  = -1;
			
			for (LElement validForwardElement : validRecommendationElements) {
				if(!element.hasPrior_Probability(validForwardElement)){
					double backwardProbability = 0.0;
					double probability_Parent = validForwardElement.getProbability();
					double probability_Child = element.getProbability();
					assert validForwardElement.hasPrior_Probability(element)==true;
					double prior_probability = validForwardElement.getPrior_Probability(element);
					double posterioriprobability = 0.0;
					
					/*
					 * This goes with the global probability approach
					 */
					
					
					if(probability_Parent<=0.001||probability_Child<=0.001){
						posterioriprobability = prior_probability*computeLocalProbability(validForwardElement,element,tmpTransRelation);
						if(Double.isNaN(posterioriprobability)){
							posterioriprobability = prior_probability;
						}
						if(posterioriprobability>=posterthreshold){
							RecommendationContext recontext = new RecommendationContext(element, tmpTransRelation.getName(),
									getRecommendationType(), posterioriprobability);
							recommendations.put(validForwardElement, recontext);
						}
					}else{
						posterioriprobability = prior_probability*probability_Parent/probability_Child;
						if(Double.isNaN(posterioriprobability)){
							posterioriprobability = prior_probability;
						}
						if(posterioriprobability>=threshold){
							RecommendationContext recontext = new RecommendationContext(element, tmpTransRelation.getName(),
									getRecommendationType(), posterioriprobability);
							recommendations.put(validForwardElement, recontext);
						}
					}/*
					if(probability_Child!=0)
						posterioriprobability = prior_probability*probability_Parent/probability_Child;
					else
						posterioriprobability = prior_probability;
					if(posterioriprobability>=posterthreshold){
						RecommendationContext recontext = new RecommendationContext(element, tmpTransRelation.getName(),
								getRecommendationType(), posterioriprobability);
						recommendations.put(validForwardElement, recontext);
					}*/
					try {
						output.write((""+posterioriprobability).getBytes());
						output.write("\n".getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					/*
					if(posterioriprobability>=posterthreshold){
						RecommendationContext recontext = new RecommendationContext(element, tmpTransRelation.getName(),
								getRecommendationType(), posterioriprobability);
						recommendations.put(validForwardElement, recontext);
					}
					System.out.println(posterioriprobability);
					 */
					element.setPosteriori_Probability(validForwardElement, posterioriprobability);
					
					
					elementProbability.put(validForwardElement, posterioriprobability);
					
					
				}else{
					double forwardprobability = element.getPrior_Probability(validForwardElement);
					System.out.println(forwardprobability);
					if(forwardprobability>threshold){
						RecommendationContext recontext = new RecommendationContext(element, tmpTransRelation.getName(),
								getRecommendationType(), forwardprobability);
						recommendations.put(validForwardElement, recontext);
					}
					elementProbability.put(validForwardElement, forwardprobability);
					
					if(forwardprobability>max){
						max = forwardprobability;
					}
					
				}
				
			}
			
			/*
			for (LElement validForwardElement : validRecommendationElements) {
				if(elementProbability.get(validForwardElement)==max){
					RecommendationContext recontext = new RecommendationContext(element, tmpTransRelation.getName(),getRecommendationType(),max);
					recommendations.put(validForwardElement, recontext);
				}
			}*/
			
			
			
			
		}
		
		return recommendations;
	}
	
	/**
	 * this method is initally added for computing local probability purpose
	 * @param validForwardElement
	 * @param element
	 * @param tmpTransRelation
	 * @return
	 */
	private double computeLocalProbability(LElement parent,LElement element, LRelation tmpTransRelation) {
		// TODO Auto-generated method stub
		Set<LElement> allParents = AOB.getRange(element,tmpTransRelation);
		/*
		double parentprobability = parent.getPrior_Probability(element);
		double childprobability = 0.0;
		for(LElement parentelement:allParents){
			double conditionalpro = parentelement.getPrior_Probability(element);
			childprobability+=conditionalpro;
		}*/
		
		
		Map<LElement,Double>elementToProbability = new HashMap<LElement,Double>();
		for(LElement parent_m:allParents){
			Set<LElement>allPrents_parent_m = AOB.getRange(parent_m,tmpTransRelation);
			double tempprobability = 0.0;
			for(LElement grand:allPrents_parent_m){
				elementToProbability.put(grand, 1.0);
				tempprobability+=grand.getPrior_Probability(parent_m);
			}
			elementToProbability.put(parent_m, tempprobability);
			
		}
		double parentprobability = parent.getPrior_Probability(element)*elementToProbability.get(parent);
		double childprobability = 0.0;
		for(LElement parentelement:allParents){
			double conditionalpro = parentelement.getPrior_Probability(element)*elementToProbability.get(parentelement);
			childprobability+=conditionalpro;
		}
		
		return parentprobability/childprobability;
	}

	@Override
	public String getRecommendationType() {
		// TODO Auto-generated method stub
		return "RB";
	}
	
	

}
