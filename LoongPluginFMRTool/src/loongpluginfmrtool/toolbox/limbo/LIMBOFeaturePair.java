package loongpluginfmrtool.toolbox.limbo;

import java.util.HashSet;
import java.util.Set;

import loongpluginfmrtool.module.ModuledFeature;

public class LIMBOFeaturePair {
	protected Set<ModuledFeature>allfeautureneeds = new HashSet<ModuledFeature>();
	public LIMBOFeaturePair(){
		
	}
	public void addModuledFeature(ModuledFeature feature){
		this.allfeautureneeds.add(feature);
	}
	public Set<ModuledFeature> getModuledFeature(){
		return this.allfeautureneeds;
	}
}
