package loongplugin.featuremodeleditor;

import loongplugin.featuremodeleditor.event.FeatureModelChangedEvent;


public interface IFeatureModelChangeListener {
	
	void featureModelChanged(FeatureModelChangedEvent event);
}
