package loongplugin.configuration;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import loongplugin.featureconfiguration.TreeElement;
import loongplugin.featureconfiguration.Configuration;
import loongplugin.featureconfiguration.SelectableFeature;

/**
 * Converts a given configuration into elements of an tree viewer.
 * 
 * @author Thomas Thuem
 */
public class ConfigurationContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {

	private Configuration configuration;

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		configuration = (Configuration) newInput;
	}

	public void dispose() {
	}
	
	public Object[] getElements(Object parent) {
		if (parent == null)
			return new String[] { "Loading..." };
		if (parent == configuration)
			return new Object[] { configuration.getRoot() };
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeElement)
			return ((TreeElement) child).getParent();
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeElement)
			return ((TreeElement) parent).getChildren();
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeElement)
			return ((TreeElement) parent).hasChildren();
		return false;
	}

}
