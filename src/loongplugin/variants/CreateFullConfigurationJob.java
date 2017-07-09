package loongplugin.variants;

import java.io.File;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.featureconfiguration.Configuration;
import loongplugin.featureconfiguration.SelectableFeature;
import loongplugin.featureconfiguration.Selection;
import loongplugin.utils.WriteToFile;

public class CreateFullConfigurationJob extends WorkspaceJob {

	private final IWorkspaceRoot root;

	private final IProject sourceProject;



	private FeatureModel featureModel;
	
	private List<Feature> featureslist;
	
	private String validConfigurationFilePath;
	
	public CreateFullConfigurationJob(IProject sourceProject,FeatureModel pfeaturemodel) {
		
		super("Generating Variant: ");
		this.sourceProject = sourceProject;
		root = ResourcesPlugin.getWorkspace().getRoot();
		
		this.featureModel = pfeaturemodel;
		
		this.featureslist = this.featureModel.getRankedFeature();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
		String projectPath = workspace.getRoot().getLocation().toOSString()+File.separatorChar+sourceProject.getName().toString();
		
		this.validConfigurationFilePath = projectPath+File.separatorChar+"validconf.txt";
		// get all valid configurations
		getallValidConfiguration();
	
	}

	
	
	private void get_t_wiseconfiguration(int t){
		Set<Configuration> solutions = new HashSet<Configuration>();
		int totalsize = (int) (Math.pow(2, featureslist.size()));
		Set<BitSet>visited = new HashSet<BitSet>();
		WriteToFile writer = new WriteToFile(validConfigurationFilePath);
		boolean isfirst = true;
		String featureliststr = "";
		for(Feature feature:featureslist){
			if(isfirst){
				isfirst = false;
			}else{
				featureliststr+="|";
			}
			featureliststr+=feature.getName();
		}
		featureliststr+="\n";
		writer.writeALine(featureliststr);
		
	}
	
	private void getallValidConfiguration() {
		// get full list of the features
		
		Set<Configuration> solutions = new HashSet<Configuration>();
		int totalsize = (int) (Math.pow(2, featureslist.size()));
		Set<BitSet>visited = new HashSet<BitSet>();
		WriteToFile writer = new WriteToFile(validConfigurationFilePath);
		String featureliststr = "";
		boolean isfirst = true;
		
		for(Feature feature:featureslist){
			if(isfirst){
				isfirst = false;
			}else{
				featureliststr+="|";
			}
			featureliststr+=feature.getName();
		}
		featureliststr+="\n";
		writer.writeALine(featureliststr);
		
		
		for(int i = 0; i < totalsize-1;i++){
			Configuration configuration = new Configuration(this.featureModel);
			BitSet configbitwise = new BitSet(featureslist.size());
			Map<Feature,SelectableFeature> feature_selectablefeature = configuration.getFeaturetoSelectableFeature();
			for(int j = 0; j < featureslist.size();j++){
				SelectableFeature scale = feature_selectablefeature.get(featureslist.get(j));
				if (scale.getAutomatic() == Selection.UNDEFINED) {
					if (scale.getManual() == Selection.UNDEFINED){
						if(((1<<j) & i)!=0){
							configuration.setManual(scale, Selection.SELECTED);
							configbitwise.set(j, true);
						}else{
							configuration.setManual(scale, Selection.UNSELECTED);
							configbitwise.set(j, false);
						}
					}
					else if (scale.getManual() == Selection.SELECTED){
						if(((1<<j) & i)==0){
							configuration.setManual(scale, Selection.UNSELECTED);
							configbitwise.set(j, false);
						}
					}
					else if (scale.getManual() == Selection.UNSELECTED){
						if(((1<<j) & i)!=0){
							configuration.setManual(scale, Selection.SELECTED);
							configbitwise.set(j, true);
						}
					}
					if(!configuration.valid()){
						break;
					}
				}
			}
			
			if(configuration.valid() && !visited.contains(configbitwise)){
				solutions.add(configuration);
				visited.add(configbitwise);
				String result = "";
				Set<Feature> selected = configuration.getSelectedFeatures();
				for(Feature fe:featureslist){
					if(selected.contains(fe)){
						result+="1";
					}else{
						result+="0";
					}
				}
				result+="\n";
				writer.writeALine(result);
			}
			
		}
		
		writer.close();
		
		
	}
	

	
	

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		// generate all valid configuration in the system
		
		
		monitor.done();
		return Status.OK_STATUS;
	}

}
