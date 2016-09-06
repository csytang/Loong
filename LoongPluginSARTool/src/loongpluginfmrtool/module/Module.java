package loongpluginfmrtool.module;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.util.ASTNodeWalker;

public class Module {
	private LElement dominate;
	private int moduleIndex=0;
	private Set<LElement> module_Body = new HashSet<LElement>();
	private LFlyweightElementFactory LElementFactory;
	private ASTNode dominateASTNode;
	public Module(LElement element,int index,LFlyweightElementFactory pElementFactory){
		this.dominate = element;
		this.moduleIndex = index;
		this.LElementFactory = pElementFactory;
		this.dominateASTNode = element.getASTNode();
	}
	
	/**
	 * initialize this module
	 */
	public void initialize(){
		// resolve import
		resolveimport();
		
		// resolve body
		resolvebody();
		
		// resolve configurations
		
		
		// resolve configuration options
		
		
		// resolve variability
		
		
	}
	
	private void resolveimport() {
		// TODO Auto-generated method stub
		// obtain all LElement that referenced in this element and removed all defined in this
		
		
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
	
}
