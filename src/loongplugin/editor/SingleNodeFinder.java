package loongplugin.editor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;



public class SingleNodeFinder extends ASTVisitor {
	private int offset;

	public SingleNodeFinder(int offset) {
		this.offset = offset;
	}

	ASTNode result = null;


	@Override
	public void postVisit(ASTNode node) {
		// TODO Auto-generated method stub
		if (node.getStartPosition() <= offset
				&& (node.getStartPosition() + node.getLength()) > offset) {
			if (result == null || node.getLength() < result.getLength())
				result = node;
		}
	}
	
	public ASTNode getResult(){
		return result;
	}
	
}