package loongplugin.feature.guidsl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import loongplugin.feature.FeatureModel;
import loongplugin.featuremodeleditor.IFeatureModelReader;

public abstract class AbstractFeatureModelReader implements IFeatureModelReader{
	/**
	 * the structure to store the parsed data
	 */
	protected FeatureModel featureModel;
	
	/**
	 * warnings occurred while parsing
	 */
	protected LinkedList<ModelWarning> warnings = new LinkedList<ModelWarning>();
	
	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}
	
	public FeatureModel getFeatureModel() {
		return featureModel;
	}
	
	public void readFromFile(IFile file) throws UnsupportedModelException, FileNotFoundException {
		warnings.clear();
		String fileName = file.getRawLocation().toOSString();		
        InputStream inputStream = new FileInputStream(fileName);
        parseInputStream(inputStream);
 	}
	
	@Override
	public void readFromFile(File file) throws UnsupportedModelException, FileNotFoundException {
		warnings.clear();
		String fileName = file.getPath();		
        InputStream inputStream = new FileInputStream(fileName);
        parseInputStream(inputStream);
 	}

	public void readFromString(String text) throws UnsupportedModelException {
		warnings.clear();
        InputStream inputStream = new ByteArrayInputStream(text.getBytes());
        parseInputStream(inputStream);
 	}
	
	public List<ModelWarning> getWarnings() {
		return warnings;
	}

	/**
	 * Reads a feature model from an input stream.
	 * 
	 * @param  inputStream  the textual representation of the feature model
	 * @throws UnsupportedModelException
	 */
	protected abstract void parseInputStream(InputStream inputStream)
			throws UnsupportedModelException;
	
}
