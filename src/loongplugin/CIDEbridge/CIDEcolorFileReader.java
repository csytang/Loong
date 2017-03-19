package loongplugin.CIDEbridge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.xml.parsers.ParserConfigurationException;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.utils.EmbeddedASTNodeCollector;

import org.xml.sax.SAXException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.swt.graphics.RGB;




public class CIDEcolorFileReader {
	
	private IFile colorFile = null;//.color
	private IFile javaFile = null;// the java file related to this file
	private IFile clrFile = null;
	private CLRAnnotatedSourceFile javaCLRFile;
	private ICompilationUnit compilationUnit;
	private CompilationUnitColorManager colormanager;
	
	private FeatureModel fmodel;
	private Set<ASTNode> compilationASTNodeSet = new HashSet<ASTNode>();
	private WeakHashMap<String,ASTNode> tempASTIDcache = new WeakHashMap<String, ASTNode>();
	private Set<String> tempASTIDs = new HashSet<String>();
	private Map<String, Set<Feature>> ASTIDToFeatures = new HashMap<String,Set<Feature>>();
	private Map<String, Set<Feature>> seperatedASTIDToFeatures = new HashMap<String,Set<Feature>>();
	private IProgressMonitor monitor;
	public CIDEcolorFileReader(IFile pcolorFile,FeatureModel pmodel,IProject sourceproject,IProject targetproject,IProgressMonitor pmonitor){
		colorFile = pcolorFile;
		fmodel = pmodel;
		monitor = pmonitor;
		IPath relativerPath = pcolorFile.getProjectRelativePath();
		relativerPath = relativerPath.removeFileExtension();
		while(relativerPath.getFileExtension().equals("java")){
			relativerPath = relativerPath.removeFileExtension();
			if(relativerPath.getFileExtension()==null)
				break;
		}
		
		IPath relativeJavaPath = relativerPath.addFileExtension("java");
		IPath relativeCLRPath = relativerPath.addFileExtension("clr");
		
		clrFile = targetproject.getFile(relativeCLRPath);
		if(colorFile.getFileExtension().equals("color")){
			//如果是在CIDE2 中得就生产想要的列表
			// 获得相应的java file
			
			javaFile = targetproject.getFile(relativeJavaPath);
			assert javaFile.exists()==true;
			assert javaFile.getFileExtension().contains("java")==true;
			compilationUnit = JavaCore.createCompilationUnitFrom(javaFile);
			javaCLRFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(compilationUnit);
			colormanager = (CompilationUnitColorManager) javaCLRFile.getColorManager();
			
			try {
				ASTIDToFeatures = readJavaColorFile(fmodel);
			} catch (ClassNotFoundException | CoreException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// collect astnodes for this file
			
			compilationASTNodeSet = EmbeddedASTNodeCollector.collectASTNodes(compilationUnit);
			// compute ASTID
			computeASTID();
			
			seperatedASTIDToFeatures = computeSeperatedASTIDToFeatures(ASTIDToFeatures);
			// converto clr IFile
			try {
				createCLRFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Map<String, Set<Feature>> computeSeperatedASTIDToFeatures(
			Map<String, Set<Feature>> aSTIDToFeatures) {
		// TODO Auto-generated method stub
		Map<String, Set<Feature>> result = new HashMap<String,Set<Feature>>();
		for(String astID:tempASTIDcache.keySet()){
			if(!result.containsKey(astID)){
				result.put(astID, new HashSet<Feature>());
			}
		}
		for(String asstIDcompounted:aSTIDToFeatures.keySet()){
			String tempString = asstIDcompounted;
			Set<Feature>associated = aSTIDToFeatures.get(asstIDcompounted);
			int length = tempString.length();
			for(String asstID:tempASTIDcache.keySet()){
				if(tempString.contains(asstID)){
					int startIndex = tempString.indexOf(asstID);
					int endIndex = startIndex+asstID.length();
					if(startIndex==0){
						if(endIndex==length){
							result.get(asstID).addAll(associated);
						}else if(tempString.charAt(1+endIndex)==':'){
							result.get(asstID).addAll(associated);
						}
					}else if(endIndex==length){
						if(tempString.charAt(startIndex-1)==':'){
							result.get(asstID).addAll(associated);
						}
					}else if(tempString.charAt(startIndex-1)==':' && 
							tempString.charAt(endIndex-1)==':'){
						result.get(asstID).addAll(associated);
					}
				}
			}
		}
		
		return result;
	}

	private final static long serialVersionUID = 1l;
	
	public void createCLRFile() throws IOException, CoreException{
		//colormanager.beginBatch();
		for(String astID:tempASTIDcache.keySet()){
			if(seperatedASTIDToFeatures.containsKey(astID)){
				Set<Feature>associatedFeatures = seperatedASTIDToFeatures.get(astID);
				ASTNode astnode = tempASTIDcache.get(astID);
				for(Feature f:associatedFeatures){
					colormanager.addColor(astnode, f);
					f.addASTNodeToFeature(compilationUnit,astnode);
				}
				
			}
		}
		//colormanager.endBatch();
		
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(b);
		out.writeLong(serialVersionUID);
		out.writeObject(colormanager.getNode2Colors());
		
		ByteArrayInputStream source = new ByteArrayInputStream(b
				.toByteArray());
		out.close();
		if (!clrFile.exists())
			clrFile.create(source, true, monitor);
		else
			clrFile.setContents(source, true, true, monitor);
	}
	
	public IFile getCreatedCLRIFile(){
		return clrFile;
	}
	
	
	
	public void computeASTID(){
		tempASTIDcache.clear();
		tempASTIDs.clear();
		for(ASTNode node:compilationASTNodeSet){
			String nodeastID = ASTID.calculateId(node);
			tempASTIDcache.put(nodeastID, node);
			tempASTIDs.add(nodeastID);
		}
	}
	/**
	 * 处理 .color 和 .java.color 全部都可以归结为 .color 
	 * 我们将这个映射到  .clr上
	 * @throws CoreException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public Map<String, Set<Feature>> readJavaColorFile(FeatureModel featureModel) throws CoreException, IOException, ClassNotFoundException, ParserConfigurationException, SAXException{
		InputStream is = colorFile.getContents(true);
		boolean isXML = is.read() == '<';

		is = colorFile.getContents(true);
		// check for XML file
		if (isXML){
			return CIDEXMLReader.loadFeatureMap(new InputStreamReader(is),
					(FeatureModel) featureModel);
		}
		// otherwise legacy formats
		ObjectInputStream out = new ObjectInputStream(is);
		
		try {
			long version = out.readLong();
			if (version == CIDELegacyReader.LEGACY_SERIALIZED_VERSION)
				return CIDELegacyReader.loadLegacySerialization(out,featureModel);
			else if (version != CIDESerializedReader.serialVersionUID)
				return new HashMap<String, Set<Feature>>();
			else
				return CIDESerializedReader.loadFeatureMap(out,featureModel);
		} finally {
			out.close();
		}
	}

	
}
