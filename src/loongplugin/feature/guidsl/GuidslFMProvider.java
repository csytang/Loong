package loongplugin.feature.guidsl;

import org.eclipse.core.resources.IProject;

import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.feature.FeatureModelProviderProxy;
import loongplugin.feature.IFeatureModelProvider;

public class GuidslFMProvider implements IFeatureModelProvider {

	public GuidslFMProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public FeatureModel getFeatureModel(IProject project)
			throws FeatureModelNotFoundException {
		// TODO Auto-generated method stub
		return FeatureModelManager.getInstance(project).getFeatureModel();
	}

}
