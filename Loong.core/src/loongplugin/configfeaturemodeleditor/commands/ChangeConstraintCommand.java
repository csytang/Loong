package loongplugin.configfeaturemodeleditor.commands;

import loongplugin.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

public class ChangeConstraintCommand extends Command {

	private ConfFeature helloModel;

	private Rectangle constraint;

	private Rectangle oldConstraint;

	@Override
	public void execute() {
		helloModel.setConstraint(constraint);
	}

	public void setModel(Object model) {
		this.helloModel = (ConfFeature) model;
		oldConstraint = helloModel.getConstraint();
	}

	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
	}

	@Override
	public void undo() {
		helloModel.setConstraint(oldConstraint);
	}

}
