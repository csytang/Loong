package loongpluginfmrtool.toolbox.bunch.clusteringmethod;



public class ObjectiveFunctionCalculatorFactory extends GenericFactory{
	String currObjFnMethod = "Incremental MQ Weighted";
	String defaultMethod = "Incremental MQ Weighted";
	  
	public ObjectiveFunctionCalculatorFactory(){
	    setFactoryType("ObjectiveFunctionCalculator");
	    addItem("Basic MQ Function", "bunch.BasicMQ");
	    addItem("Turbo MQ Function", "bunch.TurboMQ");
	    addItem("Incremental MQ Weighted", "bunch.TurboMQIncrW");
	    addItem("bunch.BasicMQ", "bunch.BasicMQ");
	    addItem("bunch.TurboMQ", "bunch.TurboMQ");
	    addItem("bunch.ITurboMQ", "bunch.TurboMQIncrW");
	    addItem("bunch.TurboMQIncrW", "bunch.TurboMQIncrW");
	}
	  
	
	  
	public String getDefaultMethod(){
	    return this.defaultMethod;
	}
	  
	public String getCurrentCalculator(){
	    return this.currObjFnMethod;
	}
	  
	public void setCurrentCalculator(String sCalc){
	    this.currObjFnMethod = sCalc;
	}
}
