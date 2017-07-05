package loongplugin.editor.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import loongplugin.color.ColorHelper;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.SourceFileColorManager;
import loongplugin.editor.SelectionFinder;
import loongplugin.editor.SingleNodeFinder;
import loongplugin.feature.Feature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;






public abstract class ColoredTextHover implements IJavaEditorTextHover {

	protected CLRAnnotatedSourceFile sourceFile;
	protected static final String NOT_COLORED = "Selected code fragment cannot be colored";
	// private final IProject project;
	private SourceFileColorManager colorManager;
	protected String NL = "\n";
	
	public ColoredTextHover(CLRAnnotatedSourceFile psourceFile){
		setColoredSourceFile(psourceFile);
	}
	
	protected void setColoredSourceFile(CLRAnnotatedSourceFile psourceFile) {
		// TODO Auto-generated method stub
		this.sourceFile = psourceFile;
		if(this.sourceFile!=null){
			this.colorManager = (SourceFileColorManager) this.sourceFile.getColorManager();
		}else{
			this.colorManager = null;
		}
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		// TODO Auto-generated method stub
		if (hoverRegion == null)
			return null;

		// find colors
		List<ASTNode> nodes = findSelectedNodes(hoverRegion);

		if ((nodes == null || nodes.isEmpty()) && hoverRegion.getLength() == 0)
			return null;
		if (nodes == null || nodes.isEmpty())
			return NOT_COLORED;

		String tooltip = "";

		tooltip += tooltipNodes(nodes);
		String ttc = tooltipColors(nodes);
		if (ttc == null)
			return null;
		tooltip += ttc;

		return tooltip.trim();
	}
	private Set<Feature> getAllColors(Collection<ASTNode> nodes) {
		assert nodes.size() > 0;
		Set<Feature> result = new HashSet<Feature>();
		Iterator<ASTNode> i = nodes.iterator();
		result.addAll(colorManager.getColors(i.next()));
		while (i.hasNext()) {
			join(result, colorManager.getColors(i.next()));
		}
		return result;
	}
	/**
	 * removes all entries from target that are not contained in newEntries
	 * 
	 * @param target
	 * @param newEntries
	 */
	private void join(Set<Feature> target, Set<Feature> newEntries) {
		for (Iterator<Feature> i = target.iterator(); i.hasNext();) {
			if (!newEntries.contains(i.next()))
				i.remove();
		}
	}
	
	private String tooltipColors(List<ASTNode> nodes) {
		// TODO Auto-generated method stub
		assert nodes.size() > 0;
		String tooltip = "";
		// all colors
		Set<Feature> allColors = getAllColors(nodes);
		tooltip += printColors(
				(nodes.size() > 1 ? "Common " : "") + "Features", allColors);

		// details (direct colors, inherited colors, file colors)
		tooltip += NL;
		Set<Feature> directColors = getDirectColors(nodes);
		if (directColors.size() > 0)
			tooltip += printColorsShort("Direct Colors", directColors);
		else
			tooltip += "No direct colors."+NL;

		Set<Feature> inheritedColors = getInheritedColors(nodes);
		if (inheritedColors.size() > 0)
			tooltip += printColorsShort("Inherited Colors", inheritedColors);

		//Set<Feature> fileColors = getFileColors();
		//if (fileColors.size() > 0)
		//	tooltip += printColorsShort("File Colors", fileColors);

		// for no colors whatsoever return no tooltip
		if (directColors.size() == 0 && inheritedColors.size() == 0)
			return null;

		return tooltip;
	}
	
	private Set<Feature> getDirectColors(Collection<ASTNode> nodes) {
		assert nodes.size() > 0;
		Set<Feature> result = new HashSet<Feature>();
		Iterator<ASTNode> i = nodes.iterator();
		result.addAll(colorManager.getOwnColors(i.next()));
		while (i.hasNext()) {
			join(result, colorManager.getOwnColors(i.next()));
		}
		return result;
	}
	
	
	
	private Set<Feature> getInheritedColors(Collection<ASTNode> nodes) {
		// all inherited colors, but not the file colors
		assert nodes.size() > 0;
		Set<Feature> result = new HashSet<Feature>();
		Iterator<ASTNode> i = nodes.iterator();
		result.addAll(colorManager.getInheritedColors(i.next()));
		while (i.hasNext()) {
			join(result, colorManager.getInheritedColors(i.next()));
		}
		//result.removeAll(getFileColors());
		return result;
	}

	private String printColors(String title, Collection<Feature> colors) {
		String result = title + ":"+NL;
		List<Feature> sortedColors = ColorHelper.sortFeatures(colors);
		for (Feature color : sortedColors) {
			result += " - " + color.getName() + NL;
		}
		return result;
	}

	
	private String printColorsShort(String title, Collection<Feature> colors) {
		String result = title + ": ";
		List<Feature> sortedColors = ColorHelper.sortFeatures(colors);
		boolean first = true;
		for (Feature color : sortedColors) {
			if (first)
				first = false;
			else
				result += ", ";
			result += color.getName();
		}
		return result + NL;
	}
	private String tooltipNodes(List<ASTNode> nodes) {
		// TODO Auto-generated method stub
		assert nodes.size() > 0;
		String result = "";
		for (int nodeidx = 0; nodeidx < nodes.size() && nodeidx < 5; nodeidx++) {
			String aststr = getASTStringOutput(nodes.get(nodeidx));
			result += "\"" + aststr + "\""+NL;
		}
		if (nodes.size() > 5)
			result += "..."+NL;

		return result + NL;
	}

	private String getASTStringOutput(ASTNode astNode) {
		// TODO Auto-generated method stub
		String aststr = astNode.toString().trim();
		aststr = aststr.replace('\n', ' ').replace('\r', ' ')
				.replace('\t', ' ');
		if (aststr.length() > 60) {
			aststr = aststr.substring(0, 50) + " ... "
					+ aststr.substring(aststr.length() - 5);
		}
		return aststr;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		// TODO Auto-generated method stub
		Point selection = textViewer.getSelectedRange();
		if (selection.x <= offset && offset < selection.x + selection.y)
			return new Region(selection.x, selection.y);
		return new Region(offset, 0);
	}

	private List<ASTNode> findSelectedNodes(IRegion hoverRegion) {
		CompilationUnit ast;
		try {
			ast = sourceFile.getAST();
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		} 

		ArrayList<ASTNode> result = new ArrayList<ASTNode>();
		if (hoverRegion.getLength() == 0) {
			SingleNodeFinder snf = new SingleNodeFinder(hoverRegion.getOffset());
			ast.accept(snf);
			ASTNode node = snf.getResult();
			
			while (node != null)
				node = node.getParent();
			if (node != null)
				result.add(node);
		} else
			ast.accept(new SelectionFinder(new HashSet<ASTNode>(result), hoverRegion)); 
		
		return result;
	}

	@Override
	public abstract void setEditor(IEditorPart editor) ;
}
