package loongplugin.uml;


import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class LoongUMLPlugin extends AbstractUIPlugin {

	// The shared instance
	private static LoongUMLPlugin plugin;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "LoongPluginUML";
	
	
	private static ResourceBundle resourceBundle;
	
	
	//============================================================================
		// Common settings
		//============================================================================
		public static final String PREF_SHOW_GRID = "pref.showgrid";
		public static final String PREF_GRID_SIZE = "pref.gridsize";
		public static final String PREF_SNAP_GEOMETRY = "pref.snapgeometry";
		
		//============================================================================
		// Appearance settings
		//============================================================================
		public static final String PREF_ANTI_ALIAS = "pref.antialias";
		public static final String PREF_NEWSTYLE = "pref.style.new";
		
		
		//============================================================================
		// Class diagram settings
		//============================================================================
		/**
		 * Show simple name of classes to make Class seem shorter in class diagrams
		 */
		public static final String PREF_CLASS_DIAGRAM_SHOW_SIMPLE_NAME = "pref.classdiagram.simplename";
		
		/**
		 * Show parameter name or not, methods will be shorter if parameter name isn't shown but only parameter type
		 */
		public static final String PREF_CLASS_DIAGRAM_SHOW_PARAMETER_NAME = "pref.classdiagram.show_parameter_name";
		
		//============================================================================
		// Sequence diagram settings
		//============================================================================
		/**
		 * Create a return message automatically in sequence diagrams
		 */
		public static final String PREF_SEQUENCE_DIAGRAM_CREATE_RETURN = "pref.sequence.createreturn";
		
		/**
		 * Show simple name of classes to make Class seem shorter in sequence diagrams
		 */
		public static final String PREF_SEQUENCE_DIAGRAM_SHOW_SIMPLE_NAME = "pref.sequence.simplename";

		private List dndListeners = new ArrayList();
		
	/**
	 * The constructor
	 */
	public LoongUMLPlugin() {
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("loongplugin.uml.LoongUMLPlugin");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}
	
	

	@Override
	public void start(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		super.stop(context);
		plugin = null;
		
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static LoongUMLPlugin getDefault() {
		if(plugin==null){
			plugin = new LoongUMLPlugin();
		}
		return plugin;
	}



	public String getResourceString(String key) {
		// TODO Auto-generated method stub
		return resourceBundle.getString(key);
	}


	/**
	 * Logging exception information.
	 * 
	 * @param ex exception
	 */
	public static void logException(Throwable ex) {
		ILog log = getDefault().getLog();
		IStatus status = null;
		if (ex instanceof CoreException) {
			status = ((CoreException) ex).getStatus();
		} else {
			status = new Status(IStatus.ERROR, PLUGIN_ID, 0, ex.toString(), ex);
		}
		log.log(status);

		// TODO debug
		ex.printStackTrace();
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("LoongPlugin.uml", path);
	}
	
	public List getDndListeners() {
		return dndListeners;
	}
}
