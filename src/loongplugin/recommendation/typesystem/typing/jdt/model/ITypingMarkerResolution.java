package loongplugin.recommendation.typesystem.typing.jdt.model;

import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

public interface ITypingMarkerResolution extends IMarkerResolution,
		IMarkerResolution2, Comparable<ITypingMarkerResolution> {

	int getRelevance();

}
