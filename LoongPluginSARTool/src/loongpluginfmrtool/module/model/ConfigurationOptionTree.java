package loongpluginfmrtool.module.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;

public class ConfigurationOptionTree {
	private Set<ConfigurationOption> roots;
	private Module module;
	private Map<ConfigurationOption,HashSet<ConfigurationOption>> successors;//后继
	private Map<ConfigurationOption,HashSet<ConfigurationOption>> preprocessors;//前驱
	private Set<ConfigurationOption>optionset;
	public ConfigurationOptionTree(Module pmodule){
		this.module = pmodule;
		this.successors =new HashMap<ConfigurationOption,HashSet<ConfigurationOption>>();
		this.preprocessors = new HashMap<ConfigurationOption,HashSet<ConfigurationOption>>();
		this.optionset = module.getAllConfigurationOptions();
		// set a template collection of root and remove unneed ones
		this.roots = new HashSet<ConfigurationOption>(optionset);
		build();
		
	}
	
	
	public Set<ConfigurationOption> getRoots(){
		return roots;
	}
	
	public void build(){
		// option --> target
		for(ConfigurationOption source:optionset){
			Set<ConfigurationRelationLink>connectedlinks = source.getlinks();
			for(ConfigurationRelationLink link:connectedlinks){
				ConfigurationOption target = link.getTargetConfigurationOption();
				if(successors.containsKey(source)){
					HashSet<ConfigurationOption>targetset = successors.get(source);
					targetset.add(source);
					successors.put(source, targetset);
				}else{
					HashSet<ConfigurationOption>targetset = new HashSet<ConfigurationOption>();
					targetset.add(target);
					successors.put(source, targetset);
				}
				if(preprocessors.containsKey(target)){
					HashSet<ConfigurationOption>sourceset = preprocessors.get(target);
					sourceset.add(source);
					successors.put(target, sourceset);
				}else{
					HashSet<ConfigurationOption>sourceset = new HashSet<ConfigurationOption>();
					sourceset.add(source);
					successors.put(target, sourceset);
				}
				if(this.roots.contains(target)){
					this.roots.remove(target);
				}
			}
		}
	}

	public Set<ConfigurationOption> getChildren(ConfigurationOption option_top) {
		// TODO Auto-generated method stub
		return successors.get(option_top);
	}
}
