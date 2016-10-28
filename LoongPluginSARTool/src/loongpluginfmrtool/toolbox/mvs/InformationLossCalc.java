package loongpluginfmrtool.toolbox.mvs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.Module;

public class InformationLossCalc {
	private ModuleDependencyTable atable;
	private int[][]dependentarray;
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	
	private Map<ModuledFeature, Integer> featureToIndex = new HashMap<ModuledFeature, Integer>();
	private Map<Integer, ModuledFeature> indexToFeature = new HashMap<Integer, ModuledFeature>();
	private int num_module;
	private double[][] information_loss_table_array;
	private int[][] moduledependency_table;
	private int[][] featuredependency_table_array;
	private double[][] featuredependency_table_normalized_array;
	private int size;
	private double[][] kullback_leibler_table;
	
	public InformationLossCalc(){
		
	}
	
	public double computeILNeg(Set<ModuleWrapper>msetgroup,ModuleDependencyTable ptable,Map<Integer, Module>pindexToModule){
		double informationloss = 0.0;
		Set<Module>allmodules= new HashSet<Module>();
		
		// initialize
		for(ModuleWrapper wrapper:msetgroup){
			allmodules.addAll(wrapper.getModuleSet());
		}
		
		this.atable = ptable;
		
		dependentarray = atable.getTable();
		
		indexToModule = pindexToModule;
		
		num_module = allmodules.size();
		
		int featureindex = 0;
		for(Module module:allmodules){
			ModuledFeature module_feature = new ModuledFeature(module,num_module);
			// add to set
			features.add(module_feature);
			// add the mapping
			featureToIndex.put(module_feature, featureindex);	
			indexToFeature.put(featureindex,module_feature);
			featureindex++;
		}
		
		// computing information loss
		
		
		
		
		
		
		return informationloss;
	}
	
	
	protected void buildKullbackLeiblerTable() {
		// TODO Auto-generated method stub
		kullback_leibler_table = new double[size][size];
		for(int i = 0;i < this.size;i++){
			for(int j = i;j < this.size;j++){
				double kullbackleibler;
				if(i==j){
					kullbackleibler = 0.0;
				}else{
				    kullbackleibler = compute_Single_Kullback_Leibler(i,j);
				    kullback_leibler_table[i][j] = kullbackleibler; //DK(p||q)
				    kullback_leibler_table[j][i] = kullbackleibler;
				}
			}
		}
	}
	
	protected double compute_Single_Kullback_Leibler(int index_1,int index_2){
		double distance = 0.0;
		int module_index1 = index_1;
		int module_index2 = index_2;
		
		for(int j = 0;j < size;j++){
			double value_index_1 = featuredependency_table_normalized_array[module_index1][j];
			double value_index_2 = featuredependency_table_normalized_array[module_index2][j];
			if(value_index_1==0||value_index_2==0){
				continue;
			}
			double temp = Math.log(value_index_1/value_index_2);
			distance += temp*value_index_1;
		}
		
		return distance;
	}
	

	protected void normalizedDependencyTable() {
		// TODO Auto-generated method stub
		// normalize normal table
		featuredependency_table_normalized_array = new double[size][size];
		for(int i = 0;i < size;i++){
			for(int j = 0;j < size;j++){
				if(featuredependency_table_array[i][j]>0){
					featuredependency_table_array[i][j]=1;
				}
			}
		}
		for(int i = 0;i < size;i++){
			int rowtotal = 0;
			for(int j = 0;j < size;j++){
				rowtotal+=featuredependency_table_array[i][j];
			}
			for(int j = 0;j < size;j++){
				if(featuredependency_table_array[i][j]==0)
					featuredependency_table_normalized_array[i][j] = 0.0;
				else{
					double double_table = ((double)featuredependency_table_array[i][j])/rowtotal;
					featuredependency_table_normalized_array[i][j]  = double_table;
				}
			}
		}
		
	}
	
	
	private void computeInformationLossTable() {
		// TODO Auto-generated method stub
		information_loss_table_array = new double[features.size()][features.size()];
		int size = features.size();
		for(int i = 0;i < size;i++){
			for(int j = i;j < size;j++){
				if(i==j){
					information_loss_table_array[i][j] = 0;
				}else{
					information_loss_table_array[i][j] = compute_Single_InformationLoss(i,j);
					information_loss_table_array[j][i] = information_loss_table_array[i][j];
				}
			}
			
		}
	}

	protected double compute_Single_InformationLoss(int index_1,int index_2){
		double information_loss = 0.0;
		ModuledFeature module_feature1 = indexToFeature.get(index_1);
		ModuledFeature module_feature2 = indexToFeature.get(index_2);
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
			
			double p_bar_i = pro_module_1/pro_module_*featuredependency_table_normalized_array[index_1][i]+pro_module_2/pro_module_*featuredependency_table_normalized_array[index_2][i];
			if(p_bar_i==0||featuredependency_table_normalized_array[index_1][i]==0){
				mergedkl_vector_module_1[i] = 0;
			}else
				mergedkl_vector_module_1[i] =  pro_module_1*Math.log(pro_module_1/p_bar_i);
			if(!Double.isNaN(mergedkl_vector_module_1[i]))
				total_mergedkl_vector_module_1+=mergedkl_vector_module_1[i];
			assert Double.isNaN(total_mergedkl_vector_module_1)==false;
			assert total_mergedkl_vector_module_1>=0;
			if(p_bar_i==0||featuredependency_table_normalized_array[index_2][i]==0){
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
		assert temp_2>=0;
		information_loss = temp_1*temp_2;
		assert Double.isNaN(information_loss)==false;
		return information_loss;
	}
}
