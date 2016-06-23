package loongplugin.uml.editpart;


import loongplugin.uml.classdiagram.figure.RealizationConnectionFigure;

import org.eclipse.draw2d.IFigure;

public class RealizationEditPart extends AbstractUMLConnectionEditPart {

	protected IFigure createFigure() {
		return new RealizationConnectionFigure();
	}
	
}

