package loongplugin.events;

import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;

import org.eclipse.core.resources.IProject;

import loongplugin.feature.Feature;
import loongplugin.feature.ProjectionKindManager.ProjectionKind;




public class ColorListChangedEvent extends EventObject{
	
	private final Collection<Change> changes;

	private final IProject project;

	private ProjectionKind projectionKind = null;

	public ColorListChangedEvent(Object source, IProject project,
			Collection<Change> changes) {
		super(source);
		this.changes = changes;
		this.project = project;
	}

	public ColorListChangedEvent(Object source, IProject project, Change change) {
		super(source);
		this.changes = Collections.singleton(change);
		this.project = project;
	}

	public ColorListChangedEvent(Object source, IProject project,Feature feature, ChangeType type) {
		super(source);
		this.changes = Collections.singleton(new Change(feature, type));
		this.project = project;
	}

	/**
	 * projectionkind changes
	 * 
	 * @param source
	 * @param projectionKind
	 */
	public ColorListChangedEvent(Object source, IProject project,
			ProjectionKind projectionKind) {
		super(source);
		this.changes = Collections.emptySet();
		this.project = project;
		this.projectionKind = projectionKind;
	}

	public Collection<Change> getChanges() {
		return changes;
	}

	public IProject getProject() {
		return project;
	}

	public boolean anyChangeOf(ChangeType type) {
		for (Change change : changes)
			if (change.type == type)
				return true;
		return false;
	}

	public ProjectionKind getNewProjectionKind() {
		return projectionKind;
	}

	

}
