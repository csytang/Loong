package loongplugin.views.recommendedfeatureview;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public class RecommendFeatureNameJob extends WorkspaceJob{

	public RecommendFeatureNameJob(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
