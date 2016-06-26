package loongplugin.configfeaturemodeleditor.model;

import org.eclipse.draw2d.geometry.Rectangle;

public class ConfFeature extends AbstractModel {

	public static final String PROP_CONSTRAINT = "CONSTRAINT";

	private String text = "unknown";

	private Rectangle constraint;

	public Rectangle getConstraint() {
		return constraint;
	}

	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
		firePropertyChange(PROP_CONSTRAINT, null, constraint);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
