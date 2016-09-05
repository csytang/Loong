package loongpluginfmrtool.views.moduleviews;

import org.eclipse.core.resources.IProject;

public class ModuleBuilder {
	
	public static ModuleBuilder instance;
	private static IProject targetProject;
	private boolean  isBuilt = false;
	public static ModuleBuilder getInstance(IProject selectedProject){
		if(instance==null){
			instance = new ModuleBuilder(selectedProject);
		}else if(targetProject==null ||
				targetProject!=selectedProject){
			instance = new ModuleBuilder(selectedProject);
		}
		return instance;
	}
	public ModuleBuilder(IProject selectedProject){
		ModuleBuilder.targetProject = selectedProject;
		if(!isBuilt){
			
			
			
			
			
			isBuilt = true;
		}
	}
}
