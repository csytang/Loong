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
	public GAPopulation(GenticClustering pclustering,int cluster, boolean initialise){
		this.clustering = pclustering;
		this.indextomodules = pclustering.getIndextoModule();
		this.acluster = cluster;
		individuals = new GAIndividual[cluster];
		// update default size;
		if(initialise){
			int defaultsize = 0;
			for(int i = 0;i < cluster;i++){
				GAIndividual individul = new GAIndividual(clustering);
				defaultsize = indextomodules.size();
				individul.setDefaultGeneLength(defaultsize);
				saveIndividual(i,individul);
			}
			
			// initial the individual
			for(int index = 0;index < defaultsize;index++){
				 Random random = new Random();
			     int s = random.nextInt(cluster);
			     for(int clusterindex= 0;clusterindex < cluster;clusterindex++){
			    	 GAIndividual ind = individuals[clusterindex];
			    	 if(clusterindex==s)
			    		 ind.setGene(index, true);
			    	 else
			    		 ind.setGene(index, false);
			     }
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
	
	public double getPopulationFitCount(){
		double count = 0.0;
		for (int i = 0; i < size(); i++) {
			count+=individuals[i].getFitness();
        }
		return count;
	}
	
    
}
