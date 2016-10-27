package loongpluginfmrtool.toolbox.mvs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;



public class GenticClustering {
	
	 /* GA parameters */
    private static final double uniformRate = 0.5;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;
    private Map<Integer, Module>indexToModule;
    private int cluster;
    private ModuleDependencyTable table;
    private GAPopulation initpoluation;
	public GenticClustering(Map<Integer, Module>pindexToModule,int pcluster,ModuleDependencyTable ptable){
		this.indexToModule = pindexToModule;
		this.cluster = pcluster;
		this.table = ptable;
		this.initpoluation = new GAPopulation(this,pcluster,true);
	}
	
	public GAPopulation getInitialGAPopulation(){
		return initpoluation;
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
            Set<Module>settedmodules = pop.getFittest().getallSelectedModules(indexToModule);
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
        //  交叉 的 处理
        
        for (int i = elitismOffset; i < pop.size(); i++) {
            GAIndividual indiv1 = tournamentSelection(pop);
            GAIndividual indiv2 = tournamentSelection(pop);
            GAIndividual newIndiv = GACrossOver.crossover(indiv1, indiv2,this,uniformRate);
            newPopulation.saveIndividual(i, newIndiv);
        }
       
        //使用 mutate 进行检查 
       //在未 mutation之前
//       System.out.println("Current fitness is:"+newPopulation.getPopulationFitCount());
        
    //   newPopulation.printPopulation();
       
       int modulesize = indexToModule.size();
        
        for(int i = 0;i < modulesize;i++){
        	boolean hasmutipleset = false;
        	boolean hasbeenset = false;
        	List<Integer> mutiplesetIndexList = new LinkedList<Integer>();
        	mutiplesetIndexList.clear();
        	for(int j = 0;j < newPopulation.size();j++){
        		GAIndividual individual = newPopulation.getIndividual(j);
        		if(individual.getGene(i)==true){
	        		if(hasbeenset==false){
	        			hasbeenset = true;
	        			mutiplesetIndexList.add(j);
	        		}else{
	        			hasmutipleset = true;
	        			mutiplesetIndexList.add(j);
	        		}
        		}
        	}
        	if(hasmutipleset){
        		//如果有 多个被设定
        		
        		// 选择一个 会是 fitness 降低最少的 
//        		 System.out.println("Has multiple set for module index:"+i);
        		 int maxImproveIndex = 0;
     			boolean isfirstset = true;
     			double maxImprove = 0.0;
     			int multiplesetIndexSize = mutiplesetIndexList.size();
     			for(int index= 0;index < multiplesetIndexSize;index++){
     				int muindex = mutiplesetIndexList.get(index);
     				GAIndividual individual = newPopulation.getIndividual(muindex);
     				GAIndividual mutateindividual = new GAIndividual(individual);
     				mutateindividual.setGene(i, false);
     				FitnessCalc cal = new FitnessCalc();
     				double improve = cal.getFitnessValue(mutateindividual, indexToModule)-cal.getFitnessValue(individual, indexToModule);
     				if(isfirstset){
     					maxImprove = improve;
     					maxImproveIndex = muindex;
     					isfirstset = false;
     				}else if(improve>maxImprove){
     					maxImprove = improve;
     					maxImproveIndex = muindex;
     				}
     			}
     			for(int index= 0;index < multiplesetIndexSize;index++){
     				int muindex = mutiplesetIndexList.get(index);
     				if(muindex!=maxImproveIndex){
     					GAIndividual changeindividual = newPopulation.getIndividual(muindex);
             			changeindividual.setGene(i, false);
             			newPopulation.saveIndividual(muindex, changeindividual);
     				}
     			}
  //   			System.out.println("Current fitness is:"+newPopulation.getPopulationFitCount());
     			//cnewPopulation.printPopulation();
        		
        	}else{
        		if(!hasbeenset){
//        			System.out.println("Has no set for module index:"+i);
        			int maxImproveIndex = 0;
        			boolean isfirstset = true;
        			double maxImprove = 0.0;
        			for(int msindex = 0;msindex < newPopulation.size();msindex++){
        				GAIndividual individual = newPopulation.getIndividual(msindex);
        				GAIndividual mutateindividual = new GAIndividual(individual);
        				mutateindividual.setGene(i, true);
        				FitnessCalc cal = new FitnessCalc();
        				double improve = cal.getFitnessValue(mutateindividual, indexToModule)-cal.getFitnessValue(individual, indexToModule);
        				if(isfirstset){
        					maxImprove = improve;
        					maxImproveIndex = msindex;
        					isfirstset = false;
        				}else if(improve>maxImprove){
        					maxImprove = improve;
        					maxImproveIndex = msindex;
        				}
        			}
        			GAIndividual changeindividual = newPopulation.getIndividual(maxImproveIndex);
        			changeindividual.setGene(i, true);
 //       			System.out.println("Current fitness is:"+newPopulation.getPopulationFitCount());
        			newPopulation.saveIndividual(maxImproveIndex, changeindividual);
  //      		    newPopulation.printPopulation();
        		       
        			//newPopulation.saveIndividual(maxImproveIndex, changeindividual);
        		}
        	}
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
		return fittest;
	}
	
	
    
    


	

	public Map<Integer, Module> getIndextoModule() {
		// TODO Auto-generated method stub
		return indexToModule;
	}
    
	
}
