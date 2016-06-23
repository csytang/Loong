package loongplugin.recommendation.typesystem;

import loongplugin.feature.FeatureModel;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;



public class InvocationCheck extends AbstractTypingCheck {
	private LElement bodySource;
	private LElement bodyTarget;

	public InvocationCheck(LElement paramSource, LElement paramTarget,
			LElement bodySource, LElement bodyTarget, FeatureModel model) {
		super(paramSource, paramTarget, model);
		this.bodySource = bodySource;
		this.bodyTarget = bodyTarget;

	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		ApplicationObserver jayFX = ApplicationObserver.getInstance();

		if (!strategy.implies(getFeatureModel(), jayFX
				.getElementFeatures(bodyTarget), jayFX
				.getElementFeatures(bodySource))) {
			return false;
		}

		if (strategy.equal(getFeatureModel(), jayFX
				.getElementFeatures(targetElement), jayFX
				.getElementFeatures(sourceElement)))
			return true;

		if (!strategy.implies(getFeatureModel(), jayFX
				.getElementFeatures(bodySource), jayFX
				.getElementFeatures(bodyTarget))) {

			if (strategy.equal(getFeatureModel(), jayFX
					.getElementFeatures(targetElement), jayFX
					.getElementFeatures(bodyTarget)))
				return true;

		}

		return false;

	}

	public String getErrorMessage() {
		return "Requirement not present";
	}

	public String getProblemType() {
		return "loong.typing.jdt.requirement";
	}

}
