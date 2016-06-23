package loongplugin.uml.editpart;

import loongplugin.uml.classdiagram.figure.ClassFigureFactory;
import loongplugin.uml.classdiagram.figure.UMLClassFigure;



public class InterfaceEditPart extends CommonEntityEditPart {

	public UMLClassFigure getClassFigure() {
		return ClassFigureFactory.getInterfaceFigure();
	}
}
