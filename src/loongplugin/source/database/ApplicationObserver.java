package loongplugin.source.database;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/* JayFX - A Fact Extractor Plug-in for Eclipse
 * Copyright (C) 2006  McGill University (http://www.cs.mcgill.ca/~swevo/jayfx)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Revision: 1.7 $
 */
import java.util.Observable;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.thoughtworks.xstream.converters.ConversionException;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;
import loongplugin.source.database.model.LElement;
import loongplugin.source.database.model.LElementColorManager;
import loongplugin.source.database.model.LFlyweightElementFactory;
import loongplugin.source.database.model.LRelation;

public class ApplicationObserver extends Observable{
	// The database object should be used for building the database
    private ProgramDatabase aDB;
    
    // A Set of all the packages in the "project"
    private Set<String> aPackages = new HashSet<String>();
        
    private IProject selectedProject = null;
    
    
    private static ApplicationObserver AOB = null;
    private LElementColorManager elementColorManager;
    private Analyzer aAnalyzer;
    private LFlyweightElementFactory elementFactory;
    private List<ICompilationUnit> lTargets = new LinkedList<ICompilationUnit>();
    private boolean hasbeenInialized = false;
	private ApplicationObserver() {
		AOB = null;
		selectedProject = null;
	}
	
	public static ApplicationObserver getInstance() {
		if (AOB == null)
			AOB = new ApplicationObserver();
		return AOB;
	}
	
	
	/**
	 * Returns an IElement describing the argument Java element. Not designed to
	 * be able to find initializer blocks or arrays.
	 * 
	 * @param pElement
	 *            Never null.
	 * @return Never null
	 * @throws ConversionException
	 *             if the element cannot be converted.
	 */
	public LElement convertToElement(ASTNode astNode)
			throws ConversionException {

		return aDB.getElement(ASTID.calculateId(astNode));

	}
	/**
	 * Initializes the program database with information about relations between
	 * all the source elements in pProject and all of its dependent projects.
	 * 
	 * @param pProject
	 *            The project to analyze. Should never be null.
	 * @param pProgress
	 *            A progress monitor. Can be null.
	 * @param pCHA
	 *            Whether to calculate overriding relationships between methods
	 *            and to use these in the calculation of CALLS and CALLS_BY
	 *            relations.
	 * @param puremod
	 * 			  if puremod is true, feature information is not required
	 * @throws ApplicationObserverException 
	 * @throws ApplicationControllerException
	 *             If the method cannot complete correctly
	 */
	public void initialize(IProject aProject, IProgressMonitor pProgress) throws ApplicationObserverException {
		// TODO Auto-generated method stub
		assert( aProject != null );
		// The database object should be used for building the database
		// aDB = BerkeleyProgramDatabase.getInstance();
		aDB = new ProgramDatabase();
		selectedProject = aProject;
		
		
		elementFactory = new LFlyweightElementFactory();
		
		elementColorManager = new LElementColorManager(this);
		
		aAnalyzer = new Analyzer(aDB);
		
		// Collect all target classes
    	//List<ICompilationUnit> lTargets = new ArrayList<ICompilationUnit>();
    	for (IJavaProject lNext : getJavaProjects(aProject))
    	{
    		lTargets.addAll(getICompilationUnits(lNext));
    		
    	}
    	
    	if( pProgress != null ){ 
    		pProgress.beginTask( "Building program database", lTargets.size()*2);
    	}
    	
    	ApplicationDeclareandRelationBuilder abuilder;
    	for( ICompilationUnit lCU : lTargets )
    	{
    		pProgress.subTask("Process CompilationUnit: "+lCU.getElementName());
    		System.out.println("Process CompilationUnit: "+lCU.getElementName());
          	try
  			{
          		IPackageDeclaration[] lPDs = lCU.getPackageDeclarations();
          		if( lPDs.length > 0 )
          		{
          			aPackages.add( lPDs[0].getElementName() );
          			
          		}
  			}
          	catch( JavaModelException lException )
  			{
          		throw new ApplicationObserverException( lException );
  			}
          	
          	CLRAnnotatedSourceFile annotatedsourcefile = getCLRAnnotatedSourceFile(lCU);
          	abuilder = new ApplicationDeclareandRelationBuilder(aDB,lCU,annotatedsourcefile,elementFactory,elementColorManager);
          	
          	abuilder.createElementsAndDeclareRelations();
          	if( pProgress != null ) 
          		pProgress.worked(1);
    	}
    	
    	AccessRelationBuilder relationBuilder = new AccessRelationBuilder(aDB,elementFactory);
    	for( ICompilationUnit lCU : lTargets )
    	{
    		pProgress.subTask("Creating relations in "+lCU.getElementName());
    		System.out.println("Creating relations in : "+lCU.getElementName());
    		CLRAnnotatedSourceFile annotatedsourcefile = getCLRAnnotatedSourceFile(lCU);
    		
    		relationBuilder.buildRelations(lCU,annotatedsourcefile,elementColorManager);
    		
    		if( pProgress != null ) 
          		pProgress.worked(1);
    	}
        
        pProgress.done();
    	
        hasbeenInialized = true;
	}
	
	
	private CLRAnnotatedSourceFile getCLRAnnotatedSourceFile(ICompilationUnit lCU) {
		// TODO Auto-generated method stub
		CLRAnnotatedSourceFile clrannotateFile = null;
		IFile sourceFile = (IFile)lCU.getResource();
		IPath clrFilePath = sourceFile.getFullPath().removeFileExtension().addFileExtension("clr");
		clrannotateFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(ResourcesPlugin.getWorkspace().getRoot().getFile(clrFilePath));
		
		return clrannotateFile;
	}

