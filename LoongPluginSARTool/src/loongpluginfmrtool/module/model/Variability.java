package loongpluginfmrtool.module.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Variability {
	/**
	 * 算一个varability
	 * 然后再算每一个configuration 的比重
	 */
	private Module module;
	private int totalValidConfig = 0;
	private Set<Configuration>configurations;
	private Set<ConfigurationOption>options;
	public Variability(Module pmodule){
		this.module = pmodule;
		this.configurations = new HashSet<Configuration>();
		this.options = this.module.getAllConfigurationOptions();
	}
	
	protected void Collect(){
		
	}
	

	public int getValidConfigurationCount() {
		// TODO Auto-generated method stub
		return totalValidConfig;
	}
	
}
