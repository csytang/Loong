package loongplugin.typing.internal.manager;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import loongplugin.typing.model.ITypingExtension;
import loongplugin.typing.model.ITypingProvider;



public class TypingExtensionProxy implements ITypingExtension {
	private final IConfigurationElement configElement;

	public TypingExtensionProxy(IConfigurationElement configurationElement) {
		this.configElement = configurationElement;
		name = configElement.getAttribute("name");
		id = configElement.getAttribute("id");
	}

	private final String name;
	private final String id;
	private ITypingExtension target = null;

	private void loadTarget() {
		try {
			target = (ITypingExtension) configElement.createExecutableExtension("provider");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Typing Extension: " + name + " (" + id + ")";
	}

	public ITypingProvider createTypingProvider(IProject project) {
		if (target == null)
			loadTarget();
		return target.createTypingProvider(project);
	}

}
