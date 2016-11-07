package loongpluginfmrtool.toolbox.arc;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

public class ARCConfigurationWizard extends Wizard {

	private ARCConfigurationWizardPageDataLoad dataload;
	private ARCConfigurationWizardPageConfig cfg;
	private ApplicationObserver aAO;
	private IProject aProject;
	public ARCConfigurationWizard(IProject pProject,ApplicationObserver pAO,Shell shell,String topicModelFilePath,String docTopicsFilePath,int minaltopics,int totaltopics) {
		super();
		this.aProject = pProject;
		this.aAO = pAO;
		
		dataload =  ARCConfigurationWizardPageDataLoad.getDefault(aProject,shell);
		cfg = ARCConfigurationWizardPageConfig.getDefault(aProject,aAO,shell,topicModelFilePath,docTopicsFilePath,minaltopics,totaltopics);
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
