package loongpluginfmrtool.views.moduleviews;

import loongplugin.LoongImages;
import loongpluginfmrtool.module.model.ConfigurationOption;
import loongpluginfmrtool.module.model.Import;
import loongpluginfmrtool.module.model.Module;
import loongpluginfmrtool.module.model.ModuleComponent;
import loongpluginfmrtool.views.recommendedfeatureview.ASTNodeWrapper;
import loongpluginfmrtool.views.recommendedfeatureview.IJavaElementWrapper;
import loongpluginfmrtool.views.recommendedfeatureview.RSFeature;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ModuleLabelProvider implements ITableLabelProvider{

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		switch(columnIndex){
		case 0:{
			if(element instanceof Module){
				return LoongImages.getImage(LoongImages.MODULE);
			}else if(element instanceof ModuleComponent){
				ModuleComponent module_component = (ModuleComponent)element;
				if(module_component instanceof Import){
					return LoongImages.getImage(LoongImages.IMPORT);
				}else if(module_component instanceof ConfigurationOption){
					return LoongImages.getImage(LoongImages.MACRO);
				}else				
					return null;
			}
		}
	}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		switch(columnIndex){
			case 0:{ 
				if(element instanceof Module){
					Module module = (Module)element;
					return module.getId();
				}else if(element instanceof ModuleComponent){
					ModuleComponent module_component = (ModuleComponent)element;
					if(module_component instanceof Import){
						Import import_module_component = (Import)module_component;
						return import_module_component.getImportDisplayName();
					}else if(module_component instanceof ConfigurationOption){
						ConfigurationOption configuration_module_component = (ConfigurationOption)module_component;
						return configuration_module_component.toString();
					}
				}
			}
			case 1:{
				if(element instanceof Module){
					return ((Module)element).getModuleName();
				}else if(element instanceof ModuleComponent){
					ModuleComponent module_component = (ModuleComponent)element;
					if(module_component instanceof Import){
						Import import_module_component = (Import)module_component;
						return import_module_component.getImportType().name();
					}else if(module_component instanceof ConfigurationOption){
						ConfigurationOption configuration_module_component = (ConfigurationOption)module_component;
						return configuration_module_component.getAffectedASTNodesRange();
					}
				}
			}
		}
		return null;
	}

}
