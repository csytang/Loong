package loongpluginfmrtool.module;

import java.util.HashSet;
import java.util.Set;

import loongpluginfmrtool.module.model.Module;

public class ModuledFeature {
	private String featurename = "unknown";
	private Set<Module>modules = new HashSet<Module>();
	public ModuledFeature(){
		
	}
	public ModuledFeature(Module module){
		this.modules.add(module);
	}
	public boolean containModule(Module module){
		return this.modules.contains(module);
	}
	
	public void addModule(Module module){
		this.modules.add(module);
	}
	
	public void mergeModuledFeature(ModuledFeature other){
		this.modules.addAll(other.modules);
	}
	
	public Set<Module> getModules(){
		return this.modules;
	}
}
