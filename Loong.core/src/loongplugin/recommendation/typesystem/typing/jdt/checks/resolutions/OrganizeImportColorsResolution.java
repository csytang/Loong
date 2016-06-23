package loongplugin.recommendation.typesystem.typing.jdt.checks.resolutions;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.BindingProjectColorCache;
import loongplugin.recommendation.typesystem.typing.jdt.model.AbstractTypingMarkerResolution;
import loongplugin.recommendation.typesystem.typing.jdt.organizeimports.OrganizeAllImportsJob;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;



/**
 * sets the colors of all import statements. needs to parse the JDT ast and then
 * assign colors to every bridged import statement
 * 
 * @author ckaestne
 * 
 */
public class OrganizeImportColorsResolution extends
		AbstractTypingMarkerResolution {

	protected final CLRAnnotatedSourceFile source;

	private final BindingProjectColorCache bindingProjectColorCache;

	public OrganizeImportColorsResolution(CLRAnnotatedSourceFile source,
			BindingProjectColorCache bindingProjectColorCache) {
		this.source = source;
		this.bindingProjectColorCache = bindingProjectColorCache;
	}

	public void run(IMarker marker) {

		try {
			OrganizeAllImportsJob.organizeImports(source,
						bindingProjectColorCache);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getLabel() {
		return "Organize Features on Imports";
	}

	public String getDescription() {
		return "Sets the feature annotations on every import statement "
				+ "to the features of the imported type's declaration.";
	}

	public Image getImage() {
		return null;
	}

}
