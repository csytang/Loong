package loongpluginfmrtool.toolbox.mvs;

import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.model.Module;

public class VariabilityLoss {
	public VariabilityLoss(){
		
	}
	public static double computeVLoss(ModuleSet mset1,ModuleSet mset2){
		double loss = 0.0;
		int totalVariability1 = mset1.getVariabilityCount();
		int totalVariability2 = mset2.getVariabilityCount();
		
		// create a temp counting
		mset1.tempmergeModuleSet(mset2);
		int totalafter = mset1.tempgetVariabilityCount();
		int left = totalVariability1+totalVariability2-totalafter;
		if(totalVariability1+totalVariability2==0){
			return 1.0;
		}
		loss = ((double)left)/(totalVariability1+totalVariability2);
		return loss;
	}
}
