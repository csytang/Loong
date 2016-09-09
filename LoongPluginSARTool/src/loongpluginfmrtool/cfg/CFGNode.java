package loongpluginfmrtool.cfg;

import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;

public class CFGNode extends LElement{

	public CFGNode(String pId, LICategories pcategory,
			CLRAnnotatedSourceFile pColorSourceFile, ASTNode pastNode) {
		super(pId, pcategory, pColorSourceFile, pastNode);
		// TODO Auto-generated constructor stub
	}
	
	
}
