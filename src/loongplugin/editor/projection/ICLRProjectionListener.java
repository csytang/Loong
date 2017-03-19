package loongplugin.editor.projection;

import org.eclipse.jface.text.source.projection.IProjectionListener;

public interface ICLRProjectionListener extends IProjectionListener {

	/**
	 * Tells this listener that projection has been enabled.
	 */
	void CLRProjectionEnabled();

	/**
	 * Tells this listener that projection has been disabled.
	 */
	void CLRProjectionDisabled();

}
