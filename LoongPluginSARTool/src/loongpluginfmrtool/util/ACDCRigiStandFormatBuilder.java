package loongpluginfmrtool.util;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;

public class ACDCRigiStandFormatBuilder {
	private ApplicationObserver aAO;
	private IProject aProject;
	private ProgramDatabase aDB;
	private IFile file;
	private Set<LElement> allelements;
	private Set<LRelation>allcontainsrelations = new HashSet<LRelation>();
	public ACDCRigiStandFormatBuilder(ApplicationObserver pAO,IProject pProject){
		aAO = pAO;
		aProject = pProject;
		aDB = pAO.getProgramDatabase();
		file = pProject.getFile(aProject.getName()+"_info.rsf");		
		allcontainsrelations.add(LRelation.DECLARES);
		allcontainsrelations.add(LRelation.DECLARES_FIELD);
		allcontainsrelations.add(LRelation.DECLARES_FIELD_ACCESS);
		allcontainsrelations.add(LRelation.DECLARES_IMPORT);
		allcontainsrelations.add(LRelation.DECLARES_LOCAL_VARIABLE_ACCESS);
		allcontainsrelations.add(LRelation.DECLARES_METHOD);
		allcontainsrelations.add(LRelation.DECLARES_METHOD_ACCESS);
		allcontainsrelations.add(LRelation.DECLARES_TYPE);
		allcontainsrelations.add(LRelation.DECLARES_TYPE_ACCESS);
		allcontainsrelations.add(LRelation.DECLARES_PARAMETER);
		
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
		Set<LElement>artifects = new HashSet<LElement>();
		if( monitor != null ){ 
			monitor.beginTask( "Extracting ACDC RSF facts", allelements.size()+3);
    	}
		
		for(LElement element:allelements){
			String artName = element.getASTID();
			// create instance type
			String artType = "";
			switch(element.getCategory()){
			case CLASS:{
				artType = "Class";
				break;
			}
			case FIELD:{
				artType = "Field";
				break;
			}
			case METHOD:{
				artType = "Method";
				break;
			}
			case TYPE:{
				artType = "Type";
				break;
			}
			case LOCAL_VARIABLE:{
				artType = "Variable";
				break;
			}
			case IMPORT:{
				artType = "Import";
				break;
			}
			case COMPILATION_UNIT:{
				artType = "JavaFile";
				break;
			}
			}
			
			if(!artType.equals("")){
				String artString = "$INSTANCE\t"+artName+"\t"+artType+"\n";
				try {
					out.write(artString.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				artifects.add(element);
			}
			if( monitor != null ) 
				monitor.worked(1);
		}
		
		for(LElement element:artifects){
			for(LRelation relation:allcontainsrelations){
				Set<LElement> alltargetelement = aAO.getRange(element, relation);
				if(alltargetelement!=null){
					for(LElement target:alltargetelement){
						if(artifects.contains(target)){
							String artString = "contain\t"+element.getASTID()+"\t"+target.getASTID()+"\n";
							try {
								out.write(artString.getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			
		}
		monitor.worked(1);
		
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
        monitor.worked(1);
		monitor.done();
	}
}
