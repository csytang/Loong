package loongpluginfmrtool.module.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.Type;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.Module;

public class ExternalConfBuilder {
	private Module module;
	private ModuleBuilder modulebuilder = null;
	private Map<LElement,Set<ConfigurationOption>> method_configurations = new HashMap<LElement,Set<ConfigurationOption>>();
	private Map<ConfigurationOption,LElement>configuration_method = new HashMap<ConfigurationOption,LElement>();
	private List<LElement>allmethods = new LinkedList<LElement>();
	
	private Map<ConfigurationOption,List<Module>>cong_link_modules = new HashMap<ConfigurationOption,List<Module>>();
	private Map<ConfigurationOption,List<Module>>cong_unlink_modules = new HashMap<ConfigurationOption,List<Module>>();
	private LFlyweightElementFactory LElementFactory;
	
	public ExternalConfBuilder(Module pmodule,LFlyweightElementFactory pLElementFactory){
		this.module = pmodule;
		this.modulebuilder = ModuleBuilder.instance;
		this.LElementFactory = pLElementFactory;
		this.method_configurations = pmodule.getMethod_To_Configuration();
		this.configuration_method = pmodule.getConfiguration_To_Method();
		this.allmethods = new LinkedList<LElement>(this.module.getallMethods()); 
	}
	public void parse(){
		// process basic module linking information
		for(LElement method:allmethods){
			if(method_configurations.containsKey(method)){
				Set<ConfigurationOption> configurations = method_configurations.get(method);
				for(ConfigurationOption option:configurations){
					Set<Statement> enablestatements = option.getEnable_Statements();
					Set<Statement> disablestatements = option.getDisable_Statements();
					List<Module> enable_external_modules = directToOtherModules(enablestatements);
					if(!enable_external_modules.isEmpty()){
						cong_link_modules.put(option, enable_external_modules);						
					}
					
					if(disablestatements!=null){
						List<Module> disable_external_modules = directToOtherModules(disablestatements);
						if(!disable_external_modules.isEmpty()){
							cong_unlink_modules.put(option, disable_external_modules);
						}
					}
				}
			}
		}
		// extracting variability
		
	}
	
	protected List<Module> directToOtherModules(Set<Statement> statements){
		List<Module> externals = new LinkedList<Module>();
		if(statements==null){
			return externals;
		}else if(statements.isEmpty()){
			return externals;
		}
		
		return externals;
	}
	
	class MethodInvocationVisitor extends ASTVisitor{
		private ConfigurationOption config;
		private boolean isEnable = true;
		public MethodInvocationVisitor(ConfigurationOption pconfig,boolean pisEnable){
			config = pconfig;
			isEnable = pisEnable;
		}
		
		private void addToEnableConfigurationControl(ASTNode node,Module remote_module){
			module.addExternalEnableConfigurationControl(config, node);
			if(cong_link_modules.containsKey(config)){
				List<Module> modules = cong_link_modules.get(config);
				if(!modules.contains(remote_module)){
					modules.add(remote_module);
					cong_link_modules.put(config, modules);
				}
			}else{
				List<Module> modules = new LinkedList<Module>();
				modules.add(remote_module);
				cong_link_modules.put(config, modules);
			}
		}
		private void addToDisableConfigurationControl(ASTNode node,Module remote_module){
			module.addExternalDisableConfigurationControl(config, node);
			if(cong_unlink_modules.containsKey(config)){
				List<Module> modules = cong_unlink_modules.get(config);
				if(!modules.contains(remote_module)){
					modules.add(remote_module);
					cong_unlink_modules.put(config, modules);
				}
			}else{
				List<Module> modules = new LinkedList<Module>();
				modules.add(remote_module);
				cong_unlink_modules.put(config, modules);
			}
		}
		
		@Override
		public boolean visit(ClassInstanceCreation node) {
			// TODO Auto-generated method stub
			Type instance_type = node.getType();
			ITypeBinding typebinding = instance_type.resolveBinding();
			LElement declelement = LElementFactory.getElement(typebinding);
			Module remote_module = ModuleBuilder.instance.getModuleByLElement(declelement);
			if(isEnable){
				addToEnableConfigurationControl(declelement.getASTNode(),remote_module);
			}else{
				addToDisableConfigurationControl(declelement.getASTNode(),remote_module);
			}
			return super.visit(node);
		}


		@Override
		public boolean visit(MethodInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveMethodBinding();
			LElement declelement = LElementFactory.getElement(method);
			CompilationUnit compilation_unit = declelement.getCompilationUnit();
			LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
			Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
			if(isEnable){
				addToEnableConfigurationControl(declelement.getASTNode(),remote_module);
			}else{
				addToDisableConfigurationControl(declelement.getASTNode(),remote_module);
			}
			
			return super.visit(node);
		}

		@Override
		public boolean visit(SuperConstructorInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveConstructorBinding();
			LElement declelement = LElementFactory.getElement(method);
			CompilationUnit compilation_unit = declelement.getCompilationUnit();
			LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
			Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
			
			if(isEnable){
				addToEnableConfigurationControl(declelement.getASTNode(),remote_module);
			}else{
				addToDisableConfigurationControl(declelement.getASTNode(),remote_module);
			}
			
			return super.visit(node);
		}

		@Override
		public boolean visit(SuperMethodInvocation node) {
			// TODO Auto-generated method stub
			IMethodBinding method = node.resolveMethodBinding();
			LElement declelement = LElementFactory.getElement(method);
			CompilationUnit compilation_unit = declelement.getCompilationUnit();
			LElement compilation_unit_element = LElementFactory.getElement(compilation_unit);
			Module remote_module = ModuleBuilder.instance.getModuleByLElement(compilation_unit_element);
			
			if(isEnable){
				addToEnableConfigurationControl(declelement.getASTNode(),remote_module);
			}else{
				addToDisableConfigurationControl(declelement.getASTNode(),remote_module);
			}
			
			return super.visit(node);
		}
		
	}

}
