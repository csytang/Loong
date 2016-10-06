package loongpluginfmrtool.toolbox.bunch.clusteringmethod;



public class ClusteringMethodFactory extends GenericFactory{
	String defaultMethod = "Hill Climbing";
	  
	public ClusteringMethodFactory(){
	    setFactoryType("ClusteringMethod");
	    addItem("Hill Climbing", "bunch.GeneralHillClimbingClusteringMethod");
	    addItem("NAHC", "bunch.NextAscentHillClimbingClusteringMethod");
	    addItem("SAHC", "bunch.SteepestAscentHillClimbingClusteringMethod");
	    addItem("GA", "bunch.GAClusteringMethod");
	    addItem("Exhaustive", "bunch.OptimalClusteringMethod");
	 }
	  
	 public String getDefaultMethod(){
	    return this.defaultMethod;
	 }
	  
	 public String[] getItemList(){
	    String[] masterList = super.getItemList();
	    String[] resList = new String[masterList.length - 2];
	    
	    int resPos = 0;
	    for (int i = 0; i < masterList.length; i++){
	      String item = masterList[i];
	      if ((!item.equals("SAHC")) && (!item.equals("NAHC"))) {
	        resList[(resPos++)] = item;
	      }
	    }
	    return resList;
	 }
	  
	 public ClusteringMethod getMethod(String name){
	    return (ClusteringMethod)getItemInstance(name);
	  }
	

}
