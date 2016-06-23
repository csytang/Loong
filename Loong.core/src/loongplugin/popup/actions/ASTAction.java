package loongplugin.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import loongplugin.LoongPlugin;

public class ASTAction implements IObjectActionDelegate{
	
	private Shell shell;
	private IWorkbenchWindow window;// current workbench window
	private IWorkbench workbench = PlatformUI.getWorkbench();
	private IPerspectiveDescriptor perspective;// current perspective
	private IPerspectiveRegistry perspectiveRegistry;// registered perspective 
	public ASTAction() {
		// TODO Auto-generated constructor stub
		super();
		window = workbench.getActiveWorkbenchWindow();
		perspectiveRegistry =workbench.getPerspectiveRegistry();
		perspective = window.getActivePage().getPerspective();
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		// Project information		
		// Open Loong perspective for this project
		if(!perspective.getId().equals("LoongPlugin.perspective")){
			IPerspectiveDescriptor loongPerspectiveDes = perspectiveRegistry.findPerspectiveWithId("LoongPlugin.perspective");
			//window.getActivePage().getOpenPerspectives()
			window.getActivePage().setPerspective(loongPerspectiveDes);
		}
		try {
			window.getActivePage().showView(LoongPlugin.ID_ASTVIEW);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

}
