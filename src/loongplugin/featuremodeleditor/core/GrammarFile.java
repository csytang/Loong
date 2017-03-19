package loongplugin.featuremodeleditor.core;

import org.eclipse.core.resources.IFile;


/**
 * encapsulates a grammar file. handles markers and such
 * 
 * (could already be done on the CORE level)
 * 
 * @author Christian Kaestner
 */
public class GrammarFile extends ModelMarkerHandler {

	private IFile file;

	public GrammarFile(IFile file) {
		super(file);
		this.file=file;
	}

	public IFile getResource() {
		return file;
	}

}