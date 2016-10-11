package loongpluginfmrtool.module.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Configuration {
	private Map<ConfigurationOption,Boolean>configurationdetail = new HashMap<ConfigurationOption,Boolean>();
	private List<ConfigurationOption>optionlist = new LinkedList<ConfigurationOption>();
	private Module module;
	public Configuration(Module pmodule,Map<ConfigurationOption,Boolean>pconfigurationlist){
		this.configurationdetail = pconfigurationlist;
		this.module = pmodule;
	}
	public Map<ConfigurationOption,Boolean> getConfigurationDetail(){
		return this.configurationdetail;
	}
	public void addNewOption(ConfigurationOption option,boolean selected){
		this.configurationdetail.put(option, selected);
	}
}
