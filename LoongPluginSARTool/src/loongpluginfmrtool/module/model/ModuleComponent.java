package loongpluginfmrtool.module.model;

public class ModuleComponent {
	private Module parent;
	public ModuleComponent(Module pparent){
		this.parent = pparent;
	}
	public Module getParent(){
		return this.parent;
	}
}