	public void dump(){
		
	}
	
	/**
	 * Returns all projects to analyze in IJavaProject form, including the
	 * dependent projects.
	 * 
	 * @param pProject
	 *            The project to analyze (with its dependencies. Should not be
	 *            null.
	 * @return A list of all the dependent projects (including pProject). Never
	 *         null.
	 * @throws ApplicationControllerException
	 *             If the method cannot complete correctly.
	 */
	private static List<IJavaProject> getJavaProjects(IProject pProject) throws ApplicationObserverException {
		assert (pProject != null);

		List<IJavaProject> lReturn = new ArrayList<IJavaProject>();
		try {
			lReturn.add(JavaCore.create(pProject));
			IProject[] lReferencedProjects = pProject.getReferencedProjects();
			for (int i = 0; i < lReferencedProjects.length; i++) {
				lReturn.add(JavaCore.create(lReferencedProjects[i]));
			}
		} catch (CoreException pException) {
			throw new ApplicationObserverException(
					"Could not extract project information", pException);
		}
		return lReturn;
	}

	/**
	 * Returns all the compilation units in this projects
	 * 
	 * @param pProject
	 *            The project to analyze. Should never be null.
	 * @return The compilation units to generate. Never null.
	 * @throws ApplicationControllerException
	 *             If the method cannot complete correctly
	 */
	public static List<ICompilationUnit> getICompilationUnits(IJavaProject pProject) throws ApplicationObserverException {
		assert (pProject != null);

		List<ICompilationUnit> lReturn = new ArrayList<ICompilationUnit>();

		try {
			IPackageFragment[] lFragments = pProject.getPackageFragments();
			for (int i = 0; i < lFragments.length; i++) {
				ICompilationUnit[] lCUs = lFragments[i].getCompilationUnits();
				for (int j = 0; j < lCUs.length; j++) {
					lReturn.add(lCUs[j]);
				}
			}
		} catch (JavaModelException pException) {
			throw new ApplicationObserverException(
					"Could not extract compilation units from project",
					pException);
		}
		return lReturn;
	}
    
    
    
    /**
     * Returns all the elements in the database in their lighweight form.
     * @return A Set of IElement objects representing all the elements in the 
     * program database.
     */
    public Set<LElement> getAllElements()
    {
    	return aDB.getAllElements();
    }

	public ProgramDatabase getProgramDatabase() {
		// TODO Auto-generated method stub
		return aDB;
	}

	public Set<LElement> getElementsOfFeature(Feature feature) {
		// TODO Auto-generated method stub
		return elementColorManager.getElementsOfFeature(feature);
	}

	public Set<Feature> getRelatedFeatures(Feature feature) {
		return elementColorManager.getRelatedFeatures(feature);
	}
	
	
	
	public Set<Feature> getElementFeatures(LElement element) {
		return elementColorManager.getElementFeatures(element);
	}

	public LFlyweightElementFactory getLFlyweightElementFactory(){
		return elementFactory;
	}

	public Set<LElement> getRange(LElement element, LRelation tmpTransRelation) {
		return aAnalyzer.getRange(element,tmpTransRelation);
	}

	public IProject getInitializedProject() {
		// TODO Auto-generated method stub
		return selectedProject;
	}

	public boolean isInitialized(IProject project){
		if(project==selectedProject)
			return hasbeenInialized;
		else
			return false;
	}

	public Set<String> getPackages() {
		return aPackages;
	}

	

	public List<CompilationUnit> getCompilationUnits() {
		List<CompilationUnit>res = new LinkedList<CompilationUnit>();
		for(ICompilationUnit iunit:this.lTargets){
			
			CLRAnnotatedSourceFile clrannotateFile = null;
			IFile sourceFile = (IFile)iunit.getResource();
			IPath clrFilePath = sourceFile.getFullPath().removeFileExtension().addFileExtension("clr");
			clrannotateFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(ResourcesPlugin.getWorkspace().getRoot().getFile(clrFilePath));
			CompilationUnit unit;
			try {
				unit = clrannotateFile.getAST();
				if(!res.contains(unit)){
					res.add(unit);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return res;
	}

	public List<ICompilationUnit> getICompilationUnits() {
		return this.lTargets;
	}
	

   
   
  
}
