package colordide.cideconfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.color.coloredfile.ASTColorInheritance;
import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.SourceFileColorManager;
import loongplugin.feature.Feature;
import loongplugin.utils.EmbeddedASTNodeCollector;

/**
 * bride that provides access to the color manager also for JDT-AST nodes
 * 
 * has to reimplement the inheritance functionality
 * 
 **/
public class JDTColorManagerBridge {

	//private final IFile file;
	private final CLRAnnotatedSourceFile colorfile;
	public JDTColorManagerBridge(SourceFileColorManager colorManager, CLRAnnotatedSourceFile colorfile) {
		this.originalColorManager = colorManager;
		this.colorfile = colorfile;
	}

	public JDTColorManagerBridge(CLRAnnotatedSourceFile source) {
		this((SourceFileColorManager) source.getColorManager(), source);
	}

	private SourceFileColorManager originalColorManager;

	public Set<Feature> getColors(ASTNode node) {
		Set<Feature> result = new HashSet<Feature>();
		result.addAll(getOwnColors(node));
		result.addAll(getInheritedColors(node));
		return Collections.unmodifiableSet(result);
	}

	public Set<Feature> getInheritedColors(ASTNode node) {
		return getInheritedColorsI(node, 1);
	}

	private Set<Feature> getInheritedColorsI(ASTNode node, int i) {
		Set<Feature> result = new HashSet<Feature>();

		ASTNode parent = node.getParent();
		if (parent != null) {
			if (ASTColorInheritance.inheritsColors(parent, node))
				result.addAll(getOwnColors(parent));
			result.addAll(getInheritedColorsI(parent, i + 1));
		}
		try {
			result.addAll(originalColorManager.getColors(colorfile.getAST()));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Collections.unmodifiableSet(result);
	}

	public Set<Feature> getOwnColors(ASTNode node) {
		return originalColorManager.getColors(node);
	}

}
