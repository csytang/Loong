package loongplugin.feature;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;

import loongplugin.LoongPlugin;
import loongplugin.color.ColorManager;
import loongplugin.configuration.ExtensionPointManager;
import loongplugin.feature.guidsl.GuidslReader;
import loongplugin.feature.guidsl.UnsupportedModelException;
import loongplugin.featuremodeleditor.IFeatureModelChangeListener;
import loongplugin.featuremodeleditor.event.FeatureModelChangedEvent;
import loongplugin.modelcolor.ModelIDCLRFileReader;
import loongplugin.source.database.ApplicationObserver;

public class FeatureModelManager extends ExtensionPointManager<FeatureModelProviderProxy>{
	private static FeatureModelManager instance;
	private FeatureModel fmodel;
	private GuidslReader gReader;
	private static IProject project;
	private IFile mFile;
	private IFile modelidclrFile;
	private ColorManager cmanager;
	private static IWorkbenchWindow iw;
	private static final long serialVersionUID = 1L;
	private FeatureModelChangeListener featuremodelListener;
	public static List<FeatureModelChangedEvent> resetEvent = new LinkedList<FeatureModelChangedEvent>();
	
	@SuppressWarnings("static-access")
	public FeatureModelManager(IProject project) {
		super(LoongPlugin.PLUGIN_ID,"featureModelProvider");
		if(instance!=null && project==instance.project){
			return;
		}
		
		fmodel = new FeatureModel();
		gReader = new GuidslReader(fmodel);
		this.project = project;
		if(this.project==null){
			return;
		}
		mFile = this.project.getFile("model.m");
		try {
			if(mFile.exists()){
				gReader.parseInputStream(mFile.getContents());
			}else{
				if (!mFile.exists()) {
					mFile.create(new ByteArrayInputStream(
							"Project : [Feature1] [Feature2] :: _Project ;".getBytes()), true,
							null);
				}
				gReader.parseInputStream(mFile.getContents());
			}
		} catch (UnsupportedModelException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fmodel = gReader.getFeatureModel();
		
		//TODO: 判断是否有本地的modelIDClr被导入 如果有则按照那个ID标准和颜色标准来制定
		
		modelidclrFile = this.project.getFile("modelidclr.xml");
		if(modelidclrFile.exists()){
			cmanager = new ColorManager(fmodel);
			ModelIDCLRFileReader modelidreader = new ModelIDCLRFileReader(modelidclrFile,fmodel,cmanager);
			//fmodel = modelidreader.getFeatureModel();
			cmanager = modelidreader.getColorManager();
			
		}else{
			// Assign Id to all features;		
			fmodel.setIdToAllFeatures();	
			
			// Initial color to all features
			cmanager = new ColorManager(fmodel);
			cmanager.featureColorInit();
		}
		
		
		
		instance = this;
		
		featuremodelListener = new FeatureModelChangeListener();
		LoongPlugin.getDefault().addFeatureModelChangeListener(featuremodelListener);
	}
	
	public static FeatureModelManager getInstance(){
		if(instance==null){
			if(ApplicationObserver.getInstance().getInitializedProject()!=null){
				instance = new FeatureModelManager(ApplicationObserver.getInstance().getInitializedProject());
			}else
				instance = new FeatureModelManager(getCurrentProject());
		}
		return instance;
	}
	
	
	public boolean hasbeenReset(FeatureModelChangedEvent event){
		if(FeatureModelManager.resetEvent.contains(event))
			return true;
		else
			return false;
	}
	public void setFeatureModel(FeatureModel pmodel){
		this.fmodel = pmodel;
	}
	
	public static FeatureModelManager getInstance(IProject currentProject)  {
		// TODO Auto-generated method stub
		if(instance==null){
			instance = new FeatureModelManager(currentProject);
		}else if(project!=currentProject){
			instance = new FeatureModelManager(currentProject);
		}else if(instance.getFeatureModel()==null){
			instance = new FeatureModelManager(currentProject);
		}
		return instance;
	}
	
	public ColorManager getColorManager(){
		return cmanager;
	}
	
	public IFile getModelFile(){
		return this.mFile;
	}
	
	public FeatureModel getFeatureModel(){
		return fmodel;
	}
	
	public static RGB getCombinedRGB(Collection<Feature> featureList) {
		RGB rgb = new RGB(255, 255, 255);
		if (featureList.size() > 0) {
			for (Feature feature : featureList) {
				rgb.red += feature.getRGB().red;
				rgb.green += feature.getRGB().green;
				rgb.blue += feature.getRGB().blue;
			}
			rgb.red /= featureList.size() + 1;
			rgb.green /= featureList.size() + 1;
			rgb.blue /= featureList.size() + 1;
		}
		return rgb;
	}
	
	public static IProject getCurrentProject(){    
        ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    

        ISelection selection = selectionService.getSelection();    

        IProject project = null;    
        if(selection instanceof IStructuredSelection) {    
            Object element = ((IStructuredSelection)selection).getFirstElement();    

            if (element instanceof IResource) {    
                project= ((IResource)element).getProject();    
            } else if (element instanceof PackageFragmentRootContainer) {    
                IJavaProject jProject =     
                    ((PackageFragmentRootContainer)element).getJavaProject();    
                project = jProject.getProject();    
            } else if (element instanceof IJavaElement) {    
                IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
                project = jProject.getProject();    
            }    
        }     
        return project;    
    }
	private static WeakHashMap<RGB, Color> colorCache = new WeakHashMap<RGB, Color>();
	
	public static Color getCombinedColor(Collection<Feature> featureList) {
		RGB rgb = getCombinedRGB(featureList);
		Color color = colorCache.get(rgb);
		if (color == null) {
			color = new Color(Display.getDefault(), rgb);
			colorCache.put(rgb, color);
		}
		return color;
	}

	public Collection<? extends Feature> getFeatures() {
		// TODO Auto-generated method stub
		if(fmodel==null){
			
			fmodel = new FeatureModel();
			gReader = new GuidslReader(fmodel);
			if(this.project==null){
				
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						iw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						IEditorReference[] editorReferences = iw.getActivePage().getEditorReferences();
				        IEditorInput input = null;
				        for(IEditorReference editor:editorReferences){
				        	try {
								input = editor.getEditorInput();
							} catch (PartInitException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				        	if(input instanceof IFileEditorInput){
				        		break;
				        	}
				        }
				        IFileEditorInput fileInput = (IFileEditorInput)input;
				        project = fileInput.getFile().getProject();
					}
				});
				
		        
			}
			mFile = this.project.getFile("model.m");
			try {
				gReader.parseInputStream(mFile.getContents());
				
			} catch (UnsupportedModelException | CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Assign Id to all features;		
			fmodel.setIdToAllFeatures();	
			
			// Initial color to all features
			cmanager = new ColorManager(fmodel);
			cmanager.featureColorInit();
			return fmodel.getFeatures();
		}else{
			return fmodel.getFeatures();
		}
	}

	

