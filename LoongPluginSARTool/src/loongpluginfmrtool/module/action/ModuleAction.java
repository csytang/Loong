package loongpluginfmrtool.module.action;

public interface ModuleAction {
	public ModuleActionType getActionType();
	public void periorChecker();
	public void perform();
	public void undo();
	
}
