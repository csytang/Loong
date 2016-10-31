package loongpluginfmrtool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import loongpluginfmrtool.module.model.Module;

public class ClusteringResultRSFOutput {
	private Map<Integer,Set<Module>>aclusterres;
	private IFile file;
	private final IProject project;
	
	public ClusteringResultRSFOutput(Map<Integer,Set<Module>>pclusterres,String method,IProject sourceProject){
		aclusterres = pclusterres;
		project = sourceProject;
		String fileName = method+"_"+".rsf";
		file = project.getFile(fileName); 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(Map.Entry<Integer, Set<Module>>entry:aclusterres.entrySet()){
			int clusterid = entry.getKey();
			Set<Module>set = entry.getValue();
			String fullString = "";
			for(Module module:set){
				fullString = "";
				fullString = "contains";
				fullString += "\t";
				fullString += clusterid;
				fullString += "\t";
				fullString += module.getDisplayName();
				fullString += "\n";
				try {
					out.write(fullString.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
	        
		}
		InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
	    try {
			file.create(inputsource, EFS.NONE, null);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
