package loongplugin.uml.action;

import loongplugin.uml.LoongUMLPlugin;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class OpenPropertyViewAction extends AbstractUMLEditorAction {

	public OpenPropertyViewAction(GraphicalViewer viewer){
		super(LoongUMLPlugin.getDefault().getResourceString("menu.openPropertyView"), viewer);
		setImageDescriptor(LoongUMLPlugin.getImageDescriptor("icons/view_properties.gif"));
	}
	
	public void update(IStructuredSelection sel){
	}

	public void run(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			window.getActivePage().showView("org.eclipse.ui.views.PropertySheet");
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
