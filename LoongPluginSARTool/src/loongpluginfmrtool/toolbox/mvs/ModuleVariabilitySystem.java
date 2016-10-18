package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

public class ModuleVariabilitySystem {
	
	private Map<Module,ModuledFeature>modulesToFeatures = new HashMap<Module,ModuledFeature>();
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private Map<Module, Integer>ModuleToIndex = new HashMap<Module, Integer>();
	private Map<Integer, ModuledFeature> indexToFeature = new HashMap<Integer, ModuledFeature>();
	
	private Set<Module> allmodules = new HashSet<Module>();
	private ModuleDependencyTable dependency_table;
	private double[][] information_loss_table_array;
	private double[][] variability_loss_table_array;
	private int[][] moduledependency_table;
	private int[][] featuredependency_table_array;
	private double[][] featuredependency_table_normalized_array;
	private int size;
	private int totalsize;
	private double MAXVALUE = 1;
	private int cluster;
	private boolean debug = true;
	private double variabilitythreshold = 0.3;
	public ModuleVariabilitySystem(ModuleBuilder pbuilder,int pcluster){
		this.builder = pbuilder;
		this.cluster = pcluster;
		this.indexToModule = this.builder.getIndexToModule();
		this.allmodules = new HashSet<Module>(this.indexToModule.values());
		this.dependency_table = this.builder.getDependencyTable();
		this.totalsize = this.indexToModule.size();
		initialize();
		performClustering();
	}
	
	protected void initialize(){
		
	}
	
	protected void performClustering(){
		
	}
}