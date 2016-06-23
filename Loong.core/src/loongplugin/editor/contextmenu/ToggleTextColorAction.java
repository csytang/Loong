package loongplugin.editor.contextmenu;

import org.eclipse.jface.action.Action;

import loongplugin.editor.toggle.ToggleSelectionText;
import loongplugin.feature.Feature;


public class ToggleTextColorAction extends Action {

	Feature feature;
	private ToggleSelectionText context;
	private boolean setFeature = true;
	
	ToggleTextColorAction(ToggleSelectionText context, Feature feature,boolean setFeature) {
		this.setText(feature.getName());
		this.feature=feature;
		this.context=context;
		this.setFeature = setFeature;
	}
	
	@Override
	public void run() {
		context.run(feature,this.setFeature);
	}
}
