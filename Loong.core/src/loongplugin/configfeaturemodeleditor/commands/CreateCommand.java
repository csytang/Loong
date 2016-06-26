package loongplugin.configfeaturemodeleditor.commands;

import loongplugin.configfeaturemodeleditor.model.ConfFeatureModel;
import loongplugin.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.gef.commands.Command;

public class CreateCommand extends Command {

	private ConfFeatureModel contentsModel;

	private ConfFeature helloModel;

	@Override
	public void execute() {
		contentsModel.addChild(helloModel);
	}

	public void setContentsModel(Object model) {
		contentsModel = (ConfFeatureModel) model;
	}

	public void setHelloModel(Object model) {
		helloModel = (ConfFeature) model;
	}

	@Override
	public void undo() {
		contentsModel.removeChild(helloModel);
	}

}
