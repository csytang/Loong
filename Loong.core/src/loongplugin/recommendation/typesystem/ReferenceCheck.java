package loongplugin.recommendation.typesystem;

import loongplugin.feature.FeatureModel;
import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.source.database.ApplicationObserver;
import loongplugin.source.database.model.LElement;



public class ReferenceCheck extends AbstractTypingCheck {

	public ReferenceCheck(LElement sourceElement, LElement targetElement,
			FeatureModel model) {
		super(sourceElement, targetElement, model);
	}

	public boolean evaluate(IEvaluationStrategy strategy) {
		ApplicationObserver jayFX = ApplicationObserver.getInstance();

		// System.out.println("::: CHECK :::" );
		// System.out.println(" ==> MODEL:" + getFeatureModel() );
		// System.out.println(" ==> SOURCE:" +
		// jayFX.getElementColors(getSourceElement()) );
		// System.out.println(" ==> TARGET:" +
		// jayFX.getElementColors(targetElement));
		// System.out.println(" ===> VAR1: " +
		// strategy.implies(getFeatureModel(),
		// jayFX.getElementColors(getSourceElement()),
		// jayFX.getElementColors(targetElement)));
		// System.out.println(" ===> VAR2 - X: " +
		// strategy.implies(getFeatureModel(),
		// jayFX.getElementColors(targetElement),
		// jayFX.getElementColors(getSourceElement())));

		return strategy.implies(getFeatureModel(), jayFX
				.getElementFeatures(targetElement), jayFX
				.getElementFeatures(sourceElement));
	}

	public String getErrorMessage() {
		return "Access not present";
	}

	public String getProblemType() {
		return "loong.typing.jdt.reference";
	}

}
