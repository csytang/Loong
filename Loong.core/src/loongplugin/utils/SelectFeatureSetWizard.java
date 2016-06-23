package loongplugin.utils;

import java.util.List;
import java.util.Set;

import loongplugin.feature.Feature;

import org.eclipse.jface.wizard.Wizard;



public class SelectFeatureSetWizard extends Wizard {
	public SelectFeatureSetPage p;

	private Set<Feature> sf;
	private Set<Feature> nsf;

	public SelectFeatureSetWizard(List<Feature> featureList,
			Set<Feature> initialSelection) {
		p = new SelectFeatureSetPage("", featureList);
		p.setInitialSelection(initialSelection);
	}

	// ColorHelper.sortFeatures(featureModel.getVisibleFeatures())
//	public SelectFeatureWizard(IProject project, Set<IFeature> initialSelection)
//			throws FeatureModelNotFoundException {
//		this(project, initialSelection, FeatureModelManager.getInstance()
//				.getFeatureModel(project));
//	}

	public void addPages() {
		this.addPage(p);
		super.addPages();
	}

	public boolean performFinish() {
		this.sf = p.getSelectedFeatures();
		this.nsf = p.getNotSelectedFeatures();
		return true;
	}

	public Set<Feature> getSelectedFeatures() {
		return sf;
	}

	public Set<Feature> getNotSelectedFeatures() {
		return nsf;
	}

}
