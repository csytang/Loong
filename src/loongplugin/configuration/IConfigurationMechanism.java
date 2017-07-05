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

package loongplugin.configuration;

import java.util.Collection;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.feature.Feature;

/**
 * this is the general interface that all configuration mechanisms (i.e. all
 * mechanisms that transform, remove or serialize ASTs during the
 * configuration/generation process implement.
 * 
 * configuration mechanisms can decide for themselves whether they handle a
 * certain file. configuration mechanisms have a priority. mechanisms with the
 * highest priority are asked first. priority 0 is reserved for the default
 * generation mechanism that handles all remaining files.
 * 
 * @author ckaestne
 * 
 */
public interface IConfigurationMechanism {
	/**
	 * priority of the mechanism. mechanisms with the highest priority are asked
	 * first whether they can handle a file
	 * 
	 * @return priority > 0, or 0 for default mechanism
	 */
	int getPriority();

	/**
	 * returns whether this mechanism can configure the file
	 * 
	 * @param file
	 * @return
	 */
	boolean canConfigureFile(CLRAnnotatedSourceFile file);

	/**
	 * performs the actual configuration and returns the content of the target
	 * file.
	 * 
	 * @param sourceFile
	 *            source file
	 * @param selectedFeatures
	 *            selected features
	 * @return content of the configured target file
	 * @throws ConfigurationException
	 *             in case the file cannot be parsed or there is another
	 *             configuration problem
	 */
	String configureFile(CLRAnnotatedSourceFile sourceFile,
			Collection<Feature> selectedFeatures)
			throws ConfigurationException;
}
