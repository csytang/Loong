package loongplugin.views.recommendedfeatureview;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import loongplugin.LoongPlugin;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.SWT;

public class RecommendedFeatureView extends ViewPart {

	public static final String ID = LoongPlugin.PLUGIN_ID+".recommendedFeatureList";
	private TreeViewer fViewer;
	
	private Map<String,Integer>featureNameToFrequency = new HashMap<String,Integer>();
	private Map<String,Set<IJavaElement>>featureNameToBelongings = new HashMap<String,Set<IJavaElement>>();
	
	
	public RecommendedFeatureView() {
		// TODO Auto-generated constructor stub
		// create a new job for recommended feature name and write to a xml file
		
		
	}

	@Override
	public void createPartControl(Composite parent) {
		fViewer = new TreeViewer(parent);
		// add drop support to fViewer
		int ops = DND.DROP_COPY|DND.DROP_LINK;
		Transfer[] transfers = new Transfer[]{LocalSelectionTransfer.getTransfer()};
		fViewer.addDropSupport(ops, transfers, new RecommendTreeDropAdapter(fViewer));
		
		
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
