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
package loongplugin.featuremodeleditor.editpart;

import java.util.LinkedList;
import java.util.List;

import loongplugin.feature.Constraint;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.featuremodeleditor.GUIDefaults;
import loongplugin.featuremodeleditor.policies.ModelLayoutEditPolicy;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;



/**
 * The main editpart that has all <code>FeatureEditPart</code>s as children.
 * Notice that Draw2D calls a figure child of another when its drawn within the
 * parent figure. Therefore, all features need to by direct children of this
 * editpart.
 * 
 * @author Thomas Thuem
 */
public class ModelEditPart extends AbstractGraphicalEditPart implements GUIDefaults {
	
	public ModelEditPart(FeatureModel featureModel) {
		super();
		setModel(featureModel);
	}
	
	public FeatureModel getFeatureModel() {
		return (FeatureModel) getModel();
	}
	
	protected IFigure createFigure() {
		Figure fig = new FreeformLayer();
		fig.setLayoutManager(new FreeformLayout());
		fig.setBorder(new MarginBorder(5));
		return fig;
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ModelLayoutEditPolicy(getFeatureModel()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {
		LinkedList<Object> list = new LinkedList<Object>();
		addFeatures(getFeatureModel().getRoot(), list);
		addConstraints(getFeatureModel().getConstraints(), list);
		return list;
	}

	private void addFeatures(Feature feature, List<Object> list) {
		if (feature == null)
			return;
		list.add(feature);
		for (Feature child : feature.getChildren())
			addFeatures(child, list);
	}
	
	private void addConstraints(List<Constraint> constraints, LinkedList<Object> list) {
		for (int i = 0; i < constraints.size(); i++)
			list.add(constraints.get(i));
	}

}
