package loongplugin.feature.guidsl;

public class UnsupportedModelException extends Exception {

	public final int lineNumber;

	public UnsupportedModelException(String message, int lineNumber) {
		super(message);
		this.lineNumber = lineNumber;
	}
}
