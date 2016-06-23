package loongplugin.uml;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class LoongUMLPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public LoongUMLPreferenceInitializer() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void initializeDefaultPreferences() {
		// TODO Auto-generated method stub
		IPreferenceStore store = LoongUMLPlugin.getDefault().getPreferenceStore();
		store.setDefault(LoongUMLPlugin.PREF_CLASS_DIAGRAM_SHOW_SIMPLE_NAME, false);
		store.setDefault(LoongUMLPlugin.PREF_CLASS_DIAGRAM_SHOW_PARAMETER_NAME, true);
		store.setDefault(LoongUMLPlugin.PREF_ANTI_ALIAS, false);
		store.setDefault(LoongUMLPlugin.PREF_SHOW_GRID, false);
		store.setDefault(LoongUMLPlugin.PREF_GRID_SIZE, 10);
		store.setDefault(LoongUMLPlugin.PREF_SNAP_GEOMETRY, false);
	}

}
