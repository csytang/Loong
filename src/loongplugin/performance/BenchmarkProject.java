package loongplugin.performance;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BenchmarkProject extends TitleAreaDialog {
	private Combo comboProjects;
	private String projectName;
	private IProject[]allProjects;
	private Set<String>allprojectNames = new HashSet<String>();
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	private static BenchmarkProject instance;
	public BenchmarkProject(Shell parentShell) {
		super(parentShell);
	}

	public static BenchmarkProject getDefult(Shell parentShell){
		if(instance==null)
			instance = new BenchmarkProject(parentShell);
		return instance;
	}
	
	public BenchmarkProject getDefult(){
		return instance;
	}
	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject project:allProjects){
			allprojectNames.add(project.getName());
		}
		
		setMessage("(*Notice, the benchmark project should be in Loong nature and imported to this workspace already)");
		setTitle("Please input the name of benchmark");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.heightHint = 118;
		container.setLayoutData(gd_container);
		GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		
		
		Label label = new Label(container,SWT.NONE);
		label.setText("Please select benchmark project to reference");
		new Label(container, SWT.NONE);
		
		comboProjects = new Combo(container, SWT.READ_ONLY);
		String[] allprojectnamesarray = allprojectNames.toArray(new String[allprojectNames.size()]); 
		comboProjects.setItems(allprojectnamesarray);
		comboProjects.setText(allprojectnamesarray[0]);
		comboProjects.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				// TODO Auto-generated method stub
				projectName = comboProjects.getText();
			}
		
		});
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 250);
	}
	
	public String getProjectName(){
		return projectName;
	}

}
