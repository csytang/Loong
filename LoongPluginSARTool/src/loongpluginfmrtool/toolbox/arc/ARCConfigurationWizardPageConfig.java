package loongpluginfmrtool.toolbox.arc;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ARCConfigurationWizardPageConfig extends WizardPage {

	/**
	 * Create the wizard.
	 */
	private static ARCConfigurationWizardPageConfig instance;
	private ARCConfigurationWizardPageConfig() {
		super("wizardPage");
		setTitle("Data Load Wizard for Architecture Recovery With Concerns");
		setDescription("This configuration will help you create configuration file (.cfg) for ARC");
	}
	
	public static ARCConfigurationWizardPageConfig getDefault() {
		// TODO Auto-generated method stub
		if(instance==null)
			instance = new ARCConfigurationWizardPageConfig();
		return instance;
	}
	

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
	}

	
	

}
