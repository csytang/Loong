package loongplugin.editor.contextmenu;

import java.util.Collection;

import loongplugin.editor.toggle.ToggleSelectionText;
import loongplugin.feature.Feature;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;

public class CLRAnnotateRemoveContextMenu extends MenuManager implements IContributionItem {
	
	public CLRAnnotateRemoveContextMenu(ToggleSelectionText context,
			Collection<? extends Feature> features){
		super("Remove feature to the selection");
		for (Feature feature : features) {
			if(context.isChecked(feature))
				this.add(new ToggleTextColorAction(context, feature,false));
		}
		
	}
	
}
