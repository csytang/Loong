package loongplugin.recommendation.typesystem;

import loongplugin.recommendation.typesystem.typing.jdt.model.IEvaluationStrategy;
import loongplugin.source.database.model.LElement;



public interface IElementTypingCheck {
	enum Severity {
		ERROR, WARNING, INFO
	};

	/**
	 * severity to be shown on the eclipse marker
	 */
	Severity getSeverity();

	/**
	 * message to be shown on the eclipse marker
	 */
	String getErrorMessage();

	/**
	 * internal type of the eclipse marker (attribute of the marker)
	 */
	String getProblemType();

	/**
	 * location where the error is shown (file and AST node)
	 * 
	 * @return
	 */
	LElement getSourceElement();

	/**
	 * returns whether this typing check is ok (well-typed).
	 * 
	 * this method uses the evaluation strategy to perform the actual type check
	 * (a implies b etc). The evaluation strategy is responsible for looking up
	 * feature annotations on AST nodes and for caching results for other
	 * checks.
	 * 
	 * 
	 * @param strategy
	 *            evaluation strategy provided by CIDE or its feature model
	 * @return true for well-typed, false for typing error
	 */
	boolean evaluate(IEvaluationStrategy strategy);

	@Override
	boolean equals(Object obj);

}
