package loongplugin.editor.contextmenu;

import loongplugin.editor.toggle.ToggleSelectionText;

import org.eclipse.jface.action.Action;

public class CleanToggleTextAllColorAction extends Action {
	
	private ToggleSelectionText context;
	public CleanToggleTextAllColorAction(ToggleSelectionText pcontext){
		context = pcontext;
		setText("Clean all features to the selection");
	}
	@Override
	public void run() {
		
		context.cleanAllAnnotation();
	}
}
