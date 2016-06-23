package loongplugin.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongplugin.CIDEbridge.CIDEASTNodeCollector;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;

public class EmbeddedASTNodeCollector {
	
	public static Set<ASTNode> collectASTNodes(ICompilationUnit unit){
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(unit);
		CompilationUnit result = (CompilationUnit) parser.createAST(null);
		
		CIDEASTNodeCollector cideastcollector = new CIDEASTNodeCollector();
		result.accept(cideastcollector);
		Set<ASTNode> astnodes = cideastcollector.getASTNodeSet();
		// 添加所有import 部分
		for (Object astnode : result.imports()) {
			if (astnode instanceof ImportDeclaration) {
				astnodes.add((ASTNode) astnode);
			}
		}
		astnodes.add(result);
		
		return astnodes;
		
	}
	
	
	public static Map<IBinding, Set<ASTNode>> collectBindingASTNodes(ASTNode node){
		Map<IBinding, Set<ASTNode>> bindingastnodes = new HashMap<IBinding, Set<ASTNode>>();
		CIDEASTNodeCollector cideastcollector = new CIDEASTNodeCollector();
		
		node.accept(cideastcollector);
		bindingastnodes = cideastcollector.getBindingASTNodeSet();
		return bindingastnodes;
	}

}
