package loongpluginfmrtool.toolbox.arc;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ARCConfigurationWizard extends Wizard {

	private ARCConfigurationWizardPageDataLoad dataload;
	private ARCConfigurationWizardPageConfig cfg;
	private IProject aProject;
	
	public ARCConfigurationWizard(IProject pProject) {
		super();
		this.aProject = pProject;
		dataload =  ARCConfigurationWizardPageDataLoad.getDefault(aProject);
		cfg = ARCConfigurationWizardPageConfig.getDefault();
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
