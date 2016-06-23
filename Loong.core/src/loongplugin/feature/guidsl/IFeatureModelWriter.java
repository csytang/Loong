package loongplugin.feature.guidsl;

import java.io.File;

import loongplugin.feature.FeatureModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;


/**
 * Writes a feature model to a file or string.
 * 
 * @author Thomas Thuem
 */
public interface IFeatureModelWriter {

	/**
	 * Returns the feature model to write out.
	 * 
	 * @return the model to write
	 */
	public FeatureModel getFeatureModel();
	
	/**
	 * Sets the feature model to be saved in a textual representation.
	 * 
	 * @param featureModel the model to write
	 */
	public void setFeatureModel(FeatureModel featureModel);

	/**
	 * Saves a feature model to a file.
	 * 
	 * @param file
	 * @throws CoreException
	 */
	public abstract void writeToFile(IFile file) throws CoreException;
	
	/**
	 * Saves a feature model to a file.
	 * 
	 * @param file
	 * @throws CoreException
	 */
	public abstract void writeToFile(File file);
	
	/**
	 * Converts a feature model to a textual representation.
	 * 
	 * @return
	 */
	public abstract String writeToString();

}
