package loongpluginfmrtool.module.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LICategories;
import loongpluginfmrtool.module.model.Module;

public class ModuleBuilder {
	
	public static ModuleBuilder instance;
	private static IProject targetProject;
	private Map<Integer,Module> indexToModule = new HashMap<Integer,Module>();
	private ApplicationObserver lDB;
	final private ProgramDatabase pd;
	private LFlyweightElementFactory LElementFactory;
	private static Map<LElement,Module> elementToModule = new HashMap<LElement,Module>();
	public static ModuleBuilder getInstance(IProject selectedProject,ApplicationObserver pDB){
		instance = new ModuleBuilder(selectedProject,pDB);
		return instance;
	}
	public ModuleBuilder(IProject selectedProject,ApplicationObserver pDB){
		ModuleBuilder.targetProject = selectedProject;
		this.lDB = pDB;
		this.LElementFactory = pDB.getLFlyweightElementFactory();
		this.pd = pDB.getProgramDatabase();
	}
	public void init(){
		final IContainer root=ResourcesPlugin.getWorkspace().getRoot();
		WorkspaceJob job=new WorkspaceJob("CreateModule"){
		    @Override 
		    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		    	buildModules(monitor);
		    	return Status.OK_STATUS;
		    }
		 };
		 job.setRule(root);
		 job.setUser(true);
		 job.setPriority(Job.INTERACTIVE);
		 job.schedule();
		 
		
		 try {
			job.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 // Create and check cross module(external variability)
		 WorkspaceJob externalcheckerjob=new WorkspaceJob("CheckVariabilityModule"){
			    @Override 
			    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
			    	variabilityModules(monitor);
			    	return Status.OK_STATUS;
			    }

				
		};
		
		externalcheckerjob.setRule(root);
		externalcheckerjob.setUser(true);
		externalcheckerjob.setPriority(Job.INTERACTIVE);
		externalcheckerjob.schedule();
		try {
			externalcheckerjob.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
	}
	
	public Module getModuleByIndex(int index){
		if(indexToModule.containsKey(index)){
			return indexToModule.get(index);
		}else
			return null;
	}
	public void variabilityModules(IProgressMonitor pProgress){
		if(pProgress != null){ 
			int size = indexToModule.size();
    		pProgress.beginTask("Extract variability", size);
    	}

		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			Module module = entry.getValue();
			module.externalvariability();
			
			pProgress.worked(1);
		}
		pProgress.done();
	}
	
	public void buildModules(IProgressMonitor pProgress){
		if(pProgress != null){ 
    		pProgress.beginTask("Building basic modules", this.pd.getAllElements().size()+3);
    	}
		int module_index = 0;
		for(LElement element:this.pd.getAllElements()){
			pProgress.subTask("Process element:"+element.getCompilationUnitName());
			if(element.getCategory().equals(LICategories.COMPILATION_UNIT)){
				// initialize a module
				Module module = new Module(element,module_index,this.LElementFactory,this);
				elementToModule.put(element, module);
				indexToModule.put(module_index, module);
				// over the index
				module_index++;
			}
			pProgress.worked(1);
		}
		
		pProgress.subTask("Initializing modules");
		for(Map.Entry<Integer, Module>entry:indexToModule.entrySet()){
			Module module = entry.getValue();
			module.initialize();
		}
		pProgress.worked(3);
		pProgress.done();
		
	}
	
	
	
	public Module getModuleByLElement(LElement useelement) {
		// TODO Auto-generated method stub
		if(useelement!=null)
			return elementToModule.get(useelement);
		else
			return null;
	}
}
