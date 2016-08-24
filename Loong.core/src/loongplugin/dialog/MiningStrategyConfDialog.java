package loongplugin.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MiningStrategyConfDialog extends TitleAreaDialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public MiningStrategyConfDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.MAX | SWT.RESIZE | SWT.TITLE);
	}
	private static MiningStrategyConfDialog instance;
	private boolean isTopolgySelected = false;
	private boolean isTypeCheckSelected = false;
	private boolean isSubstringSelected = false;
	private boolean isResolveBindSelected = false;
	
	public static MiningStrategyConfDialog getDefault(Shell parentShell){
		if(instance==null){
			instance = new MiningStrategyConfDialog(parentShell);
		}
		return instance;
	}
	public static MiningStrategyConfDialog getDefault(){
		return instance;
	}
	

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("In this configuration dialog, you can select feature mining strategies to support in this feature mining tool.");
		setTitle("Setup feature mining strategy");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		CheckBoxSelectionListener[] selectionListners = new CheckBoxSelectionListener[4];
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		
		
		Label label = new Label(container,SWT.NONE);
		label.setText("Please select feature mining strategy for current feature mining work");
		
		
		Group group = new Group(container,SWT.SHADOW_ETCHED_IN);
		GridLayout groupgridLayout = new GridLayout();
		groupgridLayout.numColumns = 1;		
		group.setText("Feature mining strategies");
		group.setLayout(groupgridLayout);
		
		Button button_topology = new Button(group, SWT.CHECK);
		button_topology.setText("Topology");
		selectionListners[0] = new CheckBoxSelectionListener("Topology");
		button_topology.addSelectionListener(selectionListners[0]);
		
		Button button_resolvebind = new Button(group, SWT.CHECK);
		button_resolvebind.setText("StiCProb");
		selectionListners[1] = new CheckBoxSelectionListener("StiCProb");
		button_resolvebind.addSelectionListener(selectionListners[1]);
		
		Button button_textcomparision = new Button(group, SWT.CHECK);
		button_textcomparision.setText("Substring Comparision");
		selectionListners[2] = new CheckBoxSelectionListener("Substring");
		button_textcomparision.addSelectionListener(selectionListners[2]);
		
		Button button_typecheck = new Button(group, SWT.CHECK);
		button_typecheck.setText("Type checking");
		selectionListners[3] = new CheckBoxSelectionListener("TypeCheck");
		button_typecheck.addSelectionListener(selectionListners[3]);
		
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
		return new Point(450, 300);
	}

	
	public boolean isTopologySelected(){
		return isTopolgySelected;
	}
	public void setTopologySelected(){
		isTopolgySelected = true;
	}
	public void setTopologyUnselected(){
		isTopolgySelected = false;
	}
	
	public boolean isTypeCheckSelected(){
		return isTypeCheckSelected;
	}
	public void setTypeCheckSelected(){
		isTypeCheckSelected = true;
	}
	public void setTypeCheckUnselected(){
		isTypeCheckSelected = false;
	}
	
	public boolean isSubStringSelected(){
		return isSubstringSelected;
	}
	public void setSubStringSelected(){
		isSubstringSelected = true;
	}
	public void setSubStringUnselected(){
		isSubstringSelected = false;
	}
	
	public boolean isResolvebindSelected(){
		return isResolveBindSelected;
	}
	public void setResolvebindSelected(){
		isResolveBindSelected = true;
	}
	public void setResolvebindUnselected(){
		isResolveBindSelected = false;
	}
	
	public boolean strategyHasBeenSelected(){
		if(isResolvebindSelected()||
				isSubStringSelected()||
				isTypeCheckSelected()||
				isTopologySelected()){
			return true;
		}else{
			return false;
		}
	}
}
