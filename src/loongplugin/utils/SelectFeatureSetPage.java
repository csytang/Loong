package loongplugin.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loongplugin.feature.Feature;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class SelectFeatureSetPage extends WizardPage {

	private Table table;

	private Set<Feature> initialSelected = Collections.EMPTY_SET;
	private Set<Feature> initialGrayed = Collections.EMPTY_SET;

	private boolean selectAll = false;

	private final List<Feature> featureList;

	public SelectFeatureSetPage(String pageName, List<Feature> featureList) {
		super(pageName);
		this.setTitle("Select Features");
		this.featureList = featureList;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		layout.marginHeight = layout.marginWidth = 5;
		composite.setLayout(layout);
		Label label = new Label(composite, SWT.NONE);
		label.setText("Select Features:");
		table = new Table(composite, SWT.CHECK | SWT.BORDER);
		FormData formData = new FormData();
		formData.top = new FormAttachment(label, 5);
		formData.bottom = new FormAttachment(100, 0);
		formData.right = new FormAttachment(100, 0);
		formData.left = new FormAttachment(0, 0);
		table.setLayoutData(formData);

		for (Feature feature : featureList) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText("Feature: " + feature.getName());
			item.setData(feature);
			boolean contains=initialSelected.contains(feature);
			item.setChecked(selectAll || contains);
			if (initialGrayed.contains(feature))
				item.setGrayed(true);
		}

		// SelectionDependencyManager selectionDepManager = new
		// SelectionDependencyManager(project,
		// table);
		// selectionDepManager.updateAll();
		// table.addListener(SWT.Selection, selectionDepManager);

		setControl(composite);
	}
	public Set<Feature> getSelectedFeatures() {
		Set<Feature> result = new HashSet<Feature>();
		for (TableItem item : table.getItems()) {
			if (item.getChecked() && !item.getGrayed())
				result.add((Feature) item.getData());
		}
		return result;
	}

	/**
	 * necessary to distinguish from grayed features
	 * 
	 * @return
	 */
	public Set<Feature> getNotSelectedFeatures() {
		Set<Feature> result = new HashSet<Feature>();
		for (TableItem item : table.getItems()) {
			if (!item.getChecked() && !item.getGrayed())
				result.add((Feature) item.getData());
		}
		return result;
	}

	public void setInitialSelection(Set<Feature> selected, Set<Feature> grayed) {
		if (selected != null)
			this.initialSelected = selected;
		else
			this.initialSelected = Collections.EMPTY_SET;
		if (grayed != null)
			this.initialGrayed = grayed;
		else
			this.initialGrayed = Collections.EMPTY_SET;
	}

	public void setInitialSelection(Set<Feature> initialSelection) {
		setInitialSelection(initialSelection, null);
	}

	public void selectAll(boolean allSelected) {
		this.selectAll = allSelected;
	}
}
