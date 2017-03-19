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

import loongplugin.featuremodeleditor.FeatureUIHelper;
import loongplugin.featuremodeleditor.GUIDefaults;
import loongplugin.featuremodeleditor.commands.FeatureDragAndDropCommand;
import loongplugin.featuremodeleditor.editpart.FeatureEditPart;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;


/**
 * Allows feature to be moved at the feature diagram and provides a feedback
 * figure.
 * 
 * @author Thomas Thuem
 */
public class FeatureMoveEditPolicy extends NonResizableEditPolicy implements GUIDefaults {

	private FeatureEditPart editPart;

	private ModelLayoutEditPolicy superPolicy;

	public FeatureMoveEditPolicy(FeatureEditPart editPart, ModelLayoutEditPolicy superPolicy) {
		this.editPart = editPart;
		this.superPolicy = superPolicy;
	}
	
	private Point s;
	
	private RectangleFigure r;
	
	private PolylineConnection c;

	@Override
	protected IFigure createDragSourceFeedbackFigure() {
		r = new RectangleFigure();
		FigureUtilities.makeGhostShape(r);
		r.setLineStyle(Graphics.LINE_DOT);
		r.setForegroundColor(ColorConstants.white);
		r.setBounds(getInitialFeedbackBounds());
		
		s = FeatureUIHelper.getSourceLocation(editPart.getFeatureModel());
		Point s2 = s.getCopy();
		getHostFigure().translateToAbsolute(s2);

		c = new PolylineConnection();
		c.setForegroundColor(NEW_CONNECTION_FOREGROUND);
		c.setSourceAnchor(new XYAnchor(s2));
		c.setTargetAnchor(new XYAnchor(s2));
		
		FreeformLayer l = new FreeformLayer();
		l.add(r);
		l.add(c);
		
		addFeedback(l);
		return l;
	}
	
	@Override
	protected void showChangeBoundsFeedback(ChangeBoundsRequest request) {
		//call createDragSourceFeedbackFigure on start of the move
		getDragSourceFeedbackFigure();
		
		PrecisionRectangle rect = new PrecisionRectangle(getInitialFeedbackBounds().getCopy());
		getHostFigure().translateToAbsolute(rect);
		rect.translate(request.getMoveDelta());
		rect.resize(request.getSizeDelta());
		r.translateToRelative(rect);
		r.setBounds(rect);
		
		Point s2 = s.getCopy();
		getHostFigure().translateToAbsolute(s2);
		s2.translate(request.getMoveDelta());
		c.setSourceAnchor(new XYAnchor(s2));

		FeatureDragAndDropCommand cmd = superPolicy.getConstraintCommand();
		Point location;
		if (cmd != null && cmd.getNewParent() != null) {
			location = FeatureUIHelper.getTargetLocation(cmd.getNewParent());
			getHostFigure().translateToAbsolute(location);
			c.setForegroundColor(cmd.canExecute() ? NEW_CONNECTION_FOREGROUND : VOID_CONNECTION_FOREGROUND);			
		}
		else
			location = s2;
		c.setTargetAnchor(new XYAnchor(location));
	}
	
	@Override
	protected void eraseChangeBoundsFeedback(ChangeBoundsRequest request) {
		super.eraseChangeBoundsFeedback(request);
		s = null;
		r = null;
		c = null;
	}

}
