package loongplugin.recommendation.typesystem.typing.jdt.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.FeatureModelNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;


public abstract class AbstractFileBasedTypingProvider extends
		AbstractTypingProvider {

	protected AbstractFileBasedTypingProvider(IProject project) {
		super(project);
	}

	public Set<ITypingCheck> getChecks() {
		return merge(checks.values());
	}

	protected <K> Set<K> merge(Collection<Set<K>> setList) {
		Set<K> result = new HashSet<K>();
		for (Set<K> item : setList)
			result.addAll(item);
		return result;
	}

	final protected Map<IFile, Set<ITypingCheck>> checks = new HashMap<IFile, Set<ITypingCheck>>();

	public void updateAll(IProgressMonitor monitor) {
		monitor.subTask("Searching for files to check");
		final LinkedList<CLRAnnotatedSourceFile> files = new LinkedList<CLRAnnotatedSourceFile>();
		try {
			getProject().accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getType() == IResource.FILE) {

						IFile file = (IFile) resource;
						if(file.getFileExtension()=="clr"){
							CLRAnnotatedSourceFile coloredSourceFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile
									.getColoredJavaSourceFile(file);
							if (matchFileForUpdate(coloredSourceFile))
								files.add(coloredSourceFile);

							return false;
						}else
							return true;
					}
					return true;
				}

			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// detect files that no longer exist but still have checks
		Set<ITypingCheck> obsoleteChecks = new HashSet<ITypingCheck>();
		Iterator<Entry<IFile, Set<ITypingCheck>>> oldFiles = checks.entrySet()
				.iterator();
		while (oldFiles.hasNext()) {
			Entry<IFile, Set<ITypingCheck>> entry = oldFiles.next();
			if (!files.contains(entry.getKey()))
				oldFiles.remove();
			obsoleteChecks.addAll(entry.getValue());
		}
		if (obsoleteChecks.size() > 0)
			fireTypingCheckChanged(Collections.EMPTY_SET, obsoleteChecks,
					monitor);

		updateFileInternal(files, monitor);
	}

	public void updateFile(Collection<CLRAnnotatedSourceFile> files,
			IProgressMonitor monitor) {
		updateFileInternal(files, monitor);
	}

	protected void updateFileInternal(Collection<CLRAnnotatedSourceFile> files,
			IProgressMonitor monitor) {
		Set<ITypingCheck> addedChecks = new HashSet<ITypingCheck>();
		Set<ITypingCheck> obsoleteChecks = new HashSet<ITypingCheck>();

		if (monitor.isCanceled())
			return;

		monitor.beginTask("Type checking...", 2);
		SubProgressMonitor monitor1 = new SubProgressMonitor(monitor, 1);
		monitor1.beginTask("Checking files", files.size());
		for (CLRAnnotatedSourceFile file : files) {
			if (monitor.isCanceled())
				return;
			monitor1.subTask("Checking " + file.getName());
			Set<ITypingCheck> oldChecks = checks.get(file.getResource());
			if (oldChecks == null)
				oldChecks = new HashSet<ITypingCheck>();
			if (matchFileForUpdate(file)) {
				Set<ITypingCheck> newChecks = checkFile(file);
				if (newChecks == null)
					newChecks = new HashSet<ITypingCheck>();

				for (ITypingCheck old : oldChecks)
					if (!newChecks.contains(old))
						obsoleteChecks.add(old);
				for (ITypingCheck newc : newChecks)
					if (!oldChecks.contains(newc))
						addedChecks.add(newc);
				checks.put(file.getResource(), newChecks);
			} else {
				obsoleteChecks.addAll(oldChecks);
				checks.remove(file.getResource());
			}
			monitor1.worked(1);
		}
		monitor1.done();

		fireTypingCheckChanged(addedChecks, obsoleteChecks, monitor);
		monitor.done();
	}

	protected abstract Set<ITypingCheck> checkFile(CLRAnnotatedSourceFile file);

	/**
	 * used in default implementation of updateAll. used to sort out which files
	 * to update. asked for every single file in the workspace
	 * 
	 * @param resource
	 * @return
	 */
	protected boolean matchFileForUpdate(CLRAnnotatedSourceFile resource) {
		return true;
	}

}
