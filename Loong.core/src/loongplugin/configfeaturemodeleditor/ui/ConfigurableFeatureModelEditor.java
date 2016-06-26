package loongplugin.configfeaturemodeleditor.ui;

import java.net.URL;

import loongplugin.LoongPlugin;
import loongplugin.configfeaturemodeleditor.model.ConfFeatureModel;
import loongplugin.configfeaturemodeleditor.model.ConfFeature;
import loongplugin.configfeaturemodeleditor.model.FeatureConnectionModel;
import loongplugin.configfeaturemodeleditor.parts.PartFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

public class ConfigurableFeatureModelEditor extends GraphicalEditorWithPalette {

	/**
	 * Editor ID.
	 */
	public static final String ID = LoongPlugin.PLUGIN_ID+".mConfigFeatureModelEditor";
	
	public ImageDescriptor FEATURE_DESCRIPTION;
	public ImageDescriptor FEATURECONNECTION_DESCRIPTION;
	
	public ConfigurableFeatureModelEditor() {
		Bundle bundle = Platform.getBundle(LoongPlugin.PLUGIN_ID);
		URL fullPathString = BundleUtility.find(bundle,"icons/feature.jpg");
		FEATURE_DESCRIPTION = ImageDescriptor.createFromURL(fullPathString);
		URL fullConnectionPathString = BundleUtility.find(bundle,"icons/arrow.gif");
		FEATURECONNECTION_DESCRIPTION = ImageDescriptor.createFromURL(fullConnectionPathString);
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		getGraphicalViewer().setEditPartFactory(new PartFactory());
	}

	@Override
	protected void initializeGraphicalViewer() {
		ConfFeatureModel contents = new ConfFeatureModel();


		ConfFeature confF = new ConfFeature();
		confF.setConstraint(new Rectangle(20, 80, 80, 50));
		contents.addChild(confF);

		getGraphicalViewer().setContents(contents);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();

		PaletteGroup toolGroup = new PaletteGroup("Tools");
		ToolEntry tool = new SelectionToolEntry();
		toolGroup.add(tool);
		root.setDefaultEntry(tool);
		toolGroup.add(new MarqueeToolEntry());

		PaletteDrawer drawer = new PaletteDrawer("Element");
		CreationToolEntry creationEntry = new CreationToolEntry("Feature",
				"Add a feature to diagram", new SimpleFactory(ConfFeature.class), FEATURE_DESCRIPTION,
				FEATURE_DESCRIPTION);
		drawer.add(creationEntry);
		
		PaletteDrawer relations = new PaletteDrawer("Relations");
		ConnectionCreationToolEntry connectionEntry = new ConnectionCreationToolEntry("Connection",
				"Connect featurs", new SimpleFactory(FeatureConnectionModel.class), FEATURECONNECTION_DESCRIPTION,
				FEATURECONNECTION_DESCRIPTION);
		relations.add(connectionEntry);

		root.add(toolGroup);
		root.add(drawer);
		root.add(relations);
		
		
		return root;
	}

}
