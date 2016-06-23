package loongplugin.featuremodeleditor.core;

import loongplugin.LoongPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * The MarkerHandler encapsulates creating and removing markers.
 * 
 * @author Thomas Thuem
 * 
 */
public class ModelMarkerHandler implements IModelMarkerHandler {

	private static final String MODEL_MARKER = LoongPlugin.PLUGIN_ID + ".modelProblemMarker";

	public ModelMarkerHandler(IFile modelFile) {
		this.modelFile = modelFile;
		this.project = modelFile.getProject();
	}

	protected final IFile modelFile;

	protected final IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see featureide.core.internal.IMarkerHandler#createModelMarker(java.lang.String,
	 *      int)
	 */
	public void createModelMarker(String message, int severity, int lineNumber) {
		try {
			IResource resource = modelFile.exists() ? modelFile : project;
			IMarker marker = resource.createMarker(MODEL_MARKER);
			if (marker.exists()) {
				marker.setAttribute(IMarker.MESSAGE, message);
				marker.setAttribute(IMarker.SEVERITY, severity);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see featureide.core.internal.IMarkerHandler#deleteAllModelMarkers()
	 */
	public void deleteAllModelMarkers() {
		try {
			if (project.isAccessible())
				project
						.deleteMarkers(MODEL_MARKER, false,
								IResource.DEPTH_ZERO);
			if (modelFile.exists())
				modelFile.deleteMarkers(MODEL_MARKER, false,
						IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see featureide.core.internal.IMarkerHandler#hasModelMarkers()
	 */
	public boolean hasModelMarkers() {
		return hasModelMarkers(project) || hasModelMarkers(modelFile);
	}

	private boolean hasModelMarkers(IResource resource) {
		try {
			return resource.findMarkers(MODEL_MARKER, false,
					IResource.DEPTH_ZERO).length > 0;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}

}
