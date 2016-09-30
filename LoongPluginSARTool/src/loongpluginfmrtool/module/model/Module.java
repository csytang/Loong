package loongpluginfmrtool.module.model;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.builder.ExternalConfBuilder;
import loongpluginfmrtool.module.builder.InternalConfBuilder;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.builder.ModuleDependencyBuilder;
import loongpluginfmrtool.module.util.ASTNodeWalker;
import loongpluginfmrtool.module.util.ASTSubBindingFinder;
import loongpluginfmrtool.views.moduleviews.ModuleModel;

public class Module implements Serializable {
	private LElement dominate;
	private int moduleIndex=0;
	private Set<LElement> allmethods = new HashSet<LElement>();
	private Set<ModuleComponent> components = new HashSet<ModuleComponent>();
	private LFlyweightElementFactory lElementfactory;
	private ASTNode dominateASTNode;
	private ModuleBuilder abuilder;
	private InternalConfBuilder contflowbuilder;
	private ExternalConfBuilder externalconfbuilder;
	private Map<LElement,Set<ConfigurationOption>> method_configurations = new HashMap<LElement,Set<ConfigurationOption>>();
	private Map<ConfigurationOption,LElement> configuration_method = new HashMap<ConfigurationOption,LElement>();
	private Map<ConfigurationOption,Set<ASTNode>>external_enable_cong_control = new HashMap<ConfigurationOption,Set<ASTNode>>();
	private Map<ConfigurationOption,Set<ASTNode>>external_disable_cong_control = new HashMap<ConfigurationOption,Set<ASTNode>>();
	private Set<ConfigurationOption> configurations;
	private Map<Module,Integer> depenendecies = new HashMap<Module,Integer>();
	
	private ModuleModel model;
	private Variability variability;
	private boolean isInternalVariabilityComputed = false;
	private boolean isExternalVariabilityComputed = false;
	
	public Module(LElement element,int index,LFlyweightElementFactory pElementFactory,ModuleBuilder mbuilder,ModuleModel pmodel){
		this.dominate = element;
		this.moduleIndex = index;
		this.lElementfactory = pElementFactory;
		this.abuilder = mbuilder;
		this.dominateASTNode = element.getASTNode();		
		this.contflowbuilder = new InternalConfBuilder(this);
		this.model = pmodel;
		this.variability = new Variability(this);
	}
	
	/**
	 * initialize this module
	 */
	public void initialize(){
		
		
		// resolve body
		resolvebody();
		
		// resolve variability
		resolveInternalVariability();
		
		configurations =  getAllConfigurationOptions();
		components.addAll(configurations);
		
	}
	
	private void resolveInternalVariability(){
		this.contflowbuilder.parse();
		this.method_configurations = this.contflowbuilder.getMethod_To_Configuration();
		this.configuration_method = this.contflowbuilder.getConfiguration_To_Method();
		isInternalVariabilityComputed = true;
	}
	
	public void resolveExternalVariability(){
		externalconfbuilder = new ExternalConfBuilder(this,lElementfactory);
		externalconfbuilder.parse();
		isExternalVariabilityComputed = true;
	}
	
	/**
	 * This function will extract all variability 
	 * patterns from the configurations.
	 */
	public void extractVariability() {
		// TODO Auto-generated method stub
		this.variability.collect();
		
	}
	
	public boolean isExternalVariabilityComputed(){
		return isExternalVariabilityComputed;
	}
	
	public boolean isInternalVariabilityComputed(){
		return isInternalVariabilityComputed;
	}
	
	
	
	public Map<LElement,Set<ConfigurationOption>> getMethod_To_Configuration(){
		return this.method_configurations;
	}
	
