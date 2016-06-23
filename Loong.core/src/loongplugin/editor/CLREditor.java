package loongplugin.editor;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import loongplugin.LoongImages;
import loongplugin.LoongPlugin;
import loongplugin.color.ProjectionColorManager;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.IColoredJavaSourceFile;
import loongplugin.configuration.CLRJavaSourceViewerConfiguration;
import loongplugin.editor.contextmenu.CLRAnnotateContextMenu;
import loongplugin.editor.contextmenu.CLRAnnotateRemoveContextMenu;
import loongplugin.editor.contextmenu.CleanToggleTextAllColorAction;
import loongplugin.editor.projection.CLRProjectionAnnotationModel;
import loongplugin.editor.projection.CLRProjectionSummary;
import loongplugin.editor.projection.CLRProjectionSupport;
import loongplugin.editor.toggle.ToggleSelectionText;
import loongplugin.editor.viewer.CLRJavaSourceViewer;
import loongplugin.editor.viewer.ColorProjectionSubmenu;
import loongplugin.editor.viewer.ColoredHighlightingManager;
import loongplugin.editor.viewer.ColoredHighlightingReconciler;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;


public class CLREditor extends CompilationUnitEditor {
	
	private CLRAnnotateContextMenu annotatecontextMenu;
	private CLRAnnotateRemoveContextMenu annotateremovecontextMenu;
	private ColoredHighlightingManager fCodeColorManager;
	private CleanToggleTextAllColorAction cleanallannotationAction;
	private CLRJavaSourceViewer viewer;
	private Collection<? extends Feature>allfeatures;
	private IProject selectedProject;
	
	
	private void installCodeColoring() {
		if (fCodeColorManager == null) {
			IColoredJavaSourceFile sourceFile = CLRAnnotatedSourceFile
					.getColoredJavaSourceFile((ICompilationUnit) this
							.getInputJavaElement());

			fCodeColorManager = new ColoredHighlightingManager();
			fCodeColorManager.install(this,
					(JavaSourceViewer) getSourceViewer(), JavaPlugin
							.getDefault().getJavaTextTools().getColorManager(),
					getPreferenceStore(), sourceFile.getColorManager());
			
		}
		if (fCodeColorManager != null)
				fCodeColorManager.fReconciler.scheduleJob();
	}
	/**
	 * Uninstall Semantic Highlighting.
	 * 
	 * @since 3.0
	 */
	void uninstallCodeColoring() {
		if (fCodeColorManager != null) {
			fCodeColorManager.uninstall();
			fCodeColorManager = null;
		}
	}
	
	private ProjectionColorManager projectionColorManager;
	
	private ListenerList fReconcilingListeners = new ListenerList(
			ListenerList.IDENTITY);
	
	public CLREditor() {
		// TODO Auto-generated constructor stub
		
	}
	

	@Override
	protected void installOccurrencesFinder(boolean forceUpdate) {
		// TODO Auto-generated method stub
		if (isMarkingOccurrences())
			uninstallOccurrencesFinder();
	}



	@Override
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		// TODO Auto-generated method stub
		// 显示上下文菜单 
		/*
		 *  这里我们要用户选择 feature对应的颜色
		 */
		CLRAnnotatedSourceFile sourceFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile((ICompilationUnit) this
						.getInputJavaElement());
		ISelection selection = this.getSelectionProvider().getSelection();
		
		ToggleSelectionText context = new ToggleSelectionText(sourceFile,selection);
		this.selectedProject = sourceFile.getProject();
		this.allfeatures = FeatureModelManager.getInstance(selectedProject).getFeatures();
		annotatecontextMenu = new CLRAnnotateContextMenu(context,allfeatures);
		annotateremovecontextMenu = new CLRAnnotateRemoveContextMenu(context,allfeatures);
		cleanallannotationAction = new CleanToggleTextAllColorAction(context);
				
		menu.add(annotatecontextMenu);
		menu.add(annotateremovecontextMenu);
		menu.add(cleanallannotationAction);
		
		menu.add(new ColorProjectionSubmenu(this,context));
		
