package loongplugin.feature.guidsl;

public class ModelWarning {
	public final String message;

	public final int line;

	public ModelWarning(String message, int line) {
		this.message = message;
		this.line = line;
	}
}
