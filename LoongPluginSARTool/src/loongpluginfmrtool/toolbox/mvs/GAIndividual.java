package loongpluginfmrtool.toolbox.mvs;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import loongpluginfmrtool.module.model.Module;


public class GAIndividual {
	private Vector<Boolean> genes = new Vector<Boolean>();
	private double fitness = 0;
	private GenticClustering clustering;
	private int size;
	public GAIndividual(GenticClustering pclustering){
		this.clustering = pclustering;
		
	}
	
	public GAIndividual(GAIndividual individual) {
		// TODO Auto-generated constructor stub
		this.clustering = individual.clustering;
		this.size = individual.size;
		this.genes = individual.genes;
	}

	public GenticClustering getGeneClustering(){
		return this.clustering;
	}
	// Create a random individual
	/*
	public Set<Module> generateIndividual(Set<Module> selected, Map<Integer, Module> indextomodules) {
        for (int i = 0; i < size; i++) {
            boolean gene = Math.random()>0.5;
            //genes.addELement(i, gene);
            if(gene==true){
            	Module indexmodule = indextomodules.get(i);
            	if(selected.contains(indexmodule)){// 如果已经被设定
            		genes.add(i, false);
            	}else{
            		genes.add(i, gene);
            	}
            }else{
            	genes.add(i, gene);
            }
            
        }
        return selected;
    }*/
    
	/* Getters and setters */
    // Use this if you want to create individuals with different gene lengths
    public void setDefaultGeneLength(int length) {
    	size = length;
    	genes = new Vector<Boolean>(length);
    }
    
    /* Public methods */
    public int size() {
        return genes.capacity();
    }
    
    public boolean getGene(int index) {
        return genes.get(index);
    }
    
    public void setGene(int index, boolean value) {
    	if(genes.isEmpty()){
    		genes.add(index,value);
    	}else if(genes.size()<=index){
    		genes.add(index,value);
    	}else if(genes.elementAt(index)==null){
    		genes.add(index,value);
    	}else
    		genes.set(index,value);
        fitness = 0;
    }
    
    
    public Vector<Boolean> getGene(){
    	return genes;
    }

	public double getFitness() {
		// TODO Auto-generated method stub
		if (fitness == 0) {
			FitnessCalc cal = new FitnessCalc();
            fitness = cal.getFitnessValue(this,clustering.getIndextoModule());
        }
        return fitness;
	}
	
	public void removingAlreadyAnnotated(Map<Integer, Module> indexToModule,Set<Module>readyAnnotated){
		Vector<Boolean> updatedgenes = new Vector<Boolean>();
		for(int i = 0;i < genes.size();i++){
			if(genes.get(i)==true){
				if(readyAnnotated.contains(indexToModule.get(i))){//如果 已经 标记了
					updatedgenes.add(i, false);
				}else{
					updatedgenes.add(i, true);
				}
			}else{
				updatedgenes.add(i, false);
			}
		}
		genes = updatedgenes;
	}

	public void printIndividual() {
		// TODO Auto-generated method stub
		for(Boolean ge:genes){
			if(ge)
				System.out.print("1");
			else
				System.out.print("0");
			System.out.print("\t");
		}
		System.out.println();
	}

	public Set<Module> getallSelectedModules(Map<Integer, Module> indexToModule) {
		// TODO Auto-generated method stub
		Set<Module>modules = new HashSet<Module>();
		for(int i = 0;i < genes.size();i++){
			if(genes.get(i)==true){
				modules.add(indexToModule.get(i));
			}
		}
		return modules;
	}
    
}
