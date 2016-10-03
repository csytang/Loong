package loongpluginfmrtool.toolbox.limbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.*;
import loongpluginfmrtool.module.*;

public class LIMBO {
	private Map<Module,ModuledFeature>modulesToFeatures = new HashMap<Module,ModuledFeature>();
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private Map<Module, Integer>ModuleToIndex = new HashMap<Module, Integer>();
	private Map<Integer, ModuledFeature> indexToFeature = new HashMap<Integer, ModuledFeature>();
	
	private double threshold;
	private Set<Module>allmodules = new HashSet<Module>();
	private double[][] kullback_leibler_table;
	private ModuleDependencyTable dependency_table;
	private double[][] information_loss_table_array;
	private int[][] moduledependency_table;
	private int[][] featuredependency_table_array;
	private double[][] featuredependency_table_normalized_array;
	private int size;
	private double MAXVALUE = 10000;
	private int cluster;
	private boolean debug = true;
	public LIMBO(ModuleBuilder pbuilder,double pthreshold,int pcluster){
		this.builder = pbuilder;
		this.threshold = pthreshold;
		this.cluster = pcluster;
		this.indexToModule = this.builder.getIndexToModule();
		this.allmodules = new HashSet<Module>(this.indexToModule.values());
		this.dependency_table = this.builder.getDependencyTable();
		initialize();
		performClustering();
	}
	
