package loongplugin.configfeaturemodeleditor.commands;


import loongplugin.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.gef.commands.Command;

public class DirectEditCommand extends Command {


	private String oldText, newText;

	private ConfFeature confFeature;

	@Override
	public void execute() {
		oldText = confFeature.getText();
		confFeature.setText(newText);

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
