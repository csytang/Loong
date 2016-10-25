package loongpluginfmrtool.toolbox.mvs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

public class ModuleVariabilitySystem {
	
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private Set<Module> allmodules = new HashSet<Module>();
	private ModuleDependencyTable dependency_table;	
	private int cluster;
	public ModuleVariabilitySystem(ModuleBuilder pbuilder,int pcluster){
		this.builder = pbuilder;
		this.cluster = pcluster;
		this.indexToModule = this.builder.getIndexToModule();
		this.allmodules = new HashSet<Module>(this.indexToModule.values());
		this.dependency_table = this.builder.getDependencyTable();
		initialize();
		performClustering();
	}
	
	protected void initialize(){
		
	}
	
	protected void performClustering(){
		
	}
}