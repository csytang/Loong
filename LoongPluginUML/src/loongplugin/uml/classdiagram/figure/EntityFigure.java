package loongplugin.uml.classdiagram.figure;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * ラベルが設定できるFigure.
 * @author Takahiro Shida.
 *
 */
public interface EntityFigure extends IFigure{

	Label getLabel();
	
	Rectangle getCellEditorRectangle();
	
}
