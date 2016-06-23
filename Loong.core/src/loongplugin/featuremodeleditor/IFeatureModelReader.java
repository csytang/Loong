package loongplugin.featuremodeleditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import loongplugin.feature.FeatureModel;
import loongplugin.feature.guidsl.ModelWarning;
import loongplugin.feature.guidsl.UnsupportedModelException;

import org.eclipse.core.resources.IFile;



/**
 * Parses a feature model from a given file or string.
 * 
 * @author Thomas Thuem
 */
public interface IFeatureModelReader {
	
	/**
	 * Returns the feature model where the read data is stored.
	 * 
	 * @return the model to fill
	 */
	public FeatureModel getFeatureModel();
	
	/**
	 * Sets the feature model where the read data is stored.
	 * 
	 * @param featureModel the model to fill
	 */
	public void setFeatureModel(FeatureModel featureModel);

	/**
	 * Parses a specific feature model file.
	 * 
	 * @param  file  the feature model file
	 * @throws UnsupportedModelException
	 * @throws FileNotFoundException
	 */
	public void readFromFile(IFile file)
			throws UnsupportedModelException, FileNotFoundException;
	/**
	 * Parses a specific feature model file.
	 * 
	 * @param  file  the feature model file
	 * @throws UnsupportedModelException
	 * @throws FileNotFoundException
	 */
	public void readFromFile(File file)
			throws UnsupportedModelException, FileNotFoundException;

	/**
	 * Parses a textual representation of a feature model.
	 * 
	 * @param text
	 * @throws UnsupportedModelException
	 */
	public void readFromString(String text)
			throws UnsupportedModelException;

	/**
	 * Returns warnings occurred while last parsing.
	 * 
	 * @return
	 */
	public List<ModelWarning> getWarnings();

}
