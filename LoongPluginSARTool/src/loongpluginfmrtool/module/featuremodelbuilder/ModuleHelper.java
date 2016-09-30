package loongpluginfmrtool.module.featuremodelbuilder;

import java.util.Map;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.model.Module;

public class ModuleHelper {
	private Module amodule;
	private ModuleBuilder abuilder;
	private double probability;
	private Map<Integer, Module>indexToModule;
	private double[][] normalizedtable;
	private int size;
	private ModuleDependencyTable table;
	public ModuleHelper(Module pmodule,ModuleBuilder pbuilder){
		this.amodule  = pmodule;
		this.abuilder = pbuilder;
		this.table = pbuilder.getDependencyTable();
		build();
	}
	protected void build(){
		this.indexToModule = this.abuilder.getIndexToModule();
		this.size = indexToModule.size();
		this.probability = (double)1.0/this.size;
		this.normalizedtable = table.getNormalizedTable();
	}
}
