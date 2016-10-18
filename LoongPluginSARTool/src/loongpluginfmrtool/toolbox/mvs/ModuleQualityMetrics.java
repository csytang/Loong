package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.model.Module;

public class ModuleQualityMetrics {
	private ModuleSet mset1;
	private ModuleSet mset2;
	private double intra_connectivity_feature1 = 0.0;
	private double intra_connectivity_feature2 = 0.0;

	private double inter_connectivity = 0.0;
	
	public ModuleQualityMetrics(ModuleSet pmset1, ModuleSet pmset2){
		this.mset1 = pmset1;
		this.mset2 = pmset2;
		computebasicMetrics();
	}
	
	public ModuleQualityMetrics(ModuleSet pmset1){
		this.mset1 = pmset1;
		intra_connectivity_feature1 = compute_intro_connectivity(mset1);
	}
	
	protected void computebasicMetrics(){
		intra_connectivity_feature1 = compute_intro_connectivity(mset1);
		intra_connectivity_feature2 = compute_intro_connectivity(mset2);
		inter_connectivity = compute_inter_connectivity(mset1,mset2);
	}
	
	public double getIntraConnectMSet1(){
		return intra_connectivity_feature1;
	}
	public double getIntraConnectMSet2(){
		return intra_connectivity_feature2;
	}
	public double getInterConnect(){
		return inter_connectivity;
	}
	
	/**
	 * the inter-connectivity measurement is a fraction of the maximum number of interedge 
	 * dependencies between clusters i and j (2NiNj) . This measuremenet is bound between the
	 * values of 0 and 1. Eij is 0 when there are no module-level relations between subsystem i
	 * sybsystem j; Eij is 1 when each module in subsystem i depends on all of the modules in subsystem j 
	 * and vice -versa
	 * @param feature1
	 * @param feature2
	 * @return
	 */
	private double compute_inter_connectivity(ModuleSet mset1,
			ModuleSet mset2) {
		// TODO Auto-generated method stub
		int num_md_f1 = mset1.getModuleSet().size();
		int num_md_f2 = mset2.getModuleSet().size();
		double result = 0.0;
		double ef1f2 = 0.0;
		//f1
		Set<Module> intramodules_f1 = mset1.getModuleSet();
		
		//f2
		Set<Module> intramodules_f2 = mset2.getModuleSet();
		
		for(Module module:intramodules_f1){
			Map<Module,Integer> module_count = module.getAllDependency();
			for(Map.Entry<Module, Integer>entry:module_count.entrySet()){
				Module depdent_module = entry.getKey();
				if(intramodules_f2.contains(depdent_module)){
					ef1f2 += entry.getValue();
				}
			}
		}
		
		for(Module module:intramodules_f2){
			Map<Module,Integer> module_count = module.getAllDependency();
			for(Map.Entry<Module, Integer>entry:module_count.entrySet()){
				Module depdent_module = entry.getKey();
				if(intramodules_f1.contains(depdent_module)){
					ef1f2 += entry.getValue();
				}
			}
		}
		
		result = ef1f2/(2*num_md_f1*num_md_f2);
		return result;
	}

	/**
	 * Intra-connectivity (A) measures the degree of connectivity betwen the components that are grouped in the same cluster.
	 * A high degree of intra-connectivity indicates good subsystem partitioning.
	 * A low degree of intra-connectivity indicates poor subsystem partitioning.
	 * A = u/pow(N,2)
	 * u = 
	 */
	protected double compute_intro_connectivity(ModuleSet mset){
		int num_module = mset.getModuleSet().size();
		int intra_connection = 0;
		Set<Module> intramodules = mset.getModuleSet();
		List<Module> list_intramodules = new ArrayList<Module>(intramodules);
		for(int i = 0;i < list_intramodules.size();i++){
			Module modulei = list_intramodules.get(i);
			for(int j = i+1;j < list_intramodules.size();j++){
				Module modulej = list_intramodules.get(i);
				intra_connection += modulei.getTotalDependency(modulej);
			}
		}
		
		return ((double)intra_connection)/Math.pow(num_module,2);
	}
}
