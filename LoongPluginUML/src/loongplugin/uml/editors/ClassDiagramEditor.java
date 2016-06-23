package loongplugin.uml.editors;



import loongplugin.uml.DiagramEditor;
import loongplugin.uml.LoongUMLPlugin;
import loongplugin.uml.action.AbstractUMLEditorAction;
import loongplugin.uml.action.AddAttributeAction;
import loongplugin.uml.action.AddOperationAction;
import loongplugin.uml.action.AutoLayoutAction;
import loongplugin.uml.action.CopyAction;
import loongplugin.uml.action.DownAction;
import loongplugin.uml.action.PasteAction;
import loongplugin.uml.action.ShowAllAction;
import loongplugin.uml.action.ShowPublicAction;
import loongplugin.uml.action.ToggleAction;
import loongplugin.uml.action.UpAction;
import loongplugin.uml.classdiagram.figure.UMLClassFigure;
import loongplugin.uml.editpart.UMLEditPartFactory;
import loongplugin.uml.model.AggregationModel;
import loongplugin.uml.model.AnchorModel;
import loongplugin.uml.model.AssociationModel;
import loongplugin.uml.model.ClassModel;
import loongplugin.uml.model.CompositeModel;
import loongplugin.uml.model.DependencyModel;
import loongplugin.uml.model.GeneralizationModel;
import loongplugin.uml.model.InterfaceModel;
import loongplugin.uml.model.NoteModel;
import loongplugin.uml.model.RealizationModel;
import loongplugin.uml.model.RootModel;
import loongplugin.uml.model.Visibility;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.Scale;

public class ClassDiagramEditor extends DiagramEditor {
	
	private AbstractUMLEditorAction addAttributeAction = null;
	private AbstractUMLEditorAction addOperationAction = null;
	private UpAction upAction = null;
	private DownAction downAction = null;
	private CopyAction copyAction = null;
	private AutoLayoutAction autoLayoutAction = null;
	
	private PasteAction pasteAction = null;
	private ShowPublicAction showPublicAction = null;
	private ShowAllAction showAllAction = null;
	private ToggleAction togglePublicAttr = null;
	private ToggleAction toggleProtectedAttr = null;
	private ToggleAction togglePackageAttr = null;
	private ToggleAction togglePrivateAttr = null;
	private ToggleAction togglePublicOpe = null;
	private ToggleAction toggleProtectedOpe = null;
	private ToggleAction togglePackageOpe = null;
	private ToggleAction togglePrivateOpe = null;

	private AlignmentAction top;
	private AlignmentAction midlle;
	private AlignmentAction bottom;
	private AlignmentAction left;
	private AlignmentAction center;
	private AlignmentAction right;

	public ClassDiagramEditor() {
		super();
	}

