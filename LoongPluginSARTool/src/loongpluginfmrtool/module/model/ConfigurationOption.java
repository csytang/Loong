package loongpluginfmrtool.module.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongplugin.source.database.model.LElement;

public class ConfigurationOption {
	public LElement aconfigOption;
	private Map<ConfigurationOption,ConfigRelation> aconfigOpToRelation;
	private Set<LElement>controledLElements;
	public ConfigurationOption(LElement pconfigOption){
		this.aconfigOption = pconfigOption;
		this.aconfigOpToRelation = new HashMap<ConfigurationOption,ConfigRelation>();
		this.controledLElements = new HashSet<LElement>();
	}
	public void addConfiguration(ConfigurationOption option,ConfigRelation relation){
		aconfigOpToRelation.put(option, relation);
	}
	public void setControledLElement(LElement element){
		controledLElements.clear();
		controledLElements.add(element);
	}
	public void addControledLElement(LElement newelement){
		controledLElements.add(newelement);
	}
	public void addControledLElement(Collection<LElement> newelements){
		controledLElements.addAll(newelements);
	}
	public ConfigRelation getConfigRelation(ConfigurationOption config){
		if(this.aconfigOpToRelation.containsKey(config)){
			return this.aconfigOpToRelation.get(config);
		}else{
			return ConfigRelation.UNRELATE;
		}
	}
}
