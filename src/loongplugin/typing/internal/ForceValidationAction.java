package loongplugin.typing.internal;

import java.util.ArrayList;
import java.util.List;

import loongplugin.LoongPlugin;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.editor.CLREditor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;



public class ForceValidationAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate, IEditorActionDelegate {
	final List<IProject> projects = new ArrayList<IProject>();
	private CLREditor activeEditor;

	public void run(IAction action) {
		IProject[] p;
		if (projects.isEmpty())
			p = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		else
			p = projects.toArray(new IProject[projects.size()]);
		
		if (activeEditor != null)
			activeEditor.doSave(null);

		LoongPlugin.getDefault().getTypingManager().recheckProjects(p);
	}
	
	public List<IProject> getProjects() {
		return projects;
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			boolean alreadyCleared = false;
			for (Object selected : ((IStructuredSelection) selection).toArray()) {
				if (selected instanceof IProject) {
					if (!alreadyCleared) {
						activeEditor = null;
						projects.clear();
						alreadyCleared = true;
					}
					projects.add((IProject) selected);
				}
			}
		}
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) { }
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof CLREditor) {
			CLRAnnotatedSourceFile sourceFile = ((CLREditor) targetEditor).getCLRAnnotatedFile();
			if (sourceFile != null) {
				activeEditor = (CLREditor) targetEditor;
				projects.clear();
				projects.add(sourceFile.getProject());
			}
		}
	}
}