	protected void initialize(){
		moduledependency_table = this.dependency_table.getTable();
		for(Map.Entry<Integer, Module> entry:indexToModule.entrySet()){
			int index = entry.getKey();
			Module module = entry.getValue();
			ModuledFeature module_feature = new ModuledFeature(module);
			// add to set
			features.add(module_feature);
			// add the mapping
			
			ModuleToIndex.put(module, index);
			
			modulesToFeatures.put(module, module_feature);
			indexToFeature.put(index,module_feature);
		}
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

	protected void init(){
		
		size = indexToFeature.size();
		createDependencyTable();
		normalizedDependencyTable();
		buildKullbackLeiblerTable();
	}
	
	protected void createDependencyTable(){
		featuredependency_table_array = new int[size][size];
		for(int i = 0;i < size;i++){
			ModuledFeature feature_1 = indexToFeature.get(i);
			for(int j = 0;j < size;j++){
				ModuledFeature feature_2 = indexToFeature.get(j);
				featuredependency_table_array[i][j] = computeDependencyBetweenFeatures(feature_1,feature_2);
			}
		}
		
	}
	
	
	private int computeDependencyBetweenFeatures(ModuledFeature feature_1,
			ModuledFeature feature_2) {
		// TODO Auto-generated method stub
		Set<Module>module_feature_1 = feature_1.getModules();
		Set<Module>module_feature_2 = feature_2.getModules();
		int dependency = 0;
		for(Module module_1:module_feature_1){
			int index1 = ModuleToIndex.get(module_1);
			for(Module module_2:module_feature_2){
				int index2 = ModuleToIndex.get(module_2);
				dependency += moduledependency_table[index1][index2];
			}
		}
		return dependency;
	}

	protected void performClustering(){
		
		init();
		computeInformationLossTable();
		
		while(true){
			boolean merged = merge();
			if(!merged||features.size()<=cluster){
				break;
			}else{
				createDependencyTable();
				normalizedDependencyTable();
				buildKullbackLeiblerTable();
			}
		}
		if(debug){
			print();
		}
	}

	protected void print(){
		System.out.println("Total clusters:"+features.size());
		
		for(Map.Entry<Integer, ModuledFeature>entry:indexToFeature.entrySet()){
			System.out.println("\t index:"+entry.getKey());
			ModuledFeature feature = entry.getValue();
			System.out.println("\t feature contains:");
			for(Module submodule:feature.getModules()){
				System.out.println("\t \tModule:"+submodule.getDisplayName());
			}
		}
	}
	
	protected void featureinitupdate(){
		int index = 0;
		indexToFeature.clear();
		
		for(ModuledFeature feature:features){
			indexToFeature.put(index,feature);
			index++;
		}
	}
	
	
	private boolean merge() {
		// TODO Auto-generated method stub
		Map<ModuledFeature,LIMBOFeaturePair>needClustering = new HashMap<ModuledFeature,LIMBOFeaturePair>();
		Set<LIMBOFeaturePair>modulefeatures = new HashSet<LIMBOFeaturePair>();
		
		
		double minimalValue = MAXVALUE;
		for(int i = 0;i < size;i++){
			for(int j = i+1;j < size;j++){
				if(minimalValue>information_loss_table_array[i][j]){
					minimalValue = information_loss_table_array[i][j];
				}
			}
		}
		if(minimalValue>threshold){
			return false;
		}
		for(int i = 0;i < size;i++){
			for(int j = i+1;j < size;j++){
				if(minimalValue==information_loss_table_array[i][j]){
					ModuledFeature feature1 = indexToFeature.get(i);
					ModuledFeature feature2 = indexToFeature.get(j);
				
					if(needClustering.containsKey(feature1)){
						LIMBOFeaturePair pair = needClustering.get(feature1);
						pair.addModuledFeature(feature2);
						needClustering.put(feature2, pair);
					}else if(needClustering.containsKey(feature2)){
						LIMBOFeaturePair pair = needClustering.get(feature2);
						pair.addModuledFeature(feature1);
						needClustering.put(feature1, pair);
					}else{
						LIMBOFeaturePair pair = new LIMBOFeaturePair();
						pair.addModuledFeature(feature2);
						pair.addModuledFeature(feature1);
						modulefeatures.add(pair);
						needClustering.put(feature1, pair);
						needClustering.put(feature2, pair);
					}
				}
			}
		}
		for(LIMBOFeaturePair mergepair:modulefeatures){
			Set<ModuledFeature>pair_features =  mergepair.getModuledFeature();
			ArrayList<ModuledFeature>pairlist = new ArrayList<ModuledFeature>(pair_features);
			int size = pairlist.size();
			ModuledFeature mainfeature = pairlist.get(0);
			for(int i = 1;i <size;i++){
				mainfeature.mergeModuledFeature(pairlist.get(i));
				features.remove(pairlist.get(i));
			}
		}
		size = features.size();
		featureinitupdate();
		
		return true;
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
		double pro_module_1 = ((double)module_feature1.size())/(module_feature1.size()+module_feature2.size());
		double pro_module_2 = ((double)module_feature2.size())/(module_feature1.size()+module_feature2.size());
		double temp_1 = pro_module_1+pro_module_2;
		double pro_module_ = temp_1;
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
				mergedkl_vector_module_1[i] =  featuredependency_table_normalized_array[index_1][i]*Math.log(featuredependency_table_normalized_array[index_1][i]/p_bar_i);
			total_mergedkl_vector_module_1+=mergedkl_vector_module_1[i];
			assert Double.isNaN(total_mergedkl_vector_module_1)==false;
			if(p_bar_i==0||featuredependency_table_normalized_array[index_2][i]==0){
				mergedkl_vector_module_2[i] = 0;
			}else
				mergedkl_vector_module_2[i] =  featuredependency_table_normalized_array[index_2][i]*Math.log(featuredependency_table_normalized_array[index_2][i]/p_bar_i);
			total_mergedkl_vector_module_2+=mergedkl_vector_module_2[i];
			assert Double.isNaN(total_mergedkl_vector_module_2)==false;
		}
		
		double temp_2 = pro_module_1/pro_module_*total_mergedkl_vector_module_1+pro_module_2/pro_module_*total_mergedkl_vector_module_2;
		assert Double.isNaN(temp_2)==false;
		information_loss = temp_1*temp_2;
		assert Double.isNaN(information_loss)==false;
		return information_loss;
	}
	
}
