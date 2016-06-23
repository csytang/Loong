package loongplugin.uml.java;

import java.util.ArrayList;
import java.util.List;











import loongplugin.uml.LoongUMLPlugin;
import loongplugin.uml.model.AbstractUMLConnectionModel;
import loongplugin.uml.model.AbstractUMLEntityModel;
import loongplugin.uml.model.AttributeModel;
import loongplugin.uml.model.ClassModel;
import loongplugin.uml.model.InterfaceModel;
import loongplugin.uml.model.OperationModel;
import loongplugin.uml.model.RootModel;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * The command to add Java types to the class diagram.
 * 
 * @author Naoki Takezoe
 */
public class ImportClassModelCommand extends Command {
	
	private IType[] types;
	private RootModel root;
	private List<AbstractUMLEntityModel> models;
	private Point location;
	
	/**
	 * Constructor for the one type adding.
	 * 
	 * @param root the root model
	 * @param type the type to add
	 */
	public ImportClassModelCommand(RootModel root,IType type){
		this(root, new IType[]{ type });
	}
	
	/**
	 * Constructor for the two or more types adding.
	 * 
	 * @param root the root model
	 * @param types types to add
	 */
	public ImportClassModelCommand(RootModel root,IType[] types){
		this.root = root;
		this.types = types;
	}
	
	public void setLocation(Point location){
		this.location = location;
	}
	
	public void execute(){
		models = new ArrayList<AbstractUMLEntityModel>();
		List<AbstractUMLEntityModel> addedModels = new ArrayList<AbstractUMLEntityModel>();
		for(int i=0;i<types.length;i++){
			AbstractUMLEntityModel entity = createModel(types[i]);
			addedModels.add(entity);
			if(entity != null){
				if(location!=null){
					entity.setConstraint(new Rectangle(
							location.x + (i * 10), 
							location.y + (i * 10), -1, -1));
				} else {
					entity.setConstraint(new Rectangle(10, 10, -1, -1));
				}
				root.copyPresentation(entity);
				root.addChild(entity);
				models.add(entity);
			}
		}
		
		addConnections(addedModels);
	}
	
	private void addConnections(List<AbstractUMLEntityModel> addedModels){
		try {
			for(int i=0;i<addedModels.size();i++){
				AbstractUMLEntityModel model = addedModels.get(i);
				if(types[i].isInterface()){
					UMLJavaUtils.appendInterfacesConnection(this.root, types[i], model);
				} else {
					UMLJavaUtils.appendSuperClassConnection(this.root, types[i], model);
					UMLJavaUtils.appendInterfacesConnection(this.root, types[i], model);
					UMLJavaUtils.appendAggregationConnection(this.root, types[i], (ClassModel) model);
				}
				UMLJavaUtils.appendSubConnection(root, types[i].getJavaProject(), model);
			}
		} catch(JavaModelException ex){
			LoongUMLPlugin.logException(ex);
		}
	}
	
	private AbstractUMLEntityModel createModel(IType type){
		try {
			if(type.isInterface()){
				InterfaceModel model = new InterfaceModel();
				model.setName(type.getFullyQualifiedParameterizedName());
				AttributeModel[] fields = UMLJavaUtils.getFields(type);
				for(int i=0;i<fields.length;i++){
					model.addChild(fields[i]);
				}
				OperationModel[] methods = UMLJavaUtils.getMethods(type);
				for(int i=0;i<methods.length;i++){
					model.addChild(methods[i]);
				}
				return model;
				
			} else if(type.isClass()){
				ClassModel model = new ClassModel();
				model.setName(type.getFullyQualifiedParameterizedName());
				AttributeModel[] attrs = UMLJavaUtils.getFields(type);
				for(int i=0;i<attrs.length;i++){
					model.addChild(attrs[i]);
				}
				OperationModel[] methods = UMLJavaUtils.getMethods(type);
				for(int i=0;i<methods.length;i++){
					model.addChild(methods[i]);
				}
				return model;
			}
		} catch(Exception ex){
			LoongUMLPlugin.logException(ex);
		}
		return null;
	}
	
	public void undo(){
		for(AbstractUMLEntityModel model: models){
			for(AbstractUMLConnectionModel conn: model.getModelSourceConnections()){
				conn.detachSource();
				conn.detachTarget();
			}
			for(AbstractUMLConnectionModel conn: model.getModelTargetConnections()){
				conn.detachSource();
				conn.detachTarget();
			}
			this.root.removeChild(model);
		}
	}
}
