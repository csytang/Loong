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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import loongplugin.feature.Feature;
import loongplugin.feature.FeatureConnection;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.PropertyConstants;
import loongplugin.featuremodeleditor.GUIDefaults;
import loongplugin.featuremodeleditor.figure.CircleDecoration;
import loongplugin.featuremodeleditor.figure.RelationDecoration;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;





/**
 * An editpart for connections between features and their parents. Creates the
 * source decoration dependent on the mandatory property.
 * 
 * @author Thomas Thuem
 *
 */
/**
 * An editpart for connections between features and their parents. Creates the
 * source decoration dependent on the mandatory property.
 * 
 * @author Thomas Thuem
 * 
 */
public class ConnectionEditPart extends AbstractConnectionEditPart implements
		GUIDefaults, PropertyConstants, PropertyChangeListener {

	public ConnectionEditPart(FeatureConnection connection) {
		super();
		setModel(connection);
	}

	public FeatureConnection getConnectionModel() {
		return (FeatureConnection) getModel();
	}

	@Override
	protected IFigure createFigure() {
		PolylineConnection figure = new PolylineConnection();
		figure.setForegroundColor(CONNECTION_FOREGROUND);
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPolicy() {
			@Override
			protected void showCurrentEditValue(DirectEditRequest request) {
			}

			@Override
			protected Command getDirectEditCommand(DirectEditRequest request) {
				return null;
			}
		});
	}

	@Override
	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_OPEN) {
			changeConnectionType();
		}
	}

	private void changeConnectionType() {
		Feature feature = getConnectionModel().getTarget();

		if (feature.isAlternative()) {
			feature.changeToAnd();
		} else if (feature.isAnd()) {
			feature.changeToOr();
		} else {
			feature.changeToAlternative();
		}
		ModelEditPart parent = (ModelEditPart) getSource().getParent();
		FeatureModel featureModel = parent.getFeatureModel();
		featureModel.handleModelDataChanged();
	}

	@Override
	protected void refreshVisuals() {
		refreshParent();
		refreshSourceDecoration();
		refreshTargetDecoration();
		refreshToolTip();
	}

	public void refreshParent() {
		Feature newModel = getConnectionModel().getTarget();
		FeatureEditPart newEditPart = (FeatureEditPart) getViewer()
				.getEditPartRegistry().get(newModel);
		setTarget(newEditPart);
	}

	public void refreshSourceDecoration() {
		Feature source = ((FeatureConnection) getModel()).getSource();
		Feature target = ((FeatureConnection) getModel()).getTarget();

		RotatableDecoration sourceDecoration = null;
		if (target.isAnd() || OR_CIRCLES)
			sourceDecoration = new CircleDecoration(source.isMandatory());

		PolylineConnection connection = (PolylineConnection) getConnectionFigure();
		connection.setSourceDecoration(sourceDecoration);
	}

	public void refreshTargetDecoration() {
		Feature source = ((FeatureConnection) getModel()).getSource();
		Feature target = ((FeatureConnection) getModel()).getTarget();

		RotatableDecoration targetDecoration = null;
		if (target.getChildrenCount() > 1 || HALF_ARC)
			if (!target.isAnd() && target.isFirstChild(source))
				targetDecoration = new RelationDecoration(target.isMultiple(),
						target.getLastChild());

		PolylineConnection connection = (PolylineConnection) getConnectionFigure();
		connection.setTargetDecoration(targetDecoration);
	}

	private Label toolTipLabel = new Label("");

	public void refreshToolTip() {
		Feature target = ((FeatureConnection) getModel()).getTarget();
		PolylineConnection connection = (PolylineConnection) getConnectionFigure();
		String toolTip = " Connection type: \n"
				+ (target.isAnd() ? " And" : (target.isMultiple() ? " Or"
						: " Alternative"));
		toolTipLabel.setText(toolTip);
		connection.setToolTip(toolTipLabel);
	}

	@Override
	public void activate() {
		getConnectionModel().addListener(this);
		getConnectionModel().getSource().addListener(this);
		super.activate();
	}

	@Override
	public void deactivate() {
		super.deactivate();
		getConnectionModel().removeListener(this);
		getConnectionModel().getSource().removeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String prop = event.getPropertyName();
		if (prop.equals(PARENT_CHANGED)) {
			refreshParent();
		} else if (prop.equals(MANDANTORY_CHANGED)) {
			refreshSourceDecoration();
		}
	}

}

