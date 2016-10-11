package loongpluginfmrtool.toolbox.acdc;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ACDCConfigurationDialog extends TitleAreaDialog {
	private Text text;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public ACDCConfigurationDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Please input the configuration argument for ACDC clustering");
		parent.setToolTipText("");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblAcdcArgument = new Label(container, SWT.NONE);
		lblAcdcArgument.setBounds(10, 10, 123, 14);
		lblAcdcArgument.setText("ACDC Argument:");
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(10, 45, 430, 19);

		return area;
	}
	
	

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		
		String argu = text.getText();
		
		ACDC acdc = new ACDC(argu.split(" "));
		super.okPressed();
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

}
