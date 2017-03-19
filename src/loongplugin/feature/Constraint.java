package loongplugin.feature;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.LinkedList;

import org.prop4j.Node;

public class Constraint implements PropertyConstants,Serializable {
	private FeatureModel featureModel;
	private int index;

	public Constraint(FeatureModel featureModel, int index) {
		this.featureModel = featureModel;
		this.index = index;
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public Node getNode() {
		return featureModel.getConstraint(index);
	}

	private LinkedList<PropertyChangeListener> listenerList = new LinkedList<PropertyChangeListener>();

	public void addListener(PropertyChangeListener listener) {
		if (!listenerList.contains(listener))
			listenerList.add(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		listenerList.remove(listener);
	}

	public void fire(PropertyChangeEvent event) {
		for (PropertyChangeListener listener : listenerList)
			listener.propertyChange(event);
	}
	
	
}
