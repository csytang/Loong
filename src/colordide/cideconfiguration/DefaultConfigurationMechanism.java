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

package colordide.cideconfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.configuration.ConfigurationException;
import loongplugin.configuration.IConfigurationMechanism;
import loongplugin.feature.Feature;



/**
 * default mechanisms using AST transformations and rendering using the IASTNode
 * interfaces...
 * 
 * used by all languages generated from a gCIDE grammar
 * 
 * @author ckaestne
 * 
 */
public class DefaultConfigurationMechanism implements IConfigurationMechanism {

	/**
	 * can configure any file by default
	 */
	public boolean canConfigureFile(CLRAnnotatedSourceFile file) {
		return true;
	}

	public String configureFile(CLRAnnotatedSourceFile sourceFile,Collection<Feature> selectedFeatures)
			throws ConfigurationException {
		return DeleteHiddenNodesVisitor.hideCode(sourceFile, selectedFeatures);
	}

	/**
	 * lowest priority
	 */
	public int getPriority() {
		return 0;
	}

}
