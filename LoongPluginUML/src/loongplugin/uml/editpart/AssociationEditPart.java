package loongplugin.uml.editpart;

import java.beans.PropertyChangeEvent;

import loongplugin.uml.classdiagram.figure.AssociationConnectionFigure;
import loongplugin.uml.model.AssociationModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class AssociationEditPart extends AbstractUMLConnectionEditPart {
	
	protected IFigure createFigure() {
		AssociationModel model = (AssociationModel)getModel();
		return new AssociationConnectionFigure(model);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		AssociationModel model = (AssociationModel)getModel();
		((AssociationConnectionFigure) getFigure()).update(model);
		super.propertyChange(evt);
	}

	protected Label getStereoTypeLabel() {
		return ((AssociationConnectionFigure) getFigure()).getStereoTypeLabel();
	}

}