		super.editorContextMenuAboutToShow(menu);
	}
	@Override
	public Image getTitleImage() {
		// TODO Auto-generated method stub
		Image t = LoongImages.getImage(LoongImages.CLREDITOR);
		if (t != null)
			return t;
		else
			return super.getTitleImage();
	}

	@Override
	protected JavaSourceViewer createJavaSourceViewer(Composite parent, IVerticalRuler verticalRuler,
			IOverviewRuler overviewRuler, boolean isOverviewRulerVisible, int styles, IPreferenceStore store) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		/*
		 * 这里使用自定义的souceviewer CLRJavaSourceViewer来代替已有的sourceviewer
		*/
		viewer = new CLRJavaSourceViewer(
				parent, verticalRuler, getOverviewRuler(),
				isOverviewRulerVisible(), styles, store);
		
		return viewer;
	}



	@SuppressWarnings("restriction")
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createPartControl(parent);
		IProject project = FeatureModelManager.getCurrentProject();
		if(project==null){
			IEditorInput input = this.getEditorInput();
			IFileEditorInput fileEditorInput = (IFileEditorInput)input;
			project = fileEditorInput.getFile().getProject();
		}
		FeatureModelManager manager = FeatureModelManager.getInstance(project);
		
		
		installCodeColoring();
		if (isMarkingOccurrences())
			uninstallOccurrencesFinder();

		CLRJavaSourceViewer viewer = (CLRJavaSourceViewer) getViewer();

		CLRProjectionSupport projectionSupport = new CLRProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
		projectionSupport.install();
		viewer.doOperation(ProjectionViewer.TOGGLE);

		viewer.disableProjection();
		viewer.enableCLRProjection();
	}

	@Override
	public ITypeRoot getInputJavaElement() {
		// TODO Auto-generated method stub
		return super.getInputJavaElement();
	}



	public void reconciled(CompilationUnit ast, boolean forced, IProgressMonitor progressMonitor) {
		// TODO Auto-generated method stub
		Object[] listeners = fReconcilingListeners.getListeners();
		for (int i = 0, length = listeners.length; i < length; ++i)
			((IJavaReconcilingListener) listeners[i]).reconciled(ast, forced,
					progressMonitor);
	}



	public void removeReconcileListener2(ColoredHighlightingReconciler listener) {
		// TODO Auto-generated method stub
		synchronized (fReconcilingListeners) {
			fReconcilingListeners.remove(listener);
		}
	}



	public void addReconcileListener2(ColoredHighlightingReconciler listener) {
		// TODO Auto-generated method stub
		synchronized (fReconcilingListeners) {
			fReconcilingListeners.add(listener);
		}
	}



	public ProjectionColorManager getProjectionColorManager() {
		// TODO Auto-generated method stub
		if (projectionColorManager == null)
			projectionColorManager = new ProjectionColorManager(this);
		return projectionColorManager;
	}
	
	public CLRAnnotatedSourceFile getCLRAnnotatedFile() {
		// TODO Auto-generated method stub
		ITypeRoot java = getInputJavaElement();
		assert java != null;
		assert java.getResource() instanceof IFile;
		IFile file = (IFile) java.getResource();
		if (file != null) {
			IPath fullPath = file.getFullPath();
			String fileExtension = fullPath.getFileExtension();
			if(fileExtension.endsWith("clr"))
				return (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(file);
			else if(fileExtension.endsWith("java")){
				ICompilationUnit compilationUnit = (ICompilationUnit)JavaCore.create(file);
				return (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(compilationUnit);
			}
		}
		return null;
	}
	
	public IDocument getDocument() {
		// TODO Auto-generated method stub
		return getSourceViewer().getDocument();
	}
	@Override
	protected IJavaElement getElementAt(int offset) {
		// TODO Auto-generated method stub
		ICompilationUnit unit= (ICompilationUnit)getInputJavaElement();

		if (unit != null) {
			try {
				JavaModelUtil.reconcile(unit);
				return unit.getElementAt(offset);
			} catch (JavaModelException x) {
				if (!x.isDoesNotExist())
				JavaPlugin.log(x.getStatus());
				// nothing found, be tolerant and go on
			}
		}

		return null;
	}
	

}
