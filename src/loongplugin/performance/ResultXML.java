package loongplugin.performance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import loongplugin.feature.Feature;

public class ResultXML {
	private Map<Feature,Map<String,Double>> results;
	private IProject aproject;
	
	public ResultXML(Map<Feature,Map<String,Double>> presults,IProject project){
		results = presults;
		aproject = project;
		try {
			writeresult();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeresult() throws ParserConfigurationException, TransformerException, CoreException{
		IFile file = aproject.getFile("result.xml");
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(out);
		
		DocumentBuilderFactory  fct=DocumentBuilderFactory.newInstance();
        
        DocumentBuilder bui= fct.newDocumentBuilder();
      
        Document doc=bui.newDocument();
        
        Element resultelement =doc.createElement("result");
        doc.appendChild(resultelement);
        
        for(Map.Entry<Feature,Map<String,Double>>resultentry:results.entrySet()){
        	
        	Feature f = resultentry.getKey();
        	Map<String,Double> fresult = resultentry.getValue();
        	
        	Element feature =doc.createElement("feature");
        	
        	Attr name = doc.createAttribute("name");
        	name.setValue(f.getName());
        	feature.setAttributeNode(name);
        	
        	for(String attr:fresult.keySet()){
        		Element astelement = doc.createElement("attr");
        		Attr attrname = doc.createAttribute("name");
        		attrname.setValue(attr);
        		Text attrtext=doc.createTextNode(fresult.get(attr)+"");
        		astelement.setAttributeNode(attrname);
        		astelement.appendChild(attrtext);
        		feature.appendChild(astelement);
        	}
        	
        	resultelement.appendChild(feature);
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
