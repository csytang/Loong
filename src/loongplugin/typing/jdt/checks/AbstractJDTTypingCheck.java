package loongplugin.typing.jdt.checks;

import org.eclipse.jdt.core.dom.ASTNode;

import loongplugin.color.coloredfile.CLRAnnotatedSourceFile;
import loongplugin.typing.model.ITypingCheck;
import loongplugin.typing.jdt.JDTTypingProvider;

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