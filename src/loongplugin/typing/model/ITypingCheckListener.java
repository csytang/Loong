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

package loongplugin.typing.model;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * informs about new or obsolete typechecks (deltas)
 * 
 * @author ckaestne
 * 
 */
public interface ITypingCheckListener {

	/**
	 * invoked after checks have been added/removed from the typing provider
	 * 
	 * monitor for progress report if called in a job (internally creates a submonitor with work = 1)
	 */
	void changedTypingChecks(TypeCheckChangeEvent event,
			IProgressMonitor monitor);

}
