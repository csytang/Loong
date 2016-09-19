package loongpluginfmrtool.module.model;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.action.ModuleAction;
import loongpluginfmrtool.module.builder.InternalConfBuilder;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.module.util.ASTNodeWalker;
import loongpluginfmrtool.module.util.ASTSubBindingFinder;

public class Module {
	private LElement dominate;
	private int moduleIndex=0;
	private Set<LElement> module_Body = new HashSet<LElement>();
	private Set<Import> imports = new HashSet<Import>();
	private Set<LElement> allelements = new HashSet<LElement>();
	private LFlyweightElementFactory LElementFactory;
	private ASTNode dominateASTNode;
	private ModuleBuilder abuilder;
	private ModuleAction amoduleaction;
	private InternalConfBuilder contflowbuilder;
	private List<ConfigurationOption> configoptions;
	public Module(LElement element,int index,LFlyweightElementFactory pElementFactory,ModuleBuilder mbuilder){
		this.dominate = element;
		this.moduleIndex = index;
		this.LElementFactory = pElementFactory;
		this.abuilder = mbuilder;
		this.dominateASTNode = element.getASTNode();
		Set<ASTNode> allnodes = ASTNodeWalker.allWalker(dominateASTNode);
		for(ASTNode node:allnodes){
			LElement subelement = LElementFactory.getElement(node);
			abuilder.addLElementModuleMapping(subelement, this);
			allelements.add(subelement);
		}
		this.contflowbuilder = new InternalConfBuilder(this);
		this.configoptions = new LinkedList<ConfigurationOption>();
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
	
	
	private void resolvevariability(){
		this.contflowbuilder.parse();
	}
	
	private void resolveimport() {
		// TODO Auto-generated method stub
		// obtain all LElement that referenced in this element and removed all defined in this
		if(dominateASTNode!=null){
			
			// Process bindings
			Map<ASTNode,IBinding> allastnodebindings = ASTSubBindingFinder.astBindingFinder(dominateASTNode);
			
			for(Map.Entry<ASTNode, IBinding>entry:allastnodebindings.entrySet()){
				IBinding binding = entry.getValue();
				LElement bindelement = LElementFactory.getElement(binding);
				
				if(!allelements.contains(bindelement)){
					// build an import
					LElement useelement = LElementFactory.getElement(entry.getKey());
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

	private void resolvebody(){
		// find the method body inside the class
		if(dominateASTNode!=null){
			Set<ASTNode> methodASTNodes = ASTNodeWalker.methodWalker(dominateASTNode);
			for(ASTNode method:methodASTNodes){
				LElement methodelement = LElementFactory.getElement(method);
				if(methodelement!=null)
					module_Body.add(methodelement);
			}
		}
	}
	
	public LElement getDominateElement(){
		return dominate;
	}

	public LFlyweightElementFactory getelementfactory() {
		// TODO Auto-generated method stub
		return LElementFactory;
	}
	
}
