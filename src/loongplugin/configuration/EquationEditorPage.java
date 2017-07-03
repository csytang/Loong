

package loongplugin.configuration;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.featureconfiguration.Configuration;
import loongplugin.featureconfiguration.SelectableFeature;
import loongplugin.featureconfiguration.Selection;
import loongplugin.variants.AbstractConfigurationPage;

public class EquationEditorPage extends AbstractConfigurationPage {

	private TreeViewer viewer;
	private final Configuration configuration;
	private FeatureModel model;

	public EquationEditorPage(String pageName,FeatureModel featureModel) {
		super(pageName, featureModel);
		this.model = featureModel;
		configuration = new Configuration(this.model);
		setPageComplete(configuration.valid());
		setTitle("Feature selection");
		this.setDescription("Select features for a variant (double-click on feature to select or deselect)");
	}

	@Override
	protected Control createMainControl(Composite composite) {
		viewer = new TreeViewer(composite);
		viewer.addDoubleClickListener(listener);
		viewer.setContentProvider(new ConfigurationContentProvider());
		try {
			Class<?> c = Class
					.forName("loongplugin.configuration.ConfigurationLabelProvider");
			System.out.println(c);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IBaseLabelProvider lp = new ConfigurationLabelProvider();
		viewer.setLabelProvider(lp);

		viewer.setInput(configuration);
		viewer.expandAll();
		return viewer.getControl();
	}

	private IDoubleClickListener listener = new IDoubleClickListener() {

		public void doubleClick(DoubleClickEvent event) {
			Object object = ((ITreeSelection) event.getSelection())
					.getFirstElement();
			final SelectableFeature feature = (SelectableFeature) object;
			if (feature.getAutomatic() == Selection.UNDEFINED) {
				// set to the next value
				if (feature.getManual() == Selection.UNDEFINED)
					set(feature, Selection.SELECTED);
				else if (feature.getManual() == Selection.SELECTED)
					set(feature, Selection.UNSELECTED);
				else
					// case: unselected
					set(feature, Selection.UNDEFINED);
				configChanged();
				viewer.refresh();
			}
			
		}

		private void set(SelectableFeature feature, Selection selection) {
			configuration.setManual(feature, selection);
		}

	};

	@Override
	public Set<Feature> getNotSelectedFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void configChanged() {
		boolean isValid = configuration.valid();
		setPageComplete(isValid);
		setErrorMessage(isValid ? null : "Invalid selection");
	}

	@Override
	public Set<Feature> getSelectedFeatures() {
		Set<Feature> selection = configuration.getSelectedFeatures();

		Set<Feature> result = new HashSet<Feature>();
		for (Feature s : selection)
			result.add(s);
		return result;
	}

}
