package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;


public class FitnessCalc {

	private static ModuleDependencyTable dependency_table;
	private static int size;
	private static Map<Integer,ModuleSet>indexModuleSet = new HashMap<Integer,ModuleSet>();

	private static Stack<ModuleSet> modulestack = new Stack<ModuleSet>();
	
	public static double getFitnessValue(GAIndividual gaIndividual,ArrayList<Module>moduleArray) {
		// TODO Auto-generated method stub
		// Information Loss; Variability Loss; Modularity
		double fitness = 0.0;
		BitSet gene = gaIndividual.getGene();
		size = gene.size();
		Set<Module>containedModule = new HashSet<Module>();
		dependency_table = gaIndividual.getGeneClustering().getDependencyTable();
		
		
		int index = 0;
		for(int i = 0;i < size;i++){
			boolean value = gene.get(i);
			if(value){
				containedModule.add(moduleArray.get(i));
				Set<Module> moduleset = new HashSet<Module>();
				moduleset.add(moduleArray.get(i));
				ModuleSet set = new ModuleSet(moduleset,dependency_table,index);
				indexModuleSet.put(index,set);
				modulestack.push(set);
				index++;
			}
		}
		
		double informationloss = 0.0;
		
		double modularityvalue = 0.0;
		
		double variabilityloss = 0.0;
		// pop the module from module stack and merge to compute all loss during merging
		ModuleSet source= modulestack.pop();
		while(!modulestack.isEmpty()){
			ModuleSet top = modulestack.pop();
			informationloss+=computeInformationLoss(source,top);
			variabilityloss+=VariabilityLoss.computeVLoss(source, top);
			source.addModuleSet(top);
			resetAllIndexs(source,top);
		}
		
		// module quality metrics
		ModuleQualityMetrics metrics = new ModuleQualityMetrics(source);
		modularityvalue = metrics.getIntraConnectMSet1();
		
		fitness = informationloss+modularityvalue-variabilityloss;
		
		
		
		return fitness;
	}

	/**
	 * 重新编写 index
	 * 
	 */
	private static void resetAllIndexs(ModuleSet left,ModuleSet remove) {
		// TODO Auto-generated method stub
		Map<Integer,ModuleSet>updateindexModuleSet = new HashMap<Integer,ModuleSet>();
		int index = 0;
		for(Map.Entry<Integer, ModuleSet>entry:indexModuleSet.entrySet()){
			ModuleSet set = entry.getValue();
			if(!set.equals(remove)){
				updateindexModuleSet.put(index, set);
				set.setIndex(index);
				index++;
			}
		}
		indexModuleSet = updateindexModuleSet;
		size = updateindexModuleSet.size();
	}

	private static double computeInformationLoss(ModuleSet source,ModuleSet target) {
		// build the dependency table
		int[][] dependencytable = createDependencyTable();
		double[][] normalized_dependency_table = normalizedDependencyTable(dependencytable);
		double [][]information_loss_table_array = computeInformationLossTable(normalized_dependency_table);
		int indexsource = source.getIndex();
		int indextarget = target.getIndex();
		return information_loss_table_array[indexsource][indextarget];
	}

	private static double[][] computeInformationLossTable(double[][] normalized_dependency_table) {
		// TODO Auto-generated method stub
		double[][] information_loss_table_array = new double[size][size];
		for(int i = 0;i < size;i++){
			for(int j = i;j < size;j++){
				if(i==j){
					information_loss_table_array[i][j] = 0;
				}else{
					information_loss_table_array[i][j] = compute_Single_InformationLoss(i,j,normalized_dependency_table);
					information_loss_table_array[j][i] = information_loss_table_array[i][j];
				}
			}
			
		}
		return information_loss_table_array;
	}

	/**
	 * 计算单一的信息损失
	 * @param index_1
	 * @param index_2
	 * @param normalized_dependency_table
	 * @return
	 */
	protected static double compute_Single_InformationLoss(int index_1,int index_2,double[][] normalized_dependency_table){
		double information_loss = 0.0;
		ModuleSet module_feature1 = indexModuleSet.get(index_1);
		ModuleSet module_feature2 = indexModuleSet.get(index_2);
		double pro_module_1 = module_feature1.getProbability();
		double pro_module_2 = module_feature2.getProbability();
		double temp_1 = pro_module_1+pro_module_2;
		double pro_module_ = pro_module_1+pro_module_2;;
		assert Double.isNaN(pro_module_)==false;
		double[] mergedkl_vector_module_1 = new double[size];
		double[] mergedkl_vector_module_2 = new double[size];
		double total_mergedkl_vector_module_1 = 0.0;
		double total_mergedkl_vector_module_2 = 0.0;
		// 计算 D-KL Kullback-Leibler divergence
		for(int i = 0;i < size;i++){
			
			double p_bar_i = pro_module_1/pro_module_*normalized_dependency_table[index_1][i]+pro_module_2/pro_module_*normalized_dependency_table[index_2][i];
			if(p_bar_i==0||normalized_dependency_table[index_1][i]==0){
				mergedkl_vector_module_1[i] = 0;
			}else
				mergedkl_vector_module_1[i] =  pro_module_1*Math.log(pro_module_1/p_bar_i);
			if(!Double.isNaN(mergedkl_vector_module_1[i]))
				total_mergedkl_vector_module_1+=mergedkl_vector_module_1[i];
			assert Double.isNaN(total_mergedkl_vector_module_1)==false;
			if(p_bar_i==0||normalized_dependency_table[index_2][i]==0){
				mergedkl_vector_module_2[i] = 0;
			}else
				mergedkl_vector_module_2[i] =  pro_module_2*Math.log(pro_module_2/p_bar_i);
			if(!Double.isNaN(mergedkl_vector_module_2[i]))
				total_mergedkl_vector_module_2+=mergedkl_vector_module_2[i];
			assert Double.isNaN(total_mergedkl_vector_module_2)==false;
		}
		assert pro_module_!=0;
		double temp_2 = (pro_module_1/pro_module_)*total_mergedkl_vector_module_1+(pro_module_2/pro_module_)*total_mergedkl_vector_module_2;
		assert Double.isNaN(temp_2)==false;
		
		
		information_loss = temp_1*temp_2;
		
		assert Double.isNaN(information_loss)==false;
		return information_loss;
	}
	
	
	
	/**
	 * 将数组进行归一化处理
	 * @param dependencytable
	 * @return
	 */
	private static double[][] normalizedDependencyTable(int[][] dependencytable) {
		// TODO Auto-generated method stub
		double[][] normalized_dependency_table = new double[size][size];
		for(int i = 0;i < size;i++){
			int rowcount = 0;
			for(int j = 0;j < size;j++){
				rowcount+=dependencytable[i][j];
			}
			for(int j = 0; j < size;j++){
				normalized_dependency_table[i][j] = ((double)dependencytable[i][j])/rowcount;
			}
		}
		
		return normalized_dependency_table;
	}

	/**
	 * 创建依赖关系表
	 * @return
	 */
	private static int[][] createDependencyTable() {
		// TODO Auto-generated method stub
		int[][] dependencytable = new int[size][size];
		for(int i = 0;i < size;i++){
			ModuleSet moduleseti = indexModuleSet.get(i);
			for(int j = 0;j < size;j++){
				ModuleSet modulesetj = indexModuleSet.get(j);
				if(i==j){
					dependencytable[i][j] = 0;
				}else{
					dependencytable[i][j] = moduleseti.computeDependency(modulesetj);
				}
			}
		}
		return dependencytable;
	}

}
