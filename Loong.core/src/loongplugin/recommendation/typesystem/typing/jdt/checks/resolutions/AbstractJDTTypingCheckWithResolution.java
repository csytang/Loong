package loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.recommendation.typesystem.typing.jdt.AbstractJDTTypingCheck;
import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingCheckWithResolution;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingMarkerResolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;



public abstract class AbstractJDTTypingCheckWithResolution extends
		AbstractJDTTypingCheck implements ITypingCheckWithResolution {

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
		Collections.sort(resolutions);
		return resolutions.toArray(new ITypingMarkerResolution[resolutions
				.size()]);
	}

	protected abstract void addResolutions(ArrayList<ITypingMarkerResolution> resolutions,HashSet<Feature> colorDiff);

	protected abstract Set<Feature> getTargetColors();

	protected static ASTNode findCallingType(ASTNode node) {
		while (node != null && !(node instanceof Type))
			node = node.getParent();
		return node;
	}

	protected static ASTNode findCallingMethod(ASTNode node) {
		while (node != null
				&& !(node instanceof MethodRef || node instanceof MethodDeclaration))
			node = node.getParent();
		return node;
	}

	protected static ASTNode findCallingStatement(ASTNode node) {
		while (node != null
				&& !(node instanceof Statement))
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

					resolutions.add(new ChangeNodeColorResolution(file, node,
							Collections.singleton(color), add, nodeType,
							relevance));
				}
			resolutions.add(new ChangeNodeColorResolution(file, node,
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
						resolutions.add(new ChangeNodeColorResolution(file,
								currentNode, Collections.singleton(color), add,
								nodeType, relevance));
			}
		}

		return resolutions;
	}
}
