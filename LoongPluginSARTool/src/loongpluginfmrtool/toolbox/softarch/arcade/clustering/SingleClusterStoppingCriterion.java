package loongpluginfmrtool.toolbox.softarch.arcade.clustering;

import loongpluginfmrtool.toolbox.softarch.arcade.config.Config;

public class SingleClusterStoppingCriterion implements StoppingCriterion {
	public boolean notReadyToStop() {
		return ClusteringAlgoRunner.fastClusters.size() != 1;
	}
}