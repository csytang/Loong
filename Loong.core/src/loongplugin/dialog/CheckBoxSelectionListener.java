package loongplugin.dialog;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class CheckBoxSelectionListener extends SelectionAdapter {
	private String astrategyName = "";
	public CheckBoxSelectionListener(String pstrategyName){
		astrategyName = pstrategyName;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		Button button = (Button)e.widget;
		if(button.getSelection()){
			switch (astrategyName){
			case "Topology":{
				MiningStrategyConfDialog.getDefault().setTopologySelected();
				break;
			}
			case "StiCProb":{
				MiningStrategyConfDialog.getDefault().setResolvebindSelected();
				break;
			}
			case "Substring":{
				MiningStrategyConfDialog.getDefault().setSubStringSelected();
				break;
			}
			case "TypeCheck":{
				MiningStrategyConfDialog.getDefault().setTypeCheckSelected();
				break;
			}
			default:
				break;
			}
		}else{
			switch (astrategyName){
			case "Topology":{
				MiningStrategyConfDialog.getDefault().setTopologyUnselected();
				break;
			}
			case "ResolveBind":{
				MiningStrategyConfDialog.getDefault().setResolvebindUnselected();
				break;
			}
			case "Substring":{
				MiningStrategyConfDialog.getDefault().setSubStringUnselected();
				break;
			}
			case "TypeCheck":{
				MiningStrategyConfDialog.getDefault().setTypeCheckUnselected();
				break;
			}
			default:
				break;
			}
		}
		
	}
	
}
