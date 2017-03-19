package loongplugin.seeds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.utils.LegacyAnnotationCaptureJob;


public class SeedsXMLWriter {
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
	private IProject targetproject;
	public SeedsXMLWriter(FeatureModel pfmodel,IProject targetProject) throws InterruptedException{
		this.fmodel = pfmodel;
		this.targetproject = targetProject;
		try {
			if(!LegacyAnnotationCaptureJob.getCaptureStatus()){
				LegacyAnnotationCaptureJob job = new LegacyAnnotationCaptureJob(targetproject,this);
				job.setUser(true);
				job.setPriority(Job.LONG);
				job.schedule();
				
			}else
				seedswrite();
			
			
		} catch (SAXException
				| ParserConfigurationException | TransformerException | CoreException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

	public void seedswrite() throws SAXException, ParserConfigurationException, TransformerException, CoreException{
	
		IFile file = targetproject.getFile("seed.xml");
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(out);
		
		DocumentBuilderFactory  fct=DocumentBuilderFactory.newInstance();
         
        DocumentBuilder bui=fct.newDocumentBuilder();
      
        Document doc=bui.newDocument();
        
        Element featureseed=doc.createElement("featureseed");
        doc.appendChild(featureseed);
        for(Feature f:fmodel.getFeatures()){
        	Element featureelement = doc.createElement("featureattr");
        	//<featureattr name = "" id = "" color = "">
        	Attr name = doc.createAttribute("name");
        	name.setValue(f.getName());
        	featureelement.setAttributeNode(name);
        	
        	
        	Attr id = doc.createAttribute("id");
        	id.setValue(f.getId()+"");
        	featureelement.setAttributeNode(id);
        	
        	Attr color = doc.createAttribute("color");
        	RGB rgb = f.getRGB();
        	String hex = String.format("#%02x%02x%02x", rgb.red, rgb.green, rgb.blue);
        	color.setValue(hex);
        	featureelement.setAttributeNode(color);
        	
        	featureseed.appendChild(featureelement);
        	Map<ICompilationUnit,Set<ASTNode>> astnodes = f.getASTNodeBelongs();
        	for(ICompilationUnit unit:astnodes.keySet()){
        		Element seedelement = doc.createElement("seed");
        		Attr compliationUnit = doc.createAttribute("compliationUnit");
        		compliationUnit.setValue(unit.getElementName());
        		featureelement.appendChild(seedelement);
        		seedelement.setAttributeNode(compliationUnit);
        		
        		for(ASTNode node:astnodes.get(unit)){
        			Element astelement = doc.createElement("astid");
        			String astID = ASTID.calculateId(node);
        			Text asttext=doc.createTextNode(astID);
        			astelement.appendChild(asttext);
        			seedelement.appendChild(astelement);
        		}
        	}
        	
        }
        
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		//File targetfile = new File(file.getFullPath().toFile().getAbsolutePath());
       
        transformer.transform(source, result);
        
        InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
        if(file.exists()){
        	file.setContents(inputsource, EFS.NONE, null);
        }else
        	file.create(inputsource, EFS.NONE, null);
	}
}
