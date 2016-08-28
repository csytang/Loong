package loongpluginsartool.editor.configfeaturemodeleditor.commands;

import loongpluginsartool.editor.configfeaturemodeleditor.model.ConfFeatureModel;
import loongpluginsartool.editor.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.gef.commands.Command;

public class DeleteConfFeatureCommand extends Command {

	private ConfFeatureModel confFeatureModel;

	private ConfFeature confFeature;

	@Override
	public void execute() {
		confFeatureModel.removeChild(confFeature);
	}

	public void setConfFeatureModel(Object model) {
		confFeatureModel = (ConfFeatureModel) model;
	}

	public void setConfFeature(Object model) {
		confFeature = (ConfFeature) model;
	}

	@Override
	public void undo() {
		confFeatureModel.addChild(confFeature);
	}

}
