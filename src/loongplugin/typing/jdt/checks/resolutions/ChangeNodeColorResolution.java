/**
    Copyright 2010 Christian K�stner

    This file is part of CIDE.

    CIDE is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.

    CIDE is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CIDE.  If not, see <http://www.gnu.org/licenses/>.

    See http://www.fosd.de/cide/ for further information.
*/

package loongplugin.typing.jdt.checks.resolutions;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Image;

import loongplugin.LoongPlugin;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.color.coloredfile.IColorManager;
import loongplugin.events.ASTColorChangedEvent;
import loongplugin.feature.Feature;
import loongplugin.typing.model.AbstractTypingMarkerResolution;


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
		LoongPlugin.getDefault().notifyListeners(
				new ASTColorChangedEvent(this, targetNode, source));
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
