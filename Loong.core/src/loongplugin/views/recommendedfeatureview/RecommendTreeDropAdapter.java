package loongplugin.views.recommendedfeatureview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;



public class RecommendTreeDropAdapter extends ViewerDropAdapter {
	
	private List<IJavaElement> allJavaElements;
	protected RecommendTreeDropAdapter(TreeViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean performDrop(Object data) {
		// TODO Auto-generated method stub
		IStructuredSelection selection = (IStructuredSelection) data;
		allJavaElements = new ArrayList<IJavaElement>();
		for(Iterator<?> ite = selection.iterator(); ite.hasNext();){
			Object element = ite.next();
			if(element instanceof IJavaElement){
				allJavaElements.add((IJavaElement) element);
			}
		}
		return true;
	}
	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		// TODO Auto-generated method stub
		if(LocalSelectionTransfer.getTransfer().isSupportedType(transferType)){
			return true;
		}else
			return false;
		
	}
	@Override
	public void dropAccept(DropTargetEvent event) {
		// TODO Auto-generated method stub
		event.detail = DND.DROP_COPY;
	}
	@Override
	public void dragOver(DropTargetEvent event) {
		// TODO Auto-generated method stub
		event.detail = DND.DROP_COPY;
	}
	
	
	
}
