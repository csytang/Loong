package loongplugin.performance;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongplugin.LoongPlugin;
import loongplugin.CIDEbridge.CIDEASTNodeCollector;
import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.utils.EmbeddedASTNodeCollector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Document;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;



public class ComputePerformanceJob extends WorkspaceJob {

	
	private FeatureModel fmodel;
	private IProject sourceProject;
	private IProject benmarkProject;
	//对于一个 feature来说 有占有的 line of code
	private Map<Feature,Integer>benchmarkStatistic = new HashMap<Feature,Integer>();
	private Map<Feature,Integer>miningStatistic = new HashMap<Feature,Integer>();
	private Map<Feature,Integer>featureCorrectRecommendLOC = new HashMap<Feature,Integer>();
	private Map<Feature,Double>featurerecall = new HashMap<Feature,Double>();
	private Map<Feature,Double>featureprecision = new HashMap<Feature,Double>();
	private Map<Feature,Map<String,Double>> results;
	public ComputePerformanceJob(IProject source,IProject benchmark) {
		super("Computing mining performance for :"+source.getName());
		// TODO Auto-generated constructor stub
		this.sourceProject = source;
		this.benmarkProject = benchmark;
		this.fmodel = FeatureModelManager.getInstance(sourceProject).getFeatureModel();
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		IJavaProject sourceJavaProject = JavaCore.create(sourceProject);
		IJavaProject benchmarkJavaProject = JavaCore.create(benmarkProject);
		int compUnitCount = countJavaProject(sourceJavaProject);
		monitor.beginTask("Computing performance", compUnitCount+1);
		InitalizedStatistic();
		monitor.worked(1);
		
		for (IPackageFragmentRoot root : sourceJavaProject.getPackageFragmentRoots()) {
			if (monitor.isCanceled())
				break;
			if (!root.exists())
				continue;
			if (root.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;
			IPackageFragmentRoot targetRoot = null;
			if (root.getResource() instanceof IFolder) {
				IPath path = root.getPath().makeAbsolute();
				path = path.removeFirstSegments(1);// remove project
				IFolder folder = benmarkProject.getFolder(path);
				 targetRoot = benchmarkJavaProject.getPackageFragmentRoot(folder);
			}else if(root.getResource() instanceof IProject){
				 targetRoot = benchmarkJavaProject.getPackageFragmentRoot(benmarkProject);
			}
			comparePackageFragementRoot(sourceJavaProject, root, targetRoot, monitor);
		}
		
		// 计算 recall 和 precision
		computeFeatureRecall();
		computeFeaturePrecision();
		
		computeFeatureMiningResultView();
		
		// 写入结果 xml中
	    @SuppressWarnings("unused")
		ResultXML resultxml = new ResultXML(results,sourceProject);
		monitor.done();
		
		return Status.OK_STATUS;
	}
	
	
	private void computeFeatureMiningResultView() {
		// TODO Auto-generated method stub
		
		results = new HashMap<Feature,Map<String,Double>>();
		for(Feature f:fmodel.getFeatures()){
			Map<String,Double> statisticresult = new HashMap<String,Double>();
			final String Attr_BenchMark = "benchMark_Annotated_LOC";
			final String Attr_Mining = "mining_Annotated_LOC";
			final String Attr_CorrectRecommend = "correct_Recommend_LOC";
			final String Attr_Precision = "precision";
			final String Attr_Recall = "recall";
			final String Attr_F1 = "F1_Score";
			statisticresult.put(Attr_BenchMark, benchmarkStatistic.get(f).doubleValue());
			statisticresult.put(Attr_Mining, miningStatistic.get(f).doubleValue());
			statisticresult.put(Attr_CorrectRecommend, featureCorrectRecommendLOC.get(f).doubleValue());
			double precision_f = featureprecision.get(f);
			double recall_f = featurerecall.get(f);
			statisticresult.put(Attr_Precision, precision_f);
			statisticresult.put(Attr_Recall, recall_f);
			double F_1 = 2*precision_f*recall_f/(recall_f+precision_f);
			statisticresult.put(Attr_F1, F_1);
			results.put(f, statisticresult);
		}
	}
	

	private void InitalizedStatistic() {
		// TODO Auto-generated method stub
		for(Feature f:fmodel.getFeatures()){
			benchmarkStatistic.put(f, 0);
			miningStatistic.put(f, 0);
			featureCorrectRecommendLOC.put(f, 0);
		}
	}

	private void comparePackageFragementRoot(IJavaProject sourceJavaProject,
			IPackageFragmentRoot root, IPackageFragmentRoot targetRoot,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		for (IPackageFragment pkg : sourceJavaProject.getPackageFragments()) {
			if (monitor.isCanceled())
				return;
			if (pkg.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;
			if (!root.getPackageFragment(pkg.getElementName()).exists())
				continue;

			IPackageFragment targetPackage = targetRoot.createPackageFragment(
					pkg.getElementName(), true,  new SubProgressMonitor(monitor, 0));
			try {
				comparePackage(pkg, targetPackage, monitor);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (pkg.getCompilationUnits().length == 0)
				pkg.delete(false,  new SubProgressMonitor(monitor, 0));
		}
	}

	private void comparePackage(IPackageFragment sourcePackage, IPackageFragment benchmarkPackage, IProgressMonitor monitor) throws JavaModelException, CoreException, IOException {
		// TODO Auto-generated method stub
		//IPath targetProjectParth = benchmarkPackage.getJavaProject().getPath();
		// 收集在benchmark中得数据
		for (Object object:benchmarkPackage.getNonJavaResources()){
			if(object instanceof IFile){
				// 1. 获取在benchmark 中的 clr文件 
				IFile benchmarkfile = (IFile)object;
				IPath relativefilePath = benchmarkfile.getProjectRelativePath();
				String fileExtension = benchmarkfile.getFileExtension();
			
				if(fileExtension.equals("clr")){
					// 2. 统计 benchmark中得annotation 信息
					IPath relativeJavaPath = relativefilePath.removeFileExtension().addFileExtension("java");
					
					IFile sourceJavafile = benmarkProject.getFile(relativeJavaPath);
					IFile miningclrfile = sourceProject.getFile(relativefilePath);
					
					ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(sourceJavafile);
					ASTParser parser = ASTParser.newParser(AST.JLS3);
				    parser.setSource(compilationUnit);
				    parser.setResolveBindings(false);
				    CompilationUnit cunit = (CompilationUnit) parser.createAST(null);
				    
				    Set<ASTNode> compilationASTNodeSet = EmbeddedASTNodeCollector.collectASTNodes(compilationUnit);
					
					Map<String,ASTNode> tempASTIDcache = computeASTID(compilationASTNodeSet);
					
					try {
						benchmarkCLRFileDataCollection(benchmarkfile,cunit,tempASTIDcache,compilationUnit);
						//如果在这路径下对应的source clr 存在则进行比较
						if(miningclrfile.exists()){
							
							computeCorrectPredictionLOC(benchmarkfile,miningclrfile,tempASTIDcache,cunit,compilationUnit);
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
		}
		
		// 收集在mining中得数据
		for (Object object:sourcePackage.getNonJavaResources()){
			if(object instanceof IFile){
				// 1. 获取在benchmark 中的 clr文件 
				IFile miningfile = (IFile)object;
				IPath relativefilePath = miningfile.getProjectRelativePath();
				String fileExtension = miningfile.getFileExtension();
			
				if(fileExtension.equals("clr")){
					// 2. 统计 benchmark中得annotation 信息
					IPath relativeJavaPath = relativefilePath.removeFileExtension().addFileExtension("java");
					IFile miningclrfile = sourceProject.getFile(relativefilePath);
					IFile sourceJavafile = sourceProject.getFile(relativeJavaPath);
					
					ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(sourceJavafile);
					ASTParser parser = ASTParser.newParser(AST.JLS3);
				   
				    parser.setResolveBindings(true);
				    parser.setSource(compilationUnit);
				    
				    CompilationUnit cunit = (CompilationUnit) parser.createAST(null);
				    
				    Set<ASTNode> compilationASTNodeSet = EmbeddedASTNodeCollector.collectASTNodes(compilationUnit);
					
					Map<String,ASTNode> tempASTIDcache = computeASTID(compilationASTNodeSet);
					
					try {
						miningCLRFileDataCollection(miningclrfile,cunit,tempASTIDcache,compilationUnit);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
		}
		
	}
	
	private void computeCorrectPredictionLOC(IFile benchmarkCLRfile,IFile miningclrfile,Map<String,ASTNode> tempASTIDcache,CompilationUnit cunit,ICompilationUnit compilationUnit) throws CoreException, IOException, ClassNotFoundException{
		InputStream is = benchmarkCLRfile.getContents(true);
		ObjectInputStream out = new ObjectInputStream(is);
		
		long version = out.readLong();
		
		HashMap<ASTID, Set<Feature>> benchmarknode2colors = (HashMap<ASTID, Set<Feature>>) out.readObject();
		out.close();
		
		HashMap<Feature,Set<ASTID>> transposedbenchmarkfeature2nodes = transposeMap(benchmarknode2colors);
		
		InputStream minis = miningclrfile.getContents(true);
		ObjectInputStream minout = new ObjectInputStream(minis);
		
		long minversion = minout.readLong();
		
		HashMap<ASTID, Set<Feature>> miningnode2colors = (HashMap<ASTID, Set<Feature>>) minout.readObject();
		minout.close();
		HashMap<Feature,Set<ASTID>> transposedminingeature2nodes = transposeMap(miningnode2colors);
		
		for(Feature f:transposedminingeature2nodes.keySet()){
			Set<ASTID> predictionsf = transposedminingeature2nodes.get(f);
			Set<Integer>allpredictionLines = getLines(predictionsf,tempASTIDcache,cunit,compilationUnit);
			if(transposedbenchmarkfeature2nodes.containsKey(f)){
				Set<ASTID> benchmarkf = transposedbenchmarkfeature2nodes.get(f);
				Set<Integer>allbenchmarkLines = getLines(benchmarkf,tempASTIDcache,cunit,compilationUnit);
				allpredictionLines.retainAll(allbenchmarkLines);
				int size = allpredictionLines.size();
				
				featureCorrectRecommendLOC.put(f, featureCorrectRecommendLOC.get(f)+size);
			}
			
		}
		
		
		
		
		
	}
	
	
	private Set<Integer> getLines(Set<ASTID> predictions,Map<String,ASTNode> tempASTIDcache,CompilationUnit cunit,ICompilationUnit compilationUnit) throws JavaModelException {
		// TODO Auto-generated method stub
		Set<Integer>allLineNumbers = new HashSet<Integer>();
		for(ASTID id:predictions){
			ASTNode node = tempASTIDcache.get(id.id);
			int startLine = 0;
			int endLine  = 0;
			if(node==null){
				//处理Typebinding 的问题
				String astid = id.id;
				if(astid.contains("::")){
					int startIndex = astid.indexOf("::");
					astid = astid.substring(0,startIndex+"::".length());
					astid += "null";
					node = tempASTIDcache.get(astid);
				}else{
					continue;
				}
			}
			
			if(node instanceof CompilationUnit){
				startLine = 0;
				Document doc = new Document(compilationUnit.getSource());
				endLine = doc.getNumberOfLines();
			}else{
				startLine = cunit.getLineNumber(node.getStartPosition())-1;
				endLine = cunit.getLineNumber(node.getStartPosition()+node.getLength())-1;
			}
			for(int line=startLine;line <= endLine;line++){
				allLineNumbers.add(line);
			}
			
		}
		
		return allLineNumbers;
	}

	private HashMap<Feature,Set<ASTID>> transposeMap(HashMap<ASTID, Set<Feature>> node2colors){
		HashMap<Feature,Set<ASTID>> featureToASTIDs = new HashMap<Feature,Set<ASTID>>();
		for(ASTID id:node2colors.keySet()){
			Set<Feature>features = node2colors.get(id);
			for(Feature f:features){
				if(featureToASTIDs.containsKey(f)){
					featureToASTIDs.get(f).add(id);
				}else{
					Set<ASTID>idset = new HashSet<ASTID>();
					idset.add(id);
					featureToASTIDs.put(f, idset);
				}
			}
		}
		
		return featureToASTIDs;
	}
	
	
	
	private void benchmarkCLRFileDataCollection(IFile benchmarkCLRfile,CompilationUnit cunit,Map<String,ASTNode> tempASTIDcache,ICompilationUnit compilationUnit) throws CoreException, IOException, ClassNotFoundException{
		/*
		 * 收集benchmark中信息
		 */
		
		InputStream is = benchmarkCLRfile.getContents(true);
		ObjectInputStream out = new ObjectInputStream(is);
		int linesDetected = 0;
		long version = out.readLong();
		
		HashMap<ASTID, Set<Feature>> benchmarknode2colors = (HashMap<ASTID, Set<Feature>>) out.readObject();
		out.close();
		Map<Feature,Set<Integer>>featureToLineNumbers = new HashMap<Feature,Set<Integer>>();
		for(ASTID id:benchmarknode2colors.keySet()){
			String ASTIDString = id.id;
			ASTNode astnode = tempASTIDcache.get(ASTIDString);
			//TODO import declaration problem
			int startLine = 0;
			int endLine  = 0;
			if(astnode==null){
				//处理Typebinding 的问题
				if(ASTIDString.contains("::")){
					int startIndex = ASTIDString.indexOf("::");
					ASTIDString = ASTIDString.substring(0,startIndex+"::".length());
					ASTIDString += "null";
					astnode = tempASTIDcache.get(ASTIDString);
				}else{
					continue;
				}
			}
			if(astnode instanceof CompilationUnit){
				startLine = 0;
				Document doc = new Document(compilationUnit.getSource());
				endLine = doc.getNumberOfLines();
			}else{
				startLine = cunit.getLineNumber(astnode.getStartPosition())-1;
				endLine = cunit.getLineNumber(astnode.getStartPosition()+astnode.getLength())-1;
			}
			
			Set<Feature>associatedFeatureToId = benchmarknode2colors.get(id);
			for(Feature f:associatedFeatureToId){
				if(featureToLineNumbers.containsKey(f)){
					Set<Integer> lines = featureToLineNumbers.get(f);
					for(int line = startLine;line <= endLine;line++){
						lines.add(line);
					}
					featureToLineNumbers.put(f, lines);
				}else{
					Set<Integer> lines = new HashSet<Integer>();
					for(int line = startLine;line <= endLine;line++){
						lines.add(line);
					}
					featureToLineNumbers.put(f, lines);
				}
			}
		}
		for(Feature f:featureToLineNumbers.keySet()){
			if(featureToLineNumbers.get(f)!=null){
				benchmarkStatistic.put(f, benchmarkStatistic.get(f)+featureToLineNumbers.get(f).size());
				
			}
		}
		
		
	}
	
	
	private void miningCLRFileDataCollection(IFile miningclrfile,CompilationUnit cunit,Map<String,ASTNode> tempASTIDcache,ICompilationUnit compilationUnit) throws CoreException, IOException, ClassNotFoundException{
		/*
		 * 收集mining中信息
		 */
		
		CLRAnnotatedSourceFile clrfile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(miningclrfile);
		CompilationUnitColorManager clrcolormanager = (CompilationUnitColorManager)clrfile.getColorManager();
		
		Map<Feature,Set<Integer>>featureToLineNumbers = new HashMap<Feature,Set<Integer>>();
		for(ASTID id:clrcolormanager.getNode2Colors().keySet()){
			String ASTIDString = id.id;
			ASTNode astnode = tempASTIDcache.get(ASTIDString);
			if(astnode==null){
				//处理Typebinding 的问题
				if(ASTIDString.contains("::")){
					int startIndex = ASTIDString.indexOf("::");
					ASTIDString = ASTIDString.substring(0,startIndex+"::".length());
					ASTIDString += "null";
					astnode = tempASTIDcache.get(ASTIDString);
				}else{
					continue;
				}
			}
			int startLine = 0;
			int endLine = 0;
			if(astnode instanceof CompilationUnit){
				startLine = 0;
				Document doc = new Document(compilationUnit.getSource());
				endLine = doc.getNumberOfLines();
			}else{
				 startLine = cunit.getLineNumber(astnode.getStartPosition())-1;
				 endLine = cunit.getLineNumber(astnode.getStartPosition()+astnode.getLength())-1;
			}
			Set<Feature>associatedFeatureToId = clrcolormanager.getNode2Colors().get(id);
			for(Feature f:associatedFeatureToId){
				if(featureToLineNumbers.containsKey(f)){
					Set<Integer> lines = featureToLineNumbers.get(f);
					for(int line = startLine;line <= endLine;line++){
						lines.add(line);
					}
					featureToLineNumbers.put(f, lines);
				}else{
					Set<Integer> lines = new HashSet<Integer>();
					for(int line = startLine;line <= endLine;line++){
						lines.add(line);
					}
					featureToLineNumbers.put(f, lines);
				}
			}
		}
		for(Feature f:featureToLineNumbers.keySet()){
			if(featureToLineNumbers.get(f)!=null){
				miningStatistic.put(f, miningStatistic.get(f)+featureToLineNumbers.get(f).size());
				
			}
		}
		
	}
	
	
	
	
	
	public Map<String,ASTNode> computeASTID(Set<ASTNode> compilationASTNodeSet){
		Map<String,ASTNode> tempASTIDcache = new HashMap<String, ASTNode>();
		tempASTIDcache.clear();
		
		for(ASTNode node:compilationASTNodeSet){
			String nodeastID = ASTID.id(node).id;
			tempASTIDcache.put(nodeastID, node);
		}
		return tempASTIDcache;
	}
	
	
	private int countJavaProject(IJavaProject sourceJavaProject)
			throws CoreException {
		int sum = 0;
		for (IPackageFragmentRoot root : sourceJavaProject
				.getPackageFragmentRoots()) {
			if (!root.exists())
				continue;
			if (root.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;

			sum += countPackageFragementRoot(sourceJavaProject, root);
		}
		return sum;
	}
	
	
	private int countPackageFragementRoot(IJavaProject sourceJavaProject,
			IPackageFragmentRoot sourceRoot) throws CoreException {
		int sum = 0;
		for (IPackageFragment pkg : sourceJavaProject.getPackageFragments()) {
			if (pkg.getKind() == IPackageFragmentRoot.K_BINARY)
				continue;
			if (!sourceRoot.getPackageFragment(pkg.getElementName()).exists())
				continue;

			sum += countPackage(pkg);
		}
		return sum;
	}
	
	private int countPackage(IPackageFragment sourcePackage)
			throws CoreException {
		return sourcePackage.getCompilationUnits().length;
	}
	
	
	private void computeFeatureRecall(){
		/**
		 * Recall = |relevant document N retrieved document|/|relevant document|
		 */
		for(Feature f:fmodel.getFeatures()){
			double benchmarkpre = benchmarkStatistic.get(f);
			double recall= 0;
			if(benchmarkpre ==0 ){
				recall = 1.0;
			}else
				recall = (double)featureCorrectRecommendLOC.get(f)/benchmarkpre;
			featurerecall.put(f, recall);
		}
	}
	
	private void computeFeaturePrecision(){
		/**
		 * Recall = |relevant document N retrieved document|/|retrieved document|
		 */
		for(Feature f:fmodel.getFeatures()){
			double mingmarkpre = miningStatistic.get(f);
			double precision = 0;
			if(miningStatistic.get(f)==0){
				precision = 1;
			}else
				precision = (double)featureCorrectRecommendLOC.get(f)/mingmarkpre;
			featureprecision.put(f, precision);
		}
	}

	public Map<Feature, Map<String, Double>> getOverAllResult() {
		// TODO Auto-generated method stub
		return results;
	}
}
