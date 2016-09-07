package loongpluginfmrtool.module;

import loongplugin.source.database.model.LElement;
public class Import {
	 
	private LElement ause;
	private LElement adef;
	private Module auseModule;
	private Module adefModule;
	private ImportType aimporttype;
	public Import(LElement puse,LElement pdef,Module puseMo,Module pdefMo,ImportType pimporttype){
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
	
	
	
	
	 
	
	
}
