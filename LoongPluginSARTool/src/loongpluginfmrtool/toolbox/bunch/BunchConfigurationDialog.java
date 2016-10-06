package loongpluginfmrtool.toolbox.bunch;

import java.beans.Beans;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.jface.dialogs.TitleAreaDialog;

import javax.swing.JTextField;

import loongpluginfmrtool.module.builder.ModuleBuilder;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;


public class BunchConfigurationDialog extends Dialog{
	


	private JTextField textField;
	private JTextField textField_1;

	private ModuleBuilder builder;
	private Shell shell;
	private Text text;
	private BunchPreferences preferences = new BunchPreferences();;
	public BunchConfigurationDialog(ModuleBuilder pbuilder,Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
		this.builder = pbuilder;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		Button okbutton = createButton(parent, IDialogConstants.OK_ID, "Run",
				true);
	}
	
	
/*
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(5,false);
		gl_container.marginHeight = 10;
		gl_container.marginWidth = 6;
		container.setLayout(gl_container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblClusteringMethod = new Label(container, SWT.NONE);
		lblClusteringMethod.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClusteringMethod.setText("Clustering Method:");
		
		Combo clusteringmethodcombo = new Combo(container, SWT.NONE);
		clusteringmethodcombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		////////////////// Clustering methods/////////////////////////
		
		String[] methodList = this.preferences.getClusteringMethodFactory().getItemList();
		for(int i = 0;i < methodList.length;i++){
			clusteringmethodcombo.add(methodList[i], i);
		}
		String defaultCM = this.preferences.getClusteringMethodFactory().getDefaultMethod();
		clusteringmethodcombo.select(getIndexofStr(methodList,defaultCM));
		///////////////////////////////////////////////////////////////
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.setText("Options");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText("Use the following options to control Bunch:");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblClusteringAlgorithm = new Label(container, SWT.NONE);
		lblClusteringAlgorithm.setText("Clustering Algorithm:");
		
		///////////////////Clustering algorithm//////////////////////////
		Combo algorithmcombo = new Combo(container, SWT.NONE);
		algorithmcombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		String[] algorithmList = this.preferences.getObjectiveFunctionCalculatorFactory().getItemList();
		for(int i = 0;i < algorithmList.length;i++){
			algorithmcombo.add(algorithmList[i], i);
		}
		String defaultalgorithm = this.preferences.getObjectiveFunctionCalculatorFactory().getDefaultMethod();
		algorithmcombo.select(getIndexofStr(algorithmList,defaultalgorithm));
		/////////////////////////////////////////////
		
		new Label(container, SWT.NONE);
		Button btnLimitRuntimTo = new Button(container, SWT.CHECK);
		btnLimitRuntimTo.setText("Limit Runtim To");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblms = new Label(container, SWT.NONE);
		lblms.setText("(ms)");
		
		Label lblAction = new Label(container, SWT.NONE);
		lblAction.setText("Action:");
		
		Combo combo_2 = new Combo(container, SWT.NONE);
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new Label(container, SWT.NONE);

		
		return container;
	}

	private int getIndexofStr(String[] methodList, String defaultCM) {
		// TODO Auto-generated method stub
		int index = 0;
		for(String str:methodList){
			if(str.equalsIgnoreCase(defaultCM)){
				return index;
			}else{
				index++;
			}
		}
		index = 0;
		return index;
	}
	
	

}
