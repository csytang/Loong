package loongpluginfmrtool.toolbox.limbo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.featuremodelbuilder.InformationLossTable;
import loongpluginfmrtool.module.featuremodelbuilder.KullbackLeiblerTable;
import loongpluginfmrtool.module.featuremodelbuilder.ModuleDependencyTable;
import loongpluginfmrtool.module.model.*;
import loongpluginfmrtool.module.*;

public class LIMBO {
	private Map<Module,ModuledFeature>modulesToFeatures = new HashMap<Module,ModuledFeature>();
	private Set<ModuledFeature> features = new HashSet<ModuledFeature>();
	private ModuleBuilder builder;
	private Map<Integer, Module>indexToModule = new HashMap<Integer, Module>();
	private double threshold;
	private Set<Module>allmodules = new HashSet<Module>();
	private double[][] kullback_leibler_table;
	private ModuleDependencyTable dependency_table;
	private double[][] information_loss_table_array;
	private int[][] dependency_table_array;
	private double[][] dependency_table_normalized_array;
	private int size;
	public LIMBO(ModuleBuilder pbuilder,double pthreshold){
		this.builder = pbuilder;
		this.threshold = pthreshold;
		this.indexToModule = this.builder.getIndexToModule();
		this.size = this.indexToModule.size();
		this.allmodules = new HashSet<Module>(this.indexToModule.values());
		this.dependency_table = this.builder.getDependencyTable();
		this.information_loss_table_array = new double[size][size];
		this.dependency_table_array = this.dependency_table.getTable();
		this.dependency_table_normalized_array = new double[size][size];
		normalizedDependencyTable();
		buildKullbackLeiblerTable();
		performClustering();
	}
	
	protected void buildKullbackLeiblerTable() {
		// TODO Auto-generated method stub
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
			double value_index_1 = dependency_table_normalized_array[module_index1][j];
			double value_index_2 = dependency_table_normalized_array[module_index2][j];
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
		for(int i = 0;i < size;i++){
			for(int j = 0;j < size;j++){
				if(dependency_table_array[i][j]>0){
					dependency_table_array[i][j]=1;
				}
			}
		}
		for(int i = 0;i < size;i++){
			int rowtotal = 0;
			for(int j = 0;j < size;j++){
				rowtotal+=dependency_table_array[i][j];
			}
			for(int j = 0;j < size;j++){
				if(dependency_table_array[i][j]==0)
					dependency_table_normalized_array[i][j] = 0.0;
				else{
					double double_table = ((double)dependency_table_array[i][j])/rowtotal;
					dependency_table_normalized_array[i][j]  = double_table;
				}
			}
		}
		
	}

	protected void init(){
		for(Module module:allmodules){
			ModuledFeature module_feature = new ModuledFeature(module);
			// add to set
			features.add(module_feature);
			// add the mapping
			modulesToFeatures.put(module, module_feature);
		}
	}
	protected void performClustering(){
		//STEP 1.
		init();
		computeInformationLossTable();
		//STEP 2.
		
		
		//STEP 3.
		
		//STEP 4.
		
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
					
				}
			}
			
		}
		
	}
}
