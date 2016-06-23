package loongplugin.views.astview;

import java.util.List;
import loongplugin.LoongPlugin;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.events.ASTColorChangedEvent;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jface.action.Action;



public class CleanASTColorAction extends Action  {

	private List<ASTNode> affectednodes;
	private CLRAnnotatedSourceFile file;
	
	public CleanASTColorAction(List<ASTNode> nodes, CLRAnnotatedSourceFile file){
		super();
		this.affectednodes = nodes;
		this.file = file;
		this.setText("Clean feature assigned for selected node(s)");
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		this.setChecked(!this.isChecked());
		CompilationUnitColorManager colormanager = (CompilationUnitColorManager) file.getColorManager();
		colormanager.beginBatch();
		ASTVisitor colorRemover = new ColorRemover(colormanager);
		for (ASTNode node : affectednodes)
			node.accept(colorRemover);
		colormanager.endBatch();
		LoongPlugin.getDefault().notifyListeners(new ASTColorChangedEvent(this,affectednodes,file));
	}
	
	
	public static final class ColorRemover extends ASTVisitor {
		private CompilationUnitColorManager colorManager;

		public ColorRemover(CompilationUnitColorManager colorManager) {
			this.colorManager = colorManager;
		}

		@Override
		public void preVisit(ASTNode node) {
			// TODO Auto-generated method stub
			colorManager.clearColor(node);
			super.preVisit(node);
		}

		

	}

}
