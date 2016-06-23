package loongplugin.uml.action;


import loongplugin.uml.editors.ClassDiagramEditor;
import loongplugin.uml.model.ClassModel;
import loongplugin.uml.model.InterfaceModel;




/**
 * Paste entities in the activity diagram.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class PasteAction extends AbstractPasteAction {
	
	public PasteAction(ClassDiagramEditor editor) {
		super(editor);
		registerAllowType(ClassModel.class);
		registerAllowType(InterfaceModel.class);
	}

}
