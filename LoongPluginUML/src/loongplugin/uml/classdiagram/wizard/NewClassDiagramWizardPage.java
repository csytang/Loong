package loongplugin.uml.classdiagram.wizard;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import loongplugin.uml.classdiagram.figure.UMLClassFigure;
import loongplugin.uml.model.RootModel;
import loongplugin.uml.serializer.DiagramSerializer;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewClassDiagramWizardPage extends WizardNewFileCreationPage {

	public NewClassDiagramWizardPage(
			ISelection selection) {
		super("wizardPage",(IStructuredSelection) selection);
		setTitle("Create Class Diagram for Project");
		setDescription("Create a new class diagram");
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected InputStream getInitialContents() {
		// TODO Auto-generated method stub
		try {
			RootModel root = new RootModel();
			root.setShowIcon(true);
			root.setBackgroundColor(UMLClassFigure.classColor.getRGB());
			root.setForegroundColor(ColorConstants.black.getRGB());
			return DiagramSerializer.serialize(root);
		} catch(UnsupportedEncodingException ex){
			return null;
		}
	}

	@Override
	public void createControl(Composite parent) {
		/*
		 * 
		 */
		// TODO Auto-generated method stub
		super.createControl(parent);
		this.setFileName("classdiagram.ucls");
	}
	
	
	

	
	
	
	
}
