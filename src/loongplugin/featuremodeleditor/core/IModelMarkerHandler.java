package loongplugin.featuremodeleditor.core;

public interface IModelMarkerHandler {

	/**
	 * Creates a new marker at the model file with the given message. If the
	 * file not exists, the marker will be set to the associated project.
	 * 
	 * @param  message  the message to remark
	 * @param lineNumber 
	 */
	public abstract void createModelMarker(String message, int severity, int lineNumber);

	public abstract void deleteAllModelMarkers();

	public abstract boolean hasModelMarkers();

}