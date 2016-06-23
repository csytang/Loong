package loongplugin.editor.toggle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

import loongplugin.LoongPlugin;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.IColorManager;
import loongplugin.color.coloredfile.IColoredJavaSourceFile;
import loongplugin.feature.Feature;
import loongplugin.editor.SelectionFinder;
import loongplugin.events.ASTColorChangedEvent;

public class ToggleSelectionText {
	
	private boolean enabled = false;

	private final Set<ASTNode> selectedNodes = new HashSet<ASTNode>();

	private IColorManager colorManager;

	private CLRAnnotatedSourceFile sourceFile;
	
	public ToggleSelectionText(CLRAnnotatedSourceFile sourceFile,
			ISelection selection){
		
		if (!(selection instanceof ITextSelection))
			return;
		if (sourceFile == null)
			return;
		this.sourceFile = sourceFile;
		this.colorManager = sourceFile.getColorManager();
		
		
		ITextSelection tsel = (ITextSelection) selection;
		if (tsel.getLength() == 0)
			return;

		updateSelectedASTs(tsel, sourceFile);

		enabled = !selectedNodes.isEmpty();
	}
	
	boolean isEnabled() {
		return enabled;
	}

	public boolean isChecked(Feature feature) {
		if (!enabled){
			//System.out.println("In toggle selection, is not enable since no ast node is selected");
			return false;
		}

		boolean allSelected = true;
		for (ASTNode node : selectedNodes) {
			allSelected &= colorManager.hasColor(node, feature);
		}

		return allSelected;
	}
	
	public void run(Feature feature, boolean addColor) {
		colorManager.beginBatch();
		for (ASTNode node : selectedNodes) {
			if (addColor){
				colorManager.addColor(node, feature);
				feature.addASTNodeToFeature(sourceFile.getCompilationUnit(),node);
				
			}
			else{
				colorManager.removeColor(node, feature);
				feature.removeASTNodeToFeature(sourceFile.getCompilationUnit(),node);
			}
		}
		colorManager.endBatch();
		
		LoongPlugin.getDefault().notifyListeners(new ASTColorChangedEvent(this, selectedNodes, sourceFile));
	}
	
	private void updateSelectedASTs(ITextSelection txtSelection,
			IColoredJavaSourceFile sourceFile) {
		try {
			selectedNodes.clear();
			CompilationUnit ast = sourceFile.getAST();
			ast.accept(new SelectionFinder(selectedNodes, txtSelection));
			
		} catch (Exception e) {
		}
	}
	public Set<ASTNode> getSelectedNodes() {
		return selectedNodes;
	}
	
	public IProject getProject(){
		return sourceFile.getProject();
	}
	
	public void cleanAllAnnotation(){
		for(ASTNode astnode:selectedNodes){
			colorManager.clearColor(astnode);
			astnode.accept(new ASTVisitor(){
				@Override
				public void preVisit(ASTNode node) {
					// TODO Auto-generated method stub
					colorManager.clearColor(node);
					super.preVisit(node);
				}
				
			});
			
		}
	}
}
