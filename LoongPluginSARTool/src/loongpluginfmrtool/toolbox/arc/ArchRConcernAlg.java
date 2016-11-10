package loongpluginfmrtool.toolbox.arc;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LRelation;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.toolbox.softarch.arcade.clustering.ConcernClusteringRunner;
import loongpluginfmrtool.toolbox.softarch.arcade.clustering.FastFeatureVectors;
import loongpluginfmrtool.toolbox.softarch.arcade.clustering.FeatureVectorMap;
import loongpluginfmrtool.toolbox.softarch.arcade.config.Config;
import loongpluginfmrtool.toolbox.softarch.arcade.config.Config.StoppingCriterionConfig;
import loongpluginfmrtool.toolbox.softarch.arcade.functiongraph.TypedEdgeGraph;
import loongpluginfmrtool.toolbox.softarch.arcade.topics.TopicModelExtractionMethod;

public class ArchRConcernAlg {

	private ApplicationObserver aAO;
	private ModuleBuilder abuilder;
	private Set<LElement> allelements;
	private Map<CompilationUnit,Set<CompilationUnit>>dependsrelationmapping = new HashMap<CompilationUnit,Set<CompilationUnit>>();
	private int numTopics = 0;
	private ProgramDatabase aDB;
	private Set<File>allsourcefiles = new HashSet<File>();
	private Map<CompilationUnit,File> compilationUnitFile = new HashMap<CompilationUnit,File>();
	private Set<LRelation>allcontainsrelations = new HashSet<LRelation>();
	public static FastFeatureVectors ffVecs = null;
	private String projectPath;
	private String sourcecodeDir;
	private String topicModelFilename;
	private String docTopicsFilename;
	private String topWordsFilename;
	private String arcClustersFilename;
	private IProject aProject;
	private Shell shell;
	private boolean configrationset = false;
	/**
	 * 
	 * @param pAO application obersever by default
	 * @param pbuilder mobuild builder
	 * @param pstoppingCriterion the stopping criterion for architecture recovery with concern algorithm
	 * @param pcluster # of clusters
	 */
	public ArchRConcernAlg(ApplicationObserver pAO,ModuleBuilder pbuilder){
		this.aAO = pAO;
		this.aDB = this.aAO.getProgramDatabase();
		this.aProject = this.aAO.getInitializedProject();
		this.abuilder = pbuilder;
			
		preconfig();
		configuration();
		run();
	}
	
	protected void preconfig(){
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
		sourcecodeDir = projectPath;
		
		assert isValidFilePath(sourcecodeDir);
		
		allelements = aDB.getAllElements();
		// creating the basic dependency information in the map mode
		TypedEdgeGraph typedEdgeGraph = new TypedEdgeGraph();
		
		
		// build the relation mapping
		for(LElement element:allelements){
			CompilationUnit sourceunit = element.getCompilationUnit();
			if(!compilationUnitFile.containsKey(sourceunit)){
				ICompilationUnit iunit = (ICompilationUnit) sourceunit.getJavaElement();
				IPath path = iunit.getPath().makeAbsolute();
				String fullsourcePath = projectPath+path.toOSString();
				File absolutefile = new File(fullsourcePath);
				if(!allsourcefiles.contains(absolutefile)){
					allsourcefiles.add(absolutefile);
				}
				compilationUnitFile.put(sourceunit, absolutefile);
			}
			for(LRelation relation:allcontainsrelations){
					Set<LElement> alltargetelement = aAO.getRange(element, relation);
					if(alltargetelement!=null){
						for(LElement target:alltargetelement){
							CompilationUnit targetunit = target.getCompilationUnit();
							if(!compilationUnitFile.containsKey(targetunit)){
								ICompilationUnit iunit = (ICompilationUnit) targetunit.getJavaElement();
								IPath path = iunit.getPath().makeAbsolute();
								String fullsourcePath = projectPath+path.toOSString();
								File absolutefile = new File(fullsourcePath);
								if(!allsourcefiles.contains(absolutefile)){
									allsourcefiles.add(absolutefile);
								}
								compilationUnitFile.put(targetunit, absolutefile);
							}
							if(dependsrelationmapping.containsKey(sourceunit)){
									Set<CompilationUnit>targestunits = dependsrelationmapping.get(sourceunit);
									targestunits.add(targetunit);
									dependsrelationmapping.put(sourceunit, targestunits);
							}else{
									Set<CompilationUnit>targestunits = new HashSet<CompilationUnit>();
									targestunits.add(targetunit);
									dependsrelationmapping.put(sourceunit, targestunits);
							}
							
						}
				}
				
			}
		}
		
		// put the dependence mapping into type edge graph
		for(Map.Entry<CompilationUnit, Set<CompilationUnit>>entry:dependsrelationmapping.entrySet()){
			CompilationUnit sourceunit = entry.getKey();
			String sourcename = getFullName(sourceunit);
			Set<CompilationUnit> targetunits = entry.getValue();
			for(CompilationUnit unit:targetunits){
				typedEdgeGraph.addEdge("depends", sourcename, getFullName(unit));
			}
		}
		
		
		// build the topic based vectors
		
		FeatureVectorMap fvMap = new FeatureVectorMap(typedEdgeGraph);
		ffVecs = fvMap.convertToFastFeatureVectors();
		
		numTopics = (int)(((double)allsourcefiles.size())* 0.18);
		
		// write the archifect 
		int numClusters = (int) ((double) allsourcefiles.size() * .20); // number of clusters to obtain is based
								// on the number of entities
		// all folders should be scanned and output redirected
		topicModelFilename = projectPath + File.separatorChar + numTopics + "_topics.mallet";
		docTopicsFilename = projectPath + File.separatorChar + numTopics + "-doc-topics.txt";
		topWordsFilename = projectPath + File.separatorChar + numTopics + "_top_words_per_topic.txt";
		arcClustersFilename =projectPath + File.separatorChar + numTopics + "_topics_"
				+numClusters + "_arc_clusters.rsf";
	}
	
	private void configuration(){
		WizardDialog dialog = new WizardDialog(shell,new ARCConfigurationWizard(this,aProject,aAO,shell,topicModelFilename,docTopicsFilename,arcClustersFilename,numTopics,allsourcefiles.size()));
		dialog.create();
		dialog.open();
	}
	
	public void setConfigurationFile(IFile cfgfile){
		String fulllocationpath = projectPath+File.separatorChar+cfgfile.getLocation().toOSString();
		Config.initConfigFromFile(fulllocationpath);
		configrationset = true;
	}
	
	private void run(){
		if(configrationset){
			ConcernClusteringRunner runner = new ConcernClusteringRunner(ffVecs,TopicModelExtractionMethod.MALLET_API, sourcecodeDir,sourcecodeDir+"/base", numTopics, topicModelFilename, docTopicsFilename, topWordsFilename);
	
		}
	}
	private boolean isValidFilePath(String filePath) {
		// TODO Auto-generated method stub
		File file = new File(filePath);
		return file.exists();
	}

	public String getFullName(CompilationUnit unit){
		String packageName = unit.getPackage().getName().toString();
		List types = unit.types();    
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class  
		String fullName = packageName+"."+typeDec.getName().toString();
		return fullName;
	}
}
