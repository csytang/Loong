package loongplugin.uml.model;


public interface ICloneableModel extends Cloneable {
	
	public Object clone();
	
	public AbstractUMLEntityModel getParent();
	
}