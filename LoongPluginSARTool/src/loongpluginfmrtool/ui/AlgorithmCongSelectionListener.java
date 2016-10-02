package loongpluginfmrtool.ui;


import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class AlgorithmCongSelectionListener extends SelectionAdapter {

	private Algorithms agr;
	public AlgorithmCongSelectionListener(Algorithms pagr){
		agr = pagr;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		Button button = (Button)e.widget;
		if(button.getSelection()){
			switch (agr.name()){
			case "ACDC":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.ACDC);
				break;
			}
			case "LIMBO":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.LIMBO);
				break;
			}
			case "ARC":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.ARC);
				break;
			}
			case "BUNCH":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.BUNCH);
				break;
			}
			case "VMS":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.VMS);
				break;
			}
			default:
				break;
			}
		}else{
			switch (agr.name()){
			case "ACDC":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.ACDC);
				break;
			}
			case "LIMBO":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.LIMBO);
				break;
			}
			case "ARC":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.ARC);
				break;
			}
			case "BUNCH":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.BUNCH);
				break;
			}
			case "VMS":{
				AlgorithmConfigurationUI.getDefault().setAlgorithmSelected(Algorithms.VMS);
				break;
			}
			default:
				break;
			}
		}
	}

}
