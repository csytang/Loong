package loongplugin.color;

import loongplugin.events.ASTColorChangedEvent;
import loongplugin.events.ColorListChangedEvent;
import loongplugin.events.FileColorChangedEvent;

public interface IColorChangeListener {
	void astColorChanged(ASTColorChangedEvent event);
	void fileColorChanged(FileColorChangedEvent event);
	void colorListChanged(ColorListChangedEvent event);
}
