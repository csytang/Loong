package loongpluginfmrtool.module;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LICategories;

public class ModuleBuilder {
	
	public static ModuleBuilder instance;
	private static IProject targetProject;
	private boolean  isBuilt = false;
	private Map<Integer,Module> indexToModule = new HashMap<Integer,Module>();
	private ApplicationObserver lDB;
	final private ProgramDatabase pd;
	private LFlyweightElementFactory LElementFactory;
	public static ModuleBuilder getInstance(IProject selectedProject,ApplicationObserver pDB){
		instance = new ModuleBuilder(selectedProject,pDB);
		return instance;
	}
	public ModuleBuilder(IProject selectedProject,ApplicationObserver pDB){
		ModuleBuilder.targetProject = selectedProject;
		this.lDB = pDB;
		this.LElementFactory = pDB.getLFlyweightElementFactory();
		this.pd = pDB.getProgramDatabase();
		int index = 0;
		if(!isBuilt){
			WorkspaceJob op = new WorkspaceJob("Building basic modules") {

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) {
					// TODO Auto-generated method stub
					buildModules(monitor);
					return Status.OK_STATUS;
				}
			};
			op.setUser(true);
			op.schedule();
			isBuilt = true;
		}
	}
	
	
	public Module getModuleByIndex(int index){
		if(indexToModule.containsKey(index)){
			return indexToModule.get(index);
		}else
			return null;
	}
	
	
	public void buildModules(IProgressMonitor pProgress){
		if(pProgress != null){ 
    		pProgress.beginTask( "Building basic modules", this.pd.getAllElements().size());
    	}
		int module_index = 0;
		for(LElement element:this.pd.getAllElements()){
			pProgress.setTaskName("Process element:"+element.getCompilationUnitName());
			if(element.getCategory()==LICategories.CLASS){
				// initialize a module
				Module module = new Module(element,module_index,this.LElementFactory);
				module.initialize();
				// over the index
				module_index++;
			}
			pProgress.worked(1);
		}
		pProgress.done();
		
	}
}
