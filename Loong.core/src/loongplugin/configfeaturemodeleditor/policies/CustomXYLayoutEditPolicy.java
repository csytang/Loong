package loongplugin.configfeaturemodeleditor.policies;

import loongplugin.configfeaturemodeleditor.commands.ChangeConstraintCommand;
import loongplugin.configfeaturemodeleditor.commands.CreateCommand;
import loongplugin.configfeaturemodeleditor.model.ConfFeature;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

public class CustomXYLayoutEditPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		ChangeConstraintCommand command = new ChangeConstraintCommand();
		command.setModel(child.getModel());
		command.setConstraint((Rectangle) constraint);
		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		CreateCommand createCommand = new CreateCommand();
		Rectangle constraint = (Rectangle) getConstraintFor(request);
		ConfFeature model = (ConfFeature) request.getNewObject();
		model.setConstraint(constraint);
		createCommand.setContentsModel(getHost().getModel());
		createCommand.setHelloModel(model);
		return createCommand;
	}

}
