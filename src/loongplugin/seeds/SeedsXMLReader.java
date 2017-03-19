package loongplugin.seeds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.feature.FeatureModel;

import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.feature.Feature;
import loongplugin.utils.EmbeddedASTNodeCollector;


public class SeedsXMLReader {
	/*
	 * The structure of SeedLog
	 * -Feature
	 *  - CompilationUnit
	 *     - ASTID
	 *  - CompilationUnit
	 *     - ASTID
	 *  - FeatureId
	 *  - FeatureColor_RGB
	 *  <featureseed>
	 *  	<featureattr name = "" id = "" color = "">
	 *  		<seed id = "", compliationUnit = "">
	 *  			<astid>ASTID</astid>
	 *  		</seed>
	 *  	</featureattr>
	 *  </featureseed>
	 */
	
	private FeatureModel fmodel;
	private IProject aproject;
	private IFile iaseedfile;
	private File aseedfile;
	private Map<Feature,Map<IFile,Set<ASTNode>>> recoveredseed = new HashMap<Feature,Map<IFile,Set<ASTNode>>>();
	private Map<Element,Feature> temp_ElementToFeature = new HashMap<Element,Feature>();
	private Map<Element,Element> temp_ElementToParent = new HashMap<Element,Element>();
	private Map<Element,IFile> temp_ElementToIFile = new HashMap<Element,IFile>();
	
	private Map<String,IFile> temp_ICUStrToIFile = new HashMap<String,IFile>();
	private Map<IFile,Set<ASTNode>> temp_IFiletoASTNodes = new HashMap<IFile,Set<ASTNode>>();
	private enum EntryFile {IFile,File};
	private EntryFile inputType;
	
	private Element root;
	public SeedsXMLReader(IFile seedfile,FeatureModel pfmodel,IProject pproject){
		iaseedfile = seedfile;
		fmodel = pfmodel;
		aproject = pproject;
		inputType = EntryFile.IFile;
		//1. 收集所有的ICompilationUnit的信息
		//2. 收集所有的ASTNode信息
		collectICompilationUnit(aproject);
		try {
			seedsread();
		} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public SeedsXMLReader(File seedfile,FeatureModel pfmodel,IProject pproject){
		aseedfile = seedfile;
		fmodel = pfmodel;
		aproject = pproject;
		inputType = EntryFile.File;
		//1. 收集所有的ICompilationUnit的信息
		//2. 收集所有的ASTNode信息
		collectICompilationUnit(aproject);
		try {
			seedsread();
		} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	private void collectICompilationUnit(IContainer container){
		try {
			   IResource[] members = container.members();
			   for (IResource member : members)
			   {
			      if (member instanceof IContainer) 
			      {
			    	  collectICompilationUnit((IContainer)member);
			      }
			      else if (member instanceof IFile)
			      {	
			    	  IFile memberfile = (IFile)member;
			    	  IPath relativefilePath = memberfile.getProjectRelativePath();
			    	  String extension = memberfile.getFileExtension();
			    	  if(extension==null)
			    		  continue;
					  if(extension.equals("java")){
						 ICompilationUnit unit =  JavaCore.createCompilationUnitFrom(memberfile);
						 String unitName = unit.getElementName();
						 temp_ICUStrToIFile.put(unitName, memberfile);
						 Set<ASTNode> allastnodes = EmbeddedASTNodeCollector.collectASTNodes(unit);
						 temp_IFiletoASTNodes.put(memberfile, allastnodes);
					  }
					  
			      }
			   }
		} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
	public void seedsread() throws CoreException, DocumentException, FileNotFoundException{
		InputStream in = null;
		if(inputType==EntryFile.IFile){
			in = iaseedfile.getContents(true);
		}else{
			in = new FileInputStream(aseedfile);
		}
		SAXReader reader = new SAXReader();  
		Document doc = reader.read(in);
		root = doc.getRootElement();
		readNode(root, null);
	}
	
	public void readNode(Element node, Element parent) {
		if (node == null) 
			return;
		List<Attribute> attrs = node.attributes(); 
		
		if (attrs != null && attrs.size() > 0) {
			if(node.getName().equals("featureattr")){
				String featureNameStr = node.attribute("name").getValue();
				Feature feature = fmodel.getFeature(featureNameStr);
				temp_ElementToFeature.put(node, feature);
				if(!recoveredseed.containsKey(feature)){
					Map<IFile,Set<ASTNode>> featurebindingseeds = new HashMap<IFile,Set<ASTNode>>();
					recoveredseed.put(feature, featurebindingseeds);
				}
			}
			if(node.getName().equals("seed")){
				Attribute compliationUnitAttr = node.attribute("compliationUnit");
				String compliationUnitStr = compliationUnitAttr.getValue();
				IFile file = temp_ICUStrToIFile.get(compliationUnitStr);
				temp_ElementToIFile.put(node, file);
				Feature associatedfeature;
				if(temp_ElementToFeature.containsKey(parent)){
					associatedfeature = temp_ElementToFeature.get(parent);
				}else{
					readNode(parent,root);
					associatedfeature = temp_ElementToFeature.get(parent);
				}
				assert recoveredseed.containsKey(associatedfeature)==true;
				Map<IFile,Set<ASTNode>> featurebindingseeds = recoveredseed.get(associatedfeature);
				if(!featurebindingseeds.containsKey(file)){
					Set<ASTNode>astNodes = new HashSet<ASTNode>();
					featurebindingseeds.put(file, astNodes);
				}
			}
		}else if(node.getName().equals("astid")){
				assert temp_ElementToParent.containsKey(parent);
				Element grandparent = temp_ElementToParent.get(parent);
				Feature feature = temp_ElementToFeature.get(grandparent);
				IFile file = temp_ElementToIFile.get(parent);
				Set<ASTNode>allastnodes = temp_IFiletoASTNodes.get(file);
				Map<String,ASTNode>astIDToASTNode = collectASTID(allastnodes);
				List<Text> childNodes = node.content();
				Set<ASTNode>associatedset = new HashSet<ASTNode>();
				for(Text e:childNodes){
					String astId = e.getText();
					assert astIDToASTNode.containsKey(astId);
					ASTNode astnode = astIDToASTNode.get(astId);
					associatedset.add(astnode);
				}
				Map<IFile,Set<ASTNode>> associateseeds = recoveredseed.get(feature);
				if(associateseeds.containsKey(file)){
					Set<ASTNode> existseeds =  associateseeds.get(file);
					existseeds.addAll(associatedset);
					associateseeds.put(file, existseeds);
				}else{
					associateseeds.put(file, associatedset);
				}
		}
		
		List<Element> childNodes = node.elements();
		
		for (Element e : childNodes) { 
			temp_ElementToParent.put(e, node);
			readNode(e, node);
		}
	}
	
	private Map<String,ASTNode> collectASTID(Set<ASTNode>allastnodes){
		Map<String,ASTNode> ASTIDToASTNode = new HashMap<String,ASTNode>();
		for(ASTNode node:allastnodes){
			String astId = ASTID.calculateId(node);
			ASTIDToASTNode.put(astId, node);
		}
		
		return ASTIDToASTNode;
	}
	
	public Map<Feature,Map<IFile,Set<ASTNode>>> getSeeds(){
		return recoveredseed;
	}
}
