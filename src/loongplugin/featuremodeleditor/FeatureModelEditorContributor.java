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
package loongplugin.featuremodeleditor;

import loongplugin.featuremodeleditor.actions.AlternativeAction;
import loongplugin.featuremodeleditor.actions.AndAction;
import loongplugin.featuremodeleditor.actions.CreateCompoundAction;
import loongplugin.featuremodeleditor.actions.CreateLayerAction;
import loongplugin.featuremodeleditor.actions.DeleteAction;
import loongplugin.featuremodeleditor.actions.MandantoryAction;
import loongplugin.featuremodeleditor.actions.OrAction;

import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;



/**
 * Defines the actions for the feature model editor and contributes them.
 * 
 * @author Thomas Thuem
 */
public class FeatureModelEditorContributor extends EditorActionBarContributor {

	private static final String[] DIAGRAM_ACTION_IDS = { CreateLayerAction.ID,
			CreateCompoundAction.ID, DeleteAction.ID, MandantoryAction.ID,
			AndAction.ID, OrAction.ID, AlternativeAction.ID,
			ActionFactory.UNDO.getId(), ActionFactory.REDO.getId(),
			//ActionFactory.CUT.getId(), ActionFactory.COPY.getId(),
			//ActionFactory.PASTE.getId(),
			ActionFactory.SELECT_ALL.getId(),
			//ActionFactory.FIND.getId(),
			ActionFactory.PRINT.getId(),
			GEFActionConstants.ZOOM_IN,
			GEFActionConstants.ZOOM_OUT,
			//IDEActionFactory.BOOKMARK.getId()
			};

	private static final String[] TEXTEDITOR_ACTION_IDS = {
			ActionFactory.DELETE.getId(), ActionFactory.UNDO.getId(),
			ActionFactory.REDO.getId(), ActionFactory.CUT.getId(),
			ActionFactory.COPY.getId(), ActionFactory.PASTE.getId(),
			ActionFactory.SELECT_ALL.getId(), ActionFactory.FIND.getId(),
			ActionFactory.PRINT.getId(), IDEActionFactory.BOOKMARK.getId() };

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		FeatureModelEditor editor = (FeatureModelEditor) targetEditor;
		setActivePage(editor, editor.getActivePage());
	}

	public void setActivePage(FeatureModelEditor editor, int pageIndex) {
		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			switch (pageIndex) {
			case 0:
				hookGlobalDiagramActions(editor, actionBars);
				break;
			case 1:
				hookGlobalTextActions(editor, actionBars);
				break;
			}
			actionBars.updateActionBars();
		}
	}

	private void hookGlobalDiagramActions(FeatureModelEditor editor,
			IActionBars actionBars) {
		for (int i = 0; i < DIAGRAM_ACTION_IDS.length; i++) {
			actionBars.setGlobalActionHandler(DIAGRAM_ACTION_IDS[i], editor
					.getDiagramAction(DIAGRAM_ACTION_IDS[i]));
		}
	}

	private void hookGlobalTextActions(FeatureModelEditor editor,
			IActionBars actionBars) {
		ITextEditor textEditor = editor.getSourceEditor();
		for (int i = 0; i < TEXTEDITOR_ACTION_IDS.length; i++) {
			actionBars.setGlobalActionHandler(TEXTEDITOR_ACTION_IDS[i],
					textEditor.getAction(TEXTEDITOR_ACTION_IDS[i]));
		}
	}

	@Override
	public void contributeToToolBar(IToolBarManager manager) {
		super.contributeToToolBar(manager);
		manager.add(new Separator());
		manager.add(new ZoomComboContributionItem(getPage()));
	}

}
