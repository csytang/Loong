package loongplugin.views.recommendedfeatureview;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import loongplugin.LoongPlugin;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.featuremodeleditor.IFeatureModelChangeListener;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.SWT;
import org.osgi.framework.Bundle;

public class RecommendedFeatureView extends ViewPart {

	public static final String ID = LoongPlugin.PLUGIN_ID+".recommendedFeatureList";
	private TableViewer fViewer;
	private Table table;
	private RSFeatureModel rsfeatureModel = new RSFeatureModel();
	private List<IJavaElement> allJavaElements;
	public static RecommendedFeatureView instance;
	private String[]columnNames={"properties","value"};
	
	private IProject selectedProject=null;
	private RSFeatureModelChangeListener featuremodelListener;
	
	public static RecommendedFeatureView getInstance(){
		if(instance==null)
			new RecommendedFeatureView();
		return instance;
	}
	public RecommendedFeatureView() {
		// TODO Auto-generated constructor stub
		instance = this;
		selectedProject = getSelectedProject();
		if(selectedProject==null){
			// Obtain the selectedProject from active editor
			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if(activeEditor==null)
				return;
			IEditorInput input = activeEditor.getEditorInput();
			if (input instanceof IFileEditorInput){
				IFileEditorInput editorinput = (IFileEditorInput)input;
				selectedProject = editorinput.getFile().getProject();
			}
		}
		featuremodelListener = new RSFeatureModelChangeListener();
		
	}
	
	public void setIJavaElementsToResolve(List<IJavaElement> pallJavaElements){
		this.allJavaElements = pallJavaElements;
	}

	@Override
	public void createPartControl(Composite parent) {
		createTable(parent);
		createTableViewer();
		
	}
	
	private void createTableViewer() {
		fViewer = new TableViewer(table);
		fViewer.setColumnProperties(columnNames);
		// add drop support to fViewer
		int ops = DND.DROP_COPY|DND.DROP_LINK;
		Transfer[] transfers = new Transfer[]{LocalSelectionTransfer.getTransfer()};
		fViewer.addDropSupport(ops, transfers, new RecommendTreeDropAdapter(fViewer));
		fViewer.setContentProvider(new RecommendedFeatureNameContentProvider());
		fViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new RecommendedFeatureNameLabelProvider(createImageDescriptor())));
		fViewer.setInput(rsfeatureModel);
		
		
	}
	private void createTable(Composite parent) {
		table = new Table(parent, SWT.BORDER | SWT.CHECK | SWT.MULTI);
		table.setHeaderVisible(true);

		TableColumn column;
		column = new TableColumn(table, SWT.LEFT);
		column.setText("FeatureName_Properties");
		column.setWidth(120);
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Value");
		column.setWidth(80);

		table.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				int width = table.getClientArea().width;
				if (width > 200) {
					table.getColumn(0).setWidth(width - 60);
					table.getColumn(1).setWidth(80);
				} else {
					table.getColumn(0).setWidth(width / 2);
					table.getColumn(1).setWidth(width / 2);
				}
			}
		});

	}
	
	class RecommendedFeatureNameContentProvider implements ITreeContentProvider{

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	
	class RecommendedFeatureNameLabelProvider extends LabelProvider implements IStyledLabelProvider{
		
		private ImageDescriptor featureImage;
		private ResourceManager resourceManager;
		
		public RecommendedFeatureNameLabelProvider(ImageDescriptor pfeatureImage){
			this.featureImage = pfeatureImage;
		}
		@Override
		public StyledString getStyledText(Object element) {
			// TODO Auto-generated method stub
			if(element instanceof IJavaElement){
				String name = ((IJavaElement)element).getElementName();
				return new StyledString(name);
			}else if(element instanceof String){
				return new StyledString((String)element);
			}
			return null;
		}
		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			if(element instanceof String){
				return getResourceManager().createImage(featureImage);
			}
			return super.getImage(element);
		}
		
		protected ResourceManager getResourceManager() {
		    if (resourceManager == null) {
		        resourceManager = new LocalResourceManager(JFaceResources.getResources());
		     }
		     return resourceManager;
		}
		
	}
	
	private ImageDescriptor createImageDescriptor() {
		Bundle bundle = Platform.getBundle(LoongPlugin.PLUGIN_ID);
		URL fullPathString = BundleUtility.find(bundle,"icons/feature.jpg");
		return ImageDescriptor.createFromURL(fullPathString);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	private IProject getSelectedProject() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
	    ISelection selection = selectionService.getSelection();    
	    IProject project = null; 
	    if(selection==null)
	    	return null;
		Object element = ((IStructuredSelection)selection).getFirstElement();    

        if (element instanceof IResource) {    
            project= ((IResource)element).getProject();    
        } else if (element instanceof PackageFragmentRootContainer) {    
            IJavaProject jProject =  ((PackageFragmentRootContainer)element).getJavaProject();    
            project = jProject.getProject();    
        } else if (element instanceof IJavaElement) {    
            IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
            project = jProject.getProject();    
        }    
        return project;
	}
	
	class RecommendTreeDropAdapter extends ViewerDropAdapter {
		
		private List<IJavaElement> allJavaElements;
		protected RecommendTreeDropAdapter(TableViewer viewer) {
			super(viewer);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean performDrop(Object data) {
			// TODO Auto-generated method stub
			IStructuredSelection selection = (IStructuredSelection) data;
			allJavaElements = new ArrayList<IJavaElement>();
			for(Iterator<?> ite = selection.iterator(); ite.hasNext();){
				Object element = ite.next();
				if(element instanceof IJavaElement){
					allJavaElements.add((IJavaElement) element);
				}
			}
			//Create a job for this update
			RecommendFeatureNameJob job;
			if(selectedProject!=null)
				job = new RecommendFeatureNameJob(allJavaElements,selectedProject);
			else{
				job = new RecommendFeatureNameJob(allJavaElements,allJavaElements.get(0).getJavaProject().getProject());
			}
			job.setUser(true);
			job.setPriority(Job.LONG);
			job.schedule();
			try {
				job.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			getViewer().refresh();
			return true;
		}
		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			// TODO Auto-generated method stub
			if(LocalSelectionTransfer.getTransfer().isSupportedType(transferType)){
				return true;
			}else
				return false;
		}
		@Override
		public void dropAccept(DropTargetEvent event) {
			// TODO Auto-generated method stub
			event.detail = DND.DROP_COPY;
		}
		@Override
		public void dragOver(DropTargetEvent event) {
			// TODO Auto-generated method stub
			event.detail = DND.DROP_COPY;
		}
		
		
		
	}
	
	private void redraw(){
		if (table.isDisposed()){
			return;
		}
		
		table.setRedraw(false);
		table.removeAll();
		if (selectedProject != null && rsfeatureModel != null) {
			for(RSFeature feature:rsfeatureModel.getFeatures()){
				TableItem item = new TableItem(table, SWT.DEFAULT);
				
			}
		}
		
		table.setRedraw(true);
		
	}

	class RSFeatureModelChangeListener implements IRSFeatureModelChangeListener{

		@Override
		public void featureModelChanged(RSFeatureModelChangedEvent event) {
			// TODO Auto-generated method stub
			rsfeatureModel = event.getFeatureModel();
			
			redraw();
		}
		
	}
}
