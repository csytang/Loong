package loongplugin.typing.internal.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import loongplugin.LoongPlugin;
import loongplugin.configuration.ExtensionPointManager;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingCheckListener;
import loongplugin.recommendation.typesystem.typing.jdt.model.ITypingProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;



/**
 * organizes all providers per project and collects checks
 * 
 * @author ckaestne
 * 
 */
public class TypingExtensionManager extends
		ExtensionPointManager<TypingExtensionProxy> {

	private static TypingExtensionManager instance;

	private TypingExtensionManager() {
		super(LoongPlugin.PLUGIN_ID, "typingProvider");
	}

	public static TypingExtensionManager getInstance() {
		if (instance == null)
			instance = new TypingExtensionManager();
		return instance;
	}

	protected TypingExtensionProxy parseExtension(
			IConfigurationElement configurationElement) {
		if (!configurationElement.getName().equals("typingProvider"))
			return null;
		return new TypingExtensionProxy(configurationElement);
	}

	private WeakHashMap<IProject, List<ITypingProvider>> typingProviderCache = new WeakHashMap<IProject, List<ITypingProvider>>();

	public List<ITypingProvider> getTypingProviders(IProject project) {
		List<ITypingProvider> typingProviders = typingProviderCache
				.get(project);
		if (typingProviders == null) {
			typingProviders = new ArrayList<ITypingProvider>(getProviders().size());
			for (TypingExtensionProxy proxy : getProviders()) {
				ITypingProvider typingProvider = proxy.createTypingProvider(project);
				typingProviders.add(typingProvider);
			}
			typingProviderCache.put(project, typingProviders);
		}
		return typingProviders;
	}

	/**
	 * register listeners but avoid double registration
	 * 
	 * @param providers
	 * @param listener
	 */
	public static void registerListener(List<ITypingProvider> providers,
			ITypingCheckListener listener) {
		for (ITypingProvider provider : providers) {
			provider.removeTypingCheckListener(listener);
			provider.addTypingCheckListener(listener);
		}
	}

}
