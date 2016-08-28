package loongpluginsartool.editor.configfeaturemodeleditor.commands;


import java.util.Map;

import loongpluginsartool.editor.configfeaturemodeleditor.model.ConfFeature;
import loongpluginsartool.views.recommendedfeatureview.RSFeature;
import loongpluginsartool.views.recommendedfeatureview.RecommendedFeatureView;

import org.eclipse.gef.commands.Command;

public class DirectEditCommand extends Command {


	private String oldText, newText;

	private ConfFeature confFeature;

	@Override
	public void execute() {
		oldText = confFeature.getText();
		confFeature.setText(newText);
		// 检查是否需要更改
		RecommendedFeatureView rfviewinstance = RecommendedFeatureView.getInstance();
		Map<ConfFeature,RSFeature> ConfToRsFeature = rfviewinstance.getConfToRSFeature();
		if(ConfToRsFeature.containsKey(confFeature)){
			RSFeature rsfeature = ConfToRsFeature.get(confFeature);
			rsfeature.updateFeatureName(newText);
		}
	}

	public void setModel(Object model) {
		confFeature = (ConfFeature) model;
	}

	public void setText(String text) {
		newText = text;
	}

	@Override
	public void undo() {
		confFeature.setText(oldText);
	}
}
