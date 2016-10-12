package loongpluginfmrtool.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.model.Variability;

public class ModuledFeature {
	private String featurename = "unknown";
	private Set<Module>modules = new HashSet<Module>();
	private int totalsize;
	private Map<Module,Variability>moduletoVariability = new HashMap<Module,Variability>();
	private Set<Module>temp_modules = new HashSet<Module>();
	private Map<Module,Variability>temp_moduletoVariability = new HashMap<Module,Variability>();
	private boolean needvariability;
	
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
	
	public ModuledFeature(Module module,int ptotalsize,boolean pneedvariability){
		this.modules.add(module);
		this.totalsize = ptotalsize;
		this.needvariability = pneedvariability;
		if(this.needvariability){
			updateVariability();
		}
	}
	
	protected void updateVariability(){
		for(Module module:modules){
			Variability variability = module.getVariability();
			moduletoVariability.put(module, variability);
		}
	}
	
	
	public double getProbability(){
		return ((double)modules.size())/this.totalsize;
	}
	public boolean containModule(Module module){
		return this.modules.contains(module);
	}
	
	public void addModule(Module module){
		this.modules.add(module);
	}
	
	public void mergeModuledFeature(ModuledFeature other){
		this.modules.addAll(other.modules);
		// also merge the variability
		if(this.needvariability){
			updateVariability();
			removeInvalidConfigurations(moduletoVariability);
			//System.out.println("Finish the variability");
		}
	}
	
	public boolean hasVariabilityConflict(ModuledFeature other){
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
	protected void removeInvalidConfigurations(Map<Module,Variability>moduletoVariability){
		ArrayList<Variability>variablityList = new ArrayList<Variability>(moduletoVariability.values());
		for(int i = 0;i < variablityList.size();i++){
			Variability variabiliti = variablityList.get(i);
			if(!moduletoVariability.containsKey(variabiliti.getModule()))
				continue;
			for(int j = i+1;j < variablityList.size();j++){
				Variability variabilityj = variablityList.get(j);
				if(!moduletoVariability.containsKey(variabilityj.getModule()));
				if(variabiliti.hasConflict(variabilityj)){
					moduletoVariability.remove(variabilityj.getModule());
				}
			}
		}
	}
	
	public Set<Module> getModules(){
		return this.modules;
	}
	public int size() {
		// TODO Auto-generated method stub
		return modules.size();
	}
	
	/**
	 * 临时的加入 并没有真是的加入 只是在运算中应用
	 * @param modulefeature2
	 */
	public void tempmergeModuledFeature(ModuledFeature modulefeature) {
		// TODO Auto-generated method stub
		temp_modules = new HashSet<Module>(modules);
		temp_modules.addAll(modulefeature.modules);
		temp_moduletoVariability = new HashMap<Module,Variability>();
		for(Module module:temp_modules){
			Variability variability = module.getVariability();
			temp_moduletoVariability.put(module, variability);
		}
		if(this.needvariability){
			removeInvalidConfigurations(temp_moduletoVariability);
		}
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
