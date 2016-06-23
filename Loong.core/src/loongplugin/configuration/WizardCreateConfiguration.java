package loongplugin.configuration;

import loongplugin.feature.FeatureModel;
//import loongplugin.featureconfiguration.CreateConfigurationJob;
import loongplugin.variants.AbstractConfigurationPage;
import loongplugin.variants.WizardPageCreateProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;




public class WizardCreateConfiguration extends Wizard {

	private IProject sourceProject;

	private FeatureModel featureModel;

	public WizardCreateConfiguration(IProject sourceProject, FeatureModel fm) {
		super();
		this.sourceProject = sourceProject;
		this.featureModel = fm;
	}
	
	private AbstractConfigurationPage selectFeaturesPage;

	private WizardPageCreateProject createProjectPage;
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		
		CreateConfigurationJob job = new CreateConfigurationJob(sourceProject,
				selectFeaturesPage.getSelectedFeatures(),
				createProjectPage.projectName.getText());
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();

		return true;
	}

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		selectFeaturesPage = featureModel.getConfigurationPage("SelectFeatures");
		selectFeaturesPage.setTitle("Select Features");
		addPage(selectFeaturesPage);
		createProjectPage = new WizardPageCreateProject("CreateProjects",
				sourceProject);
		addPage(createProjectPage);
	}

	
	
	
}
