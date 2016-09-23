package loongpluginfmrtool.module.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import loongplugin.featureconfiguration.Configuration;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LICategories;
import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.views.moduleviews.IModuleModelChangeListener;
import loongpluginfmrtool.views.moduleviews.ModuleModel;
import loongpluginfmrtool.views.moduleviews.ModuleViewPart;
import loongpluginfmrtool.views.moduleviews.ModuleViewPart.ModuleModelChangeListener;
import loongpluginfmrtool.views.moduleviews.moduleModelChangedEvent;

public class ModuleBuilder {
	
	public static ModuleBuilder instance;
	private static IProject targetProject;
	private Map<Integer,Module> indexToModule = new HashMap<Integer,Module>();
	private ApplicationObserver lDB;
	final private ProgramDatabase pd;
	private LFlyweightElementFactory LElementFactory;
	private static Map<LElement,Module> elementToModule = new HashMap<LElement,Module>();
	private List<IModuleModelChangeListener>listeners = new LinkedList<IModuleModelChangeListener>();
	private ModuleModel amodel = new ModuleModel();
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
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ModuleViewPart.ID);
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ModuleViewPart view_instance = ModuleViewPart.getInstance();
		listeners.add(view_instance.getModuleListener());
		
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
	
	public void notifyModuleListener() {
		// TODO Auto-generated method stub
		moduleModelChangedEvent event = new moduleModelChangedEvent(this,amodel);
		for(IModuleModelChangeListener listener:listeners){
			listener.moduleModelChanged(event);
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
				Module module = new Module(element,module_index,this.LElementFactory,this,amodel);
				elementToModule.put(element, module);
				indexToModule.put(module_index, module);
				amodel.addModule(module);
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
	
	protected void computeVariabilityLevel(){
		for(Module module:amodel.getModules()){
			for(ConfigurationOption config:module.getAllConfigurationOptions()){
				config.computeVariability();
			}
		}
		
	}
	
	protected void computeOverallVariabilityLevel(){
		
		
	}
	public void computeStatistic() {
		// TODO Auto-generated method stub
		
		computeVariabilityLevel();
		
		computeOverallVariabilityLevel();
	}
	
	
}
