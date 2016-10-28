package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

public class ModuleVariabilitySystem {
	
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private ModuleDependencyTable dependency_table;
	private int populationcount;
	private int cluster;
	private GAPopulation population;
	private GenticClustering clustering;
	private int evoluation;
	private boolean debug = true;
	public ModuleVariabilitySystem(ModuleBuilder pbuilder,int pcluster,int ppopulationcount,int pevoluation){
		this.builder = pbuilder;
		this.cluster = pcluster;
		this.indexToModule = this.builder.getIndexToModule();
		this.dependency_table = this.builder.getDependencyTable();
		this.populationcount = ppopulationcount;
		this.evoluation = pevoluation;
		performClustering();
	}
	
	protected void performClustering(){
		/*
		 * 
		 */
		clustering = new GenticClustering(indexToModule,cluster,populationcount,dependency_table);
		population =  clustering.getInitialGAPopulation();
		for(int i = 0;i < evoluation;i++){
			population = clustering.evolvePopulation(population);
			if(debug){
				System.out.println(population.getFittest().getFitness());
			}
		}
		
	}
}