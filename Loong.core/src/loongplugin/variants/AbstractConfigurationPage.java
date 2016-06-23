package loongplugin.variants;

import java.util.Set;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;


/**
 * page that allows you to select a set of features, which is validated against
 * the feature model. only valid feature selections that can be used for the
 * configuration/derivation/generation process may be selected
 * 
 * user has to provide a feature model and receives a feature set. the concrete
 * representation (e.g., how features are shown, how errors are shown) is up to
 * a subclass that implements this page for a concrete type of feature models
 * 
 * @see SelectFeatureSetPage for a related page without validation
 * 
 * @author ckaestne
 * 
 */
public abstract class AbstractConfigurationPage extends WizardPage {

	protected final FeatureModel featureModel;

	protected Set<Feature> initialSelection;

	public AbstractConfigurationPage(String pageName, FeatureModel featureModel) {
		super(pageName);
		this.featureModel = featureModel;
		this.initialSelection = null;
		this.setTitle(getTitle());
	}

	/**
	 * simple template of setting up the dialog with only a single central
	 * control. can be overridden to replace
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		layout.marginHeight = layout.marginWidth = 5;
		composite.setLayout(layout);
		Label label = new Label(composite, SWT.NONE);
		label.setText("Select Features:");
		Control mainControl = createMainControl(composite);
		FormData formData = new FormData();
		formData.top = new FormAttachment(label, 5);
		formData.bottom = new FormAttachment(100, 0);
		formData.right = new FormAttachment(100, 0);
		formData.left = new FormAttachment(0, 0);
		mainControl.setLayoutData(formData);

		setControl(composite);
	}

	protected abstract Control createMainControl(Composite composite);

	public abstract Set<Feature> getSelectedFeatures();

	/**
	 * necessary to distinguish from grayed features
	 * 
	 * @return
	 */
	public abstract Set<Feature> getNotSelectedFeatures();

	public void setInitialSelection(Set<Feature> features) {
		initialSelection = features;
	}
}
