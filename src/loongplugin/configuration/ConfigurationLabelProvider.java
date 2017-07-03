package loongplugin.configuration;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import loongplugin.LoongImages;
import loongplugin.featureconfiguration.Configuration;
import loongplugin.featureconfiguration.SelectableFeature;
import loongplugin.featureconfiguration.Selection;


/**
 * Provides labels and images for the configuration tree view.
 * 
 * @author Thomas Thuem
 */
public class ConfigurationLabelProvider extends LabelProvider {

	public final Image IMAGE_UNDEFINED, IMAGE_SELECTED, IMAGE_DESELECTED,
	IMAGE_ASELECTED, IMAGE_ADESELECTED;

	public ConfigurationLabelProvider() {
		IMAGE_UNDEFINED = LoongImages.getImage("undefined.ico");
		IMAGE_SELECTED = LoongImages.getImage("selected.ico");
		IMAGE_DESELECTED = LoongImages.getImage("deselected.ico");
		IMAGE_ASELECTED = LoongImages.getImage("aselected.ico");
		IMAGE_ADESELECTED = LoongImages.getImage("adeselected.ico");
	}
	
	public String getText(Object o) {
		if (o instanceof SelectableFeature) {
			SelectableFeature feature = (SelectableFeature) o;
			if (feature.getParent() == null) {
				Configuration configuration = feature.getConfiguration();
				String s = configuration.valid() ? "valid" : "invalid";
				s += ", ";
				long number = configuration.number();
				if (number < 0)
					s += "more than " + (-1 - number) + " solutions";
				else
					s += number + " solutions";
				return feature.getName() + " (" + s + ")";
			}
			return feature.getName();
		}
		return o.toString();
	}

	public Image getImage(Object o) {
		if (!(o instanceof SelectableFeature))
			return null;
		SelectableFeature feature = (SelectableFeature) o;
		if (feature.getAutomatic() != Selection.UNDEFINED)
			return feature.getAutomatic() == Selection.SELECTED ? IMAGE_ASELECTED
					: IMAGE_ADESELECTED;
		if (feature.getManual() == Selection.UNDEFINED)
			return IMAGE_UNDEFINED;
		return feature.getManual() == Selection.SELECTED ? IMAGE_SELECTED
				: IMAGE_DESELECTED;
	}

	@Override
	public void dispose() {
		IMAGE_UNDEFINED.dispose();
		IMAGE_SELECTED.dispose();
		IMAGE_DESELECTED.dispose();
		IMAGE_ASELECTED.dispose();
		IMAGE_ADESELECTED.dispose();
		super.dispose();
	}

}