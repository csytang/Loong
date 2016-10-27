package loongpluginfmrtool.toolbox.softarch.arcade.clustering;

import loongpluginfmrtool.toolbox.softarch.arcade.config.Config;

public class ClusterGainStoppingCriterion implements StoppingCriterion {
	public boolean notReadyToStop() {
		return ClusteringAlgoRunner.fastClusters.size() != 1
					&& ClusteringAlgoRunner.fastClusters.size() != ClusteringAlgoRunner.numClustersAtMaxClusterGain;
	}
}