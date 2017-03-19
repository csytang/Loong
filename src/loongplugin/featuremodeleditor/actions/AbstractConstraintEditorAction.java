/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2010  FeatureIDE Team, University of Magdeburg
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
package loongplugin.featuremodeleditor.actions;

import loongplugin.feature.Constraint;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.guidsl.FeatureModelWriter;
import loongplugin.featuremodeleditor.ConstraintEditor;

import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;



/**
 * Basic implementation for actions on constraints.
 * 
 * @author Christian Becker
 * @author Thomas Thuem
 */
public abstract class AbstractConstraintEditorAction extends Action {

	protected GraphicalViewerImpl viewer;

	protected FeatureModel featuremodel;
	
	protected FeatureModelWriter writer;
	
	protected String featuretext;

	private ISelectionChangedListener listener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event
					.getSelection();
			setEnabled(isValidSelection(selection));
		}
	};

	public AbstractConstraintEditorAction(GraphicalViewerImpl viewer,
			FeatureModel featuremodel, String menuname) {
		super(menuname);
		this.viewer = viewer;
		this.featuremodel = featuremodel;
		setEnabled(false);
		viewer.addSelectionChangedListener(listener);
	}

	public void run() {
		writer = new FeatureModelWriter(featuremodel);
		featuretext = writer.writeToString();
	}

	protected void openEditor(Constraint constraint) {
		new ConstraintEditor(featuremodel, constraint);
	}

	protected abstract boolean isValidSelection(IStructuredSelection selection);

}
