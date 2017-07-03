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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.typing.model.*;
import loongplugin.typing.jdt.*;
import loongplugin.typing.jdt.checks.AbstractJDTTypingCheck;


public abstract class AbstractJDTTypingCheckWithResolution extends	AbstractJDTTypingCheck implements ITypingCheckWithResolution {

	public AbstractJDTTypingCheckWithResolution(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source) {
		super(file, typingProvider, source);
	}

	public ITypingMarkerResolution[] getResolutions(IMarker marker) {
		Set<Feature> declColors = getTargetColors();
		if (declColors == null) {
			return NO_RESOLUTIONS;
		}

		Set<Feature> callColors = file.getColorManager().getColors(source);

		HashSet<Feature> colorDiff = new HashSet<Feature>();
		colorDiff.addAll(declColors);
		colorDiff.removeAll(callColors);
		if (colorDiff.isEmpty())
			return NO_RESOLUTIONS;

		ArrayList<ITypingMarkerResolution> resolutions = new ArrayList<ITypingMarkerResolution>();
		addResolutions(resolutions, colorDiff);
		//Collections.sort(resolutions);
		Collections.sort(resolutions);
		return resolutions.toArray(new ITypingMarkerResolution[resolutions
				.size()]);
	}

	protected abstract void addResolutions(
			ArrayList<ITypingMarkerResolution> resolutions,
			HashSet<Feature> colorDiff);

	protected abstract Set<Feature> getTargetColors();

	protected static ASTNode findCallingType(ASTNode node) {
		
		while (node != null 
				&& !(node.getNodeType()==ASTNode.TYPE_DECLARATION))
			node = node.getParent();
		return node;
	}

	protected static ASTNode findCallingMethod(ASTNode node) {
		
		while (node != null
				&& !(node.getNodeType()==ASTNode.METHOD_DECLARATION))
			node = node.getParent();
		return node;
	}

	protected static ASTNode findCallingStatement(ASTNode node) {
		while (node != null 
				&& !(node.getNodeType()==ASTNode.EXPRESSION_METHOD_REFERENCE||
				node.getNodeType()==ASTNode.EXPRESSION_STATEMENT))
			node = node.getParent();
		return node;
	}

	protected Collection<ITypingMarkerResolution> createChangeNodeColorResolution(
			ASTNode node, HashSet<Feature> colorDiff, boolean add,
			String nodeType, int relevance) {

		if (node == null)
			return Collections.EMPTY_SET;
		ArrayList<ITypingMarkerResolution> resolutions = new ArrayList<ITypingMarkerResolution>();

		if (add) {
			if (colorDiff.size() > 1)
				for (Feature color : colorDiff) {

					resolutions.add((ITypingMarkerResolution)new ChangeNodeColorResolution(file, node,
							Collections.singleton(color), add, nodeType,
							relevance));
				}
			resolutions.add((ITypingMarkerResolution)new ChangeNodeColorResolution(file, node,
					colorDiff, add, nodeType, relevance + 1));
		}

		if (!add) {
			// find declaring node
			for (Feature color : colorDiff) {
				ASTNode currentNode = node;
				while (currentNode != null
						&& !file.getColorManager().hasColor(currentNode, color)) {
					currentNode = currentNode.getParent();
				}

				if (currentNode != null)
					if (file.getColorManager().hasColor(currentNode, color))
						resolutions.add((ITypingMarkerResolution)new ChangeNodeColorResolution(file,
								currentNode, Collections.singleton(color), add,
								nodeType, relevance));
			}
		}

		return resolutions;
	}
}
