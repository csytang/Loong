package loongplugin.typing.jdt.checks.resolutions;

import java.text.ParseException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.typing.model.AbstractTypingMarkerResolution;
import loongplugin.typing.jdt.BindingProjectColorCache;
import loongplugin.typing.jdt.organzeimports.OrganizeAllImportsJob;
import loongplugin.typing.model.ITypingMarkerResolution;

public class OrganizeImportColorsResolution extends AbstractTypingMarkerResolution{
	protected final CLRAnnotatedSourceFile source;

	private final BindingProjectColorCache bindingProjectColorCache;

	public OrganizeImportColorsResolution(CLRAnnotatedSourceFile source,
			BindingProjectColorCache bindingProjectColorCache) {
		this.source = source;
		this.bindingProjectColorCache = bindingProjectColorCache;
	}

	public void run(IMarker marker) {
		try {
			OrganizeAllImportsJob.organizeImports(source, bindingProjectColorCache);
		}  catch (CoreException | ParseException e) {
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