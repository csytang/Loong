package loongplugin.popup.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loongplugin.color.ColorHelper;
import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.color.coloredfile.SourceFileColorManager;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.feature.FeatureModelManager;
import loongplugin.feature.FeatureModelNotFoundException;
import loongplugin.utils.SelectFeatureSetWizard;
import loongplugin.utils.SetCompUnitColorJob;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;



public class SetColorPopup implements IWorkbenchWindowActionDelegate, IObjectActionDelegate{

	private final List<IResource> resources = new ArrayList<IResource>();
	protected IWorkbenchPart part;
	
	
	public SetColorPopup() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		assert !resources.isEmpty();
		
		IProject project = resources.get(0).getProject();
		
		// Process unsupported multiple files selections from different projects
		for (IResource r : resources) {
			if (r.getProject() != project) {
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
				messageBox.setText("Unsupported selection. Select resources from a single project only.");
				messageBox.open();
				return;
			}
		}
		
		if (!resources.isEmpty()) {
			FeatureModel fm = FeatureModelManager.getInstance().getFeatureModel();
			SelectFeatureSetWizard wizard = new SelectFeatureSetWizard(ColorHelper.sortFeatures(fm.getFeatures()), null);
			try {
				calcInitialSelection(resources, wizard, fm);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			WizardDialog dialog = new WizardDialog(new Shell(), wizard);
			dialog.create();
			dialog.open();
			Set<Feature> features = wizard.getSelectedFeatures();
			Set<Feature> removedfeatures = wizard.getNotSelectedFeatures();
			if (features != null && (features.size() + removedfeatures.size() > 0)) {
				WorkspaceJob op = new SetCompUnitColorJob(resources,features,removedfeatures);
				op.setUser(true);
				op.schedule();
			}
			
		}
	}

	private void calcInitialSelection(List<IResource> resources2,
			SelectFeatureSetWizard wizard, FeatureModel fm) throws CoreException {
		Set<Feature> selected = null;
		Set<Feature> grayed = new HashSet<Feature>();
		for (IResource r : resources2) {
			Set<Feature> colors = null;
			if (r instanceof IFile) {
				String fileExt = r.getFileExtension();
				if(fileExt.equals("java")){
					ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom((IFile)r);
					ASTParser parser = ASTParser.newParser(AST.JLS3);
					parser.setSource(compilationUnit);
					CompilationUnit result = (CompilationUnit) parser.createAST(null);
					CLRAnnotatedSourceFile clrFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(compilationUnit);
					SourceFileColorManager colormanager = (SourceFileColorManager) clrFile.getColorManager();
					colors = colormanager.getColors(result);
				}
			}
			if (r instanceof IFolder || r instanceof IProject) {
				IContainer container = (IContainer) r;
				colors = new HashSet<Feature>();
				IResource[] resouces = container.members();
				for(IResource nestedresource:resources){
					if (nestedresource instanceof IFile) {
						ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom((IFile)nestedresource);
						ASTParser parser = ASTParser.newParser(AST.JLS3);
						parser.setSource(compilationUnit);
						CompilationUnit result = (CompilationUnit) parser.createAST(null);
						CLRAnnotatedSourceFile clrFile = (CLRAnnotatedSourceFile) CLRAnnotatedSourceFile.getColoredJavaSourceFile(compilationUnit);
						SourceFileColorManager colormanager = (SourceFileColorManager) clrFile.getColorManager();
						if(!colormanager.getColors(result).isEmpty()){
							colors.addAll(colormanager.getColors(result));
						}
					}
				}
			}
			if (colors != null) {
				if (selected == null)
					selected = colors;
				else {
					detectedGrayed(selected, grayed, colors);
				}
			}
		}

		wizard.p.setInitialSelection(selected, grayed);
	}
	
	private void detectedGrayed(Set<Feature> selected, Set<Feature> grayed,
			Set<Feature> colors) {
		if (selected.equals(colors))
			return;
		for (Feature f : selected) {
			if (!colors.contains(f))
				grayed.add(f);
		}
		for (Feature f : colors) {
			if (!selected.contains(f))
				grayed.add(f);
		}
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		resources.clear();
		if (selection instanceof IStructuredSelection) {
			for (Object selected : ((IStructuredSelection) selection).toArray()) {
				if (selected instanceof IResource) {
					resources.add((IResource) selected);
				}

			}
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		this.part = targetPart;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

}
