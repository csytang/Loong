package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

public class ModuleVariabilitySystem {
	
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private Set<Module> allmodules = new HashSet<Module>();
	private ModuleDependencyTable dependency_table;
	private int range = 10;
	private int cluster;
	private Map<GAPopulation,Double>gapopulationToFitness = new HashMap<GAPopulation,Double>();
	public ModuleVariabilitySystem(ModuleBuilder pbuilder,int pcluster){
		this.builder = pbuilder;
		this.cluster = pcluster;
		this.indexToModule = this.builder.getIndexToModule();
		this.allmodules = new HashSet<Module>(this.indexToModule.values());
		this.dependency_table = this.builder.getDependencyTable();
		
		performClustering();
	}
	
	protected void performClustering(){
		GenticClustering gencluster = new GenticClustering(indexToModule,cluster,dependency_table);
		GAPopulation ga = gencluster.getInitialGAPopulation();
		//目前测试 使用
		
		int loop = 0;
		while(true){
			System.out.println("Loop:"+loop);
			double originalFitnessCount = ga.getPopulationFitCount();
			System.out.println("Fitness is :"+originalFitnessCount);
			
			ga.printPopulation();
			if(gapopulationToFitness.keySet().size()<range){
				gapopulationToFitness.put(ga, originalFitnessCount);
			}else if(gapopulationToFitness.keySet().size()==range){
				boolean isfirst = true;
				double valueMinal = 0.0;
				GAPopulation minimalPopulation = null;
				for(Map.Entry<GAPopulation,Double>entry:gapopulationToFitness.entrySet()){
					GAPopulation population = entry.getKey();
					double value = entry.getValue();
					if(isfirst){
						minimalPopulation = population;
						valueMinal = value;
						isfirst = false;
					}else if(valueMinal>value){
						minimalPopulation = population;
						valueMinal = value;
					}
				}
				if(valueMinal<originalFitnessCount){
					gapopulationToFitness.put(ga, originalFitnessCount);
					gapopulationToFitness.remove(minimalPopulation);
				}else{
					break;
				}
			}
			GAPopulation evolved = gencluster.evolvePopulation(ga);
			
			ga = evolved;
			loop++;
		}
	}
}