package loongpluginfmrtool.toolbox.wca;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import loongplugin.source.database.ApplicationObserver;
import loongpluginfmrtool.module.builder.ModuleBuilder;
import edu.usc.softarch.arcade.config.Config.StoppingCriterionConfig;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Combo;

public class WCAConfigurationDialog extends Dialog {
	private Text text;
	private Shell shell;
	private ModuleBuilder builder;
	private int cluster;
	private ApplicationObserver aAO;
	private StoppingCriterionConfig stoppingCriterion;
	private Combo combo;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public WCAConfigurationDialog(ModuleBuilder pbuilder,ApplicationObserver pAO,Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
		this.builder = pbuilder;
		this.aAO = pAO;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		GridLayout gridlayout = (GridLayout) container.getLayout();
		gridlayout.numColumns = 6;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lblNewLabel.setText("# Clusters Prefered");
		
		text = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.heightHint = 20;
		text.setLayoutData(gd_text);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblStoppingCriteria = new Label(container, SWT.NONE);
		GridData gd_lblStoppingCriteria = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_lblStoppingCriteria.widthHint = 104;
		lblStoppingCriteria.setLayoutData(gd_lblStoppingCriteria);
		lblStoppingCriteria.setText("Stopping Criteria:");
		new Label(container, SWT.NONE);
		
		combo = new Combo(container, SWT.NONE);
		combo.setItems(new String[] {"Preselected", "Clustergain"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.select(0);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okbutton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		
	}
	
	

	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		
		String textcontent = text.getText();
		int index = combo.getSelectionIndex();
		try{
			this.cluster = Integer.parseInt(textcontent);
			if(index==0){
				stoppingCriterion = StoppingCriterionConfig.preselected;
			}else{
				stoppingCriterion = StoppingCriterionConfig.clustergain;
			}
			WeightedClusteringAlg wca = new WeightedClusteringAlg(aAO,builder,stoppingCriterion,cluster);
		}catch(NumberFormatException e){
			Display.getCurrent().syncExec(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					MessageDialog.openInformation(shell, "Loong Plugin System-FMRTool",
					"Please input an integer for number of cluster configuration.");
				}
		    	
		    });
		}
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(405, 207);
	}

}
