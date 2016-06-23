package loongplugin.uml.action;

import loongplugin.uml.editors.ClassDiagramEditor;
import loongplugin.uml.model.ClassModel;
import loongplugin.uml.model.InterfaceModel;







/**
 * Copy selected entities in the activity diagram.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class CopyAction extends AbstractCopyAction {

	public CopyAction(ClassDiagramEditor editor, PasteAction pasteAction) {
		super(editor, pasteAction);
		registerAllowType(ClassModel.class);
		registerAllowType(InterfaceModel.class);
	}

}
