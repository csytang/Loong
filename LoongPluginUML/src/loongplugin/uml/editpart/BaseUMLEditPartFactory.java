package loongplugin.uml.editpart;

import loongplugin.uml.model.AnchorModel;
import loongplugin.uml.model.NoteModel;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * NoteとAnchorのみ対応するEditPartFactory.
 * @author Takahiro Shida.
 *
 */
public abstract class BaseUMLEditPartFactory implements EditPartFactory {

	/* (非 Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = createUMLEditPart(context, model);		
		if(model instanceof NoteModel) {
			part = new NoteEditPart();
		} else if (model instanceof AnchorModel) {
			part = new AnchorEditPart();
		}
		if (part.getModel() == null) {
			part.setModel(model);
		}
		return part;
	}

	protected abstract EditPart createUMLEditPart(EditPart context, Object model);
}
