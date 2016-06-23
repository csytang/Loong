package loongplugin.recommendation.topology;

/**
 * Interface describing objects interested in reactiong to changes to the
 * suggestion array.
 * */
public interface SuggestionArrayChangeListener {
	/**
	 * A signal that the suggestion array has changed.
	 * */
	void suggestionsChanged();
}
