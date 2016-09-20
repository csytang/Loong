package loongpluginfmrtool.module.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import loongplugin.source.database.model.LElement;
import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.Module;

public class ExternalConfBuilder {
	private Module module;
	private ModuleBuilder modulebuilder;
	private Map<LElement,Set<ConfigurationOption>> method_configurations = new HashMap<LElement,Set<ConfigurationOption>>();
	private Map<ConfigurationOption,LElement>configuration_method = new HashMap<ConfigurationOption,LElement>();
	
	public ExternalConfBuilder(Module pmodule){
		this.module = pmodule;
		this.modulebuilder = ModuleBuilder.instance;
		this.method_configurations = pmodule.getMethod_To_Configuration();
		this.configuration_method = pmodule.getConfiguration_To_Method();
	}
	public void parse(){
		
	}

}
