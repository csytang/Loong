package loongplugin.configfeaturemodeleditor.parts;



import loongplugin.configfeaturemodeleditor.policies.CustomConnectionEndpointEditPolicy;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

public class CustomAbstractConnectionEditPart extends
AbstractConnectionEditPart {

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
			new CustomConnectionEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
			new CustomConnectionEndpointEditPolicy());
	}
}
