package loongplugin.uml.classdiagram.wizard;




import loongplugin.uml.LoongUMLPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NewClassDiagramWizard extends Wizard implements INewWizard {

	private ISelection selection;
	private NewClassDiagramWizardPage page;
	
	
	public NewClassDiagramWizard(){
		super();
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	public void addPages() {
		page = new NewClassDiagramWizardPage(selection);
		addPage(page);
	}

	public boolean performFinish() {
		IFile file = page.createNewFile();
		if(file==null){
			return false;
		}
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, file, true);
		} catch(PartInitException ex){
			LoongUMLPlugin.logException(ex);
			return false;
		}
		return true;
	}

}
