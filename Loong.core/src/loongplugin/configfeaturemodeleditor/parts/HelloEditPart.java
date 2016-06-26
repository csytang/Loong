package loongplugin.configfeaturemodeleditor.parts;

import java.beans.PropertyChangeEvent;

import loongplugin.configfeaturemodeleditor.model.ConfFeature;
import loongplugin.configfeaturemodeleditor.policies.CustomComponentEditPolicy;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;

public class HelloEditPart extends EditPartWithListener {

	@Override
	protected IFigure createFigure() {
		ConfFeature model = (ConfFeature) getModel();

		Label label = new Label();
		label.setText(model.getText());
		label.setBorder(new CompoundBorder(new LineBorder(),
				new MarginBorder(3)));
		label.setBackgroundColor(ColorConstants.orange);
		label.setOpaque(true);

		return label;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new CustomComponentEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		Rectangle constraint = ((ConfFeature) getModel()).getConstraint();
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), constraint);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ConfFeature.PROP_CONSTRAINT))
			refreshVisuals();
	}

}
