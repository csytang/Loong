package loongplugin.source.database;

import org.eclipse.core.resources.IProject;

import loongplugin.recommendation.typesystem.typing.jdt.JDTTypingProvider;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingExtension;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingProvider;

public class JDTTypingExtension implements ITypingExtension {

	public JDTTypingExtension() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ITypingProvider createTypingProvider(IProject project) {
		// TODO Auto-generated method stub
		return new JDTTypingProvider(project);
	}

}
