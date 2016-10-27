package loongpluginfmrtool.toolbox.mvs;




import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import loongpluginfmrtool.module.model.Module;

public class GAPopulation {
	
	private GAIndividual[] individuals;
	private int acluster;
	private Map<Integer,Module> indextomodules;
	private GenticClustering clustering;
	public GAPopulation(GenticClustering pclustering,int cluster,int populationsize, boolean initialise){
		this.clustering = pclustering;
		this.indextomodules = pclustering.getIndextoModule();
		this.acluster = cluster;
		individuals = new GAIndividual[cluster];
		// update default size;
		if(initialise){
			int defaultsize = 0;
			for(int i = 0;i < populationsize;i++){
				GAIndividual individul = new GAIndividual(clustering,acluster);
				defaultsize = indextomodules.size();
				individul.setDefaultGeneLength(defaultsize);
				individul.initialize();
				saveIndividual(i,individul);
			}
			
			
			
		}
	}
	
	
	 /* Getters */
    public GAIndividual getIndividual(int index) {
        return individuals[index];
    }
    
    
    public void printPopulation(){
    	for(int i = 0;i < acluster;i++){
    		GAIndividual  ind = individuals[i];
    		ind.printIndividual();
    	}
    }
    
    /* Public methods */
    // Get population size
    public int size() {
        return individuals.length;
    }

    // Save individual
    public void saveIndividual(int index, GAIndividual indiv) {
        individuals[index] = indiv;
    }
	
	public GAIndividual getFittest() {
		GAIndividual fittest = individuals[0];
        // Loop through individuals to find fittest
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }
	
	
	
    
}
