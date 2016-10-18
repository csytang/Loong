package loongpluginfmrtool.toolbox.mvs;

import java.util.BitSet;


public class GAIndividual {
	static int defaultGeneLength = 64;
	private static BitSet genes = new BitSet();
	private double fitness = 0;
	private GenticClustering clustering;
	public GAIndividual(GenticClustering pclustering){
		this.clustering = pclustering;
	}
	
	public GenticClustering getGeneClustering(){
		return this.clustering;
	}
	// Create a random individual
    public void generateIndividual() {
        for (int i = 0; i < size(); i++) {
            boolean gene = Math.random()>0.5;
            genes.set(i, gene);
            
        }
    }
    
	/* Getters and setters */
    // Use this if you want to create individuals with different gene lengths
    public static void setDefaultGeneLength(int length) {
        defaultGeneLength = length;
        genes = new BitSet(defaultGeneLength);
    }
    
    /* Public methods */
    public int size() {
        return genes.size();
    }
    
    public boolean getGene(int index) {
        return genes.get(index);
    }
    
    public void setGene(int index, boolean value) {
        genes.set(index,value);
        fitness = 0;
    }
    public BitSet getGene(){
    	return genes;
    }

	public double getFitness() {
		// TODO Auto-generated method stub
		if (fitness == 0) {
            fitness = FitnessCalc.getFitnessValue(this,clustering.getModuleArray());
        }
        return fitness;
	}
    
}
