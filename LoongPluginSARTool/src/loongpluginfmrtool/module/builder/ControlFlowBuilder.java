package loongpluginfmrtool.module.builder;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.util.ASTNodeWalker;

public class ControlFlowBuilder {
	private Module module;
	private Set<LElement> method_elements = new HashSet<LElement>();
	private ASTNode moduleastnode;
	private LFlyweightElementFactory LElementFactory = null;
	public ControlFlowBuilder(Module pmodule){
		this.module = pmodule;
		this.moduleastnode = this.module.getDominateElement().getASTNode();
		this.LElementFactory = this.module.getelementfactory();
	}
	public void build(){
		// 1. process to a collection of method
		obtainMethodInside();
		
		// 2. build the control flow graph for each method
		processMethodControlflowGraph();
		
		
	}
	
	/**
	 * get all method declared in this module
	 */
	protected void obtainMethodInside(){
		assert module!=null;
		Set<ASTNode> method_astnodes = ASTNodeWalker.methodWalker(moduleastnode);
		for(ASTNode method_ast:method_astnodes){
			MethodDeclaration methoddecl = (MethodDeclaration)method_ast;
			IBinding methodbind = methoddecl.resolveBinding();
			assert LElementFactory!=null;
			LElement method_element = LElementFactory.getElement(methodbind);
			assert method_element!=null;
			this.method_elements.add(method_element);
		}
		
	}
	
	/**
	 * create the control flow graph for all methods declared
	 */
	protected void processMethodControlflowGraph(){
		for(LElement method:this.method_elements){
			
		}
	}
}
