package loongplugin.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import loongplugin.LoongPlugin;


public class LoongProjectNature implements IProjectNature{

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = LoongPlugin.PLUGIN_ID+ ".ProjectNature";
	
	
	private IProject project;
	
	@Override
	public void configure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IProject getProject() {
		// TODO Auto-generated method stub
		return project;
	}

	@Override
	public void setProject(IProject project) {
		// TODO Auto-generated method stub
		this.project = project;
	}

}
