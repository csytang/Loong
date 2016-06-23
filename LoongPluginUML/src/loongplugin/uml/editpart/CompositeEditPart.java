package loongplugin.uml.editpart;

import java.beans.PropertyChangeEvent;





import loongplugin.uml.classdiagram.figure.CompositeConnectionFigure;
import loongplugin.uml.model.CompositeModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class CompositeEditPart extends AbstractUMLConnectionEditPart {
	
	protected IFigure createFigure() {
		CompositeModel model = (CompositeModel)getModel();
		return new CompositeConnectionFigure(model);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		CompositeModel model = (CompositeModel)getModel();
		((CompositeConnectionFigure) getFigure()).update(model);
		super.propertyChange(evt);
	}

	protected Label getStereoTypeLabel() {
		return ((CompositeConnectionFigure)getFigure()).getStereoTypeLabel();
	}
	
}

