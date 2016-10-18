package loongpluginfmrtool.toolbox.mvs;




import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import loongpluginfmrtool.module.model.Module;

public class GAPopulation {
	
	private GAIndividual[] individuals;
	private int acluster;
	private ArrayList<Module>allmodules;
	private GenticClustering clustering;
	public GAPopulation(GenticClustering pclustering,int cluster, boolean initialise){
		this.clustering = pclustering;
		this.allmodules = pclustering.getModuleArray();
		this.acluster = cluster;
		if(initialise){
			for(int i = 0;i < cluster;i++){
				GAIndividual individul = new GAIndividual(clustering);
				individul.generateIndividual();
				saveIndividual(i,individul);
			}
		}
	}
	
	
	 /* Getters */
    public GAIndividual getIndividual(int index) {
        return individuals[index];
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

	public List<Module> getAllModules() {
		// TODO Auto-generated method stub
		return allmodules;
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
