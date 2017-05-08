package loongplugin.views.featureview;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;
import loongplugin.LoongImages;
import loongplugin.LoongPlugin;
import loongplugin.color.ColorHelper;
import loongplugin.color.ColorManager;
import loongplugin.color.IColorChangeListener;
import loongplugin.events.ASTColorChangedEvent;
import loongplugin.events.ColorListChangedEvent;
import loongplugin.events.FileColorChangedEvent;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.featuremodeleditor.IFeatureModelChangeListener;
import loongplugin.featuremodeleditor.event.FeatureModelChangedEvent;
import loongplugin.utils.EditorUtility;


public class FeatureView extends ViewPart{
	
	private IProject project;
	private FeatureModel fmodel;
	private Table table;
	private ActiveProjectListner activeProjectListner;
	private ColorChangeListener colorChangeListner;
	private Action selectColorAction;
	private Label validPanel;
	private Action renameAction;
	private MenuManager featureContextMenuMgr;
	private static FeatureView instance;
	private Action fRefreshAction;
	private FeatureModelChangeListener featuremodelListener;
	
	public static FeatureView getInstance(){
		if(instance==null){
			instance = new FeatureView();
		}
		return instance;
		
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager toolBar = bars.getToolBarManager();
		toolBar.add(fRefreshAction);
	}
	
	
	
	private class ColorChangeListener implements IColorChangeListener{ 

		public void fileColorChanged(FileColorChangedEvent event) {
			// TODO Auto-generated method stub
			
		}

		public void colorListChanged(ColorListChangedEvent event) {
			// TODO Auto-generated method stub
			if (event.getProject() == project)
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							redraw();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		}

		@Override
		public void astColorChanged(ASTColorChangedEvent event) {
			// TODO Auto-generated method stub
			
		}

	}
	
