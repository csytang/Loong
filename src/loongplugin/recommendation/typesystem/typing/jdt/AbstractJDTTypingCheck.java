package loongplugin.recommendation.typesystem.typing.jdt;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingCheck;

import org.eclipse.jdt.core.dom.ASTNode;



public abstract class AbstractJDTTypingCheck implements ITypingCheck {
	protected final ASTNode source;
	protected final JDTTypingProvider typingProvider;
	protected final CLRAnnotatedSourceFile file;

	public AbstractJDTTypingCheck(CLRAnnotatedSourceFile file,
			JDTTypingProvider typingProvider, ASTNode source) {
		this.file = file;
		this.source = source;
		this.typingProvider = typingProvider;
	}

	public CLRAnnotatedSourceFile getFile() {
		return file;
	}

	public Severity getSeverity() {
		return Severity.ERROR;
	}

	public ASTNode getSource() {
		return source;
	}

}