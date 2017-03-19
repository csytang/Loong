package loongplugin.editor.viewer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.views.navigator.CollapseAllAction;

import loongplugin.editor.CLREditor;
import loongplugin.editor.toggle.ToggleSelectionText;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelManager;



public class ColorProjectionSubmenu extends MenuManager implements
		IContributionItem {

	public class ExpandAllAction extends Action {
		private CLREditor editor;

		public ExpandAllAction(CLREditor editor) {

			super("Show all");
			this.editor = editor;
		}

		@Override
		public void run() {
			editor.getProjectionColorManager().expandAllColors();
		}
	}
	public class CollapsAllAction extends Action {
		private CLREditor editor;

		public CollapsAllAction(CLREditor editor) {

			super("Hide all");
			this.editor = editor;
		}

		@Override
		public void run() {
			editor.getProjectionColorManager().collapseAllColors();
		}
	}

	public class ToggleColorProjectionAction extends Action {

		private CLREditor editor;

		private Feature feature;

		private boolean wasExpanded;

		public ToggleColorProjectionAction(CLREditor editor,
				Feature feature, boolean isExpanded, IProject project) {

			super(feature.getName());
			this.setChecked(isExpanded);
			this.wasExpanded = isExpanded;
			this.editor = editor;
			this.feature = feature;
		}

		@Override
		public void run() {
			if (wasExpanded)// isExpanded
				editor.getProjectionColorManager().collapseColor(feature);
			else
				editor.getProjectionColorManager().expandColor(feature);
		}

	}

	public ColorProjectionSubmenu(CLREditor editor,
			ToggleSelectionText context) {

		super("Projection");
		Set<Feature> expandedColors = editor.getProjectionColorManager().getExpandedColors();
		
		Collection<?extends Feature> allFeatures = FeatureModelManager.getInstance().getFeatures();
		for (Feature feature : allFeatures) {
			this.add(new ToggleColorProjectionAction(editor, feature,
					expandedColors.contains(feature), context.getProject()));
		}
		this.add(new ExpandAllAction(editor));
		this.add(new CollapsAllAction(editor));

	}
}
