package loongplugin.typing.internal;

import java.util.Collection;
import java.util.List;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingProvider;
import loongplugin.typing.internal.manager.TypingExtensionManager;

import org.eclipse.core.resources.IProject;
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

		List<ITypingProvider> typingProviders = TypingExtensionManager
				.getInstance().getTypingProviders(project);
		TypingExtensionManager.registerListener(typingProviders,
				typingManager.listener);
		for (ITypingProvider typingProvider : typingProviders) {
			typingProvider.updateFile(files, monitor);
		}
		// monitor.worked(5);
		return Status.OK_STATUS;
	}

}
