package loongplugin.recommendation.typesystem.typing.jdt;

import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingExtension;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingProvider;

import org.eclipse.core.resources.IProject;



public class JDTTypingExtension implements ITypingExtension {

	public ITypingProvider createTypingProvider(IProject project) {
		return new JDTTypingProvider(project);
	}

}
