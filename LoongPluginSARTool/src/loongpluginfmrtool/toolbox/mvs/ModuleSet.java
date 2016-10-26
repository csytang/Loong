package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.model.Variability;

public class ModuleSet {
	private Set<Module> amodules;
	private ModuleDependencyTable table;
	private int index;
	private int totalmodulesize;
	
	private Set<Module>temp_modules = new HashSet<Module>();
	private Map<Module,Variability>temp_moduletoVariability = new HashMap<Module,Variability>();
	
	private Map<Module,Variability>moduletoVariability = new HashMap<Module,Variability>();

	
	public ModuleSet(Set<Module>pmodules,ModuleDependencyTable ptable,int pindex){
		this.amodules = pmodules;
		this.table = ptable;
		this.totalmodulesize = table.getTable().length;
		this.index = pindex;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public void setIndex(int pindex){
		this.index = pindex;
	}
	
	public int computeDependency(ModuleSet set){
		int count = 0;
		Set<Module> othermoduleset = set.getModuleSet();
		for(Module curr:amodules){
			for(Module other:othermoduleset){
				count+=table.getDependencyCount(curr, other);
			}
		}
		return count;
	}

	protected Set<Module> getModuleSet() {
		// TODO Auto-generated method stub
		return amodules;
	}

	public void addModuleSet(ModuleSet top) {
		// TODO Auto-generated method stub
		amodules.addAll(top.getModuleSet());
		
		updateVariability();
		removeInvalidConfigurations(moduletoVariability);
		//System.out.println("Finish the variability");
		
	}

	public double getProbability() {
		// TODO Auto-generated method stub 
		return ((double)amodules.size())/totalmodulesize;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ModuleSet){
			ModuleSet set_obj = (ModuleSet)obj;
			return set_obj.getIndex()==index;
		}else{
			return false;
		}
		
	}
	
	public int getVariabilityCount(){
		int totalvalidconfig = 0;
		for(Variability variablity:moduletoVariability.values()){
			totalvalidconfig+=variablity.getValidConfigurationCount();
		}
		return totalvalidconfig;
	}
	
	public Set<Variability> getVariabilities(){
		return new HashSet<Variability> (moduletoVariability.values());
	}
	
	
	protected void updateVariability(){
		for(Module module:amodules){
			Variability variability = module.getVariability();
			moduletoVariability.put(module, variability);
		}
	}
	
	public boolean hasVariabilityConflict(ModuleSet other){
		for(Variability varcurr:moduletoVariability.values()){
			for(Variability varother:other.moduletoVariability.values()){
				if(varcurr.hasConflict(varother)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 删除掉 有冲突的variability configuration 
	 * 删除中采取随机的政策
	 * @param moduletoVariability
	 */
	protected void removeInvalidConfigurations(Map<Module,Variability>needmoduletoVariability){
		ArrayList<Variability>variablityList = new ArrayList<Variability>(needmoduletoVariability.values());
		for(int i = 0;i < variablityList.size();i++){
			Variability variabiliti = variablityList.get(i);
			if(!temp_moduletoVariability.containsKey(variabiliti.getModule()))
				continue;
			for(int j = i+1;j < variablityList.size();j++){
				Variability variabilityj = variablityList.get(j);
				if(!temp_moduletoVariability.containsKey(variabilityj.getModule()));
				if(variabiliti.hasConflict(variabilityj)){
					temp_moduletoVariability.remove(variabilityj.getModule());
				}
			}
		}
	}
	
	/**
	 * 临时的加入 并没有真是的加入 只是在运算中应用
	 * @param modulefeature2
	 */
	public void tempmergeModuleSet(ModuleSet moduleset) {
		// TODO Auto-generated method stub
		temp_modules = new HashSet<Module>(amodules);
		temp_modules.addAll(moduleset.amodules);
		temp_moduletoVariability = new HashMap<Module,Variability>();
		for(Module module:temp_modules){
			Variability variability = module.getVariability();
			temp_moduletoVariability.put(module, variability);
		}
		
		removeInvalidConfigurations(temp_moduletoVariability);
		
	}
	
	/**
	 * 基于临时的值进行计算  
	 * @return
	 */
	public int tempgetVariabilityCount() {
		// TODO Auto-generated method stub
		return temp_moduletoVariability.size();
	}
	
}
