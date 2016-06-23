package loongplugin.popup.actions;

import java.util.Map;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.CompilationUnitColorManager;
import loongplugin.dialog.MiningStrategyConfDialog;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModelManager;
import loongplugin.modelcolor.ModelIDCLRFile;
import loongplugin.recommendation.LElementRecommendationManager;
import loongplugin.recommendation.RecommendationContextCollection;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class AnnotateFeatures implements IObjectActionDelegate{
	/**
	 * Parse the project and create the source database for selected project
	 */
	private IStructuredSelection aSelection;
	public AnnotateFeatures() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		// 1. check all features seeds are selected and annotated with colors
		
		// 检查是否构建的 program database 
		ApplicationObserver lDB = ApplicationObserver.getInstance();
		// 检查是否 选定 feature mining strategy
		if(!lDB.isInitialized()){
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "ProgramDB has not been built!");
			return;
		}
		
		if(MiningStrategyConfDialog.getDefault()==null){
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Please configure mining strategy in (Set Feature Mining Strategy) menu");
			return;
		}else if(!MiningStrategyConfDialog.getDefault().strategyHasBeenSelected()){
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Please configure mining strategy in (Set Feature Mining Strategy) menu");
			return;
		}
		
		
		// 生成FeatureColorModel 文件
		ModelIDCLRFile modelIDCLRFIle = new ModelIDCLRFile(FeatureModelManager.getInstance().getFeatureModel(),lDB.getInitializedProject());
		
		
		LElementRecommendationManager lerecommendationmanager = new LElementRecommendationManager();
		
		lerecommendationmanager.generateRecommendations();
		
		
		for(Feature feature:FeatureModelManager.getInstance().getFeatures()){
			//System.out.println("For feature:"+feature.getName());
			Map<LElement, RecommendationContextCollection> recommendermaps = lerecommendationmanager.getRecommendations(feature);
			for(LElement element:recommendermaps.keySet()){
				//System.out.println("LElement:"+element.getShortName());
				CLRAnnotatedSourceFile clrsourcefile = element.getCLRFile();
				CompilationUnitColorManager colormanager = (CompilationUnitColorManager) clrsourcefile.getColorManager();
				annotatedfeatureToNode(clrsourcefile.getCompilationUnit(),colormanager,element.getASTNode(),feature);
			}
			
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		if (selection instanceof IStructuredSelection)
			aSelection = (IStructuredSelection) selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}
	public void annotatedfeatureToNode(ICompilationUnit unit,CompilationUnitColorManager colormanager,ASTNode node,Feature feature){
		System.out.println("----Annotated Features--[start]----------");
		colormanager.beginBatch();
			colormanager.addColor(node, feature);
			feature.addASTNodeToFeature(unit,node);
		colormanager.endBatch();
		System.out.println("----Annotated Features-[end]-----------");
	}
}
