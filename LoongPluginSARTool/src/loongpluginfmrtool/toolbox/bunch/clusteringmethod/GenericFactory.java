package loongpluginfmrtool.toolbox.bunch.clusteringmethod;

import java.beans.Beans;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class GenericFactory implements Serializable{
	protected Hashtable methodTable_d;
	public static final long serialVersionUID = 100L;
	protected String factoryType_d;
	  
	public GenericFactory(){
	    this.methodTable_d = new Hashtable(10);
	}
	  
	public void setFactoryType(String name){
	    this.factoryType_d = name;
	}
	  
	public String getFactoryType(){
	    return this.factoryType_d;
	}
	  
	public void addItem(String name, String className){
	    this.methodTable_d.put(name, className);
	}
	  
	public Enumeration getAvailableItems(){
	    return this.methodTable_d.keys();
	}
	  
	public String[] getItemList(){
	    String[] list = new String[this.methodTable_d.size()];
	    Enumeration e = this.methodTable_d.keys();
	    int i = 0;
	    while (e.hasMoreElements()) {
	      list[(i++)] = ((String)e.nextElement());
	    }
	    return list;
	 }
	  
	 public String getItemName(String name){
	    return (String)this.methodTable_d.get(name);
	 }
	  
	 public Object getItemInstance(String name){
	    String cls = null;
	    if (name.toLowerCase().equals("default")) {
	    	cls = "bunch.Default" + this.factoryType_d;
	    } else {
	    	cls = (String)this.methodTable_d.get(name);
	    }
	    Object obj = null;
	    try{
	      obj = Beans.instantiate(null, cls);
	    }
	    catch (Exception e){
	      return getItemInstanceFromClass(name);
	    }
	    return obj;
	 }
	  
	 public Object getItemInstanceFromClass(String cls){
	    Object obj = null;
	    try
	    {
	      obj = Beans.instantiate(null, cls);
	    }
	    catch (Exception e)
	    {
	      throw new RuntimeException(e.toString());
	    }
	    return obj;
	 }
	  
	 public String getDefaultMethod(){
	     return null;
	 }
}
