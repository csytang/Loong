package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;



public class GenticClustering {
	
	 /* GA parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;
    private ArrayList<Module>allmodules;
    private int cluster;
    private ModuleDependencyTable table;
	public GenticClustering(ArrayList<Module>pallmodules,int pcluster,ModuleDependencyTable ptable){
		this.allmodules = pallmodules;
		this.cluster = pcluster;
		this.table = ptable;
	}
	
	
	public ModuleDependencyTable getDependencyTable(){
		return this.table;
	}
	
	// Evolve a population种群进化
	public GAPopulation evolvePopulation(GAPopulation pop) {
		// Keep our best individual
		GAPopulation newPopulation = new GAPopulation(this,pop.size(),false);
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        
        // Loop over the population size and create new individuals with
        // crossover 交叉
        for (int i = elitismOffset; i < pop.size(); i++) {
            GAIndividual indiv1 = tournamentSelection(pop);
            GAIndividual indiv2 = tournamentSelection(pop);
            GAIndividual newIndiv = GACrossOver.crossover(indiv1, indiv2,this,uniformRate);
            newPopulation.saveIndividual(i, newIndiv);
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
	}

	/**
	 * 锦标赛选择算法 会选择出来一个在种群 和另一个进行交换 
	 * @param pop
	 * @return
	 */
	public GAIndividual tournamentSelection(GAPopulation pop) {
		// TODO Auto-generated method stub
		// Create a tournament population
        GAPopulation tournament = new GAPopulation(this,tournamentSize, false);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        GAIndividual fittest = tournament.getFittest();
		return null;
	}
	
	
    
    /**
     * 对一个 单一变量 进行 变异
     * @param indiv
     */
    // Mutate an individual
    private static void mutate(GAIndividual indiv) {
        // Loop through genes
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
            	boolean gene = Math.random()>0.5;
                indiv.setGene(i, gene);
            }
        }
    }


	public ArrayList<Module> getModuleArray() {
		// TODO Auto-generated method stub
		return allmodules;
	}
    
	
}
