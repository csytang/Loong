package loongpluginfmrtool.toolbox.mvs;

import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;
import loongpluginfmrtool.module.model.Module;

public class VariabilityLoss {
	public VariabilityLoss(){
		
	}
	public static double computeVLoss(ModuledFeature modulefeature1,ModuledFeature modulefeature2){
		double loss = 0.0;
		int totalVariability1 = modulefeature1.getVariabilityCount();
		int totalVariability2 = modulefeature2.getVariabilityCount();
		
		// create a temp counting
		modulefeature1.tempmergeModuledFeature(modulefeature2);
		int totalafter = modulefeature1.tempgetVariabilityCount();
		int left = totalVariability1+totalVariability2-totalafter;
		if(totalVariability1+totalVariability2==0){
			return 1.0;
		}
		loss = ((double)left)/(totalVariability1+totalVariability2);
		return loss;
	}
}
