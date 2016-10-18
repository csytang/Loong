package loongpluginfmrtool.toolbox.mvs;

/**
 * 交叉
 * 交叉率： 一对父代个体进行交叉的概率
 * 
 * @author tangchris
 *
 */
public class GACrossOver {
	/**
	 * 进行交叉
	 * @param indiv1
	 * @param indiv2
	 * @return
	 */
	public GACrossOver(){
		
	}
    public static  GAIndividual crossover(GAIndividual indiv1, GAIndividual indiv2,GenticClustering clustering,double uniformRate) {
    	GAIndividual newSol = new GAIndividual(clustering);
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }
}