	public Map<ConfigurationOption,LElement> getConfiguration_To_Method(){
		return this.configuration_method;
	}
	
	
	public Set<LElement> getallMethods(){
		return allmethods;
	}
	private void resolvebody(){
		// find the method body inside the class
		if(dominateASTNode!=null){
			Set<ASTNode> methodASTNodes = ASTNodeWalker.methodWalker(dominateASTNode);
			for(ASTNode method:methodASTNodes){
				MethodDeclaration methoddecl = (MethodDeclaration)method;
				IMethodBinding binding = methoddecl.resolveBinding();
				if(binding==null){
					try{
						throw new Exception("Cannot find the binding for"+methoddecl.getName().toString());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				LElement methodelement = lElementfactory.getElement(binding);
				if(methodelement!=null)
					allmethods.add(methodelement);
			}
		}
	}
	
	public LElement getDominateElement(){
		return dominate;
	}

	public LFlyweightElementFactory getelementfactory() {
		// TODO Auto-generated method stub
		return lElementfactory;
	}
	
	
	public void addExternalEnableConfigurationControl(ConfigurationOption config,Set<ASTNode> undercontrolled){
		if(this.external_enable_cong_control.containsKey(config)){
			Set<ASTNode> allastnodes = this.external_enable_cong_control.get(config);
			allastnodes.addAll(undercontrolled);
			this.external_enable_cong_control.put(config, allastnodes);
		}else
			this.external_enable_cong_control.put(config, undercontrolled);
	}
	public void addExternalEnableConfigurationControl(ConfigurationOption config,ASTNode undercontrolled){
		if(this.external_enable_cong_control.containsKey(config)){
			Set<ASTNode> allastnodes = this.external_enable_cong_control.get(config);
			allastnodes.add(undercontrolled);
			this.external_enable_cong_control.put(config, allastnodes);
		}else{
			Set<ASTNode> allastnodes = new HashSet<ASTNode>();
			allastnodes.add(undercontrolled);
			this.external_enable_cong_control.put(config, allastnodes);
		}
	}
	public void addExternalDisableConfigurationControl(ConfigurationOption config,Set<ASTNode> undercontrolled){
		if(this.external_disable_cong_control.containsKey(config)){
			Set<ASTNode> allastnodes = this.external_disable_cong_control.get(config);
			allastnodes.addAll(undercontrolled);
			this.external_disable_cong_control.put(config, allastnodes);
		}else
			this.external_disable_cong_control.put(config, undercontrolled);
	}
	public void addExternalDisableConfigurationControl(ConfigurationOption config,ASTNode undercontrolled){
		if(this.external_disable_cong_control.containsKey(config)){
			Set<ASTNode> allastnodes = this.external_disable_cong_control.get(config);
			allastnodes.add(undercontrolled);
			this.external_disable_cong_control.put(config, allastnodes);
		}else{
			Set<ASTNode> allastnodes = new HashSet<ASTNode>();
			allastnodes.add(undercontrolled);
			this.external_disable_cong_control.put(config, allastnodes);
		}
	}
	
	public Set<ASTNode> getExternalEnableConfigurationControl(ConfigurationOption config){
		if(external_enable_cong_control.containsKey(config)){
			return external_enable_cong_control.get(config);
		}else
			return new HashSet<ASTNode>();
	}
	
	public Set<ASTNode> getExternalDisableConfigurationControl(ConfigurationOption config){
		if(external_disable_cong_control.containsKey(config)){
			return external_disable_cong_control.get(config);
		}else
			return new HashSet<ASTNode>();
	}
	
	public Set<ConfigurationOption> getAllConfigurationOptions(){
		if(configurations==null){
			configurations = configuration_method.keySet();
		}
		return configurations;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		CompilationUnit unit = (CompilationUnit)dominateASTNode;
		List types = unit.types();    
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class  
		return typeDec.getName().toString();
	}
	
	public String getModuleName(){
		CompilationUnit unit = (CompilationUnit)dominateASTNode;
		List types = unit.types();    
		TypeDeclaration typeDec = (TypeDeclaration) types.get(0); //typeDec is the class  
		return "Module:"+typeDec.getName().toString();
	}

	public Set<ModuleComponent> getComponents() {
		// TODO Auto-generated method stub
		return components;
	}

	public ModuleModel getParent() {
		// TODO Auto-generated method stub
		return model;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return moduleIndex+"";
	}

	public CompilationUnit getCompilationUnit() {
		// TODO Auto-generated method stub
		return this.dominate.getCompilationUnit();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(!(obj instanceof Module)){
			return false;
		}else{
			Module module_obj = (Module)obj;
			if(module_obj.moduleIndex==this.moduleIndex)
				return true;
			else
				return false;
		}
		
	}

	public IFile getIFile() {
		// TODO Auto-generated method stub
		IFile file = null;
		CompilationUnit unit = this.dominate.getCompilationUnit();
		ICompilationUnit iunit = (ICompilationUnit) unit.getJavaElement();
		IPath path = iunit.getPath();
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		return file;
	}

	public void resolveDependency() {
		// TODO Auto-generated method stub
		ModuleDependencyBuilder dependencybuilder = new ModuleDependencyBuilder(this);
		dependencybuilder.parse();
		
	}
	
	
	
	
}
