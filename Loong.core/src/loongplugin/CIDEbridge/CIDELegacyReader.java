/**
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

package loongplugin.CIDEbridge;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;


/**
 * can read the very old serialized format from CIDE's first version
 * 
 * @author ckaestne
 * 
 */
public class CIDELegacyReader {

	static final long LEGACY_SERIALIZED_VERSION = 1l;

	/**
	 * old serialization, where a feature class was directly serialized. needed
	 * to be able to load old files
	 * 
	 * @param out
	 * @param project
	 * @param featureModel
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FeatureModelNotFoundException
	 */

	static HashMap<String, Set<Feature>> loadLegacySerialization(ObjectInputStream out, FeatureModel featureModel)
			throws IOException, ClassNotFoundException {
		HashMap<String, Set<colordide.features.Feature>> storedMap;
		storedMap = (HashMap<String, Set<colordide.features.Feature>>)out.readObject();
		
		HashMap<String, Set<Feature>> result = emptyMap();
		for (Map.Entry<String, Set<colordide.features.Feature>> entry : storedMap.entrySet()) {
			//DEBUG
			//System.out.println("Entry key:"+entry.getKey());
			//FINSIH
			Set<colordide.features.Feature> colorIds = entry.getValue();
			if (!colorIds.isEmpty()) {
				Set<Feature> features = new HashSet<Feature>();
				for (colordide.features.Feature id : colorIds) {
					Feature feature = featureModel.getFeatureById(id.getId());
					if (feature != null)
						features.add(feature);
				}
				if (!features.isEmpty())
					result.put(entry.getKey(), features);
			}
		}
		return result;
	}

	private static HashMap<String, Set<Feature>> emptyMap() {
		return new HashMap<String, Set<Feature>>();
	}
}
