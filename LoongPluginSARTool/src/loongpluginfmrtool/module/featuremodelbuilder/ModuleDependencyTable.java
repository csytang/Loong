package loongpluginfmrtool.module.featuremodelbuilder;

import java.util.Map;

import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.model.Module;

public class ModuleDependencyTable {
	private ModuleBuilder builder;
	private int totalsize;
	private int[][] table;
	private Map<Integer,Module> indexToModule;
	public ModuleDependencyTable(ModuleBuilder pbuilder){
		builder = pbuilder;
	}
	public void buildTable(){
		indexToModule = builder.getIndexToModule();
		totalsize = indexToModule.size();
		table = new int[totalsize][totalsize];
		safechecker();
		for(int i = 0;i < totalsize;i++){
			for(int j = 0;j < totalsize;j++){
				if(i==j){
					table[i][j] = 0;
				}else{
					table[i][j] = computeTotalReference(indexToModule.get(i),indexToModule.get(j));
				}
			}
		}
	}
	private int computeTotalReference(Module a,Module b){
		int reference = 0;
		
		return reference;
	}
	private void safechecker(){
		for(int i = 0;i < totalsize;i++){
			if(!indexToModule.containsKey(i)){
				try{
					throw new Exception("cannot find module for index"+i);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
