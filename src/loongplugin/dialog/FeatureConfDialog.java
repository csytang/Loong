package loongplugin.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import loongplugin.LoongPlugin;
import loongplugin.color.ColorHelper;
import loongplugin.color.ColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.guidsl.GuidslReader;
import loongplugin.featuremodeleditor.IFeatureModelChangeListener;
import loongplugin.featuremodeleditor.event.FeatureModelChangedEvent;


public class FeatureConfDialog extends TitleAreaDialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */

	
	protected static String[] columnNames = new String[] { "id", "name", "depends-on", "color"};
	private TableViewer tablev;
	private FeatureModel fmodel;
	private ColorManager clrmanager;
	private FeatureModelChangeListener featuremodelListener;
	private IProject aproject;
	public FeatureConfDialog(Shell parentShell,IProject project) {
		super(parentShell);
		aproject = project;
		fmodel = FeatureModelManager.getInstance(project).getFeatureModel();
		clrmanager = FeatureModelManager.getInstance(project).getColorManager();
		featuremodelListener = new FeatureModelChangeListener();
		LoongPlugin.getDefault().addFeatureModelChangeListener(featuremodelListener);
	}
	
	
	
	public class FeaturesColorEditor extends DialogCellEditor {

		public FeaturesColorEditor(Table table, int style) {
			super(table, style);
		}

		@Override
		protected Object openDialogBox(Control cellEditorWindow) {
			RGB oldColor = (RGB) this.getValue();

			ColorDialog colorDialog = new ColorDialog(cellEditorWindow
					.getShell());
			colorDialog.setRGB(oldColor);
			colorDialog.setText("Select Color");
			return colorDialog.open();
		}

		protected void updateContents(Object value) {
			if (getDefaultLabel() == null) {
				return;
			}
			String text = ColorHelper.rgb2str((RGB) this.getValue());
			getDefaultLabel().setText(text);
		}
	}
	
	public class FeatureModifier implements ICellModifier{
		
		
		@Override
		public boolean canModify(Object element, String property) {
			// TODO Auto-generated method stub
			return property.equals(columnNames[3]);
		}

		@Override
		public Object getValue(Object element, String property) {
			// TODO Auto-generated method stub
			Feature f = (Feature)element;
			if(property.equals(columnNames[0])){
				return f.getId();
			}else if(property.equals(columnNames[1])){
				return f.getName();
			}else if(property.equals(columnNames[2])){
				if(f.getParent()==null){
					return "-";
				}else
					return f.getParent().getName();
			}else if(property.equals(columnNames[3])){
				try {
					return f.getRGB();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			// TODO Auto-generated method stub
			if (element instanceof Item) {
				element = ((Item) element).getData();
			}
			Feature f = (Feature)element;
			if(property.equals(columnNames[3])){
				RGB c = (RGB)value;
				f.setRGB(c);
			}
			tablev.refresh(element);
		}
		
	}
	
	public class FeatureContentProvider implements IStructuredContentProvider {

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		public Object[] getElements(Object inputElement) {
			return FeatureModelManager.getInstance(aproject).getFeatureModel().getRankedFeature().toArray();
			
		}

	}
	
	//Feature label provider
	public class FeatureLabelProvider extends ColumnLabelProvider{
		
		public void update(final ViewerCell cell){
			Feature f = (Feature)cell.getElement();
			//final TableItem item = (TableItem)cell.getItem();
			switch(cell.getColumnIndex()){
				case 0:{
					cell.setText(f.getId()+"");
					break;
				}
				case 1:{
					cell.setText(f.getName());
					break;
				}
				case 2:{
					if(f.getParent()==null){
						cell.setText("-");
					}else
						cell.setText(f.getParent().getName());
					break;
				}
				case 3:{
					try {
						RGB updatecolor = clrmanager.getRGB(f);
						cell.setBackground(new Color(Display.getCurrent(),updatecolor.red,updatecolor.green,updatecolor.blue));
						cell.setText(updatecolor.toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				}
			}
		}
	}
	
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(null);
		setTitle("Edit feature model for annotated color");
		
		Composite composite = (Composite) super.createDialogArea(parent);
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		
		
	    // Create a table
		Table table = new Table(composite, style);
	    table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
	    table.setLinesVisible(true);

	    // Create columns and show
	 	TableColumn featureid = new TableColumn(table,SWT.LEFT);
	 	featureid.setText("ID");
	 	featureid.setWidth(50);
	 		
	 	TableColumn featurename = new TableColumn(table,SWT.LEFT);
	 	featurename.setText("Feature Name");
	 	featurename.setWidth(200);
	 		
	 	TableColumn featureparent = new TableColumn(table,SWT.LEFT);
	 	featureparent.setText("Parent");
	 	featureparent.setWidth(100);

	 	TableColumn featurecolor = new TableColumn(table,SWT.LEFT);
		featurecolor.setText("Color");
		featurecolor.setWidth(100);
	 	
	    
	    tablev = new TableViewer(table);
		tablev.setColumnProperties(columnNames);
	    
		// Add CellEditor
		CellEditor[] editors = new CellEditor[4];
		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		editors[2] = new TextCellEditor(table);
		editors[3] = new FeaturesColorEditor(table,SWT.READ_ONLY);
		
		tablev.setCellModifier(new FeatureModifier());
	    tablev.setContentProvider(new FeatureContentProvider());
	    
		tablev.setLabelProvider(new FeatureLabelProvider());
		tablev.setInput(fmodel.getRankedFeature());
		
		
		return composite;
	}
	

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(455, 379);
	}
	
	class FeatureModelChangeListener implements IFeatureModelChangeListener{

		@Override
		public void featureModelChanged(FeatureModelChangedEvent event) {
			
			if(!FeatureModelManager.getInstance(aproject).hasbeenReset(event)){
				fmodel = event.getFeatureModel();
				FeatureModelManager.getInstance(aproject).setFeatureModel(fmodel);
				fmodel.setIdToAllFeatures();	
				
				// Initial color to all features
				FeatureModelManager.getInstance(aproject).setColorManager(new ColorManager(fmodel));
				FeatureModelManager.getInstance(aproject).getColorManager().featureColorInit();
				FeatureModelManager.resetEvent.add(event);
			}
			
			fmodel = FeatureModelManager.getInstance(aproject).getFeatureModel();
			clrmanager = FeatureModelManager.getInstance(aproject).getColorManager();
			
		}
		
	}
}
