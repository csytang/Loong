package loongplugin.CIDEbridge;
/*
Copyright 2010 Christian K�stner

This file is part of CIDE.

CIDE is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 3 of the License.

CIDE is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CIDE.  If not, see <http://www.gnu.org/licenses/>.

See http://www.fosd.de/cide/ for further information.
*/


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
* reads a simple XML format (not validated before reading)
* 
* <annotations> <key name="ASTID" features="1,2,3" /> <key name="ASTID"
* features="1,2,3" /> </annotations>
* 
* featureids are separated by commas
* 
* @author ckaestne
* 
*/
public class CIDEXMLReader {

/**
 * using sax parser (for performance) to get all values
 * 
 * @param r
 * @param featureModel
 * @return
 * @throws IOException
 * @throws ClassNotFoundException
 */
static HashMap<String, Set<Feature>> loadFeatureMap(Reader r, final FeatureModel featureModel) throws IOException,
		ClassNotFoundException {
	final HashMap<String, Set<Feature>> result = new HashMap<String, Set<Feature>>();
	
	try {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
		SAXParser parser = parserFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		// xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
		// false);
		// xmlReader.setFeature("http://xml.org/sax/features/validation",
		// false);
		DefaultHandler handler = new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes)
					throws SAXException {
				if (qName.equals("key")) {
					String key = attributes.getValue("name");
					String featureIdsStr = attributes.getValue("features");
					if (key != null && featureIdsStr != null) {
						String[] featureIds = featureIdsStr.split(",");
						HashSet<Feature> features = new HashSet<Feature>();
						for (String featureId : featureIds) {
							long id = -1;
							try {
								id = Long.parseLong(featureId);
								
								//FINISH
							} catch (NumberFormatException e) {
							}
							if (id >= 0) {
								Feature feature = featureModel.getFeatureById(id);
								if (feature != null)
									features.add(feature);
								else
									System.out.println("Unknown feature (xml): "
											+ id);
							}
						}
						result.put(key, features);
					}
				}
			}

			@Override
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
		xmlReader.parse(new InputSource(r));
	} catch (SAXException e) {
		e.printStackTrace();
	} catch (ParserConfigurationException e) {
		e.printStackTrace();
	}

	return result;
}

}
