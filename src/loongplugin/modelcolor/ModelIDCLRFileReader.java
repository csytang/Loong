package loongplugin.modelcolor;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.RGB;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import loongplugin.LoongPlugin;
import loongplugin.color.ColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;

public class ModelIDCLRFileReader {
	// 1. 
	private FeatureModel fmodel;
	private Map<String, Long> featureIds;
	private Map<Long, RGB> featureColors;
	private Map<Long, Boolean> featureVisibility;
	private IFile file;
	private ColorManager colormanger;
	
	public ModelIDCLRFileReader(IFile modelcolors,FeatureModel pmodel,ColorManager cmanager){
		fmodel = pmodel;
		file = modelcolors;
		colormanger = cmanager;
		try {
			load();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		upgratefmodel();
		
	}
	
	public FeatureModel getFeatureModel(){
		return fmodel;
	}
	
	public ColorManager getColorManager(){
		return colormanger;
	}
	
	public ModelIDCLRFileReader(IFile modelcolors,
			FeatureModel pmodel) {
		// TODO Auto-generated constructor stub
		fmodel = pmodel;
		file = modelcolors;
		colormanger = FeatureModelManager.getInstance().getColorManager();
		try {
			load();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	private void upgratefmodel() {
		// TODO Auto-generated method stub
		for(Feature f:fmodel.getFeatures()){
			String featureName = f.getName();
			long id = featureIds.get(featureName);
			f.setId(id);
			fmodel.setFeatureId(f, id);
			
			if(featureColors.containsKey(id)){
				RGB color = featureColors.get(id);
				f.setRGB(color);
				colormanger.setRGB(f, color);
			}
		}
		
	}

	
	private void load() throws CoreException, IOException, SAXException, ParserConfigurationException {
		featureIds = new HashMap<String, Long>();
		featureColors = new HashMap<Long, RGB>();
		featureVisibility = new HashMap<Long, Boolean>();
		
		
		if (!file.exists())
			return;
	
		InputStream is = file.getContents(true);
			
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		DefaultHandler handler = new DefaultHandler() {
			
			public void startElement(String uri, String localName,
					String qName, Attributes attributes)
					throws SAXException {
				if (qName.equals("featureattr")) {
					String name = attributes.getValue("name");
					long id = -1;
					try {
						id = Long.parseLong(attributes.getValue("id"));
					} catch (NumberFormatException e) {
					}
					String hexcolor = "";
					long color = -1;
					try {
						//color 解析程序出錯
						hexcolor = attributes.getValue("color");
						
					} catch (NumberFormatException e) {
					}
					boolean selected = !"false".equals(attributes
							.getValue("selected"));

					if (name != null && id > 0) {
						featureIds.put(name, id);	
						RGB rgb = hex2Rgb(hexcolor);
						featureColors.put(id, rgb);
						featureVisibility.put(id, selected);
					}

				}
			}

			
			public InputSource resolveEntity(String publicId,
					String systemId) throws IOException, SAXException {
				return new InputSource(
						new ByteArrayInputStream(new byte[0]));
			}
		};
		xmlReader.setContentHandler(handler);
		xmlReader.setDTDHandler(handler);
		xmlReader.setErrorHandler(handler);
		xmlReader.setEntityResolver(handler);
		xmlReader.parse(new InputSource(is));
	
			
	}
	
	public static RGB hex2Rgb(String colorStr) {
	    return new RGB(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
}
