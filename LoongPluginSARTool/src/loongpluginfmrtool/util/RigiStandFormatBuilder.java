package loongpluginfmrtool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;

public class RigiStandFormatBuilder {
	private ApplicationObserver aAO;
	private IProject aProject;
	private ProgramDatabase aDB;
	private IFile file;
	private Set<LElement> allelements;
	public RigiStandFormatBuilder(ApplicationObserver pAO,IProject pProject){
		aAO = pAO;
		aProject = pProject;
		aDB = pAO.getProgramDatabase();
		file = pProject.getFile(aProject.getName()+"_info.rsf");		
		
	}
	
	public void build(IProgressMonitor monitor){
		// if the file exist, delete it
		if(file.exists()){
			//delete the old file
			try {
				file.delete(true, monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		allelements = aDB.getAllElements();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		
		
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
