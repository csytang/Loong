package loongplugin.uml.editpart;

import java.beans.PropertyChangeEvent;





import loongplugin.uml.classdiagram.figure.DependencyConnectionFigure;
import loongplugin.uml.model.DependencyModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class DependencyEditPart extends AbstractUMLConnectionEditPart {

	protected IFigure createFigure() {
		DependencyModel model = (DependencyModel)getModel();
		return new DependencyConnectionFigure(model);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		DependencyModel model = (DependencyModel)getModel();
		((DependencyConnectionFigure) getFigure()).update(model);
		refreshVisuals();
	}
	
	protected Label getStereoTypeLabel() {
		return ((DependencyConnectionFigure) getFigure()).getStereoTypeLabel();
	}
}
