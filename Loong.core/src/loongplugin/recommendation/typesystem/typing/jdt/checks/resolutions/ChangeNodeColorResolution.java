package loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions;

import java.util.Iterator;
import java.util.Set;

import loongplugin.LoongPlugin;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.IColorManager;
import loongplugin.events.ASTColorChangedEvent;
import loongplugin.feature.Feature;
import loongplugin.recommendation.typesystem.typing.jdt.model.AbstractTypingMarkerResolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Image;



public class ChangeNodeColorResolution extends AbstractTypingMarkerResolution {

	private final Set<Feature> colorDiff;

	private boolean add;

	private ASTNode targetNode;

	private String title = null;

	private String description = null;

	protected CLRAnnotatedSourceFile source;

	private String nodeType = null;

	public ChangeNodeColorResolution(CLRAnnotatedSourceFile source,
			ASTNode targetNode, Set<Feature> colorDiff, boolean add) {
		this.targetNode = targetNode;
		this.colorDiff = colorDiff;
		this.add = add;
		this.source = source;
	}

	public ChangeNodeColorResolution(CLRAnnotatedSourceFile source,
			ASTNode targetNode, Set<Feature> colorDiff, boolean add,
			String title, String desc) {
		this(source, targetNode, colorDiff, add);
		this.title = title;
		this.description = desc;
	}

	public ChangeNodeColorResolution(CLRAnnotatedSourceFile source,
			ASTNode targetNode, Set<Feature> colorDiff, boolean add,
			String nodeType, int relevance) {
		this(source, targetNode, colorDiff, add);
		this.nodeType = nodeType;
		setRelevance(relevance);
	}

	public void run(IMarker marker) {
		nodeColors().beginBatch();
		for (Feature color : colorDiff) {
			if (add)
				nodeColors().addColor(targetNode, color);
			else
				nodeColors().removeColor(targetNode, color);
		}
		nodeColors().endBatch();
		LoongPlugin.getDefault().notifyListeners(new ASTColorChangedEvent(this, targetNode, source));
	}

	protected IColorManager nodeColors() {
		return source.getColorManager();
	}

	public String getLabel() {
		if (title != null)
			return title;
		return generateTitle();
	}

	private String generateTitle() {
		String t = add ? "Add" : "Remove";
		t += " feature";
		t += colorDiff.size() > 1 ? "s " : " ";
		t += add ? "to " : "from ";
		t += nodeType == null ? "node" : nodeType;
		t += ": ";
		for (Iterator<Feature> iterator = colorDiff.iterator(); iterator
				.hasNext();) {
			t += iterator.next().getName() + (iterator.hasNext() ? ", " : "");
		}
		return t + ".";
	}

	public String getDescription() {
		return description;
	}

	public Image getImage() {
		return null;
	}

}
