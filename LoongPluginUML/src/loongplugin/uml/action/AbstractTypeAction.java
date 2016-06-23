package loongplugin.uml.action;



import loongplugin.uml.editpart.AbstractUMLEntityEditPart;
import loongplugin.uml.editpart.AttributeEditPart;
import loongplugin.uml.editpart.OperationEditPart;
import loongplugin.uml.model.AbstractUMLEntityModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

public class AbstractTypeAction extends AbstractUMLEditorAction {
	
	protected CommandStack stack;
	protected AbstractUMLEntityModel target;
	
	public AbstractTypeAction(String name, CommandStack stack,  GraphicalViewer viewer){
		super(name, viewer);
		this.stack = stack;
	}
	
	/**
	 * If the selection contains attribute, method or type, this action would be enabled.
	 * 
	 * @param sel the selection
	 */
	public void update(IStructuredSelection sel){
		Object obj = sel.getFirstElement();
		if(obj!=null && obj instanceof AbstractUMLEntityEditPart){
			setEnabled(true);
			target = (AbstractUMLEntityModel)((AbstractUMLEntityEditPart)obj).getModel();
		} else if(obj!=null && obj instanceof OperationEditPart){
			setEnabled(true);
			target = (AbstractUMLEntityModel)((OperationEditPart)obj).getParent().getModel();
		} else if(obj!=null && obj instanceof AttributeEditPart){
			setEnabled(true);
			target = (AbstractUMLEntityModel)((AttributeEditPart)obj).getParent().getModel();
		} else {
			setEnabled(false);
			target = null;
		}
	}

}

