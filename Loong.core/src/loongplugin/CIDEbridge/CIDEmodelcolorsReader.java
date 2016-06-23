package loongplugin.CIDEbridge;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import loongplugin.color.ColorHelper;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.RGB;




public class CIDEmodelcolorsReader {

	private Map<String, Long> featureIds;
	private Map<Long, RGB> featureColors;
	private Map<Long, Boolean> featureVisibility;
	
	private final static long serialVersionUID = 1L;
	private IFile file;
	
	public CIDEmodelcolorsReader(IFile modelcolors) {
		// TODO Auto-generated constructor stub
		file = modelcolors;
		load();
	}
	
	protected long getNextId() {
		long max = 0;
		for (long id : featureIds.values())
			max = Math.max(max, id);
		return max + 1;
	}

	public void notifyFeatureRenamed(String oldName, String newName) {
		if (featureIds.containsKey(oldName)) {
			featureIds.put(newName, featureIds.remove(oldName));
			
		}
	}

	public long getFeatureId(Feature feature) {
		Long id = featureIds.get(feature.getName());
		if (id == null) {
			id = new Long(getNextId());
			featureIds.put(feature.getName(), id);
			
		}
		return id.longValue();
	}

	public RGB getFeatureColor(Feature feature) {
		long id = getFeatureId(feature);
		RGB color = featureColors.get(id);
		
		return color;
	}

	public boolean isFeatureVisible(Feature feature) {
		long id = getFeatureId(feature);
		Boolean visible = featureVisibility.get(id);
		if (visible == null)
			return true;
		return visible.booleanValue();
	}

	public Feature findFeatureById(Collection<Feature> features, long id) {
		for (Feature feature : features)
			if (getFeatureId(feature) == id)
				return feature;
		return null;
	}
	
	private void load() {
		featureIds = new HashMap<String, Long>();
		featureColors = new HashMap<Long, RGB>();
		featureVisibility = new HashMap<Long, Boolean>();
		
		try {
			if (!file.exists())
				return;

			InputStream is = file.getContents(true);
			boolean isXML = is.read() == '<';
			is = file.getContents(true);

			if (isXML) {
				CIDEXMLReaderWriter.readFile(is, featureIds, featureColors,
						featureVisibility);
			} else {
				// legacy mechanism
				ObjectInputStream out = new ObjectInputStream(is);
				try {
					long version = out.readLong();
					if (version != serialVersionUID)
						return;

					featureIds = (Map<String, Long>) out.readObject();
					featureColors = (Map<Long, RGB>) out.readObject();
					featureVisibility = (Map<Long, Boolean>) out.readObject();
				} finally {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
}
