package loongplugin.configfeaturemodeleditor.parts;

import loongplugin.configfeaturemodeleditor.model.ConfFeatureModel;
import loongplugin.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class PartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		// get EditPart for model element
		EditPart part = getPartForElement(model);
		// store model element in EditPart
		part.setModel(model);
		return part;
	}

	/**
	 * Maps an object to an EditPart.
	 */
	private EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof ConfFeatureModel) 
			return new ContentsEditPart();
		if (modelElement instanceof ConfFeature) 
			return new HelloEditPart();

		throw new RuntimeException("Can't create part for model element: "
				+ ((modelElement != null) ? modelElement.getClass().getName()
						: "null"));
	}
}