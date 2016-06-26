package loongplugin.configfeaturemodeleditor.actions;

import loongplugin.configfeaturemodeleditor.ui.ConfigurableFeatureModelEditor;
import loongplugin.configfeaturemodeleditor.ui.ConfigurableFeatureModelEditorInput;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class DiagramAction extends Action implements ISelectionListener,
		IWorkbenchAction {

	private final IWorkbenchWindow window;
	public static final String ID = "gef.step.diagram";
	private IStructuredSelection selection;

	public DiagramAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Diagram");
		setToolTipText("Draw the GEF diagram");
		// setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
		// Activator.PLUGIN_ID, "icons/online.gif"));
		window.getSelectionService().addSelectionListener(this);
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		String path = openFileDialog();
		if (path != null) {
			IEditorInput input = new ConfigurableFeatureModelEditorInput(new Path(path));
			IWorkbenchPage page = window.getActivePage();
			try {
				page.openEditor(input, ConfigurableFeatureModelEditor.ID, true);
			} catch (PartInitException e) {
				// handle error
			}
		}
	}

	private String openFileDialog() {
		FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
		dialog.setText("GEF File");
		dialog.setFilterExtensions(new String[] { ".diagram" });
		return dialog.open();
	}

}
