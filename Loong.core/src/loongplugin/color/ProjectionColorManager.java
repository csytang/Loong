package loongplugin.color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.source.Annotation;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.IColoredJavaSourceFile;
import loongplugin.editor.CLREditor;
import loongplugin.editor.projection.CLRProjectionAnnotationModel;
import loongplugin.editor.projection.ColoredCLRProjectionAnnotation;
import loongplugin.editor.viewer.CLRJavaSourceViewer;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelManager;



@SuppressWarnings("restriction")
public class ProjectionColorManager {

	private final CLREditor editor;

	public ProjectionColorManager(CLREditor editor) {
		this.editor = editor;
	}

	private final Set<Feature> collapsedColors = new HashSet<Feature>();
	private IProject project;

	public Set<Feature> getExpandedColors() {
		IColoredJavaSourceFile sourceFile = CLRAnnotatedSourceFile.getColoredJavaSourceFile((ICompilationUnit) editor
						.getInputJavaElement());
		this.project = sourceFile.getProject();
		Set<Feature> visibleFeatures = new HashSet<Feature>(FeatureModelManager.getInstance(project).getFeatures());
		
		visibleFeatures.removeAll(collapsedColors);
		
		
		return visibleFeatures;
	}

	public void expandColor(Feature color) {
		collapsedColors.remove(color);
		updateProjectionAnnotations();
	}

	public void collapseColor(Feature color) {
		collapsedColors.add(color);
		updateProjectionAnnotations();
	}

	public void expandAllColors() {
		collapsedColors.clear();
		updateProjectionAnnotations();
	}
	public void collapseAllColors() {
		collapsedColors.addAll(FeatureModelManager.getInstance(project).getFeatures());
		updateProjectionAnnotations();
	}

	protected void updateProjectionAnnotations() {
		CLRJavaSourceViewer viewer = (CLRJavaSourceViewer) editor
				.getViewer();
		CLRProjectionAnnotationModel annotationModel = viewer
				.getCLRProjectionAnnotationModel();
		Set<Feature> visibleColors = getExpandedColors();

		List<Annotation> changedAnnotations = new ArrayList<Annotation>();
		for (Iterator iter = annotationModel.getAnnotationIterator(); iter
				.hasNext();) {
			ColoredCLRProjectionAnnotation annotation = (ColoredCLRProjectionAnnotation) iter
					.next();
			boolean changed = annotation.adjustCollapsing(visibleColors);
			if (changed)
				changedAnnotations.add(annotation);
		}

		annotationModel.modifyAnnotations(null, null, changedAnnotations
				.toArray(new Annotation[changedAnnotations.size()]));
	}
}
