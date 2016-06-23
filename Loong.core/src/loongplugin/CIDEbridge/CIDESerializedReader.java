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
 * can read the old serialized format used until March 2010.
 * 
 * @author ckaestne
 * 
 */
public class CIDESerializedReader {

	final static long serialVersionUID = 2l;

	/**
	 * the features themselfs are not serialized, only their IDs. this method
	 * does the serialization
	 * 
	 * @param out
	 * @param featureModel
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FeatureModelNotFoundException
	 */
	@SuppressWarnings("unchecked")
	static HashMap<String, Set<Feature>> loadFeatureMap(ObjectInputStream out,
			FeatureModel featureModel) throws IOException,
			ClassNotFoundException {
		HashMap<String, Set<Long>> storedMap = (HashMap<String, Set<Long>>) out
				.readObject();

		HashMap<String, Set<Feature>> result = emptyMap();
		for (Map.Entry<String, Set<Long>> entry : storedMap.entrySet()) {
			Set<Long> colorIds = entry.getValue();
			if (!colorIds.isEmpty()) {
				Set<Feature> features = new HashSet<Feature>();
				for (long id : colorIds) {
					Feature feature = featureModel.getFeatureById(id);
					if (feature != null)
						features.add(feature);
					else
						System.out.println("Unknown feature: "
								+ id);
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
