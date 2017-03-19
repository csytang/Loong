package loongplugin.modelcolor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import loongplugin.color.coloredfile.ASTID;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class ModelIDCLRFile {
	/*
	 * modelidclr.xml
	 */
	private IProject selectedProject;
	private FeatureModel fmodel;
	
	public ModelIDCLRFile(FeatureModel pfmodel,IProject targetProject){
		selectedProject =  targetProject;
		fmodel = pfmodel;
		try {
			writemodelIDCLRFile();
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
	
	
	
	private void writemodelIDCLRFile() throws ParserConfigurationException, TransformerException, CoreException {
		// TODO Auto-generated method stub
		IFile file = selectedProject.getFile("modelidclr.xml");
		
		if(file.exists()){
			return;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(out);
		
		DocumentBuilderFactory  fct=DocumentBuilderFactory.newInstance();
        
        DocumentBuilder bui=fct.newDocumentBuilder();
        
        Document doc=bui.newDocument();
        
        Element featureseed=doc.createElement("featuremodelattributes");
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
        	        	
        }
        
        
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		//File targetfile = new File(file.getFullPath().toFile().getAbsolutePath());
       
        transformer.transform(source, result);
        
        InputStream inputsource = new ByteArrayInputStream(out.toByteArray());
        file.create(inputsource, EFS.NONE, null);
	}



	
	
}
