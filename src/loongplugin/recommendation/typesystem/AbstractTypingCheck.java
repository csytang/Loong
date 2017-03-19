package loongplugin.recommendation.typesystem;

import loongplugin.feature.FeatureModel;
import loongplugin.source.database.model.LElement;



public abstract class AbstractTypingCheck implements IElementTypingCheck {
	protected final LElement sourceElement;
	protected final LElement targetElement;

	FeatureModel model;

	public AbstractTypingCheck(LElement sourceElement, LElement targetElement,
			FeatureModel model) {
		this.sourceElement = sourceElement;
		this.targetElement = targetElement;

		this.model = model;
	}

	public LElement getSourceElement() {
		return sourceElement;
	}

	public LElement getTargetElement() {
		return targetElement;
	}

	public FeatureModel getFeatureModel() {
		return model;
	}

	public Severity getSeverity() {
		return Severity.ERROR;
	}

}