	private void hookContextMenu() {
		featureContextMenuMgr = new MenuManager("#PopupMenu");
		featureContextMenuMgr.setRemoveAllWhenShown(true);
		featureContextMenuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				FeatureView.this.fillContextMenu(manager);
			}
		});

		Menu menu = featureContextMenuMgr.createContextMenu(table);
		table.setMenu(menu);
		// getSite().registerContextMenu(menuMgr, table.get);
	}
	
	
	private void fillContextMenu(IMenuManager manager) {
		boolean enabled = getSelectedFeature() != null;
		renameAction.setEnabled(enabled);
		selectColorAction.setEnabled(enabled);
		manager.add(renameAction);
		manager.add(selectColorAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	@Override
	public void init(IViewSite site) throws PartInitException {
		// TODO Auto-generated method stub
		super.init(site);
		activeProjectListner = new ActiveProjectListner();
		site.getPage().addPartListener(activeProjectListner);
		featuremodelListener = new FeatureModelChangeListener();
		LoongPlugin.getDefault().addFeatureModelChangeListener(featuremodelListener);
		//colorChangeListner = new ColorChangeListener();
		//LoongPlugin.getDefault().addColorChangeListener(colorChangeListner);

	}

	private class ActiveProjectListner implements IPartListener {

		public void partActivated(IWorkbenchPart part) {
			update();
		}

		private void update() {
			IProject ap = getActiveProject();
			try {
				setProject(ap);
			} catch (FeatureModelNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
			update();
		}

		public void partClosed(IWorkbenchPart part) {
			update();
		}

		public void partDeactivated(IWorkbenchPart part) {
			update();
		}

		public void partOpened(IWorkbenchPart part) {
			update();
		}

	}
	
	public FeatureView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
			createTable(parent);
			createValidPanel(parent);
			createLayout(parent);
			makeActions();
			hookContextMenu();
			contributeToActionBars();
			//hookDoubleClickAction();
		
	}

	

	private void createValidPanel(Composite parent) {
		validPanel = new Label(parent, SWT.NONE);
		validPanel.setText("");
	}
	
	@Override
	public void setFocus() {
		table.setFocus();
	}
	
	private void createLayout(Composite parent) {
		FormData formData = new FormData(-1, 16);
		formData.height = 16;
		formData.right = new FormAttachment(100, 0);
		formData.left = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		FormData tableLayoutData = new FormData(-1, -1);
		tableLayoutData.top = new FormAttachment(0, 0);
		tableLayoutData.right = new FormAttachment(100, 0);
		tableLayoutData.left = new FormAttachment(0, 0);
		tableLayoutData.bottom = new FormAttachment(validPanel);
		
		table.setLayoutData(tableLayoutData);
		validPanel.setLayoutData(formData);
		parent.setLayout(new FormLayout());
		parent.pack();
		
	}
	
	private IProject getActiveProject() {
		IEditorPart part = EditorUtility.getActiveEditor();
		if (part != null) {
			IEditorInput input = part.getEditorInput();
			if (input instanceof IFileEditorInput)
				return ((IFileEditorInput) input).getFile().getProject();
		}
		return null;
	}
	
	private void setProject(IProject newProject) throws FeatureModelNotFoundException {
		if (project == newProject)
			return;
		project = newProject;
		if (project != null)
			fmodel = FeatureModelManager.getInstance().getFeatureModel();
		else
			fmodel = null;
		if(fmodel==null)
			return;
		else{
			
		}
		try {
			redraw();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void createTable(Composite parent) {
		table = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CHECK);
		table.setHeaderVisible(true);

		TableColumn column;
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Name");
		column.setWidth(120);
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Color");
		column.setWidth(60);

		table.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				int width = table.getClientArea().width;
				if (width > 120) {
					table.getColumn(0).setWidth(width - 60);
					table.getColumn(1).setWidth(60);
				} else {
					table.getColumn(0).setWidth(width / 2);
					table.getColumn(1).setWidth(width / 2);
				}
			}
		});

		try {
			setProject(getActiveProject());
		} catch (FeatureModelNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		table.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			
			}

			public void widgetSelected(SelectionEvent e) {
				if (project != null && e.detail == SWT.CHECK) {
					TableItem item = (TableItem) e.item;
					Feature feature = (Feature) item.getData();
					if(item.getChecked()){
						fmodel.setFeatureToVisiable(feature);
					}else{
						fmodel.setFeatureToNonVisiable(feature);
					}
				}
			}
		});

	}

	private IProject getSelectedProject() {
		
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
	    ISelection selection = selectionService.getSelection();    
	    IProject project = null; 
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
	
	
	private void redraw() throws Exception {
		if(table==null)
			return;
		if (table.isDisposed()){
			return;
		}
		try {
			table.setRedraw(false);
			
			Feature oldSelection = getSelectedFeature();

			for (TableItem item : table.getItems()) {
				if (item.getBackground(1) != null)
					item.getBackground(1).dispose();
			}
			table.removeAll();
			if (project != null && fmodel != null) {
				for (Feature feature : fmodel.getFeatures()){
					TableItem item = new TableItem(table, SWT.DEFAULT);
					item.setText(0, feature.getName());
					item.setText(1, ColorHelper.rgb2str(feature.getRGB()));
					item.setBackground(1, new Color(Display.getCurrent(),feature.getRGB()));
					item.setData(feature);
				}
				setSelectedFeature(oldSelection);
			}
		} finally {
			table.setRedraw(true);
		}
	}
	
	private void setSelectedFeature(Feature feature) {
		if (feature != null)
			for (TableItem item : table.getItems()) {
				if (item.getData() == feature) {
					table.setSelection(item);
					break;
				}
			}
		table.setSelection(-1);
	}
	
	protected Feature getSelectedFeature() {
		int idx = table.getSelectionIndex();
		if (idx == -1)
			return null;
		return (Feature) table.getItem(idx).getData();
	}
	
	private void makeActions() {

		renameAction = new Action("Rename...") {
			public void run() {
				Feature feature = getSelectedFeature();
				if (feature == null)
					return;

				String oldName = feature.getName();

				InputDialog input = new InputDialog(getSite().getShell(),"Rename Feature", "New name:", oldName,
						new IInputValidator() {
							public String isValid(String newText) {
								if (newText.length() == 0)
									return "Name must not be empty";
								return null;
							}
						});
				if (input.open() == InputDialog.OK)
					try {
						feature.setName(input.getValue());
						redraw();
					} catch (UnsupportedOperationException e) {
						// nothing yet. TODO prevent renaming features that
						// cannot
						// be renamed
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		};
		renameAction.setToolTipText("Rename IFeature");
		renameAction.setAccelerator(SWT.F2);

		selectColorAction = new Action("Select color...") {
			@Override
			public void run() {
				Feature feature = getSelectedFeature();
				if (feature == null)
					return;

				RGB oldColor;
				try {
					oldColor = feature.getRGB();
					ColorDialog colorDialog = new ColorDialog(getSite().getShell());
					colorDialog.setRGB(oldColor);
					colorDialog.setText("Color for Feature \"" + feature.getName()+ "\"");
					RGB newColor = colorDialog.open();
					if (newColor != null && newColor != oldColor)
						try {
							feature.setRGB(newColor);
							redraw();
						} catch (UnsupportedOperationException e) {
							// nothing yet. TODO prevent renaming features that
							// cannot be renamed
						}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		
		
		fRefreshAction = new Action() {
			public void run() {
				try {
					redraw();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		fRefreshAction.setText("&Refresh AST"); //$NON-NLS-1$
		fRefreshAction.setToolTipText("Refresh AST"); //$NON-NLS-1$
		fRefreshAction.setEnabled(true);
		LoongImages.setImageDescriptors(fRefreshAction, LoongImages.REFRESH);
		
	}
	

	@Override
	public void dispose() {
		try {
			setProject(null);
		} catch (FeatureModelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (activeProjectListner != null)
			getSite().getPage().removePartListener(activeProjectListner);
		if (colorChangeListner != null)
			LoongPlugin.getDefault().removeColorChangeListener(colorChangeListner);
		if(featuremodelListener!=null){
			LoongPlugin.getDefault().removeFeatureModelChangeListener(featuremodelListener);
		}
	}


	class FeatureModelChangeListener implements IFeatureModelChangeListener{

		@Override
		public void featureModelChanged(FeatureModelChangedEvent event) {
			if(!FeatureModelManager.getInstance().hasbeenReset(event)){
				fmodel = event.getFeatureModel();
				FeatureModelManager.getInstance().setFeatureModel(fmodel);
				fmodel.setIdToAllFeatures();	
				
				// Initial color to all features
				FeatureModelManager.getInstance().setColorManager(new ColorManager(fmodel));
				FeatureModelManager.getInstance().getColorManager().featureColorInit();
				FeatureModelManager.resetEvent.add(event);
			}
			
			fmodel = FeatureModelManager.getInstance().getFeatureModel();
			try {
				redraw();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
