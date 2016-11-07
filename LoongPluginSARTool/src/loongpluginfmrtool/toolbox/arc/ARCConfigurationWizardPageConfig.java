package loongpluginfmrtool.toolbox.arc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

public class ARCConfigurationWizardPageConfig extends WizardPage {
	private static ARCConfigurationWizardPageConfig instance;
	private Text projecttextContent;
	private Text odemtextContent;
	private Text grandtruthtextContent;
	private Text deptrsftextContent;
	private Text txtArc;
	private Combo granulecombo;
	private Text text_7;
	private Text numtopicrangeContentStart;
	private Text topictextContent;
	private Text selectePkgTextContent;
	private Text docTopicFileTextContent;
	private Text txtJava_1;
	private Combo simmeasurecombo;
	private Text smellclstextContent;
	private Combo stoppingcriteriacombo;
	private IProject aProject;
	private Shell shell;
	private String projectPath = "";
	private Button btnExtract;
	private Text numtopicrangeContentEnd;
	private Text numtopicStep;
	private Text text_1;
	private Text text_2;
	private ApplicationObserver aAO;
	private ProgramDatabase aPD;
	private String atopicModelFilePath ="";
	private String adocTopicsFilePath ="";
	private int aminaltopics =0;
	private int atotaltopics =0;
	private String workspacePath;
	
	private ARCConfigurationWizardPageConfig(IProject pProject,ApplicationObserver pAO,Shell pShell,String topicModelFilePath,String docTopicsFilePath,int minaltopics,int totaltopics) {
		super("wizardPage");
		this.aProject = pProject;
		this.shell = pShell;
		this.aAO = pAO;
		this.aPD = this.aAO.getProgramDatabase();
		this.atopicModelFilePath = topicModelFilePath;
		this.adocTopicsFilePath = docTopicsFilePath;
		this.aminaltopics = minaltopics;
		this.atotaltopics = totaltopics;
		workspacePath =ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		projectPath = workspacePath+File.separatorChar+aProject.getName().toString();
		setTitle("Data Load Wizard for Architecture Recovery With Concerns");
		setDescription("This configuration will help you create configuration file (.cfg) for ARC");
	}
	
