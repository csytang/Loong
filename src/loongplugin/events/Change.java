package loongplugin.events;

import loongplugin.feature.Feature;

public class Change {
	final public Feature feature;
	final public ChangeType type;

	public Change(Feature feature, ChangeType type) {
		this.feature = feature;
		this.type = type;
	}
}
