package loongplugin.uml.action;

import loongplugin.uml.LoongUMLPlugin;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class OpenOutlineViewAction extends AbstractUMLEditorAction {
	
	public OpenOutlineViewAction(GraphicalViewer viewer){
		super(LoongUMLPlugin.getDefault().getResourceString("menu.openOutlineView"), viewer);
		setImageDescriptor(LoongUMLPlugin.getImageDescriptor("icons/view_outline.gif"));
	}
	
	public void update(IStructuredSelection sel){
	}

	public void run(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			window.getActivePage().showView("org.eclipse.ui.views.ContentOutline");
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

}

