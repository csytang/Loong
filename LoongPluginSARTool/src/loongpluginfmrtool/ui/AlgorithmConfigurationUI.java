package loongpluginfmrtool.ui;



import loongpluginfmrtool.module.builder.ModuleBuilder;
import loongpluginfmrtool.toolbox.acdc.ACDCConfigurationDialog;
import loongpluginfmrtool.toolbox.bunch.Bunch;
import loongpluginfmrtool.toolbox.limbo.LIMBO;
import loongpluginfmrtool.toolbox.limbo.LIMBOConfigurationDialog;
import loongpluginfmrtool.toolbox.mvs.MVSConfigurationDialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AlgorithmConfigurationUI extends TitleAreaDialog {

	public static AlgorithmConfigurationUI instance;
	
	private Algorithms curr = Algorithms.VMS;
	private ModuleBuilder builder;
	private Shell shell;
	private IProject aProject;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AlgorithmConfigurationUI(Shell parentShell,ModuleBuilder mbuilder,IProject pProject) {
		super(parentShell);
		this.shell = parentShell;
		instance = this;
		builder = mbuilder;
		aProject  = pProject;
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("LoongFMR feature model recovery algorithm\n");
		setMessage("Please select the feature model recovery approach from the list.");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		
		Group group = new Group(container,SWT.SHADOW_ETCHED_IN);
		GridLayout groupgridLayout = new GridLayout();
		groupgridLayout.numColumns = 1;		
		group.setText("Feature Model Recovery Strategies");
		group.setLayout(groupgridLayout);
		
		Button button_acdc = new Button(group, SWT.RADIO);
		button_acdc.setText("Algorithm for Comprehension-Driven Clustering(ACDC)");
		button_acdc.addSelectionListener(new AlgorithmCongSelectionListener(Algorithms.ACDC));
		
		Button button_limbo = new Button(group, SWT.RADIO);
		button_limbo.setText("scaLable InforMation BOttleneck(LIMBO)");
		button_limbo.addSelectionListener(new AlgorithmCongSelectionListener(Algorithms.LIMBO));
		
		Button button_arc = new Button(group, SWT.RADIO);
		button_arc.setText("Architecture Recovery with Concern (ARC)");
		button_arc.addSelectionListener(new AlgorithmCongSelectionListener(Algorithms.ARC));
		
		Button button_burch = new Button(group, SWT.RADIO);
		button_burch.setText("Bunch");
		button_burch.addSelectionListener(new AlgorithmCongSelectionListener(Algorithms.BUNCH));
		
		Button button_vms = new Button(group, SWT.RADIO);
		button_vms.setText("Variability Module System(VMS)");
		button_vms.addSelectionListener(new AlgorithmCongSelectionListener(Algorithms.VMS));
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okbutton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		okbutton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch(curr.name()){
				case "BUNCH":{
					Bunch bunch = new Bunch();
					break;
				}
				case "VMS":{
					MVSConfigurationDialog dialog = new MVSConfigurationDialog(builder,shell);
					dialog.create();
					dialog.open();
					break;
				}
				case "LIMBO":{
					LIMBOConfigurationDialog dialog = new LIMBOConfigurationDialog(builder,shell);
					dialog.create();
					dialog.open();
					break;
				}
				case "ARC":{
					break;
				}
				case "ACDC":{
					ACDCConfigurationDialog dialog = new ACDCConfigurationDialog(shell,aProject);
					dialog.create();
					dialog.open();
					break;
				}
				}
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 336);
	}

	public static AlgorithmConfigurationUI getDefault(Shell parentShell,ModuleBuilder builder,IProject aProject) {
		// TODO Auto-generated method stub
		if(instance == null){
			instance = new AlgorithmConfigurationUI(parentShell,builder,aProject);
		}
		return instance;
	}
	
	public static AlgorithmConfigurationUI getDefault() {
		// TODO Auto-generated method stub
		return instance;
	}

	public Algorithms getSelectedAlgorithm(){
		return curr;
	}

	public void setAlgorithmSelected(Algorithms algorithm) {
		// TODO Auto-generated method stub
		curr = algorithm;
	}
}
