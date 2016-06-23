package loongplugin.uml.classdiagram.figure;


import loongplugin.uml.LoongUMLPlugin;

import org.eclipse.draw2d.Figure;

/**
 * The factory to create figures in the class diagram.
 * 
 * @author shidat
 */
public class ClassFigureFactory {

	public static UMLClassFigure getClassFigure() { 
		if (LoongUMLPlugin.getDefault().getPreferenceStore().getBoolean(LoongUMLPlugin.PREF_NEWSTYLE)) {
			return new ClassFigure();
		}
		return new UMLClassFigure(LoongUMLPlugin.getImageDescriptor("icons/class.gif").createImage(), new Figure());
	}
	
	public static UMLClassFigure getInterfaceFigure() {
		if (LoongUMLPlugin.getDefault().getPreferenceStore().getBoolean(LoongUMLPlugin.PREF_NEWSTYLE)) {
			return new InterfaceFigure();
		}
		return new UMLClassFigure(LoongUMLPlugin.getImageDescriptor("icons/interface.gif").createImage(), new Figure());
	}
}