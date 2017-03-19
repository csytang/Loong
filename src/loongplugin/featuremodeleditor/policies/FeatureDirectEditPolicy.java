/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2009  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package loongplugin.featuremodeleditor.policies;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.featuremodeleditor.GUIDefaults;
import loongplugin.featuremodeleditor.commands.FeatureRenamingCommand;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;



/**
 * Allows to rename features at the feature diagram.
 * 
 * @author Thomas Thuem
 */
public class FeatureDirectEditPolicy extends DirectEditPolicy implements GUIDefaults {
	
	private final FeatureModel featureModel;

	private final Feature feature;
	
	public FeatureDirectEditPolicy(FeatureModel featureModel, Feature feature) {
		this.featureModel = featureModel;
		this.feature = feature;
	}
	
	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) {
		String newName = (String) request.getCellEditor().getValue();
		return new FeatureRenamingCommand(featureModel, feature.getName(), newName);
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) {
	}
	
}
