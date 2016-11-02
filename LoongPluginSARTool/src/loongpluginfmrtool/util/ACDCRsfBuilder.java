package loongpluginfmrtool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.ProgramDatabase;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LICategories;
import loongplugin.source.database.model.LRelation;

public class ACDCRsfBuilder {
	private ApplicationObserver aAO;
	private IProject aProject;
	private ProgramDatabase aDB;
	private IFile file;
	private Set<LElement> allelements;
	private Set<LRelation>allcontainsrelations = new HashSet<LRelation>();
	private Map<CompilationUnit,Set<CompilationUnit>>dependsrelationmapping = new HashMap<CompilationUnit,Set<CompilationUnit>>();
	public ACDCRsfBuilder(ApplicationObserver pAO,IProject pProject){
		aAO = pAO;
		aProject = pProject;
		aDB = pAO.getProgramDatabase();
		file = pProject.getFile(aProject.getName()+"_info.rsf");		
		allcontainsrelations.add(LRelation.ACCESS_FIELD);
		allcontainsrelations.add(LRelation.ACCESS_LOCAL_VARIABLE);
		allcontainsrelations.add(LRelation.ACCESS_METHOD);
		allcontainsrelations.add(LRelation.ACCESS_TYPE);
		allcontainsrelations.add(LRelation.ACCESSES);
		allcontainsrelations.add(LRelation.EXTENDS_TYPE);
		allcontainsrelations.add(LRelation.IMPLEMENTS_METHOD);
		allcontainsrelations.add(LRelation.OVERRIDES_METHOD);
		allcontainsrelations.add(LRelation.REFERENCES);
		allcontainsrelations.add(LRelation.REQUIRES);
		
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
		Set<CompilationUnit>artifects = new HashSet<CompilationUnit>();
		if( monitor != null ){ 
			monitor.beginTask( "Extracting ACDC RSF facts", allelements.size()+3);
    	}
		
		for(LElement element:allelements){
			String artName = element.getASTID();
			// create instance type
			String artType = "";
			if(element.getCategory()==LICategories.COMPILATION_UNIT){
				artType = "Module";
			}
			
			if(!artType.equals("")){
				String artString = "$INSTANCE\t"+artName+"\t"+artType+"\n";
				try {
					out.write(artString.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				artifects.add(element.getCompilationUnit());
			}
			if( monitor != null ) 
				monitor.worked(1);
		}
		
		for(LElement element:allelements){
			CompilationUnit sourceunit = element.getCompilationUnit();
			if(artifects.contains(sourceunit)){
				for(LRelation relation:allcontainsrelations){
					Set<LElement> alltargetelement = aAO.getRange(element, relation);
					if(alltargetelement!=null){
						for(LElement target:alltargetelement){
							CompilationUnit targetunit = target.getCompilationUnit();
							if(artifects.contains(targetunit)){
								if(dependsrelationmapping.containsKey(sourceunit)){
									Set<CompilationUnit>targestunits = dependsrelationmapping.get(sourceunit);
									targestunits.add(targetunit);
									dependsrelationmapping.put(sourceunit, targestunits);
								}else{
									Set<CompilationUnit>targestunits = new HashSet<CompilationUnit>();
									targestunits.add(targetunit);
									dependsrelationmapping.put(sourceunit, targestunits);
								}
							}
						}
					}
				}
			}
		}
		
		for(Map.Entry<CompilationUnit, Set<CompilationUnit>>entry:dependsrelationmapping.entrySet()){
			CompilationUnit sourceunit = entry.getKey();
			String sourcepackageName = sourceunit.getPackage().getName().toString();
			List sourcetypes = sourceunit.types();    
			TypeDeclaration sourcetypeDec = (TypeDeclaration) sourcetypes.get(0); //typeDec is the class  
			String sourcefullName = sourcepackageName+"."+sourcetypeDec.getName().toString();
			
			Set<CompilationUnit> targetunits = entry.getValue();
			for(CompilationUnit target:targetunits){
				String targetpackageName = target.getPackage().getName().toString();
				List targettypes = target.types();    
				TypeDeclaration targettypeDec = (TypeDeclaration) targettypes.get(0); //typeDec is the class  
				String targetfullName = sourcepackageName+"."+targettypeDec.getName().toString();
				
				String artdepString = "depends "+sourcefullName+" "+targetfullName+"\n";
				try {
					out.write(artdepString.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
