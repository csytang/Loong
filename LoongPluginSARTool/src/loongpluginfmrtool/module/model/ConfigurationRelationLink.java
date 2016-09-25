package loongpluginfmrtool.module.model;

public class ConfigurationRelationLink {
	private ConfigurationOption asource;
	private ConfigurationOption atarget;
	private ConfigRelation arelation;
	public  ConfigurationRelationLink(ConfigurationOption source,ConfigurationOption target,ConfigRelation relation){
		this.asource = source;
		this.atarget = target;
		this.arelation = relation;
	}
	public ConfigurationOption getSourceConfigurationOption(){
		return asource;
	}
	public ConfigurationOption getTargetConfigurationOption(){
		return atarget;
	}
	public ConfigRelation getRelation(){
		return arelation;
	}
	public ConfigurationOption getParent(){
		return asource;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String link = asource.toString()+"\t-->\t"+atarget.toString();
		return link;
	}
	
	
}
