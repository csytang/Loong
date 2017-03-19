package loongplugin.popup.actions;

import java.util.Iterator;
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
import loongplugin.utils.StopWatch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
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
	private IProject aProject;
	public AnnotateFeatures() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		// 1. check all features seeds are selected and annotated with colors
		aProject = getSelectedProject();
		// 检查是否构建的 program database 
		ApplicationObserver lDB = ApplicationObserver.getInstance();
		// 检查是否 选定 feature mining strategy
		if(!lDB.isInitialized(aProject)){
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
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		
		LElementRecommendationManager lerecommendationmanager = new LElementRecommendationManager();
		
		
		lerecommendationmanager.generateRecommendations();
		
		
		for(Feature feature:FeatureModelManager.getInstance().getFeatures()){
			System.out.println("For feature:"+feature.getName());
			Map<LElement, RecommendationContextCollection> recommendermaps = lerecommendationmanager.getRecommendations(feature);
			
			for(LElement element:recommendermaps.keySet()){
				System.out.println("Annotate LElement:"+element.getId());
				CLRAnnotatedSourceFile clrsourcefile = element.getCLRFile();
				CompilationUnitColorManager colormanager = (CompilationUnitColorManager) clrsourcefile.getColorManager();
				annotatedfeatureToNode(clrsourcefile.getCompilationUnit(),colormanager,element.getASTNode(),feature);
				
			}
			
		}
		stopwatch.stop();
		// Statistics
		String timeInSecsToComputeClusters = "Time in seconds to annotate features: "
						+ stopwatch.getElapsedTimeSecs();
		String timeInMilliSecondsToComputeClusters = "Time in milliseconds to annotate features: "
						+ stopwatch.getElapsedTime();
		System.out.println(timeInSecsToComputeClusters);
		System.out.println(timeInMilliSecondsToComputeClusters);
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
		
		colormanager.beginBatch();
		colormanager.addColor(node, feature);
		feature.addASTNodeToFeature(unit,node);
		colormanager.endBatch();
		
	}
	
	private IProject getSelectedProject() {
		IProject lReturn = null;
		Iterator i = aSelection.iterator();
		if (i.hasNext()) {
			Object lNext = i.next();
			if (lNext instanceof IResource) {
				lReturn = ((IResource) lNext).getProject();
			} else if (lNext instanceof IJavaElement) {
				IJavaProject lProject = ((IJavaElement) lNext).getJavaProject();
				lReturn = lProject.getProject();
			}
		}
		return lReturn;
	}
}
