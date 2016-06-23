package loongplugin.uml;




import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class LoongUMLPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {


	private BooleanFieldEditor antiAlias;

	private BooleanFieldEditor showGrid;

	private SpinnerFieldEditor gridSize;

	private BooleanFieldEditor snapToGeometry;

	private BooleanFieldEditor newThema;

	private BooleanFieldEditor showSimpleNameInClassDiagram;

	/**
	 * Show parameter name or not, methods will be shorter if parameter name isn't shown but only parameter type
	 */
	private BooleanFieldEditor showParameterName;
	
	
	
	

	public LoongUMLPreferencePage() {
		super("LoongPlugin—UMLPage Preference");
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// for Diagram Layout (Grid)
		Group layoutGroup = new Group(composite, SWT.NULL);
		layoutGroup.setText(LoongUMLPlugin.getDefault().getResourceString("preference.layout"));
		layoutGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showGrid = new BooleanFieldEditor(LoongUMLPlugin.PREF_SHOW_GRID, LoongUMLPlugin.getDefault().getResourceString("preference.layout.showGrid"),
				layoutGroup);
		gridSize = new SpinnerFieldEditor(LoongUMLPlugin.PREF_GRID_SIZE, LoongUMLPlugin.getDefault().getResourceString("preference.layout.gridSize"), 1, 100,
				layoutGroup);
		snapToGeometry = new BooleanFieldEditor(LoongUMLPlugin.PREF_SNAP_GEOMETRY, LoongUMLPlugin.getDefault().getResourceString(
				"preference.layout.snapToGeometry"), layoutGroup);
		layoutGroup.setLayout(new GridLayout(3, false));

		// for Class Diagram
		Group classGroup = new Group(composite, SWT.NULL);
		classGroup.setText(LoongUMLPlugin.getDefault().getResourceString("preference.classdiagram"));
		classGroup.setLayout(new GridLayout(1, false));
		classGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showSimpleNameInClassDiagram = new BooleanFieldEditor(LoongUMLPlugin.PREF_CLASS_DIAGRAM_SHOW_SIMPLE_NAME, LoongUMLPlugin.getDefault().getResourceString(
				"preference.classdiagram.simpleName"), classGroup);
		showParameterName = new BooleanFieldEditor(LoongUMLPlugin.PREF_CLASS_DIAGRAM_SHOW_PARAMETER_NAME, LoongUMLPlugin.getDefault().getResourceString(
				"preference.classdiagram.showParameterName"), classGroup);
		

		// Graphics style.
		Group appearanceGoup = new Group(composite, SWT.NULL);
		appearanceGoup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		appearanceGoup.setText(LoongUMLPlugin.getDefault().getResourceString("preference.appearance"));

		antiAlias = new BooleanFieldEditor(LoongUMLPlugin.PREF_ANTI_ALIAS, LoongUMLPlugin.getDefault().getResourceString("preference.antialias"),
				appearanceGoup);

		newThema = new BooleanFieldEditor(LoongUMLPlugin.PREF_NEWSTYLE, LoongUMLPlugin.getDefault().getResourceString("preference.appearance.new"),
				appearanceGoup);

		// Initializes values
		fillInitialValues();

		return composite;
	}

	private void fillInitialValues() {
		IPreferenceStore store = LoongUMLPlugin.getDefault().getPreferenceStore();

		antiAlias.setPreferenceStore(store);
		antiAlias.load();

		showGrid.setPreferenceStore(store);
		showGrid.load();

		gridSize.setPreferenceStore(store);
		gridSize.load();

		snapToGeometry.setPreferenceStore(store);
		snapToGeometry.load();

		newThema.setPreferenceStore(store);
		newThema.load();
		showSimpleNameInClassDiagram.setPreferenceStore(store);
		showSimpleNameInClassDiagram.load();
		showParameterName.setPreferenceStore(store);
		showParameterName.load();
		
	}

	public boolean performOk() {
		
		antiAlias.store();
		showGrid.store();
		gridSize.store();
		snapToGeometry.store();
		newThema.store();
		showSimpleNameInClassDiagram.store();
		showParameterName.store();
		
		return true;
	}

	public void init(IWorkbench workbench) {
	}

}
