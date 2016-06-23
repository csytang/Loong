package loongplugin.uml.editpart;



import loongplugin.uml.classdiagram.figure.GeneralizationConnectionFigure;

import org.eclipse.draw2d.IFigure;

public class GeneralizationEditPart extends AbstractUMLConnectionEditPart {

	protected IFigure createFigure() {
		return new GeneralizationConnectionFigure();
	}

}
