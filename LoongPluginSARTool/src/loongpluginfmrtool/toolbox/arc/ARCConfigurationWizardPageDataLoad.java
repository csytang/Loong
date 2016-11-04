package loongpluginfmrtool.toolbox.arc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ARCConfigurationWizardPageDataLoad extends WizardPage {
	private Text offlineupdatecontenttext;
	private Button offlineupdateButton;
	private Button onlineDownloadButton;
	private ARCConfigurationWizardDataLoadSelectionListener[]listener = new ARCConfigurationWizardDataLoadSelectionListener[2];
	private String projectPath;
	/**
	 * Create the wizard.
	 */
	private static ARCConfigurationWizardPageDataLoad instance;
	private String baseurl = "http://www.chrisyttang.org/loong_fmr/sup/";
	private String stopword = "stopwords.txt";
	private String stopworddie = "stoplists";
	private String[] lang = {"cs.txt","de.txt","en.txt","fi.txt","fr.txt","jp.txt","misc.txt","project.txt","stopwords_total.txt"};
	private IProject aProject;
	private ARCConfigurationWizardPageDataLoad(IProject pProject) {
		super("wizardPage");
		setTitle("Data Load Wizard for Architecture Recovery With Concerns\n");
		setDescription("This configuration will help you download or direct the modules and files used in ARC");
		aProject = pProject;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+aProject.getName().toString();
	}
	
	public static ARCConfigurationWizardPageDataLoad getDefault(IProject pProject){
		if(instance==null){
			instance = new ARCConfigurationWizardPageDataLoad(pProject);
		}
		return instance;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblCreating = new Label(container, SWT.NONE);
		lblCreating.setBounds(10, 10, 457, 14);
		lblCreating.setText("Download supporting stopping dictionaries from webpage or local directory\n");
		
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 30, 256, 69);
		
		Button btnRadioButton = new Button(group, SWT.RADIO);
		btnRadioButton.setBounds(10, 10, 181, 18);
		btnRadioButton.setText("Download From Webpage");
		listener[0] = new ARCConfigurationWizardDataLoadSelectionListener(this,DataLoadMode.online);
		btnRadioButton.addSelectionListener(listener[0]);
		
		Button btnRadioButton_1 = new Button(group, SWT.RADIO);
		btnRadioButton_1.setBounds(10, 36, 212, 18);
		btnRadioButton_1.setText("Use the Files Already Downloaded");
		listener[1] = new ARCConfigurationWizardDataLoadSelectionListener(this,DataLoadMode.offline);
		btnRadioButton_1.addSelectionListener(listener[1]);
		
		offlineupdatecontenttext = new Text(container, SWT.BORDER);
		offlineupdatecontenttext.setEnabled(false);
		offlineupdatecontenttext.setBounds(10, 113, 344, 19);
		
		offlineupdateButton = new Button(container, SWT.NONE);
		offlineupdateButton.setEnabled(false);
		offlineupdateButton.setBounds(409, 109, 189, 28);
		offlineupdateButton.setText("Add A Local Directory");
		
		Label lblstatusLabel = new Label(container, SWT.NONE);
		lblstatusLabel.setEnabled(false);
		lblstatusLabel.setAlignment(SWT.CENTER);
		lblstatusLabel.setBounds(10, 205, 592, 14);
		lblstatusLabel.setText("Status");
		
		onlineDownloadButton = new Button(container, SWT.NONE);
		onlineDownloadButton.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				File stopwordfile = new File(projectPath+File.separatorChar+"sup"+File.separatorChar+stopword);
				URL url;
				try {
					url = new URL(baseurl+stopword);
					FileUtils.copyURLToFile(url, stopwordfile);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(String sublang:lang){
					File sublangfile = new File(projectPath+File.separatorChar+"sup"+File.separatorChar+stopworddie+File.separatorChar+sublang);
					try {
						url = new URL(baseurl+stopworddie+"/"+sublangfile);
						FileUtils.copyURLToFile(url, sublangfile);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		});
		onlineDownloadButton.setEnabled(false);
		onlineDownloadButton.setBounds(249, 155, 127, 28);
		onlineDownloadButton.setText("Download online");
	}
	
	
	public void setOnlineSetting() {
		// TODO Auto-generated method stub
		
		onlineDownloadButton.setEnabled(true);
		offlineupdatecontenttext.setEnabled(false);
		offlineupdatecontenttext.setEditable(false);
		offlineupdateButton.setEnabled(false);
	}

	public void setOfflineSetting() {
		// TODO Auto-generated method stub
		
		onlineDownloadButton.setEnabled(false);
		offlineupdatecontenttext.setEnabled(true);
		offlineupdatecontenttext.setEditable(true);
		offlineupdateButton.setEnabled(true);
	}
}
