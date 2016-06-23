package loongplugin.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import loongplugin.color.ColorHelper;
import loongplugin.feature.Feature;
import loongplugin.feature.FeatureModel;
import loongplugin.variants.AbstractConfigurationPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;



public class NonValidatingConfigurationListPage extends AbstractConfigurationPage {

	public NonValidatingConfigurationListPage(String pageName,FeatureModel featureModel) {
		super(pageName, featureModel);
		setPageComplete(true);
	}
	
	protected Table table;
	protected final HashMap<Feature, TableItem> featureItems = new HashMap<Feature, TableItem>();
	
	@Override
	protected Control createMainControl(Composite composite) {
	table = new Table(composite, SWT.CHECK | SWT.BORDER);
	
	for (Feature feature : ColorHelper.sortFeatures(featureModel
			.getFeatures())) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText("Feature: " + feature.getName());
		item.setData(feature);
		item.setChecked(initialSelection != null
				&& initialSelection.contains(feature));
		featureItems.put(feature, item);
	}
	return table;
	}
	
	@Override
	public Set<Feature> getSelectedFeatures() {
		Set<Feature> result = new HashSet<Feature>();
		for (TableItem item : table.getItems()) {
			if (item.getChecked() && !item.getGrayed())
				result.add((Feature) item.getData());
		}
		return result;
	}
	
	@Override
	public Set<Feature> getNotSelectedFeatures() {
		Set<Feature> result = new HashSet<Feature>();
		for (TableItem item : table.getItems()) {
			if (!item.getChecked() && !item.getGrayed())
				result.add((Feature) item.getData());
		}
		return result;
	}
	
	@Override
	public String getTitle() {
		return "Select Features for Configuration";
	}
}
