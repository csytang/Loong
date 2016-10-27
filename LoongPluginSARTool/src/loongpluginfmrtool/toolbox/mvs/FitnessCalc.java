package loongpluginfmrtool.toolbox.mvs;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;


public class FitnessCalc {

	private ModuleDependencyTable dependency_table;
	private int modulesize;
	private int clustercount;
	private Map<Integer,Set<ModuleWrapper>>clusterToModules = new HashMap<Integer,Set<ModuleWrapper>>();// 类id 到 类内 module
	private Map<Integer,Module>indexToModules = new HashMap<Integer,Module>();
	private List<Set<ModuleWrapper>>listedWrapperGroup = new LinkedList<Set<ModuleWrapper>>();
	
	public double getFitnessValue(GAIndividual gaIndividual,Map<Integer, Module>pindexToModule,int cluster) {
		// TODO Auto-generated method stub
		// Information Loss; Variability Loss; Modularity
		double fitness = 0.0;
		Vector<Integer> gene = gaIndividual.getGene();
		modulesize = gene.size();
		dependency_table = gaIndividual.getGeneClustering().getDependencyTable();
		indexToModules = pindexToModule;
		clustercount = cluster;
		//convert the indextoModuel 
		/**
		 * mdid : the id of the module
		 * mdcluster: the id of the cluster
		 */
		
		//////////////////////////////////////////////////////////////////////////////////////////

		for(int mdid = 0;mdid <gene.size();mdid++){
			int mdcluster = gene.get(mdid);
			if(clusterToModules.containsKey(mdcluster)){
				Set<ModuleWrapper> mset = clusterToModules.get(mdcluster);
				mset.add(new ModuleWrapper(indexToModules.get(mdid),dependency_table,mdcluster));
			}else{
				Set<Module>moduleset = new HashSet<Module>();
				moduleset.add(indexToModules.get(mdid));
				ModuleWrapper mwrapper = new ModuleWrapper(moduleset,dependency_table,mdcluster);
				Set<ModuleWrapper> mset= new HashSet<ModuleWrapper>();
				mset.add(mwrapper);
				clusterToModules.put(mdcluster, mset);
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////////
		double modularityvalue = 0.0;
		double variabilityloss = 0.0;
		double intramodularity = 0.0;
		double intermodularity = 0.0;
		for(Map.Entry<Integer, Set<ModuleWrapper>>entry:clusterToModules.entrySet()){
			Set<ModuleWrapper> wrapperset = entry.getValue();
			variabilityloss+=VariabilityLoss.computeVLossPos(wrapperset);
			ModuleQualityMetrics metrics = new ModuleQualityMetrics(wrapperset);
			intramodularity+=metrics.getIntraConnectMSet1();
			listedWrapperGroup.add(wrapperset);
		}
		intramodularity = intramodularity / clustercount;
		for(int i= 0;i < listedWrapperGroup.size();i++){
			Set<ModuleWrapper> wrapperseti = listedWrapperGroup.get(i);
			for(int j = i+1;j < listedWrapperGroup.size();j++){
				Set<ModuleWrapper> wrappersetj = listedWrapperGroup.get(j);
				ModuleQualityMetrics metrics = new ModuleQualityMetrics(wrapperseti,wrappersetj);
				intermodularity+=metrics.getInterConnect();
			}
		}
		intermodularity = intermodularity/ (clustercount*(clustercount-1)/2);
		
		modularityvalue = intramodularity-intermodularity;
		//////////////////////////////////////////////////////////////////////////////////////////
		
		double informationloss = 0.0;
		
		
		
		//////////////////////////////////////////////////////////////////////////////////////////

		fitness = modularityvalue-informationloss+variabilityloss;
		
		
		return fitness;
	}

	

	private  double computeInformationLoss(ModuleWrapper source,ModuleWrapper target) {
		// build the dependency table
		int[][] dependencytable = createDependencyTable();
		double[][] normalized_dependency_table = normalizedDependencyTable(dependencytable);
		double [][]information_loss_table_array = computeInformationLossTable(normalized_dependency_table);
		int indexsource = source.getIndex();
		int indextarget = target.getIndex();
		return information_loss_table_array[indexsource][indextarget];
	}

	private  double[][] computeInformationLossTable(double[][] normalized_dependency_table) {
		// TODO Auto-generated method stub
		double[][] information_loss_table_array = new double[realsize][realsize];
		for(int i = 0;i < realsize;i++){
			for(int j = i;j < realsize;j++){
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
	protected  double compute_Single_InformationLoss(int index_1,int index_2,double[][] normalized_dependency_table){
		double information_loss = 0.0;
		ModuleSet module_feature1 = indexModuleSet.get(index_1);
		ModuleSet module_feature2 = indexModuleSet.get(index_2);
		double pro_module_1 = module_feature1.getProbability();
		double pro_module_2 = module_feature2.getProbability();
		double temp_1 = pro_module_1+pro_module_2;
		double pro_module_ = pro_module_1+pro_module_2;;
		assert Double.isNaN(pro_module_)==false;
		double[] mergedkl_vector_module_1 = new double[realsize];
		double[] mergedkl_vector_module_2 = new double[realsize];
		double total_mergedkl_vector_module_1 = 0.0;
		double total_mergedkl_vector_module_2 = 0.0;
		// 计算 D-KL Kullback-Leibler divergence
		for(int i = 0;i < realsize;i++){
			
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
	private  double[][] normalizedDependencyTable(int[][] dependencytable) {
		// TODO Auto-generated method stub
		double[][] normalized_dependency_table = new double[realsize][realsize];
		for(int i = 0;i < realsize;i++){
			int rowcount = 0;
			for(int j = 0;j < realsize;j++){
				rowcount+=dependencytable[i][j];
			}
			for(int j = 0; j < realsize;j++){
				normalized_dependency_table[i][j] = ((double)dependencytable[i][j])/rowcount;
			}
		}
		
		return normalized_dependency_table;
	}

	/**
	 * 创建依赖关系表
	 * @return
	 */
	private  int[][] createDependencyTable() {
		// TODO Auto-generated method stub
		int[][] dependencytable = new int[realsize][realsize];
		for(int i = 0;i < realsize;i++){
			ModuleSet moduleseti = indexModuleSet.get(i);
			
			for(int j = 0;j < realsize;j++){
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