	protected PaletteRoot getPaletteRoot() {
		// palatte from GEF
		PaletteRoot root = new PaletteRoot();
		LoongUMLPlugin plugin = LoongUMLPlugin.getDefault();

		// PaletterGroup （label） the given lable of the palette group
		PaletteGroup tools = new PaletteGroup(plugin.getResourceString("palette.tool"));
		
		// 
		ToolEntry tool = new SelectionToolEntry();
		tools.add(tool);
		root.setDefaultEntry(tool);
		// 
		tool = new MarqueeToolEntry();
		tools.add(tool);

		PaletteDrawer common = new PaletteDrawer(plugin.getResourceString("palette.common"));
		common.add(createEntityEntry(plugin.getResourceString("palette.common.note"), NoteModel.class,
				"icons/note.gif"));
		common.add(createConnectionEntry(plugin.getResourceString("palette.common.anchor"), AnchorModel.class,
				"icons/anchor.gif"));

		//
		PaletteDrawer entities = new PaletteDrawer(plugin.getResourceString("palette.entity"));
		entities.add(createEntityEntry(plugin.getResourceString("palette.entity.class"), ClassModel.class,
				"icons/class.gif"));
		entities.add(createEntityEntry(plugin.getResourceString("palette.entity.interface"),
				InterfaceModel.class, "icons/interface.gif"));

		PaletteDrawer relations = new PaletteDrawer(plugin.getResourceString("palette.relation"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.dependency"),
				DependencyModel.class, "icons/dependency.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.association"),
				AssociationModel.class, "icons/association.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.generalization"),
				GeneralizationModel.class, "icons/generalization.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.realization"),
				RealizationModel.class, "icons/realization.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.aggregation"),
				AggregationModel.class, "icons/aggregation.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.composition"),
				CompositeModel.class, "icons/composition.gif"));

		// add tools common entities to PaletterGroup
		root.add(tools);
		root.add(common);
		root.add(entities);
		root.add(relations);

		return root;
	}

	protected RootModel createInitializeModel() {
		RootModel model = new RootModel();
		model.setShowIcon(true);
		model.setBackgroundColor(UMLClassFigure.classColor.getRGB());
		model.setForegroundColor(ColorConstants.black.getRGB());
		return model;
	}

	protected String getDiagramType() {
		return "class";
	}
	
	protected void createActions() {
		super.createActions();
		pasteAction = new PasteAction(this);
		getActionRegistry().registerAction(pasteAction);
		getSelectionActions().add(pasteAction.getId());
		
		copyAction = new CopyAction(this, pasteAction);
		getActionRegistry().registerAction(copyAction);
		getSelectionActions().add(copyAction.getId());
	}

	protected void createDiagramAction(GraphicalViewer viewer) {
		addAttributeAction = new AddAttributeAction(viewer.getEditDomain().getCommandStack(), viewer);
		addOperationAction = new AddOperationAction(viewer.getEditDomain().getCommandStack(), viewer);
		upAction = new UpAction(viewer.getEditDomain().getCommandStack(), viewer);
		downAction = new DownAction(viewer.getEditDomain().getCommandStack(), viewer);
		autoLayoutAction = new AutoLayoutAction(viewer);
		
		showPublicAction = new ShowPublicAction(viewer);
		showAllAction = new ShowAllAction(viewer);
		
		togglePublicAttr = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.attr.public"), viewer, 
				ToggleAction.ATTRIBUTE, Visibility.PUBLIC);
		toggleProtectedAttr = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.attr.protected"), viewer,
				ToggleAction.ATTRIBUTE, Visibility.PROTECTED);
		togglePackageAttr = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.attr.package"), viewer,
				ToggleAction.ATTRIBUTE, Visibility.PACKAGE);
		togglePrivateAttr = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.attr.private"), viewer,
				ToggleAction.ATTRIBUTE, Visibility.PRIVATE);
		togglePublicOpe = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.ope.public"), viewer,
				ToggleAction.OPERATION, Visibility.PUBLIC);
		toggleProtectedOpe = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.ope.protected"), viewer,
				ToggleAction.OPERATION, Visibility.PROTECTED);
		togglePackageOpe = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.ope.package"), viewer,
				ToggleAction.OPERATION, Visibility.PACKAGE);
		togglePrivateOpe = new ToggleAction(
				LoongUMLPlugin.getDefault().getResourceString("filter.ope.private"), viewer,
				ToggleAction.OPERATION, Visibility.PRIVATE);
	}

	protected void fillDiagramPopupMenu(MenuManager manager) {
		// TODO use ContextMenuProvider.
		manager.add(new Separator("align"));
		manager.add(autoLayoutAction);
		top = new AlignmentAction((IWorkbenchPart) this, PositionConstants.TOP);
		top.setSelectionProvider(getGraphicalViewer());
		midlle = new AlignmentAction((IWorkbenchPart) this, PositionConstants.MIDDLE);
		midlle.setSelectionProvider(getGraphicalViewer());
		bottom = new AlignmentAction((IWorkbenchPart) this, PositionConstants.BOTTOM);
		bottom.setSelectionProvider(getGraphicalViewer());
		left = new AlignmentAction((IWorkbenchPart) this, PositionConstants.LEFT);
		left.setSelectionProvider(getGraphicalViewer());
		center = new AlignmentAction((IWorkbenchPart) this, PositionConstants.CENTER);
		center.setSelectionProvider(getGraphicalViewer());
		right = new AlignmentAction((IWorkbenchPart) this, PositionConstants.RIGHT);
		right.setSelectionProvider(getGraphicalViewer());
		
		getActionRegistry().registerAction(top);
		getActionRegistry().registerAction(midlle);
		getActionRegistry().registerAction(bottom);
		getActionRegistry().registerAction(left);
		getActionRegistry().registerAction(center);
		getActionRegistry().registerAction(right);
		MenuManager alignmenu = new MenuManager(LoongUMLPlugin.getDefault().getResourceString("menu.align"));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT));
		manager.add(alignmenu);
		
		MenuManager filtermenu = new MenuManager(LoongUMLPlugin.getDefault().getResourceString("menu.filter"));
		filtermenu.add(showPublicAction);
		filtermenu.add(showAllAction);
		filtermenu.add(new Separator());
		filtermenu.add(togglePublicAttr);
		filtermenu.add(toggleProtectedAttr);
		filtermenu.add(togglePackageAttr);
		filtermenu.add(togglePrivateAttr);
		filtermenu.add(new Separator());
		filtermenu.add(togglePublicOpe);
		filtermenu.add(toggleProtectedOpe);
		filtermenu.add(togglePackageOpe);
		filtermenu.add(togglePrivateOpe);
		manager.add(filtermenu);
		
		manager.add(new Separator("add"));
		manager.add(addAttributeAction);
		manager.add(addOperationAction);
		manager.add(upAction);
		manager.add(downAction);
		
		manager.add(new Separator("copy"));
		manager.add(copyAction);
		manager.add(pasteAction);
	}

	protected void updateDiagramAction(ISelection selection) {
		addAttributeAction.update((IStructuredSelection) selection);
		addOperationAction.update((IStructuredSelection) selection);
		upAction.update((IStructuredSelection) selection);
		downAction.update((IStructuredSelection) selection);
		
		autoLayoutAction.update((IStructuredSelection) selection);
		top.update();
		midlle.update();
		bottom.update();
		left.update();
		center.update();
		right.update();
		showPublicAction.update((IStructuredSelection) selection);
		showAllAction.update((IStructuredSelection) selection);
		togglePackageAttr.update((IStructuredSelection) selection);
		togglePackageOpe.update((IStructuredSelection) selection);
		togglePrivateAttr.update((IStructuredSelection) selection);
		togglePrivateOpe.update((IStructuredSelection) selection);
		toggleProtectedAttr.update((IStructuredSelection) selection);
		toggleProtectedOpe.update((IStructuredSelection) selection);
		togglePublicAttr.update((IStructuredSelection) selection);
		togglePublicOpe.update((IStructuredSelection) selection);
	}

	protected EditPartFactory createEditPartFactory() {
		return new UMLEditPartFactory();
	}

}
