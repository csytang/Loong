package loongplugin;
/*******************************************************************************
 * Copyright (c) 2016 The Hong Kong Polytechnic University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Chris Tang - initial API and implementation
 *******************************************************************************/

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import loongplugin.color.IColorChangeListener;
import loongplugin.editor.bindings.BindingColorCache;
import loongplugin.events.ASTColorChangedEvent;
import loongplugin.events.ColorListChangedEvent;
import loongplugin.events.FileColorChangedEvent;
import loongplugin.featuremodeleditor.IFeatureModelChangeListener;
import loongplugin.featuremodeleditor.event.FeatureModelChangedEvent;
import loongplugin.nature.LoongProjectNature;
import loongplugin.typing.internal.TypingManager;
/**
 * The activator class controls the plug-in life cycle
 */
public class LoongPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "LoongPlugin"; //$NON-NLS-1$
	public static final String ID_ASTVIEW = "LoongPlugin.astview";
	public static final int AST_VERSION = AST.JLS3;
	private List<WeakReference<IColorChangeListener>> listeners = null;
	private List<WeakReference<IFeatureModelChangeListener>> featuremodellisteners = null;
	public BindingColorCache colorCache;
	
	// The shared instance
	private static LoongPlugin plugin;
	private TypingManager typingManager;

	private static final long serialVersionUID = 1L;
	
	/**
	 * The constructor
	 */
	public LoongPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;
		typingManager = new TypingManager();
		typingManager.register();

		// initial type-check
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LoongPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	
	
	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message,
				null));
	}

	public static void logErrorStatus(String message, IStatus status) {
		if (status == null) {
			logErrorMessage(message);
			return;
		}
		MultiStatus multi = new MultiStatus(PLUGIN_ID, IStatus.ERROR, message, null);
		multi.add(status);
		log(multi);
	}

	public static void log(String message, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, e));
	}
	

	static class IncompatibleCideVersionsException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * 当FeatureModel改变时，通知所有的监听者
	 * @param event
	 */
	public void notifyFeatureModelListeners(FeatureModelChangedEvent event){
		if(featuremodellisteners!=null){
			for(WeakReference<IFeatureModelChangeListener> ref: new ArrayList<WeakReference<IFeatureModelChangeListener>>(featuremodellisteners)){
				IFeatureModelChangeListener listener = ref.get();
				if(listener!=null){
					listener.featureModelChanged(event);
				}
			}
		}
	}
	
	public void addFeatureModelChangeListener(IFeatureModelChangeListener Listner) {
		// TODO Auto-generated method stub
		if (featuremodellisteners == null)
			featuremodellisteners = new ArrayList<WeakReference<IFeatureModelChangeListener>>();
		featuremodellisteners.add(new WeakReference<IFeatureModelChangeListener>(Listner));
	}
	
	public void removeFeatureModelChangeListener(IFeatureModelChangeListener Listner){
		Iterator<WeakReference<IFeatureModelChangeListener>> iter = featuremodellisteners.iterator();
		while (iter.hasNext()) {
			WeakReference<IFeatureModelChangeListener> reference = iter.next();
			IFeatureModelChangeListener referencedListener = reference.get();
			if (referencedListener == null || referencedListener == featuremodellisteners)
				iter.remove();
		}
	}

	public void notifyListeners(ColorListChangedEvent event) {
		// TODO Auto-generated method stub
		if (listeners != null)
			for (WeakReference<IColorChangeListener> ref : new ArrayList<WeakReference<IColorChangeListener>>(
					listeners)) {
				IColorChangeListener listener = ref.get();
				if (listener != null)
					listener.colorListChanged(event);
			}

	}
	
	public void notifyListeners(ASTColorChangedEvent event) {
		// TODO Auto-generated method stub
		if (listeners != null)
			for (WeakReference<IColorChangeListener> ref : new ArrayList<WeakReference<IColorChangeListener>>(
					listeners)) {
				IColorChangeListener listener = ref.get();
				if (listener != null)
					listener.astColorChanged(event);
			}
	}
	
	public void notifyListeners(FileColorChangedEvent event) {
		if (listeners != null)
			for (WeakReference<IColorChangeListener> ref : new ArrayList<WeakReference<IColorChangeListener>>(
					listeners)) {
				IColorChangeListener listener = ref.get();
				if (listener != null)
					listener.fileColorChanged(event);
			}
	}

	public void removeColorChangeListener(IColorChangeListener listener) {
		// TODO Auto-generated method stub
		Iterator<WeakReference<IColorChangeListener>> iter = listeners.iterator();
		while (iter.hasNext()) {
			WeakReference<IColorChangeListener> reference = iter.next();
			IColorChangeListener referencedListener = reference.get();
			if (referencedListener == null || referencedListener == listener)
				iter.remove();
		}
	}

	public void addColorChangeListener(IColorChangeListener Listner) {
		// TODO Auto-generated method stub
		if (listeners == null)
			listeners = new ArrayList<WeakReference<IColorChangeListener>>();
		listeners.add(new WeakReference<IColorChangeListener>(Listner));
	}

	/**
	 * Convenience method for easy and clean logging of exceptions. All messages
	 * collected by this method will be written to the eclipse log file. The
	 * exception's stack trace is added to the log as well.
	 * 
	 * @param exception
	 *            Exception containing the stack trace
	 */
	public void logError(Throwable exception) {
		if (exception != null)
			logError(exception.getMessage(), exception);
	}

	
	/**
	 * Convenience method for easy and clean logging of exceptions. All messages
	 * collected by this method will be written to the eclipse log file. The
	 * exception's stack trace is added to the log as well.
	 * 
	 * @param message
	 *            A message that should be written to the eclipse log file
	 * @param exception
	 *            Exception containing the stack trace
	 */
	public void logError(String message, Throwable exception) {
		log(IStatus.ERROR, message, exception);
	}
	
	/**
	 * Logging any kind of message.
	 * 
	 * @param severity
	 * @param message
	 * @param exception
	 */
	private void log(int severity, String message, Throwable exception) {
		if (isDebugging())
			getLog().log(new Status(severity, LoongPlugin.PLUGIN_ID, message, exception));
	}

	public static boolean isLoongProject(IProject project) {
		//has nature checking
		try {
			IProjectDescription description = project.getDescription();
			return Arrays.asList(description.getNatureIds()).contains(
					LoongProjectNature.NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	

	
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

	public TypingManager getTypingManager() {
		return typingManager;
	}
}
