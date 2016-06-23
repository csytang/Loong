package loongplugin.uml.editpart;

import java.beans.PropertyChangeEvent;

import loongplugin.uml.classdiagram.figure.AggregationConnectionFigure;
import loongplugin.uml.model.AggregationModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class AggregationEditPart extends AbstractUMLConnectionEditPart {
	
	protected IFigure createFigure() {
		AggregationModel model = (AggregationModel)getModel();
		return new AggregationConnectionFigure(model);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		AggregationModel model = (AggregationModel)getModel();
		((AggregationConnectionFigure) getFigure()).update(model);
		super.propertyChange(evt);
	}

	protected Label getStereoTypeLabel() {
		return ((AggregationConnectionFigure) getFigure()).getStereoTypeLabel();
	}

}