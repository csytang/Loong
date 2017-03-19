package loongplugin.editor.projection;

import java.util.Set;

import loongplugin.feature.Feature;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;




public class CLRProjectionAnnotation extends Annotation implements IAnnotationPresentation {
	
	private static class DisplayDisposeRunnable implements Runnable {

		public void run() {
			if (fgCollapsedImage != null) {
				fgCollapsedImage.dispose();
				fgCollapsedImage = null;
			}
			if (fgExpandedImage != null) {
				fgExpandedImage.dispose();
				fgExpandedImage = null;
			}
		}
	}
	/**
	 * The type of projection annotations.
	 */
	public static final String TYPE = "org.eclipse.inlineprojection"; //$NON-NLS-1$

	private static Image fgCollapsedImage;

	private static Image fgExpandedImage;

	/** 
	 * annotation的状态
	 *  */
	private boolean fIsCollapsed = false;
	
	private Set<Feature> features;

	private Position position;
	
	private IProject project;
	/**
	 * Creates a new expanded projection annotation.
	 */
	public CLRProjectionAnnotation(Set<Feature> features, IProject project, Position pos){
		this(false,features,project,pos);
	}
	
	/**
	 * 创建一个投影的注释. 当是 <code>isCollapsed</code>
	 * 是真的时候 the annotation is initially collapsed.
	 * 
	 * @param isCollapsed
	 *            <code>true</code> if the annotation should initially be
	 *            collapsed, <code>false</code> otherwise
	 */
	public CLRProjectionAnnotation(boolean isCollapsed,Set<Feature> pfeatures, IProject project, Position pos){
		super(TYPE, false, null);
		fIsCollapsed = isCollapsed;
		this.features = pfeatures;
		this.position = pos;
		this.project = project;
	}
	
	@Override
	public int getLayer() {
		// TODO Auto-generated method stub
		return IAnnotationPresentation.DEFAULT_LAYER;
	}

	// 加入扩展、收缩所用图标
	@Override
	public void paint(GC gc, Canvas canvas, Rectangle rectangle) {
		// TODO Auto-generated method stub
		Image image = getImage(canvas.getDisplay());
		if (image != null) {
			ImageUtilities.drawImage(image, gc, canvas, rectangle,
					SWT.CENTER, SWT.TOP);
			
		}
	}
	
	//获得图像
	private Image getImage(Display display) {
		initializeImages(display);
		return isCollapsed() ? fgCollapsedImage : fgExpandedImage;
	}
	
	/**
	 * 返回annotation的状态
	 * 
	 * @return <code>true</code> if collapsed
	 */
	public boolean isCollapsed() {
		return fIsCollapsed;
	}
	
	//获得初始化图像
	private void initializeImages(Display display) {
		if (fgCollapsedImage == null) {

			ImageDescriptor descriptor = ImageDescriptor.createFromFile(
					ProjectionAnnotation.class, "images/collapsed.gif"); //$NON-NLS-1$
			fgCollapsedImage = descriptor.createImage(display);
			descriptor = ImageDescriptor.createFromFile(
					ProjectionAnnotation.class, "images/expanded.gif"); //$NON-NLS-1$
			fgExpandedImage = descriptor.createImage(display);
			display.disposeExec(new DisplayDisposeRunnable());
		}
	}
	
	/**
	 * Marks this annotation as being collapsed.
	 */
	public void markCollapsed() {
		fIsCollapsed = true;
	}

	/**
	 * Marks this annotation as being unfolded.
	 */
	public void markExpanded() {
		fIsCollapsed = false;
	}

	public void setPosition(Position pos) {
		this.position = pos;
	}

	public Position getPosition() {
		return position;
	}

	public Set<Feature> getColors() {
		return features;
	}
	
	public IProject getProject(){
		return project;
	}
	
	public void setColors(Set<Feature> features) {
		this.features = features;
	}

	public boolean adjustCollapsing(Set<Feature> expandedFeatures) {
		// TODO Auto-generated method stub
		boolean expanded = expandedFeatures.containsAll(features);
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
}
