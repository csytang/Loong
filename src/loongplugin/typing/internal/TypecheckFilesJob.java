package loongplugin.typing.internal;

import java.util.Collection;
import java.util.List;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
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



/**
 * type checks one or several files in the same project
 * 
 * @author ckaestne
 * 
 */
public class TypecheckFilesJob extends WorkspaceJob {

	private TypingManager typingManager;
	private final Collection<CLRAnnotatedSourceFile> files;
	private final IProject project;

	public TypecheckFilesJob(IProject project, List<CLRAnnotatedSourceFile> files,
			TypingManager manager) {
		super("Typechecking Project");
		this.project = project;
		this.files = files;
		this.typingManager = manager;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		if (monitor.isCanceled())
			return Status.CANCEL_STATUS;
		/*
		List<ITypingProvider> typingProviders = TypingExtensionManager
				.getInstance().getTypingProviders(project);
		TypingExtensionManager.registerListener(typingProviders,
				typingManager.listener);
		for (ITypingProvider typingProvider : typingProviders) {
			typingProvider.updateFile(files, monitor);
		}
		*/
		monitor.subTask("Checking " + project.getName());

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
		// monitor.worked(5);
		return Status.OK_STATUS;
		
		
	}

}
