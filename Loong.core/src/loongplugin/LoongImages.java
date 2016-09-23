/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package loongplugin;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class LoongImages {

	private static final IPath ICONS_PATH = new Path("$nl$/icons"); //$NON-NLS-1$

	public static final String COLLAPSE = "collapseall.gif"; //$NON-NLS-1$

	public static final String EXPAND = "expandall.gif"; //$NON-NLS-1$

	public static final String LINK_WITH_EDITOR = "synced.gif"; //$NON-NLS-1$

	public static final String SETFOCUS = "setfocus.gif"; //$NON-NLS-1$

	public static final String REFRESH = "refresh.gif"; //$NON-NLS-1$

	public static final String CLEAR = "clear.gif"; //$NON-NLS-1$

	public static final String ADD_TO_TRAY = "add.gif"; //$NON-NLS-1$

	public static final String CHECKED_IMAGE = "checked.gif";

	public static final String UNCHECKED_IMAGE = "unchecked.gif";

	public static final String COLOREDJ = "coloredj.gif"; //$NON-NLS-1$
	
	public static final String CLREDITOR = "editor.gif";//$NON-NLS-1$

	public static final String FEATURE = "feature.jpg";
	
	public static final String JAVA_OBJ = "/eclipse/jcu_obj.png";
	public static final String EXPORT = "export.jpg";
	
	public static final String ABSTRACT_CO = "/eclipse/abstract_co.png";
	public static final String ANNOTATION_OBJ = "/eclipse/annotation_obj.png";
	public static final String CLASS_OBJ = "/eclipse/class_obj.gif";
	public static final String CONST_OVR = "/eclipse/constr_ovr.png";
	public static final String ENMU_OBJ = "/eclipse/enum_obj.gif";
	public static final String EXC_CATCH = "/eclipse/exc_catch.gif";
	public static final String FIELD_DEFAULT_OBJ = "/eclipse/field_default_obj.png";
	public static final String IMPORT_CO = "/eclipse/import_co.gif";
	public static final String INTERFACE_OBJ = "/eclipse/int_obj.gif";
	public static final String TYPES = "/eclipse/types.gif";
	public static final String PACKAGE_OBJ = "/eclipse/package_obj.gif";
	public static final String LOCAL_OBJ = "/eclipse/localvariable_obj.gif";
	public static final String METHOD_DEF = "/eclipse/methpub_obj.gif";
	// ---- Helper methods to access icons on the file system
	// --------------------------------------

	public static final String MODULE = "module.png";
	public static final String EXECUTE = "run.png";

	public static final String MACRO = "macro.ico";
	public static final String IMPORT = "import.png";
	public static final String LINK_INTERNAL = "link-internal.png";
	public static final String LINK_EXTERNAL = "link-external.png";
	public static void setImageDescriptors(IAction action, String type) {
		ImageDescriptor id = create("d", type); //$NON-NLS-1$
		if (id != null)
			action.setDisabledImageDescriptor(id);
		id = create("c", type); //$NON-NLS-1$
		if (id != null) {
			action.setHoverImageDescriptor(id);
			action.setImageDescriptor(id);
		}else{
			id = create("", type);
			if (id != null) {
				action.setHoverImageDescriptor(id);
				action.setImageDescriptor(id);
			}else{
				action.setImageDescriptor(ImageDescriptor.getMissingImageDescriptor());
			}
		}
		
		
	}

	private static ImageDescriptor create(String prefix, String name) {
		IPath path = ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(LoongPlugin.getDefault().getBundle(),
				path);
	}

	/*
	 * Since 3.1.1. Load from icon paths with $NL$
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle,
			IPath path) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		return null;
	}

	public static Image getCheckImage(boolean isSelected) {
		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;

		return getImage(key);
	}

	public static Image getImage(String key) {
		Image i = imageRegistry.get(key);
		if (i == null) {
			imageRegistry.put(key, create("", key));
			return imageRegistry.get(key);
		}
		return i;
	}

	// For the checkbox images
	private static ImageRegistry imageRegistry = new ImageRegistry();

	public static ImageRegistry getImageRegistry() {
		// TODO Auto-generated method stub
		return imageRegistry;
	}

}
