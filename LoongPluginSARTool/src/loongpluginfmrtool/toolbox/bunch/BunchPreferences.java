package loongpluginfmrtool.toolbox.bunch;

import java.io.Serializable;

import loongpluginfmrtool.toolbox.bunch.clusteringmethod.ClusteringMethodFactory;
import loongpluginfmrtool.toolbox.bunch.clusteringmethod.ObjectiveFunctionCalculatorFactory;


public class BunchPreferences implements Serializable{

	private ClusteringMethodFactory methodFactory_d;// clustering 方法
	private ObjectiveFunctionCalculatorFactory objectivecalculatorFactory_d;
	public BunchPreferences(){
		methodFactory_d = new ClusteringMethodFactory();
		objectivecalculatorFactory_d = new ObjectiveFunctionCalculatorFactory();
	}
	public void setClusteringMethodFactory(ClusteringMethodFactory fac){
	    this.methodFactory_d = fac;
	}
	  
	public ClusteringMethodFactory getClusteringMethodFactory(){
	    return this.methodFactory_d;
	}
	
	// clustering algorithms
	public ObjectiveFunctionCalculatorFactory getObjectiveFunctionCalculatorFactory(){
		return objectivecalculatorFactory_d;
	}
}
