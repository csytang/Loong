package loongpluginfmrtool.module.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.Statement;

public class Configuration {
	private Map<ConfigurationOption,Boolean>configurationdetail = new HashMap<ConfigurationOption,Boolean>();
	private List<ConfigurationOption>optionlist = new LinkedList<ConfigurationOption>();
	private Module module;
	private Set<ConfigurationOption>selected = new HashSet<ConfigurationOption>();
	private Set<ConfigurationOption>unselected = new HashSet<ConfigurationOption>();
	public Configuration(Module pmodule,Map<ConfigurationOption,Boolean>pconfigurationlist){
		this.configurationdetail = pconfigurationlist;
		this.module = pmodule;
	}
	public Set<ConfigurationOption> getSelected(){
		return selected;
	}
	public Set<ConfigurationOption> getUnselected(){
		return unselected;
	}
	public Map<ConfigurationOption,Boolean> getConfigurationDetail(){
		return this.configurationdetail;
	}
	public void addNewOption(ConfigurationOption option,boolean selected){
		this.configurationdetail.put(option, selected);
	}
	public boolean conflictwith(Configuration other_config) {
		// TODO Auto-generated method stub
		Set<ConfigurationOption>curr_selected = selected;
		Set<ConfigurationOption>curr_unselected = unselected;
		Set<ConfigurationOption>other_selected = other_config.selected;
		Set<ConfigurationOption>other_unselected = other_config.unselected;
		boolean crosscheck = crosscheckselectedunselected(curr_selected,other_unselected);
		boolean crosscheck2 = crosscheckselectedunselected(other_selected,curr_unselected);
		return crosscheck&&crosscheck2;
	}
	
	protected boolean  crosscheckselectedunselected(Set<ConfigurationOption> enableset1,Set<ConfigurationOption> unableset2){
		boolean valid = true;
		if(enableset1.isEmpty()||unableset2.isEmpty()){
			return valid;
		}
		for(ConfigurationOption enable_option:enableset1){
			Set<Statement> enabledstatements = enable_option.getEnable_Statements();
			for(ConfigurationOption disable_option:unableset2){
				Set<Statement> disabledstatements = disable_option.getDisable_Statements();
				if(hasOverLap(enabledstatements,disabledstatements)){
					return false;
				}
			}
		}
		return valid;
	}
	private boolean hasOverLap(Set<Statement> enabledstatements,
			Set<Statement> disabledstatements) {
		// TODO Auto-generated method stub
		for(Statement disstatment:disabledstatements){
			if(enabledstatements.contains(disstatment))
				return true;
		}
		return false;
	}
}
