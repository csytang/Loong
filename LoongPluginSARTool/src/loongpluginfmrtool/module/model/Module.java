package loongpluginfmrtool.module.model;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.builder.ExternalConfBuilder;
import loongpluginfmrtool.module.builder.InternalConfBuilder;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.util.ASTNodeWalker;
import loongpluginfmrtool.module.util.ASTSubBindingFinder;

public class Module {
	private LElement dominate;
	private int moduleIndex=0;
	private Set<LElement> allmethods = new HashSet<LElement>();
	private Set<Import> imports = new HashSet<Import>();
	
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
	
	
	public Module(LElement element,int index,LFlyweightElementFactory pElementFactory,ModuleBuilder mbuilder){
		this.dominate = element;
		this.moduleIndex = index;
		this.lElementfactory = pElementFactory;
		this.abuilder = mbuilder;
		this.dominateASTNode = element.getASTNode();		
		this.contflowbuilder = new InternalConfBuilder(this);
		
	}
	
	/**
	 * initialize this module
	 */
	public void initialize(){
		// resolve import
		resolveimport();
		
		// resolve body
		resolvebody();
		
		// resolve variability
		resolvevariability();
		
	}
	
	public void externalvariability(){
		System.out.println("----------EXTERNAL-------------");
		externalconfbuilder = new ExternalConfBuilder(this,lElementfactory);
		externalconfbuilder.parse();
		System.out.println("------------------------------");
	}
	
	
	private void resolvevariability(){
		this.contflowbuilder.parse();
		this.method_configurations = this.contflowbuilder.getMethod_To_Configuration();
		this.configuration_method = this.contflowbuilder.getConfiguration_To_Method();
	}
	
	public Map<LElement,Set<ConfigurationOption>> getMethod_To_Configuration(){
		return this.method_configurations;
	}
	
	public Map<ConfigurationOption,LElement> getConfiguration_To_Method(){
		return this.configuration_method;
	}
	
	private void resolveimport() {
		// TODO Auto-generated method stub
		// obtain all LElement that referenced in this element and removed all defined in this
		if(dominateASTNode!=null){
			
			// Process bindings
			Map<ASTNode,IBinding> allastnodebindings = ASTSubBindingFinder.astBindingFinder(dominateASTNode);
			
			for(Map.Entry<ASTNode, IBinding>entry:allastnodebindings.entrySet()){
				IBinding binding = entry.getValue();
				LElement bindelement = lElementfactory.getElement(binding);
				
				if(bindelement!=null){
					// build an import
					LElement useelement = lElementfactory.getElement(entry.getKey());
					ImportType importtype = ImportType.NONE;
					switch(binding.getKind()){
						case IBinding.TYPE:{
							importtype = ImportType.CLASS_INSTANCE;
							break;
						}
						case IBinding.METHOD:{
							importtype = ImportType.METHOD;
							break;
						}
						case IBinding.VARIABLE:{
							importtype = ImportType.CLASS_FIELD;
							break;
						}
					}
					Module usemodule = abuilder.getModuleByLElement(useelement);
					Import mimport = new Import(useelement,bindelement,this,usemodule,importtype); 
					this.imports.add(mimport);
				}
			}
		}
		
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
					System.out.println("Cannot find");
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
}
