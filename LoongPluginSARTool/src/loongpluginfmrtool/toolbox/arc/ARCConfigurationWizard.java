package loongpluginfmrtool.toolbox.arc;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ARCConfigurationWizard extends Wizard {

	private ARCConfigurationWizardPageDataLoad dataload = ARCConfigurationWizardPageDataLoad.getDefault();
	private ARCConfigurationWizardPageConfig cfg = ARCConfigurationWizardPageConfig.getDefault();
	
	
	public ARCConfigurationWizard() {
		super();
		setWindowTitle("Configuration Page");
	}

	@Override
	public void addPages() {
		addPage(dataload);
		addPage(cfg);
	}


	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		// TODO Auto-generated method stub
		if(page==dataload){
			return cfg;
		}
		return null;
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return true;
	}
	
	

}
