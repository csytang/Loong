package loongplugin.editor.contextmenu;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.sat4j.specs.TimeoutException;

import loongplugin.editor.toggle.ToggleSelectionText;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelManager;

public class CLRAnnotateContextMenu extends MenuManager implements IContributionItem {
	
	/*
	 * 注册feature 颜色 实现上下文菜单
	 */
	public CLRAnnotateContextMenu(ToggleSelectionText context,
			Collection<? extends Feature> features){
		super("Annotated feature to the selection");
		Set<Feature>selectedset = new HashSet<Feature>();
		
		for (Feature feature : features) {
			if(!context.isChecked(feature))
				this.add(new ToggleTextColorAction(context, feature,true));
		}
		/*
		for (Feature feature : features) {
			if(context.isChecked(feature))
				selectedset.add(feature);
		}
		
		List<Set<Feature>> featureSets = new LinkedList<Set<Feature>>();
		
		for(Feature feature:features){
			if(selectedset.isEmpty()){
				this.add(new ToggleTextColorAction(context, feature,true));
			}else if(selectedset.contains(feature)){
				continue;
			}else if(!selectedset.contains(feature)){
				featureSets.clear();
				featureSets.add(selectedset);
				Set <Feature> temp = new HashSet<Feature>();
				temp.add(feature);
				featureSets.add(temp);
				try {
					if(FeatureModelManager.getInstance().getFeatureModel().areMutualExclusive(selectedset, featureSets)){
						this.add(new ToggleTextColorAction(context, feature,true));
					}
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
	}

	
	
}
