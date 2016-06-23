package loongplugin.uml.editpart;



import loongplugin.uml.model.AggregationModel;
import loongplugin.uml.model.AssociationModel;
import loongplugin.uml.model.AttributeModel;
import loongplugin.uml.model.ClassModel;
import loongplugin.uml.model.CompositeModel;
import loongplugin.uml.model.DependencyModel;
import loongplugin.uml.model.GeneralizationModel;
import loongplugin.uml.model.InterfaceModel;
import loongplugin.uml.model.OperationModel;
import loongplugin.uml.model.RealizationModel;
import loongplugin.uml.model.RootModel;

import org.eclipse.gef.EditPart;

public class UMLEditPartFactory extends BaseUMLEditPartFactory {

	protected EditPart createUMLEditPart(EditPart context, Object model) {
		EditPart part = null;
		if(model instanceof RootModel){
			return new RootEditPart();
		} else if(model instanceof ClassModel){
			return new ClassEditPart();
		} else if(model instanceof InterfaceModel){
			return new InterfaceEditPart();
		} else if(model instanceof DependencyModel){
			return new DependencyEditPart();
		} else if(model instanceof AggregationModel){
			return new AggregationEditPart();
		} else if(model instanceof CompositeModel){
			return new CompositeEditPart();
		} else if(model instanceof AssociationModel) {
			return new AssociationEditPart();
		} else if(model instanceof GeneralizationModel){
			return new GeneralizationEditPart();
		} else if(model instanceof RealizationModel){
			return new RealizationEditPart();
		} else if(model instanceof AttributeModel){
			return new AttributeEditPart();
		} else if(model instanceof OperationModel){
			return new OperationEditPart();
		} 
		return part;
	}
	
}
