package loongpluginfmrtool.module.model;

import loongplugin.source.database.model.LElement;
public class Import extends ModuleComponent{
	 
	private LElement ause;
	private LElement adef;
	private Module auseModule;
	private Module adefModule;
	private ImportType aimporttype;
	
	public Import(Module module){
		super(module);
	}
	public Import(LElement puse,LElement pdef,Module pdefMo,Module puseMo,ImportType pimporttype){
		super(pdefMo);
		this.ause = puse;
		this.adef = pdef;
		this.auseModule = puseMo;
		this.adefModule = pdefMo;
		this.aimporttype = pimporttype;
	}
	public LElement getDeclarationLElement(){
		return adef;
	}
	
	public LElement getReferenceLElement(){
		return ause;
	}
	
	public Module getDeclarationModule(){
		return auseModule;
	}
	
	public Module getReferenceModule(){
		return adefModule;
	}
	
	public ImportType getImportType(){
		return aimporttype;
	}
	
	public String getImportDisplayName(){
		return "Import"+this.auseModule.getDisplayName();
	}
	
	
	 
	
	
}
