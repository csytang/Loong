package loongplugin.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import org.eclipse.jdt.core.dom.ASTNode;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;


public class ASTColorChangedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private final Collection<ASTNode> nodes;

	private final CLRAnnotatedSourceFile sourceFile;

	public ASTColorChangedEvent(Object source, ASTNode node, CLRAnnotatedSourceFile sourceFile) {
		super(source);
		ArrayList<ASTNode> nodes=new ArrayList<ASTNode>();
		nodes.add(node);
		this.nodes = nodes;
		this.sourceFile=sourceFile;
	}

	public ASTColorChangedEvent(Object source, Collection<ASTNode> nodes, CLRAnnotatedSourceFile sourceFile) {
		super(source);
		assert nodes!=null && !nodes.isEmpty();
		this.nodes = nodes;
		this.sourceFile=sourceFile;
	}

	public Collection<ASTNode> getAffectedNodes() {
		return nodes;
	}
	
	public CLRAnnotatedSourceFile getColoredJavaSourceFile(){
		return sourceFile;
	}

}