	public static ARCConfigurationWizardPageConfig getDefault(IProject pProject,ApplicationObserver pAO,Shell pShell,String topicModelFilePath,String docTopicsFilePath,int minaltopics,int totaltopics) {
		// TODO Auto-generated method stub
		if(instance==null)
			instance = new ARCConfigurationWizardPageConfig(pProject,pAO,pShell,topicModelFilePath,docTopicsFilePath,minaltopics,totaltopics);
		return instance;
	}
	

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);

		setControl(container);
		container.setLayout(new GridLayout(4, false));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("project_name");
		
		projecttextContent = new Text(container, SWT.BORDER);
		projecttextContent.setEditable(false);
		projecttextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projecttextContent.setText(this.aProject.getName());
		
		Label lblNewLabel_3 = new Label(container, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("lang");
		
		txtJava_1 = new Text(container, SWT.BORDER);
		txtJava_1.setEditable(false);
		txtJava_1.setText("java");
		txtJava_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
	
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("odem_file");
		
		odemtextContent = new Text(container, SWT.BORDER);
		odemtextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnExtract = new Button(container, SWT.NONE);
		GridData gd_btnExtract = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExtract.widthHint = 121;
		btnExtract.setLayoutData(gd_btnExtract);
		btnExtract.setToolTipText("This will inovke the Class Dependency Analyzer(CDA) v 1.16.0 to generate the odem file");
		btnExtract.setText("Extract");// odem file
		btnExtract.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO extract the odem file from the target project
				
				
			}
			 
		});
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.setToolTipText("Direct upload file from local disk");
		btnNewButton.setText("Load Local File");
		
		
		Label lblGroundtruthfile = new Label(container, SWT.NONE);
		lblGroundtruthfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroundtruthfile.setText("ground_truth_file");
		
		grandtruthtextContent = new Text(container, SWT.BORDER);
		grandtruthtextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		// attempt to set the grandtruthtextContent
		if(aProject.getFile("data"+File.separatorChar+aProject.getName()+"_ground_truth_recovery.rsf").exists()){
			IFile grandtruthfile = aProject.getFile("data"+File.separatorChar+aProject.getName()+"_ground_truth_recovery.rsf");
			String fullpath = grandtruthfile.getLocation().toOSString();
			grandtruthtextContent.setText(fullpath);
			grandtruthtextContent.setEditable(false);
		}
		
		Label lblDepsrsffile = new Label(container, SWT.NONE);
		lblDepsrsffile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDepsrsffile.setText("deps_rsf_file");
		
		deptrsftextContent = new Text(container, SWT.BORDER);
		deptrsftextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(aProject.getFile("data"+File.separatorChar+aProject.getName()+"_deps.rsf").exists()){
			IFile deptrsf = aProject.getFile("data"+File.separatorChar+aProject.getName()+"_deps.rsf");
			String fullpath = deptrsf.getLocation().toOSString();
			deptrsftextContent.setText(fullpath);
			deptrsftextContent.setEditable(false);
		}
		
		Label lblClusteringalgorithm = new Label(container, SWT.NONE);
		lblClusteringalgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblClusteringalgorithm.setText("clustering_algorithm");
		
		txtArc = new Text(container, SWT.BORDER);
		txtArc.setText("arc");
		txtArc.setEditable(false);
		txtArc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSimmeasure_1 = new Label(container, SWT.NONE);
		lblSimmeasure_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSimmeasure_1.setText("sim_measure");
		
		simmeasurecombo = new Combo(container, SWT.BORDER);
		simmeasurecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		simmeasurecombo.setItems(new String[]{"js","uem","uemnm","ilm","scm"});
		simmeasurecombo.select(0);
		
		Label lblGranule = new Label(container, SWT.NONE);
		lblGranule.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGranule.setText("granule");
		
		granulecombo = new Combo(container, SWT.NONE);
		granulecombo.setItems(new String[]{"file","class","func"});
		granulecombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		granulecombo.select(0);
		
		Label lblPreselectedrange = new Label(container, SWT.NONE);
		lblPreselectedrange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPreselectedrange.setText("preselected_range");
		
		text_7 = new Text(container, SWT.BORDER);
		GridData gd_text_7 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_7.widthHint = 174;
		text_7.setLayoutData(gd_text_7);
		
		text_1 = new Text(container, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		text_2 = new Text(container, SWT.BORDER);
		text_2.setText("5");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblStopcriterion = new Label(container, SWT.NONE);
		lblStopcriterion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStopcriterion.setText("stop_criterion");
		
		
		
		stoppingcriteriacombo = new Combo(container, SWT.NONE);
		stoppingcriteriacombo.setItems(new String[] {"preselected","clustergain"});
		stoppingcriteriacombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		stoppingcriteriacombo.select(0);
		
		Label lblTopicsdir = new Label(container, SWT.NONE);
		lblTopicsdir.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTopicsdir.setText("topics_dir");
		
		topictextContent = new Text(container, SWT.BORDER);
		topictextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		topictextContent.setText(projectPath);
		
		Label lblNumtopicsrange = new Label(container, SWT.NONE);
		lblNumtopicsrange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNumtopicsrange.setText("numtopics_range");
		
		numtopicrangeContentStart = new Text(container, SWT.BORDER);
		numtopicrangeContentStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		numtopicrangeContentStart.setText(aminaltopics+"");
		
		numtopicrangeContentEnd = new Text(container, SWT.BORDER);
		numtopicrangeContentEnd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		numtopicrangeContentEnd.setText(atotaltopics+"");
		
		numtopicStep = new Text(container, SWT.BORDER);
		numtopicStep.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		numtopicStep.setText("5");

		
		
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("selected_pkgs");
		
		selectePkgTextContent = new Text(container, SWT.BORDER);
		selectePkgTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		if(aProject!=null){
			Set<String> apackages = this.aAO.getPackages();
			String fullpackage = "";
			Set<String> shortedpackges = new HashSet<String>();
			for(String subpackage:apackages){
				shortedpackges = replacesubPackages(subpackage,shortedpackges);
			}
			
			for(String subpackage:shortedpackges){
				fullpackage+=subpackage;
				fullpackage+=",";
			}
			if(shortedpackges.size()>1){
				fullpackage = fullpackage.substring(0,fullpackage.length()-1);
			}
			selectePkgTextContent.setText(fullpackage);
		}

		Label lblDoctopicsfile = new Label(container, SWT.NONE);
		lblDoctopicsfile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDoctopicsfile.setText("doc_topics_file");
		
		docTopicFileTextContent = new Text(container, SWT.BORDER);
		docTopicFileTextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		docTopicFileTextContent.setText(adocTopicsFilePath);
		
		Label lblNewLabel_4 = new Label(container, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("smell_clusters_file");
		
		smellclstextContent = new Text(container, SWT.BORDER);
		smellclstextContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new Label(container, SWT.NONE);
		
		Button btnGenerateScript = new Button(container, SWT.NONE);
		btnGenerateScript.setText("Generate Script");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		
	}

	/**
	 * this function will replace all sub packages of parentpackage if any
	 * @param subpackage
	 * @param shortedpackges
	 * @return
	 */
	private Set<String> replacesubPackages(String parentpackage,Set<String> shortedpackges) {
		// TODO Auto-generated method stub
		Set<String>res = new HashSet<String>();
		boolean issubpackage = false;
		boolean isparent = false;
		for(String packagename:shortedpackges){
			if(issubpackage){
				res.add(packagename);
				continue;
			}
			if(packagename.indexOf(parentpackage)==0){
				if(packagename.charAt(parentpackage.length())=='.'){
					isparent = true;
					continue;
				}else{
					res.add(packagename);
				}	
			}else if(parentpackage.indexOf(packagename)==0){
				if(parentpackage.charAt(packagename.length())=='.'){
					issubpackage = true;
					continue;
				}else{
					res.add(packagename);
				}
			}else{
				res.add(packagename);
			}
		}
		
		if(isparent)
			res.add(parentpackage);
		return res;
	}
	
}