	@Override
	protected FeatureModelProviderProxy parseExtension(
			IConfigurationElement configurationElement) {
		// TODO Auto-generated method stub
		if (!configurationElement.getName().equals("featureModelProvider"))
			return null;
		return new FeatureModelProviderProxy(configurationElement);
	}

	public FeatureModelProviderProxy getActiveFeatureModelProvider() {
		// TODO Auto-generated method stub
		String featureModelProviderId = "LoongPlugin.feature.guidsl.GuidslFMProvider";
		List<FeatureModelProviderProxy> providers = getProviders();
		for (FeatureModelProviderProxy provider : providers)
			if (provider.getId().equals(featureModelProviderId))
				return provider;
		return null;
	}

	
	public List<FeatureModelProviderProxy> getFeatureModelProviders() {
		return getProviders();
	}
	
	class FeatureModelChangeListener implements IFeatureModelChangeListener{

		@Override
		public void featureModelChanged(FeatureModelChangedEvent event) {
			// TODO Auto-generated method stub
			fmodel = event.getFeatureModel();
			gReader = new GuidslReader(fmodel);
			// Assign Id to all features;		
			fmodel.setIdToAllFeatures();	
			
			// Initial color to all features
			cmanager = new ColorManager(fmodel);
			cmanager.featureColorInit();
			resetEvent.add(event);
		}
		
	}
	public void setColorManager(ColorManager colorManager) {
		// TODO Auto-generated method stub
		cmanager = colorManager;
	}
}
