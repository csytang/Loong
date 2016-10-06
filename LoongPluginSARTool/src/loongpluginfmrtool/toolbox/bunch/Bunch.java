package loongpluginfmrtool.toolbox.bunch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.model.Module;

public class Bunch {

	private Map<Module,ModuledFeature>modulesToFeatures = new HashMap<Module,ModuledFeature>();
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private Map<Module, Integer>ModuleToIndex = new HashMap<Module, Integer>();
	private Map<Integer, ModuledFeature> indexToFeature = new HashMap<Integer, ModuledFeature>();
	
	private Set<Module>allmodules = new HashSet<Module>();
	private int cluster;
	private boolean debug = true;
	
	public Bunch(ModuleBuilder pbuilder,int pcluster){
		this.builder = pbuilder;
		this.cluster = pcluster;
	}
}
