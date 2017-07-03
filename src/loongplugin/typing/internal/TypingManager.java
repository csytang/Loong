package loongplugin.typing.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import loongplugin.LoongPlugin;
import loongplugin.color.IColorChangeListener;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.events.ASTColorChangedEvent;
import loongplugin.events.ChangeType;
import loongplugin.events.ColorListChangedEvent;
import loongplugin.events.FileColorChangedEvent;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.typing.internal.manager.EvaluationStrategyManager;
import loongplugin.typing.internal.manager.TypingExtensionManager;
import loongplugin.typing.model.IEvaluationStrategy;
import loongplugin.typing.model.ITypingCheck;
import loongplugin.typing.model.ITypingCheckListener;
import loongplugin.typing.model.ITypingProvider;
import loongplugin.typing.model.TypeCheckChangeEvent;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;



public class TypingManager {
	public TypingManager() {
		listener = new ListenerMix();
	}

	final ListenerMix listener;

	private final Set<ITypingCheck> knownChecks = new HashSet<ITypingCheck>();

	public void register() {
		LoongPlugin.getDefault().addColorChangeListener(listener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
	}

	public void unregister() {
		if (listener == null)
			return;
		LoongPlugin.getDefault().removeColorChangeListener(listener);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
	}

	class ListenerMix implements IColorChangeListener, ITypingCheckListener,
			IResourceChangeListener {

		public void astColorChanged(ASTColorChangedEvent event) {
			reevaluateFileChecks(Collections.singleton(event
					.getColoredJavaSourceFile()));
		}

		public void colorListChanged(ColorListChangedEvent event) {
			if (event.anyChangeOf(ChangeType.REMOVE)
					|| event.anyChangeOf(ChangeType.DEPENDENCY)) {
				clearEvaluationStrategyCache(event.getProject());
				reevaluateProjectChecks(event.getProject());
			}
		}

		/**
		 * file color changed. reevaluate everything affected by this file TODO:
		 * for now only reevaluate checks in this file
		 */
		public void fileColorChanged(FileColorChangedEvent event) {
			final HashSet<CLRAnnotatedSourceFile> toCheck = new HashSet<CLRAnnotatedSourceFile>();
			for (IContainer folder : event.getAffectedFolders()) {
				try {
					if (folder.exists())
						folder.accept(new IResourceVisitor() {

							public boolean visit(IResource resource)
									throws CoreException {
								if (resource instanceof IFile)
										toCheck.add((CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile((IFile) resource));
									
								return true;
							}
						});
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}

			reevaluateFileChecks(toCheck);
		}

		public void changedTypingChecks(TypeCheckChangeEvent event,
				IProgressMonitor monitor) {
			// called from within a job!
			knownChecks.removeAll(event.getObsoleteChecks());
			removeObsoleteErrors(event.getObsoleteChecks());
			evaluateChecks(event.getAddedChecks(), event.getProvider()
					.getProject(), monitor);
			knownChecks.addAll(event.getAddedChecks());
		}

		/**
		 * file content changed. recalculate necessary checks (but do not
		 * reevaluate existing ones)
		 */
		public void resourceChanged(IResourceChangeEvent event) {

			final List<CLRAnnotatedSourceFile> toCheck = new LinkedList<CLRAnnotatedSourceFile>();
			try {
				if (event != null && event.getDelta() != null)
					event.getDelta().accept(new IResourceDeltaVisitor() {

						public boolean visit(IResourceDelta delta)
								throws CoreException {
							if (delta.getKind() == IResourceDelta.CHANGED
									&& (delta.getFlags() & IResourceDelta.CONTENT) > 0)
								if (delta.getResource().getType() == IResource.FILE){
									IFile deltaFile = (IFile) delta.getResource();
										if(deltaFile!=null){
											if(deltaFile.getFullPath()!=null){
												if(deltaFile.getFullPath().getFileExtension().endsWith(".clr")){
													toCheck.add((CLRAnnotatedSourceFile) CLRAnnotatedSourceFile
																.getColoredJavaSourceFile((IFile) delta
																		.getResource()));
												}
											}
										}
								}
									
							return true;
						}
					});
			} catch (CoreException e) {

				e.printStackTrace();
			}

			recheckFiles(toCheck);
		}

	}

	/**
	 * called from within a job
	 * 
	 * @param monitor
	 */
	public void evaluateChecks(Collection<ITypingCheck> checks,
			IProject project, IProgressMonitor monitor) {
		SubProgressMonitor monitor2 = new SubProgressMonitor(monitor, 1);
		monitor2.beginTask("Evaluating type checks...", checks.size() + 10);

		// called from within a job!
		IEvaluationStrategy strategy = null;
		
		try {
			strategy = EvaluationStrategyManager.getInstance()
						.getEvaluationStrategy(project);
			// cannot check anything without a strategy
			if (strategy == null)
				return;
		} catch (FeatureModelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		monitor2.worked(10);

		int i = 0;
		for (ITypingCheck check : checks) {
			i++;
			if (i % 25 == 0) {
				monitor2.subTask("Evaluating type check " + i + "/"
						+ checks.size());
				monitor2.worked(25);
			}

			boolean isWelltyped = check.evaluate(strategy);

			if (!isWelltyped)
				markIlltyped(check);
			else
				markWelltyped(check);

		}
		
		monitor2.done();
	}

	private void markWelltyped(ITypingCheck check) {
		// remove marker in case one exists (can happen during reevaluation)
		assert check.getFile() != null;
		if (markerIds.containsKey(check)) {
			long markerId = markerIds.remove(check);
			IMarker marker = check.getFile().getResource().getMarker(markerId);
			if (marker.exists())
				try {
					marker.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				}
		}
	}

	private void markIlltyped(ITypingCheck check) {

		try {
			if (markerIds.containsKey(check)) {
				long markerId = markerIds.get(check);
				IMarker marker = check.getFile().getResource().getMarker(
						markerId);
				if (marker.exists()) {
					new TypingMarkerFactory().updateErrorMarker(marker, check);
					return;
				}
			}
			IMarker marker = new TypingMarkerFactory().createErrorMarker(check);
			markerIds.put(check, marker.getId());
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	/**
	 * do not call from within a job
	 */
	protected void reevaluateProjectChecks(final IProject project) {
		final HashSet<ITypingCheck> checks = new HashSet<ITypingCheck>(
				knownChecks);
		WorkspaceJob op = new WorkspaceJob("Reevaluate Typing") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				List<ITypingProvider> typingProviders = TypingExtensionManager
						.getInstance().getTypingProviders(project);
				for (ITypingProvider typingProvider : typingProviders) {
					typingProvider.prepareReevaluationAll(monitor);
				}

				// TODO currently pretty inefficient. should store association
				// of checks to projects or files more directly
				LinkedList<ITypingCheck> toCheck = new LinkedList<ITypingCheck>();
				for (ITypingCheck check : checks) {
					if (check.getFile().getResource().getProject() == project)
						toCheck.add(check);
				}
				evaluateChecks(toCheck, project, monitor);

				return Status.OK_STATUS;
			}
		};
		op.setUser(true);
		op.schedule();
	}

	protected void clearEvaluationStrategyCache(IProject project) {
		IEvaluationStrategy strategy;
		
		FeatureModel featureModel = FeatureModelManager.getInstance()
					.getFeatureModel();
		try {
			strategy = EvaluationStrategyManager.getInstance()
						.getEvaluationStrategy(project);
			strategy.clearCache(featureModel);
		} catch (FeatureModelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * do not call from within a job
	 */
	protected void reevaluateFileChecks(final Set<CLRAnnotatedSourceFile> files) {
		final HashSet<ITypingCheck> checks = new HashSet<ITypingCheck>(
				knownChecks);
		WorkspaceJob op = new WorkspaceJob("Reevaluate Typing") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {

				Map<IProject, Collection<CLRAnnotatedSourceFile>> groupedFiles = groupByProject(files);
				for (Entry<IProject, Collection<CLRAnnotatedSourceFile>> fileGroup : groupedFiles
						.entrySet()) {

					List<ITypingProvider> typingProviders = TypingExtensionManager
							.getInstance().getTypingProviders(
									fileGroup.getKey());
					for (ITypingProvider typingProvider : typingProviders) {
						typingProvider.prepareReevaluation(
								fileGroup.getValue(), monitor);
					}

				}
				// TODO currently pretty inefficient. should store
				// association
				// of checks to projects or files more directly
				for (ITypingCheck check : checks) {
					if (files.contains(check.getFile()))
						evaluateChecks(Collections.singleton(check), check
								.getFile().getResource().getProject(), monitor);
				}
				return Status.OK_STATUS;
			}
		};
		op.setUser(true);
		op.schedule();
	}

	private final HashMap<ITypingCheck, Long> markerIds = new HashMap<ITypingCheck, Long>();

	/**
	 * called from within a job
	 */
	public void removeObsoleteErrors(Collection<ITypingCheck> obsoleteChecks) {
		// called from within a job!
		for (ITypingCheck check : obsoleteChecks)
			markWelltyped(check);

	}

	/**
	 * do not call from within a job
	 */
	public void recheckFiles(List<CLRAnnotatedSourceFile> files) {
		HashMap<IProject, List<CLRAnnotatedSourceFile>> orderedFiles = new HashMap<IProject, List<CLRAnnotatedSourceFile>>();
		for (CLRAnnotatedSourceFile file : files) {
			List<CLRAnnotatedSourceFile> projectFiles = orderedFiles.get(file
					.getResource().getProject());
			if (projectFiles == null) {
				projectFiles = new ArrayList<CLRAnnotatedSourceFile>();
				orderedFiles.put(file.getResource().getProject(), projectFiles);
			}
			projectFiles.add(file);
		}

		for (Entry<IProject, List<CLRAnnotatedSourceFile>> fileSet : orderedFiles
				.entrySet()) {
			WorkspaceJob op = new TypecheckFilesJob(fileSet.getKey(), fileSet
					.getValue(), this);
			op.setUser(true);
			op.schedule();
		}
	}

	/**
	 * do not call from within a job
	 */
	public void recheckProjects(IProject project) {
		WorkspaceJob op = new TypecheckProjectJob(project, this);
		op.setUser(true);
		op.schedule();
	}

	protected static Map<IProject, Collection<CLRAnnotatedSourceFile>> groupByProject(
			Collection<CLRAnnotatedSourceFile> files) {
		Map<IProject, Collection<CLRAnnotatedSourceFile>> result = new HashMap<IProject, Collection<CLRAnnotatedSourceFile>>();
		for (CLRAnnotatedSourceFile file : files) {
			IProject project = file.getProject();
			Collection<CLRAnnotatedSourceFile> projectFiles = result.get(project);
			if (projectFiles == null) {
				projectFiles = new HashSet<CLRAnnotatedSourceFile>();
				result.put(project, projectFiles);
			}
			projectFiles.add(file);
		}
		return result;
	}

	/**
	 * do not call outside this plugin. this method is only used to access
	 * cached checks to marker resolutions
	 */
	public Set<ITypingCheck> getKnownChecks() {
		return knownChecks;
	}

}
