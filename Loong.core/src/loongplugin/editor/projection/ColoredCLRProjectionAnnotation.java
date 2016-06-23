package loongplugin.editor.projection;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.Position;

import loongplugin.feature.Feature;


public class ColoredCLRProjectionAnnotation extends CLRProjectionAnnotation {
	
	public ColoredCLRProjectionAnnotation(boolean isCollapsed,
			Set<Feature> pfeatures, IProject project, Position pos) {
		super(isCollapsed, pfeatures, project, pos);
		// TODO Auto-generated constructor stub
	}

	private Set<Feature> colors;

	private Position position;

	public void setColors(Set<Feature> colors) {
		this.colors = colors;
	}

	public boolean adjustCollapsing(Set<Feature> selectedColors) {
		boolean expanded = selectedColors.containsAll(colors);
		if (isCollapsed() && expanded) {
			this.markExpanded();
			return true;
		}
		if (!isCollapsed() && !expanded) {
			this.markCollapsed();
			return true;
		}
		return false;
	}

	public void setPosition(Position pos) {
		this.position = pos;
	}

	public Position getPosition() {
		return position;
	}

	public Set<Feature> getColors() {
		return colors;
	}
}
