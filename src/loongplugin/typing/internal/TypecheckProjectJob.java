/**
 * 
 */
package loongplugin.typing.internal;

import java.util.List;

import loongplugin.LoongPlugin;
import loongplugin.typing.internal.manager.TypingExtensionManager;
import loongplugin.typing.model.DebugTyping;
import loongplugin.typing.model.ITypingProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;


/**
 * full typecheck of an entire project
 * 
 * @author ckaestne
 * 
 */
class TypecheckProjectJob extends WorkspaceJob {

	private final IProject project;
	private TypingManager typingManager;

	public TypecheckProjectJob(IProject aproject, TypingManager manager) {
		super("Typechecking Project");
		
		this.project = aproject;
		this.typingManager = manager;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;

		
			
			if (project.isOpen() && LoongPlugin.isLoongProject(project)) {
				monitor.subTask("Checking " + project.getName());
				System.out.println("TypeChecking projects:"+project.getName());
				// delete old markers
				project.deleteMarkers(TypingMarkerFactory.MARKER_TYPE_ID, true,
						IResource.DEPTH_INFINITE);

				DebugTyping.reset();
				long s = System.currentTimeMillis();
				List<ITypingProvider> typingProviders = TypingExtensionManager
						.getInstance().getTypingProviders(project);
				TypingExtensionManager.registerListener(typingProviders,
						typingManager.listener);
				for (ITypingProvider typingProvider : typingProviders) {
					typingProvider.updateAll(monitor);
				}
				System.out.println("Typing SPL " + project + " in "
						+ (System.currentTimeMillis() - s) + " ms");
				DebugTyping.print();// debug only
			}
		
		// monitor.worked(5);
		return Status.OK_STATUS;
	}

}