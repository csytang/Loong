package loongplugin.feature.guidsl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import loongplugin.feature.FeatureModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;


/**
 * Default writer to be extended for each feature model format.
 * 
 * @author Thomas Thuem
 */
public abstract class AbstractFeatureModelWriter  implements IFeatureModelWriter {

	/**
	 * the feature model to write out
	 */
	protected FeatureModel featureModel;
	
	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}
	
	public FeatureModel getFeatureModel() {
		return featureModel;
	}
	
	public void writeToFile(IFile file) throws CoreException {
		InputStream source = new ByteArrayInputStream(writeToString().getBytes());
		if (file.exists()) {
			file.setContents(source, false, true, null);
		}
		else {
			file.create(source, false, null);
		}
	}
	
	public void writeToFile(File file) {
		try {
			if (!file.exists()) file.createNewFile();
			FileOutputStream output = new FileOutputStream(file);
			output.write(writeToString().getBytes());
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
