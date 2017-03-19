package loongplugin.utils;

import loongplugin.CIDEbridge.CIDEASTNodeCollector;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class LElementMethodFinder {
	public static MethodDeclaration findMethod(ASTNode node,CompilationUnit unit){
		MethodDeclaration parentMethod = null;
		if(node instanceof MethodDeclaration)
			return (MethodDeclaration)node;
		LElementMethodDeclarationCollector lelementcollector = new LElementMethodDeclarationCollector(parentMethod,node);
		unit.accept(lelementcollector);	
		parentMethod = lelementcollector.getMethodDeclarationNode();
		return parentMethod;
	}
}
class LElementMethodDeclarationCollector extends ASTVisitor{
		private MethodDeclaration aparentMethod;
		private ASTNode anode;
		public LElementMethodDeclarationCollector(final MethodDeclaration pparentMethod,ASTNode node){
			aparentMethod = pparentMethod;
			anode = node;
		}
		public MethodDeclaration getMethodDeclarationNode(){
			return aparentMethod;
		}
		@Override
		public boolean visit(MethodDeclaration node) {
			// TODO Auto-generated method stub
			if(node.getStartPosition()<=anode.getStartPosition()){
				if(node.getStartPosition()+node.getLength()>=anode.getStartPosition()+anode.getLength()){
					aparentMethod = node;
				}
			}
			return super.visit(node);
		}
}